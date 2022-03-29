package com.jaldeeinc.jaldee.activities;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.jaldeeinc.jaldee.R;
import com.jaldeeinc.jaldee.adapter.ReleasedQNRAdapter;
import com.jaldeeinc.jaldee.common.Config;
import com.jaldeeinc.jaldee.connection.ApiClient;
import com.jaldeeinc.jaldee.connection.ApiInterface;
import com.jaldeeinc.jaldee.model.Bookings;
import com.jaldeeinc.jaldee.model.RlsdQnr;
import com.jaldeeinc.jaldee.response.ActiveAppointment;
import com.jaldeeinc.jaldee.response.ActiveCheckIn;
import com.jaldeeinc.jaldee.response.ActiveOrders;
import com.jaldeeinc.jaldee.response.Questionnaire;
import com.jaldeeinc.jaldee.response.QuestionnaireResponse;
import com.squareup.okhttp.ResponseBody;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ReleasedQNRActivity extends AppCompatActivity {
    RecyclerView rvRlsdQNR;
    CardView cvBack;
    LinearLayoutManager linearLayoutManager;
    Context mContext;
    ArrayList<Questionnaire> afterQuestionnaire;
    boolean isContainAfterQNR = false;
    String from;
    ActiveAppointment apptInfo;
    ActiveCheckIn checkinInfo;
    ActiveOrders orderInfo;
    int providerAccountId;
    String uid;
    public int serviceId;
    public String uniqueId;
    public String status;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_released_qnr);
        initializations();
        mContext = this;

        linearLayoutManager = new LinearLayoutManager(ReleasedQNRActivity.this);
        Gson gson = new Gson();
        Intent intent = getIntent();
        from = intent.getStringExtra("from");
        if (from.equalsIgnoreCase(Constants.BOOKING_APPOINTMENT)) {
            apptInfo = gson.fromJson(intent.getStringExtra("bookingInfo"), ActiveAppointment.class);
            if (apptInfo != null) {
                serviceId = apptInfo.getService().getId();
                providerAccountId = apptInfo.getProviderAccount().getId();
                uid = apptInfo.getUid();
                uniqueId = String.valueOf(apptInfo.getProviderAccount().getUniqueId());
                if (apptInfo.getApptStatus() != null) {
                    status = apptInfo.getApptStatus();
                }
                createRlsdQNR(apptInfo.getReleasedQnr(), apptInfo.getQuestionnaires());
            }
        } else if (from.equalsIgnoreCase(Constants.BOOKING_CHECKIN)) {
            checkinInfo = gson.fromJson(intent.getStringExtra("bookingInfo"), ActiveCheckIn.class);
            if (checkinInfo != null) {
                serviceId = checkinInfo.getService().getId();
                providerAccountId = checkinInfo.getProviderAccount().getId();
                uid = checkinInfo.getYnwUuid();
                uniqueId = String.valueOf(checkinInfo.getProviderAccount().getUniqueId());
                if (checkinInfo.getWaitlistStatus() != null) {
                    status = checkinInfo.getWaitlistStatus();
                }
                createRlsdQNR(checkinInfo.getReleasedQnr(), checkinInfo.getQuestionnaires());
            }
        } else if (from.equalsIgnoreCase(Constants.ORDERS)) {
            orderInfo = gson.fromJson(intent.getStringExtra("bookingInfo"), ActiveOrders.class);
            if (orderInfo != null) {
                serviceId = orderInfo.getCatalog().getCatLogId();
                providerAccountId = orderInfo.getProviderAccount().getId();
                uid = orderInfo.getUid();
                uniqueId = String.valueOf(orderInfo.getProviderAccount().getUniqueId());
                if (orderInfo.getOrderStatus() != null) {
                    status = orderInfo.getOrderStatus();
                }
                createRlsdQNR(orderInfo.getReleasedQnr(), orderInfo.getQuestionnaires());
            }
        }

        cvBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                finish();
            }
        });
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        if (from.equalsIgnoreCase(Constants.BOOKING_CHECKIN)) {
            getCheckinDetails(uid, providerAccountId);
        } else if (from.equalsIgnoreCase(Constants.BOOKING_APPOINTMENT)) {
            getAppointmentDetails(uid, providerAccountId);
        } else if (from.equalsIgnoreCase(Constants.ORDERS)) {
            getOrderDetails(uid, providerAccountId);
        }
    }

    private void initializations() {

        cvBack = findViewById(R.id.cv_back);
        rvRlsdQNR = findViewById(R.id.rv_rlsd_qnr);
    }

    private void createRlsdQNR(ArrayList<RlsdQnr> rlsdQnrs, ArrayList<QuestionnaireResponse> questionnaireResponses) {
        for (RlsdQnr rlsdQnr : rlsdQnrs) {
            if (rlsdQnr.getStatus().equalsIgnoreCase("released")) {
                isContainAfterQNR = true;
            }
        }
            if (isContainAfterQNR) {
            apiGetAfterQuestionnaire(rlsdQnrs, questionnaireResponses);
        } else {
            for (RlsdQnr rlsdQnr : rlsdQnrs) {
                for (QuestionnaireResponse questionnaireResponse : questionnaireResponses) {
                    if (rlsdQnr.getId() == questionnaireResponse.getQuestionnaireId()) {
                        rlsdQnr.setQnrName(questionnaireResponse.getQuestionnaireName());
                    }
                }
            }
            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(mContext);
            rvRlsdQNR.setLayoutManager(mLayoutManager);
            ReleasedQNRAdapter releasedQNRAdapter = new ReleasedQNRAdapter(mContext, rlsdQnrs, serviceId, providerAccountId, uid, uniqueId, status, from, questionnaireResponses);
            rvRlsdQNR.setAdapter(releasedQNRAdapter);
        }
    }

    private void apiGetAfterQuestionnaire(ArrayList<RlsdQnr> rlsdQnrs, ArrayList<QuestionnaireResponse> aQuestionnaireResponses) {
        final ApiInterface apiService =
                ApiClient.getClient(mContext).create(ApiInterface.class);
        Call<ArrayList<Questionnaire>> call = null;
        if (from.equalsIgnoreCase(Constants.BOOKING_APPOINTMENT)) {
            call = apiService.getAppointmentAfterQuestionnaire(uid, providerAccountId);
        } else if (from.equalsIgnoreCase(Constants.BOOKING_CHECKIN)) {
            call = apiService.getWaitlistAfterQuestionnaire(uid, providerAccountId);
        } else if (from.equalsIgnoreCase(Constants.ORDERS)) {
            call = apiService.getOrderAfterQuestionnaire(uid, providerAccountId);
        }
        call.enqueue(new Callback<ArrayList<Questionnaire>>() {
            @Override
            public void onResponse(Call<ArrayList<Questionnaire>> call, Response<ArrayList<Questionnaire>> response) {
                try {
                    if (response.code() == 200) {
                        afterQuestionnaire = response.body();
                        for (RlsdQnr rlsdQnr : rlsdQnrs) {
                            if (rlsdQnr.getStatus().equalsIgnoreCase("released")) {
                                for (Questionnaire questionnaire : afterQuestionnaire) {
                                    if (rlsdQnr.getId() == questionnaire.getId()) {
                                        rlsdQnr.setQnrName(questionnaire.getQuestionnaireId());
                                    }
                                }
                            } else {
                                for (QuestionnaireResponse questionnaireResponse : aQuestionnaireResponses) {
                                    if (rlsdQnr.getId() == questionnaireResponse.getQuestionnaireId()) {
                                        rlsdQnr.setQnrName(questionnaireResponse.getQuestionnaireName());
                                    }
                                }
                            }
                        }
                        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(mContext);
                        rvRlsdQNR.setLayoutManager(mLayoutManager);
                        ReleasedQNRAdapter releasedQNRAdapter = new ReleasedQNRAdapter(mContext, rlsdQnrs, afterQuestionnaire, serviceId, providerAccountId, uid, uniqueId, status, from, aQuestionnaireResponses);
                        rvRlsdQNR.setAdapter(releasedQNRAdapter);
                    } else {
                        if (response.code() == 422) {
                            Toast.makeText(mContext, response.errorBody().string(), Toast.LENGTH_SHORT).show();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ArrayList<Questionnaire>> call, Throwable t) {
                // Log error here since request failed
                Config.logV("Fail---------------" + t.toString());
            }
        });
    }

    private void getCheckinDetails(String uuid, int pAccountId) {

        final ApiInterface apiService =
                ApiClient.getClient(mContext).create(ApiInterface.class);
        final Dialog mDialog = Config.getProgressDialog(ReleasedQNRActivity.this, ReleasedQNRActivity.this.getResources().getString(R.string.dialog_log_in));
        mDialog.show();
        Call<ActiveCheckIn> call = apiService.getActiveCheckInUUID(uuid, String.valueOf(pAccountId));
        call.enqueue(new Callback<ActiveCheckIn>() {
            @Override
            public void onResponse(Call<ActiveCheckIn> call, Response<ActiveCheckIn> response) {
                try {
                    if (mDialog.isShowing())
                        Config.closeDialog(getParent(), mDialog);
                    Config.logV("URL------ACTIVE CHECKIN---------" + response.raw().request().url().toString().trim());
                    Config.logV("Response--code-------------------------" + response.code());
                    if (response.code() == 200) {
                        checkinInfo = response.body();
                        if (checkinInfo != null) {

                            serviceId = checkinInfo.getService().getId();
                            providerAccountId = checkinInfo.getProviderAccount().getId();
                            uid = checkinInfo.getYnwUuid();
                            uniqueId = checkinInfo.getUniqueId();
                            if (checkinInfo.getWaitlistStatus() != null) {
                                status = checkinInfo.getWaitlistStatus();
                            }
                            createRlsdQNR(checkinInfo.getReleasedQnr(), checkinInfo.getQuestionnaires());

                        }
                    }
                } catch (Exception e) {
                    Log.i("mnbbnmmnbbnm", e.toString());
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ActiveCheckIn> call, Throwable t) {
                Config.logV("Fail---------------" + t.toString());

                if (mDialog.isShowing())
                    Config.closeDialog(getParent(), mDialog);
            }
        });
    }

    private void getAppointmentDetails(String uuid, int pAccountId) {
        final ApiInterface apiService =
                ApiClient.getClient(mContext).create(ApiInterface.class);
        final Dialog mDialog = Config.getProgressDialog(ReleasedQNRActivity.this, ReleasedQNRActivity.this.getResources().getString(R.string.dialog_log_in));
        mDialog.show();
        Call<ActiveAppointment> call = apiService.getActiveAppointmentUUID(uuid, String.valueOf(pAccountId));
        call.enqueue(new Callback<ActiveAppointment>() {
            @Override
            public void onResponse(Call<ActiveAppointment> call, Response<ActiveAppointment> response) {
                try {
                    if (mDialog.isShowing())
                        Config.closeDialog(getParent(), mDialog);
                    Config.logV("URL------ACTIVE CHECKIN---------" + response.raw().request().url().toString().trim());
                    Config.logV("Response--code-------------------------" + response.code());
                    if (response.code() == 200) {
                        apptInfo = response.body();

                        if (apptInfo != null) {
                            serviceId = apptInfo.getService().getId();
                            providerAccountId = apptInfo.getProviderAccount().getId();
                            uid = apptInfo.getUid();
                            uniqueId = apptInfo.getUniqueId();
                            if (apptInfo.getApptStatus() != null) {
                                status = apptInfo.getApptStatus();
                            }
                            createRlsdQNR(apptInfo.getReleasedQnr(), apptInfo.getQuestionnaires());

                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ActiveAppointment> call, Throwable t) {
                Config.logV("Fail---------------" + t.toString());

                if (mDialog.isShowing())
                    Config.closeDialog(getParent(), mDialog);
            }
        });
    }
    private void getOrderDetails(String uuid, int pAccountId) {

        ApiInterface apiService = ApiClient.getClient(mContext).create(ApiInterface.class);
        final Dialog mDialog = Config.getProgressDialog(ReleasedQNRActivity.this, ReleasedQNRActivity.this.getResources().getString(R.string.dialog_log_in));
        mDialog.show();
        Call<ActiveOrders> call = apiService.getOrderDetails(uuid, pAccountId);
        call.enqueue(new Callback<ActiveOrders>() {
            @Override
            public void onResponse(Call<ActiveOrders> call, Response<ActiveOrders> response) {
                if (mDialog.isShowing())
                    Config.closeDialog(getParent(), mDialog);
                try {
                    if (response.code() == 200) {

                        orderInfo = response.body();
                        if (orderInfo != null) {

                            serviceId = orderInfo.getCatalog().getCatLogId();
                            providerAccountId = orderInfo.getProviderAccount().getId();
                            uid = orderInfo.getUid();
                            uniqueId = String.valueOf(orderInfo.getProviderAccount().getUniqueId());
                            if (orderInfo.getOrderStatus() != null) {
                                status = orderInfo.getOrderStatus();
                            }
                            createRlsdQNR(orderInfo.getReleasedQnr(), orderInfo.getQuestionnaires());

                        }
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ActiveOrders> call, Throwable t) {
                // Log error here since request failed
                Config.logV("Fail---------------" + t.toString());
                if (mDialog.isShowing())
                    Config.closeDialog(getParent(), mDialog);
            }
        });
    }
}