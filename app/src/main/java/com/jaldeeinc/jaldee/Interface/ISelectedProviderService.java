package com.jaldeeinc.jaldee.Interface;

import com.jaldeeinc.jaldee.response.SearchAppoinment;
import com.jaldeeinc.jaldee.response.SearchService;

public interface ISelectedProviderService {


    void onCheckInSelected(SearchService checinServiceInfo);

    void onAppointmentSelected(SearchAppoinment appointmentServiceInfo);
}
