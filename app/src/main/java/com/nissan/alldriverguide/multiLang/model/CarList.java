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
    @SerializedName("car_download_img_ldpi")
    @Expose
    private String imgLdpi;
    @SerializedName("car_download_img_xhdpi")
    @Expose
    private String imgXhdpi;
    @SerializedName("car_download_img_xxxhdpi")
    @Expose
    private String imgXXXhdpi;
    @SerializedName("car_download_img_xxhdpi")
    @Expose
    private String imgXXhdpi;
    @SerializedName("car_download_img_hdpi_img")
    @Expose
    private String imgHdpi;
    @SerializedName("car_download_img_large_mdpi")
    @Expose
    private String imgLargeMdpi;
    @SerializedName("car_download_img_xlarge_mdpi")
    @Expose
    private String imgXLargeMdpi;

    public String getImgLdpi() {
        return imgLdpi;
    }

    public void setImgLdpi(String imgLdpi) {
        this.imgLdpi = imgLdpi;
    }

    public String getImgXhdpi() {
        return imgXhdpi;
    }

    public void setImgXhdpi(String imgXhdpi) {
        this.imgXhdpi = imgXhdpi;
    }

    public String getImgXXXhdpi() {
        return imgXXXhdpi;
    }

    public void setImgXXXhdpi(String imgXXXhdpi) {
        this.imgXXXhdpi = imgXXXhdpi;
    }

    public String getImgXXhdpi() {
        return imgXXhdpi;
    }

    public void setImgXXhdpi(String imgXXhdpi) {
        this.imgXXhdpi = imgXXhdpi;
    }

    public String getImgHdpi() {
        return imgHdpi;
    }

    public void setImgHdpi(String imgHdpi) {
        this.imgHdpi = imgHdpi;
    }

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

    public String getImgLargeMdpi() {
        return imgLargeMdpi;
    }

    public void setImgLargeMdpi(String imgLargeMdpi) {
        this.imgLargeMdpi = imgLargeMdpi;
    }

    public String getImgXLargeMdpi() {
        return imgXLargeMdpi;
    }

    public void setImgXLargeMdpi(String imgXLargeMdpi) {
        this.imgXLargeMdpi = imgXLargeMdpi;
    }
}
