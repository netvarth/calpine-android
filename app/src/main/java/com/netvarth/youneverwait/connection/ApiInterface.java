package com.netvarth.youneverwait.connection;


import com.netvarth.youneverwait.activities.CheckinTest;
import com.netvarth.youneverwait.model.BillModel;
import com.netvarth.youneverwait.model.Domain_Spinner;
import com.netvarth.youneverwait.model.FamilyArrayModel;
import com.netvarth.youneverwait.model.FamilyModel;
import com.netvarth.youneverwait.model.SearchModel;
import com.netvarth.youneverwait.model.TestModel;
import com.netvarth.youneverwait.response.ActiveCheckIn;
import com.netvarth.youneverwait.response.CheckInModel;
import com.netvarth.youneverwait.response.CheckSumModel;
import com.netvarth.youneverwait.response.FavouriteModel;
import com.netvarth.youneverwait.response.InboxModel;
import com.netvarth.youneverwait.response.LocationResponse;
import com.netvarth.youneverwait.response.LoginResponse;
import com.netvarth.youneverwait.response.PaymentModel;
import com.netvarth.youneverwait.response.ProfileModel;
import com.netvarth.youneverwait.response.QueueList;
import com.netvarth.youneverwait.response.QueueTimeSlotModel;
import com.netvarth.youneverwait.response.SearchAWsResponse;
import com.netvarth.youneverwait.response.SearchCheckInMessage;
import com.netvarth.youneverwait.response.SearchLocation;
import com.netvarth.youneverwait.response.SearchService;
import com.netvarth.youneverwait.response.SearchSetting;
import com.netvarth.youneverwait.response.SearchTerminology;
import com.netvarth.youneverwait.response.SearchViewDetail;
import com.netvarth.youneverwait.utils.SharedPreference;
import com.payumoney.core.PayUmoneySdkInitializer;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.OPTIONS;
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

    @Headers("Accept: application/json")
    @POST("consumer")
    Call<ResponseBody> getSignUpResponse(@Body RequestBody jsonObj);

    @POST("consumer/{otp}/verify")
    Call<ResponseBody> OtpVerify(@Path("otp") String otp);

    @Headers("Accept: application/json")
    @PUT("consumer/{otp}/activate")
    Call<ResponseBody> SetPassword(@Path("otp") String otp, @Body RequestBody jsonObj);

    @Headers("Accept: application/json")
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
    Call<ArrayList<ActiveCheckIn>> getActiveCheckIn();

    @GET("ynwConf/businessDomains")
    Call<ArrayList<Domain_Spinner>> getAllDomains();

    @GET("ynwConf/searchLabels")
    Call<SearchModel> getAllSearch();

    @GET("ynwConf/searchDomain")
    Call<ResponseBody> getSearchDomain();


    @GET("search")
    Call<SearchAWsResponse> getSearchAWS(@QueryMap(encoded = true) Map<String, String>   query, @QueryMap(encoded = true) Map<String, String> params);


    @GET("{consumerID}/businessProfile.json")
    Call<SearchViewDetail> getSearchViewDetail(@Path("consumerID") int consumerid,@Query("modifiedDate") String mDate);

    @GET("{consumerID}/gallery.json")
    Call<ArrayList<SearchViewDetail>> getSearchGallery(@Path("consumerID") int consumerid,@Query("modifiedDate") String mDate);

    @GET("{consumerID}/location.json")
    Call<ArrayList<SearchLocation>> getSearchViewLoc(@Path("consumerID") int consumerid, @Query("modifiedDate") String mDate);

    @GET("{consumerID}/settings.json")
    Call<SearchSetting> getSearchViewSetting(@Path("consumerID") int consumerid, @Query("modifiedDate") String mDate);

    @GET("{consumerID}/terminologies.json")
    Call<SearchTerminology> getSearchViewTerminology(@Path("consumerID") int consumerid, @Query("modifiedDate") String mDate);



    @GET("consumer/waitlist/services/{id}")
    Call<ArrayList<SearchService>> getSearchService(@Path("id") int id);


    @GET("consumer/waitlist")
    Call<ArrayList<SearchCheckInMessage>> getSearchCheckInMessage(@QueryMap(encoded = true) Map<String, String>   query);


    @GET("provider/waitlist/queues/waitingTime/{id}")
    Call<ArrayList<QueueList>> getSearchID(@Path("id") String id);



    @GET(" provider/waitlist/queues/waitingTime/{queueId}")
    Call<List<QueueList>> getQueueCheckReponse(@Path("queueId") String id);


    @GET("provider/search/suggester/location")
    Call<ArrayList<LocationResponse>> getSearchLocation(@Query("criteria") String criteria);

    @GET("consumer/waitlist/queues/{serviceID}/{sub_serviceid}/{modifiedDate}")
    Call<ArrayList<QueueTimeSlotModel>> getQueueTimeSlot(@Path("serviceID") String serviceID,@Path("sub_serviceid") String sub_serviceid,@Path("modifiedDate") String modifiedDate,@Query("account")String acountid);



    @GET(" consumer/payment/modes/{accountid}")
    Call<ArrayList<PaymentModel>> getPayment(@Path("accountid") String accountid);


    @GET("consumer/waitlist/history")
    Call<ArrayList<CheckInModel>> getCheckInList(@QueryMap(encoded = true) Map<String, String>   query);


    @POST("consumer/communications")
    Call<ResponseBody> PostMessage(@Query("account") String account,@Body RequestBody jsonObj);



    @GET("consumer/communications")
    Call<ArrayList<InboxModel>> getMessage();


    @GET("consumer/providers")
    Call<ArrayList<FavouriteModel>> getFavourites();



    @POST("consumer/waitlist/communicate/{waitlistid}")
    Call<ResponseBody> WaitListMessage(@Path("waitlistid") String otp,@Query("account") String account,@Body RequestBody jsonObj);



    @Headers("User-Agent: android")
    @POST("consumer/payment")
    Call<CheckSumModel> generateHash(@Body RequestBody jsonObj,@Query("accountId") String account);

    @POST("consumer/waitlist")
    Call<ResponseBody> Checkin(@Query("account") String account,@Body RequestBody jsonObj);


    @GET("consumer/bill/{ynwuuid}")
    Call<BillModel> getBill(@Path("ynwuuid") String uuid);

    @DELETE("consumer/waitlist/{ynwuuid}")
    Call<ResponseBody> deleteActiveCheckIn(@Path("ynwuuid") String uuid,@Query("account") String account);


    @GET("{serviceid}/services.json")
    Call<ArrayList<SearchService>> getService(@Path("serviceid") int serviceid, @Query("modifiedDate") String mDate);


    /*@POST("PayUMoneyHash.php")
    Call<ResponseBody> generateHashTest(@Body RequestBody jsonObj);*/
    @POST("hashgenerator")
    Call<TestModel> generateHashTest();
}