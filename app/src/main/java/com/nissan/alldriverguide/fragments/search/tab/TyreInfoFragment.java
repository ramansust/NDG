package com.nissan.alldriverguide.fragments.search.tab;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.datasoft.downloadManager.epubUtils.EpubInfo;
import com.nissan.alldriverguide.R;
import com.nissan.alldriverguide.adapter.DataGridAdapter;
import com.nissan.alldriverguide.database.PreferenceUtil;
import com.nissan.alldriverguide.utils.Values;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Objects;

import androidx.fragment.app.Fragment;

/**
 * Created by shubha on 11/20/17.
 */

public class TyreInfoFragment extends Fragment {

    private ProgressBar progressBar;
    private ListView listView;

    public static TyreInfoFragment newInstance() {
        return new TyreInfoFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View layout = inflater.inflate(R.layout.fragment_common_search, container, false);

        initializeView(layout);
        addData();

        return layout;
    }


    private void initializeView(View view) {
        progressBar = view.findViewById(R.id.progress_view);
        listView = view.findViewById(R.id.listView);
    }

    private void addData() {
        if (progressBar != null) {
            progressBar.setVisibility(View.VISIBLE);
        }
        ArrayList<EpubInfo> list = new PreferenceUtil(requireActivity().getApplicationContext()).retrieveSearchEpubList(Values.carType + Values.UNDERSCORE + new PreferenceUtil(requireActivity().getApplicationContext()).getSelectedLang() + Values.UNDERSCORE + Values.TYRE_TYPE);

        ArrayList<EpubInfo> finalSearchResultList = WarningLightFragment.searchForTag(list, BaseTabFragmentActivity.keyword);
        if (progressBar != null) {
            progressBar.setVisibility(View.GONE);
        }
        if (finalSearchResultList.size() == 0) {
            listView.setVisibility(View.GONE);
//            linearLayoutNoContent.setVisibility(View.VISIBLE);
        } else {
            setAdapter(finalSearchResultList);
        }
    }

    private void setAdapter(ArrayList<EpubInfo> ePubInfoArrayList) {

        if (Values.carType == 15 || Values.carType == 16) {

            for (Iterator<EpubInfo> iterator = ePubInfoArrayList.listIterator(); iterator.hasNext(); ) {
                EpubInfo info = iterator.next();
                if (Objects.requireNonNull(info.getTitle()).contains("REPAIRING")) {
                    iterator.remove();
                }

            }
        }

        DataGridAdapter adapter = new DataGridAdapter(getActivity(), ePubInfoArrayList, Values.TYRE_TYPE);
        listView.setAdapter(adapter);
    }

}
