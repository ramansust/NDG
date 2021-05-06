package com.nissan.alldriverguide.fragments.assistance;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.datasoft.downloadManager.epubUtils.EpubInfo;
import com.nissan.alldriverguide.MainActivity;
import com.nissan.alldriverguide.R;
import com.nissan.alldriverguide.adapter.HomePageExpandableAdapter;
import com.nissan.alldriverguide.database.PreferenceUtil;
import com.nissan.alldriverguide.model.HomePageEpubInfo;
import com.nissan.alldriverguide.utils.Analytics;
import com.nissan.alldriverguide.utils.Logger;
import com.nissan.alldriverguide.utils.NissanApp;
import com.nissan.alldriverguide.utils.Values;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

public class HomePageFragment extends Fragment implements View.OnClickListener {

    private ExpandableListView expandableListView;
    private ImageButton btnBack;
    private LinearLayout linearBack;
    private HomePageExpandableAdapter adapter;
    private List<EpubInfo> list;
    private TextView title;
    private static final String TITLE = "title";

    private ProgressBar progressBar;

    public static Fragment newInstance(String title) {
        Fragment frag = new HomePageFragment();
        Bundle args = new Bundle();
        args.putString(TITLE, title);
        frag.setArguments(args);
        return frag;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_homepage, container, false);

        initViews(view);

        try {
            progressBar.setVisibility(View.VISIBLE);
            expandableListView.setVisibility(View.GONE);

            loadData();
        } catch (Exception e) {
            e.printStackTrace();
        }

        setListener();
        return view;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        new PreferenceUtil(getActivity()).setOpenCountForRateApp();
    }

    /**
     * Here load the initialized data
     *
     * @throws Exception
     */
    private void loadData() throws Exception {
        title.setText(getArguments().get(TITLE).toString());

        // here check the toc.ncx is exist or not in sdCard
        if (new File(NissanApp.getInstance().getCarPath(Values.carType) + NissanApp.getInstance().getePubFolderPath(Values.carType) + Values.UNDERSCORE + new PreferenceUtil(getActivity().getApplicationContext()).getSelectedLang() + Values.HOME_PAGE + Values.TOC_DIRECTORY).exists()) {
            // parseePub need to destination path like: /storage/emulated/0/.AllDriverGuide/leaf2017/leaf2017_en/.ar_homepage
            list = NissanApp.getInstance().parseePub(NissanApp.getInstance().getCarPath(Values.carType) + NissanApp.getInstance().getePubFolderPath(Values.carType) + Values.UNDERSCORE + new PreferenceUtil(getActivity().getApplicationContext()).getSelectedLang() + Values.HOME_PAGE);
            ((MainActivity) getActivity()).sendMsgToGoogleAnalytics(((MainActivity) getActivity()).getAnalyticsFromAssistance(Analytics.HOMEPAGE));
        }

        if (list != null && list.size() > 0) {
            ArrayList<HomePageEpubInfo> homePageList = new ArrayList<>();
            for (int i = 0; i < list.size(); i = i + 2) { // lopping for odd number that contain in list like: 1,3,5,7,
                HomePageEpubInfo info = new HomePageEpubInfo(); // creating object HomePageEpubInfo that extend EpubInfo

                info.setTitle(list.get(i).getTitle());
                info.setHtmlLink(list.get(i).getHtmlLink());
                info.setIndex(list.get(i).getIndex());

                String section = list.get(i + 1).getTitle(); // here get the even number title like: [CHARGING THE BATTERY, #669933, #E8F0E1]

                section = section.substring(1, section.length() - 1); // here remove the third bracket [] from section
                if (section != null && section.contains(",")) {
                    String[] stringArray = section.split(","); // split 1st part for title, 2nd part for header color and 3d part for headers child color

                    if (stringArray.length > 0) {
                        info.setSectionTitle(stringArray[0].trim()); // here set the title in HomePageEpubInfo class
                    }

                    if (stringArray.length > 1) {
                        info.setColorCode(stringArray[1].trim()); // here set the header color in HomePageEpubInfo class
                    }

                    if (stringArray.length > 2) {
                        info.setColorCodeItem(stringArray[2].trim()); // here set the headers title color in HomePageEpubInfo class
                    }
                } else if (section != null && section.contains(" ")) {
                    String[] stringArray = section.split(" ");

                    if (stringArray.length > 0) {
                        String title = section.substring(0, section.length() - stringArray[stringArray.length - 1].length()).trim();
                        info.setSectionTitle(title);
                    }

                    if (stringArray.length > 1) {
                        info.setColorCode(stringArray[stringArray.length - 1].trim());
                    }

                    if (stringArray.length > 2) {
                        info.setColorCodeItem(stringArray[2].trim());
                    }
                }

                homePageList.add(info); // here load the array list that provide for adapter
            }

            if (homePageList != null && homePageList.size() > 0) {
                adapter = new HomePageExpandableAdapter(getActivity().getApplicationContext(), homePageList);

                expandableListView.setAdapter(adapter);
            }
        } else {
            Logger.error("List is empty", "__________________");
        }

        progressBar.setVisibility(View.GONE);
        expandableListView.setVisibility(View.VISIBLE);
    }

    private void setListener() {
        btnBack.setOnClickListener(this);
        linearBack.setOnClickListener(this);

        if (adapter != null) {
            adapter.setOnClickListener(index -> {
                Fragment frag = DetailsFragment.newInstance(list.get(index).getIndex(), title.getText().toString().trim());
                FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                ft.setCustomAnimations(R.anim.right_in, R.anim.left_out, R.anim.left_in, R.anim.right_out);
                ft.replace(R.id.container, frag);
                ft.addToBackStack(Values.tabAssistance);
                ft.commit();
            });
        }
    }

    // here initialized all variable
    private void initViews(View view) {
        btnBack = view.findViewById(R.id.btn_back);
        expandableListView = view.findViewById(R.id.exp_lst_view_homepage);
        progressBar = view.findViewById(R.id.progress_bar);
        linearBack = view.findViewById(R.id.linear_back);
        title = view.findViewById(R.id.txt_title);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_back:
            case R.id.linear_back:
                getActivity().onBackPressed();
                break;
            default:
                break;
        }
    }
}
