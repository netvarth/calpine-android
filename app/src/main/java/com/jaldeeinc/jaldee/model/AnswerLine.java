package com.jaldeeinc.jaldee.model;

import com.google.gson.JsonObject;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.json.JSONObject;

import java.io.Serializable;

public class AnswerLine implements Serializable {

    private String labelName = "";
    @SerializedName("answer")
    @Expose
    private JsonObject answer = new JsonObject();


    public String getLabelName() {
        return labelName;
    }

    public void setLabelName(String labelName) {
        this.labelName = labelName;
    }

    public JsonObject getAnswer() {
        return answer;
    }

    public void setAnswer(JsonObject answer) {
        this.answer = answer;
    }
}
