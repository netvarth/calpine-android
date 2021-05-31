package com.jaldeeinc.jaldee.model;

public class QuestionnaireCheckbox {

    public boolean isChecked;
    public String text;

    public QuestionnaireCheckbox(){

    }

    public QuestionnaireCheckbox(boolean isChecked, String text) {
        this.isChecked = isChecked;
        this.text = text;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
