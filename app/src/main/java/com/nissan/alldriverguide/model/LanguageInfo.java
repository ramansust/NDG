package com.nissan.alldriverguide.model;

/**
 * Created by raman on 1/19/17.
 */
public class LanguageInfo {
    private int id;
    private String name;
    private boolean isSelected;
    private String image;

    public LanguageInfo(int id, String name, boolean isSelected, String image) {
        this.id = id;
        this.name = name;
        this.isSelected = isSelected;
        this.image = image;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setIsSelected(boolean isSelected) {
        this.isSelected = isSelected;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
