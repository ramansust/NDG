package com.nissan.alldriverguide.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by nirob on 10/31/17.
 */

public class ParentContent {

    @SerializedName("content")
    @Expose
    private List<WebContent> content = null;

    public List<WebContent> getContent() {
        return content;
    }

    public void setContent(List<WebContent> content) {
        this.content = content;
    }

/*    @SerializedName("images")
    @Expose
    private List<Object> images = null;
    @SerializedName("style")
    @Expose
    private List<String> style = null;
    @SerializedName("content")
    @Expose
    private List<WebContent> content = null;

    public List<WebContent> getContent() {
        return content;
    }

    public void setContent(List<WebContent> content) {
        this.content = content;
    }

    public List<Object> getImages() {
        return images;
    }

    public void setImages(List<Object> images) {
        this.images = images;
    }

    public List<String> getStyle() {
        return style;
    }

    public void setStyle(List<String> style) {
        this.style = style;
    }*/
}
