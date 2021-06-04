package com.jaldeeinc.jaldee.model;

import org.json.JSONObject;

import java.io.Serializable;

public class AnswerLine implements Serializable {

    private String labelName = "";
    private JSONObject answer = new JSONObject();


    public String getLabelName() {
        return labelName;
    }

    public void setLabelName(String labelName) {
        this.labelName = labelName;
    }

    public JSONObject getAnswer() {
        return answer;
    }

    public void setAnswer(JSONObject answer) {
        this.answer = answer;
    }
}
