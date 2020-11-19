package com.nissan.alldriverguide.fragments.search.tab;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.mobioapp.infinitipacket.model.EpubInfo;
import com.nissan.alldriverguide.R;
import com.nissan.alldriverguide.adapter.CombimeterSearchAdapter;
import com.nissan.alldriverguide.database.CommonDao;
import com.nissan.alldriverguide.database.PreferenceUtil;
import com.nissan.alldriverguide.fragments.search.model.SearchCombimeterModel;
import com.nissan.alldriverguide.utils.Values;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by nirob on 9/12/17.
 */

public class WarningLightFragment extends Fragment {

    public static Fragment newInstance() {
        Fragment frag = new WarningLightFragment();
        return frag;
    }

    private String drawable_folder = Values.car_path + "/combimeter_button";
    public static Map<String, List<Object>> dataMap;
    private ArrayList<EpubInfo> newList = new ArrayList<>();
    private static ArrayList<EpubInfo> epubInfoArrayList = new ArrayList<>();
    public Map<String, List<Object>> map;
    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private static final int NUM_COLUMNS = 3;
    private CommonDao commonDao;
    private CombimeterSearchAdapter adapter;

    private LinearLayout linearLayoutNoContent;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View layout = inflater.inflate(R.layout.fragment_warning_lights, container, false);

