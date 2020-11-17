package com.nissan.alldriverguide.interfaces;

import com.nissan.alldriverguide.multiLang.model.AssistanceInfo;

/**
 * Created by Rohan on 6/9/2018.
 */

public interface CompleteAssistanceTabContent {
    void onDownloaded(AssistanceInfo responseInfo);

    void onFailed(String failedReason);
}
