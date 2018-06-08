package com.nissan.alldriverguide.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.nissan.alldriverguide.multiLang.model.AlertMessage;
import com.nissan.alldriverguide.multiLang.model.TabMenu;
import com.nissan.alldriverguide.multiLang.model.Tutorial;

import java.util.List;

/**
 * Created by mobioapp on 11/30/17.
 */

public class ResponseInfo {
    @SerializedName("status_code")
    @Expose
    private String statusCode;
    @SerializedName("message")
    @Expose
    private String message;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @SerializedName("content_url")
    @Expose
    private String url;

    @SerializedName("asset_url")
    @Expose
    private String assetsUrl;


    @SerializedName("tutorials")
    @Expose
    private List<Tutorial> tutorials = null;

    @SerializedName("tab_menu")
    @Expose
    private List<TabMenu> tabMenu = null;

    @SerializedName("alert_message")
    @Expose
    private List<AlertMessage> alertMessage = null;

    public String getAssetsUrl() {
        return assetsUrl;
    }

    public void setAssetsUrl(String assetsUrl) {
        this.assetsUrl = assetsUrl;
    }

    public String getLangUrl() {
        return langUrl;
    }

    public void setLangUrl(String langUrl) {
        this.langUrl = langUrl;
    }

    @SerializedName("lang_url")
    @Expose
    private String langUrl;

    public String getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(String statusCode) {
        this.statusCode = statusCode;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<Tutorial> getTutorials() {
        return tutorials;
    }

    public void setTutorials(List<Tutorial> tutorials) {
        this.tutorials = tutorials;
    }


    public List<TabMenu> getTabMenu() {
        return tabMenu;
    }

    public void setTabMenu(List<TabMenu> tabMenu) {
        this.tabMenu = tabMenu;
    }

    public List<AlertMessage> getAlertMessage() {
        return alertMessage;
    }

    public void setAlertMessage(List<AlertMessage> alertMessage) {
        this.alertMessage = alertMessage;
    }

}
