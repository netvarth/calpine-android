package com.jaldeeinc.jaldee.connection;


import com.jaldeeinc.jaldee.model.BillModel;
import com.jaldeeinc.jaldee.model.CheckSumModelTest;
import com.jaldeeinc.jaldee.model.Domain_Spinner;
import com.jaldeeinc.jaldee.model.FamilyArrayModel;
import com.jaldeeinc.jaldee.model.ProviderUserModel;
import com.jaldeeinc.jaldee.model.SearchModel;
import com.jaldeeinc.jaldee.model.TestModel;
import com.jaldeeinc.jaldee.response.ActiveAppointment;
import com.jaldeeinc.jaldee.response.ActiveCheckIn;
import com.jaldeeinc.jaldee.response.AppointmentSchedule;
import com.jaldeeinc.jaldee.response.CheckSumModel;
import com.jaldeeinc.jaldee.response.CoupnResponse;
import com.jaldeeinc.jaldee.response.FavouriteModel;
import com.jaldeeinc.jaldee.response.InboxModel;
import com.jaldeeinc.jaldee.response.JdnResponse;
import com.jaldeeinc.jaldee.response.LocationResponse;
import com.jaldeeinc.jaldee.response.LoginResponse;
import com.jaldeeinc.jaldee.response.MyPayments;
import com.jaldeeinc.jaldee.response.PaymentModel;
import com.jaldeeinc.jaldee.response.PaytmChecksum;
import com.jaldeeinc.jaldee.response.ProfileModel;
import com.jaldeeinc.jaldee.response.QueueList;
import com.jaldeeinc.jaldee.response.QueueTimeSlotModel;
import com.jaldeeinc.jaldee.response.RatingResponse;
import com.jaldeeinc.jaldee.response.RefinedFilters;
import com.jaldeeinc.jaldee.response.ScheduleId;
import com.jaldeeinc.jaldee.response.ScheduleList;
import com.jaldeeinc.jaldee.response.SearchAWsResponse;
import com.jaldeeinc.jaldee.response.SearchAppoinment;
import com.jaldeeinc.jaldee.response.SearchAppointmentDepartmentServices;
import com.jaldeeinc.jaldee.response.SearchCheckInMessage;
import com.jaldeeinc.jaldee.response.SearchDepartment;
import com.jaldeeinc.jaldee.response.SearchDepartmentServices;
import com.jaldeeinc.jaldee.response.SearchDonation;
import com.jaldeeinc.jaldee.response.SearchLocation;
import com.jaldeeinc.jaldee.response.SearchService;
import com.jaldeeinc.jaldee.response.SearchSetting;
import com.jaldeeinc.jaldee.response.SearchTerminology;
import com.jaldeeinc.jaldee.response.SearchUsers;
import com.jaldeeinc.jaldee.response.SearchViewDetail;
import com.jaldeeinc.jaldee.response.SearchVirtualFields;
import com.jaldeeinc.jaldee.response.SectorCheckin;
import com.jaldeeinc.jaldee.response.ShareLocation;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import io.reactivex.rxjava3.core.Observable;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;


/**
 * Created by sharmila on 2/7/18.
 */

public interface ApiInterface {

    @GET("consumer/{mobile}/check")
    Call<ResponseBody> chkNewUser(@Path("mobile") String mobile);

    @Headers({"Accept: application/json","User-Agent: android"})
    @POST("consumer")
    Call<ResponseBody> getSignUpResponse(@Body RequestBody jsonObj);

    @POST("consumer/{otp}/verify")
    @Headers("User-Agent: android")
    Call<ResponseBody> OtpVerify(@Path("otp") String otp);

    @Headers({"Accept: application/json","User-Agent: android"})
    @PUT("consumer/{otp}/activate")
    Call<ResponseBody> SetPassword(@Path("otp") String otp, @Body RequestBody jsonObj);

    @Headers({"Accept: application/json","User-Agent: android"})
    @POST("consumer/login")
    Call<LoginResponse> LoginResponse(@Body RequestBody jsonObj);


    @POST("consumer/login/reset/{loginId}")
    Call<ResponseBody> ForgotPwdResponse(@Path("loginId") String loginId);

    @POST("consumer/login/reset/{otp}/validate")
    Call<String> ForgotResetOtp(@Path("otp") String otp);

    @Headers("Accept: application/json")
    @PUT("consumer/login/reset/{otp}")
    Call<String> SetResetPassword(@Path("otp") String otp, @Body RequestBody jsonObj);

    @GET("consumer/{consumerId}")
    Call<ProfileModel> getProfileDetail(@Path("consumerId") int consumerID);

