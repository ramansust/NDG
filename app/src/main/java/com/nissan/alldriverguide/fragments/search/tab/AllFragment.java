package com.nissan.alldriverguide.fragments.search.tab;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.nissan.alldriverguide.R;
import com.nissan.alldriverguide.adapter.DataGridAdapter;
import com.nissan.alldriverguide.database.CommonDao;
import com.nissan.alldriverguide.model.CombimeterInfo;
import com.nissan.alldriverguide.model.EpubModel;
import com.nissan.alldriverguide.utils.Values;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by nirob on 9/12/17.
 * This class not in used anymore
 */

public class AllFragment extends Fragment {

    public static AllFragment newInstance() {
        return new AllFragment();
    }

    public AllFragment() {
    }

    private CommonDao commondao;

    private String drawable_folder = Values.car_path + "/combimeter_button";
    private RecyclerView recyclerView;
    public static Map<String, List<Object>> dataMap;
    private ProgressBar progressBar;
    private static final int NUM_COLUMNS = 3;

    private DataGridAdapter adapter;
    private TextView blankLayout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View layout = inflater.inflate(R.layout.fragment_all, container, false);

        initializeLayout(layout);

        addData(BaseTabFragmentActivity.keyword);

        return layout;
    }

    // initialized all field
    private void initializeLayout(View layout) {
        blankLayout = (TextView) layout.findViewById(R.id.blank_layout);
        progressBar = (ProgressBar) layout.findViewById(R.id.progress_view);
        commondao = CommonDao.getInstance();

        dataMap = new LinkedHashMap<>();
        recyclerView = (RecyclerView) layout.findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(NUM_COLUMNS,
                StaggeredGridLayoutManager.VERTICAL));

    }


    // adding data in Map form data base
    public void addData(String key) {
        dataMap.clear();

        // getting data from database that save in when car language is downloaded
        ArrayList<EpubModel> search_List = commondao.getEpubListByTag(getActivity(), key, Values.carType);

        if (search_List.size() <= 0) {
            blankLayout.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
            progressBar.setVisibility(View.GONE);

        } else {
            for (EpubModel epubModel : search_List) {

                List<Object> list = dataMap.get(headerTitle(epubModel.getEpubType()));

                if (list == null) {
                    list = new ArrayList<>();
                    dataMap.put(headerTitle(epubModel.getEpubType()), list);
                }

                if (String.valueOf(epubModel.getEpubType()).equalsIgnoreCase(Values.COMBIMETER_TYPE + "")) {
                    warningLight(epubModel, list);
                } else if (String.valueOf(epubModel.getEpubType()).equalsIgnoreCase(Values.HOMEPAGE_TYPE + "")) {
                    list.add(epubModel);
                } else if (String.valueOf(epubModel.getEpubType()).equalsIgnoreCase(Values.TYRE_TYPE + "")) {
                    list.add(epubModel);
                } else if (String.valueOf(epubModel.getEpubType()).equalsIgnoreCase(Values.ENGINE_TYPE + "")) {
                    list.add(epubModel);
                } else if (String.valueOf(epubModel.getEpubType()).equalsIgnoreCase(Values.WARRANTY_TYPE + "")) {
                    list.add(epubModel);
                } else {
                }

                try {
                    if (list.size() <= 0) {
                        if (dataMap.containsKey(epubModel.getEpubType())) {
                            dataMap.remove(epubModel.getEpubType());
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
            progressBar.setVisibility(View.GONE);

            setAdapter();
        }
    }

    // set the adapter in recyclerView
    private void setAdapter() {

        List<Object> items = new ArrayList<Object>();

        int cumulativeSum = 0;

        for (Map.Entry<String, List<Object>> entry : dataMap.entrySet()) {
            String key = entry.getKey();
            List<Object> list = dataMap.get(key);

            items.addAll(list);
        }

//        adapter = new DataGridAdapter(getActivity(), items);
//        recyclerView.setAdapter(adapter);
//        sectionedAdapter = new SectionedGridRecyclerViewAdapter(getActivity(),
//                R.layout.section, R.id.section_text, recyclerView, adapter);

    }


    // section header title name
    private String headerTitle(int i) {
        switch (i) {
            case 1:
                return "Warning Lights";
            case 2:
                return "Quick Reference Guide";
            case 3:
                return "Tyre Information";
            case 4:
                return "Engine Compartment";
            case 5:
                return "Warranty";
            default:
                break;
        }
        return "";
    }

    // adding data in map for warning light
    private void warningLight(EpubModel epubModel, List<Object> list) {
        File dir = new File(drawable_folder + "/");
        String[] wl = epubModel.getLink().split("\\-");

        if (dir.isDirectory()) {
            for (File file : dir.listFiles()) {
                String[] name = file.getName().split("\\_");

                if (name[1].equalsIgnoreCase(wl[wl.length - 1])) {
                    list.add(new CombimeterInfo(file.getName(), true));
                }
            }
        }
    }
}