        map = new LinkedHashMap<>();
        commonDao = CommonDao.getInstance();
        progressBar = (ProgressBar) layout.findViewById(R.id.progress_view);
        dataMap = new LinkedHashMap<>();
        recyclerView = (RecyclerView) layout.findViewById(R.id.recyclerView);
        linearLayoutNoContent = (LinearLayout) layout.findViewById(R.id.linearLayoutNoContent);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(NUM_COLUMNS, StaggeredGridLayoutManager.VERTICAL));

        addData();
        return layout;
    }


    /**
     * Data added and display for WarningLightFragment
     */
    private void addData() {
        epubInfoArrayList = new PreferenceUtil(getActivity().getApplicationContext()).retrieveSearchEpubList(Values.carType + Values.UNDERSCORE + new PreferenceUtil(getActivity().getApplicationContext()).getSelectedLang() + Values.UNDERSCORE + Values.COMBIMETER_TYPE);
        if (epubInfoArrayList == null || epubInfoArrayList.size() == 0) {
            return;
        }
        newList = searchForTag(epubInfoArrayList, BaseTabFragmentActivity.keyword);

        progressBar.setVisibility(View.GONE);
        if (newList == null || newList.size() == 0) {
            recyclerView.setVisibility(View.GONE);
//            linearLayoutNoContent.setVisibility(View.VISIBLE);
        } else {
            setAdapter(searchTemp(newList));
        }
    }


    /**
     * This method process the List data that pass into adapter
     *
     * @param newList data list for processing
     * @return ArrayList
     */
    private ArrayList<SearchCombimeterModel> searchTemp(ArrayList<EpubInfo> newList) {

        ArrayList<SearchCombimeterModel> sortedList = new ArrayList<>();
        ArrayList<String> fileListNames = getFileNames();
        String[] colorArray = new String[]{"r", "org", "y", "grn", "c", "b", "g"};

        for (int i = 0; i < colorArray.length; i++) {
            for (int j = 0; j < newList.size(); j++) {
                for (int k = 0; k < fileListNames.size(); k++) {
                    int indexFromSearch = newList.get(j).getIndex() + 1;
                    String firstPart = "combimeter_" + indexFromSearch + "_" + colorArray[i] + fileListNames.get(k).substring(fileListNames.get(k).length() - 4, fileListNames.get(k).length()); //".jpg";// + stringArray[k].split(".")[1];

                    if (firstPart.equals(fileListNames.get(k))) {
                        SearchCombimeterModel searchCombimeterModel = new SearchCombimeterModel();
                        searchCombimeterModel.setEpubInfo(newList.get(j));
                        searchCombimeterModel.setImagePath(fileListNames.get(k));
                        sortedList.add(searchCombimeterModel);

                    }
                }
            }
        }
        return sortedList;
    }


    /**
     * This method return the result that display in WarningLightFragment section
     *
     * @param ePubInfoList need data list
     * @param keyWord      need for search keyword the contain in search tag
     * @return ArrayList
     */
    public static ArrayList<EpubInfo> searchForTag(ArrayList<EpubInfo> ePubInfoList, String keyWord) {

        ArrayList<EpubInfo> searchResultList = new ArrayList<>();

        if (ePubInfoList != null && ePubInfoList.size() > 0) {
            // search tag contain for new four cars
            /*if(Values.carType == 11 || Values.carType == 12 || Values.carType == 13 || Values.carType == 14) {
//            if(Values.carType == 14) {
                for(int i = 0; i < (ePubInfoList.size() / 2); i++) {
                    int searchTagIndex = (i * 2) + 1;

                    // this condition remove the demo item from WarningLightFragment
                    if(!ePubInfoList.get(searchTagIndex - 1).getTitle().toLowerCase().contains("demo")) {
                        if (ePubInfoList.get(searchTagIndex - 1).getTitle().toLowerCase().contains(keyWord.toLowerCase().trim()) || ePubInfoList.get(searchTagIndex).getTitle().toLowerCase().contains(keyWord.toLowerCase().trim())) {
                            ePubInfoList.get(searchTagIndex - 1).setIndex((searchTagIndex - 1) / 2); // need to check again

                            searchResultList.add(ePubInfoList.get(searchTagIndex - 1));
                        }
                    }
                }
            } else { // this one for all cars
                for (EpubInfo epubInfo : ePubInfoList) {

                    if(!epubInfo.getTitle().toLowerCase().contains("demo")) {
                        if (epubInfo.getTitle().toLowerCase().contains(keyWord.toLowerCase().trim())) {
                            searchResultList.add(epubInfo);
                        }
                    }
                }
            }*/

            /*for(int i = 0; i < (ePubInfoList.size() / 2); i++) {
                int searchTagIndex = (i * 2) + 1;

                if(ePubInfoList.get(searchTagIndex - 1).getTitle().toLowerCase().contains(keyWord.toLowerCase().trim()) || ePubInfoList.get(searchTagIndex).getTitle().toLowerCase().contains(keyWord.toLowerCase().trim())) {
                    ePubInfoList.get(searchTagIndex - 1).setIndex((searchTagIndex - 1) / 2); // need to check again

                    searchResultList.add(ePubInfoList.get(searchTagIndex - 1));
                }
            }*/

            for (int i = 0; i < (ePubInfoList.size() / 2); i++) {
                int searchTagIndex = (i * 2) + 1;

                // this condition remove the demo item from WarningLightFragment
                if (!ePubInfoList.get(searchTagIndex - 1).getTitle().toLowerCase().contains("demo")) {
                    if (ePubInfoList.get(searchTagIndex - 1).getTitle().toLowerCase().contains(keyWord.toLowerCase().trim()) || ePubInfoList.get(searchTagIndex).getTitle().toLowerCase().contains(keyWord.toLowerCase().trim())) {
                        ePubInfoList.get(searchTagIndex - 1).setIndex((searchTagIndex - 1) / 2); // need to check again

                        searchResultList.add(ePubInfoList.get(searchTagIndex - 1));
                    }
                }
            }

        }
        return searchResultList;
    }

    // here set the adapter
    private void setAdapter(ArrayList<SearchCombimeterModel> searchCombimeterModelArrayList) {
        adapter = new CombimeterSearchAdapter(getActivity(), searchCombimeterModelArrayList);
        recyclerView.setAdapter(adapter);
    }

    /**
     * This method get file name in combimeter folder form sdCard folder
     *
     * @return
     */
    private ArrayList<String> getFileNames() {
        ArrayList<String> fileNameList = new ArrayList<>();
        File dir = new File(drawable_folder + "/");

        if (dir.isDirectory()) {
            for (File file : dir.listFiles()) {
                fileNameList.add(file.getName());
            }
        }

        return fileNameList;
    }

}
