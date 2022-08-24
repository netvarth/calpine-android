package com.jaldeeinc.jaldee.connection;


import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.jaldeeinc.jaldee.model.Address;
import com.jaldeeinc.jaldee.model.BillModel;
import com.jaldeeinc.jaldee.model.CheckSumModelTest;
import com.jaldeeinc.jaldee.model.Domain_Spinner;
import com.jaldeeinc.jaldee.model.FamilyArrayModel;
import com.jaldeeinc.jaldee.model.PincodeLocationsResponse;
import com.jaldeeinc.jaldee.model.PriceList;
import com.jaldeeinc.jaldee.model.ProviderUserModel;
import com.jaldeeinc.jaldee.model.SearchModel;
import com.jaldeeinc.jaldee.model.TestModel;
import com.jaldeeinc.jaldee.response.ActiveAppointment;
import com.jaldeeinc.jaldee.response.ActiveCheckIn;
import com.jaldeeinc.jaldee.response.ActiveDonation;
import com.jaldeeinc.jaldee.response.ActiveOrders;
import com.jaldeeinc.jaldee.response.AdvancePaymentDetails;
import com.jaldeeinc.jaldee.response.AdvancePaymentDetailsOrder;
import com.jaldeeinc.jaldee.response.AppointmentSchedule;
import com.jaldeeinc.jaldee.response.Catalog;
import com.jaldeeinc.jaldee.response.CheckSumModel;
import com.jaldeeinc.jaldee.response.CoupnResponse;
import com.jaldeeinc.jaldee.response.FavouriteModel;
import com.jaldeeinc.jaldee.response.InboxList;
import com.jaldeeinc.jaldee.response.InboxModel;
import com.jaldeeinc.jaldee.response.JCash;
import com.jaldeeinc.jaldee.response.JCashExpired;
import com.jaldeeinc.jaldee.response.JCashInfo;
import com.jaldeeinc.jaldee.response.JCashSpentDetails;
import com.jaldeeinc.jaldee.response.JdnResponse;
import com.jaldeeinc.jaldee.response.LocationResponse;
import com.jaldeeinc.jaldee.response.LoginResponse;
import com.jaldeeinc.jaldee.response.MyPayments;
import com.jaldeeinc.jaldee.response.NewInbox;
import com.jaldeeinc.jaldee.response.OrderResponse;
import com.jaldeeinc.jaldee.response.PaymentModel;
import com.jaldeeinc.jaldee.response.PaytmChecksum;
import com.jaldeeinc.jaldee.response.ProfileModel;
import com.jaldeeinc.jaldee.response.ProfilePicture;
import com.jaldeeinc.jaldee.response.Provider;
import com.jaldeeinc.jaldee.response.ProviderCouponResponse;
import com.jaldeeinc.jaldee.response.Questionnaire;
import com.jaldeeinc.jaldee.response.QueueList;
import com.jaldeeinc.jaldee.response.QueueTimeSlotModel;
import com.jaldeeinc.jaldee.response.RatingResponse;
import com.jaldeeinc.jaldee.response.RefinedFilters;
import com.jaldeeinc.jaldee.response.RefundDetails;
import com.jaldeeinc.jaldee.response.Schedule;
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
import com.jaldeeinc.jaldee.response.ShoppingList;
import com.jaldeeinc.jaldee.response.SlotsData;
import com.jaldeeinc.jaldee.response.StoreDetails;
import com.jaldeeinc.jaldee.response.SubmitQuestionnaire;
import com.jaldeeinc.jaldee.response.TeleServiceCheckIn;
import com.jaldeeinc.jaldee.response.TelegramNotificationSettingsResponse;
import com.jaldeeinc.jaldee.response.UserResponse;
import com.jaldeeinc.jaldee.response.WalletCheckSumModel;
import com.jaldeeinc.jaldee.response.WalletEligibleJCash;
import com.jaldeeinc.jaldee.response.WalletPaytmChecksum;


import org.json.JSONObject;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Single;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;
import retrofit2.http.Url;


/**
 * Created by sharmila on 2/7/18.
 */

public interface ApiInterface {
    //@Headers({"BOOKING_REQ_FROM: CONSUMER_APP"})
    @GET("consumer/{mobile}/check")
    Call<ResponseBody> chkNewUser(@Path("mobile") String mobile, @Query("countryCode") String countryCode);

    //@Headers({"Accept: application/json", "User-Agent: android", "BOOKING_REQ_FROM: CONSUMER_APP"})
    @POST("consumer")
    Call<ResponseBody> getSignUpResponse(@Body RequestBody jsonObj);

    @POST("consumer/{otp}/verify")
        //@Headers({"User-Agent: android", "BOOKING_REQ_FROM: CONSUMER_APP"})
    Call<ResponseBody> OtpVerify(@Path("otp") String otp);

    //@Headers({"Accept: application/json", "User-Agent: android", "BOOKING_REQ_FROM: CONSUMER_APP"})
    @PUT("consumer/{otp}/activate")
    Call<ResponseBody> SetPassword(@Path("otp") String otp, @Body RequestBody jsonObj);


    //@Headers({"Accept: application/json", "User-Agent: android", "BOOKING_REQ_FROM: CONSUMER_APP"})
    @POST("consumer/login")
    Call<LoginResponse> LoginResponse(@Body RequestBody jsonObj);

    //@Headers({"Accept: application/json", "User-Agent: android", "BOOKING_REQ_FROM: CONSUMER_APP"})
    @POST("consumer/login")
    Single<LoginResponse> login(@Body RequestBody jsonObj);

    //@Headers({"BOOKING_REQ_FROM: CONSUMER_APP"})
    @POST("consumer/login/reset/{loginId}")
    Call<ResponseBody> ForgotPwdResponse(@Path("loginId") String loginId, @Body String countryCode);

    //@Headers({"BOOKING_REQ_FROM: CONSUMER_APP"})
    @POST("consumer/login/reset/{otp}/validate")
    Call<String> ForgotResetOtp(@Path("otp") String otp);

    //@Headers({"Accept: application/json", "BOOKING_REQ_FROM: CONSUMER_APP"})
    @PUT("consumer/login/reset/{otp}")
    Call<String> SetResetPassword(@Path("otp") String otp, @Body RequestBody jsonObj);

    //@Headers({"BOOKING_REQ_FROM: CONSUMER_APP"})
    @GET("consumer/{consumerId}")
    Call<ProfileModel> getProfileDetail(@Path("consumerId") int consumerID);

    //@Headers({"BOOKING_REQ_FROM: CONSUMER_APP"})
    @PATCH("consumer")
    Call<ResponseBody> getEditProfileDetail(@Body RequestBody jsonObj);

    //@Headers({"BOOKING_REQ_FROM: CONSUMER_APP"})
    @PUT("consumer/login/chpwd")
    Call<ResponseBody> ChangePwd(@Body RequestBody jsonObj);

    //@Headers({"BOOKING_REQ_FROM: CONSUMER_APP"})
    @POST("consumer/login/verifyLogin/{email}")
    Call<ResponseBody> ChangeEmail(@Path("email") String email);

    //@Headers({"BOOKING_REQ_FROM: CONSUMER_APP"})
    @POST("consumer/login/verifyLogin/{phone}")
    Call<ResponseBody> ChangePhone(@Path("phone") String phone, @Query("countryCode") String countryCode);

    //@Headers({"BOOKING_REQ_FROM: CONSUMER_APP"})
    @POST("consumer/waitlist/saveMyLoc/{uuid}")
    Call<ShareLocation> ShareLiveLocation(@Path("uuid") String uuid, @Query("account") String account, @Body RequestBody jsonObj);

    //@Headers({"BOOKING_REQ_FROM: CONSUMER_APP"})
    @PUT("consumer/waitlist/updateMyLoc/{uuid}")
    Call<ShareLocation> UpdateShareLiveLocation(@Path("uuid") String uuid, @Query("account") String account, @Body RequestBody jsonObj);

