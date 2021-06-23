package com.jaldeeinc.jaldee.Interface;

import com.google.gson.JsonObject;
import com.jaldeeinc.jaldee.model.FamilyArrayModel;

import org.json.JSONObject;

import java.util.List;

public interface IFamillyListSelected {
    void changeMemberName(String name,int id);
    void changeMemberName(String name, FamilyArrayModel familylist);

    void CheckedFamilyList(List<FamilyArrayModel> familyList);
    void SelectedPincodeLocation(JsonObject selectedPincodeLocation);


}
