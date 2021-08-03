package com.jaldeeinc.jaldee.Interface;

import com.jaldeeinc.jaldee.model.FamilyArrayModel;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public interface IFamilyMemberDetails {
    void sendFamilyMemberDetails(int consumerId,String firstName,String lastName,String phone,String email, String countryCode);
    void sendFamilyMemberDetails(int consumerId,String firstName,String lastName,String phone,String email, String countryCode, String whtsappCountryCode, String whatsappNumber, String telegramCountryCode, String telegramNumber, String age, JSONArray preferredLanguages, JSONObject selectedPincode, String gender);
    void refreshMultipleMEmList(ArrayList<FamilyArrayModel> familyList);
    void sendFamilyMbrPhoneAndEMail(String phone, String email, String countryCode);
    void closeActivity();
}
