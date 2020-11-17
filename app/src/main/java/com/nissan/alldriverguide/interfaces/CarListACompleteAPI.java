package com.nissan.alldriverguide.interfaces;

import com.nissan.alldriverguide.multiLang.model.CarListResponse;

/**
 * Created by shubha on 6/11/18.
 */

public interface CarListACompleteAPI {

    void onDownloaded(CarListResponse responseInfo);

    void onFailed(String failedReason);

}
