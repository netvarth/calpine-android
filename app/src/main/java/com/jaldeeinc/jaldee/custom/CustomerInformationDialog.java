package com.jaldeeinc.jaldee.custom;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.hbb20.CountryCodePicker;
import com.jaldeeinc.jaldee.Interface.IFamillyListSelected;
import com.jaldeeinc.jaldee.Interface.IFamilyMemberDetails;
import com.jaldeeinc.jaldee.R;
import com.jaldeeinc.jaldee.activities.AppointmentActivity;
import com.jaldeeinc.jaldee.activities.CheckInActivity;
import com.jaldeeinc.jaldee.adapter.CheckIn_FamilyMemberListAdapter;
import com.jaldeeinc.jaldee.adapter.ChooseLanguagesAdapter;
import com.jaldeeinc.jaldee.adapter.PincodeLocationsAdapter;
import com.jaldeeinc.jaldee.common.Config;
import com.jaldeeinc.jaldee.connection.ApiClient;
import com.jaldeeinc.jaldee.connection.ApiInterface;
import com.jaldeeinc.jaldee.model.FamilyArrayModel;
import com.jaldeeinc.jaldee.model.PincodeLocationsResponse;
import com.jaldeeinc.jaldee.response.ProfileModel;
import com.jaldeeinc.jaldee.utils.SharedPreference;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CustomerInformationDialog extends Dialog implements IFamillyListSelected {
    public CustomerInformationDialog(@NonNull Context context) {
        super(context);
    }

    private Context context;
    private ImageView ivClose/*, ivAddMember*/;
    String firstName, lastName, phone, email, prepayment, selectedMemberName;
    int consumerId, memId, familyMemId;
    ArrayList<FamilyArrayModel> familyMembersList = new ArrayList<>();
    List<FamilyArrayModel> LuserProfileList = new ArrayList<>();
    private Spinner memberSpinner;
    private EditText edtTelegram, edtWhtsAppNumber, et_email, et_pincode, et_age, et_firstname, et_lastName, et_residing_location, et_residing_state;
    private Button bt_save, btn_language_ok, btn_language_cancel;
    private IFamilyMemberDetails iFamilyMemberDetails;
    CustomTextViewSemiBold tv_errorphone, tv_error_mail, tv_errorfirstname, tv_errorChooseLanguage, tv_phoneNumber, tv_gender_errormesg, tv_age_errormesg, tv_pin_errormesg, tv_lName_errormesg, tv_fName_errormesg, tv_residing_location_errmsg, tv_residing_state_errmsg;
    CustomTextViewMedium tv_languaes, tv_cnsln_for_basic_infrmtn_hint;
    CustomTextViewBold tv_cstmr_infrmn;
    boolean isPrepayment;
    ProfileModel profileDetails;
    ArrayList<FamilyArrayModel> LCheckList = new ArrayList<>();
    RecyclerView mRecycleFamily, mRecyclePincode, mRecycleChooseLanguages;
    static List<FamilyArrayModel> checkedfamilyList = new ArrayList<>();
    CheckIn_FamilyMemberListAdapter mFamilyAdpater;
    boolean multiple;
    int update;
    private IFamillyListSelected iFamillyListSelected;
    ArrayList<FamilyArrayModel> familyList = new ArrayList<>();
    ArrayList<FamilyArrayModel> data = new ArrayList<>();
    ArrayList<FamilyArrayModel> checkList = new ArrayList<>();
    private LinearLayout ll_changeMember, ll_chooselanguages, ll_languages, ll_edit_languaes, ll_add_member, ll_if_interntionl_nmbr, ll_residing_location, ll_residing_state, ll_residing_pincode;
    Animation slideUp, slideRight;
    CountryCodePicker WhtsappCCodePicker, TelegramCCodePicker;
    String countryCode = "";
    String gender = null;
    RadioGroup radio_gender, radio_language;
    RadioButton radioM, radioF, radioO, radioEnglishlng, radioOtherlng;
    JSONObject selectedPincode = new JSONObject();
    ArrayList<PincodeLocationsResponse> pincodeLocations;
    PincodeLocationsAdapter mPincodeLocationAdapter;
    TextWatcher tw_et_pincode;
    ArrayList<String> preferredLanguages = new ArrayList<String>();
    ChooseLanguagesAdapter chooseLanguagesAdapter;
    FamilyArrayModel familylist;
    ScrollView scrollView;
    String domain;
    CheckBox cb_email_update_to_myaccount;

    public CustomerInformationDialog(AppointmentActivity appointmentActivity, int familyMEmID, String email, String phone, String prepayment, IFamilyMemberDetails iFamilyMemberDetails, ProfileModel profileDetails, boolean multiple, int update, String countryCode, String domain) {
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
        this.domain = domain;
    }

    public CustomerInformationDialog(CheckInActivity checkInActivity, int familyMEmID, String email, String phone, boolean prePayment, IFamilyMemberDetails iFamilyMemberDetails, ProfileModel profileDetails, boolean multiple, int update, String countryCode, String domain) {
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
        this.domain = domain;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.customerinformation_dialog);

        tv_cstmr_infrmn = findViewById(R.id.tv_cstmr_infrmn);
        tv_cnsln_for_basic_infrmtn_hint = findViewById(R.id.tv_cnsln_for_basic_infrmtn_hint);
        ivClose = findViewById(R.id.iv_close);
        //ivAddMember = findViewById(R.id.addmember);
        edtWhtsAppNumber = findViewById(R.id.edtWhtsAppNumber);
        edtTelegram = findViewById(R.id.edtTelegram);

        et_email = findViewById(R.id.edtMail);
        bt_save = findViewById(R.id.btnSave);
        tv_error_mail = findViewById(R.id.errormesg);
        tv_errorphone = findViewById(R.id.error_mesg);
        ll_chooselanguages = findViewById(R.id.ll_chooselanguages);
        ll_changeMember = findViewById(R.id.ll_changemember);
        //et_firstname = findViewById(R.id.addfirstname);
        //et_lastName = findViewById(R.id.addlastname);
        btn_language_ok = findViewById(R.id.btn_language_ok);
        btn_language_cancel = findViewById(R.id.btn_language_cancel);
        tv_errorChooseLanguage = findViewById(R.id.errormesg_add);
        //tv_errorfirstname = findViewById(R.id.error_mesg_add);
        slideUp = AnimationUtils.loadAnimation(context, R.anim.slide_in_left);
        slideRight = AnimationUtils.loadAnimation(context, R.anim.slide_out_right);
        WhtsappCCodePicker = findViewById(R.id.Wccp);
        TelegramCCodePicker = findViewById(R.id.Tccp);

        radio_gender = findViewById(R.id.radiogender);
        radioF = findViewById(R.id.radioF);
        radioM = findViewById(R.id.radioM);
        radioO = findViewById(R.id.radioO);
        et_pincode = findViewById(R.id.et_pincode);
        radio_language = findViewById(R.id.radio_language);
        radioEnglishlng = findViewById(R.id.radioEnglishlng);
        radioOtherlng = findViewById(R.id.radioOtherlng);
        ll_languages = findViewById(R.id.ll_languages);
        ll_edit_languaes = findViewById(R.id.ll_edit_languaes);
        tv_languaes = findViewById(R.id.tv_languaes);
        tv_phoneNumber = findViewById(R.id.tv_phoneNumber);

        mRecyclePincode = findViewById(R.id.recycle_pincode_locations);
        mRecycleFamily = findViewById(R.id.recycle_familyMember);
        mRecycleChooseLanguages = findViewById(R.id.recycle_choose_languages);
        scrollView = findViewById(R.id.scrollView);
        tv_gender_errormesg = findViewById(R.id.tv_gender_errormesg);
        tv_age_errormesg = findViewById(R.id.tv_age_errormesg);
        et_age = findViewById(R.id.et_age);
        tv_pin_errormesg = findViewById(R.id.tv_pin_errormesg);

        ll_add_member = findViewById(R.id.ll_add_member);
        et_firstname = findViewById(R.id.et_fName);
        et_lastName = findViewById(R.id.et_lName);
        tv_fName_errormesg = findViewById(R.id.tv_fName_errormesg);
        tv_lName_errormesg = findViewById(R.id.tv_lName_errormesg);

        ll_residing_pincode = findViewById(R.id.ll_residing_pincode);
        ll_if_interntionl_nmbr = findViewById(R.id.ll_if_interntionl_nmbr);
        ll_residing_location = findViewById(R.id.ll_residing_location);
        ll_residing_state = findViewById(R.id.ll_residing_state);
        et_residing_location = findViewById(R.id.et_residing_location);
        et_residing_state = findViewById(R.id.et_residing_state);
        tv_residing_location_errmsg = findViewById(R.id.tv_residing_location_errmsg);
        tv_residing_state_errmsg = findViewById(R.id.tv_residing_state_errmsg);
        cb_email_update_to_myaccount = findViewById(R.id.cb_email_update_to_myaccount);
        cb_email_update_to_myaccount.setChecked(false);
        if (domain != null) {
            if (domain.equalsIgnoreCase("healthCare")) {
                tv_cstmr_infrmn.setText("Patient Information");
                tv_cnsln_for_basic_infrmtn_hint.setText("Please provide basic information about the patient");
            } else if(domain.equalsIgnoreCase("educationalInstitution")) {
                tv_cstmr_infrmn.setText("Student Information");
                tv_cnsln_for_basic_infrmtn_hint.setText("Please provide basic information about the student");
            }else {
                tv_cstmr_infrmn.setText("Customer Information");
                tv_cnsln_for_basic_infrmtn_hint.setText("Please provide basic information about the customer");
            }
        }
        if (profileDetails.getUserprofile().getGender() != null) {
            if (!profileDetails.getUserprofile().getGender().equalsIgnoreCase("")) {
                if (profileDetails.getUserprofile().getGender().equalsIgnoreCase("Male")) {
                    gender = "male";
                    //radio_gender.check(radioM.getId());
                } else if (profileDetails.getUserprofile().getGender().equalsIgnoreCase("Female")) {
                    gender = "female";
                    //radio_gender.check(radioF.getId());
                } else if (profileDetails.getUserprofile().getGender().equalsIgnoreCase("Other")) {
                    gender = "other";
                    //radio_gender.check(radioO.getId());
                }
            }
        }
        if (profileDetails.getUserprofile().getPreferredLanguages() != null) {
            preferredLanguages = profileDetails.getUserprofile().getPreferredLanguages();
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
            if (countryCode != null) {
                tv_phoneNumber.setText(countryCode + " " + phone);
            } else {
                tv_phoneNumber.setText(phone);
            }
        }

        firstName = SharedPreference.getInstance(context).getStringValue("firstname", "");
        lastName = SharedPreference.getInstance(context).getStringValue("lastname", "");
        consumerId = SharedPreference.getInstance(context).getIntValue("consumerId", 0);

        btn_language_ok.setOnClickListener(new CompoundButton.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<String> languages = chooseLanguagesAdapter.onItemSelected();

                if (languages == null || languages.isEmpty()) {
                    tv_errorChooseLanguage.setVisibility(View.VISIBLE);
                } else {
                    preferredLanguages = languages;
                    tv_languaes.setText(preferredLanguages.toString().replace("[", "").replace("]", ""));
                    ll_languages.setVisibility(View.VISIBLE);
                    ll_changeMember.setVisibility(View.VISIBLE);
                    ll_changeMember.startAnimation(slideUp);
                    ll_chooselanguages.setVisibility(View.GONE);
                    ll_chooselanguages.startAnimation(slideRight);
                    ivClose.setVisibility(View.VISIBLE);
                }
            }
        });
        btn_language_cancel.setOnClickListener(new CompoundButton.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (radioOtherlng.isChecked()) {
                    //chooseLanguagesAdapter = new ChooseLanguagesAdapter(context, (Activity) context, familylist.getPreferredLanguages());
                    chooseLanguagesAdapter = new ChooseLanguagesAdapter(context, (Activity) context, preferredLanguages);
                    mRecycleChooseLanguages.setVisibility(View.VISIBLE);
                    RecyclerView.LayoutManager mChooseLanguagesLayoutManager = new LinearLayoutManager(context);
                    mRecycleChooseLanguages.setLayoutManager(mChooseLanguagesLayoutManager);
                    mRecycleChooseLanguages.setAdapter(chooseLanguagesAdapter);
                    chooseLanguagesAdapter.notifyDataSetChanged();
                    ll_changeMember.setVisibility(View.VISIBLE);
                    ll_changeMember.startAnimation(slideUp);
                    ll_chooselanguages.setVisibility(View.GONE);
                    ll_chooselanguages.startAnimation(slideRight);
                    ivClose.setVisibility(View.VISIBLE);
                }
            }
        });

        ll_edit_languaes.setOnClickListener(new CompoundButton.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseOtherLanguages();
            }
        });
        radioOtherlng.setOnClickListener(new CompoundButton.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (preferredLanguages == null || preferredLanguages.isEmpty()) {
                    chooseOtherLanguages();
                } else {
                    tv_languaes.setText(preferredLanguages.toString().replace("[", "").replace("]", ""));////////////////
                    ll_languages.setVisibility(View.VISIBLE);
                }
                //chooseOtherLanguages();

            }
        });
        radioEnglishlng.setOnClickListener(new CompoundButton.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (radioEnglishlng.isChecked()) {
                    ll_languages.setVisibility(View.GONE);
                }
            }
        });
        radio_gender.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                tv_gender_errormesg.setVisibility(View.GONE);
            }
        });
        /*
        radioEnglishlng.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) {
                    preferredLanguages.clear();
                    preferredLanguages.add("English");
                    ll_languages.setVisibility(View.GONE);
                }
            }
        });*/
        et_pincode.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                tv_pin_errormesg.setVisibility(View.GONE);
                if (count != 6 && s.length() == 6) {
                    ApiGetPinLocations(Integer.parseInt(et_pincode.getText().toString()));
                    try {
                        selectedPincode.put("pincode", et_pincode.getText().toString());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    mRecyclePincode.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                /*if(et_pincode.length() == 6){
                    ApiGetPinLocations(Integer.parseInt(
                    netvarth.getText().toString()));
                }else {
                    mRecyclePincode.setVisibility(View.GONE);
                }*/

            }
        });


        WhtsappCCodePicker.setOnCountryChangeListener(new CountryCodePicker.OnCountryChangeListener() {
            @Override
            public void onCountrySelected() {

                //countryCode = WhtsappCCodePicker.getSelectedCountryCodeWithPlus();

            }
        });
        et_age.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (et_age.getText() != null && !et_age.getText().toString().equals("") && Integer.parseInt(et_age.getText().toString()) > 150) {
                    tv_age_errormesg.setText("Maximum age is 150");
                    tv_age_errormesg.setVisibility(View.VISIBLE);
                } else {
                    tv_age_errormesg.setVisibility(View.GONE);
                }
            }
        });
        bt_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean isOk = true;
                if (familylist.isAddMember()) {
                    if (et_firstname.getText() == null || et_firstname.getText().toString().equals("")) {
                        tv_fName_errormesg.setVisibility(View.VISIBLE);
                        //scrollView.smoothScrollTo(0, scrollView.getTop());
                        isOk = false;
                    } else if (et_firstname.getText().toString().length() < 3) {
                        tv_fName_errormesg.setText("Firstname is too short");
                        tv_fName_errormesg.setVisibility(View.VISIBLE);
                        //scrollView.smoothScrollTo(0, scrollView.getTop());
                        isOk = false;
                    } else {
                        tv_fName_errormesg.setVisibility(View.GONE);
                    }
                    if (et_lastName.getText() == null || et_lastName.getText().toString().equals("")) {
                        tv_lName_errormesg.setVisibility(View.VISIBLE);
                        //scrollView.smoothScrollTo(0, scrollView.getTop());
                        isOk = false;
                    } else {
                        tv_lName_errormesg.setVisibility(View.GONE);
                    }
                }
                if (radio_gender.getCheckedRadioButtonId() == -1) {
                    tv_gender_errormesg.setVisibility(View.VISIBLE);
                    //scrollView.smoothScrollTo(0, scrollView.getTop());
                    isOk = false;
                }
                if (et_age.getText() == null || et_age.getText().toString().equals("")) {
                    tv_age_errormesg.setVisibility(View.VISIBLE);
                    //scrollView.smoothScrollTo(0, scrollView.getTop());
                    isOk = false;
                } else if (Integer.parseInt(et_age.getText().toString()) > 150) {
                    tv_age_errormesg.setText("Maximum age is 150");
                    tv_age_errormesg.setVisibility(View.VISIBLE);
                    //scrollView.smoothScrollTo(0, scrollView.getTop());
                    isOk = false;
                }
                if (countryCode.equalsIgnoreCase("+91")) {
                    if (et_pincode.getText() == null || et_pincode.getText().toString().equals("")) {
                        tv_pin_errormesg.setVisibility(View.VISIBLE);
                        //scrollView.smoothScrollTo(0, scrollView.getTop());
                        isOk = false;
                    } else if (et_pincode.getText().length() < 6) {
                        tv_pin_errormesg.setText("Please provide a valid pincode");
                        tv_pin_errormesg.setVisibility(View.VISIBLE);
                        //scrollView.smoothScrollTo(0, scrollView.getTop());
                        isOk = false;
                    }
                } else {
                    if (et_residing_location == null || et_residing_location.getText().toString().equals("")) {
                        tv_residing_location_errmsg.setVisibility(View.VISIBLE);
                        isOk = false;
                    } else if (et_residing_state == null || et_residing_state.getText().toString().equals("")) {
                        tv_residing_state_errmsg.setVisibility(View.VISIBLE);
                        isOk = false;
                    }
                }
                if (isOk) {
                    if (familylist.getId() == consumerId) {
                        ApiEditProfileDetail();
                    } else {
                        if (familylist.isAddMember()) {
                            ApiAddFamilyMember();
                        } else {
                            ApiUpdateFamilyMember();
                        }
                    }
                } else {
                    scrollView.smoothScrollTo(0, scrollView.getTop());
                }
            }
        });


        ivClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                iFamilyMemberDetails.closeActivity();
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
                            family.setGender(gender);
                            family.setCheck(true);
                            if (preferredLanguages != null) {
                                family.setPreferredLanguages(preferredLanguages);
                            }
                            if (profileDetails.getUserprofile().getWhatsAppNum() != null && profileDetails.getUserprofile().getWhatsAppNum().size() != 0) {
                                if (profileDetails.getUserprofile().getWhatsAppNum().getAsJsonPrimitive("number") != null) {
                                    family.setWhtsAppCountryCode(profileDetails.getUserprofile().getWhatsAppNum().getAsJsonPrimitive("countryCode").getAsString());
                                    family.setWhtsAppNumber(profileDetails.getUserprofile().getWhatsAppNum().getAsJsonPrimitive("number").getAsString());
                                }
                            } else {
                                family.setWhtsAppNumber(profileDetails.getUserprofile().getPrimaryMobileNo());
                                family.setWhtsAppCountryCode(profileDetails.getUserprofile().getCountryCode());
                            }
                            if (profileDetails.getUserprofile().getTelegramNum() != null && profileDetails.getUserprofile().getTelegramNum().size() != 0) {
                                if (profileDetails.getUserprofile().getTelegramNum().getAsJsonPrimitive("number") != null) {
                                    family.setTelgrmCountryCode(profileDetails.getUserprofile().getTelegramNum().getAsJsonPrimitive("countryCode").getAsString());
                                    family.setTelgrmNumber(profileDetails.getUserprofile().getTelegramNum().getAsJsonPrimitive("number").getAsString());
                                }
                            } else {
                                family.setTelgrmNumber(profileDetails.getUserprofile().getPrimaryMobileNo());
                                family.setTelgrmCountryCode(profileDetails.getUserprofile().getCountryCode());
                            }
                            if (countryCode.equalsIgnoreCase("+91")) {
                                if (profileDetails.getUserprofile().getPinCode() != null) {
                                    family.setPincode(profileDetails.getUserprofile().getPinCode());
                                }
                            } else {
                                if (profileDetails.getUserprofile().getCity() != null) {
                                    family.setCity(profileDetails.getUserprofile().getCity());
                                }
                                if (profileDetails.getUserprofile().getState() != null) {
                                    family.setState(profileDetails.getUserprofile().getState());
                                }
                            }
                            family.setAddMember(false);
                            LuserProfileList.add(family);
                            if (LuserProfileList.size() > 0) {

                                if (response.body().size() > 0) {
                                    for (int i = 0; i < response.body().size(); i++) {
                                        FamilyArrayModel family1 = new FamilyArrayModel();
                                        family1.setFirstName(response.body().get(i).getUserProfile().getFirstName());
                                        family1.setLastName(response.body().get(i).getUserProfile().getLastName());
                                        family1.setId(response.body().get(i).getUserProfile().getId());
                                        family1.setGender(response.body().get(i).getUserProfile().getGender());
                                        if (countryCode.equalsIgnoreCase("+91")) {
                                            if (response.body().get(i).getBookingLocation() != null && response.body().get(i).getBookingLocation().getAsJsonObject().get("pincode") != null) {
                                                family1.setPincode(response.body().get(i).getBookingLocation().getAsJsonObject().get("pincode").getAsInt());
                                            }
                                        } else {
                                            if (response.body().get(i).getBookingLocation() != null) {
                                                if (response.body().get(i).getBookingLocation().getAsJsonObject().get("state") != null) {
                                                    family1.setState(response.body().get(i).getBookingLocation().getAsJsonObject().get("state").getAsString());
                                                }
                                                if (response.body().get(i).getBookingLocation().getAsJsonObject().get("district") != null) {
                                                    family1.setCity(response.body().get(i).getBookingLocation().getAsJsonObject().get("district").getAsString());
                                                }
                                            }
                                        }
                                        if (response.body().get(i).getPreferredLanguages() != null) {
                                            family1.setPreferredLanguages(response.body().get(i).getPreferredLanguages());
                                        }
                                        if (response.body().get(i).getUserProfile().getWhatsAppNum() != null && response.body().get(i).getUserProfile().getWhatsAppNum().size() != 0) {
                                            if (response.body().get(i).getUserProfile().getWhatsAppNum().getAsJsonPrimitive("number") != null) {
                                                family1.setWhtsAppCountryCode(response.body().get(i).getUserProfile().getWhatsAppNum().getAsJsonPrimitive("countryCode").getAsString());
                                                family1.setWhtsAppNumber(response.body().get(i).getUserProfile().getWhatsAppNum().getAsJsonPrimitive("number").getAsString());
                                            }
                                        }
                                        if (response.body().get(i).getUserProfile().getTelegramNum() != null && response.body().get(i).getUserProfile().getTelegramNum().size() != 0) {
                                            if (response.body().get(i).getUserProfile().getTelegramNum().getAsJsonPrimitive("number") != null) {
                                                family1.setTelgrmCountryCode(response.body().get(i).getUserProfile().getTelegramNum().getAsJsonPrimitive("countryCode").getAsString());
                                                family1.setTelgrmNumber(response.body().get(i).getUserProfile().getTelegramNum().getAsJsonPrimitive("number").getAsString());
                                            }
                                        }
                                        family1.setAddMember(false);
                                        LuserProfileList.add(family1);
                                    }
                                }
                            }
                            FamilyArrayModel family2 = new FamilyArrayModel();  // model for "Someone else" field
                            family2.setFirstName("Someone");
                            family2.setLastName("else");
                            family2.setAddMember(true);
                            family2.setWhtsAppCountryCode(profileDetails.getUserprofile().getCountryCode());
                            family2.setTelgrmNumber(profileDetails.getUserprofile().getPrimaryMobileNo());
                            family2.setTelgrmCountryCode(profileDetails.getUserprofile().getCountryCode());
                            family2.setWhtsAppNumber(profileDetails.getUserprofile().getPrimaryMobileNo());
                            LuserProfileList.add(family2);

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
                                    // bt_save.setEnabled(true);
                                } else {
                                    bt_save.setBackground(context.getResources().getDrawable(R.drawable.btn_checkin_grey));
                                    bt_save.setTextColor(context.getResources().getColor(R.color.button_grey));
                                    //bt_save.setEnabled(false);
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

                } catch (Exception e) {
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
        JSONObject jsonObj1 = new JSONObject();
        JSONObject jsonObj2 = new JSONObject();
        JSONObject jsonObj3 = new JSONObject();
        JSONArray jsonObj4 = new JSONArray();

        try {
            jsonObj1.put("id", consumerId);
            jsonObj1.put("firstName", profileDetails.getUserprofile().getFirstName());
            jsonObj1.put("lastName", profileDetails.getUserprofile().getLastName());
            if (radio_gender.getCheckedRadioButtonId() != -1) {
                if (radio_gender.getCheckedRadioButtonId() == radioO.getId()) {
                    jsonObj1.put("gender", "other");
                } else if (radio_gender.getCheckedRadioButtonId() == radioM.getId()) {
                    jsonObj1.put("gender", "male");
                } else if (radio_gender.getCheckedRadioButtonId() == radioF.getId()) {
                    jsonObj1.put("gender", "female");
                }
            }
            if (edtWhtsAppNumber.getText() != null && !edtWhtsAppNumber.getText().toString().isEmpty()) {
                jsonObj2.put("countryCode", WhtsappCCodePicker.getSelectedCountryCodeWithPlus());
                jsonObj2.put("number", edtWhtsAppNumber.getText());
                jsonObj1.putOpt("whatsAppNum", jsonObj2);
            }
            if (edtTelegram.getText() != null && !edtTelegram.getText().toString().isEmpty()) {
                jsonObj3.put("countryCode", TelegramCCodePicker.getSelectedCountryCodeWithPlus());
                jsonObj3.put("number", edtTelegram.getText());
                jsonObj1.putOpt("telegramNum", jsonObj3);
            }
            if (countryCode.equalsIgnoreCase("+91")) {
                if (selectedPincode != null) {
                    jsonObj1.putOpt("pinCode", selectedPincode.get("pincode"));
                }
            } else {
                jsonObj1.putOpt("city", et_residing_location.getText().toString());
                jsonObj1.putOpt("state", et_residing_state.getText().toString());
                this.selectedPincode.put("state", et_residing_state.getText().toString());
                this.selectedPincode.put("district", et_residing_location.getText().toString());
                jsonObj1.putOpt("bookingLocation", selectedPincode);
            }

            if (radio_language.getCheckedRadioButtonId() != -1) {
                if (radio_language.getCheckedRadioButtonId() == radioEnglishlng.getId()) {
                    jsonObj4.put("English");
                } else if (radio_language.getCheckedRadioButtonId() == radioOtherlng.getId()) {
                    for (String lang : preferredLanguages) {
                        jsonObj4.put(lang);
                    }
                }
                jsonObj1.putOpt("preferredLanguages", jsonObj4);
            }
            if(cb_email_update_to_myaccount.isChecked()) {
                jsonObj1.put("email", et_email.getText().toString());
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), jsonObj1.toString());
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
                            //iFamilyMemberDetails.sendFamilyMemberDetails(memId, profileDetails.getUserprofile().getFirstName(), profileDetails.getUserprofile().getLastName(), phone, et_email.getText().toString(), countryCode);
                            iFamilyMemberDetails.sendFamilyMemberDetails(memId, profileDetails.getUserprofile().getFirstName(), profileDetails.getUserprofile().getLastName(), phone, et_email.getText().toString(), countryCode, WhtsappCCodePicker.getSelectedCountryCodeWithPlus(), edtWhtsAppNumber.getText().toString(), TelegramCCodePicker.getSelectedCountryCodeWithPlus(), edtTelegram.getText().toString(), et_age.getText().toString(), jsonObj4, selectedPincode, jsonObj1.getString("gender"));

                            //iFamillyListSelected.CheckedFamilyList(checkedfamilyList);
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
        JSONObject jsonObj1 = new JSONObject();
        JSONObject jsonObj2 = new JSONObject();
        JSONObject jsonObj3 = new JSONObject();
        JSONArray jsonObj4 = new JSONArray();
        //ArrayList<String> jsonObj4 = new ArrayList<String>();


        try {
            jsonObj1.put("firstName", et_firstname.getText().toString());
            jsonObj1.put("lastName", et_lastName.getText().toString());
            if (radio_gender.getCheckedRadioButtonId() != -1) {
                if (radio_gender.getCheckedRadioButtonId() == radioO.getId()) {
                    jsonObj1.put("gender", "other");
                } else if (radio_gender.getCheckedRadioButtonId() == radioM.getId()) {
                    jsonObj1.put("gender", "male");
                } else if (radio_gender.getCheckedRadioButtonId() == radioF.getId()) {
                    jsonObj1.put("gender", "female");
                }
            }
            if (edtWhtsAppNumber.getText() != null && !edtWhtsAppNumber.getText().toString().isEmpty()) {
                jsonObj2.put("countryCode", WhtsappCCodePicker.getSelectedCountryCodeWithPlus());
                jsonObj2.put("number", edtWhtsAppNumber.getText());
                jsonObj1.putOpt("whatsAppNum", jsonObj2);
            }
            if (edtTelegram.getText() != null && !edtTelegram.getText().toString().isEmpty()) {
                jsonObj3.put("countryCode", TelegramCCodePicker.getSelectedCountryCodeWithPlus());
                jsonObj3.put("number", edtTelegram.getText());
                jsonObj1.putOpt("telegramNum", jsonObj3);
            }
            if (countryCode.equalsIgnoreCase("+91")) {
                if (selectedPincode != null) {
                    userProfile.putOpt("bookingLocation", selectedPincode);
                }
            } else {
                this.selectedPincode.put("state", et_residing_state.getText().toString());
                this.selectedPincode.put("district", et_residing_location.getText().toString());
                userProfile.putOpt("bookingLocation", selectedPincode);
            }
            if (radio_language.getCheckedRadioButtonId() != -1) {
                if (radio_language.getCheckedRadioButtonId() == radioEnglishlng.getId()) {
                    jsonObj4.put("English");
                } else if (radio_language.getCheckedRadioButtonId() == radioOtherlng.getId()) {
                    for (String lang : preferredLanguages) {
                        jsonObj4.put(lang);
                    }
                }
                userProfile.putOpt("preferredLanguages", jsonObj4);
            }
            if(cb_email_update_to_myaccount.isChecked()) {
                userProfile.put("email", et_email.getText().toString());
            }
            userProfile.putOpt("userProfile", jsonObj1);

            // userProfile.put("id", mUser);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), userProfile.toString());
        Call<Integer> call = apiService.AddFamilyMEmber(body);
        Config.logV("Request--BODY-------------------------" + new Gson().toJson(userProfile.toString()));
        call.enqueue(new Callback<Integer>() {
            @Override
            public void onResponse(Call<Integer> call, Response<Integer> response) {

                try {

                    if (mDialog.isShowing())
                        Config.closeDialog((Activity) context, mDialog);

                    Config.logV("URL---------------" + response.raw().request().url().toString().trim());
                    Config.logV("Response--code-------------------------" + response.code());
                    Config.logV("Request--BODY-------------------------" + new Gson().toJson(response.body()));
                    if (response.code() == 200) {
                        Integer newMemId = response.body();
                        Toast.makeText(context, "Details saved successfully ", Toast.LENGTH_LONG).show();
                        iFamilyMemberDetails.sendFamilyMemberDetails(newMemId, et_firstname.getText().toString(), et_lastName.getText().toString(), phone, et_email.getText().toString(), countryCode, WhtsappCCodePicker.getSelectedCountryCodeWithPlus(), edtWhtsAppNumber.getText().toString(), TelegramCCodePicker.getSelectedCountryCodeWithPlus(), edtTelegram.getText().toString(), et_age.getText().toString(), jsonObj4, selectedPincode, jsonObj1.getString("gender"));

                        //iFamilyMemberDetails.sendFamilyMemberDetails(memId, et_firstname.getText().toString(), et_lastName.getText().toString(), phone, et_email.getText().toString(), countryCode);
                        dismiss();

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
            public void onFailure(Call<Integer> call, Throwable t) {
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
        this.familylist = familylist;
        selectedMemberName = name;
        memId = familylist.getId();
        bt_save.setBackground(context.getResources().getDrawable(R.drawable.curved_save));
        bt_save.setTextColor(context.getResources().getColor(R.color.white));
        bt_save.setEnabled(true);

        updateUi(name, familylist);
    }

    @Override
    public void CheckedFamilyList(List<FamilyArrayModel> familyList) {
        checkedfamilyList = familyList;

    }

    @Override
    public void SelectedPincodeLocation(PincodeLocationsResponse selectedPincodeLocation) {
        try {
            this.selectedPincode.put("pincode", selectedPincodeLocation.getPincode());
            this.selectedPincode.put("state", selectedPincodeLocation.getState());
            this.selectedPincode.put("city", selectedPincodeLocation.getName());

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    public void updateUi(String name, FamilyArrayModel familylist) {
        if (familylist.isAddMember()) {
            ll_add_member.setVisibility(View.VISIBLE);
        } else {
            ll_add_member.setVisibility(View.GONE);
        }
        tv_fName_errormesg.setVisibility(View.GONE);
        tv_lName_errormesg.setVisibility(View.GONE);
        tv_gender_errormesg.setVisibility(View.GONE);
        tv_age_errormesg.setVisibility(View.GONE);
        tv_pin_errormesg.setVisibility(View.GONE);
        if (familylist.getGender() != null && !familylist.getGender().isEmpty()) {
            if (familylist.getGender().equalsIgnoreCase("male")) {
                radio_gender.check(radioM.getId());
            } else if (familylist.getGender().equalsIgnoreCase("female")) {
                radio_gender.check(radioF.getId());
            } else if (familylist.getGender().equalsIgnoreCase("other")) {
                radio_gender.check(radioO.getId());
            }
            gender = familylist.getGender();
        } else {
            radio_gender.clearCheck();
            gender = null;
        }
        if (countryCode.equalsIgnoreCase("+91")) {          /** if phone number is non internatioanal set pincode **/
            if (familylist.getPincode() != null) {
                et_pincode.setText(String.valueOf(familylist.getPincode()));
                try {
                    this.selectedPincode.put("pincode", String.valueOf(familylist.getPincode()));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                et_pincode.setText("");
                while (this.selectedPincode.length() > 0) {
                    this.selectedPincode.remove((String) this.selectedPincode.keys().next());
                }
            }
        } else {                                                        /** if phone number is an international number set location and state **/
            ll_if_interntionl_nmbr.setVisibility(View.VISIBLE);
            ll_residing_pincode.setVisibility(View.GONE);
            try {
                if (familylist.getCity() != null) {
                    et_residing_location.setText(familylist.getCity());
                    this.selectedPincode.put("district", familylist.getCity());
                } else {
                    et_residing_location.setText("");
                }
                if (familylist.getState() != null) {
                    et_residing_state.setText(familylist.getState());
                    this.selectedPincode.put("state", familylist.getState());
                } else {
                    et_residing_state.setText("");
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        if (familylist.getWhtsAppCountryCode() != null && familylist.getWhtsAppNumber() != null) {
            if (!familylist.getWhtsAppCountryCode().equalsIgnoreCase("")) {
                String cCode = familylist.getWhtsAppCountryCode().replace("+", "");
                WhtsappCCodePicker.setCountryForPhoneCode(Integer.parseInt(cCode));
            }
            edtWhtsAppNumber.setText(familylist.getWhtsAppNumber());
        } else {
            if (!countryCode.equalsIgnoreCase("")) {
                String cCode = countryCode.replace("+", "");
                WhtsappCCodePicker.setCountryForPhoneCode(Integer.parseInt(cCode));
            }
            edtWhtsAppNumber.setText(phone);
        }
        if (familylist.getTelgrmCountryCode() != null && familylist.getTelgrmNumber() != null) {
            if (!familylist.getTelgrmCountryCode().equalsIgnoreCase("")) {
                String cCode = familylist.getTelgrmCountryCode().replace("+", "");
                TelegramCCodePicker.setCountryForPhoneCode(Integer.parseInt(cCode));
            }
            edtTelegram.setText(familylist.getTelgrmNumber());
        } else {
            if (!countryCode.equalsIgnoreCase("")) {
                String cCode = countryCode.replace("+", "");
                TelegramCCodePicker.setCountryForPhoneCode(Integer.parseInt(cCode));
            }
            edtTelegram.setText(phone);
        }
        /*if (familylist.getPreferredLanguages() != null) {*/
        if (familylist.getPreferredLanguages() == null || familylist.getPreferredLanguages().isEmpty() || familylist.getPreferredLanguages().stream().anyMatch("english"::equalsIgnoreCase)) {
            radio_language.check(radioEnglishlng.getId());
            ll_languages.setVisibility(View.GONE);
            chooseLanguagesAdapter = new ChooseLanguagesAdapter(context, (Activity) context, new ArrayList<String>());
            preferredLanguages = new ArrayList<String>();

        } else {
            radio_language.check(radioOtherlng.getId());
            tv_languaes.setText(familylist.getPreferredLanguages().toString().replace("[", "").replace("]", ""));
            ll_languages.setVisibility(View.VISIBLE);
            chooseLanguagesAdapter = new ChooseLanguagesAdapter(context, (Activity) context, familylist.getPreferredLanguages());
            preferredLanguages = familylist.getPreferredLanguages();
            //Collections.copy(pincodeLocations, familylist.getPreferredLanguages());
            //preferredLanguages.addAll(familylist.getPreferredLanguages());

        }
        /*} else {
            radio_language.check(radioEnglishlng.getId());
            ll_languages.setVisibility(View.GONE);
            chooseLanguagesAdapter = new ChooseLanguagesAdapter(context, (Activity) context, new ArrayList<String>());
            preferredLanguages = new ArrayList<String>();
        }*/
        et_age.setText("");
        mRecycleChooseLanguages.setVisibility(View.VISIBLE);
        RecyclerView.LayoutManager mChooseLanguagesLayoutManager = new LinearLayoutManager(context);
        mRecycleChooseLanguages.setLayoutManager(mChooseLanguagesLayoutManager);
        mRecycleChooseLanguages.setAdapter(chooseLanguagesAdapter);
        chooseLanguagesAdapter.notifyDataSetChanged();
    }

    private void ApiGetPinLocations(int pin) {
        ApiInterface apiService =
                ApiClient.getClient(context).create(ApiInterface.class);

        final Dialog mDialog = Config.getProgressDialog(context, context.getResources().getString(R.string.dialog_log_in));
        mDialog.show();

        Call<ArrayList<PincodeLocationsResponse>> call = apiService.getPinLocations(pin);
        call.enqueue(new Callback<ArrayList<PincodeLocationsResponse>>() {
            @Override
            public void onResponse(Call<ArrayList<PincodeLocationsResponse>> call, Response<ArrayList<PincodeLocationsResponse>> response) {
                try {
                    if (mDialog.isShowing())
                        Config.closeDialog((Activity) context, mDialog);

                    Config.logV("URL---------------" + response.raw().request().url().toString().trim());
                    Config.logV("Response--code-------------------------" + response.code());
                    if (response.code() == 200) {

                        if (response.body() != null) {
                            pincodeLocations = response.body().get(0).getPostOffice();
                            mRecyclePincode.setVisibility(View.VISIBLE);
                            RecyclerView.LayoutManager mPincodeLayoutManager = new LinearLayoutManager(context);
                            mRecyclePincode.setLayoutManager(mPincodeLayoutManager);
                            mPincodeLocationAdapter = new PincodeLocationsAdapter(context, (Activity) context, pincodeLocations, iFamillyListSelected);
                            mRecyclePincode.setAdapter(mPincodeLocationAdapter);
                            mPincodeLocationAdapter.notifyDataSetChanged();
                        }
                    } else if (response.code() == 500) {
                        tv_pin_errormesg.setText("Please provide a valid pincode");
                        tv_pin_errormesg.setVisibility(View.VISIBLE);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ArrayList<PincodeLocationsResponse>> call, Throwable t) {
                // Log error here since request failed
                Config.logV("Fail---------------" + t.toString());
                if (mDialog.isShowing())
                    Config.closeDialog((Activity) context, mDialog);

            }
        });

    }

    public void chooseOtherLanguages() {
        if (radioOtherlng.isChecked()) {

            ll_changeMember.setVisibility(View.GONE);
            ll_changeMember.startAnimation(slideRight);
            ll_chooselanguages.setVisibility(View.VISIBLE);
            ll_chooselanguages.startAnimation(slideUp);
            ivClose.setVisibility(View.GONE);
            tv_errorChooseLanguage.setVisibility(View.GONE);

        }
    }

    private void ApiUpdateFamilyMember() {


        ApiInterface apiService =
                ApiClient.getClient(context).create(ApiInterface.class);

        //  final int consumerId = SharedPreference.getInstance(mContext).getIntValue("consumerId", 0);

        final Dialog mDialog = Config.getProgressDialog(context, context.getResources().getString(R.string.dialog_log_in));
        mDialog.show();
        JSONObject userProfile = new JSONObject();
        JSONObject jsonObj1 = new JSONObject();
        JSONObject jsonObj2 = new JSONObject();
        JSONObject jsonObj3 = new JSONObject();
        JSONArray jsonObj4 = new JSONArray();
        //ArrayList<String> jsonObj4 = new ArrayList<String>();


        try {
            jsonObj1.put("firstName", familylist.getFirstName());
            jsonObj1.put("lastName", familylist.getLastName());
            jsonObj1.put("id", familylist.getId());
            if (radio_gender.getCheckedRadioButtonId() != -1) {
                if (radio_gender.getCheckedRadioButtonId() == radioO.getId()) {
                    jsonObj1.put("gender", "other");
                } else if (radio_gender.getCheckedRadioButtonId() == radioM.getId()) {
                    jsonObj1.put("gender", "male");
                } else if (radio_gender.getCheckedRadioButtonId() == radioF.getId()) {
                    jsonObj1.put("gender", "female");
                }
            }
            if (edtWhtsAppNumber.getText() != null && !edtWhtsAppNumber.getText().toString().isEmpty()) {
                jsonObj2.put("countryCode", WhtsappCCodePicker.getSelectedCountryCodeWithPlus());
                jsonObj2.put("number", edtWhtsAppNumber.getText());
                jsonObj1.putOpt("whatsAppNum", jsonObj2);
            }
            if (edtTelegram.getText() != null && !edtTelegram.getText().toString().isEmpty()) {
                jsonObj3.put("countryCode", TelegramCCodePicker.getSelectedCountryCodeWithPlus());
                jsonObj3.put("number", edtTelegram.getText());
                jsonObj1.putOpt("telegramNum", jsonObj3);
            }
            if (countryCode.equalsIgnoreCase("+91")) {
                if (selectedPincode != null) {
                    userProfile.putOpt("bookingLocation", selectedPincode);
                }
            } else {
                this.selectedPincode.put("state", et_residing_state.getText().toString());
                this.selectedPincode.put("district", et_residing_location.getText().toString());
                userProfile.putOpt("bookingLocation", selectedPincode);
            }
            if (radio_language.getCheckedRadioButtonId() != -1) {
                if (radio_language.getCheckedRadioButtonId() == radioEnglishlng.getId()) {
                    jsonObj4.put("English");
                } else if (radio_language.getCheckedRadioButtonId() == radioOtherlng.getId()) {
                    for (String lang : preferredLanguages) {
                        jsonObj4.put(lang);
                    }
                }
                userProfile.putOpt("preferredLanguages", jsonObj4);
            }
            if(cb_email_update_to_myaccount.isChecked()) {
                jsonObj1.put("email", et_email.getText().toString());
            }
            userProfile.putOpt("userProfile", jsonObj1);

            // userProfile.put("id", mUser);
        } catch (JSONException e) {
            e.printStackTrace();
        }


        RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), userProfile.toString());
        Call<ResponseBody> call = apiService.UpdateFamilyMEmber(body);
        //  Config.logV("Request--BODY-------------------------" + new Gson().toJson(userProfile.toString()));
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                try {

                    if (mDialog.isShowing())
                        Config.closeDialog((Activity) context, mDialog);

                    Config.logV("URL---------------" + response.raw().request().url().toString().trim());
                    Config.logV("Response--code-------------------------" + response.code());
                    //   Config.logV("Request--BODY-------------------------" + new Gson().toJson(response.body()));
                    if (response.code() == 200) {
                        Toast.makeText(context, "Details saved successfully ", Toast.LENGTH_LONG).show();
                        iFamilyMemberDetails.sendFamilyMemberDetails(memId, familylist.getFirstName(), familylist.getLastName(), phone, et_email.getText().toString(), countryCode, WhtsappCCodePicker.getSelectedCountryCodeWithPlus(), edtWhtsAppNumber.getText().toString(), TelegramCCodePicker.getSelectedCountryCodeWithPlus(), edtTelegram.getText().toString(), et_age.getText().toString(), jsonObj4, selectedPincode, jsonObj1.getString("gender"));
                        dismiss();
                        //Toast.makeText(context, "Member updated successfully ", Toast.LENGTH_LONG).show();

                    } else {
                        Toast.makeText(context, response.errorBody().string(), Toast.LENGTH_LONG).show();
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
}
