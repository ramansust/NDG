package com.nissan.alldriverguide.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class LanguageList {

@SerializedName("epub_names")
@Expose
private String epubNames;
@SerializedName("alert_message")
@Expose
private String alertMessage;
@SerializedName("car_name")
@Expose
private String carName;
@SerializedName("language_name")
@Expose
private String languageName;
@SerializedName("total_epub")
@Expose
private Integer totalEpub;
@SerializedName("language_flag")
@Expose
private LanguageFlag languageFlag;
@SerializedName("language_shortcode")
@Expose
private String languageShortcode;
@SerializedName("language_id")
@Expose
private Integer languageId;

public String getEpubNames() {
return epubNames;
}

public void setEpubNames(String epubNames) {
this.epubNames = epubNames;
}

public String getAlertMessage() {
return alertMessage;
}

public void setAlertMessage(String alertMessage) {
this.alertMessage = alertMessage;
}

public String getCarName() {
return carName;
}

public void setCarName(String carName) {
this.carName = carName;
}

public String getLanguageName() {
return languageName;
}

public void setLanguageName(String languageName) {
this.languageName = languageName;
}

public Integer getTotalEpub() {
return totalEpub;
}

public void setTotalEpub(Integer totalEpub) {
this.totalEpub = totalEpub;
}

public LanguageFlag getLanguageFlag() {
return languageFlag;
}

public void setLanguageFlag(LanguageFlag languageFlag) {
this.languageFlag = languageFlag;
}

public String getLanguageShortcode() {
return languageShortcode;
}

public void setLanguageShortcode(String languageShortcode) {
this.languageShortcode = languageShortcode;
}

public Integer getLanguageId() {
return languageId;
}

public void setLanguageId(Integer languageId) {
this.languageId = languageId;
}

}