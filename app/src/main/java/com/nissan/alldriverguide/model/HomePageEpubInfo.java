package com.nissan.alldriverguide.model;

//import com.mobioapp.infinitipacket.model.EpubInfo;

import com.datasoft.downloadManager.epubUtils.EpubInfo;

/**
 * Created by mobioapp on 1/14/18.
 */

public class HomePageEpubInfo extends EpubInfo {
    private String sectionTitle;
    private String colorCode;
    private String colorCodeItem;


    public String getSectionTitle() {
        return sectionTitle;
    }

    public void setSectionTitle(String sectionTitle) {
        this.sectionTitle = sectionTitle;
    }

    public String getColorCode() {
        return colorCode;
    }

    public void setColorCode(String colorCode) {
        this.colorCode = colorCode;
    }

    public String getColorCodeItem() {
        return colorCodeItem;
    }

    public void setColorCodeItem(String colorCodeItem) {
        this.colorCodeItem = colorCodeItem;
    }
}
