package com.nissan.alldriverguide.multiLang.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class CountryList {

@SerializedName("thumb_ldpi")
@Expose
private Object thumbLdpi;
@SerializedName("thumb_xxhdpi")
@Expose
private Object thumbXxhdpi;
@SerializedName("thumb_hdpi")
@Expose
private Object thumbHdpi;
@SerializedName("thumb_twox")
@Expose
private Object thumbTwox;
@SerializedName("phone_national")
@Expose
private String phoneNational;
@SerializedName("phone_international")
@Expose
private String phoneInternational;
@SerializedName("thumb_onex")
@Expose
private Object thumbOnex;
@SerializedName("country_name")
@Expose
private String countryName;
@SerializedName("popup")
@Expose
private String popup;
@SerializedName("thumb_threex")
@Expose
private Object thumbThreex;
@SerializedName("country_display_name")
@Expose
private String countryDisplayName;
@SerializedName("id")
@Expose
private Integer id;
@SerializedName("thumb_xxxhdpi")
@Expose
private Object thumbXxxhdpi;
@SerializedName("thumb_xhdpi")
@Expose
private Object thumbXhdpi;

public Object getThumbLdpi() {
return thumbLdpi;
}

public void setThumbLdpi(Object thumbLdpi) {
this.thumbLdpi = thumbLdpi;
}

public Object getThumbXxhdpi() {
return thumbXxhdpi;
}

public void setThumbXxhdpi(Object thumbXxhdpi) {
this.thumbXxhdpi = thumbXxhdpi;
}

public Object getThumbHdpi() {
return thumbHdpi;
}

public void setThumbHdpi(Object thumbHdpi) {
this.thumbHdpi = thumbHdpi;
}

public Object getThumbTwox() {
return thumbTwox;
}

public void setThumbTwox(Object thumbTwox) {
this.thumbTwox = thumbTwox;
}

public String getPhoneNational() {
return phoneNational;
}

public void setPhoneNational(String phoneNational) {
this.phoneNational = phoneNational;
}

public String getPhoneInternational() {
return phoneInternational;
}

public void setPhoneInternational(String phoneInternational) {
this.phoneInternational = phoneInternational;
}

public Object getThumbOnex() {
return thumbOnex;
}

public void setThumbOnex(Object thumbOnex) {
this.thumbOnex = thumbOnex;
}

public String getCountryName() {
return countryName;
}

public void setCountryName(String countryName) {
this.countryName = countryName;
}

public String getPopup() {
return popup;
}

public void setPopup(String popup) {
this.popup = popup;
}

public Object getThumbThreex() {
return thumbThreex;
}

public void setThumbThreex(Object thumbThreex) {
this.thumbThreex = thumbThreex;
}

public String getCountryDisplayName() {
return countryDisplayName;
}

public void setCountryDisplayName(String countryDisplayName) {
this.countryDisplayName = countryDisplayName;
}

public Integer getId() {
return id;
}

public void setId(Integer id) {
this.id = id;
}

public Object getThumbXxxhdpi() {
return thumbXxxhdpi;
}

public void setThumbXxxhdpi(Object thumbXxxhdpi) {
this.thumbXxxhdpi = thumbXxxhdpi;
}

public Object getThumbXhdpi() {
return thumbXhdpi;
}

public void setThumbXhdpi(Object thumbXhdpi) {
this.thumbXhdpi = thumbXhdpi;
}

}