package com.jaldeeinc.jaldee.activities;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.BottomSheetDialog;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.jaldeeinc.jaldee.R;
import com.jaldeeinc.jaldee.adapter.CouponlistAdapter;
import com.jaldeeinc.jaldee.adapter.MultipleFamilyMemberAdapter;
import com.jaldeeinc.jaldee.common.Config;
import com.jaldeeinc.jaldee.connection.ApiClient;
import com.jaldeeinc.jaldee.connection.ApiInterface;
import com.jaldeeinc.jaldee.custom.CustomTypefaceSpan;
import com.jaldeeinc.jaldee.model.FamilyArrayModel;
import com.jaldeeinc.jaldee.payment.PaymentGateway;
import com.jaldeeinc.jaldee.payment.PaytmPayment;
import com.jaldeeinc.jaldee.response.CheckSumModel;
import com.jaldeeinc.jaldee.response.CoupnResponse;
import com.jaldeeinc.jaldee.response.PaymentModel;
import com.jaldeeinc.jaldee.response.ProfileModel;
import com.jaldeeinc.jaldee.response.QueueTimeSlotModel;
import com.jaldeeinc.jaldee.response.SearchDepartment;
import com.jaldeeinc.jaldee.response.SearchService;
import com.jaldeeinc.jaldee.response.SearchSetting;
import com.jaldeeinc.jaldee.response.SearchTerminology;
import com.jaldeeinc.jaldee.response.SearchUsers;
import com.jaldeeinc.jaldee.response.SearchViewDetail;
import com.jaldeeinc.jaldee.response.SectorCheckin;
import com.jaldeeinc.jaldee.utils.SharedPreference;
import com.payumoney.core.PayUmoneyConfig;
import com.payumoney.core.PayUmoneySdkInitializer;
import com.payumoney.core.entity.TransactionResponse;
import com.payumoney.sdkui.ui.utils.PayUmoneyFlowManager;
import com.payumoney.sdkui.ui.utils.ResultModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by sharmila on 6/8/18.
 */

public class CheckIn extends AppCompatActivity {

    ArrayList<String> couponArraylist = new ArrayList<String>();

    String phoneNumber;
    int providerId;
    String value = null;
    static TextView tv_personahead;
    static Context mContext;
    static Activity mActivity;
    Spinner mSpinnerService, mSpinnerDepartment;
    static int serviceId;
    ArrayList<SearchService> LServicesList = new ArrayList<>();
    ArrayList<SearchService> gServiceList = new ArrayList<>();
    String uniqueID;
    String uuid;
    TextView tv_addmember, tv_editphone;
    String accountID;
    static int mSpinnertext;
    boolean livetrack;
    static int deptSpinnertext;
    static ArrayList<QueueTimeSlotModel> mQueueTimeSlotList = new ArrayList<>();
    ArrayList<PaymentModel> mPaymentData = new ArrayList<>();
    static String modifyAccountID;
    boolean isPrepayment;
    TextView tv_amount;
    String sAmountPay;
    static TextView tv_name;
    String mFirstName, mLastName;
    int consumerID;
    static TextView tv_waittime;
    static TextView tv_queue;
    static TextView txt_date;
    ImageView img_calender_checkin;
    LinearLayout LcheckinDatepicker;
    static String mFrom;
    String title, place, terminology,calcMode;
    static String isShowToken;
    TextView tv_titlename, tv_place, tv_checkin_service, txtprepay;
    static ImageView ic_left, ic_right;
    static TextView tv_queuetime;
//    static TextView tv_queuename;
    static LinearLayout queuelayout;
    String toastMessage;
    TextView txt_chooseservice, txt_choosedepartment;
    static int i = 0;
    static ImageView ic_cal_minus;
    ImageView ic_cal_add;
    Button btn_checkin;
    static int queueId = 0;
    EditText couponEdit, phoneNumberValue;
    Button applycouponbtn;
    ArrayList<CoupnResponse> s3couponList = new ArrayList<>();
    TextView coupon_link;
    String couponEntered;
    TextView mtermsandCond;
    TextView mtxtTermsandCondition;
    TextView mtxtDele;
    TextView mtermsAndConditionDetail;
    int selectedService;
    int selectedDepartment;
    static String selectedDateFormat;
    String serviceSelected;
    String departmentSelected;
    TextView tv_addnote, txtprepayamount;
    static TextView txtnocheckin;
    TextView tv_title;
    String txt_message = "";
    String googlemap;
    String sector, subsector;
    LinearLayout layout_party, LservicePrepay, LcouponCheckin;
    EditText editpartysize;
    int maxPartysize;
    static RecyclerView recycle_family;
    static LinearLayout LSinglemember, Lbottomlayout;
    RecyclerView list;
    private CouponlistAdapter mAdapter;
    static String Word_Change = "";
    SearchDepartment depResponse;
    String displayNotes;
    String getAvail_date;
    TextView tv_attach, tv_camera;
    private static final String IMAGE_DIRECTORY = "/Jaldee" +
            "";
    private int GALLERY = 1, CAMERA = 2;
    RecyclerView recycle_image_attachment;

    String[] imgExtsSupported = new String[]{"jpg", "jpeg", "png"};
    String[] fileExtsSupported = new String[]{"jpg", "jpeg", "png", "pdf"};
    ArrayList<String> imagePathList = new ArrayList<>();
    private Uri mImageUri;
    File f;
    String path;
    Bitmap bitmap;
    Boolean isShow;
    String deptId;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.checkin);

        list = findViewById(R.id.list);
        tv_personahead = findViewById(R.id.txt_personahead);
        mtermsAndConditionDetail = findViewById(R.id.termsAndConditionDetail);
        mtermsandCond = findViewById(R.id.termsandCond);
        mtxtTermsandCondition = findViewById(R.id.txtTermsandCondition);
        mtxtDele = findViewById(R.id.txtDele);
        coupon_link = findViewById(R.id.coupon_link);
        mtxtTermsandCondition = findViewById(R.id.txtTermsandCondition);
        mtxtDele.setVisibility(View.INVISIBLE);
        mtermsandCond.setVisibility(View.INVISIBLE);
        mtxtTermsandCondition.setVisibility(View.INVISIBLE);
        couponEdit = findViewById(R.id.coupon_edit);
        phoneNumberValue = findViewById(R.id.phoneNumberValue);
        applycouponbtn = findViewById(R.id.applybtn);
        LcouponCheckin = findViewById(R.id.couponCheckin);
        mActivity = this;
        recycle_family = findViewById(R.id.recycle_family);
        btn_checkin = findViewById(R.id.btn_checkin);
        editpartysize = findViewById(R.id.editpartysize);
        LSinglemember = findViewById(R.id.familymember);
        LservicePrepay = findViewById(R.id.LservicePrepay);
        txtprepayamount = findViewById(R.id.txtprepayamount);
        txtprepay = findViewById(R.id.txtprepay);
        LSinglemember.setVisibility(View.VISIBLE);
        recycle_family.setVisibility(View.GONE);
        queuelayout = findViewById(R.id.queuelayout);
        txt_chooseservice = findViewById(R.id.txt_chooseservice);
        txt_choosedepartment = findViewById(R.id.txt_choosedepartment);
        layout_party = findViewById(R.id.layout_party);
        Lbottomlayout = findViewById(R.id.bottomlayout);
        tv_amount = findViewById(R.id.txtamount);
        txtnocheckin = findViewById(R.id.txtnocheckin);
        tv_name = findViewById(R.id.txtname);
        tv_waittime = findViewById(R.id.txt_waittime);
        txt_date = findViewById(R.id.txt_date);
        img_calender_checkin = findViewById(R.id.calender_checkin);
        LcheckinDatepicker = findViewById(R.id.checkinDatepicker);
        tv_queue = findViewById(R.id.txt_queue);
        tv_place = findViewById(R.id.txt_place);
        tv_titlename = findViewById(R.id.txt_title);
        tv_checkin_service = findViewById(R.id.txt_checkin_service);
        tv_queuetime = findViewById(R.id.txt_queuetime);
//        tv_queuename = findViewById(R.id.txt_queuename);
        tv_addnote = findViewById(R.id.txtaddnote);
        mSpinnerService = findViewById(R.id.spinnerservice);
        mSpinnerDepartment = findViewById(R.id.spinnerdepartment);

        tv_addnote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final BottomSheetDialog dialog = new BottomSheetDialog(mContext, R.style.DialogStyle);
                dialog.setContentView(R.layout.reply);
                dialog.show();

                final Button btn_send = dialog.findViewById(R.id.btn_send);
                Button btn_cancel = dialog.findViewById(R.id.btn_cancel);
                final EditText edt_message = dialog.findViewById(R.id.edt_message);
                TextView txtsendmsg = dialog.findViewById(R.id.txtsendmsg);
                txtsendmsg.setVisibility(View.GONE);
                btn_send.setText("ADD");


//                tv_attach = dialog.findViewById(R.id.btn);
//                tv_camera = dialog.findViewById(R.id.camera);
//                recycle_image_attachment = dialog.findViewById(R.id.recycler_view_image);
//
//                tv_attach.setVisibility(View.VISIBLE);
//                tv_camera.setVisibility(View.VISIBLE);
//
//                tv_attach.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        try {
//                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//                                if ((ContextCompat.checkSelfPermission(mContext, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) && ContextCompat.checkSelfPermission(mContext, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
//
//                                    //requestPermissions(getActivity(), new String[]{Manifest.permission.CALL_PHONE}, CALL_REQUEST);
//
//                                    requestPermissions(new String[]{
//                                            Manifest.permission.READ_EXTERNAL_STORAGE}, GALLERY);
//
//                                    return;
//                                } else {
//                                    Intent intent = new Intent();
//                                    intent.setType("*/*");
//                                    intent.setAction(Intent.ACTION_GET_CONTENT);
//                                    startActivityForResult(Intent.createChooser(intent, "Select Picture"), GALLERY);
//                                }
//                            } else {
//
//                                Intent intent = new Intent();
//                                intent.setType("*/*");
//                                intent.setAction(Intent.ACTION_GET_CONTENT);
//                                startActivityForResult(Intent.createChooser(intent, "Select Picture"), GALLERY);
//                            }
//                        } catch (Exception ex) {
//                            ex.printStackTrace();
//                        }
//                    }
//
//                });
//
//                tv_camera.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        try {
//                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//                                if (ContextCompat.checkSelfPermission(mContext, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
//
//                                    //requestPermissions(getActivity(), new String[]{Manifest.permission.CALL_PHONE}, CALL_REQUEST);
//
//                                    requestPermissions(new String[]{
//                                            Manifest.permission.CAMERA}, CAMERA);
//
//                                    return;
//                                } else {
//                                    Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
//                                    Intent cameraIntent = new Intent();
//                                    cameraIntent.setType("image/*");
//                                    cameraIntent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
//                                    cameraIntent.setAction(Intent.ACTION_GET_CONTENT);
//                                    startActivityForResult(intent, CAMERA);
//                                }
//                            } else {
//
//                                Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
//                                Intent cameraIntent = new Intent();
//                                cameraIntent.setType("image/*");
//                                cameraIntent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
//                                cameraIntent.setAction(Intent.ACTION_GET_CONTENT);
//                                startActivityForResult(intent, CAMERA);
//                            }
//                        } catch (Exception ex) {
//                            ex.printStackTrace();
//                        }
//                    }
//
//                });


                if (!txtsendmsg.equals("")) {
                    edt_message.setText(txt_message);
                }


                btn_send.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        txt_message = edt_message.getText().toString();
                        dialog.dismiss();

                    }
                });

                btn_cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });

                edt_message.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void afterTextChanged(Editable arg0) {
                        if (edt_message.getText().toString().length() >= 1 && !edt_message.getText().toString().trim().isEmpty()) {
                            btn_send.setEnabled(true);
                            btn_send.setClickable(true);
                            btn_send.setBackground(mContext.getResources().getDrawable(R.drawable.roundedrect_blue));
                        } else {
                            btn_send.setEnabled(true);
                            btn_send.setClickable(true);
                            btn_send.setBackground(mContext.getResources().getDrawable(R.drawable.roundedrect_blue));
                          //  btn_send.setBackground(mContext.getResources().getDrawable(R.drawable.btn_checkin_grey));
                        }
                    }

                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                    }
                });
            }
        });

        ic_left = findViewById(R.id.ic_left);
        ic_right = findViewById(R.id.ic_right);
        ic_cal_minus = findViewById(R.id.ic_cal_minus);
        ic_cal_add = findViewById(R.id.ic_cal_add);

        ic_cal_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String dtStart = txt_date.getText().toString();
                Config.logV("Date----------------" + dtStart);
                Date date = null;
                SimpleDateFormat format = new SimpleDateFormat("EEE, dd/MM/yyyy");
                try {
                    date = format.parse(dtStart);

                } catch (ParseException e) {
                    e.printStackTrace();
                }
                Date added_date = addDays(date, 1);
                DateFormat dateFormat = new SimpleDateFormat("EEE, dd/MM/yyyy");
                //to convert Date to String, use format method of SimpleDateFormat class.
                String strDate = dateFormat.format(added_date);
                txt_date.setText(strDate);

                DateFormat selecteddateParse = new SimpleDateFormat("yyyy-MM-dd");
                selectedDateFormat = selecteddateParse.format(added_date);
                UpdateDAte(selectedDateFormat);
            }
        });

        ic_cal_minus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String dtStart = txt_date.getText().toString();
                Config.logV("Date----------------" + dtStart);
                Date date = null;
                SimpleDateFormat format = new SimpleDateFormat("EEE, dd/MM/yyyy");
                try {
                    date = format.parse(dtStart);

                } catch (ParseException e) {
                    e.printStackTrace();
                }

                Date added_date = subtractDays(date, 1);
                DateFormat dateFormat = new SimpleDateFormat("EEE, dd/MM/yyyy");
                //to convert Date to String, use format method of SimpleDateFormat class.
                String strDate = dateFormat.format(added_date);
                txt_date.setText(strDate);
                DateFormat selecteddateParse = new SimpleDateFormat("yyyy-MM-dd");
                selectedDateFormat = selecteddateParse.format(added_date);
                UpdateDAte(selectedDateFormat);
                //  UpdateDAte(strDate);
            }
        });

        ImageView iBackPress = findViewById(R.id.backpress);
        iBackPress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // what do you want here
                finish();
            }
        });
        tv_title = findViewById(R.id.toolbartitle);
        tv_title.setText("Check-in");

        Typeface tyface = Typeface.createFromAsset(getAssets(),
                "fonts/Montserrat_Bold.otf");
        tv_title.setTypeface(tyface);

        tv_titlename.setTypeface(tyface);
        tv_name.setTypeface(tyface);
        btn_checkin.setTypeface(tyface);
