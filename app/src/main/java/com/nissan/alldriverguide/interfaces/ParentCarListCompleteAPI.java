/*
 * Copyright (c) 23/4/2019
 * Mostasim Billah
 * MobioApp
 */

package com.nissan.alldriverguide.interfaces;

import com.nissan.alldriverguide.model.parentCarList.ParentCarListResponse;

public interface ParentCarListCompleteAPI {

    void onDownloaded(ParentCarListResponse parentCarListResponse);
    void onFailed(String failedReason);
}
