package com.nv.youneverwait.Fragment;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.design.widget.BottomSheetDialog;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.internal.LinkedTreeMap;
import com.nv.youneverwait.R;
import com.nv.youneverwait.activities.MessageActivity;
import com.nv.youneverwait.activities.SwipeGalleryImage;
import com.nv.youneverwait.adapter.ContactDetailAdapter;
import com.nv.youneverwait.adapter.LocationCheckinAdapter;
import com.nv.youneverwait.adapter.SearchLocationAdapter;
import com.nv.youneverwait.adapter.VirtualFieldAdapter;
import com.nv.youneverwait.callback.LocationCheckinCallback;
import com.nv.youneverwait.callback.SearchLocationAdpterCallback;
import com.nv.youneverwait.common.Config;
import com.nv.youneverwait.connection.ApiClient;
import com.nv.youneverwait.connection.ApiInterface;
import com.nv.youneverwait.custom.CircleTransform;
import com.nv.youneverwait.custom.CustomTypefaceSpan;
import com.nv.youneverwait.custom.ResizableCustomView;
import com.nv.youneverwait.model.ContactModel;
import com.nv.youneverwait.model.WorkingModel;
import com.nv.youneverwait.response.FavouriteModel;
import com.nv.youneverwait.response.QueueList;
import com.nv.youneverwait.response.SearchCheckInMessage;
import com.nv.youneverwait.response.SearchLocation;
import com.nv.youneverwait.response.SearchService;
import com.nv.youneverwait.response.SearchSetting;
import com.nv.youneverwait.response.SearchTerminology;
import com.nv.youneverwait.response.SearchViewDetail;
import com.nv.youneverwait.response.SearchVirtualFields;
import com.nv.youneverwait.utils.SharedPreference;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


/**
 * Created by sharmila on 24/7/18.
 */


public class SearchDetailViewFragment extends RootFragment implements SearchLocationAdpterCallback, LocationCheckinCallback {

    Context mContext;

    SearchViewDetail mBusinessDataList;
    ArrayList<SearchViewDetail> mSearchGallery;
    ArrayList<SearchLocation> mSearchLocList;

    SearchSetting mSearchSettings;
    SearchTerminology mSearchTerminology;

    ArrayList<QueueList> mSearchQueueList;

    ArrayList<SearchService> mServicesList;

    ArrayList<SearchCheckInMessage> mSearchmCheckMessageList;

    ArrayList<SearchCheckInMessage> mSearchmCheckListShow = new ArrayList<>();


    TextView tv_busName, tv_domain, tv_desc, tv_msg;

    RecyclerView mRecyLocDetail, mRecycle_virtualfield, mrecycle_contactdetail;
    SearchLocationAdapter mSearchLocAdapter;
    ImageView mImgeProfile, mImgthumbProfile, mImgthumbProfile2, mImgthumbProfile1;

    int mProvoderId;
    ArrayList<String> ids;
    String uniqueID;

    TextView tv_ImageViewText, tv_Moredetails, tv_contactdetails;
    RatingBar rating;
    SearchLocationAdpterCallback mInterface;
    LocationCheckinCallback callback;
    String location;
    ImageView ic_fav;

    boolean flag_more = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View row = inflater.inflate(R.layout.searchdetails, container, false);

        mContext = getActivity();
        mRecyLocDetail = (RecyclerView) row.findViewById(R.id.mSearchLoc);
        mRecycle_virtualfield = (RecyclerView) row.findViewById(R.id.mrecycle_virtualfield);
        mrecycle_contactdetail = (RecyclerView) row.findViewById(R.id.mrecycle_contactdetail);

        rating = (RatingBar) row.findViewById(R.id.mRatingBar);
        tv_contactdetails = (TextView) row.findViewById(R.id.txt_contactdetails);
        count = 0;
        mBusinessDataList = new SearchViewDetail();
        mSearchGallery = new ArrayList<>();
        mSearchLocList = new ArrayList<>();
        mSearchSettings = new SearchSetting();
        mSearchTerminology = new SearchTerminology();
        mSearchQueueList = new ArrayList<>();
        mServicesList = new ArrayList<>();
        mSearchmCheckMessageList = new ArrayList<>();
        ids = new ArrayList<>();
        callback = (LocationCheckinCallback) this;


        TextView tv_title = (TextView) row.findViewById(R.id.toolbartitle);
        tv_title.setVisibility(View.INVISIBLE);