    //@Headers({"BOOKING_REQ_FROM: CONSUMER_APP"})
    @PUT("consumer/waitlist/update/travelmode/{uuid}")
    Call<ResponseBody> PutTravelMode(@Path("uuid") String uuid, @Query("account") Integer account, @Body RequestBody jsonObj);

    //@Headers({"BOOKING_REQ_FROM: CONSUMER_APP"})
    @PUT("consumer/waitlist/update/travelmode/{uuid}")
    Call<ShareLocation> PutTravelModes(@Path("uuid") String uuid, @Query("account") Integer account, @Body RequestBody jsonObj);

    //@Headers({"BOOKING_REQ_FROM: CONSUMER_APP"})
    @PUT("consumer/waitlist/update/latlong/{uuid}")
    Call<ResponseBody> UpdateLatLong(@Path("uuid") String uuid, @Query("account") Integer account, @Body RequestBody jsonObj);

    //@Headers({"BOOKING_REQ_FROM: CONSUMER_APP"})
    @PUT("consumer/waitlist/start/mytracking/{uuid}")
    Call<ResponseBody> StartTracking(@Path("uuid") String uuid, @Query("account") Integer account);

    //@Headers({"BOOKING_REQ_FROM: CONSUMER_APP"})
    @DELETE("consumer/waitlist/stop/mytracking/{uuid}")
    Call<ResponseBody> StopTracking(@Path("uuid") String uuid, @Query("account") Integer account);

    //@Headers({"BOOKING_REQ_FROM: CONSUMER_APP"})
    @GET("consumer/waitlist/status/mytracking/{uuid}")
    Call<ResponseBody> StatusTracking(@Path("uuid") String uuid, @Query("account") Integer account);

    //@Headers({"BOOKING_REQ_FROM: CONSUMER_APP"})
    @POST("consumer/appointment/saveMyLoc/{uuid}")
    Call<ShareLocation> ShareLiveLocationAppointment(@Path("uuid") String uuid, @Query("account") String account, @Body RequestBody jsonObj);

    //@Headers({"BOOKING_REQ_FROM: CONSUMER_APP"})
    @PUT("consumer/appointment/updateMyLoc/{uuid}")
    Call<ShareLocation> UpdateShareLiveLocationAppointment(@Path("uuid") String uuid, @Query("account") String account, @Body RequestBody jsonObj);

    //@Headers({"BOOKING_REQ_FROM: CONSUMER_APP"})
    @PUT("consumer/appointment/update/travelmode/{uuid}")
    Call<ResponseBody> PutTravelModeAppointment(@Path("uuid") String uuid, @Query("account") Integer account, @Body RequestBody jsonObj);

    //@Headers({"BOOKING_REQ_FROM: CONSUMER_APP"})
    @PUT("consumer/appointment/update/travelmode/{uuid}")
    Call<ShareLocation> PutTravelModesAppointment(@Path("uuid") String uuid, @Query("account") Integer account, @Body RequestBody jsonObj);

    //@Headers({"BOOKING_REQ_FROM: CONSUMER_APP"})
    @PUT("consumer/appointment/update/latlong/{uuid}")
    Call<ResponseBody> UpdateLatLongAppointment(@Path("uuid") String uuid, @Query("account") Integer account, @Body RequestBody jsonObj);

    //@Headers({"BOOKING_REQ_FROM: CONSUMER_APP"})
    @PUT("consumer/appointment/start/mytracking/{uuid}")
    Call<ResponseBody> StartTrackingAppointment(@Path("uuid") String uuid, @Query("account") Integer account);

    //@Headers({"BOOKING_REQ_FROM: CONSUMER_APP"})
    @DELETE("consumer/appointment/stop/mytracking/{uuid}")
    Call<ResponseBody> StopTrackingAppointment(@Path("uuid") String uuid, @Query("account") Integer account);

    //@Headers({"BOOKING_REQ_FROM: CONSUMER_APP"})
    @GET("consumer/appointment/status/mytracking/{uuid}")
    Call<ResponseBody> StatusTrackingAppointment(@Path("uuid") String uuid, @Query("account") Integer account);

    //@Headers({"BOOKING_REQ_FROM: CONSUMER_APP"})
    @PUT("consumer/login/{otp}/verifyLogin")
    Call<ResponseBody> ChngeEmailOtp(@Path("otp") String otp, @Body RequestBody jsonObj);

    //@Headers({"BOOKING_REQ_FROM: CONSUMER_APP"})
    @POST("consumer/familyMember")
    Call<Integer> AddFamilyMEmber(@Body RequestBody jsonObj);

    //@Headers({"BOOKING_REQ_FROM: CONSUMER_APP"})
    @PUT("consumer/familyMember")
    Call<ResponseBody> UpdateFamilyMEmber(@Body RequestBody jsonObj);

    //@Headers({"BOOKING_REQ_FROM: CONSUMER_APP"})
    @GET("consumer/familyMember")
    Call<ArrayList<FamilyArrayModel>> getFamilyList();

    @GET("consumer/familyMember/providerconsumer/{consumerId}?")
    Call<ResponseBody> getFamilyMemberProviderConsumer(@Path("consumerId") int familyMemId, @Query("account") Integer account);

    @POST("consumer/familyMember/providerconsumer/{familyMemId}/{providerConsumerId}")
    Call<ResponseBody> AddFamilyMEmberProviderConsumer(@Path("familyMemId") int familyMemId, @Path("providerConsumerId") int providerConsumerId, @Query("account") String account);

    //@Headers({"BOOKING_REQ_FROM: CONSUMER_APP"})
    @GET("provider/account/settings/locations/{pincode}")
    Call<ArrayList<PincodeLocationsResponse>> getPinLocations(@Path("pincode") int id);

    //@Headers({"BOOKING_REQ_FROM: CONSUMER_APP"})
    @DELETE("consumer/familyMember/{memberId}")
    Call<ResponseBody> getFamilyMEmberDelete(@Path("memberId") int id);

    //@Headers({"BOOKING_REQ_FROM: CONSUMER_APP"})
    @DELETE("consumer/login")
    Call<ResponseBody> logOut();

    //@Headers({"BOOKING_REQ_FROM: CONSUMER_APP"})
    @GET("consumer/waitlist")
    Call<ArrayList<ActiveCheckIn>> getActiveCheckIn(@QueryMap(encoded = true) Map<String, String> query);

    //@Headers({"BOOKING_REQ_FROM: CONSUMER_APP"})
    @GET("consumer/payment")
    Call<ArrayList<MyPayments>> getMyPayments();

    //@Headers({"BOOKING_REQ_FROM: CONSUMER_APP"})
    @GET("consumer/payment/{id}")
    Call<MyPayments> getMyPaymentsDetails(@Path("id") String id);

    //@Headers({"BOOKING_REQ_FROM: CONSUMER_APP"})
    @GET("consumer/appointment")
    Call<ArrayList<ActiveAppointment>> getActiveAppointment(@QueryMap(encoded = true) Map<String, String> query);

    //@Headers({"BOOKING_REQ_FROM: CONSUMER_APP"})
    @GET("consumer/payment/details/{uuid}")
    Call<ArrayList<RefundDetails>> getRefundDetails(@Path("uuid") String uuid);

    //@Headers({"BOOKING_REQ_FROM: CONSUMER_APP"})
    @GET("consumer/waitlist/{uuid}")
    Call<ActiveCheckIn> getActiveCheckInUUID(@Path("uuid") String uuid, @Query("account") String account);

    //@Headers({"BOOKING_REQ_FROM: CONSUMER_APP"})
    @GET("consumer/appointment/{uuid}")
    Call<ActiveAppointment> getActiveAppointmentUUID(@Path("uuid") String uuid, @Query("account") String account);

