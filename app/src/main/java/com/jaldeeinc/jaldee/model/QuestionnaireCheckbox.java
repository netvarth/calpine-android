package com.jaldeeinc.jaldee.model;

public class QuestionnaireCheckbox {

    public boolean isChecked;
    public boolean isBase;
    public String text;
    public Float price;

    public QuestionnaireCheckbox(){

    }

    public QuestionnaireCheckbox(boolean isChecked, String text) {
        this.isChecked = isChecked;
        this.text = text;
    }
    public QuestionnaireCheckbox(boolean isChecked, String text, float price, boolean isBase) {
        this.isChecked = isChecked;
        this.text = text;
        this.price = price;
        this.isBase = isBase;
    }

    public boolean isBase() {
        return isBase;
    }

    public void setBase(boolean base) {
        isBase = base;
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

    public Float getPrice() {
        return price;
    }

    public void setPrice(Float price) {
        this.price = price;
    }
}
