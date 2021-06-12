package com.jaldeeinc.jaldee.model;

import com.jaldeeinc.jaldee.response.AnswerLineResponse;

import java.util.ArrayList;

public class QuestionnaireResponseInput {

    private int questionnaireId;
    private ArrayList<AnswerLineResponse> answerLine = new ArrayList<>();



    public int getQuestionnaireId() {
        return questionnaireId;
    }

    public void setQuestionnaireId(int questionnaireId) {
        this.questionnaireId = questionnaireId;
    }

    public ArrayList<AnswerLineResponse> getAnswerLines() {
        return answerLine;
    }

    public void setAnswerLines(ArrayList<AnswerLineResponse> answerLine) {
        this.answerLine = answerLine;
    }
}
