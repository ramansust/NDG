package com.nissan.alldriverguide.fragments.settings;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.nissan.alldriverguide.MainActivity;
import com.nissan.alldriverguide.R;
import com.nissan.alldriverguide.TutorialActivity;
import com.nissan.alldriverguide.adapter.AssistanceAdapter;
import com.nissan.alldriverguide.customviews.ProgressDialogController;
import com.nissan.alldriverguide.database.PreferenceUtil;
import com.nissan.alldriverguide.interfaces.CompleteSettingTabContent;
import com.nissan.alldriverguide.multiLang.model.SettingsTabListModel;
import com.nissan.alldriverguide.multiLang.model.SettingsTabModel;
import com.nissan.alldriverguide.retrofit.ApiCall;
import com.nissan.alldriverguide.utils.Analytics;
import com.nissan.alldriverguide.utils.NissanApp;
import com.nissan.alldriverguide.utils.Values;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class SettingsFragment extends Fragment implements AdapterView.OnItemClickListener {

    private int[] assistanceImage = {R.drawable.change_language, R.drawable.add_extra_car, R.drawable.tutorial, R.drawable.rate_app, R.drawable.send_feedback, R.drawable.disclaimer};

    public Resources resources;
    private DisplayMetrics metrics;
    private PreferenceUtil preferenceUtil;
    private View view;
    private ListView lstView;
    private TextView txt_title;

    private AssistanceAdapter adapter;

    private String sharedpref_key;
    private ProgressDialog progressDialog;
    private ArrayList<SettingsTabListModel> settingList = new ArrayList<>();
    private String[] setting_names;
    private String preSharedpref_key;

    public static Fragment newInstance() {
        Fragment frag = new SettingsFragment();
        return frag;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_settings, container, false);

        initViews(view);
        loadResource();
        setListener();
        getSettingTabContent();
        //loadData();
        return view;
    }

    private void check_Data() {

        if (new PreferenceUtil(getActivity()).retrieveSettingDataList(sharedpref_key) != null) {
            settingList.clear();
            settingList = new PreferenceUtil(getActivity()).retrieveSettingDataList(sharedpref_key);
            Collections.sort(settingList, new Comparator<SettingsTabListModel>() {
                @Override
                public int compare(SettingsTabListModel lhs, SettingsTabListModel rhs) {
                    return lhs.getIndex().compareTo(rhs.getIndex());
                }
            });
            setting_names = new String[settingList.size()];
            for (int i = 0; i < settingList.size(); i++) {
                setting_names[i] = settingList.get(i).getTitle();
            }
            loadData();
            apiCall();
        } else {
            apiCall();
        }

    }

    private void getSettingTabContent() {

        preSharedpref_key = new PreferenceUtil(getActivity()).getPreviousLanguage() + "_" + Values.SETTINGDATA;
        sharedpref_key = Values.carType + "_" + NissanApp.getInstance().getLanguageID(new PreferenceUtil(getActivity()).getSelectedLang()) + "_" + Values.SETTINGDATA;

        String old_Lan = new PreferenceUtil(getActivity()).getPreviousLanguage();
        String new_Lan = new PreferenceUtil(getActivity()).getSelectedLang();

        if (old_Lan.equalsIgnoreCase("null")) {
            check_Data();

        } else {
            new PreferenceUtil(getActivity()).deleteMultiLangData(preSharedpref_key);
            check_Data();
        }
    }

    private void apiCall() {
        int language_ID = NissanApp.getInstance().getLanguageID(new PreferenceUtil(getActivity()).getSelectedLang());
        String language_name = new PreferenceUtil(getActivity()).getSelectedLang();
        new ApiCall().postSettingTabContent(NissanApp.getInstance().getDeviceID(getActivity()), "" + language_ID, "" + Values.carType, Values.EPUBID, "4", new CompleteSettingTabContent() {
            @Override
            public void onDownloaded(SettingsTabModel responseInfo) {
                if (responseInfo.getStatusCode().equalsIgnoreCase("200")) {

                    new PreferenceUtil(getActivity()).storeSettingDataList(responseInfo.getData(), sharedpref_key);

                    settingList.clear();
                    settingList = new PreferenceUtil(getActivity()).retrieveSettingDataList(sharedpref_key);


                    setting_names = new String[settingList.size()];
                    for (int i = 0; i < settingList.size(); i++) {
                        setting_names[i] = settingList.get(i).getTitle();
                    }
                    loadData();
                }
            }

            @Override
            public void onFailed(String failedReason) {
            }
        });
    }

    private void loadData() {
        txt_title.setText(resources.getString(R.string.settings));
        adapter = new AssistanceAdapter(getActivity().getApplicationContext(), setting_names, assistanceImage);
        lstView.setAdapter(adapter);
    }

    private void loadResource() {
        resources = new Resources(getActivity().getAssets(), metrics, NissanApp.getInstance().changeLocalLanguage(getActivity(), preferenceUtil.getSelectedLang()));
    }

    private void setListener() {
        lstView.setOnItemClickListener(this);
    }

    private void initViews(View view) {
        lstView = (ListView) view.findViewById(R.id.lst_view);
        txt_title = (TextView) view.findViewById(R.id.txt_title);

        metrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(metrics);
        preferenceUtil = new PreferenceUtil(getActivity().getApplicationContext());
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        new PreferenceUtil(getActivity()).setOpenCountForRateApp();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Fragment frag = null;
        /**
         * Here specify the fragment by the item position
         */
        switch (position) {
            case 0:
                frag = LanguageFragment.newInstance();
                ((MainActivity) getActivity()).sendMsgToGoogleAnalytics(((MainActivity) getActivity()).getAnalyticsForDownloadSection(Analytics.CHANGE_LANGUAGE));
                break;

            case 1:
                frag = AddCarFragment.newInstance();
                ((MainActivity) getActivity()).sendMsgToGoogleAnalytics(((MainActivity) getActivity()).getAnalyticsForCarSection(Analytics.CAR_SELECTION));
                break;

            case 2:
                startActivity(new Intent(getActivity(), TutorialActivity.class).putExtra("from", "fragment"));
                getActivity().overridePendingTransition(R.anim.right_in, R.anim.left_out);
                ((MainActivity) getActivity()).sendMsgToGoogleAnalytics(((MainActivity) getActivity()).getAnalyticsFromSettings(Analytics.TUTORIAL));
                new PreferenceUtil(getActivity()).setOpenCountForRateApp();
                break;

            case 3:
                // used for great or not great popup display
                preferenceUtil.setSessionOne(false);
                preferenceUtil.setSessionThree(false);
                preferenceUtil.setIsFirstTimeGreatNotGreat(false);
                new PreferenceUtil(getActivity()).resetUserNavigationCount();
                // go to play store
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + getActivity().getApplicationContext().getPackageName())));
                break;

            case 4:
                frag = Feedback.newInstance();
                break;

            case 5:
                break;

            default:
                break;
        }

        if (frag != null) {
            FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
            ft.setCustomAnimations(R.anim.right_in, R.anim.left_out, R.anim.left_in, R.anim.right_out);
            ft.replace(R.id.container, frag);
            ft.addToBackStack(Values.tabSettings);
            ft.commit();
        }
    }

}
