package com.nissan.alldriverguide.fragments.assistance;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.mobioapp.infinitipacket.model.EpubInfo;
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

public class HomePageFragment extends Fragment implements View.OnClickListener {

    private View view;
    private ExpandableListView expandableListView;
    private ImageButton btnBack;
    private LinearLayout linearBack;
    private TextView txt_back_title;
    private HomePageExpandableAdapter adapter;
    private List<EpubInfo> list;
    private TextView title;
    private static final String TITLE = "title";
    private Typeface tf;

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
        view = inflater.inflate(R.layout.fragment_homepage, container, false);

        initViews(view);

        try {
            progressBar.setVisibility(View.VISIBLE);
            expandableListView.setVisibility(View.GONE);

            loadData();
        } catch (Exception e) {
            e.printStackTrace();
        }

        setListener();

/*
        expandableListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                int index = parent.getFlatListPosition(ExpandableListView.getPackedPositionForChild(groupPosition, childPosition));

                Logger.error("index", "_______" + index);

                parent.setItemChecked(index, true);
                return true;
            }
        });
*/

        return view;
    }

    @Override
    public void onAttach(Context context) {
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
        txt_back_title.setTypeface(tf);

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
                } else {
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
            adapter.setOnClickListener(new HomePageExpandableAdapter.OnItemClickListener() {
                @Override
                public void onClick(int index) {
                    Fragment frag = DetailsFragment.newInstance(list.get(index).getIndex(), title.getText().toString().trim());
                    FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                    ft.setCustomAnimations(R.anim.right_in, R.anim.left_out, R.anim.left_in, R.anim.right_out);
                    ft.replace(R.id.container, frag);
                    ft.addToBackStack(Values.tabAssistance);
                    ft.commit();
                }
            });
        }
    }

    // here initialized all variable
    private void initViews(View view) {
        btnBack = (ImageButton) view.findViewById(R.id.btn_back);

        expandableListView = (ExpandableListView) view.findViewById(R.id.exp_lst_view_homepage);

        progressBar = (ProgressBar) view.findViewById(R.id.progress_bar);

        linearBack = (LinearLayout) view.findViewById(R.id.linear_back);
        title = (TextView) view.findViewById(R.id.txt_title);
        txt_back_title = (TextView) view.findViewById(R.id.txt_back_title);
        tf = Typeface.createFromAsset(getActivity().getAssets(), "font/nissan_brand_regular.otf"); //initialize typeface here.
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
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_back:
            case R.id.linear_back:
                ((MainActivity) getActivity()).onBackPressed();
                break;
            default:
                break;
        }
    }
}
