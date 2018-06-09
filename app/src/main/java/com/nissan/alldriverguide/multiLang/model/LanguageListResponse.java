package com.nissan.alldriverguide.multiLang.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class LanguageListResponse {


    @SerializedName("language_list")
    @Expose
    private List<LanguageList> languageList = null;

    public List<LanguageList> getLanguageList() {
        return languageList;
    }

    public void setLanguageList(List<LanguageList> languageList) {
        this.languageList = languageList;
    }

}