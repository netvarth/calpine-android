package com.jaldeeinc.jaldee.Interface;

import androidx.recyclerview.widget.RecyclerView;

import com.jaldeeinc.jaldee.model.Domain_Spinner;

import java.util.ArrayList;

public interface IFilterOptions {

    public void onDomainSelect(Domain_Spinner domain);

    public void onMethodFilterRefined(String passformula, RecyclerView recyclepopup, String domainame);

    public void onMethodQuery(ArrayList<String> passFormula, ArrayList<String> keyFormula);

    public void onMethodSubDomainFilter(String query, RecyclerView recyclview_popup, String name, String domainSelect, String displayname);
}