    //@Headers({"BOOKING_REQ_FROM: CONSUMER_APP"})
    @GET("consumer/donation/{uuid}")
    Call<ActiveDonation> getActiveDonationUUID(@Path("uuid") String uuid, @Query("account") String account);

    //@Headers({"BOOKING_REQ_FROM: CONSUMER_APP"})
    @GET("ynwConf/businessDomains")
    Call<ArrayList<Domain_Spinner>> getAllDomains();

    //@Headers({"BOOKING_REQ_FROM: CONSUMER_APP"})
    @GET("ynwConf/searchLabels")
    Call<SearchModel> getAllSearch();

    //@Headers({"BOOKING_REQ_FROM: CONSUMER_APP"})
    @GET("ynwConf/searchDomain")
    Call<ResponseBody> getSearchDomain();

    //@Headers({"BOOKING_REQ_FROM: CONSUMER_APP"})
    @GET("search")
    Call<SearchAWsResponse> getSearchAWS(@QueryMap(encoded = true) Map<String, String> query, @QueryMap(encoded = true) Map<String, String> params);

    //@Headers({"BOOKING_REQ_FROM: CONSUMER_APP"})
    @GET("{consumerID}/businessProfile.json")
    Call<SearchViewDetail> getSearchViewDetail(@Path("consumerID") int consumerid, @Query("modifiedDate") String mDate);

    //@Headers({"BOOKING_REQ_FROM: CONSUMER_APP"})
    @GET("{uniqueId}/{userId}/providerBusinessProfile.json")
    Call<SearchViewDetail> getUserBusinessProfile(@Path("uniqueId") int uniqueId, @Path("userId") int userId, @Query("modifiedDate") String mDate);

    //@Headers({"BOOKING_REQ_FROM: CONSUMER_APP"})
    @GET("{uniqueId}/{userId}/providerservices.json")
    Call<ArrayList<SearchDepartmentServices>> getDepartmentServices(@Path("uniqueId") int uniqueId, @Path("userId") int userId, @Query("modifiedDate") String mDate);

    //@Headers({"BOOKING_REQ_FROM: CONSUMER_APP"})
    @GET("{uniqueId}/{userId}/providerservices.json")
    Call<ArrayList<SearchService>> getUserServices(@Path("uniqueId") int uniqueId, @Path("userId") int userId, @Query("modifiedDate") String mDate);

    //@Headers({"BOOKING_REQ_FROM: CONSUMER_APP"})
    @GET("{uniqueId}/{userId}/providerApptServices.json")
    Call<ArrayList<SearchAppointmentDepartmentServices>> getAppointmentServices(@Path("uniqueId") int uniqueId, @Path("userId") int userId, @Query("modifiedDate") String mDate);

    //@Headers({"BOOKING_REQ_FROM: CONSUMER_APP"})
    @GET("{uniqueId}/{userId}/providerApptServices.json")
    Call<ArrayList<SearchService>> getUserAppointmentServices(@Path("uniqueId") int uniqueId, @Path("userId") int userId, @Query("modifiedDate") String mDate);

    //@Headers({"BOOKING_REQ_FROM: CONSUMER_APP"})
    @GET("{uniqueId}/{userId}/providerBusinessProfile.json")
    Observable<SearchViewDetail> getUserBusinessProfiles(@Path("uniqueId") int uniqueId, @Path("userId") int userId, @Query("modifiedDate") String mDate);

    //@Headers({"BOOKING_REQ_FROM: CONSUMER_APP"})
    @GET("{consumerID}/gallery.json")
    Call<ArrayList<SearchViewDetail>> getSearchGallery(@Path("consumerID") int consumerid, @Query("modifiedDate") String mDate);

    //@Headers({"BOOKING_REQ_FROM: CONSUMER_APP"})
    @GET("{consumerID}/location.json")
    Call<ArrayList<SearchLocation>> getSearchViewLoc(@Path("consumerID") int consumerid, @Query("modifiedDate") String mDate);

    //@Headers({"BOOKING_REQ_FROM: CONSUMER_APP"})
    @GET("{consumerID}/settings.json")
    Call<SearchSetting> getSearchViewSetting(@Path("consumerID") int consumerid, @Query("modifiedDate") String mDate);

    //@Headers({"BOOKING_REQ_FROM: CONSUMER_APP"})
    @GET("{consumerID}/terminologies.json")
    Call<SearchTerminology> getSearchViewTerminology(@Path("consumerID") int consumerid, @Query("modifiedDate") String mDate);

    //@Headers({"BOOKING_REQ_FROM: CONSUMER_APP"})
    @GET("{uniqueId}/departmentProviders.json")
    Call<ArrayList<SearchDepartmentServices>> getUserandDepartments(@Path("uniqueId") int uniqueId, @Query("modifiedDate") String mDate);

    // to get only users when there are no departments
    //@Headers({"BOOKING_REQ_FROM: CONSUMER_APP"})
    @GET("{uniqueId}/departmentProviders.json")
    Call<ArrayList<ProviderUserModel>> getUsers(@Path("uniqueId") int uniqueId, @Query("modifiedDate") String mDate);

    //@Headers({"BOOKING_REQ_FROM: CONSUMER_APP"})
    @GET("{uniqueId}/services.json")
    Call<ArrayList<SearchService>> getService(@Path("uniqueId") int uniqueId, @Query("modifiedDate") String mDate);

    //@Headers({"BOOKING_REQ_FROM: CONSUMER_APP"})
    @GET("{uniqueId}/services.json")
    Call<ArrayList<SearchDepartmentServices>> getDepartmentServices(@Path("uniqueId") int uniqueId, @Query("modifiedDate") String mDate);

    //@Headers({"BOOKING_REQ_FROM: CONSUMER_APP"})
    @GET("consumer/waitlist/services/{id}")
    Call<ArrayList<SearchService>> getSearchService(@Path("id") int id);

    //@Headers({"BOOKING_REQ_FROM: CONSUMER_APP"})
    @GET("consumer/appointment/service/{id}")
    Call<ArrayList<SearchAppoinment>> getSearchAppointment(@Path("id") int id);

    //@Headers({"BOOKING_REQ_FROM: CONSUMER_APP"})
    @GET("{consumerID}/apptServices.json")
    Call<ArrayList<SearchAppointmentDepartmentServices>> getAppointmentServices(@Path("consumerID") int consumerid, @Query("modifiedDate") String mDate);

    //@Headers({"BOOKING_REQ_FROM: CONSUMER_APP"})
    @GET("consumer/donation/services")
    Call<ArrayList<SearchDonation>> getSearchDonation(@Query("account") int id);

    //@Headers({"BOOKING_REQ_FROM: CONSUMER_APP"})
    @POST("consumer/donation")
    Call<ResponseBody> Donation(@Query("account") String account, @Body RequestBody jsonObj);

    //@Headers({"BOOKING_REQ_FROM: CONSUMER_APP"})
    @GET("consumer/appointment/schedule/location/{locid}/service/{servid}/date/{dd}")
    Call<ArrayList<AppointmentSchedule>> getAppointmentSchedule(@Path("locid") String locid, @Path("servid") String servid, @Path("dd") String dd, @Query("account") String account);

    //@Headers({"BOOKING_REQ_FROM: CONSUMER_APP"})
    @GET("consumer/appointment/schedule/{id}/{dd}")
    Call<ScheduleId> getAppointmentScheduleId(@Path("id") String id, @Path("dd") String dd, @Query("account") String account);

    //@Headers({"BOOKING_REQ_FROM: CONSUMER_APP"})
    @GET("consumer/waitlist/department/services")
    Call<SearchDepartment> getDepartment(@Query("account") int id);

    //@Headers({"BOOKING_REQ_FROM: CONSUMER_APP"})
    @GET("consumer/waitlist")
    Call<ArrayList<SearchCheckInMessage>> getSearchCheckInMessage(@QueryMap(encoded = true) Map<String, String> query);