//        tv_queuename.setTypeface(tyface);
        txt_date.setTypeface(tyface);

        mContext = this;
        mActivity = this;

        int consumerId = SharedPreference.getInstance(mContext).getIntValue("consumerId", 0);
        Config.logV("Consumer ID------------" + consumerId);
        familyMEmID = consumerId;


        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            mFrom = extras.getString("from", "");
            Config.logV("From-------------" + mFrom);
            if (mFrom.equalsIgnoreCase("favourites") || mFrom.equalsIgnoreCase("favourites_date")) {
                serviceId = extras.getInt("serviceId");
                uniqueID = extras.getString("uniqueID");
                accountID = extras.getString("accountID");
                modifyAccountID = accountID;
                title = extras.getString("title", "");
                place = extras.getString("place", "");
                terminology = extras.getString("terminology", "");
                isShowToken = extras.getString("isShowToken", "");
                getAvail_date = extras.getString("getAvail_date", "");
                ApiSearchViewDetail(uniqueID);
            } else {

                serviceId = extras.getInt("serviceId");
                uniqueID = extras.getString("uniqueID");
                accountID = extras.getString("accountID");
                /* mFrom = extras.getString("from", "");*/
                if (mFrom.equalsIgnoreCase("searchdetail_future") || mFrom.equalsIgnoreCase("searchdetail_checkin")) {
                    modifyAccountID = accountID;
                } else {
                    modifyAccountID = accountID.substring(0, accountID.indexOf("-"));
                }
                googlemap = extras.getString("googlemap", "");
                title = extras.getString("title", "");
                place = extras.getString("place", "");
                sector = extras.getString("sector", "");
                subsector = extras.getString("subsector", "");
                terminology = extras.getString("terminology", "");
                isShowToken = extras.getString("isShowToken", "");
                getAvail_date = extras.getString("getAvail_date", "");

            }
        }
        if (sector != null && subsector != null) {
            APISector(sector, subsector);
        }
        tv_place.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mFrom.equalsIgnoreCase("searchdetail_future") || mFrom.equalsIgnoreCase("searchdetail_checkin")) {
                    String geoUri = googlemap;
                    try {
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(geoUri));
                        mContext.startActivity(intent);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    String geoUri = "http://maps.google.com/maps?q=loc:" + googlemap;
                    try {
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(geoUri));
                        mContext.startActivity(intent);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        if (googlemap != null) {
            if (!googlemap.equalsIgnoreCase("") && googlemap != null) {

                tv_place.setVisibility(View.VISIBLE);
            }
        } else {
            tv_place.setVisibility(View.GONE);
        }

        tv_place.setText(place);

        tv_titlename.setText(title);


        btn_checkin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // ApiGenerateHash();
                if (phoneNumberValue.length() < 10) {
                    Toast.makeText(mContext, "Mobile number should have 10 digits" + "",Toast.LENGTH_SHORT).show();
                } else {

                    if (enableparty) {
                        if (Integer.parseInt(editpartysize.getText().toString()) > maxPartysize) {
                            Toast.makeText(mContext, "Sorry, Max party size allowed is " + maxPartysize, Toast.LENGTH_LONG).show();
                        } else {
                            ApiCheckin(txt_message);
                        }
                    } else {
                        ApiCheckin(txt_message);
                    }
                }
            }
        });
        ApiSearchViewSetting(uniqueID);
        ApiSearchViewTerminology(uniqueID);
        ApiGetProfileDetail();
        ApiGetProfileDetail();
        mFirstName = SharedPreference.getInstance(mContext).getStringValue("firstname", "");
        mLastName = SharedPreference.getInstance(mContext).getStringValue("lastname", "");
        consumerID = SharedPreference.getInstance(mContext).getIntValue("consumerId", 0);
        tv_name.setText(mFirstName + " " + mLastName);
        tv_addmember = findViewById(R.id.txtaddmember);
        //  tv_editphone = findViewById(R.id.txteditphone);


        tv_addmember.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent iFamily = new Intent(v.getContext(), CheckinFamilyMember.class);
                iFamily.putExtra("firstname", mFirstName);
                iFamily.putExtra("lastname", mLastName);
                iFamily.putExtra("consumerID", consumerID);
                iFamily.putExtra("multiple", multiplemem);
                iFamily.putExtra("memberID", familyMEmID);
                iFamily.putExtra("update", 0);
                Config.logV("multiplemem---------------------" + multiplemem);
                startActivity(iFamily);
            }
        });

//        tv_editphone.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                phoneNumberValue.setEnabled(true);
//            }
//        });

        mSpinnerService.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                mSpinnertext = ((SearchService) mSpinnerService.getSelectedItem()).getId();
                livetrack = (((SearchService) mSpinnerService.getSelectedItem()).isLivetrack());
                Log.i("vbnvbnvbn", String.valueOf(mSpinnertext));
                Log.i("lkjjkllkjjkl", String.valueOf(livetrack));

                serviceSelected = ((SearchService) mSpinnerService.getSelectedItem()).getName();
                selectedService = ((SearchService) mSpinnerService.getSelectedItem()).getId();

                // String firstWord = "Check-in for ";
                String firstWord = Word_Change;
                String secondWord = ((SearchService) mSpinnerService.getSelectedItem()).getName();
                Spannable spannable = new SpannableString(firstWord + secondWord);
                Typeface tyface1 = Typeface.createFromAsset(mContext.getAssets(),
                        "fonts/Montserrat_Bold.otf");
                spannable.setSpan(new CustomTypefaceSpan("sans-serif", tyface1), firstWord.length(), firstWord.length() + secondWord.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                spannable.setSpan(new ForegroundColorSpan(mContext.getResources().getColor(R.color.title_grey)), 0, firstWord.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                spannable.setSpan(new ForegroundColorSpan(mContext.getResources().getColor(R.color.title_consu)), firstWord.length(), firstWord.length() + secondWord.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                tv_checkin_service.setText(spannable);

                Date currentTime = new Date();
                final SimpleDateFormat sdf = new SimpleDateFormat(
                        "yyyy-MM-dd", Locale.US);
                sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
                System.out.println("UTC time: " + sdf.format(currentTime));

                if (mFrom.equalsIgnoreCase("checkin") || mFrom.equalsIgnoreCase("searchdetail_checkin") || mFrom.equalsIgnoreCase("favourites")) {

                    ApiQueueTimeSlot(String.valueOf(serviceId), String.valueOf(mSpinnertext), modifyAccountID, sdf.format(currentTime), isShowToken);
                } else {
                    if (selectedDateFormat != null) {
                        Config.logV("SELECTED @@@@@@@@@@@@@@@@@@@@@@@@@@@@");
                        ApiQueueTimeSlot(String.valueOf(serviceId), String.valueOf(mSpinnertext), modifyAccountID, selectedDateFormat, isShowToken);
                    } else {
                        Config.logV("SELECTED @@@@@@@@@@@@@@@@@@@@@@@@@@@@************");
                        ApiQueueTimeSlot(String.valueOf(serviceId), String.valueOf(mSpinnertext), modifyAccountID, sdf.format(currentTime), isShowToken);
                    }
                }


                isPrepayment = ((SearchService) mSpinnerService.getSelectedItem()).isPrePayment();
                Config.logV("Payment------------" + isPrepayment);
                if (isPrepayment) {

                    sAmountPay = ((SearchService) mSpinnerService.getSelectedItem()).getMinPrePaymentAmount();

                    Config.logV("Payment----sAmountPay--------" + sAmountPay);
                    APIPayment(modifyAccountID);

                } else {
                    LservicePrepay.setVisibility(View.GONE);
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }


        });
        mSpinnerDepartment.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                deptSpinnertext = depResponse.getDepartments().get(position).getDepartmentId();
                Log.i("dfgdfg", String.valueOf(deptSpinnertext));

                departmentSelected = depResponse.getDepartments().get(position).getDepartmentName();
                selectedDepartment = depResponse.getDepartments().get(position).getDepartmentId();
                ApiSearchUsers(selectedDepartment);
                ArrayList<Integer> serviceIds = depResponse.getDepartments().get(position).getServiceIds();
                ArrayList<SearchService> serviceList = new ArrayList<>();
                for (int serviceIndex = 0; serviceIndex < serviceIds.size(); serviceIndex++) {

                    for (int i = 0; i < gServiceList.size(); i++) {
                        if (serviceIds.get(serviceIndex) == gServiceList.get(i).getId()) {
                            serviceList.add(gServiceList.get(i));
                            break;
                        }
                    }

                }
                LServicesList.clear();
                LServicesList.addAll(serviceList);
                if (LServicesList.size() == 0) {
                    mSpinnerService.setVisibility(View.GONE);
                    btn_checkin.setVisibility(View.GONE);
                    txt_chooseservice.setVisibility(View.GONE);
                    Toast.makeText(CheckIn.this, "The selected department doesn't contain any services for this location", Toast.LENGTH_SHORT).show();
                } else {
                    ArrayAdapter<SearchService> adapter = new ArrayAdapter<SearchService>(mActivity, android.R.layout.simple_spinner_dropdown_item, LServicesList);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    mSpinnerService.setAdapter(adapter);
                    mSpinnertext = ((SearchService) LServicesList.get(0)).getId();
                    livetrack = LServicesList.get(0).isLivetrack();
                    mSpinnerService.setVisibility(View.VISIBLE);
                    txt_chooseservice.setVisibility(View.VISIBLE);
                    btn_checkin.setVisibility(View.VISIBLE);

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }


        });

        if (mFrom.equalsIgnoreCase("checkin") || mFrom.equalsIgnoreCase("searchdetail_checkin") || mFrom.equalsIgnoreCase("favourites")) {
            LcheckinDatepicker.setVisibility(View.GONE);
        } else {
            LcheckinDatepicker.setVisibility(View.VISIBLE);

            Date currentTime = new Date();
            final SimpleDateFormat sdf = new SimpleDateFormat("EEE, dd/MM/yyyy");
            sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
            System.out.println("UTC time: " + sdf.format(currentTime));
            Date added_date = addDays(currentTime, 1);
            DateFormat dateFormat = new SimpleDateFormat("EEE, dd/MM/yyyy");
            //to convert Date to String, use format method of SimpleDateFormat class.



            String strDate = dateFormat.format(added_date);
            /* txt_date.setText(sdf.format(currentTime));*/
            txt_date.setText(strDate);
            DateFormat selecteddateParse = new SimpleDateFormat("yyyy-MM-dd");
            // selectedDateFormat = selecteddateParse.format(currentTime);
            selectedDateFormat = selecteddateParse.format(added_date);
            UpdateDAte(selectedDateFormat);
        }

        img_calender_checkin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment newFragment = new MyDatePickerDialog();
                newFragment.show(getSupportFragmentManager(), "date picker");
            }
        });
        Bundle extrasnew = getIntent().getExtras();
        if (extrasnew != null) {
            uniqueID = extras.getString("uniqueID");
            ApiJaldeegetS3Coupons(uniqueID);
        }




        coupon_link.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    LcouponCheckin.setVisibility(View.VISIBLE);
            }
        });


        applycouponbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                couponEntered = couponEdit.getEditableText().toString();

                boolean found = false;
                for (int index = 0; index < couponArraylist.size(); index++) {
                    if (couponArraylist.get(index).equals(couponEntered)) {
                        found = true;
                        break;
                    }
                }
                if (found) {

                    Toast.makeText(CheckIn.this, "Coupon already added", Toast.LENGTH_SHORT).show();

                    return;
                }
                found = false;
                for (int i = 0; i < s3couponList.size(); i++) {
                    if (s3couponList.get(i).getJaldeeCouponCode().equals(couponEntered)) {
                        found = true;
                        break;
                    }
                }
                if (found) {

                    couponArraylist.add(couponEntered);

                    couponEdit.setText("");
                    Toast.makeText(CheckIn.this, couponEntered + " " + "Added", Toast.LENGTH_SHORT).show();


                } else {
                    if (couponEntered.equals("")) {
                        Toast.makeText(CheckIn.this, "Enter a coupon", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(CheckIn.this, "Coupon Invalid", Toast.LENGTH_SHORT).show();
                    }


                }
                Config.logV("couponArraylist--code-------------------------" + couponArraylist);
                list.setVisibility(View.VISIBLE);
                RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(mContext);
                list.setLayoutManager(mLayoutManager);
                mAdapter = new CouponlistAdapter(mContext, s3couponList, couponEntered, couponArraylist);
                list.setAdapter(mAdapter);
                mAdapter.notifyDataSetChanged();
            }
        });

        mtxtDele.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mtermsandCond.setVisibility(View.INVISIBLE);
                mtxtDele.setVisibility(View.INVISIBLE);
                mtxtTermsandCondition.setVisibility(View.INVISIBLE);
                couponEdit.setText("");
                mtermsAndConditionDetail.setVisibility(View.INVISIBLE);

            }
        });
        mtxtTermsandCondition.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mtermsAndConditionDetail.setVisibility(mtermsAndConditionDetail.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE);
            }
        });
    }


