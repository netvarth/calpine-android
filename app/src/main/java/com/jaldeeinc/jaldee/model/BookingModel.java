package com.jaldeeinc.jaldee.model;

import com.jaldeeinc.jaldee.response.Questionnaire;
import com.jaldeeinc.jaldee.response.SearchTerminology;
import com.jaldeeinc.jaldee.response.ServiceInfo;

import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;

public class BookingModel implements Serializable {

    public String jsonObject;
    public ArrayList<String> imagesList;
    public String message;
    public int accountId;
    public ServiceInfo serviceInfo;
    SearchTerminology mSearchTerminology;
    public int familyEMIID;
    public String phoneNumber;
    public Questionnaire questionnaire;
    public String from;
    public QuestionnaireInput questionnaireInput;
    public ArrayList<String> questionnaireImages;



    public ArrayList<String> getImagesList() {
        return imagesList;
    }

    public void setImagesList(ArrayList<String> imagesList) {
        this.imagesList = imagesList;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getAccountId() {
        return accountId;
    }

    public void setAccountId(int accountId) {
        this.accountId = accountId;
    }

    public ServiceInfo getServiceInfo() {
        return serviceInfo;
    }

    public void setServiceInfo(ServiceInfo serviceInfo) {
        this.serviceInfo = serviceInfo;
    }

    public SearchTerminology getmSearchTerminology() {
        return mSearchTerminology;
    }

    public void setmSearchTerminology(SearchTerminology mSearchTerminology) {
        this.mSearchTerminology = mSearchTerminology;
    }

    public int getFamilyEMIID() {
        return familyEMIID;
    }

    public void setFamilyEMIID(int familyEMIID) {
        this.familyEMIID = familyEMIID;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getJsonObject() {
        return jsonObject;
    }

    public void setJsonObject(String jsonObject) {
        this.jsonObject = jsonObject;
    }

    public Questionnaire getQuestionnaire() {
        return questionnaire;
    }

    public void setQuestionnaire(Questionnaire questionnaire) {
        this.questionnaire = questionnaire;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public QuestionnaireInput getQuestionnaireInput() {
        return questionnaireInput;
    }

    public void setQuestionnaireInput(QuestionnaireInput questionnaireInput) {
        this.questionnaireInput = questionnaireInput;
    }

    public ArrayList<String> getQuestionnaireImages() {
        return questionnaireImages;
    }

    public void setQuestionnaireImages(ArrayList<String> questionnaireImages) {
        this.questionnaireImages = questionnaireImages;
    }
}
