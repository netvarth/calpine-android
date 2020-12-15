package com.jaldeeinc.jaldee.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class ItemImages implements Serializable {

    @SerializedName("keyName")
    @Expose
    private String keyName;

    @SerializedName("caption")
    @Expose
    private String caption;

    @SerializedName("prefix")
    @Expose
    private String prefix;

    @SerializedName("url")
    @Expose
    private String url;

    @SerializedName("thumbUrl")
    @Expose
    private String thumbUrl;

    @SerializedName("type")
    @Expose
    private String type;

    @SerializedName("imageSize")
    @Expose
    private String imageSize;

    @SerializedName("displayImage")
    @Expose
    private boolean displayImage;

    public ItemImages(){

    }

    public ItemImages(String keyName, String caption, String prefix, String url, String thumbUrl, String type, String imageSize, boolean displayImage) {
        this.keyName = keyName;
        this.caption = caption;
        this.prefix = prefix;
        this.url = url;
        this.thumbUrl = thumbUrl;
        this.type = type;
        this.imageSize = imageSize;
        this.displayImage = displayImage;
    }

    public String getKeyName() {
        return keyName;
    }

    public void setKeyName(String keyName) {
        this.keyName = keyName;
    }

    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getThumbUrl() {
        return thumbUrl;
    }

    public void setThumbUrl(String thumbUrl) {
        this.thumbUrl = thumbUrl;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getImageSize() {
        return imageSize;
    }

    public void setImageSize(String imageSize) {
        this.imageSize = imageSize;
    }

    public boolean isDisplayImage() {
        return displayImage;
    }

    public void setDisplayImage(boolean displayImage) {
        this.displayImage = displayImage;
    }
}
