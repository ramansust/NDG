package com.nissan.alldriverguide.multiLang.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Rohan on 6/9/2018.
 */

public class SettingsTabListModel {

    @SerializedName("index")
    @Expose
    private Integer index;
    @SerializedName("title")
    @Expose
    private String title;
    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("feedback_success")
    @Expose
    private String feedbackSuccess;
    @SerializedName("feedback_title")
    @Expose
    private String feedbackTitle;
    @SerializedName("feedback_description")
    @Expose
    private String feedbackDescription;
    @SerializedName("feedback_submit")
    @Expose
    private String feedbackSubmit;

    public Integer getIndex() {
        return index;
    }

    public void setIndex(Integer index) {
        this.index = index;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getFeedbackSuccess() {
        return feedbackSuccess;
    }

    public void setFeedbackSuccess(String feedbackSuccess) {
        this.feedbackSuccess = feedbackSuccess;
    }

    public String getFeedbackTitle() {
        return feedbackTitle;
    }

    public void setFeedbackTitle(String feedbackTitle) {
        this.feedbackTitle = feedbackTitle;
    }

    public String getFeedbackDescription() {
        return feedbackDescription;
    }

    public void setFeedbackDescription(String feedbackDescription) {
        this.feedbackDescription = feedbackDescription;
    }

    public String getFeedbackSubmit() {
        return feedbackSubmit;
    }

    public void setFeedbackSubmit(String feedbackSubmit) {
        this.feedbackSubmit = feedbackSubmit;
    }
}
