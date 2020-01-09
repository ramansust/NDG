package com.nissan.alldriverguide.multiLang.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class ExploreTabModel {

    @SerializedName("header_hdpi")
    @Expose
    private String headerHdpi;
    @SerializedName("status_code")
    @Expose
    private String statusCode;
    @SerializedName("header_2x")
    @Expose
    private String header2x;
    @SerializedName("video_list")
    @Expose
    private ArrayList<ExploreTabVideoModel> videoList = null;
    @SerializedName("slide_items")
    @Expose
    private ArrayList<ExploretabSliderModel> mapImageList = null;
    @SerializedName("header_1x")
    @Expose
    private String header1x;
    @SerializedName("header_xhdpi")
    @Expose
    private String headerXhdpi;
    @SerializedName("header_ldpi")
    @Expose
    private String headerLdpi;
    @SerializedName("header_xxxhdpi")
    @Expose
    private String headerXxxhdpi;
    @SerializedName("header_xxhdpi")
    @Expose
    private String headerXxhdpi;
    @SerializedName("header_3x")
    @Expose
    private String header3x;
    @SerializedName("message")
    @Expose
    private String message;

    public String getHeaderHdpi() {
        return headerHdpi;
    }

    public void setHeaderHdpi(String headerHdpi) {
        this.headerHdpi = headerHdpi;
    }

    public String getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(String statusCode) {
        this.statusCode = statusCode;
    }

    public String getHeader2x() {
        return header2x;
    }

    public void setHeader2x(String header2x) {
        this.header2x = header2x;
    }

    public ArrayList<ExploreTabVideoModel> getVideoList() {
        return videoList;
    }

    public void setVideoList(ArrayList<ExploreTabVideoModel> videoList) {
        this.videoList = videoList;
    }

    public String getHeader1x() {
        return header1x;
    }

    public void setHeader1x(String header1x) {
        this.header1x = header1x;
    }

    public String getHeaderXhdpi() {
        return headerXhdpi;
    }

    public void setHeaderXhdpi(String headerXhdpi) {
        this.headerXhdpi = headerXhdpi;
    }

    public String getHeaderLdpi() {
        return headerLdpi;
    }

    public void setHeaderLdpi(String headerLdpi) {
        this.headerLdpi = headerLdpi;
    }

    public String getHeaderXxxhdpi() {
        return headerXxxhdpi;
    }

    public void setHeaderXxxhdpi(String headerXxxhdpi) {
        this.headerXxxhdpi = headerXxxhdpi;
    }

    public String getHeaderXxhdpi() {
        return headerXxhdpi;
    }

    public void setHeaderXxhdpi(String headerXxhdpi) {
        this.headerXxhdpi = headerXxhdpi;
    }

    public String getHeader3x() {
        return header3x;
    }

    public void setHeader3x(String header3x) {
        this.header3x = header3x;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public ArrayList<ExploretabSliderModel> getMapImageList() {
        return mapImageList;
    }

    public void setMapImageList(ArrayList<ExploretabSliderModel> mapImageList) {
        this.mapImageList = mapImageList;
    }
}