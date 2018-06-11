package com.nissan.alldriverguide.multiLang.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by shubha on 6/11/18.
 */

public class CarList {


    @SerializedName("car_details")
    @Expose
    private String carDetails;
    @SerializedName("car_name")
    @Expose
    private String carName;
    @SerializedName("car_img")
    @Expose
    private String carImg;
    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("car_display_name")
    @Expose
    private String carDisplayName;
    @SerializedName("car_unique_name")
    @Expose
    private String carUniqueName;

    public String getCarDetails() {
        return carDetails;
    }

    public void setCarDetails(String carDetails) {
        this.carDetails = carDetails;
    }

    public String getCarName() {
        return carName;
    }

    public void setCarName(String carName) {
        this.carName = carName;
    }

    public String getCarImg() {
        return carImg;
    }

    public void setCarImg(String carImg) {
        this.carImg = carImg;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCarDisplayName() {
        return carDisplayName;
    }

    public void setCarDisplayName(String carDisplayName) {
        this.carDisplayName = carDisplayName;
    }

    public String getCarUniqueName() {
        return carUniqueName;
    }

    public void setCarUniqueName(String carUniqueName) {
        this.carUniqueName = carUniqueName;
    }


}
