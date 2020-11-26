package com.nissan.alldriverguide.model;

/**
 * Created by Rohan on 9/17/2017.
 */

public class EpubModel {
    int id;
    String title;
    String tag;
    String link;
    int carType;
    int epubType;
    private int index;

    public EpubModel() {
    }

    public EpubModel(int id, String title, String tag, String link, int carType, int epubType, int index) {
        this.id = id;
        this.title = title;
        this.tag = tag;
        this.link = link;
        this.carType = carType;
        this.epubType = epubType;
        this.index = index;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public int getCarType() {
        return carType;
    }

    public void setCarType(int carType) {
        this.carType = carType;
    }

    public int getEpubType() {
        return epubType;
    }

    public void setEpubType(int epubType) {
        this.epubType = epubType;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }
}
