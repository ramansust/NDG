package com.nissan.alldriverguide.multiLang.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class TabMenu {

@SerializedName("order")
@Expose
private Integer order;
@SerializedName("title")
@Expose
private String title;
@SerializedName("id")
@Expose
private Integer id;

public Integer getOrder() {
return order;
}

public void setOrder(Integer order) {
this.order = order;
}

public String getTitle() {
return title;
}

public void setTitle(String title) {
this.title = title;
}

public Integer getId() {
return id;
}

public void setId(Integer id) {
this.id = id;
}

}