package com.jaldeeinc.jaldee.Interface;

import android.view.View;

import com.google.gson.Gson;
import com.jaldeeinc.jaldee.custom.KeyPairBoolData;
import com.jaldeeinc.jaldee.model.FamilyArrayModel;
import com.jaldeeinc.jaldee.response.CatalogItem;

import java.util.HashMap;

public interface IServiceOption {
    void updateTotalPrice();
    void radioListItemSelected(String s, Float price);
    void savetoDataBase(CatalogItem itemDetails, String serviceOption, String serviceOptionAtachedImages);
    KeyPairBoolData openImageOptions(KeyPairBoolData fileObject, String qLabelName, HashMap<String, View> viewsList);
}
