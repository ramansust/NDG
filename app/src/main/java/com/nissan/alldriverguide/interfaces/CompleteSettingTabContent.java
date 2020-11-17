package com.nissan.alldriverguide.interfaces;

import com.nissan.alldriverguide.multiLang.model.SettingsTabModel;

/**
 * Created by Rohan on 6/9/2018.
 */

public interface CompleteSettingTabContent {
    void onDownloaded(SettingsTabModel responseInfo);

    void onFailed(String failedReason);
}
