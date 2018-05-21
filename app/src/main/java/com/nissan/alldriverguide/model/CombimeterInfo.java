package com.nissan.alldriverguide.model;

/**
 * Created by nirob on 9/20/17.
 */

public class CombimeterInfo {

    String path;
    boolean flag;

    public CombimeterInfo(String path, boolean flag) {
        this.path = path;
        this.flag = flag;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public boolean isFlag() {
        return flag;
    }

    public void setFlag(boolean flag) {
        this.flag = flag;
    }
}
