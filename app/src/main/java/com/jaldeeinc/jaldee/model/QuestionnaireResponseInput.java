package com.jaldeeinc.jaldee.model;

import com.jaldeeinc.jaldee.response.AnswerLineResponse;
import com.jaldeeinc.jaldee.response.GetQuestion;

import java.util.ArrayList;

public class QuestionnaireResponseInput {

    private int questionnaireId;

    private ArrayList<GetQuestion> questions = new ArrayList<>();
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

    public ArrayList<GetQuestion> getQuestions() {
        return questions;
    }

    public void setQuestions(ArrayList<GetQuestion> questions) {
        this.questions = questions;
    }
}
