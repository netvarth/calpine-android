package com.jaldeeinc.jaldee.model;

import com.jaldeeinc.jaldee.custom.KeyPairBoolData;

import java.util.ArrayList;

public class QuestnnaireSingleFile {

    public int id;
    public String labelName;
    public String questionName;
    public boolean isManditory = false;
    public String filePath = "";
    public String hint = "";
    public String type = "";
    public ArrayList<String> allowedTypes;

    public String getQuestionName() {
        return questionName;
    }

    public void setQuestionName(String questionName) {
        this.questionName = questionName;
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

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getHint() {
        return hint;
    }

    public void setHint(String hint) {
        this.hint = hint;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }


    public ArrayList<String> getAllowedTypes() {
        return allowedTypes;
    }

    public void setAllowedTypes(ArrayList<String> allowedTypes) {
        this.allowedTypes = allowedTypes;
    }
}
