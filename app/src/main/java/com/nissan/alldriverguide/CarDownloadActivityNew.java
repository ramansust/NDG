/*
 * Copyright (c) 20/10/2019
 * Mostasim Billah
 * MobioApp
 */

package com.nissan.alldriverguide;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import com.nissan.alldriverguide.adapter.CarDownloadAdapterNew;
import com.nissan.alldriverguide.multiLang.model.CarList;
import com.nissan.alldriverguide.viewmodel.CarDownloadViewModel;

import java.util.List;

public class CarDownloadActivityNew extends AppCompatActivity {

    private CarDownloadViewModel carDownloadViewModel;
    private CarDownloadAdapterNew carDownloadAdapterNew;
    private RecyclerView rvCarList;
    private ProgressBar pbLoader;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.car_download_activity_new);

        carDownloadViewModel = ViewModelProviders.of(this).get(CarDownloadViewModel.class);

        initUI();
        loadData();
    }

    private void loadData() {
        carDownloadViewModel.getCarList("1");

        if (!carDownloadViewModel.carList.hasActiveObservers()) {
            carDownloadViewModel.carList.observe(this, new Observer<List<CarList>>() {
                @Override
                public void onChanged(@Nullable List<CarList> carLists) {
                    pbLoader.setVisibility(View.GONE);
                    Log.e("NEW CR", carLists.size() + "");
                    carDownloadAdapterNew = new CarDownloadAdapterNew(CarDownloadActivityNew.this, carLists);
                    rvCarList.setLayoutManager(new LinearLayoutManager(CarDownloadActivityNew.this));
                    rvCarList.setAdapter(carDownloadAdapterNew);
                }
            });
        }
    }

    private void initUI() {

        rvCarList = findViewById(R.id.rv_car_list_new);
        pbLoader = findViewById(R.id.pb_loader_car_download);
    }

}
