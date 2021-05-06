package com.nissan.alldriverguide.fragments.settings;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.nissan.alldriverguide.MainActivity;
import com.nissan.alldriverguide.R;
import com.nissan.alldriverguide.TutorialActivity;
import com.nissan.alldriverguide.adapter.AssistanceAdapter;
import com.nissan.alldriverguide.controller.SettingsTabContentController;
import com.nissan.alldriverguide.customviews.DialogController;
import com.nissan.alldriverguide.database.PreferenceUtil;
import com.nissan.alldriverguide.interfaces.CompleteSettingTabContent;
import com.nissan.alldriverguide.internetconnection.DetectConnection;
import com.nissan.alldriverguide.multiLang.model.SettingsTabListModel;
import com.nissan.alldriverguide.multiLang.model.SettingsTabModel;
import com.nissan.alldriverguide.utils.Analytics;
import com.nissan.alldriverguide.utils.Logger;
import com.nissan.alldriverguide.utils.NissanApp;
import com.nissan.alldriverguide.utils.Values;

import java.util.ArrayList;
import java.util.Collections;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import static com.nissan.alldriverguide.utils.Values.DEFAULT_CLICK_TIMEOUT;
import static com.nissan.alldriverguide.utils.Values.SUCCESS_STATUS;

public class SettingsFragment extends Fragment implements AdapterView.OnItemClickListener, CompleteSettingTabContent {

    private final int[] assistanceImage = {R.drawable.change_language, R.drawable.add_extra_car, R.drawable.tutorial, R.drawable.rate_app, R.drawable.send_feedback};

    public Resources resources;
    private DisplayMetrics metrics;
    private PreferenceUtil preferenceUtil;
    private ListView lstView;
    private TextView txt_title;

    private ProgressBar progressBar;
    private LinearLayout layoutDataNotFound;
    private TextView tvNoContent;

    private AssistanceAdapter adapter;

    private String sharedpref_key;
    private ArrayList<SettingsTabListModel> settingList = new ArrayList<>();
    private String[] setting_names;
    private long mLastClickTime;
    private SettingsTabContentController controller;

    public static Fragment newInstance() {
        return new SettingsFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        initViews(view);
        loadResource();
        setListener();
        check_Data();
        return view;
    }

    private void check_Data() {

        adapter = new AssistanceAdapter(getActivity().getApplicationContext(), setting_names, assistanceImage);
        lstView.setAdapter(adapter);

        sharedpref_key = Values.carType + "_" + Values.SETTING_OBJ_STORE_KEY;
        settingList = preferenceUtil.retrieveSettingDataList(sharedpref_key);

        if (settingList != null && settingList.size() > 0) {
            progressBar.setVisibility(View.GONE);
            tvNoContent.setVisibility(View.GONE);
            Collections.sort(settingList, (lhs, rhs) -> lhs.getIndex().compareTo(rhs.getIndex()));
            setting_names = new String[settingList.size()];
            for (int i = 0; i < settingList.size(); i++) {
                setting_names[i] = settingList.get(i).getTitle();
            }
            loadData();
        } else {
            progressBar.setVisibility(View.VISIBLE);
            if (!DetectConnection.checkInternetConnection(getActivity())) {
                progressBar.setVisibility(View.GONE);
                String internetCheckMessage = NissanApp.getInstance().getAlertMessage(this.getActivity(), preferenceUtil.getSelectedLang(), Values.ALERT_MSG_TYPE_INTERNET);
                showNoInternetDialogue(internetCheckMessage.isEmpty() ? resources.getString(R.string.internet_connect) : internetCheckMessage);
                return;
            }
        }

        int language_ID = NissanApp.getInstance().getLanguageID(preferenceUtil.getSelectedLang());
        controller.callApi(NissanApp.getInstance().getDeviceID(getActivity()), "" + language_ID, "" + Values.carType, Values.EPUBID, "4");

    }

    private void showNoInternetDialogue(String msg) {

        final Dialog dialog = new DialogController(getActivity()).internetDialog();
        dialog.setCancelable(false);
        TextView txtViewTitle = dialog.findViewById(R.id.txt_title);
        txtViewTitle.setText(msg);

        Button btnOk = dialog.findViewById(R.id.btn_ok);
        btnOk.setOnClickListener(v -> {
            dialog.dismiss();
            getActivity().finish();
        });

        dialog.show();

    }

