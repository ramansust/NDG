package com.nissan.alldriverguide.fragments.search.model;


import com.datasoft.downloadManager.epubUtils.EpubInfo;

/**
 * Created by shubha on 11/16/17.
 */

public class SearchCombimeterModel {

    private EpubInfo epubInfo;
    private String imagePath;

    public EpubInfo getEpubInfo() {
        return epubInfo;
    }

    public void setEpubInfo(EpubInfo epubInfo) {
        this.epubInfo = epubInfo;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }
}