    //@Headers({"BOOKING_REQ_FROM: CONSUMER_APP"})
    @GET("provider/waitlist/queues/waitingTime/{id}")
    Call<ArrayList<QueueList>> getSearchID(@Path("id") String id);

    //@Headers({"BOOKING_REQ_FROM: CONSUMER_APP"})
    @GET("provider/waitlist/queues/providerWaitingTime/{id}")
    Call<ArrayList<QueueList>> getProviderAvailableQTime(@Path("id") String id);

    //@Headers({"BOOKING_REQ_FROM: CONSUMER_APP"})
    @GET("provider/appointment/schedule/nextAvailableSchedule/{id}")
    Call<ArrayList<ScheduleList>> getSchedule(@Path("id") String id);

    //@Headers({"BOOKING_REQ_FROM: CONSUMER_APP"})
    @GET("provider/business/{id}")
    Call<String> getUniqueID(@Path("id") String id);

    //@Headers({"BOOKING_REQ_FROM: CONSUMER_APP"})
    @GET(" provider/waitlist/queues/waitingTime/{queueId}")
    Call<List<QueueList>> getQueueCheckReponse(@Path("queueId") String id);

    //@Headers({"BOOKING_REQ_FROM: CONSUMER_APP"})
    @GET(" provider/appointment/schedule/nextAvailableSchedule/{queueId}")
    Call<ArrayList<ScheduleList>> getScheduleCheckReponse(@Path("queueId") String id);

    //@Headers({"BOOKING_REQ_FROM: CONSUMER_APP"})
    @GET("provider/search/suggester/location")
    Call<ArrayList<LocationResponse>> getSearchLocation(@Query("criteria") String criteria);

    //@Headers({"BOOKING_REQ_FROM: CONSUMER_APP"})
    @GET("consumer/waitlist/queues/{serviceID}/{sub_serviceid}/{modifiedDate}")
    Call<ArrayList<QueueTimeSlotModel>> getQueueTimeSlot(@Path("serviceID") String serviceID, @Path("sub_serviceid") String sub_serviceid, @Path("modifiedDate") String modifiedDate, @Query("account") String acountid);

    //@Headers({"BOOKING_REQ_FROM: CONSUMER_APP"})
    @GET("consumer/waitlist/history")
    Call<ArrayList<ActiveCheckIn>> getCheckInList(@QueryMap(encoded = true) Map<String, String> query);

    //@Headers({"BOOKING_REQ_FROM: CONSUMER_APP"})
    @GET("consumer/appointment/history")
    Call<ArrayList<ActiveAppointment>> getAppointmentList(@QueryMap(encoded = true) Map<String, String> query);

    //@Headers({"BOOKING_REQ_FROM: CONSUMER_APP"})
    @GET("consumer/waitlist/future")
    Call<ArrayList<ActiveCheckIn>> getFutureCheckInList(@QueryMap(encoded = true) Map<String, String> query);

    //@Headers({"BOOKING_REQ_FROM: CONSUMER_APP"})
    @GET("consumer/appointment/future")
    Call<ArrayList<ActiveAppointment>> getFutureAppointmentList(@QueryMap(encoded = true) Map<String, String> query);

    //@Headers({"BOOKING_REQ_FROM: CONSUMER_APP"})
    @POST("consumer/communications")
    Call<ResponseBody> PostMessage(@Query("account") String account, @Body RequestBody jsonObj);

    //@Headers({"BOOKING_REQ_FROM: CONSUMER_APP"})
    @POST("consumer/communications")
    Call<ResponseBody> postProviderMessage(@Query("account") String account, @Query("provider") String provider, @Body RequestBody jsonObj);

    //@Headers({"BOOKING_REQ_FROM: CONSUMER_APP"})
    @GET("consumer/communications")
    Call<ArrayList<InboxModel>> getMessage();

    //@Headers({"BOOKING_REQ_FROM: CONSUMER_APP"})
    @GET("consumer/communications")
    Call<ArrayList<InboxList>> getCommunications();

    //@Headers({"BOOKING_REQ_FROM: CONSUMER_APP"})
    @GET("consumer/providers")
    Call<ArrayList<FavouriteModel>> getFavourites();

    //@Headers({"BOOKING_REQ_FROM: CONSUMER_APP"})
    @POST("consumer/waitlist/communicate/{waitlistid}")
    Call<ResponseBody> WaitListMessage(@Path("waitlistid") String otp, @Query("account") String account, @Body RequestBody jsonObj);

    //@Headers({"BOOKING_REQ_FROM: CONSUMER_APP"})
    @POST("consumer/appointment/communicate/{waitlistid}")
    Call<ResponseBody> AppointmentMessage(@Path("waitlistid") String otp, @Query("account") String account, @Body RequestBody jsonObj);

    //@Headers({"BOOKING_REQ_FROM: CONSUMER_APP"})
    @POST("consumer/waitlist/communicate/{waitlistid}")
    Call<ResponseBody> WaitListMessageWithAttachment(@Path("waitlistid") String waitlistId, @Query("account") String account, @Body RequestBody fBody);

    //@Headers({"BOOKING_REQ_FROM: CONSUMER_APP"})
    @POST("consumer/orders/communicate/{uuid}")
    Call<ResponseBody> orderMessage(@Path("uuid") String uuid, @Query("account") String account, @Body RequestBody jsonObj);

    //@Headers({"BOOKING_REQ_FROM: CONSUMER_APP"})
    @POST("consumer/donation/communicate/{uuid}")
    Call<ResponseBody> donationMessage(@Path("uuid") String uuid, @Query("account") String account, @Body RequestBody jsonObj);

    //@Headers({"User-Agent: android", "BOOKING_REQ_FROM: CONSUMER_APP"})
    @POST("consumer/payment")
    Call<CheckSumModel> generateHash(@Body RequestBody jsonObj);

    //@Headers({"User-Agent: android", "BOOKING_REQ_FROM: CONSUMER_APP"})
    @POST("consumer/payment")
    Call<PaytmChecksum> generateHashPaytm(@Body RequestBody jsonObj);

    //@Headers({"User-Agent: android", "BOOKING_REQ_FROM: CONSUMER_APP"})
    @POST("consumer/payment/wallet")
    Call<WalletCheckSumModel> generateHashWallet(@Body RequestBody jsonObj);

    //@Headers({"User-Agent: android", "BOOKING_REQ_FROM: CONSUMER_APP"})
    @POST("consumer/payment/wallet")
    Call<WalletCheckSumModel> generateHash2(@Body RequestBody jsonObj);

    //@Headers({"User-Agent: android", "BOOKING_REQ_FROM: CONSUMER_APP"})
    @POST("consumer/payment/wallet")
    Call<WalletPaytmChecksum> generateHashPaytm2(@Body RequestBody jsonObj);

    //@Headers({"User-Agent: android", "BOOKING_REQ_FROM: CONSUMER_APP"})
    @POST("consumer/payment/status")
    Call<String> verifyRazorpayPayment(@QueryMap(encoded = true) Map<String, String> query);

    //@Headers({"BOOKING_REQ_FROM: CONSUMER_APP"})
    @POST("consumer/waitlist")
    Call<ResponseBody> Checkin(@Query("account") String account, @Body RequestBody jsonObj);

    @PUT("consumer/{channel}/advancePayment")
    Call<AdvancePaymentDetails> getAdvancePaymentDetails(@Path("channel") String uuid, @Query("account") String account, @Body RequestBody jsonObj);

    //    @Headers({"BOOKING_REQ_FROM: CONSUMER_APP"})
    @PUT("consumer/orders/amount")
    Call<AdvancePaymentDetailsOrder> getOrderAdvancePaymentDetails(@Query("account") String account, @Body RequestBody jsonObj);

