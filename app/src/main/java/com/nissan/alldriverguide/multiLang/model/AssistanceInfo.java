package com.nissan.alldriverguide.multiLang.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class AssistanceInfo {

    @SerializedName("assistance_title")
    @Expose
    private String assistanceTitle;
    @SerializedName("status_code")
    @Expose
    private String statusCode;
    @SerializedName("data")
    @Expose
    private List<Datum> data = null;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("selected_car")
    @Expose
    private String selectedCar;
    @SerializedName("assistance_images")
    @Expose
    private AssistanceImages assistanceImages;

    public String getAssistanceTitle() {
        return assistanceTitle;
    }

    public void setAssistanceTitle(String assistanceTitle) {
        this.assistanceTitle = assistanceTitle;
    }

    public void setAssistanceImages(AssistanceImages assistanceImages) {
        this.assistanceImages = assistanceImages;
    }

    public AssistanceImages getAssistanceImages() {
        return assistanceImages;
    }

    public String getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(String statusCode) {
        this.statusCode = statusCode;
    }

    public List<Datum> getData() {
        return data;
    }

    public void setData(List<Datum> data) {
        this.data = data;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getSelectedCar() {
        return selectedCar;
    }

    public void setSelectedCar(String selectedCar) {
        this.selectedCar = selectedCar;
    }

}