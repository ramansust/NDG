package com.nissan.alldriverguide.multiLang.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class GlobalMessage {

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("other")
    @Expose
    private String other;
    @SerializedName("msg")
    @Expose
    private String msg;
    @SerializedName("ok")
    @Expose
    private String ok;
    @SerializedName("clear")
    @Expose
    private String clear;
    @SerializedName("cancel")
    @Expose
    private String cancel;
    @SerializedName("next")
    @Expose
    private String next;
    @SerializedName("no")
    @Expose
    private String no;

    @SerializedName("yes")
    @Expose
    private String yes;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getOther() {
        return other;
    }

    public void setOther(String other) {
        this.other = other;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getOk() {
        return ok;
    }

    public void setOk(String ok) {
        this.ok = ok;
    }

    public String getClear() {
        return clear;
    }

    public void setClear(String clear) {
        this.clear = clear;
    }

    public String getCancel() {
        return cancel;
    }

    public void setCancel(String cancel) {
        this.cancel = cancel;
    }

    public String getNext() {
        return next;
    }


    public String getNo() {
        return no;
    }

    public void setNo(String no) {
        this.no = no;
    }

    public String getYes() {
        return yes;
    }

    public void setYes(String yes) {
        this.yes = yes;
    }
}