package com.jaldeeinc.jaldee.model;

import com.jaldeeinc.jaldee.response.GetQuestion;

import java.io.Serializable;
import java.util.ArrayList;

public class QuestionnaireInput implements Serializable {

    private int questionnaireId;
    private ArrayList<GetQuestion> questions = new ArrayList<>();

    private ArrayList<AnswerLine> answerLine = new ArrayList<>();

    public int getQuestionnaireId() {
        return questionnaireId;
    }

    public void setQuestionnaireId(int questionnaireId) {
        this.questionnaireId = questionnaireId;
    }

    public ArrayList<AnswerLine> getAnswerLines() {
        return answerLine;
    }

    public void setAnswerLines(ArrayList<AnswerLine> answerLine) {
        this.answerLine = answerLine;
    }

    public ArrayList<GetQuestion> getQuestions() {
        return questions;
    }

    public void setQuestions(ArrayList<GetQuestion> questions) {
        this.questions = questions;
    }
}
