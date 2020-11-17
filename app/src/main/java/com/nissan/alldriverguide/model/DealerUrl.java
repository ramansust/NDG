package com.nissan.alldriverguide.model;

import com.google.gson.annotations.SerializedName;


public class DealerUrl {

    @SerializedName("message")
    private String mMessage;
    @SerializedName("status_code")
    private String mStatusCode;
    @SerializedName("url")
    private String mUrl;

    public String getMessage() {
        return mMessage;
    }

    public void setMessage(String message) {
        mMessage = message;
    }

    public String getStatusCode() {
        return mStatusCode;
    }

    public void setStatusCode(String statusCode) {
        mStatusCode = statusCode;
    }

    public String getUrl() {
        return mUrl;
    }

    public void setUrl(String url) {
        mUrl = url;
    }

}