    //    @Headers({"BOOKING_REQ_FROM: CONSUMER_APP"})
    @GET("consumer/wallet/redeem/remaining/amt")
    Call<String> getPrePayRemainingAmnt(@Query("useJcash") boolean useJcash, @Query("useJcredit") boolean useJcredit, @Query("advancePayAmount") String advancePayAmount);

    //    @Headers({"BOOKING_REQ_FROM: CONSUMER_APP"})
    @POST("consumer/appointment")
    Call<ResponseBody> Appointment(@Query("account") String account, @Body RequestBody jsonObj);

    //    @Headers({"BOOKING_REQ_FROM: CONSUMER_APP"})
    @GET("consumer/bill/{ynwuuid}")
    Call<BillModel> getBill(@Path("ynwuuid") String uuid, @Query("account") String account);

    //    @Headers({"BOOKING_REQ_FROM: CONSUMER_APP"})
    @POST("consumer/jaldee/coupons/{coupon}/{ynwuuid}")
    Call<BillModel> getBillCoupon(@Path("coupon") String coupon, @Path("ynwuuid") String uuid, @Query("account") String account);

    //    @Headers({"BOOKING_REQ_FROM: CONSUMER_APP"})
    @DELETE("consumer/waitlist/{ynwuuid}")
    Call<ResponseBody> deleteActiveCheckIn(@Path("ynwuuid") String uuid, @Query("account") String account);

    //    @Headers({"BOOKING_REQ_FROM: CONSUMER_APP"})
    @PUT("consumer/appointment/cancel/{ynwuuid}")
    Call<ResponseBody> deleteAppointment(@Path("ynwuuid") String uuid, @Query("account") String account);

    //    @Headers({"BOOKING_REQ_FROM: CONSUMER_APP"})
    @GET("consumer/waitlist/{ynwuuid}")
    Call<List<ResponseBody>> waitlist(@Path("ynwuuid") String uuid, @Query("account") String account);

    /*@POST("PayUMoneyHash.php")
    Call<ResponseBody> generateHashTest(@Body RequestBody jsonObj);*/
//    @Headers({"BOOKING_REQ_FROM: CONSUMER_APP"})
    @POST("hashgenerator")
    Call<TestModel> generateHashTest();

    //    @Headers({"BOOKING_REQ_FROM: CONSUMER_APP"})
    @GET(" ynwConf/settings/{sector}/{subsector}")
    Call<SectorCheckin> getSector(@Path("sector") String sector, @Path("subsector") String subsector);

    //    @Headers({"BOOKING_REQ_FROM: CONSUMER_APP"})
    @PUT("consumer/providers/revealPhoneNo/{providerID}/{revelphone}")
    Call<ResponseBody> RevealPhoneNo(@Path("providerID") int providerID, @Path("revelphone") boolean revealphone);

    //    @Headers({"BOOKING_REQ_FROM: CONSUMER_APP"})
    @DELETE("consumer/providers/{providerID}")
    Call<ResponseBody> DeleteFavourite(@Path("providerID") int id);

    //    @Headers({"BOOKING_REQ_FROM: CONSUMER_APP"})
    @POST("consumer/providers/{providerID}")
    Call<ResponseBody> AddFavourite(@Path("providerID") int id);

    //    @Headers({"BOOKING_REQ_FROM: CONSUMER_APP"})
    @GET("consumer/waitlist/rating")
    Call<ArrayList<RatingResponse>> getRating(@QueryMap(encoded = true) Map<String, String> query);

    //    @Headers({"BOOKING_REQ_FROM: CONSUMER_APP"})
    @GET("consumer/appointment/rating")
    Call<ArrayList<RatingResponse>> getRatingApp(@QueryMap(encoded = true) Map<String, String> query);

    //    @Headers({"BOOKING_REQ_FROM: CONSUMER_APP"})
    @PUT("consumer/waitlist/rating")
    Call<ResponseBody> PutRating(@Query("account") String account, @Body RequestBody jsonObj);

    //    @Headers({"BOOKING_REQ_FROM: CONSUMER_APP"})
    @PUT("consumer/appointment/rating")
    Call<ResponseBody> PutRatingApp(@Query("account") String account, @Body RequestBody jsonObj);

    //    @Headers({"BOOKING_REQ_FROM: CONSUMER_APP"})
    @POST("consumer/waitlist/rating")
    Call<ResponseBody> PostRating(@Query("account") String account, @Body RequestBody jsonObj);

    //    @Headers({"BOOKING_REQ_FROM: CONSUMER_APP"})
    @POST("consumer/appointment/rating")
    Call<ResponseBody> PostRatingApp(@Query("account") String account, @Body RequestBody jsonObj);

    //    @Headers({"BOOKING_REQ_FROM: CONSUMER_APP"})
    @GET("consumer/orders/rating")
    Call<ArrayList<RatingResponse>> getOrderRating(@QueryMap(encoded = true) Map<String, String> query);

    //    @Headers({"BOOKING_REQ_FROM: CONSUMER_APP"})
    @POST("consumer/orders/rating")
    Call<ResponseBody> putOrderRating(@Query("account") String account, @Body RequestBody jsonObj);

    //    @Headers({"BOOKING_REQ_FROM: CONSUMER_APP"})
    @PUT("consumer/orders/rating")
    Call<ResponseBody> updateOrderRating(@Query("account") String account, @Body RequestBody jsonObj);

    //    @Headers({"BOOKING_REQ_FROM: CONSUMER_APP"})
    @GET("ynwConf/refinedFilters")
    Call<RefinedFilters> getFilters();

    //    @Headers({"BOOKING_REQ_FROM: CONSUMER_APP"})
    @FormUrlEncoded
    @POST("checksum")
    Call<ArrayList<PaytmChecksum>> getPaytmCheckSum(@Field("TXN_AMOUNT") String txnAmount);

    //    @Headers({"BOOKING_REQ_FROM: CONSUMER_APP"})
    @FormUrlEncoded
    @POST("hashgenerator")
    Call<CheckSumModelTest> getPayUCheckSum(@Field("TXN_AMOUNT") String txnAmount);

    //    @Headers({"BOOKING_REQ_FROM: CONSUMER_APP"})
    @GET("{consumerID}/virtualFields.json")
    Call<SearchVirtualFields> getVirtualFields(@Path("consumerID") int consumerid, @Query("modifiedDate") String mDate);

    //    @Headers({"BOOKING_REQ_FROM: CONSUMER_APP"})
    @GET("{consumerID}/{userId}/providerVirtualFields.json")
    Call<SearchVirtualFields> getProviderVirtualFields(@Path("consumerID") int consumerid, @Path("userId") int userId, @Query("modifiedDate") String mDate);

    //    @Headers({"BOOKING_REQ_FROM: CONSUMER_APP"})
    @GET("{consumerID}/coupon.json")
    Call<ArrayList<CoupnResponse>> getCoupanList(@Path("consumerID") int consumerid, @Query("modifiedDate") String mDate);

    //    @Headers({"BOOKING_REQ_FROM: CONSUMER_APP"})
    @GET("{consumerID}/providerCoupon.json")
    Call<ArrayList<ProviderCouponResponse>> getProviderCoupanList(@Path("consumerID") int consumerid, @Query("modifiedDate") String mDate);

    //    @Headers({"BOOKING_REQ_FROM: CONSUMER_APP"})
    @GET("{consumerID}/jaldeediscount.json")
    Call<JdnResponse> getJdnList(@Path("consumerID") int consumerid, @Query("modifiedDate") String mDate);

    //    @Headers({"BOOKING_REQ_FROM: CONSUMER_APP"})
    @PUT("consumer/updatePushToken")
    Call<ResponseBody> updatePushToken(@Body RequestBody jsonObj);

    //    @Headers({"BOOKING_REQ_FROM: CONSUMER_APP"})
    @GET("ynwConf/refinedFilters/{subdomain}")
    Call<RefinedFilters> getMoreFilters(@Path("subdomain") String subdomain);

