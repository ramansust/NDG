package com.nissan.alldriverguide.fragments.settings;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.DisplayMetrics;
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
import com.nissan.alldriverguide.database.PreferenceUtil;
import com.nissan.alldriverguide.utils.Analytics;
import com.nissan.alldriverguide.utils.NissanApp;
import com.nissan.alldriverguide.utils.Values;

public class SettingsFragment extends Fragment implements AdapterView.OnItemClickListener {

    private int[] assistanceImage = {R.drawable.change_language, R.drawable.add_extra_car, R.drawable.tutorial, R.drawable.rate_app, R.drawable.send_feedback, R.drawable.disclaimer};

    public Resources resources;
    private DisplayMetrics metrics;
    private PreferenceUtil preferenceUtil;
    private View view;
    private ListView lstView;
    private TextView txt_title;

    private AssistanceAdapter adapter;

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
        loadData();
        return view;
    }

    private void loadData() {
        txt_title.setText(resources.getString(R.string.settings));
        adapter = new AssistanceAdapter(getActivity().getApplicationContext(), resources.getStringArray(R.array.settings_array), assistanceImage);
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
