package com.jaldeeinc.jaldee.model;

import okhttp3.MediaType;

public class MediaTypeAndExtention {
    MediaType mediaType;
    MediaType mediaTypeWithExtention;

    public MediaType getMediaType() {
        return mediaType;
    }

    public void setMediaType(MediaType mediaType) {
        this.mediaType = mediaType;
    }

    public MediaType getMediaTypeWithExtention() {
        return mediaTypeWithExtention;
    }

    public void setMediaTypeWithExtention(MediaType mediaTypeWithExtention) {
        this.mediaTypeWithExtention = mediaTypeWithExtention;
    }
}
