package com.nissan.alldriverguide.interfaces;

import com.nissan.alldriverguide.model.ResponseInfo;
import com.nissan.alldriverguide.multiLang.model.CarListResponse;

/**
 * Created by mobioapp on 11/30/17.
 */

public interface CompleteAPI {
    // this interface used for getting value from api call

    void onDownloaded(ResponseInfo responseInfo); // this method for getting response body
    void onFailed(String failedReason); // if response body is null then getting FAILED_STATUS 400
}
