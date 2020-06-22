package com.jaldeeinc.jaldee.Fragment;


import android.Manifest;
import android.Manifest.permission;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.RequiresApi;
import android.support.design.widget.BottomSheetDialog;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStates;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.gson.Gson;
import com.jaldeeinc.jaldee.R;
import com.jaldeeinc.jaldee.activities.BillActivity;
import com.jaldeeinc.jaldee.activities.Constants;
import com.jaldeeinc.jaldee.activities.Home;
import com.jaldeeinc.jaldee.activities.PaymentActivity;
import com.jaldeeinc.jaldee.adapter.CouponAdapter;
import com.jaldeeinc.jaldee.adapter.DetailFileAdapter;
import com.jaldeeinc.jaldee.adapter.ExpandableListAdapter;
import com.jaldeeinc.jaldee.adapter.ExpandableListAdapterAppointment;
import com.jaldeeinc.jaldee.adapter.MyPaymentAdapter;
import com.jaldeeinc.jaldee.callback.ActiveAdapterOnCallback;
import com.jaldeeinc.jaldee.callback.HistoryAdapterCallback;
import com.jaldeeinc.jaldee.common.Config;
import com.jaldeeinc.jaldee.connection.ApiClient;
import com.jaldeeinc.jaldee.connection.ApiInterface;
import com.jaldeeinc.jaldee.custom.CustomTypefaceSpan;
import com.jaldeeinc.jaldee.database.DatabaseHandler;
import com.jaldeeinc.jaldee.response.ActiveAppointment;
import com.jaldeeinc.jaldee.response.ActiveCheckIn;
import com.jaldeeinc.jaldee.response.CoupnResponse;
import com.jaldeeinc.jaldee.response.FavouriteModel;
import com.jaldeeinc.jaldee.response.MyPayments;
import com.jaldeeinc.jaldee.response.RatingResponse;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.DexterError;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.PermissionRequestErrorListener;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.payumoney.sdkui.ui.utils.ToastUtils;

import org.apache.commons.io.IOUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.support.v4.content.ContextCompat.getSystemService;


/**
 * A simple {@link Fragment} subclass.
 */
public class Tab2Fragment extends RootFragment  {

    List<MyPayments> paymentsList;
    ListView payments_listview;
    String uniqueid;
    private MyPaymentAdapter mAdapter;

    Context mContext;


    public Tab2Fragment() {

    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View row = inflater.inflate(R.layout.fragment_tab2, container, false);
        payments_listview = row.findViewById(R.id.payment_inner_list);
        mContext = getActivity();
        ApiPayments();
        return row;
    }


    private void ApiPayments() {

        final ApiInterface apiService = ApiClient.getClient(mContext).create(ApiInterface.class);
        Call<ArrayList<MyPayments>> call = apiService.getMyPayments();

        call.enqueue(new Callback<ArrayList<MyPayments>>() {
            @Override
            public void onResponse(Call<ArrayList<MyPayments>> call, Response<ArrayList<MyPayments>> response) {
                try {
                    Config.logV("URL---------------" + response.raw().request().url().toString().trim());
                    Config.logV("Response--code-------------------------" + response.code());

                    if (response.code() == 200) {

                        paymentsList = response.body();
                        mAdapter = new MyPaymentAdapter(mContext,0, paymentsList);
                        payments_listview.setAdapter(mAdapter);


                        Log.i("paymentsRes",new Gson().toJson(response.body()));


                    } else {
                        if (response.code() != 419) {
                            Toast.makeText(mContext, response.errorBody().string(), Toast.LENGTH_SHORT).show();
                        }
                    }


                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(Call<ArrayList<MyPayments>> call, Throwable t) {
                // Log error here since request failed
                Config.logV("Fail---------------" + t.toString());


            }
        });


    }

}