        ImageView iBackPress = (ImageView) row.findViewById(R.id.backpress);
        iBackPress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // what do you want here
                getFragmentManager().popBackStack();
            }
        });


        Bundle bundle = this.getArguments();
        if (bundle != null) {
            uniqueID = bundle.getString("uniqueID");

        }

        mRecyLocDetail.setNestedScrollingEnabled(false);

        Config.logV("UNIUE ID---------1111-------" + uniqueID);
        tv_busName = (TextView) row.findViewById(R.id.txtbus_name);
        tv_msg = (TextView) row.findViewById(R.id.txtmsg);
        tv_domain = (TextView) row.findViewById(R.id.txt_domain);
        mImgeProfile = (ImageView) row.findViewById(R.id.i_profile);
        mImgthumbProfile = (ImageView) row.findViewById(R.id.iThumb_profile);
        mImgthumbProfile2 = (ImageView) row.findViewById(R.id.iThumb_profile2);
        tv_ImageViewText = (TextView) row.findViewById(R.id.mImageViewText);
        mImgthumbProfile1 = (ImageView) row.findViewById(R.id.iThumb_profile1);
        ic_fav = (ImageView) row.findViewById(R.id.txtfav);
        tv_Moredetails = (TextView) row.findViewById(R.id.txtMoredetails);

        //  tv_exp = (TextView) row.findViewById(R.id.txt_expe);
        tv_desc = (TextView) row.findViewById(R.id.txt_bus_desc);

        Typeface tyface = Typeface.createFromAsset(mContext.getAssets(),
                "fonts/Montserrat_Bold.otf");
        tv_busName.setTypeface(tyface);
        tv_ImageViewText.setTypeface(tyface);


        ApiSearchViewDetail(uniqueID);
        ApiSearchGallery(uniqueID);
        ApiSearchViewTerminology(uniqueID);
        ApiSearchVirtualFields(uniqueID);

        tv_Moredetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!flag_more) {
                    flag_more = true;
                    mRecycle_virtualfield.setVisibility(View.VISIBLE);
                    Config.logV("Domain Size@@@@@@@@@@@@@" + domainVirtual.size());
                    Config.logV("Subdomain Size@@@@@@@@@@@@@" + sub_domainVirtual.size());

                    tv_Moredetails.setText("Click here to view Less Details");
                    tv_Moredetails.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.icon_up_arrow_blue, 0);
                    RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(mContext);
                    mRecycle_virtualfield.setLayoutManager(mLayoutManager);
                    mAdapter = new VirtualFieldAdapter(domainVirtual, mContext);
                    mRecycle_virtualfield.setAdapter(mAdapter);
                    mAdapter.notifyDataSetChanged();
                } else {
                    flag_more = false;
                    tv_Moredetails.setText("Click here to view More Details");
                    tv_Moredetails.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.icon_down_arrow_blue, 0);
                    mRecycle_virtualfield.setVisibility(View.GONE);
                }
            }
        });


        mInterface = (SearchLocationAdpterCallback) this;

        tv_msg.setEnabled(false);
        tv_msg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Config.logV("Provider iD--------------" + String.valueOf(mProvoderId));

                final BottomSheetDialog dialog = new BottomSheetDialog(mContext, R.style.DialogStyle);
                dialog.setContentView(R.layout.reply);
                dialog.show();

                final Button btn_send = (Button) dialog.findViewById(R.id.btn_send);
                Button btn_cancel = (Button) dialog.findViewById(R.id.btn_cancel);
                final EditText edt_message = (EditText) dialog.findViewById(R.id.edt_message);
                TextView txtsendmsg = (TextView) dialog.findViewById(R.id.txtsendmsg);
                txtsendmsg.setVisibility(View.VISIBLE);
                txtsendmsg.setText("Message to " + tv_busName.getText().toString());
                btn_send.setText("SEND");

                edt_message.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void afterTextChanged(Editable arg0) {
                        if (edt_message.getText().toString().length() > 1) {
                            btn_send.setEnabled(true);
                            btn_send.setClickable(true);
                            btn_send.setBackground(mContext.getResources().getDrawable(R.drawable.roundedrect_blue));
                        } else {
                            btn_send.setEnabled(false);
                            btn_send.setClickable(false);
                            btn_send.setBackground(mContext.getResources().getDrawable(R.drawable.btn_checkin_grey));
                        }
                    }

                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                    }
                });
                btn_send.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        String modifyAccountID = String.valueOf(mProvoderId);
                        ApiCommunicate(modifyAccountID, edt_message.getText().toString(), dialog);
                        // ApiSearchViewTerminology(modifyAccountID);
                        //dialog.dismiss();

                    }
                });

                btn_cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
            }
        });
        return row;
    }


    private void ApiCommunicate(String accountID, String message, final BottomSheetDialog mBottomDialog) {


        ApiInterface apiService =
                ApiClient.getClient(mContext).create(ApiInterface.class);


        final Dialog mDialog = Config.getProgressDialog(mContext, mContext.getResources().getString(R.string.dialog_log_in));
        mDialog.show();


        JSONObject jsonObj = new JSONObject();
        try {
            jsonObj.put("communicationMessage", message);


        } catch (JSONException e) {
            e.printStackTrace();
        }
        RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), jsonObj.toString());
        Call<ResponseBody> call = apiService.PostMessage(accountID, body);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                try {

                    if (mDialog.isShowing())
                        Config.closeDialog(getActivity(), mDialog);

                    Config.logV("URL---------------" + response.raw().request().url().toString().trim());
                    Config.logV("Response--code-------------------------" + response.code());

                    if (response.code() == 200) {

                        Toast.makeText(mContext, "Message send successfully", Toast.LENGTH_LONG).show();
                        mBottomDialog.dismiss();

                    }


                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                // Log error here since request failed
                Config.logV("Fail---------------" + t.toString());
                mBottomDialog.dismiss();
                if (mDialog.isShowing())
                    Config.closeDialog(getActivity(), mDialog);

            }
        });


    }

    public void UpdateGallery(final ArrayList<SearchViewDetail> mGallery) {
        //  Picasso.with(this).load(mGallery.get(0).getUrl()).fit().into(mImgeProfile);

        Config.logV("Gallery--------------333-----" + mGallery.size());
        try {
            if (mGallery.size() > 0) {

                if (mGallery.get(0).getUrl() != null) {
                    mImgeProfile.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            Config.logV("Gallery------------------------------" + mGallery.size());
                            ArrayList<String> mGalleryList = new ArrayList<>();
                            for (int i = 0; i < mGallery.size(); i++) {
                        /*SearchViewDetail data = new SearchViewDetail();
                        data.setUrl(mGallery.get(i).getUrl());*/
                                mGalleryList.add(mGallery.get(i).getUrl());
                            }


                            boolean mValue = SwipeGalleryImage.SetGalleryList(mGalleryList, v.getContext());
                            if (mValue) {

                                Intent intent = new Intent(mContext, SwipeGalleryImage.class);
                                startActivity(intent);
                            }


                        }
                    });
                }

                if (mGallery.get(1).getUrl() != null) {
                    mImgthumbProfile.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            Config.logV("Gallery------------------------------" + mGallery.size());
                            ArrayList<String> mGalleryList = new ArrayList<>();
                            for (int i = 1; i < mGallery.size(); i++) {
                        /*SearchViewDetail data = new SearchViewDetail();
                        data.setUrl(mGallery.get(i).getUrl());*/
                                mGalleryList.add(mGallery.get(i).getUrl());
                            }


                            boolean mValue = SwipeGalleryImage.SetGalleryList(mGalleryList, v.getContext());
                            if (mValue) {

                                Intent intent = new Intent(mContext, SwipeGalleryImage.class);
                                startActivity(intent);
                            }


                        }
                    });
                }

                if (mGallery.get(2).getUrl() != null) {
                    mImgthumbProfile1.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            Config.logV("Gallery------------------------------" + mGallery.size());
                            ArrayList<String> mGalleryList = new ArrayList<>();
                            for (int i = 1; i < mGallery.size(); i++) {
                        /*SearchViewDetail data = new SearchViewDetail();
                        data.setUrl(mGallery.get(i).getUrl());*/
                                mGalleryList.add(mGallery.get(i).getUrl());
                            }


                            boolean mValue = SwipeGalleryImage.SetGalleryList(mGalleryList, v.getContext());
                            if (mValue) {

                                Intent intent = new Intent(mContext, SwipeGalleryImage.class);
                                startActivity(intent);
                            }


                        }
                    });
                }
            }

            Picasso.with(mContext).load(mGallery.get(0).getUrl()).placeholder(R.drawable.icon_noimage).error(R.drawable.icon_noimage).transform(new CircleTransform()).fit().into(mImgeProfile);

            if (mGallery.size() > 1) {
                mImgthumbProfile.setVisibility(View.VISIBLE);
                // Picasso.with(this).load(mGallery.get(1).getUrl()).fit().into(mImgthumbProfile);

                Picasso.with(mContext).load(mGallery.get(1).getUrl()).placeholder(R.drawable.icon_noimage).error(R.drawable.icon_noimage).transform(new CircleTransform()).fit().into(mImgthumbProfile);


                if (mGallery.size() == 3) {
                    mImgthumbProfile1.setVisibility(View.VISIBLE);
                    Config.logV("Gallery--------");
                    Picasso.with(mContext).load(mGallery.get(2).getUrl()).placeholder(R.drawable.icon_noimage).error(R.drawable.icon_noimage).transform(new CircleTransform()).fit().into(mImgthumbProfile1);
                } else {
                    mImgthumbProfile1.setVisibility(View.GONE);
                }

                if (mGallery.size() > 3) {

                    mImgthumbProfile1.setVisibility(View.VISIBLE);
                    Picasso.with(mContext).load(mGallery.get(2).getUrl()).placeholder(R.drawable.icon_noimage).error(R.drawable.icon_noimage).transform(new CircleTransform()).fit().into(mImgthumbProfile1);
                    mImgthumbProfile2.setVisibility(View.VISIBLE);
                    Picasso.with(mContext).load(mGallery.get(3).getUrl()).placeholder(R.drawable.icon_noimage).error(R.drawable.icon_noimage).transform(new CircleTransform()).fit().into(mImgthumbProfile2);
                    tv_ImageViewText.setVisibility(View.VISIBLE);
                    tv_ImageViewText.setText(" +" + String.valueOf(mGallery.size() - 3));
                    Config.logV("Galeery--------------11111-----------" + mGallery.size());
                    mImgthumbProfile2.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            Config.logV("Gallery------------------------------" + mGallery.size());
                            ArrayList<String> mGalleryList = new ArrayList<>();
                            for (int i = 1; i < mGallery.size(); i++) {
                        /*SearchViewDetail data = new SearchViewDetail();
                        data.setUrl(mGallery.get(i).getUrl());*/
                                mGalleryList.add(mGallery.get(i).getUrl());
                            }


                            boolean mValue = SwipeGalleryImage.SetGalleryList(mGalleryList, v.getContext());
                            if (mValue) {

                                Intent intent = new Intent(mContext, SwipeGalleryImage.class);
                                startActivity(intent);
                            }


                        }
                    });
                } else {
                    mImgthumbProfile2.setVisibility(View.GONE);
                    tv_ImageViewText.setVisibility(View.GONE);
                    // mImgthumbProfile1.setVisibility(View.GONE);
                }


            } else {
                mImgthumbProfile.setVisibility(View.GONE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

  /*  ArrayList<SearchViewDetail> emails = new ArrayList<>();
    ArrayList<SearchViewDetail> phoneNumber = new ArrayList<>();*/
    ArrayList<ContactModel> contactDetail = new ArrayList<>();
    boolean isContact=false;

    //  boolean expand =false;
    public void UpdateMainUI(SearchViewDetail getBussinessData) {




        if(getBussinessData.getPhoneNumbers().size()>0) {
            for(int i=0;i<getBussinessData.getPhoneNumbers().size();i++){
                Config.logV("Phone @@@@@@@@@@@@"+getBussinessData.getPhoneNumbers().get(i).getInstance());
                ContactModel contact=new ContactModel();
                contact.setInstance(getBussinessData.getPhoneNumbers().get(i).getInstance());
                contact.setResource(getBussinessData.getPhoneNumbers().get(i).getResource());
                contact.setLabel(getBussinessData.getPhoneNumbers().get(i).getLabel());
                contactDetail.add(contact);
            }

        }

        if(getBussinessData.getEmails().size()>0) {
            for(int i=0;i<getBussinessData.getEmails().size();i++){
                ContactModel contact=new ContactModel();
                contact.setInstance(getBussinessData.getEmails().get(i).getInstance());
                contact.setResource(getBussinessData.getEmails().get(i).getResource());
                contact.setLabel(getBussinessData.getEmails().get(i).getLabel());
                contactDetail.add(contact);
            }
        }


        if(getBussinessData.getPhoneNumbers().size()>0||getBussinessData.getEmails().size()>0&&contactDetail.size()>0) {

            tv_contactdetails.setVisibility(View.VISIBLE);
            tv_contactdetails.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(!isContact){
                        isContact=true;
                        mrecycle_contactdetail.setVisibility(View.VISIBLE);
                        tv_contactdetails.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.icon_eye_blue_hidden, 0);
                        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(mContext);
                        mrecycle_contactdetail.setLayoutManager(mLayoutManager);
                        ContactDetailAdapter checkAdapter = new ContactDetailAdapter(contactDetail, mContext, getActivity());
                        mrecycle_contactdetail.setAdapter(checkAdapter);
                        checkAdapter.notifyDataSetChanged();
                    }else{
                        isContact=false;
                        tv_contactdetails.setText("Contact Details");
                        tv_contactdetails.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.icon_eye_blue, 0);
                        mrecycle_contactdetail.setVisibility(View.GONE);
                    }
                }
            });

        }else{
            tv_contactdetails.setVisibility(View.GONE);
            mrecycle_contactdetail.setVisibility(View.GONE);
        }

        if (getBussinessData.getVerifyLevel() != null) {
            if (!getBussinessData.getVerifyLevel().equalsIgnoreCase("NONE")) {
                tv_busName.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.icon_verified, 0);

            } else {

                tv_busName.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
            }

        } else {
            tv_busName.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
        }


        tv_msg.setEnabled(true);
        tv_busName.setText(getBussinessData.getBusinessName());
        rating.setRating(getBussinessData.getAvgRating());


        tv_domain.setText(getBussinessData.getServiceSector().getDisplayName());


        if (getBussinessData.getBusinessDesc() != null) {
            tv_desc.setVisibility(View.VISIBLE);
            tv_desc.setText(getBussinessData.getBusinessDesc());
            tv_desc.post(new Runnable() {
                @Override
                public void run() {
                    int lineCount = tv_desc.getLineCount();
                    //Config.logV("No of line---------------" + lineCount + "Name" + inboxList.getUserName());

                    if (lineCount > 3) {
                        ResizableCustomView.doResizeTextView(mContext, tv_desc, 3, "More", true);
                    } else {

                    }
                    // Use lineCount here


                }
            });

        } else {
            tv_desc.setVisibility(View.GONE);
        }


    }

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
                        Config.closeDialog(getActivity(), mDialog);

                    Config.logV("URL---------------" + response.raw().request().url().toString().trim());
                    Config.logV("Response--code-----detail--------------------" + response.code());

                    if (response.code() == 200) {

                        mBusinessDataList = response.body();
                        Config.logV("Provider------------" + response.body().getId());
                        mProvoderId = response.body().getId();
                        UpdateMainUI(mBusinessDataList);
                        ApiFavList();
                        ApiSearchViewLocation(uniqueID);

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
                    Config.closeDialog(getActivity(), mDialog);

            }
        });


    }

    private void ApiSearchGallery(final String muniqueID) {


        ApiInterface apiService =
                ApiClient.getClientS3Cloud(mContext).create(ApiInterface.class);


        final Dialog mDialog = Config.getProgressDialog(mContext, mContext.getResources().getString(R.string.dialog_log_in));
        mDialog.show();

        Date currentTime = new Date();
        final SimpleDateFormat sdf = new SimpleDateFormat(
                "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US);
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        System.out.println("UTC time: " + sdf.format(currentTime));


        Call<ArrayList<SearchViewDetail>> call = apiService.getSearchGallery(Integer.parseInt(muniqueID), sdf.format(currentTime));

        call.enqueue(new Callback<ArrayList<SearchViewDetail>>() {
            @Override
            public void onResponse(Call<ArrayList<SearchViewDetail>> call, Response<ArrayList<SearchViewDetail>> response) {

                try {

                    if (mDialog.isShowing())
                        Config.closeDialog(getActivity(), mDialog);

                    Config.logV("URL---------------" + response.raw().request().url().toString().trim());
                    Config.logV("Response--code-----gallery--------------------" + response.code());

                    if (response.code() == 200) {

                        mSearchGallery = response.body();
                        UpdateGallery(mSearchGallery);

                    }


                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(Call<ArrayList<SearchViewDetail>> call, Throwable t) {
                // Log error here since request failed
                Config.logV("Fail---------------" + t.toString());
                if (mDialog.isShowing())
                    Config.closeDialog(getActivity(), mDialog);

            }
        });


    }


    int count = 0;

    private void ApiSearchViewLocation(final String muniqueID) {


        ApiInterface apiService =
                ApiClient.getClientS3Cloud(mContext).create(ApiInterface.class);


        final Dialog mDialog = Config.getProgressDialog(mContext, mContext.getResources().getString(R.string.dialog_log_in));
        mDialog.show();

        Date currentTime = new Date();
        final SimpleDateFormat sdf = new SimpleDateFormat(
                "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US);
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        System.out.println("UTC time: " + sdf.format(currentTime));


        Call<ArrayList<SearchLocation>> call = apiService.getSearchViewLoc(Integer.parseInt(muniqueID), sdf.format(currentTime));

        call.enqueue(new Callback<ArrayList<SearchLocation>>() {
            @Override
            public void onResponse(Call<ArrayList<SearchLocation>> call, Response<ArrayList<SearchLocation>> response) {

                try {

                    if (mDialog.isShowing())
                        Config.closeDialog(getActivity(), mDialog);

                    Config.logV("URL---------------" + response.raw().request().url().toString().trim());
                    Config.logV("Response--code--Location-----------------------" + response.code());
                    mSearchLocList.clear();

                    if (response.code() == 200) {


                        mSearchLocList = response.body();
                        for (int i = 0; i < response.body().size(); i++) {
                            ids.add(String.valueOf(response.body().get(i).getId()));
                        }
                        /*for(int i=0;i<response.body().size();i++) {
                            SearchLocation searchloc = new SearchLocation();
                            searchloc.setId(response.body().get(i).getId());
                            searchloc.setPlace(response.body().get(i).getPlace());
                            searchloc.setParkingType(response.body().get(i).getParkingType());
                            searchloc.setBaseLocation(response.body().get(i).isBaseLocation());
                            searchloc.setOpen24hours(response.body().get(i).isOpen24hours());
                            searchloc.setTraumacentre(response.body().get(i).getLocationVirtualFields().getTraumacentre());
                            searchloc.setPhysiciansemergencyservices(response.body().get(i).getLocationVirtualFields().getPhysiciansemergencyservices());
                            searchloc.setTimespec(response.body().get(i).getbSchedule().getTimespec());
                            mSearchLocList.add(searchloc);
                            ids.add(String.valueOf(response.body().get(i).getId()));
                        }*/


                        for (int k = 0; k < mSearchLocList.size(); k++) {

                            Config.logV("Location-----###########" + mSearchLocList.get(k).getId());
                            ApiCheckInMessage(mSearchLocList.get(k).getId(), "notshow", "");

                        }


                        Config.logV("mSearchLocList " + mSearchLocList.size());

                        for (int k = 0; k < mSearchLocList.size(); k++) {

                            ApiSearchViewServiceID(mSearchLocList.get(k).getId());

                        }


                    }


                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(Call<ArrayList<SearchLocation>> call, Throwable t) {
                // Log error here since request failed
                Config.logV("Fail---------------" + t.toString());
                if (mDialog.isShowing())
                    Config.closeDialog(getActivity(), mDialog);

            }
        });


    }


    BottomSheetDialog dialog;

    private void ApiCheckInMessage(final int mLocid, final String from, final String loc) {


        ApiInterface apiService =
                ApiClient.getClient(mContext).create(ApiInterface.class);


        final Dialog mDialog = Config.getProgressDialog(mContext, mContext.getResources().getString(R.string.dialog_log_in));
        mDialog.show();

        Map<String, String> query = new HashMap<>();

        query.put("location-eq", String.valueOf(mLocid));
        query.put("waitlistStatus-eq", "checkedIn,arrived");


        Call<ArrayList<SearchCheckInMessage>> call = apiService.getSearchCheckInMessage(query);

        Config.logV("Location-----###########@@@@@@" + query);

        call.enqueue(new Callback<ArrayList<SearchCheckInMessage>>() {
            @Override
            public void onResponse(Call<ArrayList<SearchCheckInMessage>> call, Response<ArrayList<SearchCheckInMessage>> response) {

                try {

                    if (mDialog.isShowing())
                        Config.closeDialog(getActivity(), mDialog);

                    Config.logV("URL----------Location-----###########@@@@@@-----" + response.raw().request().url().toString().trim());
                    Config.logV("Response--code--------Message-----------------" + response.code());

                    if (response.code() == 200) {
                        mSearchmCheckListShow.clear();
                        if (response.body().size() > 0) {
                            SearchCheckInMessage mCheckMessage = new SearchCheckInMessage();
                            mCheckMessage.setmAllSearch_checkIn(response.body());
                            mCheckMessage.setLocid(mLocid);
                            mSearchmCheckMessageList.add(mCheckMessage);
                            if (from.equalsIgnoreCase("show")) {

                                dialog = new BottomSheetDialog(mContext);
                                dialog.setContentView(R.layout.checkin_loclist);
                                dialog.setCancelable(false);
                                dialog.show();
                                TextView tv_title = (TextView) dialog.findViewById(R.id.tv_title);

                                String firstWord = "Your Check-In at ";
                                String secondWord = loc;
                                location = loc;

                                Spannable spannable = new SpannableString(firstWord + secondWord);
                                Typeface tyface_edittext2 = Typeface.createFromAsset(mContext.getAssets(),
                                        "fonts/Montserrat_Bold.otf");
                                spannable.setSpan(new CustomTypefaceSpan("sans-serif", tyface_edittext2), firstWord.length(), firstWord.length() + secondWord.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                                tv_title.setText(spannable);


                                RecyclerView checkloclist = (RecyclerView) dialog.findViewById(R.id.checkloclist);

                                Button btn_close = (Button) dialog.findViewById(R.id.btn_close);


                                btn_close.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        dialog.dismiss();
                                        refreshList();

                                    }
                                });


                                mSearchmCheckListShow = response.body();
                                RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(mContext);
                                checkloclist.setLayoutManager(mLayoutManager);
                                LocationCheckinAdapter checkAdapter = new LocationCheckinAdapter(callback, String.valueOf(mProvoderId), mSearchmCheckListShow, mContext, getActivity());
                                checkloclist.setAdapter(checkAdapter);
                                checkAdapter.notifyDataSetChanged();
                            }

                           /* Config.logV("Locationttt-----kkkk###########@@@@@@" + mLocid);
                            Config.logV("Locationttt-----size###########@@@@@@" + response.body().size());*/
                        }

                    }


                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(Call<ArrayList<SearchCheckInMessage>> call, Throwable t) {
                // Log error here since request failed
                Config.logV("Location-----###########@@@@@@-------Fail--------" + t.toString());
                if (mDialog.isShowing())
                    Config.closeDialog(getActivity(), mDialog);

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
                        Config.closeDialog(getActivity(), mDialog);

                    Config.logV("URL---------------" + response.raw().request().url().toString().trim());
                    Config.logV("Response--code----------Service---------------" + response.code());

                    if (response.code() == 200) {


                        SearchService mService = new SearchService();
                        mService.setmAllService(response.body());
                        mService.setLocid(id);
                        mServicesList.add(mService);


                        Config.logV("mServicesList" + mServicesList.size());

                        Config.logV("Count " + count);
                        count++;

                        if (count == mSearchLocList.size()) {


                            if (ids.size() > 0) {
                                ApiSearchViewID(mProvoderId, ids);
                            }


                        }


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
                    Config.closeDialog(getActivity(), mDialog);

            }
        });


    }


    private void ApiSearchViewID(int mProviderid, ArrayList<String> ids) {


        ApiInterface apiService =
                ApiClient.getClient(mContext).create(ApiInterface.class);


        final Dialog mDialog = Config.getProgressDialog(mContext, mContext.getResources().getString(R.string.dialog_log_in));
        mDialog.show();

        String idPass = "";
        for (int i = 0; i < ids.size(); i++) {
            idPass += mProviderid + "-" + ids.get(i) + ",";
        }

        Config.logV("IDS_--------------------" + idPass);
        Call<ArrayList<QueueList>> call = apiService.getSearchID(idPass);

        call.enqueue(new Callback<ArrayList<QueueList>>() {
            @Override
            public void onResponse(Call<ArrayList<QueueList>> call, Response<ArrayList<QueueList>> response) {

                try {

                    if (mDialog.isShowing())
                        Config.closeDialog(getActivity(), mDialog);

                    Config.logV("URL-------SEARCH--------" + response.raw().request().url().toString().trim());
                    Config.logV("Response--code-----SearchViewID--------------------" + response.code());

                    if (response.code() == 200) {

                        mSearchQueueList = response.body();
                        ApiSearchViewSetting(uniqueID);


                    }


                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(Call<ArrayList<QueueList>> call, Throwable t) {
                // Log error here since request failed
                Config.logV("Fail---------------" + t.toString());
                if (mDialog.isShowing())
                    Config.closeDialog(getActivity(), mDialog);

            }
        });


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
                        Config.closeDialog(getActivity(), mDialog);

                    Config.logV("URL---------------" + response.raw().request().url().toString().trim());
                    Config.logV("Response--code------Setting-------------------" + response.code());

                    if (response.code() == 200) {

                        mSearchSettings = response.body();

                        Config.logV("Location Adapter-----------------------");

                        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(mContext);
                        mRecyLocDetail.setLayoutManager(mLayoutManager);
                        mSearchLocAdapter = new SearchLocationAdapter(mBusinessDataList.getServiceSector().getDomain(), mBusinessDataList.getServiceSubSector().getSubDomain(), String.valueOf(mProvoderId), uniqueID, mInterface, mBusinessDataList.getBusinessName(), mSearchSettings, mSearchLocList, mContext, mServicesList, mSearchQueueList, mSearchmCheckMessageList);
                        mRecyLocDetail.setAdapter(mSearchLocAdapter);
                        mSearchLocAdapter.notifyDataSetChanged();
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
                    Config.closeDialog(getActivity(), mDialog);

            }
        });


    }

    VirtualFieldAdapter mAdapter;
    SearchVirtualFields resultData;
    ArrayList<SearchVirtualFields> domainVirtual = new ArrayList<>();
    ArrayList<SearchVirtualFields> sub_domainVirtual = new ArrayList<>();

    ArrayList<SearchVirtualFields> mergeResult = new ArrayList<>();

    private void ApiSearchVirtualFields(String muniqueID) {


        ApiInterface apiService =
                ApiClient.getClientS3Cloud(mContext).create(ApiInterface.class);

       /* Gson gson = new GsonBuilder()
                .registerTypeAdapter(SearchVirtualFields.class, new SearchVirtualFields.DataStateDeserializer())
                .create();*/

       /* String url=SharedPreference.getInstance(mContext).getStringValue("s3Url", "")+"/";
        Retrofit retrofit  = new Retrofit.Builder()
                .baseUrl(url)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ApiInterface apiService =retrofit.create(ApiInterface.class);*/

        final Dialog mDialog = Config.getProgressDialog(mContext, mContext.getResources().getString(R.string.dialog_log_in));
        mDialog.show();

        Date currentTime = new Date();
        final SimpleDateFormat sdf = new SimpleDateFormat(
                "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US);
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        System.out.println("UTC time: " + sdf.format(currentTime));


        Call<SearchVirtualFields> call = apiService.getVirtualFields(Integer.parseInt(muniqueID), sdf.format(currentTime));

        call.enqueue(new Callback<SearchVirtualFields>() {
            @Override
            public void onResponse(Call<SearchVirtualFields> call, Response<SearchVirtualFields> response) {

                try {

                    if (mDialog.isShowing())
                        Config.closeDialog(getActivity(), mDialog);

                    Config.logV("URL----VIRTUAL-----------" + response.raw().request().url().toString().trim());
                    Config.logV("Response--code------VIRTUAL-------------------" + response.code());

                    if (response.code() == 200) {


                        resultData = response.body();
                        if (resultData != null) {
                            tv_Moredetails.setVisibility(View.VISIBLE);
                            domainVirtual.clear();
                            domainVirtual = response.body().getDomain();
                            sub_domainVirtual.clear();
                            sub_domainVirtual = response.body().getSubdomain();


                            domainVirtual.addAll(sub_domainVirtual);


                        } else {
                            tv_Moredetails.setVisibility(View.GONE);
                        }


                    }


                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(Call<SearchVirtualFields> call, Throwable t) {
                // Log error here since request failed
                Config.logV("Fail---------------" + t.toString());
                if (mDialog.isShowing())
                    Config.closeDialog(getActivity(), mDialog);

            }
        });


    }


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
                        Config.closeDialog(getActivity(), mDialog);

                    Config.logV("URL---------------" + response.raw().request().url().toString().trim());
                    Config.logV("Response--code-----Terminl--------------------" + response.code());

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
                    Config.closeDialog(getActivity(), mDialog);

            }
        });


    }


    @Override
    public void onMethodWorkingCallback(ArrayList<WorkingModel> workingModel, String value) {
        WorkingHourFragment pfFragment = new WorkingHourFragment();
        // Config.logV("Fragment context-----------" + mFragment);
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        Bundle bundle = new Bundle();
        bundle.putSerializable("workinghrlist", workingModel);
        bundle.putString("title", value);
        bundle.putString("uniqueID", uniqueID);
        pfFragment.setArguments(bundle);
        // Store the Fragment in stack
        transaction.addToBackStack(null);
        transaction.replace(R.id.mainlayout, pfFragment).commit();
    }

    @Override
    public void onMethodServiceCallback(ArrayList<SearchService> searchService, String value) {
        ServiceListFragment pfFragment = new ServiceListFragment();
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        Bundle bundle = new Bundle();
        bundle.putSerializable("servicelist", searchService);
        bundle.putString("title", value);
        bundle.putString("from", "searchdetail");
        bundle.putString("uniqueID", uniqueID);
        pfFragment.setArguments(bundle);
        // Store the Fragment in stack
        transaction.addToBackStack(null);
        transaction.replace(R.id.mainlayout, pfFragment).commit();
    }

    @Override
    public void onMethodCheckinCallback(int locID, String from, String location) {
        ApiCheckInMessage(locID, from, location);
    }


    @Override
    public void onMethodCallback() {

        Toast.makeText(mContext, "No Checkin exists at " + location, Toast.LENGTH_LONG).show();
        dialog.dismiss();
        refreshList();
    }

    public void refreshList() {
        count = 0;
        SharedPreference.getInstance(mContext).setValue("refreshcheckin", "false");
        mBusinessDataList = new SearchViewDetail();
        mSearchGallery = new ArrayList<>();
        mSearchLocList = new ArrayList<>();
        mSearchSettings = new SearchSetting();
        mSearchTerminology = new SearchTerminology();
        mSearchQueueList = new ArrayList<>();
        mServicesList = new ArrayList<>();
        mServicesList.clear();
        mSearchmCheckMessageList = new ArrayList<>();
        ids = new ArrayList<>();
        ApiSearchViewDetail(uniqueID);
        ApiSearchGallery(uniqueID);
        ApiSearchViewTerminology(uniqueID);

    }

    private void ApiAddFavo(int providerID) {


        ApiInterface apiService =
                ApiClient.getClient(mContext).create(ApiInterface.class);


        final Dialog mDialog = Config.getProgressDialog(mContext, mContext.getResources().getString(R.string.dialog_log_in));
        mDialog.show();

        Call<ResponseBody> call = apiService.AddFavourite(providerID);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                try {

                    if (mDialog.isShowing())
                        Config.closeDialog(getActivity(), mDialog);

                    Config.logV("URL---------------" + response.raw().request().url().toString().trim());
                    Config.logV("Response--code-------------------------" + response.code());

                    if (response.code() == 200) {

                        if (response.body().string().equalsIgnoreCase("true")) {
                            Toast.makeText(mContext, "Added to Favourites", Toast.LENGTH_LONG).show();
                            ApiFavList();
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
                    Config.closeDialog(getActivity(), mDialog);

            }
        });


    }

    boolean favFlag = false;
    ArrayList<FavouriteModel> mFavList = new ArrayList<>();

    private void ApiFavList() {

        Config.logV("API Call");
        final ApiInterface apiService =
                ApiClient.getClient(mContext).create(ApiInterface.class);

        Call<ArrayList<FavouriteModel>> call = apiService.getFavourites();


        call.enqueue(new Callback<ArrayList<FavouriteModel>>() {
            @Override
            public void onResponse(Call<ArrayList<FavouriteModel>> call, Response<ArrayList<FavouriteModel>> response) {

                try {

                    ic_fav.setImageResource(R.drawable.icon_favourite_line);
                    Config.logV("URL---------------" + response.raw().request().url().toString().trim());
                    Config.logV("Response--code-------------------------" + response.code());

                    if (response.code() == 200) {
                        mFavList.clear();
                        mFavList = response.body();
                        favFlag = false;
                        for (int i = 0; i < mFavList.size(); i++) {
                            Config.logV("Fav List-----##&&&-----" + mFavList.get(i).getId());
                            Config.logV("Fav Fav List--------%%%%--" + mBusinessDataList.getId());

                            if (mFavList.get(i).getId() == mBusinessDataList.getId()) {

                                favFlag = true;
                                ic_fav.setVisibility(View.VISIBLE);
                                ic_fav.setImageResource(R.drawable.icon_favourited);


                            }
                        }

                        ic_fav.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (favFlag) {
                                    AlertDialog myQuittingDialogBox = new AlertDialog.Builder(mContext)
                                            //set message, title, and icon
                                            .setTitle("Delete")
                                            .setMessage("Do you want to remove " + mBusinessDataList.getBusinessName() + " from favourite list?")


                                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {

                                                public void onClick(DialogInterface dialog, int whichButton) {
                                                    //your deleting code
                                                    dialog.dismiss();
                                                    ApiRemoveFavo(mBusinessDataList.getId());
                                                }

                                            })


                                            .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int which) {

                                                    dialog.dismiss();

                                                }
                                            })
                                            .create();
                                    myQuittingDialogBox.show();
                                } else {
                                    ApiAddFavo(mBusinessDataList.getId());
                                }
                            }
                        });

                    }


                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(Call<ArrayList<FavouriteModel>> call, Throwable t) {
                // Log error here since request failed
                Config.logV("Fail---------------" + t.toString());


            }
        });


    }


    private void ApiRemoveFavo(int providerID) {


        ApiInterface apiService =
                ApiClient.getClient(mContext).create(ApiInterface.class);


        final Dialog mDialog = Config.getProgressDialog(mContext, mContext.getResources().getString(R.string.dialog_log_in));
        mDialog.show();

        Call<ResponseBody> call = apiService.DeleteFavourite(providerID);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                try {

                    if (mDialog.isShowing())
                        Config.closeDialog(getActivity(), mDialog);

                    Config.logV("URL---------------" + response.raw().request().url().toString().trim());
                    Config.logV("Response--code-------------------------" + response.code());

                    if (response.code() == 200) {

                        if (response.body().string().equalsIgnoreCase("true")) {
                            ApiFavList();
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
                    Config.closeDialog(getActivity(), mDialog);

            }
        });


    }

    @Override
    public void onResume() {
        super.onResume();
        String refresh = SharedPreference.getInstance(mContext).getStringValue("refreshcheckin", "");
        if (refresh.equalsIgnoreCase("true")) {
            refreshList();
        }
    }
}
