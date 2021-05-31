package com.jaldeeinc.jaldee.model;

import com.jaldeeinc.jaldee.custom.KeyPairBoolData;

import java.util.ArrayList;

public class QuestnnaireSingleFile {

    public int id;
    public String labelName;
    public String questionName;
    public boolean isManditory = false;

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
}
