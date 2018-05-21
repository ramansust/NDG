package com.nissan.alldriverguide.model;

import com.mobioapp.infinitipacket.model.EpubInfo;

/**
 * Created by mobioapp on 1/14/18.
 */

public class HomePageEpubInfo extends EpubInfo {
    private String sectionTitle;

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

    private String colorCode;

    public String getColorCodeItem() {
        return colorCodeItem;
    }

    public void setColorCodeItem(String colorCodeItem) {
        this.colorCodeItem = colorCodeItem;
    }

    private String colorCodeItem;
}
