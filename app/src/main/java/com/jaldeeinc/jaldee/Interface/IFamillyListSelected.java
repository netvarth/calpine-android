package com.jaldeeinc.jaldee.Interface;

import com.jaldeeinc.jaldee.model.FamilyArrayModel;

import java.util.List;

public interface IFamillyListSelected {
    void changeMemberName(String name,int id);
    void CheckedFamilyList(List<FamilyArrayModel> familyList);
}