    //    @Headers({"BOOKING_REQ_FROM: CONSUMER_APP"})
    @GET("ynwConf/refinedFilters/{domain}/{subdomain}")
    Call<RefinedFilters> getSubDomainMoreFilters(@Path("subdomain") String subdomain, @Path("domain") String domain);

    //    @Headers({"BOOKING_REQ_FROM: CONSUMER_APP"})
    @GET("consumer/waitlist/providerByDepartmentId/{departmentId}")
    Call<ArrayList<SearchUsers>> getUsers(@Path("departmentId") int departmentId, @Query("account") int account);

    //    @Headers({"BOOKING_REQ_FROM: CONSUMER_APP"})
    @GET("consumer/waitlist/{uuid}/meetingDetails/{mode}")
    Call<TeleServiceCheckIn> getMeetingDetails(@Path("uuid") String uuid, @Path("mode") String mode, @Query("account") int account);

    //    @Headers({"BOOKING_REQ_FROM: CONSUMER_APP"})
    @GET("consumer/appointment/{uuid}/meetingDetails/{mode}")
    Call<TeleServiceCheckIn> getMeetingDetailsAppointment(@Path("uuid") String uuid, @Path("mode") String mode, @Query("account") int account);

    //    @Headers({"BOOKING_REQ_FROM: CONSUMER_APP"})
    @GET("consumer/appointment/schedule/date/{date}/location/{location}/service/{service}")
    Call<ArrayList<SlotsData>> getSlotsOnDate(@Path("date") String date, @Path("location") int location, @Path("service") int service, @Query("account") int accountId);

    //    @Headers({"BOOKING_REQ_FROM: CONSUMER_APP"})
    @PUT("consumer/appointment/reschedule")
    Call<ResponseBody> reScheduleAppointment(@Query("account") int id, @Body RequestBody jsonObj);

    //    @Headers({"BOOKING_REQ_FROM: CONSUMER_APP"})
    @PUT("consumer/waitlist/reschedule")
    Call<ResponseBody> reScheduleCheckin(@Query("account") int id, @Body RequestBody jsonObj);

    //RxJava calls

    //    @Headers({"BOOKING_REQ_FROM: CONSUMER_APP"})
    @GET("{uniqueId}/services.json")
    Observable<ArrayList<SearchDepartmentServices>> getDeptCheckInServices(@Path("uniqueId") int uniqueId, @Query("modifiedDate") String mDate);

    //    @Headers({"BOOKING_REQ_FROM: CONSUMER_APP"})
    @GET("consumer/waitlist/services/{id}")
    Observable<ArrayList<SearchService>> getCheckInServices(@Path("id") int id);

    //    @Headers({"BOOKING_REQ_FROM: CONSUMER_APP"})
    @GET("{consumerID}/apptServices.json")
    Observable<ArrayList<SearchAppointmentDepartmentServices>> getDeptAppointServices(@Path("consumerID") int consumerid, @Query("modifiedDate") String mDate);

    //    @Headers({"BOOKING_REQ_FROM: CONSUMER_APP"})
    @GET("consumer/appointment/service/{id}")
    Observable<ArrayList<SearchAppoinment>> getAppointmentServices(@Path("id") int id);

    //    @Headers({"BOOKING_REQ_FROM: CONSUMER_APP"})
    @GET("{uniqueId}/donationServices.json")
    Observable<ArrayList<SearchDonation>> getDonationServices(@Path("uniqueId") int consumerid, @Query("modifiedDate") String mDate);

    //    @Headers({"BOOKING_REQ_FROM: CONSUMER_APP"})
    @GET("{uniqueId}/departmentProviders.json")
    Observable<ArrayList<SearchDepartmentServices>> getDepartmentProviders(@Path("uniqueId") int uniqueId, @Query("modifiedDate") String mDate);

    // to get only users when there are no departments
//    @Headers({"BOOKING_REQ_FROM: CONSUMER_APP"})
    @GET("{uniqueId}/departmentProviders.json")
    Observable<ArrayList<ProviderUserModel>> getProviders(@Path("uniqueId") int uniqueId, @Query("modifiedDate") String mDate);

    //    @Headers({"BOOKING_REQ_FROM: CONSUMER_APP"})
    @GET("provider/appointment/schedule/nextAvailableSchedule/{id}")
    Observable<ArrayList<ScheduleList>> getAppointmentSchedule(@Path("id") String id);

    //    @Headers({"BOOKING_REQ_FROM: CONSUMER_APP"})
    @GET("provider/waitlist/queues/waitingTime/{id}")
    Observable<ArrayList<QueueList>> getCheckInsSchedule(@Path("id") String id);

    //    @Headers({"BOOKING_REQ_FROM: CONSUMER_APP"})
    @GET("provider/waitlist/queues/providerWaitingTime/{id}")
    Observable<ArrayList<QueueList>> getProviderCheckInSchedule(@Path("id") String id);

    //    @Headers({"BOOKING_REQ_FROM: CONSUMER_APP"})
    @GET("consumer/appointment")
    Observable<ArrayList<ActiveAppointment>> getAppointments(@QueryMap(encoded = true) Map<String, String> query);

    //    @Headers({"BOOKING_REQ_FROM: CONSUMER_APP"})
    @GET("consumer/waitlist")
    Observable<ArrayList<ActiveCheckIn>> getCheckIns(@QueryMap(encoded = true) Map<String, String> query);

    //    @Headers({"BOOKING_REQ_FROM: CONSUMER_APP"})
    @GET("consumer/waitlist/history")
    Observable<ArrayList<ActiveCheckIn>> getHistoryCheckIns(@QueryMap(encoded = true) Map<String, String> query);

    //    @Headers({"BOOKING_REQ_FROM: CONSUMER_APP"})
    @GET("consumer/appointment/history")
    Observable<ArrayList<ActiveAppointment>> getHistoryAppointments(@QueryMap(encoded = true) Map<String, String> query);

    //    @Headers({"BOOKING_REQ_FROM: CONSUMER_APP"})
    @GET("consumer/orders/catalogs/{accountId}")
    Observable<ArrayList<Catalog>> getCatalog(@Path("accountId") int id);

    //    @Headers({"BOOKING_REQ_FROM: CONSUMER_APP"})
    @GET("consumer/orders/catalogs/{accountId}")
    Call<ArrayList<Catalog>> getListOfCatalogs(@Path("accountId") int id);

    //    @Headers({"BOOKING_REQ_FROM: CONSUMER_APP"})
    @PUT("consumer/deliveryAddress")
    Call<ResponseBody> getDeliveryAddress(@Body ArrayList<Address> addressList);

    //    @Headers({"BOOKING_REQ_FROM: CONSUMER_APP"})
    @GET("consumer/deliveryAddress")
    Call<ArrayList<Address>> getDeliveryAddress();

    //    @Headers({"BOOKING_REQ_FROM: CONSUMER_APP"})
    @GET("consumer/orders/settings")
    Call<OrderResponse> getOrderEnabledStatus(@Query("account") int accountId);

    //    @Headers({"BOOKING_REQ_FROM: CONSUMER_APP"})
    @GET("consumer/orders/catalogs/pickUp/dates/{catalogId}")
    Call<ArrayList<Schedule>> getPickUpSchedule(@Path("catalogId") int catalogId, @Query("account") int accountId);

    //    @Headers({"BOOKING_REQ_FROM: CONSUMER_APP"})
    @GET("consumer/orders/catalogs/delivery/dates/{catalogId}")
    Call<ArrayList<Schedule>> getHomeDeliverySchedule(@Path("catalogId") int catalogId, @Query("account") int accountId);

    //    @Headers({"BOOKING_REQ_FROM: CONSUMER_APP"})
    @POST("consumer/orders")
    Call<ResponseBody> order(@Query("account") int account, @Body RequestBody jsonObj);

