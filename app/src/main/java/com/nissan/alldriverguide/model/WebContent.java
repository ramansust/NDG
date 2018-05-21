package com.nissan.alldriverguide.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by nirob on 10/31/17.
 */

public class WebContent implements Parcelable /*implements Parcelable*/ {

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("assistance_id")
    @Expose
    private Integer assistanceId;
    @SerializedName("title")
    @Expose
    private String title;
    @SerializedName("keyword")
    @Expose
    private String keyword;
    @SerializedName("file")
    @Expose
    private String file;

    protected WebContent(Parcel in) {
        if (in.readByte() == 0) {
            id = null;
        } else {
            id = in.readInt();
        }
        if (in.readByte() == 0) {
            assistanceId = null;
        } else {
            assistanceId = in.readInt();
        }
        title = in.readString();
        keyword = in.readString();
        file = in.readString();
    }

    public static final Creator<WebContent> CREATOR = new Creator<WebContent>() {
        @Override
        public WebContent createFromParcel(Parcel in) {
            return new WebContent(in);
        }

        @Override
        public WebContent[] newArray(int size) {
            return new WebContent[size];
        }
    };

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getAssistanceId() {
        return assistanceId;
    }

    public void setAssistanceId(Integer assistanceId) {
        this.assistanceId = assistanceId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public String getFile() {
        return file;
    }

    public void setFile(String file) {
        this.file = file;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        if (id == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(id);
        }
        if (assistanceId == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(assistanceId);
        }
        dest.writeString(title);
        dest.writeString(keyword);
        dest.writeString(file);
    }
}