    @PATCH("consumer")
    Call<ResponseBody> getEditProfileDetail(@Body RequestBody jsonObj);


    @PUT("consumer/login/chpwd")
    Call<ResponseBody> ChangePwd(@Body RequestBody jsonObj);


    @POST("consumer/login/verifyLogin/{email}")
    Call<ResponseBody> ChangeEmail(@Path("email") String email);

    @POST("consumer/login/verifyLogin/{phone}")
    Call<ResponseBody> ChangePhone(@Path("phone") String phone);

    @POST("consumer/waitlist/saveMyLoc/{uuid}")
    Call<ShareLocation> ShareLiveLocation(@Path("uuid") String uuid, @Query("account") String account, @Body RequestBody jsonObj);

    @PUT("consumer/waitlist/updateMyLoc/{uuid}")
    Call<ShareLocation> UpdateShareLiveLocation(@Path("uuid") String uuid, @Query("account") String account, @Body RequestBody jsonObj);

    @PUT("consumer/waitlist/update/travelmode/{uuid}")
    Call<ResponseBody> PutTravelMode(@Path("uuid") String uuid,@Query("account") Integer account,@Body RequestBody jsonObj);

    @PUT("consumer/waitlist/update/travelmode/{uuid}")
    Call<ShareLocation> PutTravelModes(@Path("uuid") String uuid,@Query("account") Integer account,@Body RequestBody jsonObj);

    @PUT("consumer/waitlist/update/latlong/{uuid}")
    Call<ResponseBody> UpdateLatLong(@Path("uuid") String uuid,@Query("account") Integer account,@Body RequestBody jsonObj);

    @PUT("consumer/waitlist/start/mytracking/{uuid}")
    Call<ResponseBody> StartTracking(@Path("uuid") String uuid,@Query("account") Integer account);

    @DELETE("consumer/waitlist/stop/mytracking/{uuid}")
    Call<ResponseBody> StopTracking(@Path("uuid") String uuid,@Query("account") Integer account);

    @GET("consumer/waitlist/status/mytracking/{uuid}")
    Call<ResponseBody> StatusTracking(@Path("uuid") String uuid,@Query("account") Integer account);


    @POST("consumer/appointment/saveMyLoc/{uuid}")
    Call<ShareLocation> ShareLiveLocationAppointment(@Path("uuid") String uuid, @Query("account") String account, @Body RequestBody jsonObj);

    @PUT("consumer/appointment/updateMyLoc/{uuid}")
    Call<ShareLocation> UpdateShareLiveLocationAppointment(@Path("uuid") String uuid, @Query("account") String account, @Body RequestBody jsonObj);

    @PUT("consumer/appointment/update/travelmode/{uuid}")
    Call<ResponseBody> PutTravelModeAppointment(@Path("uuid") String uuid,@Query("account") Integer account,@Body RequestBody jsonObj);

    @PUT("consumer/appointment/update/travelmode/{uuid}")
    Call<ShareLocation> PutTravelModesAppointment(@Path("uuid") String uuid,@Query("account") Integer account,@Body RequestBody jsonObj);

    @PUT("consumer/appointment/update/latlong/{uuid}")
    Call<ResponseBody> UpdateLatLongAppointment(@Path("uuid") String uuid,@Query("account") Integer account,@Body RequestBody jsonObj);

    @PUT("consumer/appointment/start/mytracking/{uuid}")
    Call<ResponseBody> StartTrackingAppointment(@Path("uuid") String uuid,@Query("account") Integer account);

    @DELETE("consumer/appointment/stop/mytracking/{uuid}")
    Call<ResponseBody> StopTrackingAppointment(@Path("uuid") String uuid,@Query("account") Integer account);

    @GET("consumer/appointment/status/mytracking/{uuid}")
    Call<ResponseBody> StatusTrackingAppointment(@Path("uuid") String uuid,@Query("account") Integer account);

    @PUT("consumer/login/{otp}/verifyLogin")
    Call<ResponseBody> ChngeEmailOtp(@Path("otp") String otp, @Body RequestBody jsonObj);


    @POST("consumer/familyMember")
    Call<ResponseBody> AddFamilyMEmber(@Body RequestBody jsonObj);

    @PUT("consumer/familyMember")
    Call<ResponseBody> UpdateFamilyMEmber(@Body RequestBody jsonObj);


    @GET("consumer/familyMember")
    Call<ArrayList<FamilyArrayModel>> getFamilyList();

    @DELETE("consumer/familyMember/{memberId}")
    Call<ResponseBody> getFamilyMEmberDelete(@Path("memberId") int id);

