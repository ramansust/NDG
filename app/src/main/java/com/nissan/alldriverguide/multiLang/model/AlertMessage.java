package com.nissan.alldriverguide.multiLang.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class AlertMessage {

    @SerializedName("select_language")
    @Expose
    private SelectLanguage selectLanguage;
    @SerializedName("delete")
    @Expose
    private Delete delete;
    @SerializedName("update_check")
    @Expose
    private UpdateCheck updateCheck;

    public SelectLanguage getSelectLanguage() {
        return selectLanguage;
    }

    public void setSelectLanguage(SelectLanguage selectLanguage) {
        this.selectLanguage = selectLanguage;
    }

    public Delete getDelete() {
        return delete;
    }

    public void setDelete(Delete delete) {
        this.delete = delete;
    }

    public UpdateCheck getUpdateCheck() {
        return updateCheck;
    }

    public void setUpdateCheck(UpdateCheck updateCheck) {
        this.updateCheck = updateCheck;
    }

}
