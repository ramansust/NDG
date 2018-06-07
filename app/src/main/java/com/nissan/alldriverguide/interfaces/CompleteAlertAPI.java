package com.nissan.alldriverguide.interfaces;

import com.nissan.alldriverguide.model.ResponseInfo;
import com.nissan.alldriverguide.multiLang.model.GlobalMsgResponse;

/**
 * Created by mobioapp on 11/30/17.
 */

public interface CompleteAlertAPI {
    void onDownloaded(GlobalMsgResponse responseInfo);
    void onFailed(String failedReason);
}