    //    @Headers({"BOOKING_REQ_FROM: CONSUMER_APP"})
    @GET("consumer/orders")
    Call<ArrayList<ActiveOrders>> getOrders(@QueryMap(encoded = true) Map<String, String> query);

    //    @Headers({"BOOKING_REQ_FROM: CONSUMER_APP"})
    @GET("consumer/orders/future")
    Call<ArrayList<ActiveOrders>> getOrdersFuture();

    //    @Headers({"BOOKING_REQ_FROM: CONSUMER_APP"})
    @GET("consumer/orders/history")
    Call<ArrayList<ActiveOrders>> getOrdersHistory();

    //    @Headers({"BOOKING_REQ_FROM: CONSUMER_APP"})
    @GET("consumer/orders/{uuid}")
    Call<ActiveOrders> getOrderDetails(@Path("uuid") String uuid, @Query("account") int account);

    //    @Headers({"BOOKING_REQ_FROM: CONSUMER_APP"})
    @GET("consumer/orders/settings/store/contact/info/{id}")
    Call<StoreDetails> getStoreDetails(@Path("id") int id);

    //    @Headers({"BOOKING_REQ_FROM: CONSUMER_APP"})
    @PUT("consumer/orders/{ynwuuid}")
    Call<ResponseBody> cancelOrder(@Path("ynwuuid") String uid, @Query("account") int accountId);

    //    @Headers({"BOOKING_REQ_FROM: CONSUMER_APP"})
    @POST("consumer/orders")
    Call<ResponseBody> orderList(@Query("account") int account, @Body RequestBody jsonObj);

    //    @Headers({"BOOKING_REQ_FROM: CONSUMER_APP"})
    @POST("consumer/waitlist/{uuid}/attachment")
    Call<ResponseBody> waitlistSendAttachments(@Path("uuid") String uid, @Query("account") int account, @Body RequestBody jsonObj);

    //    @Headers({"BOOKING_REQ_FROM: CONSUMER_APP"})
    @POST("consumer/appointment/{uuid}/attachment")
    Call<ResponseBody> appointmentSendAttachments(@Path("uuid") String uid, @Query("account") int account, @Body RequestBody jsonObj);

    //    @Headers({"BOOKING_REQ_FROM: CONSUMER_APP"})
    @GET("consumer/waitlist/attachment/{uuid}")
    Call<ArrayList<ShoppingList>> getWaitlistAttachments(@Path("uuid") String uid, @Query("account") int account);

    //    @Headers({"BOOKING_REQ_FROM: CONSUMER_APP"})
    @GET("consumer/appointment/attachment/{uuid}")
    Call<ArrayList<ShoppingList>> getAppointmentAttachments(@Path("uuid") String uid, @Query("account") int account);

    //    @Headers({"BOOKING_REQ_FROM: CONSUMER_APP"})
    @POST("provider/claim/{accId}")
    Call<ProfileModel> usrProfile(@Path("accId") int account);

    //    @Headers({"BOOKING_REQ_FROM: CONSUMER_APP"})
    @GET("provider/account/settings/config/{uniqueId}/settings,terminologies,coupon,providerCoupon,location,businessProfile,virtualFields,services,apptServices,donationServices,departmentProviders,gallery")
    Call<Provider> getProviderDetails(@Path("uniqueId") int uniqueId);

    //    @Headers({"BOOKING_REQ_FROM: CONSUMER_APP"})
    @GET("provider/account/settings/config/{uniqueId}/{userId}/providerBusinessProfile,providerVirtualFields")
    Call<UserResponse> getUserDetails(@Path("uniqueId") int uniqueId, @Path("userId") int userId);

    //    @Headers({"BOOKING_REQ_FROM: CONSUMER_APP"})
    @GET("provider/account/settings/config/{uniqueId}/coupon,providerCoupon")
    Call<Provider> getCoupons(@Path("uniqueId") int uniqueId);

    //    @Headers({"BOOKING_REQ_FROM: CONSUMER_APP"})
    @GET("provider/account/settings/config/{uniqueId}/jaldeediscount")
    Call<Provider> getJdn(@Path("uniqueId") int uniqueId);

    //    @Headers({"BOOKING_REQ_FROM: CONSUMER_APP"})
    @GET("provider/account/settings/config/{uniqueId}/terminologies")
    Call<Provider> getTerminologies(@Path("uniqueId") int uniqueId);

    //    @Headers({"BOOKING_REQ_FROM: CONSUMER_APP"})
    @GET("provider/account/settings/config/{uniqueId}/businessProfile")
    Call<Provider> getProvider(@Path("uniqueId") int uniqueId);

    @GET("provider/imagePropries/logo/{queueId}")
    Call<LinkedHashMap<String, ArrayList<ProfilePicture>>> getLogo(@Path("queueId") String id);

    /****************   ServiceOptions URLs  ***************/
    @GET("consumer/questionnaire/serviceoptions/{serviceId}/{consumerId}")
    Call<Questionnaire> getServiceOptionQnr(@Path("serviceId") int serviceId, @Path("consumerId") int consumerId, @Query("account") int account);

    @POST("consumer/appointment/serviceoption/{uid}?")
    Call<SubmitQuestionnaire> submitAppointmentServiceOptionQnr(@Path("uid") String uid, @Body RequestBody jsonObj, @Query("account") int accountId);

    @POST("consumer/waitlist/serviceoption/{uid}?")
    Call<SubmitQuestionnaire> submitWaitListServiceOptionQnr(@Path("uid") String uid, @Body RequestBody jsonObj, @Query("account") int accountId);

    @PUT("consumer/appointment/serviceoption/upload/status/{uid}?")
    Call<ResponseBody> checkAppointmentServicOptionUploadStatus(@Path("uid") String id, @Query("account") int accountId, @Body RequestBody jsonObj);

    @PUT("consumer/waitlist/serviceoption/upload/status/{uid}?")
    Call<ResponseBody> checkWaitlistServicOptionUploadStatus(@Path("uid") String id, @Query("account") int accountId, @Body RequestBody jsonObj);
    /****************   ServiceOptios URLs  ***************/

    /****************   Order ServiceOptions URLs  ***************/
    @GET("consumer/questionnaire/serviceoptions/order/item/{itemId}")
    Call<Questionnaire> getOrderItemServiceOptionQnr(@Path("itemId") int itemId, @Query("account") int account);

    @POST("consumer/orders/item/serviceoption/{itemId}/{uid}")
    Call<SubmitQuestionnaire> submitOrderItemServiceOptionQnr(@Path("itemId") int itemId, @Path("uid") String uid, @Body RequestBody jsonObj, @Query("account") int accountId);

    @POST("consumer/orders/item/serviceoption/resubmit/{uid}")
    Call<SubmitQuestionnaire> resubmitOrderItemServiceOptionQnr(@Path("uid") String uid, @Body RequestBody jsonObj, @Query("account") int accountId);

    @PUT("consumer/orders/item/serviceoption/upload/status/{uid}?")
    Call<ResponseBody> checkOrderItemServicOptionUploadStatus(@Path("uid") String id, @Query("account") int accountId, @Body RequestBody jsonObj);
    /****************   Order ServiceOptions URLs  ***************/



    /****************   Questionnaire URLs  ***************/
    @GET("consumer/questionnaire/service/{serviceId}/consumer/{consumerId}")
    Call<Questionnaire> getQuestions(@Path("serviceId") int serviceId, @Path("consumerId") int consumerId, @Query("account") int account);

    @GET("consumer/questionnaire/donation/{serviceId}")
    Call<Questionnaire> getDonationQuestions(@Path("serviceId") int serviceId, @Query("account") int account);

    @GET("consumer/questionnaire/order/{serviceId}")
    Call<Questionnaire> getOrdersQuestions(@Path("serviceId") int serviceId, @Query("account") int account);

