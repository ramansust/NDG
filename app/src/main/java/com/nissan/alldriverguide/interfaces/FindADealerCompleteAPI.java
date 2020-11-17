/*
 * Copyright (c) 4/7/2019
 * Mostasim Billah
 * MobioApp
 */

package com.nissan.alldriverguide.interfaces;

import com.nissan.alldriverguide.model.DealerUrl;

public interface FindADealerCompleteAPI {
    void onDownloaded(DealerUrl dealerUrl);

    void onFailed(String failedReason);
}
