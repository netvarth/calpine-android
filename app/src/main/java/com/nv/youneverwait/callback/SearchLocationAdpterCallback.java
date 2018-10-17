package com.nv.youneverwait.callback;

import com.nv.youneverwait.model.WorkingModel;
import com.nv.youneverwait.response.SearchService;

import java.util.ArrayList;

/**
 * Created by sharmila on 13/9/18.
 */

public interface SearchLocationAdpterCallback {

    void onMethodWorkingCallback(ArrayList<WorkingModel> workingModel, String value);

    void onMethodServiceCallback(ArrayList<SearchService> searchService, String value);

    void onMethodCheckinCallback(int locID,String from,String location);


}