    @DELETE("consumer/login")
    Call<ResponseBody> logOut();

    @GET("consumer/waitlist")
    Call<ArrayList<ActiveCheckIn>> getActiveCheckIn(@QueryMap(encoded = true) Map<String, String> query);

    @GET("consumer/payment")
    Call<ArrayList<MyPayments>> getMyPayments();

    @GET("consumer/payment/{id}")
    Call<MyPayments> getMyPaymentsDetails(@Path("id") String id);

    @GET("consumer/appointment")
    Call<ArrayList<ActiveAppointment>> getActiveAppointment(@QueryMap(encoded = true) Map<String, String> query);


    @GET("consumer/waitlist/{uuid}")
    Call<ActiveCheckIn> getActiveCheckInUUID(@Path("uuid") String uuid,@Query("account") String account);

    @GET("consumer/appointment/{uuid}")
    Call<ActiveCheckIn> getActiveAppointmentUUID(@Path("uuid") String uuid,@Query("account") String account);


    @GET("ynwConf/businessDomains")
    Call<ArrayList<Domain_Spinner>> getAllDomains();

    @GET("ynwConf/searchLabels")
    Call<SearchModel> getAllSearch();

    @GET("ynwConf/searchDomain")
    Call<ResponseBody> getSearchDomain();


    @GET("search")
    Call<SearchAWsResponse> getSearchAWS(@QueryMap(encoded = true) Map<String, String> query, @QueryMap(encoded = true) Map<String, String> params);


    @GET("{consumerID}/businessProfile.json")
    Call<SearchViewDetail> getSearchViewDetail(@Path("consumerID") int consumerid, @Query("modifiedDate") String mDate);

    @GET("{uniqueId}/{userId}/providerBusinessProfile.json")
    Call<SearchViewDetail> getUserBusinessProfile(@Path("uniqueId") int uniqueId, @Path("userId") int userId, @Query("modifiedDate") String mDate);

    @GET("{uniqueId}/{userId}/providerservices.json")
    Call<ArrayList<SearchDepartmentServices>> getDepartmentServices(@Path("uniqueId") int uniqueId, @Path("userId") int userId, @Query("modifiedDate") String mDate);

    @GET("{uniqueId}/{userId}/providerservices.json")
    Call<ArrayList<SearchService>> getUserServices(@Path("uniqueId") int uniqueId, @Path("userId") int userId, @Query("modifiedDate") String mDate);


    @GET("{uniqueId}/{userId}/providerBusinessProfile.json")
    Observable<SearchViewDetail> getUserBusinessProfiles(@Path("uniqueId") int uniqueId, @Path("userId") int userId, @Query("modifiedDate") String mDate);

    @GET("{consumerID}/gallery.json")
    Call<ArrayList<SearchViewDetail>> getSearchGallery(@Path("consumerID") int consumerid, @Query("modifiedDate") String mDate);

    @GET("{consumerID}/location.json")
    Call<ArrayList<SearchLocation>> getSearchViewLoc(@Path("consumerID") int consumerid, @Query("modifiedDate") String mDate);

    @GET("{consumerID}/settings.json")
    Call<SearchSetting> getSearchViewSetting(@Path("consumerID") int consumerid, @Query("modifiedDate") String mDate);

    @GET("{consumerID}/terminologies.json")
    Call<SearchTerminology> getSearchViewTerminology(@Path("consumerID") int consumerid, @Query("modifiedDate") String mDate);

    @GET("{uniqueId}/departmentProviders.json")
    Call<ArrayList<SearchDepartmentServices>> getUserandDepartments(@Path("uniqueId") int uniqueId, @Query("modifiedDate") String mDate);

    // to get only users when there are no departments
    @GET("{uniqueId}/departmentProviders.json")
    Call<ArrayList<ProviderUserModel>> getUsers(@Path("uniqueId") int uniqueId, @Query("modifiedDate") String mDate);


    @GET("{uniqueId}/services.json")
    Call<ArrayList<SearchService>> getService(@Path("uniqueId") int uniqueId, @Query("modifiedDate") String mDate);

    @GET("{uniqueId}/services.json")
    Call<ArrayList<SearchDepartmentServices>> getDepartmentServices(@Path("uniqueId") int uniqueId, @Query("modifiedDate") String mDate);


    @GET("consumer/waitlist/services/{id}")
    Call<ArrayList<SearchService>> getSearchService(@Path("id") int id);

    @GET("consumer/appointment/service/{id}")
    Call<ArrayList<SearchAppoinment>> getSearchAppointment(@Path("id") int id);

