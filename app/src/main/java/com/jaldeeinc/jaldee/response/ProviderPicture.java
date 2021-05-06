package com.jaldeeinc.jaldee.response;

import java.io.Serializable;

public class ProviderPicture implements Serializable {

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    private String url;

}
