package com.jaldeeinc.jaldee.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class ViewAttachments implements Serializable {

    @SerializedName("s3path")
    @Expose
    private String s3path;

    @SerializedName("thumbPath")
    @Expose
    private String thumbPath;

    @SerializedName("caption")
    @Expose
    private String caption;

    @SerializedName("type")
    @Expose
    private String type;

    public String getS3path() {
        return s3path;
    }

    public void setS3path(String s3path) {
        this.s3path = s3path;
    }

    public String getThumbPath() {
        return thumbPath;
    }

    public void setThumbPath(String thumbPath) {
        this.thumbPath = thumbPath;
    }

    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
