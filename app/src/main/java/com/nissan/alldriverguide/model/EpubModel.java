package com.nissan.alldriverguide.model;

/**
 * Created by Rohan on 9/17/2017.
 */

public class EpubModel {
    int id;
    String title;
    String tag;
    String link;
    int cartype;
    int epubtype;
    private int index;

    public EpubModel() {
    }

    public EpubModel(int id, String title, String tag, String link, int cartype, int epubtype, int index) {
        this.id = id;
        this.title = title;
        this.tag = tag;
        this.link = link;
        this.cartype = cartype;
        this.epubtype = epubtype;
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

    public int getCartype() {
        return cartype;
    }

    public void setCartype(int cartype) {
        this.cartype = cartype;
    }

    public int getEpubtype() {
        return epubtype;
    }

    public void setEpubtype(int epubtype) {
        this.epubtype = epubtype;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }
}
