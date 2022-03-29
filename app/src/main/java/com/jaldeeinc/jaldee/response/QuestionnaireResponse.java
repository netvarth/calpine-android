package com.jaldeeinc.jaldee.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

public class QuestionnaireResponse implements Serializable {

    @SerializedName("questionnaireId")
    @Expose
    private int questionnaireId;

    @SerializedName("questionnaireName")
    @Expose
    private String questionnaireName;

    @SerializedName("questionAnswers")
    @Expose
    private ArrayList<QuestionAnswers> questionAnswers;

    public int getQuestionnaireId() {
        return questionnaireId;
    }

    public void setQuestionnaireId(int questionnaireId) {
        this.questionnaireId = questionnaireId;
    }

    public ArrayList<QuestionAnswers> getQuestionAnswers() {
        return questionAnswers;
    }

    public void setQuestionAnswers(ArrayList<QuestionAnswers> questionAnswers) {
        this.questionAnswers = questionAnswers;
    }

    public String getQuestionnaireName() {
        return questionnaireName;
    }

    public void setQuestionnaireName(String questionnaireName) {
        this.questionnaireName = questionnaireName;
    }
}
