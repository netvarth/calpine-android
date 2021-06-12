package com.jaldeeinc.jaldee.model;

import java.io.Serializable;
import java.util.ArrayList;

public class QuestionnaireInput implements Serializable {

    private int questionnaireId;
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
}
