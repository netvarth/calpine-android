package com.jaldeeinc.jaldee.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.jaldeeinc.jaldee.Fragment.HomeSearchFragment;
import com.jaldeeinc.jaldee.R;
import com.jaldeeinc.jaldee.custom.BorderImageView;
import com.jaldeeinc.jaldee.custom.PicassoTrustAll;

public class TestActivity extends AppCompatActivity {

    HomeSearchFragment myJaldee;

    BorderImageView borderImageView,bi1,bi2,bi3,bi4;
    BorderImageView bi5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.item_card);

        borderImageView = findViewById(R.id.iv_itemImage);
        bi1 = findViewById(R.id.iv_itemImage1);
        bi2 = findViewById(R.id.iv_itemImage2);
        bi3 = findViewById(R.id.iv_itemImage3);
        bi4 = findViewById(R.id.iv_itemImage4);
        bi5 = findViewById(R.id.iv_itemImage5);

        String url = "https://www.thespruceeats.com/thmb/8tdgBGq--fpTdfXayB6WjS6Vdn8=/3435x2576/smart/filters:no_upscale()/roast-chicken-wings-on-cutting-board-644202006-5b170ddafa6bcc00369c17ae.jpg";

        PicassoTrustAll.getInstance(TestActivity.this).load("https://content3.jdmagicbox.com/comp/jaipur/n9/0141px141.x141.151031230321.c8n9/catalogue/lazeez-chicken-food-restaurant-jhotwara-jaipur-restaurants-34wllox.jpg").into(borderImageView);

        PicassoTrustAll.getInstance(TestActivity.this).load("https://c.ndtvimg.com/2020-01/jeinftq8_kadhai-chicken_625x300_22_January_20.jpg").into(bi1);

        PicassoTrustAll.getInstance(TestActivity.this).load("https://ichef.bbci.co.uk/food/ic/food_16x9_832/recipes/easy_spanish_chicken_09987_16x9.jpg").into(bi2);

        PicassoTrustAll.getInstance(TestActivity.this).load("https://i2.wp.com/www.vegrecipesofindia.com/wp-content/uploads/2013/04/veg-salad-recipe-1.jpg").into(bi3);

        PicassoTrustAll.getInstance(TestActivity.this).load("https://www.tarladalal.com/category/Low-Carb-Salads.jpg").into(bi4);

        PicassoTrustAll.getInstance(TestActivity.this).load("https://www.whiskaffair.com/wp-content/uploads/2019/02/Mutton-Biryani-1.jpg").into(bi5);


//        myJaldee = new HomeSearchFragment();
//        getSupportFragmentManager().beginTransaction().replace(R.id.fl_holder, myJaldee).commit();


//        public void createLocationRequest() {
//            if (googleApiClient == null) {
//                googleApiClient = new GoogleApiClient.Builder(HelperActivity.this)
//                        .addApi(LocationServices.API)
//                        .addConnectionCallbacks(this)
//                        .addOnConnectionFailedListener(this).build();
//
//                googleApiClient.connect();
//            }
//
//
//            LocationRequest locationRequest = LocationRequest.create();
//            locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
//            locationRequest.setInterval(10 * 1000);
//            locationRequest.setFastestInterval(3 * 1000);
//            LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
//                    .addLocationRequest(locationRequest)
//                    .addLocationRequest(LocationRequest.create().setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY));
//
//            builder.setAlwaysShow(true); //this is the key ingredient
//
//            Task<LocationSettingsResponse> result =
//                    LocationServices.getSettingsClient(this).checkLocationSettings(builder.build());
//
//            result.addOnCompleteListener(task -> {
//                try {
//                    LocationSettingsResponse response = task.getResult(ApiException.class);
//                    HelperActivity.this.updateRequestLocation();
//                } catch (ApiException exception) {
//                    exception.printStackTrace();
//                    switch (exception.getStatusCode()) {
//                        case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
//                            try {
//                                ResolvableApiException resolvable = (ResolvableApiException) exception;
//                                resolvable.startResolutionForResult(HelperActivity.this, Utility.generateRequestCodes().get("ACCESS_GOOGLE_LOCATION"));
//                            } catch (IntentSender.SendIntentException e) {
//                                e.printStackTrace();
//// Ignore the error.
//                            } catch (ClassCastException e) {
//                                e.printStackTrace();
//                            }
//                            break;
//                        case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
//
//                            break;
//                    }
//                }
//            });
//
//
//        }
//
//        public void updateRequestLocation() {
//            fusedLocationProviderClient = null;
//            fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
//            startLocationUpdates();
//        }
//
//        private void startLocationUpdates() {
//
//            if (locationCallback == null) {
//                locationCallback = new LocationCallback() {
//                    @Override
//                    public void onLocationResult(LocationResult locationResult) {
//                        if (locationResult == null) {
//                            return;
//                        }
//                        for (Location location : locationResult.getLocations()) {
//                            if (location != null) {
////updateUserLocation(location);
//
//                                stopLocationUpdates();
//                                return;
//                            }
//                        }
//                    }
//                };
//            }
//            LocationRequest locationRequest = LocationRequest.create();
//            locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
//            locationRequest.setInterval(10 * 1000);
//            locationRequest.setFastestInterval(2 * 1000);
//            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//                return;
//            }
//            fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, null);
//        }
//
//
//        private void stopLocationUpdates() {
//            if (fusedLocationProviderClient != null)
//                fusedLocationProviderClient.removeLocationUpdates(locationCallback);
//        }
//
//        private LocationCallback locationCallback;

    }
}