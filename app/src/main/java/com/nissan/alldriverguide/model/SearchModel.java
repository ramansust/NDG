package com.nissan.alldriverguide.model;

/**
 * Created by Rohan on 9/24/2017.
 */

public class SearchModel {

    int id;
    String searchTag;
    String date;
    int count;
    int carType;
    private String langType;

    public SearchModel() {
    }

    public SearchModel(String searchTag, String date, int count, int carType, String langType) {
        this.searchTag = searchTag;
        this.date = date;
        this.count = count;
        this.carType = carType;
        this.langType = langType;
    }

    public SearchModel(int id, String searchTag, String date, int count, int carType) {
        this.id = id;
        this.searchTag = searchTag;
        this.date = date;
        this.count = count;
        this.carType = carType;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getSearchTag() {
        return searchTag;
    }

    public void setSearchTag(String searchTag) {
        this.searchTag = searchTag;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public int getCarType() {
        return carType;
    }

    public void setCarType(int carType) {
        this.carType = carType;
    }
}
