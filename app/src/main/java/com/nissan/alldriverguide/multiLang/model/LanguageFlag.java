package com.nissan.alldriverguide.multiLang.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class LanguageFlag {

    @SerializedName("ldpi")
    @Expose
    private String ldpi;
    @SerializedName("xxxhdpi")
    @Expose
    private String xxxhdpi;
    @SerializedName("1x")
    @Expose
    private String _1x;
    @SerializedName("hdpi")
    @Expose
    private String hdpi;
    @SerializedName("xxhdpi")
    @Expose
    private String xxhdpi;
    @SerializedName("3x")
    @Expose
    private String _3x;
    @SerializedName("xhdpi")
    @Expose
    private String xhdpi;
    @SerializedName("2x")
    @Expose
    private String _2x;

    public String getLdpi() {
        return ldpi;
    }

    public void setLdpi(String ldpi) {
        this.ldpi = ldpi;
    }

    public String getXxxhdpi() {
        return xxxhdpi;
    }

    public void setXxxhdpi(String xxxhdpi) {
        this.xxxhdpi = xxxhdpi;
    }

    public String get1x() {
        return _1x;
    }

    public void set1x(String _1x) {
        this._1x = _1x;
    }

    public String getHdpi() {
        return hdpi;
    }

    public void setHdpi(String hdpi) {
        this.hdpi = hdpi;
    }

    public String getXxhdpi() {
        return xxhdpi;
    }

    public void setXxhdpi(String xxhdpi) {
        this.xxhdpi = xxhdpi;
    }

    public String get3x() {
        return _3x;
    }

    public void set3x(String _3x) {
        this._3x = _3x;
    }

    public String getXhdpi() {
        return xhdpi;
    }

    public void setXhdpi(String xhdpi) {
        this.xhdpi = xhdpi;
    }

    public String get2x() {
        return _2x;
    }

    public void set2x(String _2x) {
        this._2x = _2x;
    }

}