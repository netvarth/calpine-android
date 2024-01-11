package com.jaldeeinc.customapp;

import io.reactivex.rxjava3.core.Observable;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiInterface {

    @GET("{uniqueId}/account_config.json")
    Observable<ConfigResponseDTO> getConfiguration(@Path("uniqueId") String uniqueId, @Query("modifiedDate") String mDate);

    @GET("default/customapp_config.json")
    Observable<CustomAppConfigDTO> getCustomAppConfiguration(@Query("modifiedDate") String mDate);

    @GET("{uniqueId}/version.json")   // get provider custom app  version
    Call<VersionResponseDTO> getVersionInfo(@Path("uniqueId") String uniqueId, @Query("modifiedDate") String mDate);

    @GET("{uniqueId}/pversion.json")   // get business app version
    Call<VersionResponseDTO> getPVersionInfo(@Path("uniqueId") String uniqueId, @Query("modifiedDate") String mDate);

}
