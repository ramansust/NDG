package com.nissan.alldriverguide.model;

/**
 * Created by mobioapp on 10/12/17.
 */

public class CallInfo {
    private String countryName;
    private String countryFlag;

    public String getPopupText() {
        return popupText;
    }

    private String popupText;

    public String getCountryName() {
        return countryName;
    }

    public void setCountryName(String countryName) {
        this.countryName = countryName;
    }

/*    public int getFlag() {
        return flag;
    }

    public void setFlag(int flag) {
        this.flag = flag;
    }*/

    public String getCountryFlag() {
        return countryFlag;
    }

    public void setCountryFlag(String countryFlag) {
        this.countryFlag = countryFlag;
    }

    public CallInfo(String countryName, int flag, String nationalNumber, String internationalNumber) {
        this.countryName = countryName;
        this.nationalNumber = nationalNumber;
        this.internationalNumber = internationalNumber;
    }

    public CallInfo(String countryName, String flag, String nationalNumber, String internationalNumber, String popupText) {
        this.countryName = countryName;
        this.countryFlag = flag;
        this.nationalNumber = nationalNumber;
        this.internationalNumber = internationalNumber;
        this.popupText = popupText;
    }

    public String getNationalNumber() {
        return nationalNumber;
    }

    public void setNationalNumber(String nationalNumber) {
        this.nationalNumber = nationalNumber;
    }

    public String getInternationalNumber() {
        return internationalNumber;
    }

    public void setInternationalNumber(String internationalNumber) {
        this.internationalNumber = internationalNumber;
    }

    private String nationalNumber;
    private String internationalNumber;
}
