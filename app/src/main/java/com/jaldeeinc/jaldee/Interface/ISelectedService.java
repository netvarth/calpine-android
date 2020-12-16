package com.jaldeeinc.jaldee.Interface;

import com.jaldeeinc.jaldee.model.ProviderUserModel;
import com.jaldeeinc.jaldee.response.Catalog;
import com.jaldeeinc.jaldee.response.SearchAppoinment;
import com.jaldeeinc.jaldee.response.SearchDonation;
import com.jaldeeinc.jaldee.response.SearchService;

public interface ISelectedService {

    void onCheckInSelected(SearchService checinServiceInfo);

    void onAppointmentSelected(SearchAppoinment appointmentServiceInfo);

    void onProviderSelected(ProviderUserModel providerInfo);

    void onDonationSelected(SearchDonation donationServiceInfo);

    void onCatalogSelected(Catalog catalogInfo);
}
