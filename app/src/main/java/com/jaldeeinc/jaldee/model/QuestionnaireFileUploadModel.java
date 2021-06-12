package com.jaldeeinc.jaldee.model;

import com.jaldeeinc.jaldee.custom.KeyPairBoolData;

import java.util.ArrayList;

public class QuestionnaireFileUploadModel {

    public int id;
    public String questionName;
    public String labelName;
    public ArrayList<KeyPairBoolData> fileNames;
    public boolean isManditory = false;
    public ArrayList<KeyPairBoolData> files = new ArrayList<>();
    public String hint = "";


    public String getQuestionName() {
        return questionName;
    }

    public void setQuestionName(String questionName) {
        this.questionName = questionName;
    }

    public ArrayList<KeyPairBoolData> getFileNames() {
        return fileNames;
    }

    public void setFileNames(ArrayList<KeyPairBoolData> fileNames) {
        this.fileNames = fileNames;
    }

    public boolean isManditory() {
        return isManditory;
    }

    public void setManditory(boolean manditory) {
        isManditory = manditory;
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

    public ArrayList<KeyPairBoolData> getFiles() {
        return files;
    }

    public void setFiles(ArrayList<KeyPairBoolData> files) {
        this.files = files;
    }

    public String getHint() {
        return hint;
    }

    public void setHint(String hint) {
        this.hint = hint;
    }
}
