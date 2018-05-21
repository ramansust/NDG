package com.nissan.alldriverguide.model;

/**
 * Created by Rohan on 9/24/2017.
 */

public class SearchModel {

    int id;
    String searchtag;
    String date;
    int count;
    int cartype;
    private String langType;

    public SearchModel() {
    }

    public SearchModel(String searchtag, String date, int count, int cartype, String langType) {
        this.searchtag = searchtag;
        this.date = date;
        this.count = count;
        this.cartype = cartype;
        this.langType = langType;
    }

    public SearchModel(int id, String searchtag, String date, int count, int cartype) {
        this.id = id;
        this.searchtag = searchtag;
        this.date = date;
        this.count = count;
        this.cartype = cartype;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getSearchtag() {
        return searchtag;
    }

    public void setSearchtag(String searchtag) {
        this.searchtag = searchtag;
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

    public int getCartype() {
        return cartype;
    }

    public void setCartype(int cartype) {
        this.cartype = cartype;
    }
}
