package com.nv.youneverwait.Fragment;

import android.Manifest;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.BottomSheetDialog;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextWatcher;
import android.util.TypedValue;
import android.view.Gravity;
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
import com.nv.youneverwait.callback.ContactAdapterCallback;
import com.nv.youneverwait.callback.LocationCheckinCallback;
import com.nv.youneverwait.callback.SearchLocationAdpterCallback;
import com.nv.youneverwait.common.Config;
import com.nv.youneverwait.connection.ApiClient;
import com.nv.youneverwait.connection.ApiInterface;
import com.nv.youneverwait.custom.CircleTransform;
import com.nv.youneverwait.custom.CustomTypefaceSpan;
import com.nv.youneverwait.custom.ResizableCustomView;
import com.nv.youneverwait.model.ContactModel;
import com.nv.youneverwait.model.SocialMediaModel;
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
import org.w3c.dom.ls.LSException;

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


public class SearchDetailViewFragment extends RootFragment implements SearchLocationAdpterCallback, LocationCheckinCallback, ContactAdapterCallback {

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


    TextView tv_busName, tv_domain, tv_desc, tv_msg, txtMore;

    RecyclerView mRecyLocDetail, mRecycle_virtualfield;
    SearchLocationAdapter mSearchLocAdapter;
    ImageView mImgeProfile, mImgthumbProfile, mImgthumbProfile2, mImgthumbProfile1;

    int mProvoderId;
    ArrayList<String> ids;
    String uniqueID;

    TextView tv_ImageViewText, tv_Moredetails, tv_specializtion, tv_SocialMedia, tv_Gallery;
    RatingBar rating;
    SearchLocationAdpterCallback mInterface;
    ContactAdapterCallback mInterfaceContact;
    LocationCheckinCallback callback;
    String location;
    TextView tv_fav;

    boolean flag_more = false;
    ImageView ic_pin, ic_yout, ic_fac, ic_gplus, ic_twitt, ic_link, ic_jaldeeverifiedIcon;
    LinearLayout LsocialMedia;
    LinearLayout LSpecialization, LSpecialization_2;
    TextView tv_spec1, tv_spec2, tv_seeAll, tv_contact;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View row = inflater.inflate(R.layout.searchdetails, container, false);

        mContext = getActivity();
        mRecyLocDetail = (RecyclerView) row.findViewById(R.id.mSearchLoc);
        mRecycle_virtualfield = (RecyclerView) row.findViewById(R.id.mrecycle_virtualfield);

        rating = (RatingBar) row.findViewById(R.id.mRatingBar);
        // tv_contactdetails = (TextView) row.findViewById(R.id.txt_contactdetails);
        tv_specializtion = (TextView) row.findViewById(R.id.txt_specializtion);
        LSpecialization = (LinearLayout) row.findViewById(R.id.LSpecialization);
        tv_Gallery = (TextView) row.findViewById(R.id.txtGallery);
        tv_SocialMedia = (TextView) row.findViewById(R.id.txtSocialMedia);
        txtMore = (TextView) row.findViewById(R.id.txtMore);
        tv_contact = (TextView) row.findViewById(R.id.txtcontact);
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
        mInterfaceContact = (ContactAdapterCallback) this;
        //isContact = false;
        Config.logV("Refresh @@@@@@@@@@@@@@@@@@");


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
        SharedPreference.getInstance(mContext).setValue("refreshcheckin", "false");
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
        tv_fav = (TextView) row.findViewById(R.id.txtfav);
        tv_Moredetails = (TextView) row.findViewById(R.id.txtMoredetails);


        LsocialMedia = (LinearLayout) row.findViewById(R.id.LsocialMedia);
        LSpecialization_2 = (LinearLayout) row.findViewById(R.id.LSpecialization_2);
        tv_spec1 = (TextView) row.findViewById(R.id.txtspec1);
        tv_spec2 = (TextView) row.findViewById(R.id.txtspec2);
        tv_seeAll = (TextView) row.findViewById(R.id.txtSeeAll);


        ic_fac = (ImageView) row.findViewById(R.id.ic_fac);
        ic_gplus = (ImageView) row.findViewById(R.id.ic_gplus);
        ic_pin = (ImageView) row.findViewById(R.id.ic_pin);
        ic_link = (ImageView) row.findViewById(R.id.ic_link);
        ic_twitt = (ImageView) row.findViewById(R.id.ic_twitt);
        ic_yout = (ImageView) row.findViewById(R.id.ic_yout);
        ic_jaldeeverifiedIcon = (ImageView) row.findViewById(R.id.ic_jaldeeverifiedIcon);


