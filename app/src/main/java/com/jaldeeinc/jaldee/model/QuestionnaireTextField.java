package com.jaldeeinc.jaldee.model;

public class QuestionnaireTextField {

    public int id;
    public String questionName;
    public String labelName;
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
