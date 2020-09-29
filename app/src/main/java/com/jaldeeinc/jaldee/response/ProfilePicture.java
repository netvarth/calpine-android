package com.jaldeeinc.jaldee.response;

public class ProfilePicture {

    String keyName;
    String caption;
    String prefix;
    String url;
    String thumbUrl;
    String type;



    public ProfilePicture(String keyName, String caption, String prefix, String url, String thumbUrl, String type) {
        this.keyName = keyName;
        this.caption = caption;
        this.prefix = prefix;
        this.url = url;
        this.thumbUrl = thumbUrl;
        this.type = type;
    }

    public ProfilePicture() {
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
}

