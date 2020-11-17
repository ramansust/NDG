package com.nissan.alldriverguide.multiLang.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class LanguageList {
    @SerializedName("cancel")
    @Expose
    private String cancel;
    @SerializedName("ok")
    @Expose
    private String ok;
    @SerializedName("language_id")
    @Expose
    private Integer languageId;
    @SerializedName("language_name")
    @Expose
    private String languageName;
    @SerializedName("total_epub")
    @Expose
    private Integer totalEpub;
    @SerializedName("language_shortcode")
    @Expose
    private String languageShortcode;
    @SerializedName("car_name")
    @Expose
    private String carName;
    @SerializedName("language_flag")
    @Expose
    private LanguageFlag languageFlag;
    @SerializedName("epub_names")
    @Expose
    private String epubNames;
    @SerializedName("alert_message")
    @Expose
    private List<AlertMessage> alertMessage = null;

    public List<AlertMessage> getAlertMessage() {
        return alertMessage;
    }

    public void setAlertMessage(List<AlertMessage> alertMessage) {
        this.alertMessage = alertMessage;
    }

    public Integer getLanguageId() {
        return languageId;
    }

    public void setLanguageId(Integer languageId) {
        this.languageId = languageId;
    }

    public String getLanguageName() {
        return languageName;
    }

    public void setLanguageName(String languageName) {
        this.languageName = languageName;
    }

    public Integer getTotalEpub() {
        return totalEpub;
    }

    public void setTotalEpub(Integer totalEpub) {
        this.totalEpub = totalEpub;
    }

    public String getLanguageShortcode() {
        return languageShortcode;
    }

    public void setLanguageShortcode(String languageShortcode) {
        this.languageShortcode = languageShortcode;
    }

    public String getCarName() {
        return carName;
    }

    public void setCarName(String carName) {
        this.carName = carName;
    }

    public LanguageFlag getLanguageFlag() {
        return languageFlag;
    }

    public void setLanguageFlag(LanguageFlag languageFlag) {
        this.languageFlag = languageFlag;
    }

    public String getEpubNames() {
        return epubNames;
    }

    public void setEpubNames(String epubNames) {
        this.epubNames = epubNames;
    }

    public String getOk() {
        return ok;
    }

    public void setOk(String ok) {
        this.ok = ok;
    }

    public String getCancel() {
        return cancel;
    }

    public void setCancel(String cancel) {
        this.cancel = cancel;
    }

}