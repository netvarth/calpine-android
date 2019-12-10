package com.jaldeeinc.jaldee.model;

public class FileAttachment {
    String s3path;
    String thumbPath;
    String caption;

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



}
