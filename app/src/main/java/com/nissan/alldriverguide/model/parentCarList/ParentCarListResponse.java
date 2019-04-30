package com.nissan.alldriverguide.model.parentCarList;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import java.util.List;
/**
 * Awesome Pojo Generator
 * */
public class ParentCarListResponse{
  @SerializedName("parent_car_list")
  @Expose
  private List<Parent_car_list> parent_car_list;
  @SerializedName("status_code")
  @Expose
  private Integer status_code;
  @SerializedName("message")
  @Expose
  private String message;
  public void setParent_car_list(List<Parent_car_list> parent_car_list){
   this.parent_car_list=parent_car_list;
  }
  public List<Parent_car_list> getParent_car_list(){
   return parent_car_list;
  }
  public void setStatus_code(Integer status_code){
   this.status_code=status_code;
  }
  public Integer getStatus_code(){
   return status_code;
  }
  public void setMessage(String message){
   this.message=message;
  }
  public String getMessage(){
   return message;
  }
}