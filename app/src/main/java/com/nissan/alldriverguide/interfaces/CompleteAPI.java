package com.nissan.alldriverguide.interfaces;

import com.nissan.alldriverguide.model.ResponseInfo;

/**
 * Created by mobioapp on 11/30/17.
 */

public interface CompleteAPI {
    void onDownloaded(ResponseInfo responseInfo);
    void onFailed(String failedReason);
}
