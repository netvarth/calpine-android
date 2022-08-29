package com.jaldeeinc.jaldee.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

public class Questions implements Serializable {

    @SerializedName("question")
    @Expose
    private GetQuestion getQuestion;

    @SerializedName("questions")
    @Expose
    private ArrayList<GetQuestion> getQuestions;

    public GetQuestion getGetQuestion() {
        return getQuestion;
    }

    public void setGetQuestion(GetQuestion getQuestion) {
        this.getQuestion = getQuestion;
    }

    public ArrayList<GetQuestion> getGetQuestions() {
        return getQuestions;
    }

    public void setGetQuestions(ArrayList<GetQuestion> getQuestions) {
        this.getQuestions = getQuestions;
    }
}
