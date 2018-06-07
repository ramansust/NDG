package com.nissan.alldriverguide.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class GlobalMsgResponse {

@SerializedName("status_code")
@Expose
private String statusCode;
@SerializedName("global_message")
@Expose
private List<GlobalMessage> globalMessage = null;
@SerializedName("message")
@Expose
private String message;
@SerializedName("alert_message")
@Expose
private List<AlertMessage> alertMessage = null;

public String getStatusCode() {
return statusCode;
}

public void setStatusCode(String statusCode) {
this.statusCode = statusCode;
}

public List<GlobalMessage> getGlobalMessage() {
return globalMessage;
}

public void setGlobalMessage(List<GlobalMessage> globalMessage) {
this.globalMessage = globalMessage;
}

public String getMessage() {
return message;
}

public void setMessage(String message) {
this.message = message;
}

public List<AlertMessage> getAlertMessage() {
return alertMessage;
}

public void setAlertMessage(List<AlertMessage> alertMessage) {
this.alertMessage = alertMessage;
}

}