    @POST("consumer/appointment/questionnaire/{uid}?")
    Call<SubmitQuestionnaire> submitAppointmentQuestionnaire(@Path("uid") String uid, @Body RequestBody jsonObj, @Query("account") int accountId);

    @POST("consumer/waitlist/questionnaire/{uid}?")
    Call<SubmitQuestionnaire> submitWaitListQuestionnaire(@Path("uid") String uid, @Body RequestBody jsonObj, @Query("account") int accountId);

    @POST("consumer/donation/questionnaire/submit/{uid}?")
    Call<ResponseBody> submitDonationQuestionnaire(@Path("uid") String uid, @Body RequestBody jsonObj, @Query("account") int accountId);

    @POST("consumer/orders/questionnaire/{uid}?")
    Call<SubmitQuestionnaire> submitOrderQuestionnaire(@Path("uid") String uid, @Body RequestBody jsonObj, @Query("account") int accountId);

    @POST("consumer/appointment/questionnaire/resubmit/{uid}?")
    Call<SubmitQuestionnaire> reSubmitAppQuestionnaire(@Path("uid") String uid, @Body RequestBody jsonObj, @Query("account") int accountId);

    @POST("consumer/waitlist/questionnaire/resubmit/{uid}?")
    Call<SubmitQuestionnaire> reSubmitWlQuestionnaire(@Path("uid") String uid, @Body RequestBody jsonObj, @Query("account") int accountId);

    @POST("consumer/orders/questionnaire/resubmit/{uid}?")
    Call<SubmitQuestionnaire> reSubmitOrderQuestionnaire(@Path("uid") String uid, @Body RequestBody jsonObj, @Query("account") int accountId);

    @GET("consumer/appointment/questionnaire/{uid}?")
    Call<ArrayList<Questionnaire>> getAppointmentAfterQuestionnaire(@Path("uid") String uid, @Query("account") int accountId);

    @GET("consumer/waitlist/questionnaire/{uid}?")
    Call<ArrayList<Questionnaire>> getWaitlistAfterQuestionnaire(@Path("uid") String uid, @Query("account") int accountId);

    @GET("consumer/orders/questionnaire/{uid}?")
    Call<ArrayList<Questionnaire>> getOrderAfterQuestionnaire(@Path("uid") String uid, @Query("account") int accountId);

    @PUT
    Observable<Response<Void>> uploadPreSignedS3File(@Url String url, @Body RequestBody image);

    //    @Headers({"BOOKING_REQ_FROM: CONSUMER_APP"})
    @PUT("consumer/appointment/questionnaire/upload/status/{uid}?")
    Call<ResponseBody> checkAppointmentUploadStatus(@Path("uid") String id, @Query("account") int accountId, @Body RequestBody jsonObj);

    //    @Headers({"BOOKING_REQ_FROM: CONSUMER_APP"})
    @PUT("consumer/waitlist/questionnaire/upload/status/{uid}?")
    Call<ResponseBody> checkWaitlistUploadStatus(@Path("uid") String id, @Query("account") int accountId, @Body RequestBody jsonObj);

    @PUT("consumer/orders/questionnaire/upload/status/{uid}?")
    Call<ResponseBody> checkOrderUploadStatus(@Path("uid") String id, @Query("account") int accountId, @Body RequestBody jsonObj);
    /****************   Questionnaire URLs  ***************/

    //    @Headers({"BOOKING_REQ_FROM: CONSUMER_APP"})
    @GET("consumer/wallet/redeem/eligible/amt")
    Call<WalletEligibleJCash> getWalletEligibleJCash();

    //    @Headers({"BOOKING_REQ_FROM: CONSUMER_APP"})
    @GET("consumer/wallet/cash/info")
    Call<JCashInfo> getJCashInfo();

    //    @Headers({"BOOKING_REQ_FROM: CONSUMER_APP"})
    @GET("consumer/wallet/cash/available")
    Call<ArrayList<JCash>> getJCashAvailable();

    //    @Headers({"BOOKING_REQ_FROM: CONSUMER_APP"})
    @GET("consumer/wallet/cash/expired")
    Call<ArrayList<JCashExpired>> getJCashExpired();

    //    @Headers({"BOOKING_REQ_FROM: CONSUMER_APP"})
    @GET("consumer/wallet/cash/spent")
    Call<ArrayList<JCashSpentDetails>> getAllJCashSpentDetails();

    //    @Headers({"BOOKING_REQ_FROM: CONSUMER_APP"})
    @GET("consumer/wallet/cash/{cashId}/txn/log")
    Call<ArrayList<JCashSpentDetails>> getJCashSpentDetails(@Path("cashId") int cashId);

    //    @Headers({"BOOKING_REQ_FROM: CONSUMER_APP"})
    @GET("chatbot/telegram/consumer/chatId/{countryCode}/{phoneNumber}")
    Call<ResponseBody> getTelegramChatId(@Path("countryCode") String countryCode, @Path("phoneNumber") String mobile);

    //    @Headers({"BOOKING_REQ_FROM: CONSUMER_APP"})
    @GET("consumer/telegram/settings")
    Call<TelegramNotificationSettingsResponse> getTelegramSettings();

    //    @Headers({"BOOKING_REQ_FROM: CONSUMER_APP"})
    @PUT("consumer/telegram/settings/{enableOrdisable}")
    Call<ResponseBody> putTelegramNotificationsettings(@Path("enableOrdisable") String enableOrdisable);

    //    @Headers({"BOOKING_REQ_FROM: CONSUMER_APP"})
    @PUT("consumer/communications/readMessages/{providerId}/{messageIds}?")
    Call<ResponseBody> readMessages(@Path("providerId") String providerId, @Path("messageIds") String messageIds, @Query("account") String accountId);

    //    @Headers({"BOOKING_REQ_FROM: CONSUMER_APP"})
    @GET("consumer/communications/unreadCount")
    Call<ResponseBody> getUnreadMessagesCount();

    //    @Headers({"BOOKING_REQ_FROM: CONSUMER_APP"})
    @GET("consumer/communications/filterComm")
    Call<ArrayList<NewInbox>> getChats();

    //    @Headers({"BOOKING_REQ_FROM: CONSUMER_APP"})
    @GET("consumer/donation/{uid}")
    Call<ActiveDonation> getDonationDetails(@Path("uid") String uuid);

    //    @Headers({"BOOKING_REQ_FROM: CONSUMER_APP"})
    @POST("provider/payment/razorpay/update?")
    Call<ResponseBody> checkRazorpayPaymentStatus(@Body RequestBody jsonObj, @Query("account") int accountId);

    //    @Headers({"BOOKING_REQ_FROM: CONSUMER_APP"})
    @POST("provider/payment/paytm/update?")
    Call<ResponseBody> checkPaytmPaymentStatus(@Body RequestBody jsonObj, @Query("account") int accountId);

    //    @Headers({"BOOKING_REQ_FROM: CONSUMER_APP"})
    @GET("consumer/appointment/schedule/{serviceId}/pricelist")
    Call<ArrayList<PriceList>> getPriceListOfService(@Path("serviceId") int serviceId);

    //    @Headers({"BOOKING_REQ_FROM: CONSUMER_APP"})
    @GET(" consumer/payment/modes/{accountid}/{serviceId}/{paymentPurpose}")
    Call<ArrayList<PaymentModel>> getPaymentModes(@Path("accountid") String accountid, @Path("serviceId") int serviceId, @Path("paymentPurpose") String paymentPurpose);

    //    @Headers({"BOOKING_REQ_FROM: CONSUMER_APP"})
    @GET(" consumer/payment/modes/{accountid}/{paymentPurpose}")
    Call<ArrayList<PaymentModel>> getPaymentMod(@Path("accountid") String accountid, @Path("paymentPurpose") String paymentPurpose);
}