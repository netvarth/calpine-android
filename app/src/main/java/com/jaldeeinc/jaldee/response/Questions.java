package com.jaldeeinc.jaldee.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Questions implements Serializable {

    @SerializedName("question")
    @Expose
    private GetQuestion getQuestion;

    public GetQuestion getGetQuestion() {
        return getQuestion;
    }

    public void setGetQuestion(GetQuestion getQuestion) {
        this.getQuestion = getQuestion;
    }
}
