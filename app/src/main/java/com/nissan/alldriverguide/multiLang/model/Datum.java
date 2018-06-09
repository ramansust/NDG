package com.nissan.alldriverguide.multiLang.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Datum {

@SerializedName("index")
@Expose
private Integer index;
@SerializedName("title")
@Expose
private String title;
@SerializedName("id")
@Expose
private Integer id;
@SerializedName("child_node")
@Expose
private List<ChildNode> childNode = null;

public Integer getIndex() {
return index;
}

public void setIndex(Integer index) {
this.index = index;
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

public List<ChildNode> getChildNode() {
return childNode;
}

public void setChildNode(List<ChildNode> childNode) {
this.childNode = childNode;
}

}