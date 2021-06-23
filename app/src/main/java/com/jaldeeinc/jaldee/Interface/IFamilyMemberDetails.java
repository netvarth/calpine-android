package com.jaldeeinc.jaldee.Interface;

import com.jaldeeinc.jaldee.model.FamilyArrayModel;

import java.util.ArrayList;

public interface IFamilyMemberDetails {
    void sendFamilyMemberDetails(int consumerId,String firstName,String lastName,String phone,String email, String countryCode);
    void refreshMultipleMEmList(ArrayList<FamilyArrayModel> familyList);
    void sendFamilyMbrPhoneAndEMail(String phone, String email);
    void closeActivity();
}
