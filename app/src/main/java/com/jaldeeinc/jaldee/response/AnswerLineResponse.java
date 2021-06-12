package com.jaldeeinc.jaldee.response;

import com.google.gson.JsonObject;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class AnswerLineResponse implements Serializable {

    @SerializedName("id")
    @Expose
    private int id;

    @SerializedName("labelName")
    @Expose
    private String labelName;

    @SerializedName("answer")
    @Expose
    private JsonObject answer;

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

    public JsonObject getAnswer() {
        return answer;
    }

    public void setAnswer(JsonObject answer) {
        this.answer = answer;
    }
}