    @Override
    public void onDownloaded(SettingsTabModel responseInfo) {
        if (SUCCESS_STATUS.equalsIgnoreCase(responseInfo.getStatusCode())) {
            settingList = new ArrayList<>();
            settingList = responseInfo.getData();
            preferenceUtil.storeSettingDataList(settingList, sharedpref_key);

            setting_names = new String[settingList.size()];
            for (int i = 0; i < settingList.size(); i++) {
                setting_names[i] = settingList.get(i).getTitle();
            }
            loadData();
        }

        if (progressBar.getVisibility() == View.VISIBLE) {
            progressBar.setVisibility(View.GONE);
            tvNoContent.setVisibility(View.GONE);
            layoutDataNotFound.setVisibility(View.GONE);
        }

    }

    @Override
    public void onFailed(String failedReason) {
        Logger.error("SettingsTabFragment", "__________" + failedReason);
        if (progressBar.getVisibility() == View.VISIBLE) {
            progressBar.setVisibility(View.GONE);
        }
    }

    private void loadData() {

        String title = NissanApp.getInstance().getTabTitle(getActivity(), "4");
        txt_title.setText(title == null || title.isEmpty() ? resources.getString(R.string.settings) : title);

        adapter.setList(setting_names, assistanceImage);
        adapter.notifyDataSetChanged();
    }

    private void loadResource() {
        resources = new Resources(getActivity().getAssets(), metrics, NissanApp.getInstance().changeLocalLanguage(getActivity(), preferenceUtil.getSelectedLang()));
    }

    private void setListener() {
        lstView.setOnItemClickListener(this);
    }

    private void initViews(View view) {
        lstView = view.findViewById(R.id.lst_view);
        txt_title = view.findViewById(R.id.txt_title);

        progressBar = view.findViewById(R.id.prog_settings);
        layoutDataNotFound = view.findViewById(R.id.layout_settings_data_not_found);
        tvNoContent = view.findViewById(R.id.txt_settings_data_not_found);

        metrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(metrics);
        preferenceUtil = new PreferenceUtil(getActivity().getApplicationContext());
        controller = new SettingsTabContentController(this);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        new PreferenceUtil(getActivity()).setOpenCountForRateApp();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Fragment frag = null;
        /*
          Here specify the fragment by the item position
         */
        switch (position) {
            case 0:
                if (SystemClock.elapsedRealtime() - mLastClickTime < DEFAULT_CLICK_TIMEOUT) {
                    return;
                }
                mLastClickTime = SystemClock.elapsedRealtime();
                frag = LanguageFragment.newInstance();
                ((MainActivity) getActivity()).sendMsgToGoogleAnalytics(((MainActivity) getActivity()).getAnalyticsForDownloadSection(Analytics.CHANGE_LANGUAGE));
                break;

            case 1:
                if (SystemClock.elapsedRealtime() - mLastClickTime < DEFAULT_CLICK_TIMEOUT) {
                    return;
                }
                mLastClickTime = SystemClock.elapsedRealtime();
                frag = AddCarFragment.newInstance();
                ((MainActivity) getActivity()).sendMsgToGoogleAnalytics(((MainActivity) getActivity()).getAnalyticsForCarSection(Analytics.CAR_SELECTION));
                break;

            case 2:
                if (SystemClock.elapsedRealtime() - mLastClickTime < DEFAULT_CLICK_TIMEOUT) {
                    return;
                }
                mLastClickTime = SystemClock.elapsedRealtime();
                startActivity(new Intent(getActivity(), TutorialActivity.class).putExtra("from", "fragment"));
                getActivity().overridePendingTransition(R.anim.right_in, R.anim.left_out);
                ((MainActivity) getActivity()).sendMsgToGoogleAnalytics(((MainActivity) getActivity()).getAnalyticsFromSettings(Analytics.TUTORIAL));
                new PreferenceUtil(getActivity()).setOpenCountForRateApp();
                break;

            case 3:
                if (SystemClock.elapsedRealtime() - mLastClickTime < DEFAULT_CLICK_TIMEOUT) {
                    return;
                }
                mLastClickTime = SystemClock.elapsedRealtime();
                // used for great or not great popup display
                preferenceUtil.setSessionOne(false);
                preferenceUtil.setSessionThree(false);
                preferenceUtil.setIsFirstTimeGreatNotGreat(false);
                new PreferenceUtil(getActivity()).resetUserNavigationCount();
                // go to play store
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + getActivity().getApplicationContext().getPackageName())));
                break;

            case 4:
                if (SystemClock.elapsedRealtime() - mLastClickTime < DEFAULT_CLICK_TIMEOUT) {
                    return;
                }
                mLastClickTime = SystemClock.elapsedRealtime();
                frag = Feedback.newInstance();
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
