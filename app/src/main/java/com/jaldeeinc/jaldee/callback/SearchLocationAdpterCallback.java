package com.jaldeeinc.jaldee.callback;

import com.jaldeeinc.jaldee.model.WorkingModel;
import com.jaldeeinc.jaldee.response.SearchAppointmentDepartmentServices;
import com.jaldeeinc.jaldee.response.SearchDepartmentServices;
import com.jaldeeinc.jaldee.response.SearchDonation;
import com.jaldeeinc.jaldee.response.SearchService;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sharmila on 13/9/18.
 */

public interface SearchLocationAdpterCallback {

    void onMethodWorkingCallback(ArrayList<WorkingModel> workingModel, String value);

    void onMethodServiceCallback(ArrayList<SearchService> searchService, String value);

    void onMethodServiceCallback(ArrayList<SearchService> searchService, String value,ArrayList<SearchDepartmentServices> mSearchDepartment);

    void onMethodServiceCallbackAppointment(ArrayList<SearchAppointmentDepartmentServices> searchService, String value, ArrayList<SearchDepartmentServices> mSearchDepartment);
    void onMethodServiceCallbackDonation(ArrayList<SearchDonation> searchService, String value);

    void onMethodCheckinCallback(int locID,String from,String location);


    void onUserAppointmentServices(List<SearchService> apptServices, String businessName, ArrayList<SearchDepartmentServices> mSearchDepartmentList);
}