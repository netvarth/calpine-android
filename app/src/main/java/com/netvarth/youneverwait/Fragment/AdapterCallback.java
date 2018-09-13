package com.netvarth.youneverwait.Fragment;

import com.netvarth.youneverwait.model.WorkingModel;

import java.util.ArrayList;

/**
 * Created by sharmila on 13/9/18.
 */

public interface AdapterCallback {
    void onMethodCallback(String value);
    void onMethodWorkingCallback(ArrayList<WorkingModel> workingModel,String value);

    void onMethodServiceCallback(ArrayList services,String value);
}