package com.jaldeeinc.jaldee.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class FileProperties {

    @SerializedName("minSize")
    @Expose
    private int minSize;

    @SerializedName("maxSize")
    @Expose
    private int maxSize;

    @SerializedName("width")
    @Expose
    private int width;

    @SerializedName("length")
    @Expose
    private int length;

    @SerializedName("fileTypes")
    @Expose
    private ArrayList<String> fileTypes;

    @SerializedName("minNoOfFile")
    @Expose
    private int minNoOfFile;

    @SerializedName("maxNoOfFile")
    @Expose
    private int maxNoOfFile;

    @SerializedName("allowedDocuments")
    @Expose
    private ArrayList<String> allowedDocuments;


    public int getMinSize() {
        return minSize;
    }

    public void setMinSize(int minSize) {
        this.minSize = minSize;
    }

    public int getMaxSize() {
        return maxSize;
    }

    public void setMaxSize(int maxSize) {
        this.maxSize = maxSize;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public ArrayList<String> getFileTypes() {
        return fileTypes;
    }

    public void setFileTypes(ArrayList<String> fileTypes) {
        this.fileTypes = fileTypes;
    }

    public int getMinNoOfFile() {
        return minNoOfFile;
    }

    public void setMinNoOfFile(int minNoOfFile) {
        this.minNoOfFile = minNoOfFile;
    }

    public int getMaxNoOfFile() {
        return maxNoOfFile;
    }

    public void setMaxNoOfFile(int maxNoOfFile) {
        this.maxNoOfFile = maxNoOfFile;
    }

    public ArrayList<String> getAllowedDocuments() {
        return allowedDocuments;
    }

    public void setAllowedDocuments(ArrayList<String> allowedDocuments) {
        this.allowedDocuments = allowedDocuments;
    }
}
