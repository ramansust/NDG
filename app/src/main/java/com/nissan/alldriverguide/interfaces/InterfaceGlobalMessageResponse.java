package com.nissan.alldriverguide.interfaces;

import com.nissan.alldriverguide.multiLang.model.GlobalMsgResponse;

/**
 * Created by mobioapp on 11/30/17.
 */

public interface InterfaceGlobalMessageResponse {
    void onDownloaded(GlobalMsgResponse responseInfo);

    void onFailed(String failedReason);
}