    @GET("{consumerID}/apptServices.json")
    Call<ArrayList<SearchAppointmentDepartmentServices>> getAppointmentServices(@Path("consumerID") int consumerid, @Query("modifiedDate") String mDate);


    @GET("consumer/donation/services")
    Call<ArrayList<SearchDonation>> getSearchDonation(@Query("account") int id);

    @POST("consumer/donation")
    Call<ResponseBody> Donation(@Query("account") String account, @Body RequestBody jsonObj);

    @GET("consumer/appointment/schedule/location/{locid}/service/{servid}/date/{dd}")
    Call<ArrayList<AppointmentSchedule>> getAppointmentSchedule(@Path("locid") String locid, @Path("servid") String servid, @Path("dd") String dd, @Query("account") String account);


    @GET("consumer/appointment/schedule/{id}/{dd}")
    Call<ScheduleId> getAppointmentScheduleId(@Path("id") String id, @Path("dd") String dd, @Query("account") String account);

    @GET("consumer/waitlist/department/services")
    Call<SearchDepartment> getDepartment(@Query("account") int id);

    @GET("consumer/waitlist")
    Call<ArrayList<SearchCheckInMessage>> getSearchCheckInMessage(@QueryMap(encoded = true) Map<String, String> query);


    @GET("provider/waitlist/queues/waitingTime/{id}")
    Call<ArrayList<QueueList>> getSearchID(@Path("id") String id);

    @GET("provider/waitlist/queues/providerWaitingTime/{id}")
    Call<ArrayList<QueueList>> getProviderAvailableQTime(@Path("id") String id);

    @GET("provider/appointment/schedule/nextAvailableSchedule/{id}")
    Call<ArrayList<ScheduleList>> getSchedule(@Path("id") String id);



    @GET("provider/business/{id}")
    Call<String> getUniqueID(@Path("id") String id);


    @GET(" provider/waitlist/queues/waitingTime/{queueId}")
    Call<List<QueueList>> getQueueCheckReponse(@Path("queueId") String id);

    @GET(" provider/appointment/schedule/nextAvailableSchedule/{queueId}")
    Call<List<ScheduleList>> getScheduleCheckReponse(@Path("queueId") String id);


    @GET("provider/search/suggester/location")
    Call<ArrayList<LocationResponse>> getSearchLocation(@Query("criteria") String criteria);

    @GET("consumer/waitlist/queues/{serviceID}/{sub_serviceid}/{modifiedDate}")
    Call<ArrayList<QueueTimeSlotModel>> getQueueTimeSlot(@Path("serviceID") String serviceID, @Path("sub_serviceid") String sub_serviceid, @Path("modifiedDate") String modifiedDate, @Query("account") String acountid);


    @GET(" consumer/payment/modes/{accountid}")
    Call<ArrayList<PaymentModel>> getPaymentModes(@Path("accountid") String accountid);


    @GET("consumer/waitlist/history")
    Call<ArrayList<ActiveCheckIn>> getCheckInList(@QueryMap(encoded = true) Map<String, String>   query);

    @GET("consumer/appointment/history")
    Call<ArrayList<ActiveAppointment>> getAppointmentList(@QueryMap(encoded = true) Map<String, String>   query);

    @GET("consumer/waitlist/future")
    Call<ArrayList<ActiveCheckIn>> getFutureCheckInList(@QueryMap(encoded = true) Map<String, String>   query);

    @GET("consumer/appointment/future")
    Call<ArrayList<ActiveAppointment>> getFutureAppointmentList(@QueryMap(encoded = true) Map<String, String>   query);


    @POST("consumer/communications")
    Call<ResponseBody> PostMessage(@Query("account") String account, @Body RequestBody jsonObj);


    @GET("consumer/communications")
    Call<ArrayList<InboxModel>> getMessage();


    @GET("consumer/providers")
    Call<ArrayList<FavouriteModel>> getFavourites();


    @POST("consumer/waitlist/communicate/{waitlistid}")
    Call<ResponseBody> WaitListMessage(@Path("waitlistid") String otp, @Query("account") String account, @Body RequestBody jsonObj);

    @POST("consumer/appointment/communicate/{waitlistid}")
    Call<ResponseBody> AppointmentMessage(@Path("waitlistid") String otp, @Query("account") String account, @Body RequestBody jsonObj);

    @POST("consumer/waitlist/communicate/{waitlistid}")
    Call<ResponseBody> WaitListMessageWithAttachment(@Path("waitlistid") String waitlistId, @Query("account") String account, @Body RequestBody fBody);


    @Headers("User-Agent: android")
    @POST("consumer/payment")
    Call<CheckSumModel> generateHash(@Body RequestBody jsonObj);

