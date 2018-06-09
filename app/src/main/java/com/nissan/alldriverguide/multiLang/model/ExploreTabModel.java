package com.nissan.alldriverguide.multiLang.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class ExploreTabModel {

    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("header_image")
    @Expose
    private String headerImage;
    @SerializedName("video_list")
    @Expose
    private ArrayList<ExploreTabVideoModel> videoList = null;
    @SerializedName("header_background_image")
    @Expose
    private String headerBackgroundImage;
    @SerializedName("status_code")
    @Expose
    private String statusCode;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getHeaderImage() {
        return headerImage;
    }

    public void setHeaderImage(String headerImage) {
        this.headerImage = headerImage;
    }

    public ArrayList<ExploreTabVideoModel> getVideoList() {
        return videoList;
    }

    public void setVideoList(ArrayList<ExploreTabVideoModel> videoList) {
        this.videoList = videoList;
    }

    public String getHeaderBackgroundImage() {
        return headerBackgroundImage;
    }

    public void setHeaderBackgroundImage(String headerBackgroundImage) {
        this.headerBackgroundImage = headerBackgroundImage;
    }

    public String getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(String statusCode) {
        this.statusCode = statusCode;
    }

}