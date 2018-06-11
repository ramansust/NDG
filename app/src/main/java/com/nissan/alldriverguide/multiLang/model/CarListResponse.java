package com.nissan.alldriverguide.multiLang.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by shubha on 6/11/18.
 */

public class CarListResponse {

    @SerializedName("car_list")
    @Expose
    private List<CarList> carList = null;
    @SerializedName("status_code")
    @Expose
    private String statusCode;
    @SerializedName("message")
    @Expose
    private String message;

    public List<CarList> getCarList() {
        return carList;
    }

    public void setCarList(List<CarList> carList) {
        this.carList = carList;
    }

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
