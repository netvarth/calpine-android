package com.jaldeeinc.jaldee.model;

import java.util.ArrayList;

public class QuestionnaireInput {

    private int questionnaireId;
    private ArrayList<AnswerLine> answerLine;

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
}
