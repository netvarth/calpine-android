package com.jaldeeinc.jaldee.custom;

import android.animation.LayoutTransition;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chinodev.androidneomorphframelayout.NeomorphFrameLayout;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.hbb20.CountryCodePicker;
import com.jaldeeinc.jaldee.Interface.IFamillyListSelected;
import com.jaldeeinc.jaldee.Interface.IFamilyMemberDetails;
import com.jaldeeinc.jaldee.Interface.ISelectSlotInterface;
import com.jaldeeinc.jaldee.Interface.ISlotInfo;
import com.jaldeeinc.jaldee.Interface.OnBottomReachedListener;
import com.jaldeeinc.jaldee.R;
import com.jaldeeinc.jaldee.activities.AppointmentActivity;
import com.jaldeeinc.jaldee.activities.CheckInActivity;
import com.jaldeeinc.jaldee.adapter.CheckIn_FamilyMemberListAdapter;
import com.jaldeeinc.jaldee.adapter.CheckIn_FamilyMemberListAdapterAppointment;
import com.jaldeeinc.jaldee.adapter.CustomSpinnerAdapter;
import com.jaldeeinc.jaldee.adapter.TimeSlotsAdapter;
import com.jaldeeinc.jaldee.common.Config;
import com.jaldeeinc.jaldee.connection.ApiClient;
import com.jaldeeinc.jaldee.connection.ApiInterface;
import com.jaldeeinc.jaldee.model.FamilyArrayModel;
import com.jaldeeinc.jaldee.response.AvailableSlotsData;
import com.jaldeeinc.jaldee.response.ProfileModel;
import com.jaldeeinc.jaldee.response.SearchDepartment;
import com.jaldeeinc.jaldee.response.SlotsData;
import com.jaldeeinc.jaldee.utils.SharedPreference;
import com.pranavpandey.android.dynamic.toasts.DynamicToast;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FamilyMemberDialog extends Dialog implements IFamillyListSelected {
    public FamilyMemberDialog(@NonNull Context context) {
        super(context);
    }

    private Context context;
    private ImageView ivClose, ivAddMember;
    String firstName, lastName, phone, email, prepayment, selectedMemberName;
    int consumerId, memId;
    ArrayList<FamilyArrayModel> familyMembersList = new ArrayList<>();
    List<FamilyArrayModel> LuserProfileList = new ArrayList<>();
    private Spinner memberSpinner;
    private EditText et_phone, et_email, et_firstname, et_lastName, et_countryCode;
    private Button bt_save, bt_add;
    private IFamilyMemberDetails iFamilyMemberDetails;
    CustomTextViewSemiBold tv_errorphone, tv_error_mail, tv_errorfirstname, tv_errorlastname;
    boolean isPrepayment;
    ProfileModel profileDetails;
    ArrayList<FamilyArrayModel> LCheckList = new ArrayList<>();
    RecyclerView mRecycleFamily;
    static List<FamilyArrayModel> checkedfamilyList = new ArrayList<>();
    CheckIn_FamilyMemberListAdapter mFamilyAdpater;
    boolean multiple;
    int update;
    private IFamillyListSelected iFamillyListSelected;
    ArrayList<FamilyArrayModel> familyList = new ArrayList<>();
    ArrayList<FamilyArrayModel> data = new ArrayList<>();
    ArrayList<FamilyArrayModel> checkList = new ArrayList<>();
    private LinearLayout ll_changeMember, ll_addmember, ll_chooseMember;
    Animation slideUp, slideRight;
    CountryCodePicker cCodePicker;
    String countryCode = "";
    String gender = null;
    boolean isVirtualService, isCheckin, isAppointment;


    public FamilyMemberDialog(AppointmentActivity appointmentActivity, int familyMEmID, String email, String phone, String prepayment, IFamilyMemberDetails iFamilyMemberDetails, ProfileModel profileDetails, boolean multiple, int update, String countryCode) {
        super(appointmentActivity);
        this.context = appointmentActivity;
        this.memId = familyMEmID;
        this.email = email;
        this.phone = phone;
        this.prepayment = prepayment;
        this.iFamilyMemberDetails = iFamilyMemberDetails;
        this.profileDetails = profileDetails;
        this.multiple = multiple;
        this.update = update;
        this.countryCode = countryCode;
        this.isAppointment = true;
    }

    public FamilyMemberDialog(CheckInActivity checkInActivity, int familyMEmID, String email, String phone, boolean prePayment, IFamilyMemberDetails iFamilyMemberDetails, ProfileModel profileDetails, boolean multiple, int update, String countryCode, boolean isVirtualService) {
        super(checkInActivity);
        this.context = checkInActivity;
        this.memId = familyMEmID;
        this.email = email;
        this.phone = phone;
        this.isPrepayment = prePayment;
        this.iFamilyMemberDetails = iFamilyMemberDetails;
        this.profileDetails = profileDetails;
        this.multiple = multiple;
        this.update = update;
        this.countryCode = countryCode;
        this.isVirtualService = isVirtualService;
        this.isCheckin = true;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.familymember_dialog);


        ivClose = findViewById(R.id.iv_close);
        ivAddMember = findViewById(R.id.addmember);
        et_phone = findViewById(R.id.edtPhone);
        et_email = findViewById(R.id.edtMail);
        bt_save = findViewById(R.id.btnSave);
        tv_error_mail = findViewById(R.id.errormesg);
        tv_errorphone = findViewById(R.id.error_mesg);
        ll_addmember = findViewById(R.id.ll_addmember);
        ll_changeMember = findViewById(R.id.ll_changemember);
        ll_chooseMember = findViewById(R.id.ll_chooseMember);
        et_firstname = findViewById(R.id.addfirstname);
        et_lastName = findViewById(R.id.addlastname);
        bt_add = findViewById(R.id.btnAdd);
        tv_errorlastname = findViewById(R.id.errormesg_add);
        tv_errorfirstname = findViewById(R.id.error_mesg_add);
        slideUp = AnimationUtils.loadAnimation(context, R.anim.slide_in_left);
        slideRight = AnimationUtils.loadAnimation(context, R.anim.slide_out_right);
        cCodePicker = findViewById(R.id.ccp);
        et_countryCode = findViewById(R.id.edt_Ccode);
        if (isCheckin && isVirtualService) {
            ll_chooseMember.setVisibility(View.GONE);
        }
        if (profileDetails.getUserprofile().getGender() != null) {
            if (!profileDetails.getUserprofile().getGender().equalsIgnoreCase("")) {
                if (profileDetails.getUserprofile().getGender().equalsIgnoreCase("Male")) {
                    gender = "male";
                } else if (profileDetails.getUserprofile().getGender().equalsIgnoreCase("Female")) {
                    gender = "female";
                } else if (profileDetails.getUserprofile().getGender().equalsIgnoreCase("Other")) {
                    gender = "other";
                }
            }
        }
        this.iFamillyListSelected = this;
        if (update == 1) {
            checkList = (ArrayList<FamilyArrayModel>) checkedfamilyList;
        } else {
            checkedfamilyList.clear();
        }

        if (email != null) {
            et_email.setText(email);
        }
        if (phone != null) {
            et_phone.setText(phone);
        }

        firstName = SharedPreference.getInstance(context).getStringValue("firstname", "");
        lastName = SharedPreference.getInstance(context).getStringValue("lastname", "");
        consumerId = SharedPreference.getInstance(context).getIntValue("consumerId", 0);
        mRecycleFamily = findViewById(R.id.recycle_familyMember);

        if (countryCode != null) {
            et_countryCode.setText(countryCode);
        }

        et_countryCode.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                et_countryCode.setVisibility(View.GONE);
                cCodePicker.setVisibility(View.VISIBLE);
                countryCode = cCodePicker.getSelectedCountryCodeWithPlus();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });


        cCodePicker.setOnCountryChangeListener(new CountryCodePicker.OnCountryChangeListener() {
            @Override
            public void onCountrySelected() {

                countryCode = cCodePicker.getSelectedCountryCodeWithPlus();
            }
        });


        et_phone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                cCodePicker.setVisibility(View.VISIBLE);
                countryCode = cCodePicker.getSelectedCountryCodeWithPlus();
                et_countryCode.setVisibility(View.GONE);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        if (isCheckin && isVirtualService) {
            bt_save.setEnabled(true);
            bt_save.setBackground(context.getResources().getDrawable(R.drawable.curved_save));
            bt_save.setTextColor(context.getResources().getColor(R.color.white));
        }else {
            bt_save.setEnabled(false);
            bt_save.setBackground(context.getResources().getDrawable(R.drawable.btn_checkin_grey));
        }
        bt_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                email = et_email.getText().toString();
                phone = et_phone.getText().toString();

                if (!phone.equalsIgnoreCase("")) {
                    if (phone.trim().length() > 9) {
                        if (isCheckin && isVirtualService) {
                            iFamilyMemberDetails.sendFamilyMbrPhoneAndEMail(phone, email);
                            dismiss();

                        } else {
                            if (prepayment != null) {
                                appointment();
                            } else {
                                checkin();
                                if (multiple) {
                                    familyList.clear();

                                    data = mFamilyAdpater.onItemSelected();

                                    Config.logV("Family------------" + data.size());
                                    for (int i = 0; i < data.size(); i++) {

                                        if (data.get(i).isCheck()) {
                                            FamilyArrayModel family = new FamilyArrayModel();
                                            family.setId(data.get(i).getId());
                                            family.setFirstName(data.get(i).getFirstName());
                                            family.setLastName(data.get(i).getLastName());
                                            family.setCheck(true);
                                            familyList.add(family);
                                        }

                                        if (i == data.size() - 1) {
                                            Config.logV("family refresh-------@@@@---------" + familyList.size());
                                            if (familyList.size() > 0) {
                                                iFamilyMemberDetails.refreshMultipleMEmList(familyList);
                                                dismiss();
                                            }
                                        }

                                    }
                                } else {
                                    CheckInActivity.refreshName(s_changename, memberid);
                                    dismiss();
                                }
                            }
                        }
                    } else {
                        tv_errorphone.setText("Enter valid mobile number");
                        tv_errorphone.setVisibility(View.VISIBLE);
                    }

                } else {
                    tv_errorphone.setText("This field is required");
                    tv_errorphone.setVisibility(View.VISIBLE);
                }

            }
        });


        ivClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

        ivAddMember.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                ll_changeMember.setVisibility(View.GONE);
                ll_changeMember.startAnimation(slideRight);
                ll_addmember.setVisibility(View.VISIBLE);
                ll_addmember.startAnimation(slideUp);

            }
        });

        bt_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!et_firstname.getText().toString().isEmpty() && !et_lastName.getText().toString().isEmpty()) {
                    ApiAddFamilyMember();
                } else {
                    if (et_firstname.getText().toString().isEmpty()) {
                        tv_errorfirstname.setText("This field is required");
                        tv_errorfirstname.setVisibility(View.VISIBLE);
                    } else {
                        if (et_lastName.getText().toString().isEmpty()) {
                            tv_errorlastname.setText("This field is required");
                            tv_errorlastname.setVisibility(View.VISIBLE);
                        }
                    }
                }
            }
        });

        ApiListFamilyMember();

    }

    boolean isEmailValid(CharSequence email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email)
                .matches();
    }

    void appointment() {
        if (prepayment != null && prepayment.equalsIgnoreCase("true")) {
            if (email.trim().length() > 0) {
                if (isEmailValid(email)) {
                    ApiEditProfileDetail();
                } else {
                    tv_error_mail.setText("Enter valid mail id");
                    tv_error_mail.setVisibility(View.VISIBLE);
                }
            } else {
                tv_error_mail.setText("This field is required");
                tv_error_mail.setVisibility(View.VISIBLE);
            }
        } else {
            if (email.trim().length() > 0) {
                if (isEmailValid(email)) {
                    ApiEditProfileDetail();
                } else {
                    tv_error_mail.setText("Enter valid mail id");
                    tv_error_mail.setVisibility(View.VISIBLE);
                }
            } else {
                iFamilyMemberDetails.sendFamilyMemberDetails(memId, selectedMemberName, lastName, phone, email, countryCode);
                Toast.makeText(context, "Details saved successfully", Toast.LENGTH_SHORT).show();
                dismiss();
            }
        }
    }

    void checkin() {
        if (isPrepayment) {
            if (email.trim().length() > 0) {
                if (isEmailValid(email)) {
                    ApiEditProfileDetail();
                } else {
                    tv_error_mail.setText("Enter valid mail Id");
                    tv_error_mail.setVisibility(View.VISIBLE);
                }
            } else {
                tv_error_mail.setText("This field is required");
                tv_error_mail.setVisibility(View.VISIBLE);
            }
        } else {
            if (email.trim().length() > 0) {
                if (isEmailValid(email)) {
                    ApiEditProfileDetail();
                } else {
                    tv_error_mail.setText("Enter valid mail Id");
                    tv_error_mail.setVisibility(View.VISIBLE);
                }
            } else {
                iFamilyMemberDetails.sendFamilyMemberDetails(memId, selectedMemberName, lastName, phone, email, countryCode);
                iFamillyListSelected.CheckedFamilyList(checkedfamilyList);
                Toast.makeText(context, "Details saved successfully", Toast.LENGTH_SHORT).show();
                dismiss();
            }
        }

    }


    private void ApiListFamilyMember() {


        ApiInterface apiService =
                ApiClient.getClient(context).create(ApiInterface.class);

        final Dialog mDialog = Config.getProgressDialog(context, context.getResources().getString(R.string.dialog_log_in));
        mDialog.show();

        Call<ArrayList<FamilyArrayModel>> call = apiService.getFamilyList();

        call.enqueue(new Callback<ArrayList<FamilyArrayModel>>() {
            @Override
            public void onResponse(Call<ArrayList<FamilyArrayModel>> call, Response<ArrayList<FamilyArrayModel>> response) {

                try {

                    if (mDialog.isShowing())
                        Config.closeDialog((Activity) context, mDialog);

                    Config.logV("URL---------------" + response.raw().request().url().toString().trim());
                    Config.logV("Response--code-------------------------" + response.code());

                    if (response.code() == 200) {

                        if (response.body() != null) {

                            LuserProfileList.clear();
                            familyMembersList.clear();
                            familyMembersList = response.body();
                            FamilyArrayModel family = new FamilyArrayModel();
                            family.setFirstName(firstName);
                            family.setLastName(lastName);
                            family.setId(consumerId);
                            LuserProfileList.add(family);
                            if (LuserProfileList.size() > 0) {

                                if (response.body().size() > 0) {
                                    for (int i = 0; i < response.body().size(); i++) {
                                        FamilyArrayModel family1 = new FamilyArrayModel();
                                        family1.setFirstName(response.body().get(i).getUserProfile().getFirstName());
                                        family1.setLastName(response.body().get(i).getUserProfile().getLastName());
                                        family1.setId(response.body().get(i).getUserProfile().getId());
                                        LuserProfileList.add(family1);
                                    }
                                }
                            }

                            LCheckList.clear();

                            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(context);
                            mRecycleFamily.setLayoutManager(mLayoutManager);

                            Config.logV("CheckList@@@@@@@@@@@@@@@@@@@@1111" + checkedfamilyList.size());
                            Config.logV("CheckList@@@@@@@@@@@@@@@@@@@@ LuserProfileList" + LuserProfileList.size());
                            if (update == 1 || multiple) {

                                for (int j = 0; j < LuserProfileList.size(); j++) {
                                    FamilyArrayModel family1 = new FamilyArrayModel();
                                    family1.setFirstName(LuserProfileList.get(j).getFirstName());
                                    family1.setLastName(LuserProfileList.get(j).getLastName());
                                    family1.setId(LuserProfileList.get(j).getId());
                                    for (int i = 0; i < checkedfamilyList.size(); i++) {

                                        if (checkedfamilyList.get(i).getId() == LuserProfileList.get(j).getId()) {
                                            family1.setCheck(true);
                                            Config.logV("Family %%%%%%%%%%%%%%%" + LuserProfileList.get(j).getFirstName());
                                        }
                                    }
                                    LCheckList.add(family1);

                                }

                                Config.logV("Family @@@@" + LCheckList.size());

                                if (checkedfamilyList.size() > 0) {
                                    bt_save.setBackground(context.getResources().getDrawable(R.drawable.curved_save));
                                    bt_save.setTextColor(context.getResources().getColor(R.color.white));
                                    bt_save.setEnabled(true);
                                } else {
                                    bt_save.setBackground(context.getResources().getDrawable(R.drawable.btn_checkin_grey));
                                    bt_save.setTextColor(context.getResources().getColor(R.color.button_grey));
                                    bt_save.setEnabled(false);
                                }


                                mFamilyAdpater = new CheckIn_FamilyMemberListAdapter(bt_save, LCheckList, multiple, LCheckList, context, (Activity) context, iFamillyListSelected);
                            } else {
                                if (memId == 0) {
                                    memId = consumerId;
                                }


                                Config.logV("memID @@@@@" + memId);
                                mFamilyAdpater = new CheckIn_FamilyMemberListAdapter(bt_save, update, memId, multiple, LuserProfileList, context, (Activity) context, iFamillyListSelected);
                            }

                            mRecycleFamily.setAdapter(mFamilyAdpater);
                            mFamilyAdpater.notifyDataSetChanged();

                        }
                    }

                } catch (
                        Exception e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(Call<ArrayList<FamilyArrayModel>> call, Throwable t) {
                // Log error here since request failed
                Config.logV("Fail---------------" + t.toString());
                if (mDialog.isShowing())
                    Config.closeDialog((Activity) context, mDialog);

            }
        });


    }

    private void ApiEditProfileDetail() {


        ApiInterface apiService =
                ApiClient.getClient(context).create(ApiInterface.class);

        final int consumerId = SharedPreference.getInstance(context).getIntValue("consumerId", 0);
        JSONObject jsonObj = new JSONObject();
        try {
            jsonObj.put("id", consumerId);
            jsonObj.put("firstName", profileDetails.getUserprofile().getFirstName());
            jsonObj.put("lastName", profileDetails.getUserprofile().getLastName());

            jsonObj.put("email", et_email.getText().toString());
            if (gender != null) {
                jsonObj.put("gender", gender);
            }


            jsonObj.put("dob", profileDetails.getUserprofile().getDob());

        } catch (JSONException e) {
            e.printStackTrace();
        }
        RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), jsonObj.toString());
        Call<ResponseBody> call = apiService.getEditProfileDetail(body);
//        Config.logV("Request--BODY-------------------------" + new Gson().toJson(jsonObj.toString()));
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                try {

//                    if (mDialog.isShowing())
//                        Config.closeDialog(getActivity(), mDialog);

                    Config.logV("URL---------------" + response.raw().request().url().toString().trim());
                    Config.logV("Response--code-------------------------" + response.code());
                    if (response.code() == 200) {
                        if (response.body().string().equalsIgnoreCase("true")) {
                            SharedPreference.getInstance(context).setValue("email", et_email.getText().toString());

                            Toast.makeText(context, "Details saved successfully ", Toast.LENGTH_LONG).show();
                            iFamilyMemberDetails.sendFamilyMemberDetails(memId, selectedMemberName, lastName, phone, email, countryCode);
                            iFamillyListSelected.CheckedFamilyList(checkedfamilyList);
                            dismiss();


                        }

                    } else {

                        Toast.makeText(context, response.errorBody().string(), Toast.LENGTH_LONG).show();

                        Config.logV("Error" + response.errorBody().string());

                    }


                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                // Log error here since request failed
                Config.logV("Fail---------------" + t.toString());
//                if (mDialog.isShowing())
//                    Config.closeDialog(getActivity(), mDialog);

            }
        });


    }


    private void ApiAddFamilyMember() {


        ApiInterface apiService =
                ApiClient.getClient(context).create(ApiInterface.class);

        final int consumerId = SharedPreference.getInstance(context).getIntValue("consumerId", 0);

        final Dialog mDialog = Config.getProgressDialog(context, context.getResources().getString(R.string.dialog_log_in));
        mDialog.show();
        JSONObject userProfile = new JSONObject();
        JSONObject jsonObj = new JSONObject();
        try {

            jsonObj.put("firstName", et_firstname.getText().toString());
            jsonObj.put("lastName", et_lastName.getText().toString());
            userProfile.putOpt("userProfile", jsonObj);
            //  userProfile.put("parent",consumerId);
        } catch (JSONException e) {
            e.printStackTrace();
        }


        RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), userProfile.toString());
        Call<ResponseBody> call = apiService.AddFamilyMEmber(body);
        Config.logV("Request--BODY-------------------------" + new Gson().toJson(userProfile.toString()));
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                try {

                    if (mDialog.isShowing())
                        Config.closeDialog((Activity) context, mDialog);

                    Config.logV("URL---------------" + response.raw().request().url().toString().trim());
                    Config.logV("Response--code-------------------------" + response.code());
                    Config.logV("Request--BODY-------------------------" + new Gson().toJson(response.body()));
                    if (response.code() == 200) {
                        bt_save.setEnabled(false);
                        bt_save.setBackground(context.getResources().getDrawable(R.drawable.btn_checkin_grey));
                        et_firstname.setText("");
                        et_lastName.setText("");
                        ll_addmember.setVisibility(View.GONE);
                        ll_addmember.startAnimation(slideRight);
                        ll_changeMember.setVisibility(View.VISIBLE);
                        ll_changeMember.startAnimation(slideUp);
                        ApiListFamilyMember();


                    } else {
                        String responseerror = response.errorBody().string();
                        Config.logV("Response--error----11111---------------------" + responseerror);
                        Toast.makeText(context, responseerror, Toast.LENGTH_SHORT).show();
                    }


                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                // Log error here since request failed
                Config.logV("Fail---------------" + t.toString());
                if (mDialog.isShowing())
                    Config.closeDialog((Activity) context, mDialog);

            }
        });


    }

    static String s_changename;
    static int memberid;


    @Override
    public void changeMemberName(String name, int id) {
        selectedMemberName = name;
        memId = id;
        bt_save.setBackground(context.getResources().getDrawable(R.drawable.curved_save));
        bt_save.setTextColor(context.getResources().getColor(R.color.white));
        bt_save.setEnabled(true);

    }

    @Override
    public void changeMemberName(String name, FamilyArrayModel familylist) {
        selectedMemberName = name;
        memId = familylist.getId();
        bt_save.setBackground(context.getResources().getDrawable(R.drawable.curved_save));
        bt_save.setTextColor(context.getResources().getColor(R.color.white));
        bt_save.setEnabled(true);

    }

    @Override
    public void CheckedFamilyList(List<FamilyArrayModel> familyList) {
        checkedfamilyList = familyList;

    }

    @Override
    public void SelectedPincodeLocation(JsonObject selectedPincodeLocation) {

    }
}
