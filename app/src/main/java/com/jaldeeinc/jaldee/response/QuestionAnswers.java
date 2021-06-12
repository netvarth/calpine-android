package com.jaldeeinc.jaldee.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class QuestionAnswers implements Serializable {

    @SerializedName("question")
    @Expose
    private GetQuestion getQuestion;

    @SerializedName("answerLine")
    @Expose
    private AnswerLineResponse answerLine;

    public GetQuestion getGetQuestion() {
        return getQuestion;
    }

    public void setGetQuestion(GetQuestion getQuestion) {
        this.getQuestion = getQuestion;
    }


    public AnswerLineResponse getAnswerLine() {
        return answerLine;
    }

    public void setAnswerLine(AnswerLineResponse answerLine) {
        this.answerLine = answerLine;
    }
}
