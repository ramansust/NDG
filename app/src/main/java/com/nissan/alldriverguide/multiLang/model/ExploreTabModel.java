package com.nissan.alldriverguide.multiLang.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ExploreTabModel {

    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("video_list")
    @Expose
    private List<ExploreTabVideoModel> videoList = null;
    @SerializedName("status_code")
    @Expose
    private String statusCode;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<ExploreTabVideoModel> getVideoList() {
        return videoList;
    }

    public void setVideoList(List<ExploreTabVideoModel> videoList) {
        this.videoList = videoList;
    }

    public String getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(String statusCode) {
        this.statusCode = statusCode;
    }

}