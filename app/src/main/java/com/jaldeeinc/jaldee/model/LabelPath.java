package com.jaldeeinc.jaldee.model;

public class LabelPath {

    private int id = 0;
    private String labelName = "";
    private String path = "";
    private String fileName = "";

    public LabelPath(){

    }

    public LabelPath(int id, String labelName, String path) {
        this.id = id;
        this.labelName = labelName;
        this.path = path;
    }

    public LabelPath(int id, String labelName, String path, String fileName) {
        this.id = id;
        this.labelName = labelName;
        this.path = path;
        this.fileName = fileName;
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getLabelName() {
        return labelName;
    }

    public void setLabelName(String labelName) {
        this.labelName = labelName;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
}
