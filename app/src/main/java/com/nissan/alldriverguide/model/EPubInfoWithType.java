package com.nissan.alldriverguide.model;


import com.datasoft.downloadManager.epubUtils.EpubInfo;

import org.jetbrains.annotations.Nullable;

/**
 * Created by shubha on 11/14/17.
 */

public class EPubInfoWithType extends EpubInfo {

    int ePubType = 0;

    public EPubInfoWithType(@Nullable String title, @Nullable String htmlLink, int index, @Nullable String searchTag) {
        super(title, htmlLink, index, searchTag);
    }

    public int getePubType() {
        return ePubType;
    }

    public void setePubType(int ePubType) {
        this.ePubType = ePubType;
    }
}
