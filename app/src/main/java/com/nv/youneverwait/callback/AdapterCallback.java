package com.nv.youneverwait.callback;

import android.support.v7.widget.RecyclerView;

import com.nv.youneverwait.model.WorkingModel;

import java.util.ArrayList;

/**
 * Created by sharmila on 13/9/18.
 */

public interface AdapterCallback {
    void onMethodCallback(String value);
    void onMethodWorkingCallback(ArrayList<WorkingModel> workingModel,String value,String UniqueID);

    void onMethodServiceCallback(ArrayList services,String value,String uniqueID);

    void onMethodOpenMap(String location);

    void onMethodMessage(String provider,String accountID,String from);

    void onMethodCoupn(String uniqueID);

    void onMethodJaldeeLogo(String ynw_verified,String provider);

    void onMethodFilterRefined(String passformula, RecyclerView recyclepopup,String domainame);

    void onMethodSubDomainFilter(String passformula, RecyclerView recyclepopup,String subdomainame);

    void onMethodQuery(ArrayList<String> formula,ArrayList<String> key);


}