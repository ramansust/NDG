package com.nissan.alldriverguide.model;

import java.util.ArrayList;

/**
 * Created by mobioapp on 1/14/18.
 */

public class HomePageSectionInfo {

    private String sectionTitle;
    private String colorCode;

    public String getColorCodeItem() {
        return colorCodeItem;
    }

    public void setColorCodeItem(String colorCodeItem) {
        this.colorCodeItem = colorCodeItem;
    }

    private String colorCodeItem;
    private ArrayList<HomePageEpubInfo> epublist;

    public String getSectionTitle() {
        return sectionTitle;
    }

    public void setSectionTitle(String sectionTitle) {
        this.sectionTitle = sectionTitle;
    }

    public ArrayList<HomePageEpubInfo> getEpublist() {
        return epublist;
    }

    public void setEpublist(ArrayList<HomePageEpubInfo> epublist) {
        this.epublist = epublist;
    }

    public String getColorCode() {
        return colorCode;
    }

    public void setColorCode(String colorCode) {
        this.colorCode = colorCode;
    }

}
