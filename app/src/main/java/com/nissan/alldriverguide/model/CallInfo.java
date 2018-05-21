package com.nissan.alldriverguide.model;

/**
 * Created by mobioapp on 10/12/17.
 */

public class CallInfo {
    private String countryName;
    private int flag;

    public String getCountryName() {
        return countryName;
    }

    public void setCountryName(String countryName) {
        this.countryName = countryName;
    }

    public int getFlag() {
        return flag;
    }

    public void setFlag(int flag) {
        this.flag = flag;
    }

    public CallInfo(String countryName, int flag, String nationalNumber, String internationalNumber) {
        this.countryName = countryName;
        this.flag = flag;
        this.nationalNumber = nationalNumber;
        this.internationalNumber = internationalNumber;
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
