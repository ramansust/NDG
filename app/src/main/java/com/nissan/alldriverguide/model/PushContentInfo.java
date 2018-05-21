package com.nissan.alldriverguide.model;

/**
 * Created by shubha on 11/28/17.
 */

public class PushContentInfo {

    private String carId = "", langId = "", ePubId = "", status = "", dateTime = "";

    public String getCarId() {
        return carId;
    }

    public void setCarId(String carId) {
        this.carId = carId;
    }

    public String getLangId() {
        return langId;
    }

    public void setLangId(String langId) {
        this.langId = langId;
    }

    public String getePubId() {
        return ePubId;
    }

    public void setePubId(String ePubId) {
        this.ePubId = ePubId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }
}
