package com.nissan.alldriverguide.multiLang.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class GlobalMessage {

@SerializedName("id")
@Expose
private Integer id;
@SerializedName("search")
@Expose
private String search;
@SerializedName("other")
@Expose
private String other;
@SerializedName("msg")
@Expose
private String msg;
@SerializedName("reset")
@Expose
private String reset;
@SerializedName("ok")
@Expose
private String ok;
@SerializedName("clear")
@Expose
private String clear;
@SerializedName("recent")
@Expose
private String recent;
@SerializedName("cancel")
@Expose
private String cancel;

public Integer getId() {
return id;
}

public void setId(Integer id) {
this.id = id;
}

public String getSearch() {
return search;
}

public void setSearch(String search) {
this.search = search;
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

public String getReset() {
return reset;
}

public void setReset(String reset) {
this.reset = reset;
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

public String getRecent() {
return recent;
}

public void setRecent(String recent) {
this.recent = recent;
}

public String getCancel() {
return cancel;
}

public void setCancel(String cancel) {
this.cancel = cancel;
}

}