        //  tv_exp = (TextView) row.findViewById(R.id.txt_expe);
        tv_desc = (TextView) row.findViewById(R.id.txt_bus_desc);

        Typeface tyface = Typeface.createFromAsset(mContext.getAssets(),
                "fonts/Montserrat_Bold.otf");
        tv_busName.setTypeface(tyface);
        tv_ImageViewText.setTypeface(tyface);
        tv_SocialMedia.setTypeface(tyface);
        tv_Gallery.setTypeface(tyface);
        tv_specializtion.setTypeface(tyface);
        txtMore.setTypeface(tyface);
        //tv_contactdetails.setTypeface(tyface);

        ApiSearchViewDetail(uniqueID);

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
                    tv_Moredetails.setText("See Less");
                    RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(mContext);
                    mRecycle_virtualfield.setLayoutManager(mLayoutManager);
                    mAdapter = new VirtualFieldAdapter(domainVirtual, mContext, domainVirtual.size());
                    mRecycle_virtualfield.setAdapter(mAdapter);
                    mAdapter.notifyDataSetChanged();
                } else {
                    flag_more = false;
                    tv_Moredetails.setText("See All");

                    int size = domainVirtual.size();
                    if (size == 1) {
                        size = 1;
                    } else {
                        size = 2;
                    }
                    RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(mContext);
                    mRecycle_virtualfield.setLayoutManager(mLayoutManager);
                    mAdapter = new VirtualFieldAdapter(domainVirtual, mContext, size);
                    mRecycle_virtualfield.setAdapter(mAdapter);
                    mAdapter.notifyDataSetChanged();
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
                        if (edt_message.getText().toString().length() > 1 && !edt_message.getText().toString().trim().isEmpty()) {
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



                    mImgeProfile.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            Config.logV("Gallery------------------------------" + mGallery.size());
                            ArrayList<String> mGalleryList = new ArrayList<>();

                            if (mBusinessDataList.getLogo() != null) {
                                mGalleryList.add(mBusinessDataList.getLogo().getUrl());
                            }

                            for (int i = 0; i < mGallery.size(); i++) {

                                mGalleryList.add(mGallery.get(i).getUrl());
                            }


                            boolean mValue = SwipeGalleryImage.SetGalleryList(mGalleryList, v.getContext());
                            if (mValue) {

                                Intent intent = new Intent(mContext, SwipeGalleryImage.class);
                                intent.putExtra("pos",0);
                                startActivity(intent);
                            }


                        }
                    });




                        mImgthumbProfile.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                Config.logV("Gallery------------------------------" + mGallery.size());
                                ArrayList<String> mGalleryList = new ArrayList<>();


                                if (mBusinessDataList.getLogo() != null) {

                                    mGalleryList.add(mBusinessDataList.getLogo().getUrl());
                                }
                                for (int i = 0; i < mGallery.size(); i++) {
                                    mGalleryList.add(mGallery.get(i).getUrl());
                                }


                                boolean mValue = SwipeGalleryImage.SetGalleryList(mGalleryList, v.getContext());
                                if (mValue) {

                                    Intent intent = new Intent(mContext, SwipeGalleryImage.class);
                                    intent.putExtra("pos",1);
                                    startActivity(intent);
                                }


                            }
                        });




                        mImgthumbProfile1.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                Config.logV("Gallery------------------------------" + mGallery.size());
                                ArrayList<String> mGalleryList = new ArrayList<>();


                                if (mBusinessDataList.getLogo() != null) {

                                    mGalleryList.add(mBusinessDataList.getLogo().getUrl());
                                }
                                for (int i = 0; i < mGallery.size(); i++) {

                                    mGalleryList.add(mGallery.get(i).getUrl());
                                }


                                boolean mValue = SwipeGalleryImage.SetGalleryList(mGalleryList, v.getContext());
                                if (mValue) {

                                    Intent intent = new Intent(mContext, SwipeGalleryImage.class);
                                    intent.putExtra("pos",2);
                                    startActivity(intent);
                                }


                            }
                        });


            } /*else {
                tv_Gallery.setVisibility(View.GONE);
            }*/

            Config.logV("Bussiness logo @@@@@@@@@@"+mBusinessDataList.getLogo());
            if (mBusinessDataList.getLogo() != null) {
                Picasso.with(mContext).load(mBusinessDataList.getLogo().getUrl()).placeholder(R.drawable.icon_noimage).error(R.drawable.icon_noimage).transform(new CircleTransform()).fit().into(mImgeProfile);

            } else {
                Picasso.with(mContext).load(mGallery.get(0).getUrl()).placeholder(R.drawable.icon_noimage).error(R.drawable.icon_noimage).transform(new CircleTransform()).fit().into(mImgeProfile);

            }


            if (mBusinessDataList.getLogo() != null && mGallery.size() > 0 || mGallery.size() > 1) {
                tv_Gallery.setVisibility(View.VISIBLE);
                mImgthumbProfile.setVisibility(View.VISIBLE);

                if (mBusinessDataList.getLogo() != null) {


                    Picasso.with(mContext).load(mGallery.get(0).getUrl()).placeholder(R.drawable.icon_noimage).error(R.drawable.icon_noimage).transform(new CircleTransform()).fit().into(mImgthumbProfile);


                    if (mGallery.size() == 2) {
                        mImgthumbProfile1.setVisibility(View.VISIBLE);
                        Config.logV("Gallery--------");
                        Picasso.with(mContext).load(mGallery.get(1).getUrl()).placeholder(R.drawable.icon_noimage).error(R.drawable.icon_noimage).transform(new CircleTransform()).fit().into(mImgthumbProfile1);
                    } else {
                        mImgthumbProfile1.setVisibility(View.GONE);
                    }

                    if (mGallery.size() == 3) {

                        mImgthumbProfile1.setVisibility(View.VISIBLE);
                        Config.logV("Gallery----333----");
                        Picasso.with(mContext).load(mGallery.get(1).getUrl()).placeholder(R.drawable.icon_noimage).error(R.drawable.icon_noimage).transform(new CircleTransform()).fit().into(mImgthumbProfile1);
                        mImgthumbProfile2.setVisibility(View.VISIBLE);
                        Config.logV("Gallery----3333----");
                        Picasso.with(mContext).load(mGallery.get(2).getUrl()).placeholder(R.drawable.icon_noimage).error(R.drawable.icon_noimage).transform(new CircleTransform()).fit().into(mImgthumbProfile2);
                    }

                    if (mGallery.size() > 3) {

                        mImgthumbProfile1.setVisibility(View.VISIBLE);
                        Config.logV("Gallery----4444----");
                        Picasso.with(mContext).load(mGallery.get(1).getUrl()).placeholder(R.drawable.icon_noimage).error(R.drawable.icon_noimage).transform(new CircleTransform()).fit().into(mImgthumbProfile1);

                        mImgthumbProfile2.setVisibility(View.VISIBLE);
                        Picasso.with(mContext).load(mGallery.get(2).getUrl()).placeholder(R.drawable.icon_noimage).error(R.drawable.icon_noimage).transform(new CircleTransform()).fit().into(mImgthumbProfile2);
                        tv_ImageViewText.setVisibility(View.VISIBLE);
                        tv_ImageViewText.setText(" +" + String.valueOf(mGallery.size() - 3));
                        Config.logV("Gallery-------------444----------" + mGallery.size());
                        mImgthumbProfile2.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                Config.logV("Gallery------------------------------" + mGallery.size());
                                ArrayList<String> mGalleryList = new ArrayList<>();


                                if (mBusinessDataList.getLogo() != null) {

                                    mGalleryList.add(mBusinessDataList.getLogo().getUrl());
                                }

                                for (int i = 0; i < mGallery.size(); i++) {

                                    mGalleryList.add(mGallery.get(i).getUrl());
                                }


                                boolean mValue = SwipeGalleryImage.SetGalleryList(mGalleryList, v.getContext());
                                if (mValue) {

                                    Intent intent = new Intent(mContext, SwipeGalleryImage.class);
                                    intent.putExtra("pos",3);
                                    startActivity(intent);
                                }


                            }
                        });
                    } else {
                        //mImgthumbProfile2.setVisibility(View.GONE);
                        tv_ImageViewText.setVisibility(View.GONE);
                        // mImgthumbProfile1.setVisibility(View.GONE);
                    }

                } else {


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
                                if (mBusinessDataList.getLogo() != null) {

                                    mGalleryList.add(mBusinessDataList.getLogo().getUrl());
                                }



                                for (int i = 0; i < mGallery.size(); i++) {
                        /*SearchViewDetail data = new SearchViewDetail();
                        data.setUrl(mGallery.get(i).getUrl());*/
                                    mGalleryList.add(mGallery.get(i).getUrl());
                                }


                                boolean mValue = SwipeGalleryImage.SetGalleryList(mGalleryList, v.getContext());
                                if (mValue) {

                                    Intent intent = new Intent(mContext, SwipeGalleryImage.class);
                                    intent.putExtra("pos",3);
                                    startActivity(intent);
                                }


                            }
                        });
                    } else {
                        mImgthumbProfile2.setVisibility(View.GONE);
                        tv_ImageViewText.setVisibility(View.GONE);
                        // mImgthumbProfile1.setVisibility(View.GONE);
                    }


                }
            } else {
                mImgthumbProfile.setVisibility(View.GONE);
                tv_Gallery.setVisibility(View.GONE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*  ArrayList<SearchViewDetail> emails = new ArrayList<>();
      ArrayList<SearchViewDetail> phoneNumber = new ArrayList<>();*/
    ArrayList<ContactModel> contactDetail = new ArrayList<>();
    boolean isContact = false;

    ArrayList<SocialMediaModel> socialMedia = new ArrayList<>();


    //  boolean expand =false;
    public void UpdateMainUI(final SearchViewDetail getBussinessData) {

        if (getBussinessData.getSpecialization() != null) {
            if (getBussinessData.getSpecialization().size() > 0) {


                tv_specializtion.setVisibility(View.VISIBLE);
                LSpecialization.setVisibility(View.VISIBLE);
                int size = getBussinessData.getSpecialization().size();
                if (size > 0) {
                    if (size == 1) {
                        LSpecialization.setVisibility(View.GONE);
                        LSpecialization_2.setVisibility(View.VISIBLE);
                        tv_spec1.setVisibility(View.VISIBLE);
                        tv_spec2.setVisibility(View.GONE);
                        tv_seeAll.setVisibility(View.GONE);
                        tv_spec1.setText(getBussinessData.getSpecialization().get(0).toString());

                    } else {
                        LSpecialization.setVisibility(View.GONE);
                        LSpecialization_2.setVisibility(View.VISIBLE);
                        tv_spec1.setVisibility(View.VISIBLE);
                        tv_spec2.setVisibility(View.VISIBLE);
                        tv_seeAll.setVisibility(View.VISIBLE);
                        tv_spec1.setText(getBussinessData.getSpecialization().get(0).toString());
                        tv_spec2.setText(getBussinessData.getSpecialization().get(1).toString());

                        tv_seeAll.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                LSpecialization_2.setVisibility(View.GONE);
                                LSpecialization.setVisibility(View.VISIBLE);
                                LSpecialization.removeAllViews();
                                LinearLayout parent1 = new LinearLayout(mContext);
                                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                                parent1.setOrientation(LinearLayout.VERTICAL);
                                parent1.setLayoutParams(params);
                                for (int i = 0; i < getBussinessData.getSpecialization().size(); i++) {

                                    TextView dynaText = new TextView(mContext);
                                    Typeface tyface = Typeface.createFromAsset(mContext.getAssets(),
                                            "fonts/Montserrat_Regular.otf");
                                    dynaText.setTypeface(tyface);
                                    dynaText.setText(getBussinessData.getSpecialization().get(i).toString());


                                    dynaText.setTextSize(14);
                                    dynaText.setTextColor(mContext.getResources().getColor(R.color.title_grey));
                                    //  dynaText.setPadding(5, 5, 5, 5);
                                    dynaText.setMaxLines(1);
                                    dynaText.setLayoutParams(params);


                                    params.setMargins(0, 10, 0, 0);
                                    dynaText.setGravity(Gravity.LEFT);
                                    parent1.addView(dynaText);

                                }
                                TextView dynaText = new TextView(mContext);
                                Typeface tyface = Typeface.createFromAsset(mContext.getAssets(),
                                        "fonts/Montserrat_Regular.otf");
                                dynaText.setTypeface(tyface);
                                dynaText.setText("See Less");

                                dynaText.setTextSize(14);
                                dynaText.setTextColor(mContext.getResources().getColor(R.color.title_consu));
                                // dynaText.setPadding(5, 5, 5, 5);
                                dynaText.setMaxLines(1);
                                dynaText.setLayoutParams(params);


                                params.setMargins(0, 10, 0, 0);
                                dynaText.setGravity(Gravity.LEFT);
                                parent1.addView(dynaText);
                                dynaText.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        LSpecialization_2.setVisibility(View.VISIBLE);
                                        LSpecialization.setVisibility(View.GONE);
                                        tv_spec1.setVisibility(View.VISIBLE);
                                        tv_spec2.setVisibility(View.VISIBLE);
                                        tv_seeAll.setVisibility(View.VISIBLE);
                                        tv_spec1.setText(getBussinessData.getSpecialization().get(0).toString());
                                        tv_spec2.setText(getBussinessData.getSpecialization().get(1).toString());

                                    }
                                });

                                LSpecialization.addView(parent1);
                            }
                        });


                    }
                }


            } else {
                tv_specializtion.setVisibility(View.GONE);
                LSpecialization.setVisibility(View.GONE);
                LSpecialization_2.setVisibility(View.GONE);
            }
        } else {
            tv_specializtion.setVisibility(View.GONE);
            LSpecialization.setVisibility(View.GONE);
            LSpecialization_2.setVisibility(View.GONE);
        }

        if (getBussinessData.getSocialMedia() != null) {
            if (getBussinessData.getSocialMedia().size() > 0) {
                LsocialMedia.setVisibility(View.VISIBLE);

                for (int i = 0; i < getBussinessData.getSocialMedia().size(); i++) {

                    if (getBussinessData.getSocialMedia().get(i).getResource().equalsIgnoreCase("facebook")) {
                        tv_SocialMedia.setVisibility(View.VISIBLE);
                        ic_fac.setVisibility(View.VISIBLE);
                        final int finalI = i;
                        ic_fac.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                try {
                                    Intent myIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(getBussinessData.getSocialMedia().get(finalI).getValue()));
                                    startActivity(myIntent);
                                } catch (ActivityNotFoundException e) {
                                    Toast.makeText(mContext, "No application can handle this request."
                                            + " Please install a webbrowser", Toast.LENGTH_LONG).show();
                                    e.printStackTrace();
                                }
                            }


                        });
                    }


                    if (getBussinessData.getSocialMedia().get(i).getResource().equalsIgnoreCase("googleplus")) {
                        tv_SocialMedia.setVisibility(View.VISIBLE);
                        ic_gplus.setVisibility(View.VISIBLE);
                        final int finalI3 = i;
                        ic_gplus.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                try {
                                    Intent myIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(getBussinessData.getSocialMedia().get(finalI3).getValue()));
                                    startActivity(myIntent);
                                } catch (ActivityNotFoundException e) {
                                    Toast.makeText(mContext, "No application can handle this request."
                                            + " Please install a webbrowser", Toast.LENGTH_LONG).show();
                                    e.printStackTrace();
                                }
                            }


                        });
                    }
                    if (getBussinessData.getSocialMedia().get(i).getResource().equalsIgnoreCase("twitter")) {
                        tv_SocialMedia.setVisibility(View.VISIBLE);
                        ic_twitt.setVisibility(View.VISIBLE);
                        final int finalI1 = i;
                        ic_twitt.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                try {
                                    Intent myIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(getBussinessData.getSocialMedia().get(finalI1).getValue()));
                                    startActivity(myIntent);
                                } catch (ActivityNotFoundException e) {
                                    Toast.makeText(mContext, "No application can handle this request."
                                            + " Please install a webbrowser", Toast.LENGTH_LONG).show();
                                    e.printStackTrace();
                                }
                            }


                        });
                    }

                    if (getBussinessData.getSocialMedia().get(i).getResource().equalsIgnoreCase("linkedin")) {
                        tv_SocialMedia.setVisibility(View.VISIBLE);
                        ic_link.setVisibility(View.VISIBLE);
                        final int finalI5 = i;
                        ic_link.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                try {
                                    Intent myIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(getBussinessData.getSocialMedia().get(finalI5).getValue()));
                                    startActivity(myIntent);
                                } catch (ActivityNotFoundException e) {
                                    Toast.makeText(mContext, "No application can handle this request."
                                            + " Please install a webbrowser", Toast.LENGTH_LONG).show();
                                    e.printStackTrace();
                                }
                            }


                        });
                    }


                    if (getBussinessData.getSocialMedia().get(i).getResource().equalsIgnoreCase("pinterest")) {
                        tv_SocialMedia.setVisibility(View.VISIBLE);
                        ic_pin.setVisibility(View.VISIBLE);
                        final int finalI4 = i;
                        ic_pin.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                try {
                                    Intent myIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(getBussinessData.getSocialMedia().get(finalI4).getValue()));
                                    startActivity(myIntent);
                                } catch (ActivityNotFoundException e) {
                                    Toast.makeText(mContext, "No application can handle this request."
                                            + " Please install a webbrowser", Toast.LENGTH_LONG).show();
                                    e.printStackTrace();
                                }
                            }


                        });
                    }


                    if (getBussinessData.getSocialMedia().get(i).getResource().equalsIgnoreCase("youtube")) {
                        tv_SocialMedia.setVisibility(View.VISIBLE);
                        ic_yout.setVisibility(View.VISIBLE);

                        final int finalI2 = i;
                        ic_yout.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                try {
                                    Intent myIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(getBussinessData.getSocialMedia().get(finalI2).getValue()));
                                    startActivity(myIntent);
                                } catch (ActivityNotFoundException e) {
                                    Toast.makeText(mContext, "No application can handle this request."
                                            + " Please install a webbrowser", Toast.LENGTH_LONG).show();
                                    e.printStackTrace();
                                }
                            }


                        });
                    }

                }


            } else {
                LsocialMedia.setVisibility(View.GONE);
                tv_SocialMedia.setVisibility(View.GONE);
            }
        } else {
            LsocialMedia.setVisibility(View.GONE);
            tv_SocialMedia.setVisibility(View.GONE);
        }
        contactDetail.clear();
        if (getBussinessData.getPhoneNumbers().size() > 0) {
            for (int i = 0; i < getBussinessData.getPhoneNumbers().size(); i++) {
                Config.logV("Phone @@@@@@@@@@@@" + getBussinessData.getPhoneNumbers().get(i).getInstance());
                ContactModel contact = new ContactModel();
                contact.setInstance(getBussinessData.getPhoneNumbers().get(i).getInstance());
                contact.setResource(getBussinessData.getPhoneNumbers().get(i).getResource());
                contact.setLabel(getBussinessData.getPhoneNumbers().get(i).getLabel());
                contactDetail.add(contact);
            }

        }

        if (getBussinessData.getEmails().size() > 0) {
            for (int i = 0; i < getBussinessData.getEmails().size(); i++) {
                ContactModel contact = new ContactModel();
                contact.setInstance(getBussinessData.getEmails().get(i).getInstance());
                contact.setResource(getBussinessData.getEmails().get(i).getResource());
                contact.setLabel(getBussinessData.getEmails().get(i).getLabel());
                contactDetail.add(contact);
            }
        }


        if (getBussinessData.getPhoneNumbers().size() > 0 || getBussinessData.getEmails().size() > 0 && contactDetail.size() > 0) {

            tv_contact.setVisibility(View.VISIBLE);
            tv_contact.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!isContact) {
                        Config.logV("Open");
                        isContact = true;
                        tv_contact.setText("Contact");
                        tv_contact.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.contact_selected, 0, 0);
                       /* mrecycle_contactdetail.setVisibility(View.VISIBLE);
                        tv_contactdetails.setVisibility(View.VISIBLE);
                        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(mContext);
                        mrecycle_contactdetail.setLayoutManager(mLayoutManager);
                        ContactDetailAdapter checkAdapter = new ContactDetailAdapter(contactDetail, mContext, getActivity());
                        mrecycle_contactdetail.setAdapter(checkAdapter);
                        checkAdapter.notifyDataSetChanged();*/
                        BottomSheetContactDialog();
                    } else {
                        Config.logV("CLosed");

                        //   tv_contactdetails.setVisibility(View.GONE);
                       /* mrecycle_contactdetail.setVisibility(View.GONE);*/
                    }
                }
            });


        } else {
            //   tv_contactdetails.setVisibility(View.GONE);
            //  mrecycle_contactdetail.setVisibility(View.GONE);
            tv_contact.setVisibility(View.GONE);
        }

        if (getBussinessData.getVerifyLevel() != null) {
            if (!getBussinessData.getVerifyLevel().equalsIgnoreCase("NONE")) {
                ic_jaldeeverifiedIcon.setVisibility(View.VISIBLE);
                if (getBussinessData.getVerifyLevel().equalsIgnoreCase("BASIC_PLUS")) {
                    ic_jaldeeverifiedIcon.setImageResource(R.drawable.jaldee_basicplus);
                }
                if (getBussinessData.getVerifyLevel().equalsIgnoreCase("BASIC")) {
                    ic_jaldeeverifiedIcon.setImageResource(R.drawable.jaldee_basic);
                }
                if (getBussinessData.getVerifyLevel().equalsIgnoreCase("PREMIUM")) {
                    ic_jaldeeverifiedIcon.setImageResource(R.drawable.jaldee_adv);
                }

            } else {

                ic_jaldeeverifiedIcon.setVisibility(View.GONE);
            }

        } else {
            ic_jaldeeverifiedIcon.setVisibility(View.GONE);
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

                    Config.logV("URL-----1111----------" + response.raw().request().url().toString().trim());
                    Config.logV("Response--code-----detail--------------------" + response.code());

                    if (response.code() == 200) {

                        mBusinessDataList = response.body();
                        Config.logV("Provider------------" + response.body().getId());
                        mProvoderId = response.body().getId();
                        UpdateMainUI(mBusinessDataList);
                        ApiSearchGallery(uniqueID);
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

                    Config.logV("URL------100000---------" + response.raw().request().url().toString().trim());
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

                    Config.logV("URL---3333------------" + response.raw().request().url().toString().trim());
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

    BottomSheetDialog bdialog;

    public void BottomSheetContactDialog() {
        bdialog = new BottomSheetDialog(mContext);
        bdialog.setContentView(R.layout.contact_list);
        bdialog.setCancelable(true);
        bdialog.show();

        RecyclerView contactlist = (RecyclerView) bdialog.findViewById(R.id.contactlist);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(mContext);
        Typeface tyface = Typeface.createFromAsset(mContext.getAssets(),
                "fonts/Montserrat_Bold.otf");
        TextView txtcontact = (TextView) bdialog.findViewById(R.id.txtcontact);
        txtcontact.setTypeface(tyface);
        Button btn_close = (Button) bdialog.findViewById(R.id.btn_close);
        btn_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                isContact = false;
                tv_contact.setText("Contact");
                tv_contact.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_contact, 0, 0);
                bdialog.dismiss();

            }
        });
        bdialog.setOnCancelListener(
                new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        //When you touch outside of dialog bounds,
                        //the dialog gets canceled and this method executes.
                        Config.logV("CANCEL @@@@@@@@@@@");
                        isContact = false;
                        tv_contact.setText("Contact");
                        tv_contact.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_contact, 0, 0);
                        bdialog.dismiss();
                    }
                }
        );

        contactlist.setLayoutManager(mLayoutManager);
        ContactDetailAdapter checkAdapter = new ContactDetailAdapter(contactDetail, mContext, getActivity(), mInterfaceContact);
        contactlist.setAdapter(checkAdapter);
        checkAdapter.notifyDataSetChanged();


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

                    Config.logV("URL-----4444-----Location-----###########@@@@@@-----" + response.raw().request().url().toString().trim());
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
                                dialog.setCancelable(true);
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

                    Config.logV("URL---5555------------" + response.raw().request().url().toString().trim());
                    Config.logV("Response--code----------Service---------------" + response.code());


                    if (response.code() == 200) {


                        SearchService mService = new SearchService();
                        mService.setmAllService(response.body());
                        mService.setLocid(id);
                        mServicesList.add(mService);


                        Config.logV("mServicesList @@@@" + response.body().size());
                        Config.logV("mServicesList" + mServicesList.size());


                        count++;
                        Config.logV("Count " + count);
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

                    Config.logV("URL---66666----SEARCH--------" + response.raw().request().url().toString().trim());
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

                    Config.logV("URL--7777-------------" + response.raw().request().url().toString().trim());
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

                    Config.logV("URL----VIRTUAL---8888--------" + response.raw().request().url().toString().trim());
                    Config.logV("Response--code------VIRTUAL-------------------" + response.code());

                    if (response.code() == 200) {


                        resultData = response.body();
                        if (resultData != null) {

                            domainVirtual.clear();
                            domainVirtual = response.body().getDomain();
                            sub_domainVirtual.clear();
                            sub_domainVirtual = response.body().getSubdomain();


                            domainVirtual.addAll(sub_domainVirtual);

                            if (domainVirtual.size() > 0) {

                                txtMore.setVisibility(View.VISIBLE);
                                mRecycle_virtualfield.setVisibility(View.VISIBLE);
                                RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(mContext);
                                mRecycle_virtualfield.setLayoutManager(mLayoutManager);
                                int size = domainVirtual.size();
                                if (size > 2) {
                                    tv_Moredetails.setVisibility(View.VISIBLE);
                                } else {
                                    tv_Moredetails.setVisibility(View.GONE);
                                }
                                if (size == 1) {
                                    size = 1;
                                } else {
                                    size = 2;
                                }


                                mAdapter = new VirtualFieldAdapter(domainVirtual, mContext, size);
                                mRecycle_virtualfield.setAdapter(mAdapter);
                                mAdapter.notifyDataSetChanged();
                            }


                        } else {
                            tv_Moredetails.setVisibility(View.GONE);
                            mRecycle_virtualfield.setVisibility(View.GONE);
                            txtMore.setVisibility(View.GONE);
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

                    Config.logV("URL-------9999--------" + response.raw().request().url().toString().trim());
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

        Toast.makeText(mContext, "Check-In cancelled successfully", Toast.LENGTH_LONG).show();
        dialog.dismiss();
        refreshList();
    }

    public void refreshList() {
        Config.logV("Refresh $$$$$@@@@@@@@@@@@@@@@@@");
        count = 0;
        isContact = false;

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
        contactDetail = new ArrayList<>();
        ApiSearchViewDetail(uniqueID);
        //ApiSearchGallery(uniqueID);
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

                    tv_fav.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.icon_favourite_line, 0, 0);
                    tv_fav.setText("Add to Fav");
                    Config.logV("URL-----22222----------" + response.raw().request().url().toString().trim());
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
                                tv_fav.setVisibility(View.VISIBLE);
                                tv_fav.setText("Remove from Fav");
                                tv_fav.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.icon_favourited, 0, 0);

                            }
                        }

                        tv_fav.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (favFlag) {

                                    ApiRemoveFavo(mBusinessDataList.getId());
                                    /*AlertDialog myQuittingDialogBox = new AlertDialog.Builder(mContext)
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
                                    myQuittingDialogBox.show();*/
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
                            Toast.makeText(mContext, "Removed from favourites", Toast.LENGTH_LONG).show();
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


    @Override
    public void onMethodContactCallback(String type, String value) {

        if (type.equalsIgnoreCase("Phoneno")) {
            callPhoneNumber(value);
        }

        if (type.equalsIgnoreCase("Email")) {
            Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                    "mailto", value, null));
            // emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Jaldee Feedback");
            emailIntent.putExtra(Intent.EXTRA_TEXT, "");
            try {
                startActivity(Intent.createChooser(emailIntent, "Send mail..."));
            } catch (android.content.ActivityNotFoundException ex) {
                Toast.makeText(mContext, "There are no email clients installed.", Toast.LENGTH_SHORT).show();
            }
        }

    }

    private final int CALL_REQUEST = 100;
    String phoneNumber;

    public void callPhoneNumber(String phNo) {
        try {

            phoneNumber = phNo;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (ContextCompat.checkSelfPermission(mContext, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling

                    //requestPermissions(getActivity(), new String[]{Manifest.permission.CALL_PHONE}, CALL_REQUEST);

                    requestPermissions(new String[]{
                            Manifest.permission.CALL_PHONE}, CALL_REQUEST);

                    return;
                } else {
                    Intent callIntent = new Intent(Intent.ACTION_CALL);
                    callIntent.setData(Uri.parse("tel:" + phNo));
                    startActivity(callIntent);
                }
            } else {

                Intent callIntent = new Intent(Intent.ACTION_CALL);
                callIntent.setData(Uri.parse("tel:" + phNo));
                startActivity(callIntent);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        if (requestCode == CALL_REQUEST) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Config.logV("CALL GRANTED @@@@@@@@@@@@@@");
                Intent callIntent = new Intent(Intent.ACTION_CALL);
                callIntent.setData(Uri.parse("tel:" + phoneNumber));
                startActivity(callIntent);
            } else {
                Toast.makeText(mContext, getResources().getString(R.string.call_permission_denied_message), Toast.LENGTH_SHORT).show();
            }
        }
    }
}
