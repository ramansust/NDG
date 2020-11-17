package com.nissan.alldriverguide.interfaces;

import com.nissan.alldriverguide.model.LanguageList;

/**
 * Created by mobioapp on 11/30/17.
 */

public interface CompleteCarwiseLanguageListAPI {
    void onDownloaded(LanguageList responseInfo);

    void onFailed(String failedReason);
}
