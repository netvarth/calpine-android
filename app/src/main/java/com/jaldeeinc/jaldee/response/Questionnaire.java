package com.jaldeeinc.jaldee.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class Questionnaire {

    @SerializedName("questionnaireId")
    @Expose
    private String questionnaireId;

    @SerializedName("id")
    @Expose
    private int id;

    @SerializedName("labels")
    @Expose
    private ArrayList<Questions> questionsList;


    public String getQuestionnaireId() {
        return questionnaireId;
    }

    public void setQuestionnaireId(String questionnaireId) {
        this.questionnaireId = questionnaireId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public ArrayList<Questions> getQuestionsList() {
        return questionsList;
    }

    public void setQuestionsList(ArrayList<Questions> questionsList) {
        this.questionsList = questionsList;
    }
}
