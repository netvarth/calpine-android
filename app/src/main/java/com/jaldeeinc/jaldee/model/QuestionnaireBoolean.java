package com.jaldeeinc.jaldee.model;

import java.util.ArrayList;

public class QuestionnaireBoolean {

    public int id;
    public String questionName;
    public String labelName;
    public boolean isManditory = false;
    public ArrayList<String> labels = new ArrayList<>();

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

    public ArrayList<String> getLabels() {
        return labels;
    }

    public void setLabels(ArrayList<String> labels) {
        this.labels = labels;
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
