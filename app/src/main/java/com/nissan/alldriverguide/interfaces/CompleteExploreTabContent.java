package com.nissan.alldriverguide.interfaces;

import com.nissan.alldriverguide.multiLang.model.ExploreTabModel;

/**
 * Created by Rohan on 6/9/2018.
 */

public interface CompleteExploreTabContent {
    void onDownloaded(ExploreTabModel responseInfo);

    void onFailed(String failedReason);
}
