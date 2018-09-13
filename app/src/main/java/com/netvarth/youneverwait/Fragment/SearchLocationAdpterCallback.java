package com.netvarth.youneverwait.Fragment;

import com.netvarth.youneverwait.model.WorkingModel;
import com.netvarth.youneverwait.response.SearchService;

import java.util.ArrayList;

/**
 * Created by sharmila on 13/9/18.
 */

public interface SearchLocationAdpterCallback {

    void onMethodWorkingCallback(ArrayList<WorkingModel> workingModel, String value);

    void onMethodServiceCallback(ArrayList<SearchService> searchService, String value);


}