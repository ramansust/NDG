package com.nissan.alldriverguide.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

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
}
