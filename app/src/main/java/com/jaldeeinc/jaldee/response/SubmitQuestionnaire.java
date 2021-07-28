package com.jaldeeinc.jaldee.response;

import java.io.Serializable;
import java.util.ArrayList;

public class SubmitQuestionnaire implements Serializable {

    ArrayList<QuestionnaireUrls> urls = new ArrayList<>();

    public ArrayList<QuestionnaireUrls> getUrls() {
        return urls == null ? new ArrayList<>() : urls;
    }

    public void setUrls(ArrayList<QuestionnaireUrls> urls) {
        this.urls = urls;
    }
}
