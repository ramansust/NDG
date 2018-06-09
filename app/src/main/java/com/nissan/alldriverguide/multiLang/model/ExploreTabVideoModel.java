package com.nissan.alldriverguide.multiLang.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Rohan on 6/9/2018.
 */

public class ExploreTabVideoModel {
    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("title")
    @Expose
    private String title;
    @SerializedName("sub_title")
    @Expose
    private String subTitle;
    @SerializedName("index")
    @Expose
    private Integer index;
    @SerializedName("thumb_ldpi")
    @Expose
    private String thumbLdpi;
    @SerializedName("thumb_hdpi")
    @Expose
    private String thumbHdpi;
    @SerializedName("thumb_xhdpi")
    @Expose
    private String thumbXhdpi;
    @SerializedName("thumb_xxhdpi")
    @Expose
    private String thumbXxhdpi;
    @SerializedName("thumb_xxxhdpi")
    @Expose
    private String thumbXxxhdpi;
    @SerializedName("thumb_onex")
    @Expose
    private String thumbOnex;
    @SerializedName("thumb_twox")
    @Expose
    private String thumbTwox;
    @SerializedName("thumb_threex")
    @Expose
    private String thumbThreex;
    @SerializedName("video_file")
    @Expose
    private Object videoFile;
    @SerializedName("video_url")
    @Expose
    private String videoUrl;
    @SerializedName("status")
    @Expose
    private Boolean status;
    @SerializedName("created_at")
    @Expose
    private String createdAt;
    @SerializedName("modified_at")
    @Expose
    private String modifiedAt;
    @SerializedName("car")
    @Expose
    private Integer car;
    @SerializedName("language")
    @Expose
    private Integer language;
    @SerializedName("created_by")
    @Expose
    private Integer createdBy;
    @SerializedName("modified_by")
    @Expose
    private Integer modifiedBy;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSubTitle() {
        return subTitle;
    }

    public void setSubTitle(String subTitle) {
        this.subTitle = subTitle;
    }

    public Integer getIndex() {
        return index;
    }

    public void setIndex(Integer index) {
        this.index = index;
    }

    public String getThumbLdpi() {
        return thumbLdpi;
    }

    public void setThumbLdpi(String thumbLdpi) {
        this.thumbLdpi = thumbLdpi;
    }

    public String getThumbHdpi() {
        return thumbHdpi;
    }

    public void setThumbHdpi(String thumbHdpi) {
        this.thumbHdpi = thumbHdpi;
    }

    public String getThumbXhdpi() {
        return thumbXhdpi;
    }

    public void setThumbXhdpi(String thumbXhdpi) {
        this.thumbXhdpi = thumbXhdpi;
    }

    public String getThumbXxhdpi() {
        return thumbXxhdpi;
    }

    public void setThumbXxhdpi(String thumbXxhdpi) {
        this.thumbXxhdpi = thumbXxhdpi;
    }

    public String getThumbXxxhdpi() {
        return thumbXxxhdpi;
    }

    public void setThumbXxxhdpi(String thumbXxxhdpi) {
        this.thumbXxxhdpi = thumbXxxhdpi;
    }

    public String getThumbOnex() {
        return thumbOnex;
    }

    public void setThumbOnex(String thumbOnex) {
        this.thumbOnex = thumbOnex;
    }

    public String getThumbTwox() {
        return thumbTwox;
    }

    public void setThumbTwox(String thumbTwox) {
        this.thumbTwox = thumbTwox;
    }

    public String getThumbThreex() {
        return thumbThreex;
    }

    public void setThumbThreex(String thumbThreex) {
        this.thumbThreex = thumbThreex;
    }

    public Object getVideoFile() {
        return videoFile;
    }

    public void setVideoFile(Object videoFile) {
        this.videoFile = videoFile;
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getModifiedAt() {
        return modifiedAt;
    }

    public void setModifiedAt(String modifiedAt) {
        this.modifiedAt = modifiedAt;
    }

    public Integer getCar() {
        return car;
    }

    public void setCar(Integer car) {
        this.car = car;
    }

    public Integer getLanguage() {
        return language;
    }

    public void setLanguage(Integer language) {
        this.language = language;
    }

    public Integer getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(Integer createdBy) {
        this.createdBy = createdBy;
    }

    public Integer getModifiedBy() {
        return modifiedBy;
    }

    public void setModifiedBy(Integer modifiedBy) {
        this.modifiedBy = modifiedBy;
    }
}
