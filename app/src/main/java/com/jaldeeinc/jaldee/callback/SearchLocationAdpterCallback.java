package com.jaldeeinc.jaldee.callback;

import com.jaldeeinc.jaldee.model.WorkingModel;
import com.jaldeeinc.jaldee.response.SearchDepartment;
import com.jaldeeinc.jaldee.response.SearchService;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sharmila on 13/9/18.
 */

public interface SearchLocationAdpterCallback {

    void onMethodWorkingCallback(ArrayList<WorkingModel> workingModel, String value);

    void onMethodServiceCallback(ArrayList<SearchService> searchService, String value);

    void onMethodCheckinCallback(int locID,String from,String location);


}