//    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//
//        if (resultCode == this.RESULT_CANCELED) {
//            return;
//        }
//        if (requestCode == GALLERY) {
//            if (data != null) {
//                try {
//                    if (data.getData() != null) {
//                        Uri uri = data.getData();
//                        String filepath = "";//default fileName
//                        //Uri filePathUri = uri;
//                        File file;
//                        String mimeType = this.mContext.getContentResolver().getType(uri);
//                        String uriString = uri.toString();
//                        String extension = "";
//                        if (uriString.contains(".")) {
//                            extension = uriString.substring(uriString.lastIndexOf(".") + 1);
//                        }
//
//                        if (Arrays.asList(imgExtsSupported).contains(extension)) {
//                            try {
//                                file = new File(new URI(uri.toString()));
//                                if (file.exists())
//                                    filepath = file.getAbsolutePath();
//
//                            } catch (URISyntaxException e) {
//                                e.printStackTrace();
//                            }
//                        } else {
//                            extension = mimeType.substring(mimeType.lastIndexOf("/") + 1);
//                            if (Arrays.asList(fileExtsSupported).contains(extension)) {
//                                filepath = getFilePathFromURI(this.mContext, uri, extension);
//                            } else {
//                                Toast.makeText(mContext, "File type not supported", Toast.LENGTH_SHORT).show();
//                                return;
//                            }
//                        }
//
////                        if (uri.getScheme().toString().compareTo("external/images/media") == 0) {
////
//////                            String[] projection = { MediaStore.Images.Media.DATA };
//////                            Cursor cursor = this.mContext.getContentResolver().query(uri, projection, null, null, null);
//////                            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
//////
//////                            cursor.moveToFirst();
//////                            String mImagePath = cursor.getString(column_index);
//////                            cursor.close();
//////                            }
////                            filepath = getFilePathFromURI(this.mContext, uri);
////                        } else if (uri.getScheme().compareTo("file") == 0) {
////                            try {
////                                file = new File(new URI(uri.toString()));
////                                if (file.exists())
////                                    filepath = file.getAbsolutePath();
////
////                            } catch (URISyntaxException e) {
////                                e.printStackTrace();
////                            }
////                        }  else {
////                            filepath = uri.getPath();
////                        }
//                        imagePathList.add(filepath);
////                        Uri mImageUri = data.getData();
////                        filePath = data.getData().getPath();
////                        String ext1 = FilenameUtils.getExtension(filePath);
////
////
////                        imagePathList.add(mImageUri.toString());
////             //         bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), mImageUri);
////                       // if(bitmap!=null){
////                    //    path = saveImage(bitmap);}
////
////                      //  else{
////                            path = getRealFilePath(mImageUri);
////                     //   }
//
//
//                        DetailFileAdapter mDetailFileAdapter = new DetailFileAdapter(imagePathList, mContext);
//                        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(this, 3);
//                        recycle_image_attachment.setLayoutManager(mLayoutManager);
//                        recycle_image_attachment.setAdapter(mDetailFileAdapter);
//
//                        mDetailFileAdapter.notifyDataSetChanged();
//
//                    }
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
//
//        } else if (requestCode == CAMERA) {
//            Bitmap bitmap = (Bitmap) data.getExtras().get("data");
//            //      imageview.setImageBitmap(bitmap);
//            path = saveImage(bitmap);
//            // imagePathList.add(bitmap.toString());
//            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
//            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
//            String paths = MediaStore.Images.Media.insertImage(mContext.getContentResolver(), bitmap, "Pic from camera", null);
//            mImageUri = Uri.parse(paths);
//            float size = getImageSize(mContext, mImageUri);
//            imagePathList.add(mImageUri.toString());
//
//            DetailFileAdapter mDetailFileAdapter = new DetailFileAdapter(imagePathList, mContext);
//            RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(this, 3);
//            recycle_image_attachment.setLayoutManager(mLayoutManager);
//            recycle_image_attachment.setAdapter(mDetailFileAdapter);
//            mDetailFileAdapter.notifyDataSetChanged();
//
//        }
//
//       else if (requestCode == PayUmoneyFlowManager.REQUEST_CODE_PAYMENT && resultCode == RESULT_OK && data != null) {
//
//
//            TransactionResponse transactionResponse = data.getParcelableExtra(PayUmoneyFlowManager.INTENT_EXTRA_TRANSACTION_RESPONSE);
//            ResultModel resultModel = data.getParcelableExtra(PayUmoneyFlowManager.ARG_RESULT);
//
//            if (transactionResponse != null && transactionResponse.getPayuResponse() != null) {
//
//                if (transactionResponse.getTransactionStatus().equals(TransactionResponse.TransactionStatus.SUCCESSFUL)) {
//                    showAlert("Payment Successful");
//                    finish();
//                } else if (transactionResponse.getTransactionStatus().equals(TransactionResponse.TransactionStatus.CANCELLED)) {
//                    showAlert("Payment Cancelled");
//                } else if (transactionResponse.getTransactionStatus().equals(TransactionResponse.TransactionStatus.FAILED)) {
//                    showAlert("Payment Failed");
//                }
//
//            } else if (resultModel != null && resultModel.getError() != null) {
//                Toast.makeText(this, "Error check log", Toast.LENGTH_SHORT).show();
//            } else {
//                //  Toast.makeText(this, "Both objects are null", Toast.LENGTH_SHORT).show();
//            }
//        } else if (requestCode == PayUmoneyFlowManager.REQUEST_CODE_PAYMENT && resultCode == RESULT_CANCELED) {
//            showAlert("Payment Cancelled");
//        }
//    }
//
//    public String saveImage(Bitmap myBitmap) {
//        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
//        myBitmap.compress(Bitmap.CompressFormat.JPEG, 90, bytes);
//        File wallpaperDirectory = new File(
//                Environment.getExternalStorageDirectory() + IMAGE_DIRECTORY);
//        // have the object build the directory structure, if needed.
//        if (!wallpaperDirectory.exists()) {
//            wallpaperDirectory.mkdirs();
//        }
//
//        try {
//            f = new File(wallpaperDirectory, Calendar.getInstance()
//                    .getTimeInMillis() + ".jpg");
//            f.createNewFile();
//            FileOutputStream fo = new FileOutputStream(f);
//            fo.write(bytes.toByteArray());
//            MediaScannerConnection.scanFile(this,
//                    new String[]{f.getPath()},
//                    new String[]{"image/jpeg"}, null);
//            fo.close();
//            Log.d("TAG", "File Saved::--->" + f.getAbsolutePath());
//
//            return f.getAbsolutePath();
//        } catch (IOException e1) {
//            e1.printStackTrace();
//        }
//        return "";
//    }
//
//    private void requestMultiplePermissions() {
//        Dexter.withActivity(this)
//                .withPermissions(
//                        Manifest.permission.CAMERA,
//                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
//                        Manifest.permission.READ_EXTERNAL_STORAGE)
//                .withListener(new MultiplePermissionsListener() {
//                    @Override
//                    public void onPermissionsChecked(MultiplePermissionsReport report) {
//                        // check if all permissions are granted
//                        if (report.areAllPermissionsGranted()) {
//                            Toast.makeText(getApplicationContext(), "All permissions are granted by user!", Toast.LENGTH_SHORT).show();
//                        }
//
//                        // check for permanent denial of any permission
//                        if (report.isAnyPermissionPermanentlyDenied()) {
//                            // show alert dialog navigating to Settings
//                            //openSettingsDialog();fc
//                            Toast.makeText(getApplicationContext(), "You Denied the Permission", Toast.LENGTH_SHORT).show();
//                        }
//                    }
//
//                    @Override
//                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
//                        token.continuePermissionRequest();
//                    }
//                }).
//                withErrorListener(new PermissionRequestErrorListener() {
//                    @Override
//                    public void onError(DexterError error) {
//                        Toast.makeText(getApplicationContext(), "Some Error! ", Toast.LENGTH_SHORT).show();
//                    }
//                })
//                .onSameThread()
//                .check();
//    }
//
//    public static float getImageSize(Context context, Uri uri) {
//        Cursor cursor = context.getContentResolver().query(uri, null, null, null, null);
//        if (cursor != null) {
//            int sizeIndex = cursor.getColumnIndex(OpenableColumns.SIZE);
//            cursor.moveToFirst();
//            float imageSize = cursor.getLong(sizeIndex);
//            cursor.close();
//            return imageSize / (1024f * 1024f); // returns size in bytes
//        }
//        return 0;
//    }
//
//    public static String getFilePathFromURI(Context context, Uri contentUri, String extension) {
//        //copy file and send new file path
//        String fileName = getFileNameInfo(contentUri);
//        if (!TextUtils.isEmpty(fileName)) {
//            String ext = "";
//            if (fileName.contains(".")) {
//            } else {
//                ext = "." + extension;
//            }
//            File wallpaperDirectoryFile = new File(
//                    Environment.getExternalStorageDirectory() + IMAGE_DIRECTORY + File.separator + fileName + ext);
//            copy(context, contentUri, wallpaperDirectoryFile);
//            return wallpaperDirectoryFile.getAbsolutePath();
//        }
//        return null;
//    }
//
//    protected static String getFileNameInfo(Uri uri) {
//        if (uri == null) {
//            return null;
//        }
//        String fileName = null;
//        String path = uri.getPath();
//        int cut = path.lastIndexOf('/');
//        if (cut != -1) {
//            fileName = path.substring(cut + 1);
//        }
//        return fileName;
//    }
//
//    public static void copy(Context context, Uri srcUri, File dstFile) {
//        try {
//            InputStream inputStream = context.getContentResolver().openInputStream(srcUri);
//            if (inputStream == null) return;
//            OutputStream outputStream = new FileOutputStream(dstFile);
//            IOUtils.copy(inputStream, outputStream);
//            inputStream.close();
//            outputStream.close();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }


    public void setCouponList(ArrayList couponArraylistNew) {
        this.couponArraylist = couponArraylistNew;
        Log.i("cooooooo", couponArraylist.toString());
    }

    public Date addDays(Date date, int days) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.DATE, days);

        return cal.getTime();
    }

    /**
     * subtract days to date in java
     *
     * @param date
     * @param days
     * @return
     */
    public Date subtractDays(Date date, int days) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.DATE, -days);

        return cal.getTime();
    }

    static String mDate;


    public static class MyDatePickerDialog extends DialogFragment {
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            Config.logV("Date selected----------------------Selected" + selectedDateFormat);

            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);
            String tomorrowdate = year + "-" + month + "-" + day;
            DatePickerDialog da;
            if (selectedDateFormat.equalsIgnoreCase(tomorrowdate)) {
                da = new DatePickerDialog(getActivity(), dateSetListener, year, month, day);
            } else {
                Date date = null;
                try {
                    DateFormat selecteddateParse = new SimpleDateFormat("yyyy-MM-dd");
                    date = selecteddateParse.parse(selectedDateFormat);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(date);
                da = new DatePickerDialog(getActivity(), dateSetListener, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
            }

            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.DAY_OF_YEAR, 1);
            // da.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
            da.getDatePicker().setMinDate(cal.getTimeInMillis());

            return da;
        }

        private DatePickerDialog.OnDateSetListener dateSetListener =
                new DatePickerDialog.OnDateSetListener() {
                    public void onDateSet(DatePicker view, int year, int month, int day) {

                        SimpleDateFormat simpledateformat = new SimpleDateFormat("EEEE");
                        Date date = new Date(year, month, day - 1);
                        String dayOfWeek = simpledateformat.format(date);
                        Config.logV("Day-------------" + dayOfWeek);

                        mDate = dayOfWeek + ", " + view.getDayOfMonth() +
                                "/" + (view.getMonth() + 1) +
                                "/" + view.getYear();
                        txt_date.setText(mDate);

                        selectedDateFormat = view.getYear() + "-" + (view.getMonth() + 1) + "-" + view.getDayOfMonth();
                        UpdateDAte(selectedDateFormat);

                    }
                };
    }

    public static void UpdateDAte(String selectedDate) {
        Date selecteddate = null;
        String dtStart = txt_date.getText().toString();
        SimpleDateFormat format = new SimpleDateFormat("EEE, dd/MM/yyyy");
        try {
            selecteddate = format.parse(dtStart);
            //  System.out.println(selecteddate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Config.logV("Selected Date---&&&&&&&&&&&#%%%%%%%-------------" + selectedDate);
        ApiQueueTimeSlot(String.valueOf(serviceId), String.valueOf(mSpinnertext), modifyAccountID, selectedDate, String.valueOf(isShowToken));

        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_YEAR, 1);
        Date tomorow = cal.getTime();
        if (tomorow.before(selecteddate)) {
            Config.logV("Date Enabled---------------");
            ic_cal_minus.setEnabled(true);
            ic_cal_minus.setImageResource(R.drawable.icon_minus_active);

        } else {
            Config.logV("Date Disabled---------------");
            ic_cal_minus.setEnabled(false);
            ic_cal_minus.setImageResource(R.drawable.icon_minus_disabled);
        }
    }

    static int familyMEmID;

    public static void refreshName(String name, int memID) {
        Config.logV("NAme----------" + name);
        if (name != null && !name.equalsIgnoreCase("")) {
            tv_name.setText(name);
            familyMEmID = memID;
        }
    }

    private void ApiSearchViewSetting(String muniqueID) {


        ApiInterface apiService =
                ApiClient.getClientS3Cloud(mContext).create(ApiInterface.class);


        final Dialog mDialog = Config.getProgressDialog(mContext, mContext.getResources().getString(R.string.dialog_log_in));
        mDialog.show();

        Date currentTime = new Date();
        final SimpleDateFormat sdf = new SimpleDateFormat(
                "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US);
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        System.out.println("UTC time: " + sdf.format(currentTime));


        Call<SearchSetting> call = apiService.getSearchViewSetting(Integer.parseInt(muniqueID), sdf.format(currentTime));

        call.enqueue(new Callback<SearchSetting>() {
            @Override
            public void onResponse(Call<SearchSetting> call, Response<SearchSetting> response) {

                try {

                    if (mDialog.isShowing())
                        Config.closeDialog(getParent(), mDialog);

                    Config.logV("URL---------------" + response.raw().request().url().toString().trim());
                    Config.logV("Response--code-------------------------" + response.code());

                    if (response.code() == 200) {
                        if (response.body().getCalculationMode() != null) {

                            calcMode = response.body().getCalculationMode();
                            isShow = response.body().isShowTokenId();

                            if (response.body().getCalculationMode().equalsIgnoreCase("NoCalc") && response.body().isShowTokenId()) {
                                isShowToken = String.valueOf(response.body().isShowTokenId());
                                tv_title.setText("Get Token");
                                Word_Change = "Token for ";
                                btn_checkin.setText("CONFIRM");
                                toastMessage = "Token has been generated successfully";
                            } else {

                                if (terminology.equals("order")) {
                                    tv_title.setText("Order");
                                    Word_Change = "Order for ";
                                    btn_checkin.setText("CONFIRM");
                                    toastMessage = "You have ordered successfully";

                                } else {
                                    tv_title.setText("Check-in");
                                    Word_Change = "Check-in for ";
                                    btn_checkin.setText("CONFIRM");
                                    toastMessage = "Check-in saved successfully ";
                                }


                            }
                            ApiSearchViewServiceID(serviceId);
                        }
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(Call<SearchSetting> call, Throwable t) {
                // Log error here since request failed
                Config.logV("Fail---------------" + t.toString());
                if (mDialog.isShowing())
                    Config.closeDialog(getParent(), mDialog);
            }
        });
    }

    SectorCheckin checkin_sector = null;
    int partySize = 0;
    boolean enableparty = false;
    boolean multiplemem = false;

    private void APISector(String sector, String subsector) {
        ApiInterface apiService =
                ApiClient.getClient(mContext).create(ApiInterface.class);


        final Dialog mDialog = Config.getProgressDialog(mContext, mContext.getResources().getString(R.string.dialog_log_in));
        mDialog.show();


        Call<SectorCheckin> call = apiService.getSector(sector, subsector);

        call.enqueue(new Callback<SectorCheckin>() {
            @Override
            public void onResponse(Call<SectorCheckin> call, Response<SectorCheckin> response) {

                try {

                    if (mDialog.isShowing())
                        Config.closeDialog(getParent(), mDialog);

                    Config.logV("URL---------------" + response.raw().request().url().toString().trim());
                    Config.logV("Response--code-------------------------" + response.code());

                    if (response.code() == 200) {
                        checkin_sector = response.body();
                        maxPartysize = 0;
                        if (checkin_sector.isPartySize() && !checkin_sector.isPartySizeForCalculation()) {
                            layout_party.setVisibility(View.VISIBLE);
                            enableparty = true;
                            maxPartysize = checkin_sector.getMaxPartySize();

                        } else {
                            layout_party.setVisibility(View.GONE);
                            enableparty = false;
                        }
                        if (checkin_sector.isPartySizeForCalculation()) {
                            multiplemem = true;

                        } else {
                            multiplemem = false;
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<SectorCheckin> call, Throwable t) {
                // Log error here since request failed
                Config.logV("Fail---------------" + t.toString());
                if (mDialog.isShowing())
                    Config.closeDialog(getParent(), mDialog);
            }
        });
    }

    boolean showPaytmWallet = false;
    boolean showPayU = false;

    private void APIPayment(String accountID) {


        ApiInterface apiService =
                ApiClient.getClient(mContext).create(ApiInterface.class);


        final Dialog mDialog = Config.getProgressDialog(mContext, mContext.getResources().getString(R.string.dialog_log_in));
        mDialog.show();


        Call<ArrayList<PaymentModel>> call = apiService.getPaymentModes(accountID);

        call.enqueue(new Callback<ArrayList<PaymentModel>>() {
            @Override
            public void onResponse(Call<ArrayList<PaymentModel>> call, Response<ArrayList<PaymentModel>> response) {

                try {

                    if (mDialog.isShowing())
                        Config.closeDialog(getParent(), mDialog);

                    Config.logV("URL----%%%%%-----------" + response.raw().request().url().toString().trim());
                    Config.logV("Response--code-------------------------" + response.code());

                    if (response.code() == 200) {

                        mPaymentData = response.body();

                        for (int i = 0; i < mPaymentData.size(); i++) {
                            if (mPaymentData.get(i).getDisplayname().equalsIgnoreCase("Wallet")) {
                                showPaytmWallet = true;
                            }

                            if (mPaymentData.get(i).getName().equalsIgnoreCase("CC") || mPaymentData.get(i).getName().equalsIgnoreCase("DC") || mPaymentData.get(i).getName().equalsIgnoreCase("NB")) {
                                showPayU = true;
                            }
                        }

                        if ((showPayU) || showPaytmWallet) {
                            Config.logV("URL----%%%%%---@@--");
                            LservicePrepay.setVisibility(View.VISIBLE);
                            Typeface tyface = Typeface.createFromAsset(getAssets(),
                                    "fonts/Montserrat_Bold.otf");
                            txtprepay.setTypeface(tyface);
                            txtprepayamount.setTypeface(tyface);
                            String firstWord = "Prepayment Amount: ";
                            String secondWord = "₹ " + Config.getAmountinTwoDecimalPoints(Double.parseDouble(sAmountPay));
                            Spannable spannable = new SpannableString(firstWord + secondWord);
                            spannable.setSpan(new ForegroundColorSpan(mContext.getResources().getColor(R.color.colorAccent)),
                                    firstWord.length(), firstWord.length() + secondWord.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                            txtprepayamount.setText(spannable);
                        }

                    } else {
                        Toast.makeText(mContext, response.errorBody().string(), Toast.LENGTH_LONG).show();
                    }


                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(Call<ArrayList<PaymentModel>> call, Throwable t) {
                // Log error here since request failed
                Config.logV("Fail---------------" + t.toString());
                if (mDialog.isShowing())
                    Config.closeDialog(getParent(), mDialog);

            }
        });


    }

    static SimpleDateFormat inputParser = new SimpleDateFormat("HH:mm", Locale.US);

    private static Date dateCompareOne;

    private static void ApiQueueTimeSlot(String serviceId, String subSeriveID, String accountID, String mDate, final String isShowToken) {

        ApiInterface apiService =
                ApiClient.getClient(mContext).create(ApiInterface.class);

        final Dialog mDialog = Config.getProgressDialog(mContext, mContext.getResources().getString(R.string.dialog_log_in));
        mDialog.show();


        Call<ArrayList<QueueTimeSlotModel>> call = apiService.getQueueTimeSlot(serviceId, subSeriveID, mDate, accountID);

        call.enqueue(new Callback<ArrayList<QueueTimeSlotModel>>() {
            @Override
            public void onResponse(Call<ArrayList<QueueTimeSlotModel>> call, Response<ArrayList<QueueTimeSlotModel>> response) {

                try {

                    if (mDialog.isShowing())
                        Config.closeDialog(mActivity, mDialog);

                    Config.logV("URL---------------" + response.raw().request().url().toString().trim());
                    Config.logV("Response--code-------------------------" + response.code());
                    Config.logV("mQueueTimeSlotList--------11111-----------------" + response.code());
                    if (response.code() == 200) {
                        mQueueTimeSlotList = response.body();

                        if (mQueueTimeSlotList.size() > 0) {
                            i = 0;
                            if (mQueueTimeSlotList.get(0).getCalculationMode().equalsIgnoreCase("NoCalc") && String.valueOf(mQueueTimeSlotList.get(0).getQueueSize()) != null && isShowToken.equalsIgnoreCase("true") || ((mQueueTimeSlotList.get(0).getCalculationMode().equalsIgnoreCase("Fixed") || mQueueTimeSlotList.get(0).getCalculationMode().equalsIgnoreCase("Ml")))) {
                                tv_personahead.setVisibility(View.VISIBLE);


                                String firstWord = "ahead of you ";
                                String secondWord = String.valueOf(mQueueTimeSlotList.get(0).getQueueSize()+ " People");

                                Spannable spannable = new SpannableString(secondWord + '\n' + firstWord);
                                tv_personahead.setText(spannable);

                            }
                            else{
                                tv_personahead.setVisibility(View.GONE);
                            }
                        }

                        if (mQueueTimeSlotList.size() == 1) {
                            tv_queue.setText("Time window");
                        } else {
                            tv_queue.setText("Choose the time window");
                        }

                        if (mQueueTimeSlotList.size() == 1) {
                            tv_queue.setText("Time window");
                        } else {
                            tv_queue.setText("Choose the time window");
                        }

                        if (mQueueTimeSlotList.size() > 0) {
                            Lbottomlayout.setVisibility(View.VISIBLE);
//                            tv_queuename.setVisibility(View.GONE);
                            tv_queuetime.setVisibility(View.VISIBLE);
                            tv_waittime.setVisibility(View.VISIBLE);
                            txtnocheckin.setVisibility(View.GONE);
                            if (mQueueTimeSlotList.get(i).getId() != 0) {
                                queueId = mQueueTimeSlotList.get(i).getId();
                            }

                            Config.logV("mQueueTimeSlotList-------------------------" + mQueueTimeSlotList.size());
                            tv_queue.setVisibility(View.VISIBLE);
                            queuelayout.setVisibility(View.VISIBLE);


//                            tv_queuename.setText(mQueueTimeSlotList.get(0).getName());
                            tv_queuetime.setText(mQueueTimeSlotList.get(0).getQueueSchedule().getTimeSlots().get(0).getsTime() + "- " + mQueueTimeSlotList.get(0).getQueueSchedule().getTimeSlots().get(0).geteTime());

                            String firstWord = null;

                            String secondWord = null;
                            try {
                                String startTime = "00:00";
                                String newtime = null;
                                int minutes = mQueueTimeSlotList.get(0).getQueueWaitingTime();
                                int h = minutes / 60 + Integer.parseInt(startTime.substring(0, 1));
                                int m = minutes % 60 + Integer.parseInt(startTime.substring(3, 4));
                                if (m > 0 && h > 0) {
                                    newtime = h + " Hour :" + m + " Minutes";
                                } else if (h > 0 && m == 0) {
                                    newtime = h + " Hour";
                                } else {
                                    newtime = m + " Minutes";
                                }

                                secondWord = newtime;
                                if (mFrom.equalsIgnoreCase("checkin") || mFrom.equalsIgnoreCase("searchdetail_checkin") || mFrom.equalsIgnoreCase("favourites")) {

                                    if (h > 0) {
                                        firstWord = "Checked in for Today," + " " + "Est Wait Time ";
                                    } else {
                                        firstWord = "Est Wait Time ";

                                    }
                                } else {

                                    firstWord = "Next Available Time ";
                                    DateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd");
                                    DateFormat outputFormat = new SimpleDateFormat("dd-MM-yyyy");
                                    String inputDateStr = mQueueTimeSlotList.get(0).getEffectiveSchedule().getStartDate();
                                    Date datechange = null;
                                    try {
                                        datechange = inputFormat.parse(inputDateStr);
                                    } catch (ParseException e) {
                                        e.printStackTrace();
                                    }
                                    String outputDateStr = outputFormat.format(datechange);
                                    String dtStart = outputDateStr;
                                    Date dateParse = null;
                                    SimpleDateFormat format1 = new SimpleDateFormat("dd-MM-yyyy");
                                    try {
                                        dateParse = format1.parse(dtStart);
                                        System.out.println(dateParse);
                                    } catch (ParseException e) {
                                        e.printStackTrace();
                                    }

                                    SimpleDateFormat format = new SimpleDateFormat("d");
                                    String date1 = format.format(dateParse);

                                    if (date1.endsWith("1") && !date1.endsWith("11"))
                                        format = new SimpleDateFormat("EE, MMM d'st' yyyy");
                                    else if (date1.endsWith("2") && !date1.endsWith("12"))
                                        format = new SimpleDateFormat("EE, MMM d'nd' yyyy");
                                    else if (date1.endsWith("3") && !date1.endsWith("13"))
                                        format = new SimpleDateFormat("EE, MMM d'rd' yyyy");
                                    else
                                        format = new SimpleDateFormat("EE, MMM d'th' yyyy");

                                    String yourDate = format.format(dateParse);

                                    secondWord = yourDate + ", " + newtime;
                                }
                                Config.logV("Second WORD---@@@@----222--------" + secondWord + "Datecompare" + dateCompareOne);

                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                            if (mQueueTimeSlotList.get(i).getId() != 0) {
                                queueId = mQueueTimeSlotList.get(i).getId();
                            }

                            if (mQueueTimeSlotList.get(0).getServiceTime() != null) {

                                if (mFrom.equalsIgnoreCase("checkin") || mFrom.equalsIgnoreCase("searchdetail_checkin") || mFrom.equalsIgnoreCase("favourites")) {

                                    firstWord = "Next Available Time Today, ";
                                    secondWord = mQueueTimeSlotList.get(0).getServiceTime();
                                } else {

                                    firstWord = "Next Available Time ";
                                    DateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd");
                                    DateFormat outputFormat = new SimpleDateFormat("dd-MM-yyyy");
                                    String inputDateStr = mQueueTimeSlotList.get(0).getEffectiveSchedule().getStartDate();
                                    Date datechange = null;
                                    try {
                                        datechange = inputFormat.parse(inputDateStr);
                                    } catch (ParseException e) {
                                        e.printStackTrace();
                                    }
                                    String outputDateStr = outputFormat.format(datechange);
                                    String dtStart = outputDateStr;
                                    Date dateParse = null;
                                    SimpleDateFormat format1 = new SimpleDateFormat("dd-MM-yyyy");
                                    try {
                                        dateParse = format1.parse(dtStart);
                                        System.out.println(dateParse);
                                    } catch (ParseException e) {
                                        e.printStackTrace();
                                    }

                                    SimpleDateFormat format = new SimpleDateFormat("d");
                                    String date1 = format.format(dateParse);

                                    if (date1.endsWith("1") && !date1.endsWith("11"))
                                        format = new SimpleDateFormat("EE, MMM d'st' yyyy");
                                    else if (date1.endsWith("2") && !date1.endsWith("12"))
                                        format = new SimpleDateFormat("EE, MMM d'nd' yyyy");
                                    else if (date1.endsWith("3") && !date1.endsWith("13"))
                                        format = new SimpleDateFormat("EE, MMM d'rd' yyyy");
                                    else
                                        format = new SimpleDateFormat("EE, MMM d'th' yyyy");

                                    String yourDate = format.format(dateParse);


                                    secondWord = yourDate + ", " + mQueueTimeSlotList.get(0).getServiceTime();
                                }


                                Config.logV("Second WORD---------------" + secondWord);
                            }

                            Spannable spannable = new SpannableString(firstWord + '\n' + secondWord);
                            Typeface tyface1 = Typeface.createFromAsset(mContext.getAssets(),
                                    "fonts/Montserrat_Bold.otf");
                            spannable.setSpan(new CustomTypefaceSpan("sans-serif", tyface1), firstWord.length(), firstWord.length() + secondWord.length() + 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                            spannable.setSpan(new ForegroundColorSpan(mContext.getResources().getColor(R.color.title_grey)), 0, firstWord.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                            spannable.setSpan(new ForegroundColorSpan(mContext.getResources().getColor(R.color.violet)), firstWord.length(), firstWord.length() + secondWord.length() + 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                            tv_waittime.setText(spannable);

                            Config.logV("TV WAITTIME-------------VISIBLE-------" + spannable);


                            if (mQueueTimeSlotList.get(0).getCalculationMode().equalsIgnoreCase("NoCalc")) {
                                tv_waittime.setVisibility(View.VISIBLE);
                                Config.logV("TV WAITTIME-------------INVISIBLE-------");

                                Date c = Calendar.getInstance().getTime();
                                System.out.println("Current time => " + c);
                                String formattedDate;
                                if (mFrom.equalsIgnoreCase("checkin") || mFrom.equalsIgnoreCase("searchdetail_checkin") || mFrom.equalsIgnoreCase("favourites")) {

                                    SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyy");
                                    formattedDate = "Today, " + df.format(c);
                                } else {
                                    String dtStart = selectedDateFormat;
                                    Date dateParse = null;
                                    SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd");
                                    try {
                                        dateParse = format1.parse(dtStart);
                                        System.out.println(dateParse);
                                    } catch (ParseException e) {
                                        e.printStackTrace();
                                    }

                                    SimpleDateFormat df = new SimpleDateFormat("EE,dd-MM-yyy");
                                    formattedDate = df.format(dateParse);
                                }

                                tv_waittime.setText(formattedDate);

                            } else {
                                tv_waittime.setVisibility(View.VISIBLE);
                            }


                        } else {

                            Config.logV("No Checkins-------------------" + mQueueTimeSlotList.size());
                            tv_queue.setVisibility(View.GONE);
                            queuelayout.setVisibility(View.GONE);
//                            tv_queuename.setVisibility(View.GONE);
                            tv_queuetime.setVisibility(View.GONE);
                            tv_waittime.setVisibility(View.GONE);
                            Lbottomlayout.setVisibility(View.GONE);
                            txtnocheckin.setVisibility(View.VISIBLE);
                            txtnocheckin.setText(Word_Change + "this service is not available at the moment. Please try for a different time or date");
                        }


                        if (mQueueTimeSlotList.size() > 1) {

                            ic_right.setVisibility(View.VISIBLE);
                            ic_left.setVisibility(View.VISIBLE);
                            ic_right.setImageResource(R.drawable.icon_right_angle_active);
                            ic_right.setEnabled(true);


                        } else {
                            ic_right.setVisibility(View.INVISIBLE);
                            ic_left.setVisibility(View.INVISIBLE);
                        }

                        if (i > 0) {
                            ic_left.setEnabled(true);
                            ic_left.setImageResource(R.drawable.icon_left_angle_active);
                        } else {
                            ic_left.setEnabled(false);
                            ic_left.setImageResource(R.drawable.icon_left_angle_disabled);
                        }


                        ic_left.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                i--;
                                Config.logV("Left Click------------------**" + i);
                                if (i >= 0) {

//                                    tv_queuename.setText(mQueueTimeSlotList.get(i).getName());
                                    tv_queuetime.setText(mQueueTimeSlotList.get(i).getQueueSchedule().getTimeSlots().get(0).getsTime() + "- " + mQueueTimeSlotList.get(i).getQueueSchedule().getTimeSlots().get(0).geteTime());

                                    String firstWord = null;

                                    String secondWord = null;
                                    try {
                                        String startTime = "00:00";
                                        String newtime;
                                        int minutes = mQueueTimeSlotList.get(i).getQueueWaitingTime();
                                        int h = minutes / 60 + Integer.parseInt(startTime.substring(0, 1));
                                        int m = minutes % 60 + Integer.parseInt(startTime.substring(3, 4));
                                        if (m > 0 && h > 0) {
                                            newtime = h + " Hour :" + m + "  Minutes";
                                        } else if (h > 0 && m == 0) {
                                            newtime = h + " Hour";
                                        } else {
                                            newtime = m + " Minutes";
                                        }
                                        secondWord = newtime;
                                        if (mFrom.equalsIgnoreCase("checkin") || mFrom.equalsIgnoreCase("searchdetail_checkin") || mFrom.equalsIgnoreCase("favourites")) {

                                            if (h > 0) {
                                                firstWord = "Next Available Time Today, ";
                                            } else {
                                                firstWord = "Est Wait Time ";

                                            }

                                        } else {

                                            firstWord = "Next Available Time ";
                                            DateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd");
                                            DateFormat outputFormat = new SimpleDateFormat("dd-MM-yyyy");
                                            String inputDateStr = mQueueTimeSlotList.get(0).getEffectiveSchedule().getStartDate();
                                            Date datechange = null;
                                            try {
                                                datechange = inputFormat.parse(inputDateStr);
                                            } catch (ParseException e) {
                                                e.printStackTrace();
                                            }
                                            String outputDateStr = outputFormat.format(datechange);


                                            // String strDate = outputDateStr + ", " + activelist.getServiceTime();

                                            String dtStart = outputDateStr;
                                            Date dateParse = null;
                                            SimpleDateFormat format1 = new SimpleDateFormat("dd-MM-yyyy");
                                            try {
                                                dateParse = format1.parse(dtStart);
                                                System.out.println(dateParse);
                                            } catch (ParseException e) {
                                                e.printStackTrace();
                                            }

                                            SimpleDateFormat format = new SimpleDateFormat("d");
                                            String date1 = format.format(dateParse);

                                            if (date1.endsWith("1") && !date1.endsWith("11"))
                                                format = new SimpleDateFormat("EE, MMM d'st' yyyy");
                                            else if (date1.endsWith("2") && !date1.endsWith("12"))
                                                format = new SimpleDateFormat("EE, MMM d'nd' yyyy");
                                            else if (date1.endsWith("3") && !date1.endsWith("13"))
                                                format = new SimpleDateFormat("EE, MMM d'rd' yyyy");
                                            else
                                                format = new SimpleDateFormat("EE, MMM d'th' yyyy");

                                            String yourDate = format.format(dateParse);

                                            secondWord = yourDate + ", " + newtime;
                                        }

                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }


                                    if (mQueueTimeSlotList.get(i).getId() != 0) {
                                        queueId = mQueueTimeSlotList.get(i).getId();
                                    }

                                    if (mQueueTimeSlotList.get(i).getServiceTime() != null) {
                                        // secondWord = mQueueTimeSlotList.get(i).getServiceTime();


                                        //  dateCompareOne = parseDate(secondWord);


                                        if (mFrom.equalsIgnoreCase("checkin") || mFrom.equalsIgnoreCase("searchdetail_checkin") || mFrom.equalsIgnoreCase("favourites")) {

                                            firstWord = "Next Available Time Today, ";
                                            secondWord = mQueueTimeSlotList.get(i).getServiceTime();

                                        } else {

                                            firstWord = "Next Available Time ";
                                            DateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd");
                                            DateFormat outputFormat = new SimpleDateFormat("dd-MM-yyyy");
                                            String inputDateStr = mQueueTimeSlotList.get(0).getEffectiveSchedule().getStartDate();
                                            Date datechange = null;
                                            try {
                                                datechange = inputFormat.parse(inputDateStr);
                                            } catch (ParseException e) {
                                                e.printStackTrace();
                                            }
                                            String outputDateStr = outputFormat.format(datechange);
                                            String dtStart = outputDateStr;
                                            Date dateParse = null;
                                            SimpleDateFormat format1 = new SimpleDateFormat("dd-MM-yyyy");
                                            try {
                                                dateParse = format1.parse(dtStart);
                                                System.out.println(dateParse);
                                            } catch (ParseException e) {
                                                e.printStackTrace();
                                            }

                                            SimpleDateFormat format = new SimpleDateFormat("d");
                                            String date1 = format.format(dateParse);

                                            if (date1.endsWith("1") && !date1.endsWith("11"))
                                                format = new SimpleDateFormat("EE, MMM d'st' yyyy");
                                            else if (date1.endsWith("2") && !date1.endsWith("12"))
                                                format = new SimpleDateFormat("EE, MMM d'nd' yyyy");
                                            else if (date1.endsWith("3") && !date1.endsWith("13"))
                                                format = new SimpleDateFormat("EE, MMM d'rd' yyyy");
                                            else
                                                format = new SimpleDateFormat("EE, MMM d'th' yyyy");

                                            String yourDate = format.format(dateParse);


                                            secondWord = yourDate + ", " + mQueueTimeSlotList.get(i).getServiceTime();
                                        }
                                    }

                                    Spannable spannable = new SpannableString(firstWord +'\n' + secondWord);
                                    Typeface tyface1 = Typeface.createFromAsset(mContext.getAssets(),
                                            "fonts/Montserrat_Bold.otf");
                                    spannable.setSpan(new CustomTypefaceSpan("sans-serif", tyface1), firstWord.length(), firstWord.length() + secondWord.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                                    spannable.setSpan(new ForegroundColorSpan(mContext.getResources().getColor(R.color.title_grey)), 0, firstWord.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                                    spannable.setSpan(new ForegroundColorSpan(mContext.getResources().getColor(R.color.violet)), firstWord.length(), firstWord.length() + secondWord.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                                    tv_waittime.setText(spannable);

                                    if (mQueueTimeSlotList.get(0).getCalculationMode().equalsIgnoreCase("NoCalc")) {
                                        //   tv_waittime.setVisibility(View.INVISIBLE);

                                        tv_waittime.setVisibility(View.VISIBLE);
                                        Config.logV("TV WAITTIME-------------INVISIBLE-------");
                                        Date c = Calendar.getInstance().getTime();
                                        System.out.println("Current time => " + c);
                                        String formattedDate;
                                        if (mFrom.equalsIgnoreCase("checkin") || mFrom.equalsIgnoreCase("searchdetail_checkin") || mFrom.equalsIgnoreCase("favourites")) {

                                            SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyy");
                                            formattedDate = "Today," + df.format(c);
                                        } else {
                                            String dtStart = selectedDateFormat;
                                            Date dateParse = null;
                                            SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd");
                                            try {
                                                dateParse = format1.parse(dtStart);
                                                System.out.println(dateParse);
                                            } catch (ParseException e) {
                                                e.printStackTrace();
                                            }

                                            SimpleDateFormat df = new SimpleDateFormat("EE,dd-MM-yyy");
                                            formattedDate = df.format(dateParse);
                                        }

                                        tv_waittime.setText(formattedDate);
                                    } else {
                                        tv_waittime.setVisibility(View.VISIBLE);
                                    }
                                }


                                if (i < mQueueTimeSlotList.size()) {
                                    ic_right.setEnabled(true);
                                    ic_right.setImageResource(R.drawable.icon_right_angle_active);
                                } else {
                                    ic_right.setEnabled(false);
                                    ic_right.setImageResource(R.drawable.icon_right_angle_disabled);
                                }

                                if (i <= 0) {
                                    ic_left.setEnabled(false);
                                    ic_left.setImageResource(R.drawable.icon_left_angle_disabled);
                                } else {

                                    ic_left.setEnabled(true);
                                    ic_left.setImageResource(R.drawable.icon_left_angle_active);
                                }

                            }
                        });

                        ic_right.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                if (i < 0) {
                                    i = 0;
                                }
                                i++;
                                Config.logV("Right Click----1111--------------" + i);
                                if (i < mQueueTimeSlotList.size()) {

//                                    tv_queuename.setText(mQueueTimeSlotList.get(i).getName());
                                    tv_queuetime.setText(mQueueTimeSlotList.get(i).getQueueSchedule().getTimeSlots().get(0).getsTime() + "- " + mQueueTimeSlotList.get(i).getQueueSchedule().getTimeSlots().get(0).geteTime());


                                    if (mQueueTimeSlotList.get(i).getId() != 0) {
                                        queueId = mQueueTimeSlotList.get(i).getId();
                                    }

                                    String firstWord = null;

                                    String secondWord = null;
                                    try {
                                        String startTime = "00:00";
                                        String newtime;
                                        int minutes = mQueueTimeSlotList.get(i).getQueueWaitingTime();
                                        int h = minutes / 60 + Integer.parseInt(startTime.substring(0, 1));
                                        int m = minutes % 60 + Integer.parseInt(startTime.substring(3, 4));
                                        if (m > 0 && h > 0) {
                                            newtime = h + " Hour :" + m + " Minutes";
                                        } else if (h > 0 && m == 0) {
                                            newtime = h + " Hour";
                                        } else {
                                            newtime = m + " Minutes";
                                        }
                                        secondWord = newtime;


                                        // dateCompareOne = parseDate(secondWord);


                                        if (mFrom.equalsIgnoreCase("checkin") || mFrom.equalsIgnoreCase("searchdetail_checkin") || mFrom.equalsIgnoreCase("favourites")) {

                                            if (h > 0) {
                                                firstWord = "Next Available Time Today, ";
                                            } else {
                                                firstWord = "Est Wait Time ";

                                            }
                                            secondWord = newtime;
                                            Config.logV("First Word@@@@@@@@@@@@@" + firstWord + "Second" + secondWord);
                                        } else {

                                            firstWord = "Next Available Time ";
                                            DateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd");
                                            DateFormat outputFormat = new SimpleDateFormat("dd-MM-yyyy");
                                            String inputDateStr = mQueueTimeSlotList.get(0).getEffectiveSchedule().getStartDate();
                                            Date datechange = null;
                                            try {
                                                datechange = inputFormat.parse(inputDateStr);
                                            } catch (ParseException e) {
                                                e.printStackTrace();
                                            }
                                            String outputDateStr = outputFormat.format(datechange);


                                            // String strDate = outputDateStr + ", " + activelist.getServiceTime();

                                            String dtStart = outputDateStr;
                                            Date dateParse = null;
                                            SimpleDateFormat format1 = new SimpleDateFormat("dd-MM-yyyy");
                                            try {
                                                dateParse = format1.parse(dtStart);
                                                System.out.println(dateParse);
                                            } catch (ParseException e) {
                                                e.printStackTrace();
                                            }

                                            SimpleDateFormat format = new SimpleDateFormat("d");
                                            String date1 = format.format(dateParse);

                                            if (date1.endsWith("1") && !date1.endsWith("11"))
                                                format = new SimpleDateFormat("EE, MMM d'st' yyyy");
                                            else if (date1.endsWith("2") && !date1.endsWith("12"))
                                                format = new SimpleDateFormat("EE, MMM d'nd' yyyy");
                                            else if (date1.endsWith("3") && !date1.endsWith("13"))
                                                format = new SimpleDateFormat("EE, MMM d'rd' yyyy");
                                            else
                                                format = new SimpleDateFormat("EE, MMM d'th' yyyy");

                                            String yourDate = format.format(dateParse);

                                            secondWord = yourDate + ", " + newtime;
                                        }

                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }


                                    if (mQueueTimeSlotList.get(i).getServiceTime() != null) {
                                        if (mFrom.equalsIgnoreCase("checkin") || mFrom.equalsIgnoreCase("searchdetail_checkin") || mFrom.equalsIgnoreCase("favourites")) {

                                            firstWord = "Next Available Time Today, ";
                                            secondWord = mQueueTimeSlotList.get(i).getServiceTime();
                                            Config.logV("First Word@@@@@@@@@@@@@" + firstWord + "Second777" + secondWord);

                                        } else {

                                            firstWord = "Next Available Time ";
                                            DateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd");
                                            DateFormat outputFormat = new SimpleDateFormat("dd-MM-yyyy");
                                            String inputDateStr = mQueueTimeSlotList.get(0).getEffectiveSchedule().getStartDate();
                                            Date datechange = null;
                                            try {
                                                datechange = inputFormat.parse(inputDateStr);
                                            } catch (ParseException e) {
                                                e.printStackTrace();
                                            }
                                            String outputDateStr = outputFormat.format(datechange);
                                            String dtStart = outputDateStr;
                                            Date dateParse = null;
                                            SimpleDateFormat format1 = new SimpleDateFormat("dd-MM-yyyy");
                                            try {
                                                dateParse = format1.parse(dtStart);
                                                System.out.println(dateParse);
                                            } catch (ParseException e) {
                                                e.printStackTrace();
                                            }

                                            SimpleDateFormat format = new SimpleDateFormat("d");
                                            String date1 = format.format(dateParse);

                                            if (date1.endsWith("1") && !date1.endsWith("11"))
                                                format = new SimpleDateFormat("EE, MMM d'st' yyyy");
                                            else if (date1.endsWith("2") && !date1.endsWith("12"))
                                                format = new SimpleDateFormat("EE, MMM d'nd' yyyy");
                                            else if (date1.endsWith("3") && !date1.endsWith("13"))
                                                format = new SimpleDateFormat("EE, MMM d'rd' yyyy");
                                            else
                                                format = new SimpleDateFormat("EE, MMM d'th' yyyy");

                                            String yourDate = format.format(dateParse);


                                            secondWord = yourDate + ", " + mQueueTimeSlotList.get(i).getServiceTime();

                                        }
                                    }

                                    Spannable spannable = new SpannableString(firstWord + '\n' + secondWord);
                                    Typeface tyface1 = Typeface.createFromAsset(mContext.getAssets(),
                                            "fonts/Montserrat_Bold.otf");
                                    spannable.setSpan(new CustomTypefaceSpan("sans-serif", tyface1), firstWord.length(), firstWord.length() + secondWord.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                                    spannable.setSpan(new ForegroundColorSpan(mContext.getResources().getColor(R.color.title_grey)), 0, firstWord.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                                    spannable.setSpan(new ForegroundColorSpan(mContext.getResources().getColor(R.color.violet)), firstWord.length(), firstWord.length() + secondWord.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                                    tv_waittime.setText(spannable);
                                    if (mQueueTimeSlotList.get(0).getCalculationMode().equalsIgnoreCase("NoCalc")) {
                                        // tv_waittime.setVisibility(View.INVISIBLE);
                                        tv_waittime.setVisibility(View.VISIBLE);
                                        Config.logV("TV WAITTIME-------------INVISIBLE-------");
                                        Date c = Calendar.getInstance().getTime();
                                        System.out.println("Current time => " + c);
                                        String formattedDate;
                                        if (mFrom.equalsIgnoreCase("checkin") || mFrom.equalsIgnoreCase("searchdetail_checkin") || mFrom.equalsIgnoreCase("favourites")) {

                                            SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyy");
                                            formattedDate = "Today, " + df.format(c);
                                        } else {
                                            //  formattedDate = selectedDateFormat;
                                            String dtStart = selectedDateFormat;
                                            Date dateParse = null;
                                            SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd");
                                            try {
                                                dateParse = format1.parse(dtStart);
                                                System.out.println(dateParse);
                                            } catch (ParseException e) {
                                                e.printStackTrace();
                                            }

                                            SimpleDateFormat df = new SimpleDateFormat("EE,dd-MM-yyy");
                                            formattedDate = df.format(dateParse);
                                        }
                                        tv_waittime.setText(formattedDate);
                                    } else {
                                        tv_waittime.setVisibility(View.VISIBLE);
                                    }

                                }

                                if (i >= 0) {
                                    ic_left.setEnabled(true);
                                    ic_left.setImageResource(R.drawable.icon_left_angle_active);
                                } else {
                                    ic_left.setEnabled(false);
                                    ic_left.setImageResource(R.drawable.icon_left_angle_disabled);
                                }

                                Config.logV("Queuesize---------------" + mQueueTimeSlotList.size() + "position" + i);
                                if (i == mQueueTimeSlotList.size() - 1) {

                                    ic_right.setEnabled(false);
                                    ic_right.setImageResource(R.drawable.icon_right_angle_disabled);
                                } else {
                                    ic_right.setEnabled(true);
                                    ic_right.setImageResource(R.drawable.icon_right_angle_active);
                                }
                            }
                        });


                    }


                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(Call<ArrayList<QueueTimeSlotModel>> call, Throwable t) {
                // Log error here since request failed
                Config.logV("Fail---------------" + t.toString());
                if (mDialog.isShowing())
                    Config.closeDialog(mActivity, mDialog);

            }
        });

    }


    private void ApiSearchViewServiceID(final int id) {


        ApiInterface apiService =
                ApiClient.getClient(mContext).create(ApiInterface.class);


        final Dialog mDialog = Config.getProgressDialog(mContext, mContext.getResources().getString(R.string.dialog_log_in));
        mDialog.show();


        Call<ArrayList<SearchService>> call = apiService.getSearchService(id);

        call.enqueue(new Callback<ArrayList<SearchService>>() {
            @Override
            public void onResponse(Call<ArrayList<SearchService>> call, Response<ArrayList<SearchService>> response) {

                try {

                    if (mDialog.isShowing())
                        Config.closeDialog(getParent(), mDialog);

                    Config.logV("URL---------------" + response.raw().request().url().toString().trim());
                    Config.logV("Response--code-------------------------" + response.code());

                    if (response.code() == 200) {


                        for (int i = 0; i < response.body().size(); i++) {
                            SearchService mService = new SearchService();
                            mService.setName(response.body().get(i).getName());
                            mService.setLocid(id);
                            mService.setId(response.body().get(i).getId());
                            mService.setLivetrack(response.body().get(i).isLivetrack());
                            mService.setPrePayment(response.body().get(i).isPrePayment());
                            mService.setTotalAmount(response.body().get(i).getTotalAmount());
                            mService.setMinPrePaymentAmount(response.body().get(i).getMinPrePaymentAmount());
                            LServicesList.add(mService);
                        }
                        gServiceList.addAll(LServicesList);
                        // Department Section Starts

                        ApiInterface apiService = ApiClient.getClient(mContext).create(ApiInterface.class);
                        Call<SearchDepartment> call1 = apiService.getDepartment(Integer.parseInt(accountID.split("-")[0]));
                        call1.enqueue(new Callback<SearchDepartment>() {
                            @Override
                            public void onResponse(Call<SearchDepartment> call, Response<SearchDepartment> response) {
                                try {
                                    if (response.code() == 200) {

                                        Config.logV("URL123---------------" + response.raw().request().url().toString().trim());

                                        depResponse = response.body();
                                        if (depResponse.isFilterByDept() && depResponse.getDepartments().size() > 0) {
                                            mSpinnerDepartment.setVisibility(View.VISIBLE);
                                            txt_choosedepartment.setVisibility(View.VISIBLE);
                                            ArrayAdapter<SearchDepartment> adapter = new ArrayAdapter<SearchDepartment>(mActivity, android.R.layout.simple_spinner_dropdown_item, depResponse.getDepartments());
                                            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                            mSpinnerDepartment.setAdapter(adapter);
                                            deptSpinnertext = depResponse.getDepartmentId();
                                            ArrayList<SearchService> serviceList = new ArrayList<>();
                                            ArrayList<Integer> serviceIds = depResponse.getDepartments().get(0).getServiceIds();
                                            selectedDepartment = depResponse.getDepartments().get(0).getDepartmentId();
                                            departmentSelected = depResponse.getDepartments().get(0).getDepartmentName();
                                            for (int serviceIndex = 0; serviceIndex < serviceIds.size(); serviceIndex++) {

                                                for (int i = 0; i < gServiceList.size(); i++) {
                                                    if (serviceIds.get(serviceIndex) == gServiceList.get(i).getId()) {
                                                        serviceList.add(gServiceList.get(i));
                                                        break;
                                                    }
                                                }

                                            }
                                            LServicesList.clear();
                                            LServicesList.addAll(serviceList);

                                        }
                                        if (LServicesList.size() > 0) {
                                            mSpinnerService.setVisibility(View.VISIBLE);
                                            txt_chooseservice.setVisibility(View.VISIBLE);
                                            Config.logV("mServicesList" + LServicesList.size());
                                            ArrayAdapter<SearchService> adapter = new ArrayAdapter<SearchService>(mActivity, android.R.layout.simple_spinner_dropdown_item, LServicesList);
                                            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                            mSpinnerService.setAdapter(adapter);
                                            mSpinnertext = ((SearchService) mSpinnerService.getSelectedItem()).getId();
                                            livetrack = ((SearchService) mSpinnerService.getSelectedItem()).isLivetrack();
                                        } else {

                                            mSpinnerService.setVisibility(View.GONE);
                                            txt_chooseservice.setVisibility(View.GONE);

                                            if (LServicesList.size() == 1) {
                                                // String firstWord = "Check-in for ";
                                                String firstWord = Word_Change;
                                                String secondWord = LServicesList.get(0).getName();

                                                Spannable spannable = new SpannableString(firstWord + secondWord);
                                                Typeface tyface1 = Typeface.createFromAsset(mContext.getAssets(),
                                                        "fonts/Montserrat_Bold.otf");
                                                spannable.setSpan(new CustomTypefaceSpan("sans-serif", tyface1), firstWord.length(), firstWord.length() + secondWord.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                                                spannable.setSpan(new ForegroundColorSpan(mContext.getResources().getColor(R.color.title_grey)), 0, firstWord.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                                                spannable.setSpan(new ForegroundColorSpan(mContext.getResources().getColor(R.color.title_consu)), firstWord.length(), firstWord.length() + secondWord.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                                                tv_checkin_service.setText(spannable);
                                                mSpinnertext = LServicesList.get(0).getId();
                                                livetrack = LServicesList.get(0).isLivetrack();
                                                serviceSelected = LServicesList.get(0).getName();
                                                selectedService = LServicesList.get(0).getId();


                                                Date currentTime = new Date();
                                                final SimpleDateFormat sdf = new SimpleDateFormat(
                                                        "yyyy-MM-dd", Locale.US);
                                                sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
                                                System.out.println("UTC time: " + sdf.format(currentTime));
                                                //ApiQueueTimeSlot(String.valueOf(serviceId), String.valueOf(mSpinnertext), modifyAccountID, sdf.format(currentTime));


                                                isPrepayment = LServicesList.get(0).isPrePayment();
                                                Config.logV("Payment------------" + isPrepayment);
                                                if (isPrepayment) {


                                                    sAmountPay = LServicesList.get(0).getMinPrePaymentAmount();
                                                    APIPayment(modifyAccountID);

                                                    Config.logV("Payment----sAmountPay--------" + sAmountPay);

                                                } else {
                                                    LservicePrepay.setVisibility(View.GONE);
                                                }

                                            }
                                        }

                                        Date currentTime = new Date();
                                        final SimpleDateFormat sdf = new SimpleDateFormat(
                                                "yyyy-MM-dd", Locale.US);
                                        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
                                        System.out.println("UTC time: " + sdf.format(currentTime));
                                        Config.logV("SELECTED &&&&&&&&&&&&&&&&&@@@@@@@@@@@@@@@@@@@@@@@@@@@@");

                                        //  ApiQueueTimeSlot(String.valueOf(serviceId), String.valueOf(mSpinnertext), modifyAccountID, sdf.format(currentTime));

                                        if (mFrom.equalsIgnoreCase("checkin") || mFrom.equalsIgnoreCase("searchdetail_checkin") || mFrom.equalsIgnoreCase("favourites")) {

                                            ApiQueueTimeSlot(String.valueOf(serviceId), String.valueOf(mSpinnertext), modifyAccountID, sdf.format(currentTime), isShowToken);
                                        } else {
                                            if (selectedDateFormat != null) {

                                                ApiQueueTimeSlot(String.valueOf(serviceId), String.valueOf(mSpinnertext), modifyAccountID, selectedDateFormat, isShowToken);
                                            } else {

                                                ApiQueueTimeSlot(String.valueOf(serviceId), String.valueOf(mSpinnertext), modifyAccountID, sdf.format(currentTime), isShowToken);
                                            }
                                        }
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }

                            @Override
                            public void onFailure(Call<SearchDepartment> call, Throwable t) {
                                Config.logV("Fail---------------" + t.toString());
                            }
                        });

                        // Department Ends Here

                    }


                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(Call<ArrayList<SearchService>> call, Throwable t) {
                // Log error here since request failed
                Config.logV("Fail---------------" + t.toString());
                if (mDialog.isShowing())
                    Config.closeDialog(getParent(), mDialog);

            }
        });


    }

    private void ApiSearchUsers(int deptId) {


        ApiInterface apiService = ApiClient.getClient(mContext).create(ApiInterface.class);
        Call<SearchUsers> call1 = apiService.getUsers(deptId,Integer.parseInt(accountID.split("-")[0]));
        call1.enqueue(new Callback<SearchUsers>() {
            @Override
            public void onResponse(Call<SearchUsers> call, Response<SearchUsers> response) {
                try {
                    if (response.code() == 200) {

                        Config.logV("URL123---------------" + response.raw().request().url().toString().trim());


                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<SearchUsers> call, Throwable t) {

            }



        });

        // Department Ends Here

    }






    SearchTerminology mSearchTerminology;

    private void ApiSearchViewTerminology(String muniqueID) {


        ApiInterface apiService =
                ApiClient.getClientS3Cloud(mContext).create(ApiInterface.class);


        final Dialog mDialog = Config.getProgressDialog(mContext, mContext.getResources().getString(R.string.dialog_log_in));
        mDialog.show();

        Date currentTime = new Date();
        final SimpleDateFormat sdf = new SimpleDateFormat(
                "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US);
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        System.out.println("UTC time: " + sdf.format(currentTime));


        Call<SearchTerminology> call = apiService.getSearchViewTerminology(Integer.parseInt(muniqueID), sdf.format(currentTime));

        call.enqueue(new Callback<SearchTerminology>() {
            @Override
            public void onResponse(Call<SearchTerminology> call, Response<SearchTerminology> response) {

                try {

                    if (mDialog.isShowing())
                        Config.closeDialog(getParent(), mDialog);

                    Config.logV("URL---------------" + response.raw().request().url().toString().trim());
                    Config.logV("Response--code-------------------------" + response.code());

                    if (response.code() == 200) {

                        mSearchTerminology = response.body();
                    }


                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(Call<SearchTerminology> call, Throwable t) {
                // Log error here since request failed
                Config.logV("Fail---------------" + t.toString());
                if (mDialog.isShowing())
                    Config.closeDialog(getParent(), mDialog);

            }
        });


    }


    private void ApiGetProfileDetail() {

        ApiInterface apiService =
                ApiClient.getClient(mContext).create(ApiInterface.class);

        final int consumerId = SharedPreference.getInstance(mContext).getIntValue("consumerId", 0);

        final Dialog mDialog = Config.getProgressDialog(mContext, mContext.getResources().getString(R.string.dialog_log_in));
        mDialog.show();
        Call<ProfileModel> call = apiService.getProfileDetail(consumerId);

        call.enqueue(new Callback<ProfileModel>() {
            @Override
            public void onResponse(Call<ProfileModel> call, Response<ProfileModel> response) {

                try {

                    if (mDialog.isShowing())
                        Config.closeDialog(getParent(), mDialog);

                    Config.logV("URL---------------" + response.raw().request().url().toString().trim());
                    Config.logV("Response--code-------------------------" + response.code());
                    if (response.code() == 200) {
                        phoneNumberValue.setText(response.body().getUserprofile().getPrimaryMobileNo());
                        phoneNumber = phoneNumberValue.getText().toString();
                        Config.logV("Response--BODY-------------------------" + new Gson().toJson(response));
                        Config.logV("Response--mob-------------------------" + response.body().getUserprofile().getPrimaryMobileNo());
                    } else {
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }


            @Override
            public void onFailure(Call<ProfileModel> call, Throwable t) {
                // Log error here since request failed
                Config.logV("Fail---------------" + t.toString());
                if (mDialog.isShowing())
                    Config.closeDialog(getParent(), mDialog);
            }
        });


    }

    private void ApiGenerateHash(String ynwUUID, String amount, String accountID) {


        ApiInterface apiService =
                ApiClient.getClient(mContext).create(ApiInterface.class);


        final Dialog mDialog = Config.getProgressDialog(mContext, mContext.getResources().getString(R.string.dialog_log_in));
        mDialog.show();

        //  String uniqueID = UUID.randomUUID().toString();
        JSONObject jsonObj = new JSONObject();
        try {

            jsonObj.put("amount", amount);
            jsonObj.put("paymentMode", "DC");
            jsonObj.put("uuid", ynwUUID);

        } catch (JSONException e) {
            e.printStackTrace();
        }


        RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), jsonObj.toString());
        Call<CheckSumModel> call = apiService.generateHash(body, accountID);

        call.enqueue(new Callback<CheckSumModel>() {
            @Override
            public void onResponse(Call<CheckSumModel> call, Response<CheckSumModel> response) {

                try {

                    if (mDialog.isShowing())
                        Config.closeDialog(getParent(), mDialog);

                    Config.logV("URL---------------" + response.raw().request().url().toString().trim());
                    Config.logV("Response--code-------------------------" + response.code());


                    if (response.code() == 200) {


                        CheckSumModel response_data = response.body();
                        Config.logV("Response--Sucess-------------------------" + new Gson().toJson(response.body()));

                    } else {
                        String responseerror = response.errorBody().string();
                        Config.logV("Response--error-------------------------" + responseerror);
                    }


                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(Call<CheckSumModel> call, Throwable t) {
                Config.logV("Fail---------------" + t.toString());
                if (mDialog.isShowing())
                    Config.closeDialog(getParent(), mDialog);

            }
        });
    }

    private void ApiCheckin(String txt_addnote) {

        phoneNumber = phoneNumberValue.getText().toString();
        uuid = UUID.randomUUID().toString();


        ApiInterface apiService =
                ApiClient.getClient(mContext).create(ApiInterface.class);


        final Dialog mDialog = Config.getProgressDialog(mContext, mContext.getResources().getString(R.string.dialog_log_in));
        mDialog.show();

        String uniqueID = UUID.randomUUID().toString();
        Date c = Calendar.getInstance().getTime();
        System.out.println("Current time => " + c);
        String formattedDate;
        if (mFrom.equalsIgnoreCase("checkin") || mFrom.equalsIgnoreCase("searchdetail_checkin") || mFrom.equalsIgnoreCase("favourites")) {

            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
            formattedDate = df.format(c);
        } else {
            formattedDate = selectedDateFormat;
        }


        JSONObject qjsonObj = new JSONObject();
        JSONObject queueobj = new JSONObject();
        JSONObject waitobj = new JSONObject();
        JSONObject service = new JSONObject();
        JSONArray waitlistArray = new JSONArray();
        try {

            qjsonObj.put("id", queueId);
            queueobj.put("date", formattedDate);
            queueobj.put("consumerNote", txt_addnote);
            queueobj.put("waitlistPhonenumber", phoneNumber);


            JSONArray couponList = new JSONArray();

            for (int i = 0; i < couponArraylist.size(); i++) {

                couponList.put(couponArraylist.get(i));

            }

            Log.i("kooooooo", couponList.toString());

            queueobj.put("coupons", couponList);


            Log.i("couponList", couponList.toString());

            service.put("id", serviceId);
            if (enableparty) {
                queueobj.put("partySize", editpartysize.getText().toString());
            }

            if (familyMEmID == 0) {
                familyMEmID = consumerID;
            }

            if (MultiplefamilyList.size() > 0) {
                for (int i = 0; i < MultiplefamilyList.size(); i++) {
                    JSONObject waitobj1 = new JSONObject();
                    waitobj1.put("id", MultiplefamilyList.get(i).getId());
                    waitlistArray.put(waitobj1);
                }
            } else {
                waitobj.put("id", familyMEmID);
                waitlistArray.put(waitobj);
            }


            queueobj.putOpt("service", selectedService);
            queueobj.putOpt("queue", qjsonObj);
            queueobj.putOpt("waitlistingFor", waitlistArray);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        Log.i("QueueObj Checkin", queueobj.toString());
        RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), queueobj.toString());
        Call<ResponseBody> call = apiService.Checkin(modifyAccountID, body);
        Config.logV("JSON--------------" + new Gson().toJson(queueobj.toString()));
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                try {

                    if (mDialog.isShowing())
                        Config.closeDialog(getParent(), mDialog);

                    Config.logV("URL---------------" + response.raw().request().url().toString().trim());
                    Config.logV("Response--code-------------------------" + response.code());
                    Config.logV("Response--code-------------------------" + response.body());

                    MultiplefamilyList.clear();
                    if (response.code() == 200) {


                        SharedPreference.getInstance(mContext).setValue("refreshcheckin", "true");
                        txt_message = "";

                        JSONObject reader = new JSONObject(response.body().string());
                        Iterator iteratorObj = reader.keys();

                        while (iteratorObj.hasNext()) {
                            String getJsonObj = (String) iteratorObj.next();
                            System.out.println("KEY: " + "------>" + getJsonObj);
                            value = reader.getString(getJsonObj);


                        }


                        System.out.println("VALUE: " + "------>" + value);
                        // finish();
                        Config.logV("Response--isPrepayment------------------" + isPrepayment);
                        if (isPrepayment) {
                            if (!showPaytmWallet && !showPayU) {

                                //Toast.makeText(mContext,"Pay amount by Cash",Toast.LENGTH_LONG).show();
                            } else {
                                try {
                                    final BottomSheetDialog dialog = new BottomSheetDialog(mContext);
                                    dialog.setContentView(R.layout.prepayment);
                                    dialog.show();


                                    Button btn_paytm = (Button) dialog.findViewById(R.id.btn_paytm);
                                    Button btn_payu = (Button) dialog.findViewById(R.id.btn_payu);
                                    if (showPaytmWallet) {
                                        btn_paytm.setVisibility(View.VISIBLE);
                                    } else {
                                        btn_paytm.setVisibility(View.GONE);
                                    }
                                    if (showPayU) {
                                        btn_payu.setVisibility(View.VISIBLE);
                                    } else {
                                        btn_payu.setVisibility(View.GONE);
                                    }
                                    final EditText edt_message = (EditText) dialog.findViewById(R.id.edt_message);
                                    TextView txtamt = (TextView) dialog.findViewById(R.id.txtamount);

                                    TextView txtprepayment = (TextView) dialog.findViewById(R.id.txtprepayment);

                                    txtprepayment.setText("Prepayment Amount ");

//                                    DecimalFormat format = new DecimalFormat("0.00");
                                    txtamt.setText("Rs." + Config.getAmountinTwoDecimalPoints((Double.parseDouble(sAmountPay))));
                                    Typeface tyface1 = Typeface.createFromAsset(mContext.getAssets(),
                                            "fonts/Montserrat_Bold.otf");
                                    txtamt.setTypeface(tyface1);
                                    btn_payu.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            //ApiGenerateHash(value, sAmountPay, accountID);
                                            // new PaymentGateway(mContext, mActivity).ApiGenerateHashTest(value, sAmountPay, accountID, "checkin");

                                            Config.logV("Account ID --------------" + modifyAccountID);
                                            new PaymentGateway(mContext, mActivity).ApiGenerateHash1(value, sAmountPay, modifyAccountID, Constants.PURPOSE_PREPAYMENT, "checkin");
                                            dialog.dismiss();
                                        }
                                    });

                                    btn_paytm.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {


                                            Config.logV("Account ID --------Paytm------" + modifyAccountID);
                                            PaytmPayment payment = new PaytmPayment(mContext);
                                            payment.ApiGenerateHashPaytm(value, sAmountPay, modifyAccountID, Constants.PURPOSE_PREPAYMENT, mContext, mActivity, "");
                                            //payment.generateCheckSum(sAmountPay);
                                            dialog.dismiss();
                                            //ApiGenerateHash(value, sAmountPay, accountID);
                                        }
                                    });
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }


                        } else {
                            Toast.makeText(mContext, toastMessage, Toast.LENGTH_LONG).show();
                            finish();
                        }

                        if(livetrack){
                            Intent checkinShareLocations = new Intent(mContext, CheckinShareLocation.class);
                            checkinShareLocations.putExtra("waitlistPhonenumber", phoneNumber);
                            checkinShareLocations.putExtra("uuid", value);
                            checkinShareLocations.putExtra("accountID", modifyAccountID);
                            checkinShareLocations.putExtra("title", title);
                            checkinShareLocations.putExtra("terminology", terminology);
                            checkinShareLocations.putExtra("calcMode", calcMode);
                            checkinShareLocations.putExtra("queueStartTime",mQueueTimeSlotList.get(0).getQueueSchedule().getTimeSlots().get(0).getsTime());
                            checkinShareLocations.putExtra("queueEndTime",mQueueTimeSlotList.get(0).getQueueSchedule().getTimeSlots().get(0).geteTime());
                            startActivity(checkinShareLocations);
                        }


                    } else {
                        txt_message = "";
                        if (response.code() == 422) {

                            String errorString = response.errorBody().string();

                            Config.logV("Error String-----------" + errorString);
                            Map<String, String> tokens = new HashMap<String, String>();
                            tokens.put("Customer", Config.toTitleCase(mSearchTerminology.getCustomer()));
                            tokens.put("provider", mSearchTerminology.getProvider());
                            tokens.put("arrived", mSearchTerminology.getArrived());
                            tokens.put("waitlisted", mSearchTerminology.getWaitlist());

                            tokens.put("start", mSearchTerminology.getStart());
                            tokens.put("cancelled", mSearchTerminology.getCancelled());
                            tokens.put("done", mSearchTerminology.getDone());


                            StringBuffer sb = new StringBuffer();

                            Pattern p3 = Pattern.compile("\\[(.*?)\\]");

                            Matcher matcher = p3.matcher(errorString);
                            while (matcher.find()) {
                                System.out.println(matcher.group(1));
                                matcher.appendReplacement(sb, tokens.get(matcher.group(1)));
                            }
                            matcher.appendTail(sb);

                            System.out.println("SubString@@@@@@@@@@@@@" + sb.toString());


                            Toast.makeText(mContext, sb.toString(), Toast.LENGTH_LONG).show();
                        } else {
                            String responseerror = response.errorBody().string();
                            Config.logV("Response--error-------------------------" + responseerror);
                            if (response.code() != 419)
                                Toast.makeText(mContext, responseerror, Toast.LENGTH_LONG).show();
                        }
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
                    Config.closeDialog(getParent(), mDialog);

            }
        });


    }

    // Dialog mDialog1 = null;

    public static void launchPaymentFlow(String amount, CheckSumModel checksumModel) {
        PayUmoneyConfig payUmoneyConfig = PayUmoneyConfig.getInstance();

        // payUmoneyConfig.setPayUmoneyActivityTitle("Buy" + getResources().getString(R.string.nike_power_run));
        payUmoneyConfig.setDoneButtonText("Pay Rs." + amount);

        //  Config.logV("Response--PayU-------------------------" + checksumModel.getProductinfo().get(0).toString());

        String firstname = SharedPreference.getInstance(mContext).getStringValue("firstname", "");
        String lastname = SharedPreference.getInstance(mContext).getStringValue("lastname", "");

        String mobile = SharedPreference.getInstance(mContext).getStringValue("mobile", "");


        PayUmoneySdkInitializer.PaymentParam.Builder builder = new PayUmoneySdkInitializer.PaymentParam.Builder();
        builder.setAmount(convertStringToDouble(amount))
                .setTxnId(checksumModel.getTxnid())
                .setPhone(mobile)
                // .setProductName(checksumModel.getProductinfo().getPaymentParts().get(0).toString())
                .setProductName(checksumModel.getProductinfo().getPaymentParts().get(0).toString())
                .setFirstName(firstname)
                .setEmail(checksumModel.getEmail())
                .setsUrl(checksumModel.getSuccessUrl())
                .setfUrl(checksumModel.getFailureUrl())
                .setUdf1("")
                .setUdf2("")
                .setUdf3("")
                .setUdf4("")
                .setUdf5("")
                .setUdf6("")
                .setUdf7("")
                .setUdf8("")
                .setUdf9("")
                .setUdf10("")
                .setIsDebug(true)
                .setKey(checksumModel.getMerchantKey())
                .setMerchantId(checksumModel.getMerchantId());

        try {
            PayUmoneySdkInitializer.PaymentParam mPaymentParams = builder.build();
            if (checksumModel.getChecksum().isEmpty() || checksumModel.getChecksum().equals("")) {
                Toast.makeText(mContext, "Could not generate hash", Toast.LENGTH_SHORT).show();
            } else {

                Config.logV("Checksum id---22222222222222--------" + checksumModel.getChecksum());
                mPaymentParams.setMerchantHash(checksumModel.getChecksum());
                PayUmoneyFlowManager.startPayUMoneyFlow(mPaymentParams, mActivity, R.style.PayUMoney, true);
            }
        } catch (Exception e) {
            Toast.makeText(mContext, e.getMessage(), Toast.LENGTH_LONG).show();

            // mTxvBuy.setEnabled(true);
        }
    }


    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //   mTxvBuy.setEnabled(true);

        if (requestCode == PayUmoneyFlowManager.REQUEST_CODE_PAYMENT && resultCode == RESULT_OK && data != null) {


            TransactionResponse transactionResponse = data.getParcelableExtra(PayUmoneyFlowManager.INTENT_EXTRA_TRANSACTION_RESPONSE);
            ResultModel resultModel = data.getParcelableExtra(PayUmoneyFlowManager.ARG_RESULT);

            if (transactionResponse != null && transactionResponse.getPayuResponse() != null) {

                if (transactionResponse.getTransactionStatus().equals(TransactionResponse.TransactionStatus.SUCCESSFUL)) {
                    showAlert("Payment Successful");
                    finish();
                } else if (transactionResponse.getTransactionStatus().equals(TransactionResponse.TransactionStatus.CANCELLED)) {
                    showAlert("Payment Cancelled");
                } else if (transactionResponse.getTransactionStatus().equals(TransactionResponse.TransactionStatus.FAILED)) {
                    showAlert("Payment Failed");
                }

            } else if (resultModel != null && resultModel.getError() != null) {
                Toast.makeText(this, "Error check log", Toast.LENGTH_SHORT).show();
            } else {
                //  Toast.makeText(this, "Both objects are null", Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == PayUmoneyFlowManager.REQUEST_CODE_PAYMENT && resultCode == RESULT_CANCELED) {
            showAlert("Payment Cancelled");
        }
    }

    private static Double convertStringToDouble(String str) {
        return Double.parseDouble(str);
    }

    private void showAlert(String msg) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setMessage(msg);
        alertDialog.setCancelable(false);
        alertDialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        alertDialog.show();
    }

    static ArrayList<FamilyArrayModel> MultiplefamilyList = new ArrayList<>();

    public static void refreshMultipleMEmList(ArrayList<FamilyArrayModel> familyList) {
        MultiplefamilyList.clear();
        MultiplefamilyList.addAll(familyList);

        recycle_family.setVisibility(View.VISIBLE);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(mContext);
        recycle_family.setLayoutManager(mLayoutManager);
        MultipleFamilyMemberAdapter mFamilyAdpater = new MultipleFamilyMemberAdapter(familyList, mContext, mActivity);
        recycle_family.setAdapter(mFamilyAdpater);
        mFamilyAdpater.notifyDataSetChanged();
        LSinglemember.setVisibility(View.GONE);
    }

    SearchViewDetail mBusinessDataList;

    private void ApiSearchViewDetail(final String muniqueID) {


        ApiInterface apiService =
                ApiClient.getClientS3Cloud(mContext).create(ApiInterface.class);


        final Dialog mDialog = Config.getProgressDialog(mContext, mContext.getResources().getString(R.string.dialog_log_in));
        mDialog.show();

        Date currentTime = new Date();
        final SimpleDateFormat sdf = new SimpleDateFormat(
                "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US);
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        System.out.println("UTC time: " + sdf.format(currentTime));


        Call<SearchViewDetail> call = apiService.getSearchViewDetail(Integer.parseInt(muniqueID), sdf.format(currentTime));

        call.enqueue(new Callback<SearchViewDetail>() {
            @Override
            public void onResponse(Call<SearchViewDetail> call, Response<SearchViewDetail> response) {

                try {

                    if (mDialog.isShowing())
                        Config.closeDialog(getParent(), mDialog);

                    Config.logV("URL---------------" + response.raw().request().url().toString().trim());
                    Config.logV("Response--code-----detail--------------------" + response.code());

                    if (response.code() == 200) {

                        mBusinessDataList = response.body();
                        sector = mBusinessDataList.getServiceSector().getDomain();
                        subsector = mBusinessDataList.getServiceSubSector().getSubDomain();
                        providerId = mBusinessDataList.getId();
                        APISector(sector, subsector);


                    }


                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(Call<SearchViewDetail> call, Throwable t) {
                // Log error here since request failed
                Config.logV("Fail---------------" + t.toString());
                if (mDialog.isShowing())
                    Config.closeDialog(getParent(), mDialog);

            }
        });

    }

    private void ApiJaldeegetS3Coupons(String uniqueID) {

        ApiInterface apiService =
                ApiClient.getClientS3Cloud(mContext).create(ApiInterface.class);

        Date currentTime = new Date();
        final SimpleDateFormat sdf = new SimpleDateFormat(
                "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US);
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        System.out.println("UTC time: " + sdf.format(currentTime));

        Call<ArrayList<CoupnResponse>> call = apiService.getCoupanList(Integer.parseInt(uniqueID), sdf.format(currentTime));

        call.enqueue(new Callback<ArrayList<CoupnResponse>>() {
            @Override
            public void onResponse(Call<ArrayList<CoupnResponse>> call, Response<ArrayList<CoupnResponse>> response) {

                try {

                    Config.logV("Response---------------------------" + response.body().toString());
                    Config.logV("URL-response--------------" + response.raw().request().url().toString().trim());
                    Config.logV("Response--code-------------------------" + response.code());


                    if (response.code() == 200) {
                        s3couponList.clear();
                        s3couponList = response.body();
                        Log.i("CouponResponse", s3couponList.toString());

                        if(s3couponList.size()!=0){
                            coupon_link.setVisibility(View.VISIBLE);
                        }else {
                            coupon_link.setVisibility(View.GONE);
                        }

                    }


                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(Call<ArrayList<CoupnResponse>> call, Throwable t) {
                // Log error here since request failed
                Config.logV("Fail---------------" + t.toString());

            }
        });
    }


//    private void ApiCommunicate(String waitListId, String accountID, String message, final BottomSheetDialog dialog) {
//
//        ApiInterface apiService =
//                ApiClient.getClient(mContext).create(ApiInterface.class);
//        MediaType type = MediaType.parse("*/*");
//        MultipartBody.Builder mBuilder = new MultipartBody.Builder();
//        mBuilder.setType(MultipartBody.FORM);
//        mBuilder.addFormDataPart("message", message);
//        for (int i = 0; i < imagePathList.size(); i++) {
////            if(imagePathList.contains("content://")) {
////                Uri imageUri = Uri.parse(imagePathList.get(i));
////                File file = new File(String.valueOf(imageUri));
////                mBuilder.addFormDataPart("attachments", file.getName(), RequestBody.create(type, file));
////            }
//            try {
//                bitmap = MediaStore.Images.Media.getBitmap(mContext.getApplicationContext().getContentResolver(), Uri.parse(imagePathList.get(i)));
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//            if (bitmap != null) {
//                path = saveImage(bitmap);
//            }
////            else{
////                path = getRealFilePath(Uri.parse(imagePathList.get(0)));
////            }
//
//            File file = new File(imagePathList.get(i));
//            mBuilder.addFormDataPart("attachments", file.getName(), RequestBody.create(type, file));
//        }
//        RequestBody requestBody = mBuilder.build();
//
//
//        final Dialog mDialog = Config.getProgressDialog(mContext, mContext.getResources().getString(R.string.dialog_log_in));
//        mDialog.show();
//        JSONObject jsonObj = new JSONObject();
//        try {
//            jsonObj.put("communicationMessage", message);
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//        RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), jsonObj.toString());
//
//        Call<ResponseBody> call = apiService.WaitListMessage(waitListId, String.valueOf(accountID), requestBody);
//
//        call.enqueue(new Callback<ResponseBody>() {
//            @Override
//            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
//
//                try {
//
//                    if (mDialog.isShowing())
//                        Config.closeDialog(CheckIn.this, mDialog);
//
//                    Config.logV("URL---------------" + response.raw().request().url().toString().trim());
//                    Config.logV("Response--code-------------------------" + response.code());
//
//                    if (response.code() == 200) {
//                        Toast.makeText(mContext, "Message sent successfully", Toast.LENGTH_LONG).show();
//                        imagePathList.clear();
//                        dialog.dismiss();
//
//
//                    } else {
//                        if (response.code() == 422) {
//                            Toast.makeText(mContext, response.errorBody().string(), Toast.LENGTH_SHORT).show();
//                        }
//                    }
//
//
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//
//            }
//
//            @Override
//            public void onFailure(Call<ResponseBody> call, Throwable t) {
//                // Log error here since request failed
//                Config.logV("Fail---------------" + t.toString());
//                if (mDialog.isShowing())
//                    Config.closeDialog( CheckIn.this, mDialog);
//
//            }
//        });
//    }

}
