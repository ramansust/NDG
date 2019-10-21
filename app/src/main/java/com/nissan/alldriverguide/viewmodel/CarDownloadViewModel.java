/*
 * Copyright (c) 17/10/2019
 * Mostasim Billah
 * MobioApp
 */

package com.nissan.alldriverguide.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.MutableLiveData;
import android.content.Context;
import android.support.annotation.NonNull;

import com.nissan.alldriverguide.controller.CarListContentController;
import com.nissan.alldriverguide.interfaces.CarListACompleteAPI;
import com.nissan.alldriverguide.multiLang.model.CarList;
import com.nissan.alldriverguide.multiLang.model.CarListResponse;
import com.nissan.alldriverguide.utils.NissanApp;

import java.util.List;

public class CarDownloadViewModel extends AndroidViewModel implements CarListACompleteAPI {

    private Context context;
    private CarListContentController carListContentController;

    public MutableLiveData<List<CarList>> carList = new MutableLiveData<>();

    public CarDownloadViewModel(@NonNull Application application) {
        super(application);
        context = application.getApplicationContext();
    }

    public void getCarList(String lang_id) {
        if (carListContentController == null) {
            carListContentController = new CarListContentController(this);
        }
        carListContentController.callApi(NissanApp.getInstance().getDeviceID(context), lang_id);
    }

    @Override
    public void onDownloaded(CarListResponse responseInfo) {
        carList.setValue(responseInfo.getCarList());
    }

    @Override
    public void onFailed(String failedReason) {

    }
}