    @Headers("User-Agent: android")
    @POST("consumer/payment/status")
    Call<String> verifyRazorpayPayment(@QueryMap(encoded = true) Map<String, String> query);

    @Headers("User-Agent: android")
    @POST("consumer/payment")
    Call<PaytmChecksum> generateHashPaytm(@Body RequestBody jsonObj);

    @POST("consumer/waitlist")
    Call<ResponseBody> Checkin(@Query("account") String account, @Body RequestBody jsonObj);


    @POST("consumer/appointment")
    Call<ResponseBody> Appointment(@Query("account") String account, @Body RequestBody jsonObj);


    @GET("consumer/bill/{ynwuuid}")
    Call<BillModel> getBill(@Path("ynwuuid") String uuid, @Query("account") String account);


    @POST("consumer/jaldee/coupons/{coupon}/{ynwuuid}")
    Call<BillModel> getBillCoupon(@Path("coupon") String coupon,@Path("ynwuuid") String uuid, @Query("account") String account);


    @DELETE("consumer/waitlist/{ynwuuid}")
    Call<ResponseBody> deleteActiveCheckIn(@Path("ynwuuid") String uuid, @Query("account") String account);


    @PUT("consumer/appointment/cancel/{ynwuuid}")
    Call<ResponseBody> deleteAppointment(@Path("ynwuuid") String uuid, @Query("account") String account);

    @GET("consumer/waitlist/{ynwuuid}")
    Call<List<ResponseBody>> waitlist(@Path("ynwuuid") String uuid, @Query("account") String account);

    /*@POST("PayUMoneyHash.php")
    Call<ResponseBody> generateHashTest(@Body RequestBody jsonObj);*/
    @POST("hashgenerator")
    Call<TestModel> generateHashTest();


    @GET(" ynwConf/settings/{sector}/{subsector}")
    Call<SectorCheckin> getSector(@Path("sector") String sector, @Path("subsector") String subsector);


    @PUT("consumer/providers/revealPhoneNo/{providerID}/{revelphone}")
    Call<ResponseBody> RevealPhoneNo(@Path("providerID") int providerID, @Path("revelphone") boolean revealphone);


    @DELETE("consumer/providers/{providerID}")
    Call<ResponseBody> DeleteFavourite(@Path("providerID") int id);

    @POST("consumer/providers/{providerID}")
    Call<ResponseBody> AddFavourite(@Path("providerID") int id);

    @GET("consumer/waitlist/rating")
    Call<ArrayList<RatingResponse>> getRating(@QueryMap(encoded = true) Map<String, String> query);

    @PUT("consumer/waitlist/rating")
    Call<ResponseBody> PutRating(@Query("account") String account, @Body RequestBody jsonObj);



    @POST("consumer/waitlist/rating")
    Call<ResponseBody> PostRating(@Query("account") String account, @Body RequestBody jsonObj);

    @GET("ynwConf/refinedFilters")
    Call<RefinedFilters> getFilters();

    @FormUrlEncoded
    @POST("checksum")
    Call<ArrayList<PaytmChecksum>> getPaytmCheckSum(@Field("TXN_AMOUNT") String txnAmount);


    @FormUrlEncoded
    @POST("hashgenerator")
    Call<CheckSumModelTest> getPayUCheckSum(@Field("TXN_AMOUNT") String txnAmount);


    @GET("{consumerID}/virtualFields.json")
    Call<SearchVirtualFields> getVirtualFields(@Path("consumerID") int consumerid, @Query("modifiedDate") String mDate);


    @GET("{consumerID}/coupon.json")
    Call<ArrayList<CoupnResponse>> getCoupanList(@Path("consumerID") int consumerid, @Query("modifiedDate") String mDate);


    @GET("{consumerID}/jaldeediscount.json")
    Call<JdnResponse> getJdnList(@Path("consumerID") int consumerid, @Query("modifiedDate") String mDate);

    @PUT("consumer/updatePushToken")
    Call<ResponseBody> updatePushToken(@Body RequestBody jsonObj);

    @GET("ynwConf/refinedFilters/{subdomain}")
    Call<RefinedFilters> getMoreFilters(@Path("subdomain") String subdomain);

    @GET("ynwConf/refinedFilters/{domain}/{subdomain}")
    Call<RefinedFilters> getSubDomainMoreFilters(@Path("subdomain") String subdomain,@Path("domain") String domain);

    @GET("consumer/waitlist/providerByDepartmentId/{departmentId}")
    Call<ArrayList<SearchUsers>> getUsers(@Path("departmentId")  int departmentId, @Query("account") int account);


}