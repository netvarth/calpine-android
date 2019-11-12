package com.jaldeeinc.jaldee.callback;

import android.content.Context;
import android.support.v7.widget.RecyclerView;

import com.jaldeeinc.jaldee.Fragment.SearchDetailViewFragment;
import com.jaldeeinc.jaldee.model.SearchListModel;
import com.jaldeeinc.jaldee.model.WorkingModel;
import com.jaldeeinc.jaldee.response.SearchDepartment;
import com.jaldeeinc.jaldee.response.SearchViewDetail;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sharmila on 13/9/18.
 */

public interface AdapterCallback {
    void onMethodCallback(String value, String claimable);
    void onMethodWorkingCallback(ArrayList<WorkingModel> workingModel,String value,String UniqueID);

    void onMethodServiceCallback(ArrayList services,String value,String uniqueID);



    void onMethodOpenMap(String location);

    void onMethodMessage(String provider,String accountID,String from);

    void onMethodCoupn(String uniqueID);

    void onMethodJaldeeLogo(String ynw_verified,String provider);

    void onMethodFilterRefined(String passformula, RecyclerView recyclepopup,String domainame);

    void onMethodSubDomainFilter(String passformula, RecyclerView recyclepopup,String subdomainame,String domainName,String displayNameSubdomain);

    void onMethodQuery(ArrayList<String> formula,ArrayList<String> key);
    void onMethodFirstCoupn(String uniqueid);
    void onMethodJdn(String uniqueid);
    void onMethodSpecialization(ArrayList<String> Specialization_displayname, String title);
    void onMethodDepartmentList(ArrayList<String> Departments,String businessName);



}