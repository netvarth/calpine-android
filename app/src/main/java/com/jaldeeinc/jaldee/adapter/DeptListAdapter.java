package com.jaldeeinc.jaldee.adapter;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.net.Uri;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.text.format.DateUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.jaldeeinc.jaldee.Fragment.DeptFragment;
import com.jaldeeinc.jaldee.Fragment.SearchDetailViewFragment;
import com.jaldeeinc.jaldee.R;
import com.jaldeeinc.jaldee.activities.Appointment;
import com.jaldeeinc.jaldee.activities.CheckIn;
import com.jaldeeinc.jaldee.activities.SearchServiceActivity;
import com.jaldeeinc.jaldee.activities.SwipeGalleryImage;
import com.jaldeeinc.jaldee.callback.AdapterCallback;
import com.jaldeeinc.jaldee.common.Config;
import com.jaldeeinc.jaldee.connection.ApiClient;
import com.jaldeeinc.jaldee.connection.ApiInterface;
import com.jaldeeinc.jaldee.custom.CircleTransform;
import com.jaldeeinc.jaldee.custom.PicassoTrustAll;
import com.jaldeeinc.jaldee.custom.ServiceInfoDialog;
import com.jaldeeinc.jaldee.database.DatabaseHandler;
import com.jaldeeinc.jaldee.model.DepartmentUserSearchModel;
import com.jaldeeinc.jaldee.model.Domain_Spinner;
import com.jaldeeinc.jaldee.model.NextAvailableQModel;
import com.jaldeeinc.jaldee.model.SearchListModel;
import com.jaldeeinc.jaldee.model.SearchModel;
import com.jaldeeinc.jaldee.model.WorkingModel;
import com.jaldeeinc.jaldee.response.JdnResponse;
import com.jaldeeinc.jaldee.response.SearchService;
import com.jaldeeinc.jaldee.response.SearchViewDetail;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.jaldeeinc.jaldee.common.MyApplication.getContext;
import static com.jaldeeinc.jaldee.connection.ApiClient.context;

public class DeptListAdapter extends RecyclerView.Adapter {
    List<DepartmentUserSearchModel> searchList;
    SearchModel domainList = new SearchModel();
    private static final int ITEM = 0;
    private static final int LOADING = 1;
    private AdapterCallback mAdapterCallback;
    Activity activity;
    ArrayList<WorkingModel> workingModelArrayList = new ArrayList<>();
    SearchDetailViewFragment searchDetailViewFragment;
    String termilogy;
    String countTerminology;
    Boolean firstCouponAvailable, couponAvailable;
    ArrayList<SearchViewDetail> mSearchGallery;
    JdnResponse jdnList;
    String jdnDiscount, jdnMaxvalue;
    ImageView tv_jdn;
    TextView mImageViewText;

    boolean from_user = false;


    public DeptListAdapter(FragmentActivity activity, List<DepartmentUserSearchModel> msearchList, SearchDetailViewFragment searchDetailViewFragment, Boolean firstCouponAvailable, Boolean couponAvailable, AdapterCallback mAdapterCallback) {
        this.searchList = msearchList;
        this.searchDetailViewFragment = searchDetailViewFragment;
        this.activity = activity;
        this.couponAvailable = couponAvailable;
        this.firstCouponAvailable = firstCouponAvailable;
        this.mAdapterCallback = mAdapterCallback;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        public TextView deptName;
        public TextView tv_name, tv_location, tv_domain, tv_Futuredate, tv_WaitTime, tv_spec1, tv_spec2, tv_spec3, tv_spec22, tv_spec_more, tv_peopleahead, tv_qmessage, tv_enquiry;
        LinearLayout L_services, L_layout_type, L_checkin, L_appointments;
        RelativeLayout L_specialization;
        ImageView ic_jaldeeverifiedIcon;
        ImageView profile;
        RatingBar rating;
        TextView tv_claimable, tv_distance, tv_branch_name, tvSpecializations, tvAppWaitTime;
        Button btncheckin, btnappointment;
        LinearLayout layout_row;
        LinearLayout layout_type;
        TextView txtservice1, txtservice2, txtSeeAll;
        TextView tvAppService1, tvAppService2, tvAppSeeAll;
        TextView tvDntService1, tvDntService2, tvDntSeeAll;


        public MyViewHolder(View view) {
            super(view);

            deptName = (TextView) view.findViewById(R.id.deptName);
            L_checkin = view.findViewById(R.id.checkinlayout);
            L_appointments = view.findViewById(R.id.appointmentLayout);
            ic_jaldeeverifiedIcon = view.findViewById(R.id.ic_jaldeeverifiedIcon);
            tv_name = view.findViewById(R.id.name);
            tv_claimable = view.findViewById(R.id.claimable);
            tv_branch_name = view.findViewById(R.id.branch_name);
            tv_location = view.findViewById(R.id.location);
            tv_domain = view.findViewById(R.id.domain);
            profile = view.findViewById(R.id.profile);
            rating = view.findViewById(R.id.mRatingBar);
            L_services = view.findViewById(R.id.service);
            tv_distance = view.findViewById(R.id.distance);
            btncheckin = view.findViewById(R.id.btncheckin);
            btnappointment = view.findViewById(R.id.btnappointments);
            tv_Futuredate = view.findViewById(R.id.txt_diffdate);
            tv_WaitTime = view.findViewById(R.id.txtWaitTime);
            tvSpecializations = view.findViewById(R.id.txt_specializations);
            L_specialization = view.findViewById(R.id.Lspec);
            L_layout_type = view.findViewById(R.id.layout_type);
            tv_qmessage = view.findViewById(R.id.qmessage);
            tv_peopleahead = view.findViewById(R.id.txt_peopleahead);
            layout_row = view.findViewById(R.id.layout_row);
            tv_spec1 = view.findViewById(R.id.txtspec1);
            tv_spec2 = view.findViewById(R.id.txtspec2);
            tv_spec_more = view.findViewById(R.id.txtspec3);
            tv_spec22 = view.findViewById(R.id.txtspec22);
            mImageViewText = view.findViewById(R.id.mImageViewText);
            layout_type = view.findViewById(R.id.layout_type);
            tv_jdn = view.findViewById(R.id.txtjdn);
            tv_enquiry = view.findViewById(R.id.txt_enquiry);
            tvAppWaitTime = view.findViewById(R.id.txtApptWaitTime);
            txtservice1 = (TextView) view.findViewById(R.id.txtservice1);
            txtservice2 = (TextView) view.findViewById(R.id.txtservice2);
            txtSeeAll = (TextView) view.findViewById(R.id.txtSeeAll);
            tvAppService1 = view.findViewById(R.id.tv_appService1);
            tvAppService2 = view.findViewById(R.id.tv_appservice2);
            tvAppSeeAll = view.findViewById(R.id.tv_appSeeAll);
            tvDntService1 = view.findViewById(R.id.tv_dntService1);
            tvDntService2 = view.findViewById(R.id.tv_dntService2);
            tvDntSeeAll = view.findViewById(R.id.tv_dntSeeAll);

        }
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.department_search_list_view, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        final DepartmentUserSearchModel searchdetailList = searchList.get(position);
//        Log.i("SearchDetailListUser", new Gson().toJson(searchdetailList));
        final DeptListAdapter.MyViewHolder myViewHolder = (DeptListAdapter.MyViewHolder) holder;
        resetPage(myViewHolder);
        handleProfileInfo(myViewHolder, searchdetailList);
        handleCheckinInfo(myViewHolder, searchdetailList);
        handleAppointmentInfo(myViewHolder, searchdetailList);
        handleServices(myViewHolder, searchdetailList);
        LinearLayout parent = new LinearLayout(context);
        LinearLayout.LayoutParams params1 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        parent.setOrientation(LinearLayout.HORIZONTAL);
        parent.setLayoutParams(params1);
        handleBusinesshours(myViewHolder, searchdetailList, parent, params1);
        handleEnquiry(myViewHolder, searchdetailList, parent, params1);
        handleCoupons(myViewHolder, searchdetailList, parent, params1);
        myViewHolder.layout_type.addView(parent);


        if (searchdetailList.getLocation().getParkingType() != null) {
            //     if (searchdetailList.getLocation().getParkingType().equalsIgnoreCase("1")) {
            TextView dynaText = new TextView(context);
            Typeface tyface = Typeface.createFromAsset(context.getAssets(),
                    "fonts/Montserrat_Regular.otf");
            dynaText.setTypeface(tyface);
            dynaText.setText(Config.toTitleCase(searchdetailList.getLocation().getParkingType()) + " Parking ");
            dynaText.setTextSize(11);
            dynaText.setTextColor(context.getResources().getColor(R.color.title_grey));
            dynaText.setPadding(5, 5, 5, 5);
            dynaText.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.icon_parking, 0, 0);
            dynaText.setMaxLines(1);
            params1.setMargins(10, 7, 10, 7);
            dynaText.setGravity(Gravity.LEFT);
            dynaText.setLayoutParams(params1);
            parent.addView(dynaText);
            dynaText.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(activity, searchdetailList.getLocation().getParkingType() + " parking available", Toast.LENGTH_SHORT).show();
                }
            });

        }
        if (searchdetailList.getLocation().isOpen24hours()) {

            TextView dynaText = new TextView(context);
            Typeface tyface = Typeface.createFromAsset(context.getAssets(),
                    "fonts/Montserrat_Regular.otf");
            dynaText.setTypeface(tyface);
            dynaText.setText("24 Hours");
            dynaText.setTextSize(11);
            dynaText.setTextColor(context.getResources().getColor(R.color.title_grey));
            dynaText.setPadding(5, 5, 5, 5);
            dynaText.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.icon_24hours, 0, 0);
            dynaText.setMaxLines(1);
            params1.setMargins(10, 7, 10, 7);
            dynaText.setGravity(Gravity.LEFT);
            dynaText.setLayoutParams(params1);
            parent.addView(dynaText);

            dynaText.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(activity, "Open 24 hours", Toast.LENGTH_SHORT).show();
                }
            });

        }
        if (searchdetailList.getLocation().getLocationVirtualFields().getDentistemergencyservices() != null) {
            //  if (searchdetailList.getDentistemergencyservices_location1().equalsIgnoreCase("1")) {

            TextView dynaText = new TextView(context);
            Typeface tyface = Typeface.createFromAsset(context.getAssets(),
                    "fonts/Montserrat_Regular.otf");
            dynaText.setTypeface(tyface);
            dynaText.setText("Emergency");
            dynaText.setTextSize(11);
            dynaText.setTextColor(context.getResources().getColor(R.color.title_grey));
            dynaText.setPadding(5, 5, 5, 5);
            dynaText.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.icon_emergency, 0, 0);
            dynaText.setMaxLines(1);

            dynaText.setLayoutParams(params1);
            params1.setMargins(10, 7, 10, 7);
            dynaText.setGravity(Gravity.LEFT);
            parent.addView(dynaText);

            dynaText.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(activity, "Emergency services available", Toast.LENGTH_SHORT).show();
                }
            });
            //  }
        }
        if (searchdetailList.getLocation().getLocationVirtualFields().getDocambulance() != null) {
            //  if (searchdetailList.getDocambulance_location1().equalsIgnoreCase("1")) {

            TextView dynaText = new TextView(context);
            Typeface tyface = Typeface.createFromAsset(context.getAssets(),
                    "fonts/Montserrat_Regular.otf");
            dynaText.setTypeface(tyface);
            dynaText.setText("Ambulance");
            dynaText.setTextSize(10);
            dynaText.setTextColor(context.getResources().getColor(R.color.title_grey));
            dynaText.setPadding(5, 5, 5, 5);
            dynaText.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.icon_ambulance, 0, 0);
            //dynaText.setEllipsize(TextUtils.TruncateAt.END);
            dynaText.setMaxLines(1);

            dynaText.setLayoutParams(params1);
            params1.setMargins(10, 7, 10, 7);
            dynaText.setGravity(Gravity.LEFT);
            parent.addView(dynaText);

            dynaText.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(activity, "Ambulance services available", Toast.LENGTH_SHORT).show();
                }
            });
            //  }
        }
        if (searchdetailList.getLocation().getLocationVirtualFields().getFirstaid() != null) {
            //  if (searchdetailList.getFirstaid_location1().equalsIgnoreCase("1")) {
            TextView dynaText = new TextView(context);
            Typeface tyface = Typeface.createFromAsset(context.getAssets(),
                    "fonts/Montserrat_Regular.otf");
            dynaText.setTypeface(tyface);
            dynaText.setText("First Aid");
            dynaText.setTextSize(10);
            dynaText.setTextColor(context.getResources().getColor(R.color.title_grey));
            dynaText.setPadding(5, 5, 5, 5);
            dynaText.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.icon_firstaid, 0, 0);
            //dynaText.setEllipsize(TextUtils.TruncateAt.END);
            dynaText.setMaxLines(1);
            dynaText.setLayoutParams(params1);
            params1.setMargins(10, 7, 10, 7);
            dynaText.setGravity(Gravity.LEFT);
            parent.addView(dynaText);

            dynaText.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(activity, "First aid services available", Toast.LENGTH_SHORT).show();
                }
            });
            //  }
        }
        if (searchdetailList.getLocation().getLocationVirtualFields().getPhysiciansemergencyservices() != null) {
            //   if (searchdetailList.getPhysiciansemergencyservices_location1().equalsIgnoreCase("1")) {
            TextView dynaText = new TextView(context);
            Typeface tyface = Typeface.createFromAsset(context.getAssets(),
                    "fonts/Montserrat_Regular.otf");
            dynaText.setTypeface(tyface);
            dynaText.setText("Emergency");
            dynaText.setTextSize(10);
            dynaText.setTextColor(context.getResources().getColor(R.color.title_grey));
            dynaText.setPadding(5, 5, 5, 5);
            dynaText.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.icon_emergency, 0, 0);
            //dynaText.setEllipsize(TextUtils.TruncateAt.END);
            dynaText.setMaxLines(1);
            dynaText.setLayoutParams(params1);
            params1.setMargins(10, 7, 10, 7);
            dynaText.setGravity(Gravity.LEFT);
            parent.addView(dynaText);

            dynaText.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(activity, "Emergency services available", Toast.LENGTH_SHORT).show();
                }
            });
            //  }
        }

        if (searchdetailList.getLocation().getLocationVirtualFields().getHosemergencyservices() != null) {

            TextView dynaText = new TextView(context);
            Typeface tyface = Typeface.createFromAsset(context.getAssets(),
                    "fonts/Montserrat_Regular.otf");
            dynaText.setTypeface(tyface);
            dynaText.setText("Emergency");
            dynaText.setTextSize(11);
            dynaText.setTextColor(context.getResources().getColor(R.color.title_grey));
            dynaText.setPadding(5, 5, 5, 5);
            dynaText.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.icon_emergency, 0, 0);
            dynaText.setMaxLines(1);
            dynaText.setLayoutParams(params1);
            params1.setMargins(10, 7, 10, 7);
            dynaText.setGravity(Gravity.LEFT);
            parent.addView(dynaText);
            dynaText.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(activity, "Emergency services available", Toast.LENGTH_SHORT).show();
                }
            });

        }
        if (searchdetailList.getLocation().getTraumacentre() != null) {
            //   if (searchdetailList.getTraumacentre_location1().equalsIgnoreCase("1")) {

            TextView dynaText = new TextView(context);
            Typeface tyface = Typeface.createFromAsset(context.getAssets(),
                    "fonts/Montserrat_Regular.otf");
            dynaText.setTypeface(tyface);
            dynaText.setText("Trauma");
            dynaText.setTextSize(11);
            dynaText.setTextColor(context.getResources().getColor(R.color.title_grey));
            dynaText.setPadding(5, 5, 5, 5);
            dynaText.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.icon_trauma, 0, 0);
            //dynaText.setEllipsize(TextUtils.TruncateAt.END);
            dynaText.setMaxLines(1);

            dynaText.setLayoutParams(params1);
            params1.setMargins(10, 7, 10, 7);
            dynaText.setGravity(Gravity.LEFT);
            parent.addView(dynaText);

            dynaText.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(activity, "Trauma care available", Toast.LENGTH_SHORT).show();
                }
            });

            // }
        }
//        myViewHolder.layout_type.addView(parent);
//        //////////////////////////////////////////////////////////////
//        Typeface tyface_confm = Typeface.createFromAsset(context.getAssets(),
//                "fonts/Montserrat_Bold.otf");
//        myViewHolder.tv_name.setTypeface(tyface_confm);
//        myViewHolder.btncheckin.setTypeface(tyface_confm);
//        // myViewHolder.tv_Open.setTypeface(tyface_confm);
//
        myViewHolder.tv_location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchDetailViewFragment.onMethodOpenMap(searchdetailList.getLocation().getGoogleMapUrl());
            }
        });
        myViewHolder.layout_row.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Config.logV("UNIUE ID----------------" + searchdetailList.getParentSearchViewDetail().getUniqueId());

                Config.logV("Popular Text__________@@@Dele");
                String unique_id = searchdetailList.getParentSearchViewDetail().getUniqueId();

                from_user = true;
                searchDetailViewFragment.onMethodCallback(searchdetailList, from_user, unique_id);
            }
        });


        DatabaseHandler db = new DatabaseHandler(context);
        domainList = db.getSubDomainsByFilter(searchdetailList.getSearchViewDetail().getServiceSector().getDomain(), searchdetailList.getSearchViewDetail().getUserSubdomain());
        if (domainList != null) {

            myViewHolder.tv_domain.setText(domainList.getDisplayname());
        }


        if (searchdetailList.getLocation().getPlace() != null) {
            myViewHolder.tv_location.setVisibility(View.VISIBLE);
            // myViewHolder.tv_location.setText(searchdetailList.getPlace1());
//            Config.logV("Place @@@@@@@@@@@@@@" + searchdetailList.getDistance());
//            Double distance = Double.valueOf(searchdetailList.getDistance()) * 1.6;

            myViewHolder.tv_location.setText(searchdetailList.getLocation().getPlace());// + " ( " + String.format("%.2f", distance) + " km )");
        } else {
            myViewHolder.tv_location.setVisibility(View.GONE);
        }


    }

    private void handleCoupons(MyViewHolder myViewHolder, DepartmentUserSearchModel searchdetailList, LinearLayout parent, LinearLayout.LayoutParams params1) {
        TextView firstCoupon = new TextView(context);
        Typeface tyface_3 = Typeface.createFromAsset(context.getAssets(),
                "fonts/Montserrat_Regular.otf");
        firstCoupon.setTypeface(tyface_3);
        firstCoupon.setText("SignUp Coupon");
        firstCoupon.setText(context.getResources().getString(R.string.first_coupon));
        firstCoupon.setTextSize(11);
        firstCoupon.setTextColor(context.getResources().getColor(R.color.title_grey));
        firstCoupon.setPadding(5, 5, 5, 5);
        firstCoupon.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.icc_coupon, 0, 0);
        firstCoupon.setVisibility(View.GONE);
        firstCoupon.setMaxLines(2);
        firstCoupon.setLayoutParams(params1);
        params1.setMargins(10, 7, 10, 7);
        firstCoupon.setGravity(Gravity.CENTER);
        parent.addView(firstCoupon);
        if (firstCouponAvailable) {
            firstCoupon.setVisibility(View.VISIBLE);
        }
        firstCoupon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchDetailViewFragment.onMethodFirstCoupn(searchdetailList.getParentSearchViewDetail().getUniqueId());
            }
        });
        final TextView dynaText2 = new TextView(context);
        Typeface tyface_2 = Typeface.createFromAsset(context.getAssets(),
                "fonts/Montserrat_Regular.otf");
        dynaText2.setTypeface(tyface_2);
        dynaText2.setText("Coupons");
        dynaText2.setTextSize(11);
        dynaText2.setTextColor(context.getResources().getColor(R.color.title_grey));
        dynaText2.setPadding(5, 5, 5, 5);
        dynaText2.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.icc_coupon, 0, 0);
        dynaText2.setVisibility(View.GONE);
        //dynaText.setEllipsize(TextUtils.TruncateAt.END);
        dynaText2.setMaxLines(1);
        dynaText2.setLayoutParams(params1);
        params1.setMargins(10, 7, 10, 7);
        dynaText2.setGravity(Gravity.LEFT);
        parent.addView(dynaText2);
        if (couponAvailable) {
            dynaText2.setVisibility(View.VISIBLE);
        }
        dynaText2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchDetailViewFragment.onMethodCoupn(searchdetailList.getParentSearchViewDetail().getUniqueId());
            }
        });
        apiJDN(searchdetailList.getParentSearchViewDetail().getUniqueId());
        tv_jdn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                searchDetailViewFragment.onMethodJdn(searchdetailList.getParentSearchViewDetail().getUniqueId());

            }
        });
    }

    private void handleEnquiry(MyViewHolder myViewHolder, DepartmentUserSearchModel searchdetailList, LinearLayout parent, LinearLayout.LayoutParams params1) {
        TextView dynaText1 = new TextView(context);
        Typeface tyface_5 = Typeface.createFromAsset(context.getAssets(),
                "fonts/Montserrat_Regular.otf");
        dynaText1.setTypeface(tyface_5);
        dynaText1.setText("Enquiry");
        dynaText1.setTextSize(11);
        dynaText1.setTextColor(context.getResources().getColor(R.color.title_grey));
        dynaText1.setPadding(5, 5, 5, 5);
        dynaText1.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.icon_message_gray, 0, 0);
        dynaText1.setMaxLines(1);
        dynaText1.setLayoutParams(params1);
        params1.setMargins(10, 7, 10, 7);
        dynaText1.setGravity(Gravity.LEFT);
        parent.addView(dynaText1);
        myViewHolder.tv_enquiry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchDetailViewFragment.onMethodMessage(searchdetailList.getSearchViewDetail().getBusinessName(), String.valueOf(searchdetailList.getSearchViewDetail().getId()), "dept");
            }
        });
    }

    private void handleServices(MyViewHolder myViewHolder, DepartmentUserSearchModel searchdetailList) {
        if (searchdetailList.getServices() != null) {
            final ArrayList serviceNames = new ArrayList();
            serviceNames.clear();
            for (int i = 0; i < searchdetailList.getServices().size(); i++) {
                serviceNames.add(i, searchdetailList.getServices().get(i).getName());
            }
            if (searchdetailList.getServices().size() > 0) {
                myViewHolder.L_services.setVisibility(View.VISIBLE);

//                myViewHolder.L_services.removeAllViews();
//                int size = 0;
//                if (searchdetailList.getServices().size() == 1) {
//                    size = 1;
//                } else {
//                    if (searchdetailList.getServices().size() == 2) {
//                        if(searchdetailList.getServices().get(0).getName().length()<=15 && searchdetailList.getServices().get(1).getName().length()<=15 ){
//                        size = 2;
//                        }
//                        else{
//                            size = 1;
//                        }
//
//                    } else {
//                        if(searchdetailList.getServices().get(0).getName().length()<=15 && searchdetailList.getServices().get(1).getName().length()<=15 ){
//                            size = 2;
//                        }
//                        else{
//                            size = 1;
//                        }
//                    }
//                }
//                for (int i = 0; i < size; i++) {
//                    TextView dynaText = new TextView(context);
//                    Typeface tyface = Typeface.createFromAsset(context.getAssets(),
//                            "fonts/Montserrat_Regular.otf");
//                    dynaText.setTypeface(tyface);
//                    dynaText.setText(searchdetailList.getServices().get(i).getName());
//                    try {
//                        if (searchdetailList.getServices().get(i).getServiceType().equalsIgnoreCase("virtualService")) {
//
//                            if (searchdetailList.getServices().get(i).getVirtualCallingModes().get(0).getCallingMode().equalsIgnoreCase("Zoom")) {
//                                dynaText.setCompoundDrawablesWithIntrinsicBounds(R.drawable.zoomicon_sized, 0, 0, 0);
//                                dynaText.setCompoundDrawablePadding(10);
//                            } else if (searchdetailList.getServices().get(i).getVirtualCallingModes().get(0).getCallingMode().equalsIgnoreCase("GoogleMeet")) {
//                                dynaText.setCompoundDrawablesWithIntrinsicBounds(R.drawable.googlemeet_sized, 0, 0, 0);
//                                dynaText.setCompoundDrawablePadding(10);
//                            } else if (searchdetailList.getServices().get(i).getVirtualCallingModes().get(0).getCallingMode().equalsIgnoreCase("WhatsApp")) {
//                                dynaText.setCompoundDrawablesWithIntrinsicBounds(R.drawable.whatsappicon_sized, 0, 0, 0);
//                                dynaText.setCompoundDrawablePadding(10);
//                            } else if (searchdetailList.getServices().get(i).getVirtualCallingModes().get(0).getCallingMode().equalsIgnoreCase("phone")) {
//                                dynaText.setCompoundDrawablesWithIntrinsicBounds(R.drawable.phoneiconsized_small, 0, 0, 0);
//                                dynaText.setCompoundDrawablePadding(10);
//                            }
//
//                        }
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//                    dynaText.setTextSize(11);
//                    dynaText.setPadding(5, 0, 5, 0);
//                    dynaText.setTextColor(context.getResources().getColor(R.color.title_consu));
//                    dynaText.setPaintFlags(dynaText.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
//                    dynaText.setMaxLines(1);
//                    if (size > 2) {
//                        dynaText.setEllipsize(TextUtils.TruncateAt.END);
//                        dynaText.setMaxEms(10);
//                    }
//                    final int finalI = i;
//                    dynaText.setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View v) {
//                            try {
//                                //  ApiService(String.valueOf(searchdetailList.getParentSearchViewDetail().getUniqueId()), searchdetailList.getServices().get(finalI).getName(), searchdetailList.getSearchViewDetail().getBusinessName());
//                                for (int i = 0; i < searchdetailList.getServices().size(); i++) {
//                                    if (searchdetailList.getServices().get(i).getName().toLowerCase().toString().equalsIgnoreCase(searchdetailList.getServices().get(finalI).getName())) {
//                                        Intent iService = new Intent(activity, SearchServiceActivity.class);
//                                        iService.putExtra("name", searchdetailList.getServices().get(i).getName());
//                                        iService.putExtra("duration", searchdetailList.getServices().get(i).getServiceDuration());
//                                        iService.putExtra("price", searchdetailList.getServices().get(i).getTotalAmount());
//                                        iService.putExtra("desc", searchdetailList.getServices().get(i).getDescription());
//                                        iService.putExtra("from","multiuser");
//                                        iService.putExtra("servicegallery", searchdetailList.getServices().get(i).getServicegallery());
//                                        iService.putExtra("taxable", searchdetailList.getServices().get(i).isTaxable());
//                                        iService.putExtra("title", searchdetailList.getSearchViewDetail().getBusinessName());
//                                        iService.putExtra("isPrePayment", searchdetailList.getServices().get(i).isPrePayment());
//                                        iService.putExtra("MinPrePaymentAmount", searchdetailList.getServices().get(i).getMinPrePaymentAmount());
//                                        iService.putExtra("serviceType", searchdetailList.getServices().get(i).getServiceType());
//                                        if (searchdetailList.getServices().get(i).getVirtualCallingModes() != null) {
//                                            iService.putExtra("callingMode", searchdetailList.getServices().get(i).getVirtualCallingModes().get(0).getCallingMode());
//                                        }
//                                        activity.startActivity(iService);
//
//                                    }
//                                }
//                            } catch (Exception e) {
//                                e.printStackTrace();
//                            }
//
//                        }
//                    });
//                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
//                    params.setMargins(0, 0, 20, 0);
//
//                    dynaText.setLayoutParams(params);
//                    myViewHolder.L_services.addView(dynaText);
//                }
//                int count = searchdetailList.getServices().size() - size;
//                if (searchdetailList.getServices().size() > 2 && count > 0) {
//                    TextView dynaTextMore = new TextView(context);
//                    dynaTextMore.setText("+ " + count + " more");
//                    dynaTextMore.setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View v) {
//                            mAdapterCallback.onMethodServiceCallbackUser((ArrayList) searchdetailList.getServices(), searchdetailList.getSearchViewDetail().getBusinessName(), String.valueOf(searchdetailList.getParentSearchViewDetail().getUniqueId()));
//                        }
//                    });
//                    dynaTextMore.setGravity(Gravity.LEFT);
//                    dynaTextMore.setTextColor(context.getResources().getColor(R.color.title_consu));
//                  //  dynaText.setText(" ... ");
//                    myViewHolder.L_services.addView(dynaTextMore);
//                    dynaTextMore.setTextSize(11);
//
//
//                }

                if (searchdetailList.getServices().size() == 1) {

                    myViewHolder.txtservice1.setVisibility(View.VISIBLE);
                    myViewHolder.txtservice2.setVisibility(View.GONE);
                    myViewHolder.txtSeeAll.setVisibility(View.GONE);
                    String name = searchdetailList.getServices().get(0).getName();
                    name = name.substring(0, 1).toUpperCase() + name.substring(1).toLowerCase();
                    myViewHolder.txtservice1.setText(name);
                    try {
                        if (searchdetailList.getServices().get(0).getServiceType().equalsIgnoreCase("virtualservice")) {
                            if (searchdetailList.getServices().get(0).getVirtualCallingModes().get(0).getCallingMode().equalsIgnoreCase("Zoom")) {
                                myViewHolder.txtservice1.setCompoundDrawablesWithIntrinsicBounds(R.drawable.zoomicon_sized, 0, 0, 0);
                                myViewHolder.txtservice1.setCompoundDrawablePadding(10);
                            } else if (searchdetailList.getServices().get(0).getVirtualCallingModes().get(0).getCallingMode().equalsIgnoreCase("GoogleMeet")) {
                                myViewHolder.txtservice1.setCompoundDrawablesWithIntrinsicBounds(R.drawable.googlemeet_sized, 0, 0, 0);
                                myViewHolder.txtservice1.setCompoundDrawablePadding(10);
                            } else if (searchdetailList.getServices().get(0).getVirtualCallingModes().get(0).getCallingMode().equalsIgnoreCase("WhatsApp")) {
                                myViewHolder.txtservice1.setCompoundDrawablesWithIntrinsicBounds(R.drawable.whatsappicon_sized, 0, 0, 0);
                                myViewHolder.txtservice1.setCompoundDrawablePadding(10);
                            } else if (searchdetailList.getServices().get(0).getVirtualCallingModes().get(0).getCallingMode().equalsIgnoreCase("phone")) {
                                myViewHolder.txtservice1.setCompoundDrawablesWithIntrinsicBounds(R.drawable.phoneiconsized_small, 0, 0, 0);
                                myViewHolder.txtservice1.setCompoundDrawablePadding(10);
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    myViewHolder.txtservice1.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            Intent iService = new Intent(activity, SearchServiceActivity.class);
                            iService.putExtra("name", searchdetailList.getServices().get(0).getName());
                            iService.putExtra("duration", searchdetailList.getServices().get(0).getServiceDuration());
                            iService.putExtra("price", searchdetailList.getServices().get(0).getTotalAmount());
                            iService.putExtra("desc", searchdetailList.getServices().get(0).getDescription());
                            iService.putExtra("from", "multiuser");
                            iService.putExtra("servicegallery", searchdetailList.getServices().get(0).getServicegallery());
                            iService.putExtra("taxable", searchdetailList.getServices().get(0).isTaxable());
                            iService.putExtra("title", searchdetailList.getSearchViewDetail().getBusinessName());
                            iService.putExtra("isPrePayment", searchdetailList.getServices().get(0).isPrePayment());
                            iService.putExtra("MinPrePaymentAmount", searchdetailList.getServices().get(0).getMinPrePaymentAmount());
                            iService.putExtra("serviceType", searchdetailList.getServices().get(0).getServiceType());
                            if (searchdetailList.getServices().get(0).getVirtualCallingModes() != null) {
                                iService.putExtra("callingMode", searchdetailList.getServices().get(0).getVirtualCallingModes().get(0).getCallingMode());
                            }
                            activity.startActivity(iService);
                        }
                    });

                } else if (searchdetailList.getServices().size() >= 2 && searchdetailList.getServices().get(0).getName().length() <= 15 && searchdetailList.getServices().get(1).getName().length() <= 15) {

                    if (searchdetailList.getServices().size() == 2) {

                        myViewHolder.txtservice1.setVisibility(View.VISIBLE);
                        myViewHolder.txtservice2.setVisibility(View.VISIBLE);
                        myViewHolder.txtSeeAll.setVisibility(View.GONE);
                        String name1 = searchdetailList.getServices().get(0).getName();
                        name1 = name1.substring(0, 1).toUpperCase() + name1.substring(1).toLowerCase();
                        myViewHolder.txtservice1.setText(name1 + ",");
                        try {
                            if (searchdetailList.getServices().get(0).getServiceType().equalsIgnoreCase("virtualservice")) {
                                if (searchdetailList.getServices().get(0).getVirtualCallingModes().get(0).getCallingMode().equalsIgnoreCase("Zoom")) {
                                    myViewHolder.txtservice1.setCompoundDrawablesWithIntrinsicBounds(R.drawable.zoomicon_sized, 0, 0, 0);
                                    myViewHolder.txtservice1.setCompoundDrawablePadding(10);
                                } else if (searchdetailList.getServices().get(0).getVirtualCallingModes().get(0).getCallingMode().equalsIgnoreCase("GoogleMeet")) {
                                    myViewHolder.txtservice1.setCompoundDrawablesWithIntrinsicBounds(R.drawable.googlemeet_sized, 0, 0, 0);
                                    myViewHolder.txtservice1.setCompoundDrawablePadding(10);
                                } else if (searchdetailList.getServices().get(0).getVirtualCallingModes().get(0).getCallingMode().equalsIgnoreCase("WhatsApp")) {
                                    myViewHolder.txtservice1.setCompoundDrawablesWithIntrinsicBounds(R.drawable.whatsappicon_sized, 0, 0, 0);
                                    myViewHolder.txtservice1.setCompoundDrawablePadding(10);
                                } else if (searchdetailList.getServices().get(0).getVirtualCallingModes().get(0).getCallingMode().equalsIgnoreCase("phone")) {
                                    myViewHolder.txtservice1.setCompoundDrawablesWithIntrinsicBounds(R.drawable.phoneiconsized_small, 0, 0, 0);
                                    myViewHolder.txtservice1.setCompoundDrawablePadding(10);
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        String name2 = searchdetailList.getServices().get(1).getName();
                        name2 = name2.substring(0, 1).toUpperCase() + name2.substring(1).toLowerCase();
                        myViewHolder.txtservice2.setText(name2);
                        try {
                            if (searchdetailList.getServices().get(1).getServiceType().equalsIgnoreCase("virtualservice")) {
                                if (searchdetailList.getServices().get(1).getVirtualCallingModes().get(0).getCallingMode().equalsIgnoreCase("Zoom")) {
                                    myViewHolder.txtservice2.setCompoundDrawablesWithIntrinsicBounds(R.drawable.zoomicon_sized, 0, 0, 0);
                                    myViewHolder.txtservice2.setCompoundDrawablePadding(10);
                                } else if (searchdetailList.getServices().get(1).getVirtualCallingModes().get(0).getCallingMode().equalsIgnoreCase("GoogleMeet")) {
                                    myViewHolder.txtservice2.setCompoundDrawablesWithIntrinsicBounds(R.drawable.googlemeet_sized, 0, 0, 0);
                                    myViewHolder.txtservice2.setCompoundDrawablePadding(10);
                                } else if (searchdetailList.getServices().get(1).getVirtualCallingModes().get(0).getCallingMode().equalsIgnoreCase("WhatsApp")) {
                                    myViewHolder.txtservice2.setCompoundDrawablesWithIntrinsicBounds(R.drawable.whatsappicon_sized, 0, 0, 0);
                                    myViewHolder.txtservice2.setCompoundDrawablePadding(10);
                                } else if (searchdetailList.getServices().get(1).getVirtualCallingModes().get(0).getCallingMode().equalsIgnoreCase("phone")) {
                                    myViewHolder.txtservice2.setCompoundDrawablesWithIntrinsicBounds(R.drawable.phoneiconsized_small, 0, 0, 0);
                                    myViewHolder.txtservice2.setCompoundDrawablePadding(10);
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        myViewHolder.txtservice1.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent iService = new Intent(activity, SearchServiceActivity.class);
                                iService.putExtra("name", searchdetailList.getServices().get(0).getName());
                                iService.putExtra("duration", searchdetailList.getServices().get(0).getServiceDuration());
                                iService.putExtra("price", searchdetailList.getServices().get(0).getTotalAmount());
                                iService.putExtra("desc", searchdetailList.getServices().get(0).getDescription());
                                iService.putExtra("from", "multiuser");
                                iService.putExtra("servicegallery", searchdetailList.getServices().get(0).getServicegallery());
                                iService.putExtra("taxable", searchdetailList.getServices().get(0).isTaxable());
                                iService.putExtra("title", searchdetailList.getSearchViewDetail().getBusinessName());
                                iService.putExtra("isPrePayment", searchdetailList.getServices().get(0).isPrePayment());
                                iService.putExtra("MinPrePaymentAmount", searchdetailList.getServices().get(0).getMinPrePaymentAmount());
                                iService.putExtra("serviceType", searchdetailList.getServices().get(0).getServiceType());
                                if (searchdetailList.getServices().get(0).getVirtualCallingModes() != null) {
                                    iService.putExtra("callingMode", searchdetailList.getServices().get(0).getVirtualCallingModes().get(0).getCallingMode());
                                }
                                activity.startActivity(iService);
                            }
                        });

                        myViewHolder.txtservice2.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent iService = new Intent(activity, SearchServiceActivity.class);
                                iService.putExtra("name", searchdetailList.getServices().get(1).getName());
                                iService.putExtra("duration", searchdetailList.getServices().get(1).getServiceDuration());
                                iService.putExtra("price", searchdetailList.getServices().get(1).getTotalAmount());
                                iService.putExtra("desc", searchdetailList.getServices().get(1).getDescription());
                                iService.putExtra("from", "multiuser");
                                iService.putExtra("servicegallery", searchdetailList.getServices().get(1).getServicegallery());
                                iService.putExtra("taxable", searchdetailList.getServices().get(1).isTaxable());
                                iService.putExtra("title", searchdetailList.getSearchViewDetail().getBusinessName());
                                iService.putExtra("isPrePayment", searchdetailList.getServices().get(1).isPrePayment());
                                iService.putExtra("MinPrePaymentAmount", searchdetailList.getServices().get(1).getMinPrePaymentAmount());
                                iService.putExtra("serviceType", searchdetailList.getServices().get(1).getServiceType());
                                if (searchdetailList.getServices().get(1).getVirtualCallingModes() != null) {
                                    iService.putExtra("callingMode", searchdetailList.getServices().get(1).getVirtualCallingModes().get(0).getCallingMode());
                                }
                                activity.startActivity(iService);
                            }
                        });

                    } else {
                        myViewHolder.txtservice1.setVisibility(View.VISIBLE);
                        myViewHolder.txtservice2.setVisibility(View.VISIBLE);
                        myViewHolder.txtSeeAll.setVisibility(View.VISIBLE);
                        String name1 = searchdetailList.getServices().get(0).getName();
                        name1 = name1.substring(0, 1).toUpperCase() + name1.substring(1).toLowerCase();
                        myViewHolder.txtservice1.setText(name1 + ",");
                        try {
                            if (searchdetailList.getServices().get(0).getServiceType().equalsIgnoreCase("virtualservice")) {
                                if (searchdetailList.getServices().get(0).getVirtualCallingModes().get(0).getCallingMode().equalsIgnoreCase("Zoom")) {
                                    myViewHolder.txtservice1.setCompoundDrawablesWithIntrinsicBounds(R.drawable.zoomicon_sized, 0, 0, 0);
                                    myViewHolder.txtservice1.setCompoundDrawablePadding(10);
                                } else if (searchdetailList.getServices().get(0).getVirtualCallingModes().get(0).getCallingMode().equalsIgnoreCase("GoogleMeet")) {
                                    myViewHolder.txtservice1.setCompoundDrawablesWithIntrinsicBounds(R.drawable.googlemeet_sized, 0, 0, 0);
                                    myViewHolder.txtservice1.setCompoundDrawablePadding(10);
                                } else if (searchdetailList.getServices().get(0).getVirtualCallingModes().get(0).getCallingMode().equalsIgnoreCase("WhatsApp")) {
                                    myViewHolder.txtservice1.setCompoundDrawablesWithIntrinsicBounds(R.drawable.whatsappicon_sized, 0, 0, 0);
                                    myViewHolder.txtservice1.setCompoundDrawablePadding(10);
                                } else if (searchdetailList.getServices().get(0).getVirtualCallingModes().get(0).getCallingMode().equalsIgnoreCase("phone")) {
                                    myViewHolder.txtservice1.setCompoundDrawablesWithIntrinsicBounds(R.drawable.phoneiconsized_small, 0, 0, 0);
                                    myViewHolder.txtservice1.setCompoundDrawablePadding(10);
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        String name2 = searchdetailList.getServices().get(1).getName();
                        name2 = name2.substring(0, 1).toUpperCase() + name2.substring(1).toLowerCase();
                        myViewHolder.txtservice2.setText(name2 + ",");
                        try {
                            if (searchdetailList.getServices().get(1).getServiceType().equalsIgnoreCase("virtualservice")) {
                                if (searchdetailList.getServices().get(1).getVirtualCallingModes().get(0).getCallingMode().equalsIgnoreCase("Zoom")) {
                                    myViewHolder.txtservice2.setCompoundDrawablesWithIntrinsicBounds(R.drawable.zoomicon_sized, 0, 0, 0);
                                    myViewHolder.txtservice2.setCompoundDrawablePadding(10);
                                } else if (searchdetailList.getServices().get(1).getVirtualCallingModes().get(0).getCallingMode().equalsIgnoreCase("GoogleMeet")) {
                                    myViewHolder.txtservice2.setCompoundDrawablesWithIntrinsicBounds(R.drawable.googlemeet_sized, 0, 0, 0);
                                    myViewHolder.txtservice2.setCompoundDrawablePadding(10);
                                } else if (searchdetailList.getServices().get(1).getVirtualCallingModes().get(0).getCallingMode().equalsIgnoreCase("WhatsApp")) {
                                    myViewHolder.txtservice2.setCompoundDrawablesWithIntrinsicBounds(R.drawable.whatsappicon_sized, 0, 0, 0);
                                    myViewHolder.txtservice2.setCompoundDrawablePadding(10);
                                } else if (searchdetailList.getServices().get(1).getVirtualCallingModes().get(0).getCallingMode().equalsIgnoreCase("phone")) {
                                    myViewHolder.txtservice2.setCompoundDrawablesWithIntrinsicBounds(R.drawable.phoneiconsized_small, 0, 0, 0);
                                    myViewHolder.txtservice2.setCompoundDrawablePadding(10);
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        myViewHolder.txtservice1.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent iService = new Intent(activity, SearchServiceActivity.class);
                                iService.putExtra("name", searchdetailList.getServices().get(0).getName());
                                iService.putExtra("duration", searchdetailList.getServices().get(0).getServiceDuration());
                                iService.putExtra("price", searchdetailList.getServices().get(0).getTotalAmount());
                                iService.putExtra("desc", searchdetailList.getServices().get(0).getDescription());
                                iService.putExtra("from", "multiuser");
                                iService.putExtra("servicegallery", searchdetailList.getServices().get(0).getServicegallery());
                                iService.putExtra("taxable", searchdetailList.getServices().get(0).isTaxable());
                                iService.putExtra("title", searchdetailList.getSearchViewDetail().getBusinessName());
                                iService.putExtra("isPrePayment", searchdetailList.getServices().get(0).isPrePayment());
                                iService.putExtra("MinPrePaymentAmount", searchdetailList.getServices().get(0).getMinPrePaymentAmount());
                                iService.putExtra("serviceType", searchdetailList.getServices().get(0).getServiceType());
                                if (searchdetailList.getServices().get(0).getVirtualCallingModes() != null) {
                                    iService.putExtra("callingMode", searchdetailList.getServices().get(0).getVirtualCallingModes().get(0).getCallingMode());
                                }
                                activity.startActivity(iService);
                            }
                        });

                        myViewHolder.txtservice2.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent iService = new Intent(activity, SearchServiceActivity.class);
                                iService.putExtra("name", searchdetailList.getServices().get(1).getName());
                                iService.putExtra("duration", searchdetailList.getServices().get(1).getServiceDuration());
                                iService.putExtra("price", searchdetailList.getServices().get(1).getTotalAmount());
                                iService.putExtra("desc", searchdetailList.getServices().get(1).getDescription());
                                iService.putExtra("from", "multiuser");
                                iService.putExtra("servicegallery", searchdetailList.getServices().get(1).getServicegallery());
                                iService.putExtra("taxable", searchdetailList.getServices().get(1).isTaxable());
                                iService.putExtra("title", searchdetailList.getSearchViewDetail().getBusinessName());
                                iService.putExtra("isPrePayment", searchdetailList.getServices().get(1).isPrePayment());
                                iService.putExtra("MinPrePaymentAmount", searchdetailList.getServices().get(1).getMinPrePaymentAmount());
                                iService.putExtra("serviceType", searchdetailList.getServices().get(1).getServiceType());
                                if (searchdetailList.getServices().get(1).getVirtualCallingModes() != null) {
                                    iService.putExtra("callingMode", searchdetailList.getServices().get(1).getVirtualCallingModes().get(0).getCallingMode());
                                }
                                activity.startActivity(iService);
                            }
                        });
                        myViewHolder.txtSeeAll.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                mAdapterCallback.onMethodServiceCallbackUser((ArrayList) searchdetailList.getServices(), searchdetailList.getSearchViewDetail().getBusinessName(), String.valueOf(searchdetailList.getParentSearchViewDetail().getUniqueId()));
                            }
                        });
                        //  Toast.makeText(mContext, "set text with comma seperated with seemore", Toast.LENGTH_SHORT).show();
                    }
                } else {

                    for (int i = 0; i < searchdetailList.getServices().size(); i++) {

                        if (i == 0) {

                            myViewHolder.txtservice1.setVisibility(View.VISIBLE);
                            myViewHolder.txtservice2.setVisibility(View.GONE);
                            String name1 = searchdetailList.getServices().get(0).getName();
                            name1 = name1.substring(0, 1).toUpperCase() + name1.substring(1).toLowerCase();
                            myViewHolder.txtservice1.setText(name1 + ",");
                            try {
                                if (searchdetailList.getServices().get(0).getServiceType().equalsIgnoreCase("virtualservice")) {
                                    if (searchdetailList.getServices().get(0).getVirtualCallingModes().get(0).getCallingMode().equalsIgnoreCase("Zoom")) {
                                        myViewHolder.txtservice1.setCompoundDrawablesWithIntrinsicBounds(R.drawable.zoomicon_sized, 0, 0, 0);
                                        myViewHolder.txtservice1.setCompoundDrawablePadding(10);
                                    } else if (searchdetailList.getServices().get(0).getVirtualCallingModes().get(0).getCallingMode().equalsIgnoreCase("GoogleMeet")) {
                                        myViewHolder.txtservice1.setCompoundDrawablesWithIntrinsicBounds(R.drawable.googlemeet_sized, 0, 0, 0);
                                    } else if (searchdetailList.getServices().get(0).getVirtualCallingModes().get(0).getCallingMode().equalsIgnoreCase("WhatsApp")) {
                                        myViewHolder.txtservice1.setCompoundDrawablesWithIntrinsicBounds(R.drawable.whatsappicon_sized, 0, 0, 0);
                                        myViewHolder.txtservice1.setCompoundDrawablePadding(10);
                                    } else if (searchdetailList.getServices().get(0).getVirtualCallingModes().get(0).getCallingMode().equalsIgnoreCase("phone")) {
                                        myViewHolder.txtservice1.setCompoundDrawablesWithIntrinsicBounds(R.drawable.phoneiconsized_small, 0, 0, 0);
                                        myViewHolder.txtservice1.setCompoundDrawablePadding(10);

                                    }
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            myViewHolder.txtservice1.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {

                                    Intent iService = new Intent(activity, SearchServiceActivity.class);
                                    iService.putExtra("name", searchdetailList.getServices().get(0).getName());
                                    iService.putExtra("duration", searchdetailList.getServices().get(0).getServiceDuration());
                                    iService.putExtra("price", searchdetailList.getServices().get(0).getTotalAmount());
                                    iService.putExtra("desc", searchdetailList.getServices().get(0).getDescription());
                                    iService.putExtra("from", "multiuser");
                                    iService.putExtra("servicegallery", searchdetailList.getServices().get(0).getServicegallery());
                                    iService.putExtra("taxable", searchdetailList.getServices().get(0).isTaxable());
                                    iService.putExtra("title", searchdetailList.getSearchViewDetail().getBusinessName());
                                    iService.putExtra("isPrePayment", searchdetailList.getServices().get(0).isPrePayment());
                                    iService.putExtra("MinPrePaymentAmount", searchdetailList.getServices().get(0).getMinPrePaymentAmount());
                                    iService.putExtra("serviceType", searchdetailList.getServices().get(0).getServiceType());
                                    if (searchdetailList.getServices().get(0).getVirtualCallingModes() != null) {
                                        iService.putExtra("callingMode", searchdetailList.getServices().get(0).getVirtualCallingModes().get(0).getCallingMode());
                                    }
                                    activity.startActivity(iService);
                                }
                            });
                            myViewHolder.txtSeeAll.setVisibility(View.VISIBLE);
                            myViewHolder.txtSeeAll.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {

                                    mAdapterCallback.onMethodServiceCallbackUser((ArrayList) searchdetailList.getServices(), searchdetailList.getSearchViewDetail().getBusinessName(), String.valueOf(searchdetailList.getParentSearchViewDetail().getUniqueId()));

                                }
                            });

                            break;
                        }
                    }

                }
            } else {
                myViewHolder.L_services.setVisibility(View.GONE);
            }
//            try {
//                // String serviceName = searchdetailList.getServices().toString();
//                try {
//                    JSONArray jsonArray = new JSONArray(searchdetailList.getServices());
//                    String jsonArry = jsonArray.getString(0);
//                    JSONArray jsonArray1 = new JSONArray(jsonArry);
//                    for(int i =0;i<jsonArray1.length();i++){
//                        JSONObject jsonObject = jsonArray1.getJSONObject(i);
//                        String name = jsonObject.optString("name");
//                        serviceNames.add(i,name);
//                        Log.i("sar",serviceNames.toString());
//                    }
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//            }catch (Exception e) {
//                e.printStackTrace();
//            }
        }
    }

    private void resetPage(MyViewHolder myViewHolder) {

    }

    private void handleAppointmentInfo(MyViewHolder myViewHolder, DepartmentUserSearchModel searchdetailList) {

        try {
            Typeface tyface_confm = Typeface.createFromAsset(context.getAssets(),
                    "fonts/Montserrat_Bold.otf");
            myViewHolder.btnappointment.setTypeface(tyface_confm);
            myViewHolder.btnappointment.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent iAppointment = new Intent(v.getContext(), Appointment.class);
                    iAppointment.putExtra("serviceId", searchdetailList.getLocation().getId());
                    iAppointment.putExtra("uniqueID", String.valueOf(searchdetailList.getParentSearchViewDetail().getUniqueId()));
                    iAppointment.putExtra("accountID", String.valueOf(searchdetailList.getParentSearchViewDetail().getId()));
                    iAppointment.putExtra("googlemap", searchdetailList.getLocation().getGoogleMapUrl());
                    iAppointment.putExtra("from", "multiusercheckin");
                    iAppointment.putExtra("title", searchdetailList.getParentSearchViewDetail().getBusinessName());
                    iAppointment.putExtra("place", searchdetailList.getLocation().getPlace());
                    iAppointment.putExtra("sector", searchdetailList.getParentSearchViewDetail().getServiceSector().getDomain());
                    iAppointment.putExtra("subsector", searchdetailList.getParentSearchViewDetail().getServiceSubSector().getSubDomain());
                    iAppointment.putExtra("terminology", termilogy);
                    iAppointment.putExtra("userId", Integer.parseInt(searchdetailList.getScheduleList().getProvider().getId()));
                    iAppointment.putExtra("userName", searchdetailList.getSearchViewDetail().getBusinessName());
                    iAppointment.putExtra("departmentId", String.valueOf(searchdetailList.getDepartmentId()));
                    if (searchdetailList.getScheduleList().getAvailableSchedule() != null) {
                        iAppointment.putExtra("availableDate", searchdetailList.getScheduleList().getAvailableSchedule().getAvailableDate());
                    }
                    iAppointment.putExtra("virtualservices", searchdetailList.getSearchViewDetail().isVirtualServices());
//                iAppointment.putExtra("isshowtoken", searchdetailList.getQueueList().isShowToken());
//                iAppointment.putExtra("getAvail_date", searchdetailList.getScheduleList().getAvailableSchedule().);
                    v.getContext().startActivity(iAppointment);
                }
            });
            if (searchdetailList.getParentSearchViewDetail().isOnlinePresence()) {
                if (searchdetailList.getScheduleList().getAvailableSchedule() != null &&
                        (searchdetailList.getScheduleList().getAvailableSchedule().isTodayAppt() ||
                                searchdetailList.getScheduleList().getAvailableSchedule().isFutureAppt())) {

                    if (searchdetailList.getScheduleList().getSlotsData() != null) {
                        String avlDate = searchdetailList.getScheduleList().getAvailableSchedule().getAvailableDate();
                        Date date = null;
                        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                        try {
                            date = format.parse(avlDate);
                            System.out.println(date);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        String timeSlot = convertSlotTime(searchdetailList.getScheduleList().getSlotsData().getAvailableSlots().get(0).getSlotTime().split("-")[0]);

                        if (date != null) {
                            if (DateUtils.isToday(date.getTime())) {
                                myViewHolder.tvAppWaitTime.setText("Next Available time " + "\n" + "Today,  " + timeSlot);

                            } else {
                                String availableDate = formatDate(searchdetailList.getScheduleList().getAvailableSchedule().getAvailableDate());
                                myViewHolder.tvAppWaitTime.setText("Next Available time " + "\n" + availableDate + ",  " + timeSlot);
                            }
                        }
                    }


                    myViewHolder.L_appointments.setVisibility(View.VISIBLE);
                } else {
                    myViewHolder.L_appointments.setVisibility(View.GONE);
                }
            } else {
                myViewHolder.L_appointments.setVisibility(View.GONE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void disableCheckinFeature(MyViewHolder myViewHolder) {
        myViewHolder.tv_WaitTime.setVisibility(View.GONE);
        disableCheckinButton(myViewHolder);
        myViewHolder.btncheckin.setVisibility(View.GONE);
        myViewHolder.L_checkin.setVisibility(View.GONE);
        myViewHolder.tv_peopleahead.setVisibility(View.GONE);
        myViewHolder.tv_qmessage.setVisibility(View.GONE);
        myViewHolder.tv_Futuredate.setVisibility(View.GONE);
//        myViewHolder.tv_count.setVisibility(View.GONE);
    }

    public void disableCheckinButton(MyViewHolder myViewHolder) {
        myViewHolder.btncheckin.setBackgroundColor(Color.parseColor("#cfcfcf"));
        myViewHolder.btncheckin.setTextColor(context.getResources().getColor(R.color.button_grey));
        myViewHolder.btncheckin.setEnabled(false);
        myViewHolder.btncheckin.setVisibility(View.VISIBLE);
    }

    public void enableCheckinButton(MyViewHolder myViewHolder) {
        myViewHolder.btncheckin.setBackgroundColor(context.getResources().getColor(R.color.dark_blue));
        myViewHolder.btncheckin.setTextColor(context.getResources().getColor(R.color.white));
        myViewHolder.btncheckin.setEnabled(true);
        myViewHolder.btncheckin.setVisibility(View.VISIBLE);
    }

    private void handleCheckinInfo(MyViewHolder myViewHolder, DepartmentUserSearchModel searchdetailList) {
        Typeface tyface_confm = Typeface.createFromAsset(context.getAssets(),
                "fonts/Montserrat_Bold.otf");
        myViewHolder.btncheckin.setTypeface(tyface_confm);
        disableCheckinFeature(myViewHolder);
        myViewHolder.tv_WaitTime.setVisibility(View.VISIBLE);
        if (searchdetailList.getQueueList().getNextAvailableQueue() != null) {
            myViewHolder.L_checkin.setVisibility(View.VISIBLE);
            myViewHolder.L_services.setVisibility(View.VISIBLE);
            myViewHolder.btncheckin.setVisibility(View.VISIBLE);
//            Config.logV("1--" + searchLoclist.getId() + "  2--" + mQueueList.get(i).getNextAvailableQueue().getLocation().getId());
            if (searchdetailList.getLocation().getId() == searchdetailList.getQueueList().getNextAvailableQueue().getLocation().getId()) {
                //open Now
//                if (searchdetailList.getQueueList().getNextAvailableQueue().isOpenNow()) {
//                    myViewHolder.tv_open.setVisibility(View.VISIBLE);
//                } else {
//                    myViewHolder.tv_open.setVisibility(View.GONE);
//                }

                Date c = Calendar.getInstance().getTime();
                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
                String formattedDate = df.format(c);
                System.out.println("Current time => " + formattedDate);

                //Check-In Button
                Date date1 = null, date2 = null;
                try {
                    date1 = df.parse(formattedDate);
                    if (searchdetailList.getQueueList().getNextAvailableQueue().getAvailableDate() != null)
                        date2 = df.parse(searchdetailList.getQueueList().getNextAvailableQueue().getAvailableDate());
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                if (searchdetailList.getParentSearchViewDetail().isOnlinePresence() && searchdetailList.getQueueList().isWaitlistEnabled()) {
                    enableCheckinButton(myViewHolder);
                    if (searchdetailList.getQueueList().getNextAvailableQueue().isShowToken()) {
                        myViewHolder.btncheckin.setText("GET TOKEN");
                    } else {
                        myViewHolder.btncheckin.setText("Check-in".toUpperCase());
                    }
                    if (searchdetailList.getQueueList().getNextAvailableQueue().getAvailableDate() != null) {
                        if (searchdetailList.getQueueList().getNextAvailableQueue().isOnlineCheckIn() && searchdetailList.getQueueList().getNextAvailableQueue().isAvailableToday() && formattedDate.equalsIgnoreCase(searchdetailList.getQueueList().getNextAvailableQueue().getAvailableDate())) { //Today
                            enableCheckinButton(myViewHolder);
                            if (searchdetailList.getQueueList().getNextAvailableQueue().isShowToken()) {
                                myViewHolder.btncheckin.setText("GET TOKEN");

                                if (searchdetailList.getQueueList().getNextAvailableQueue().getCalculationMode().equalsIgnoreCase("NoCalc")) { // NoCalc without show waiting time
                                    String message = Config.getPersonsAheadText(searchdetailList.getQueueList().getNextAvailableQueue().getPersonAhead());
                                    myViewHolder.tv_WaitTime.setText(message);
                                    myViewHolder.tv_WaitTime.setVisibility(View.VISIBLE);
                                    myViewHolder.tv_peopleahead.setVisibility(View.GONE);
                                } else { // Conventional (Token with Waiting time)
                                    myViewHolder.tv_WaitTime.setVisibility(View.VISIBLE);
                                    showWaitingTime(myViewHolder, searchdetailList.getQueueList().getNextAvailableQueue(), null);
//                                    myViewHolder.tv_WaitTime.setText(spannable);
                                    myViewHolder.tv_peopleahead.setVisibility(View.VISIBLE);
                                    String message = Config.getPersonsAheadText(searchdetailList.getQueueList().getNextAvailableQueue().getPersonAhead());
                                    //  myViewHolder.tv_peopleahead.setText(message);
                                    String waitTime = myViewHolder.tv_WaitTime.getText().toString();
                                    String waitTimes = waitTime.replace("\n","-");
                                    myViewHolder.tv_peopleahead.setText(waitTimes);
                                    myViewHolder.tv_WaitTime.setText(message);
                                }
                            } else { // Conventional/Fixed
                                myViewHolder.btncheckin.setText("Check-in".toUpperCase());
                                myViewHolder.tv_WaitTime.setVisibility(View.VISIBLE);
                                showWaitingTime(myViewHolder, searchdetailList.getQueueList().getNextAvailableQueue(), null);
//                                String spannable = getWaitingTime(searchdetailList.getQueueList().getNextAvailableQueue());
//                                myViewHolder.tv_WaitTime.setText(spannable);
                                myViewHolder.tv_peopleahead.setVisibility(View.VISIBLE);
                                String message = Config.getPersonsAheadText(searchdetailList.getQueueList().getNextAvailableQueue().getPersonAhead());
                                myViewHolder.tv_peopleahead.setText(message);
                            }
                        } else {
                            //  disableCheckinButton(myViewHolder);
                        }
                        if (date2 != null && date1.compareTo(date2) < 0) {
                            if (searchdetailList.getQueueList().getNextAvailableQueue().isShowToken()) {
                                myViewHolder.btncheckin.setText("GET TOKEN");
                                if (searchdetailList.getQueueList().getNextAvailableQueue().getCalculationMode().equalsIgnoreCase("NoCalc")) { // NoCalc without show waiting time
                                    String message = Config.getPersonsAheadText(searchdetailList.getQueueList().getNextAvailableQueue().getPersonAhead());
                                    myViewHolder.tv_WaitTime.setText(message);
                                    myViewHolder.tv_WaitTime.setVisibility(View.VISIBLE);
                                    myViewHolder.tv_peopleahead.setVisibility(View.GONE);
                                } else { // Conventional (Token with Waiting time)
                                    myViewHolder.tv_WaitTime.setVisibility(View.VISIBLE);
                                    showWaitingTime(myViewHolder, searchdetailList.getQueueList().getNextAvailableQueue(), null);
//                                    myViewHolder.tv_WaitTime.setText(spannable);
                                    myViewHolder.tv_peopleahead.setVisibility(View.VISIBLE);
                                    String message = Config.getPersonsAheadText(searchdetailList.getQueueList().getNextAvailableQueue().getPersonAhead());
                                    //  myViewHolder.tv_peopleahead.setText(message);
                                    String waitTime = myViewHolder.tv_WaitTime.getText().toString();
                                    String waitTimes = waitTime.replace("\n", " ");
                                    myViewHolder.tv_peopleahead.setText(waitTimes);
                                    myViewHolder.tv_WaitTime.setText(message);
                                }
                            } else {
                                myViewHolder.tv_WaitTime.setVisibility(View.VISIBLE);
                                showWaitingTime(myViewHolder, searchdetailList.getQueueList().getNextAvailableQueue(), "future");
//                            String spannable = getWaitingTime(searchdetailList.getQueueList().getNextAvailableQueue());
//                            myViewHolder.tv_WaitTime.setText(spannable);
                                myViewHolder.tv_peopleahead.setVisibility(View.VISIBLE);
                                String message = Config.getPersonsAheadText(searchdetailList.getQueueList().getNextAvailableQueue().getPersonAhead());
                                myViewHolder.tv_peopleahead.setText(message);
                            }
                        }
                        //Future Checkin
                        if (searchdetailList.getSettings().isFutureDateWaitlist() && searchdetailList.getQueueList().getNextAvailableQueue().getAvailableDate() != null) {
                            //  myViewHolder.tv_Futuredate.setVisibility(View.VISIBLE);
                            if (searchdetailList.getQueueList().getNextAvailableQueue().isShowToken()) {
                                myViewHolder.tv_Futuredate.setText("Do you want to Get Token for another day?");
                            } else {
                                myViewHolder.tv_Futuredate.setText("Do you want to" + " Check-in for another day?");
                            }
                        } else {
                            myViewHolder.tv_Futuredate.setVisibility(View.GONE);
                        }
                    }
                } else {
                    disableCheckinFeature(myViewHolder);
                }
            }
        }
        myViewHolder.btncheckin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent iCheckIn = new Intent(v.getContext(), CheckIn.class);
                iCheckIn.putExtra("serviceId", searchdetailList.getLocation().getId());
                iCheckIn.putExtra("uniqueID", String.valueOf(searchdetailList.getParentSearchViewDetail().getUniqueId()));
                iCheckIn.putExtra("accountID", String.valueOf(searchdetailList.getSearchViewDetail().getId()));
                iCheckIn.putExtra("googlemap", searchdetailList.getLocation().getGoogleMapUrl());
                // iCheckIn.putExtra("waititme", myViewHolder.tv_WaitTime.getText().toString());
                iCheckIn.putExtra("from", "multiusercheckin");
                iCheckIn.putExtra("title", searchdetailList.getSearchViewDetail().getBusinessName());
                iCheckIn.putExtra("place", searchdetailList.getLocation().getPlace());
                Config.logV("sector%%%%%%-------------" + searchdetailList.getSearchViewDetail().getServiceSector());
//                iCheckIn.putExtra("sector",  searchdetailList.getSearchViewDetail().getServiceSector());
                iCheckIn.putExtra("subsector", searchdetailList.getSearchViewDetail().getServiceSubSector().toString());
                iCheckIn.putExtra("terminology", termilogy);
                iCheckIn.putExtra("isshowtoken", searchdetailList.getQueueList().getNextAvailableQueue().isShowToken());
                iCheckIn.putExtra("getAvail_date", searchdetailList.getQueueList().getNextAvailableQueue().getAvailableDate());
                iCheckIn.putExtra("userId", Integer.parseInt(searchdetailList.getQueueList().getProvider().getId()));
                iCheckIn.putExtra("userName", searchdetailList.getSearchViewDetail().getBusinessName());
                iCheckIn.putExtra("departmentId", String.valueOf(searchdetailList.getDepartmentId()));
                iCheckIn.putExtra("virtualservices", searchdetailList.getSearchViewDetail().isVirtualServices());
                if (searchdetailList.getQueueList().getNextAvailableQueue() != null) {
                    iCheckIn.putExtra("getAvail_date", searchdetailList.getQueueList().getNextAvailableQueue().getAvailableDate());
                }
                v.getContext().startActivity(iCheckIn);
            }
        });

        myViewHolder.tv_WaitTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Do nothing..
            }
        });

        myViewHolder.tv_Futuredate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent iCheckIn = new Intent(v.getContext(), CheckIn.class);
                if (searchdetailList.getLocation() != null) {
                    iCheckIn.putExtra("serviceId", searchdetailList.getLocation().getId());
                }
                iCheckIn.putExtra("uniqueID", String.valueOf(searchdetailList.getParentSearchViewDetail().getUniqueId()));
                iCheckIn.putExtra("accountID", String.valueOf(searchdetailList.getSearchViewDetail().getId()));
                iCheckIn.putExtra("googlemap", searchdetailList.getLocation().getGoogleMapUrl());
                // iCheckIn.putExtra("waititme", myViewHolder.tv_WaitTime.getText().toString());
                iCheckIn.putExtra("from", "multiusercheckin");
                iCheckIn.putExtra("title", searchdetailList.getSearchViewDetail().getBusinessName());
                iCheckIn.putExtra("place", searchdetailList.getLocation().getPlace());
                Config.logV("sector%%%%%%-------------" + searchdetailList.getSearchViewDetail().getServiceSector());
                iCheckIn.putExtra("sector", searchdetailList.getSearchViewDetail().getServiceSector().toString());
                iCheckIn.putExtra("subsector", searchdetailList.getSearchViewDetail().getServiceSubSector().toString());
                iCheckIn.putExtra("terminology", termilogy);
                iCheckIn.putExtra("isshowtoken", searchdetailList.getQueueList().getNextAvailableQueue().isShowToken());
                iCheckIn.putExtra("getAvail_date", searchdetailList.getQueueList().getNextAvailableQueue().getAvailableDate());
                iCheckIn.putExtra("userId", Integer.parseInt(searchdetailList.getQueueList().getProvider().getId()));
                iCheckIn.putExtra("userName", searchdetailList.getSearchViewDetail().getBusinessName());
                iCheckIn.putExtra("departmentId", String.valueOf(searchdetailList.getDepartmentId()));
                iCheckIn.putExtra("virtualServices", String.valueOf(searchdetailList.getSearchViewDetail().isVirtualServices()));
                if (searchdetailList.getQueueList().getNextAvailableQueue() != null) {
                    iCheckIn.putExtra("getAvail_date", searchdetailList.getQueueList().getNextAvailableQueue().getAvailableDate());
                }
                v.getContext().startActivity(iCheckIn);
            }
        });
    }

    public void showWaitingTime(MyViewHolder myViewHolder, NextAvailableQModel searchdetailList, String type) {
        if (searchdetailList.getServiceTime() != null) {
            String firstWord = "Next Available Time ";
            String secondWord = "";
            if (type != null) {
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                Date date = null;
                try {
                    date = format.parse(searchdetailList.getAvailableDate());
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                String day = (String) DateFormat.format("dd", date);
                String monthString = (String) DateFormat.format("MMM", date);
                Typeface tyface1 = Typeface.createFromAsset(context.getAssets(),
                        "fonts/Montserrat_Bold.otf");
                secondWord = "\n" + monthString + " " + day + ", " + searchdetailList.getServiceTime();
            } else {
                secondWord = "\nToday, " + searchdetailList.getServiceTime();
            }
            Spannable spannable = new SpannableString(firstWord + secondWord);
            // spannable.setSpan(new CustomTypefaceSpan("sans-serif", tyface1), 0, firstWord.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            myViewHolder.tv_WaitTime.setText(spannable);
        } else {
            //   myViewHolder.tv_WaitTime.setVisibility(View.GONE);
            String firstWord = "Est wait time";
            String secondWord = " \n " + Config.getTimeinHourMinutes(searchdetailList.getQueueWaitingTime());
            myViewHolder.tv_WaitTime.setText(firstWord + secondWord);
        }
        myViewHolder.tv_peopleahead.setVisibility(View.VISIBLE);
        String message = Config.getPersonsAheadText(searchdetailList.getPersonAhead());
        myViewHolder.tv_peopleahead.setText(message);
    }

    public void setSpecializations(MyViewHolder myViewHolder, SearchViewDetail searchdetailList) {
        if (searchdetailList.getSpecialization() != null) {
            final List<String> list_spec = searchdetailList.getSpecialization();
            if (list_spec.size() > 0) {

                myViewHolder.L_specialization.setVisibility(View.VISIBLE);
                myViewHolder.tvSpecializations.setVisibility(View.VISIBLE);
                myViewHolder.tvSpecializations.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // mAdapterCallback.onMethodCallback(searchdetailList.getUniqueid());
                        mAdapterCallback.onMethodSpecialization(searchdetailList.getSpecialization(), searchdetailList.getBusinessName());
                    }
                });

            }
        } else {
            myViewHolder.tv_spec1.setVisibility(View.GONE);
            myViewHolder.tv_spec2.setVisibility(View.GONE);
            myViewHolder.tv_spec_more.setVisibility(View.GONE);
            myViewHolder.tv_spec22.setVisibility(View.GONE);
            myViewHolder.tvSpecializations.setVisibility(View.GONE);
            myViewHolder.L_specialization.setVisibility(View.GONE);
        }
    }

    private void handleJaldeeVerification(MyViewHolder myViewHolder, SearchViewDetail searchdetailList) {
        if (searchdetailList.getVerifyLevel() != null) {
            if (!searchdetailList.getVerifyLevel().equalsIgnoreCase("NONE")) {
                myViewHolder.ic_jaldeeverifiedIcon.setVisibility(View.INVISIBLE);
                if (searchdetailList.getVerifyLevel().equalsIgnoreCase("BASIC_PLUS")) {
                    myViewHolder.ic_jaldeeverifiedIcon.setVisibility(View.VISIBLE);
                    myViewHolder.ic_jaldeeverifiedIcon.setImageResource(R.drawable.jaldee_basicplus);
                }
                if (searchdetailList.getVerifyLevel().equalsIgnoreCase("BASIC")) {
                    myViewHolder.ic_jaldeeverifiedIcon.setVisibility(View.VISIBLE);
                    myViewHolder.ic_jaldeeverifiedIcon.setImageResource(R.drawable.jaldee_basic);
                }
                if (searchdetailList.getVerifyLevel().equalsIgnoreCase("PREMIUM") || searchdetailList.getVerifyLevel().equalsIgnoreCase("ADVANCED")) {
                    myViewHolder.ic_jaldeeverifiedIcon.setVisibility(View.VISIBLE);
                    myViewHolder.ic_jaldeeverifiedIcon.setImageResource(R.drawable.jaldee_adv);
                }
            } else {
                myViewHolder.ic_jaldeeverifiedIcon.setVisibility(View.INVISIBLE);
            }

            myViewHolder.ic_jaldeeverifiedIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mAdapterCallback.onMethodJaldeeLogo(searchdetailList.getVerifyLevel(), searchdetailList.getBusinessName());
                }
            });
        } else {
            myViewHolder.ic_jaldeeverifiedIcon.setVisibility(View.INVISIBLE);
        }
    }

    public void handleBusinesshours(MyViewHolder myViewHolder, DepartmentUserSearchModel searchdetailList, LinearLayout parent, LinearLayout.LayoutParams params1) {
        if (searchdetailList.getLocation().getbSchedule().getTimespec().get(0).getTimeSlots() != null) {
            TextView dynaText = new TextView(context);
            Typeface tyface = Typeface.createFromAsset(context.getAssets(),
                    "fonts/Montserrat_Regular.otf");
            dynaText.setTypeface(tyface);
            dynaText.setText(context.getResources().getString(R.string.working_hours));

            dynaText.setTextSize(11);
            dynaText.setTextColor(context.getResources().getColor(R.color.title_grey));
            dynaText.setPadding(5, 5, 5, 5);
            dynaText.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_clock, 0, 0);
            //dynaText.setEllipsize(TextUtils.TruncateAt.END);
            dynaText.setMaxLines(2);


            dynaText.setLayoutParams(params1);
            params1.setMargins(10, 7, 10, 7);
            dynaText.setGravity(Gravity.CENTER);
            parent.addView(dynaText);
            dynaText.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (searchdetailList.getLocation().getbSchedule().getTimespec().get(0).getTimeSlots() != null) {

                        if (searchdetailList.getLocation().getbSchedule().getTimespec().get(0).getTimeSlots() != null) {
                            try {
//                                String array_json = searchdetailList.getLocation().getbSchedule().getTimespec().get(0).toString();


                                //Get the instance of JSONArray that contains JSONObjects
//                                    JSONArray jsonArray = new JSONArray(array_json);
//                                    String jsonarry = jsonArray.getString(0);
//                                    JSONArray jsonArray1 = new JSONArray(jsonarry);

                                //Iterate the jsonArray and print the info of JSONObjects

                                workingModelArrayList.clear();

                                //  for (int i = 0; i < searchdetailList.getLocation().getbSchedule().getTimespec().get(0).getRepeatIntervals().size(); i++) {
                                //  JSONObject jsonObject = jsonArray1.getJSONObject(i);

//                                        String id = jsonObject.optString("recurringType").toString();
//                                        String repeatinterval = jsonObject.optString("repeatIntervals").toString();
//                                        String timeslot = jsonObject.optString("timeSlots").toString();
//                                        // String publish_date = jsonObject.optString("publish_date").toString();
//                                        JSONArray jsonArray_time = new JSONArray(timeslot);
//                                        JSONObject jsonObject_time = jsonArray_time.getJSONObject(0);
                                String sTime = searchdetailList.getLocation().getbSchedule().getTimespec().get(0).getTimeSlots().get(0).getsTime();
                                String eTime = searchdetailList.getLocation().getbSchedule().getTimespec().get(0).getTimeSlots().get(0).geteTime();


                                //  JSONArray jsonArray_repeat = new JSONArray(repeatinterval);

                                for (int k = 0; k < searchdetailList.getLocation().getbSchedule().getTimespec().get(0).getRepeatIntervals().size(); k++) {
                                    String repeat = searchdetailList.getLocation().getbSchedule().getTimespec().get(0).getRepeatIntervals().get(k).toString();

                                    WorkingModel work = new WorkingModel();
                                    if (repeat.equalsIgnoreCase("2")) {

                                        work.setDay("Monday");
                                        work.setTime_value(sTime + "-" + eTime);

                                    }
                                    if (repeat.equalsIgnoreCase("3")) {

                                        work.setDay("Tuesday");
                                        work.setTime_value(sTime + "-" + eTime);

                                    }
                                    if (repeat.equalsIgnoreCase("4")) {
                                        work.setDay("Wednesday");
                                        work.setTime_value(sTime + "-" + eTime);
                                    }
                                    if (repeat.equalsIgnoreCase("5")) {

                                        work.setDay("Thursday");
                                        work.setTime_value(sTime + "-" + eTime);
                                    }
                                    if (repeat.equalsIgnoreCase("6")) {

                                        work.setDay("Friday");
                                        work.setTime_value(sTime + "-" + eTime);

                                    }
                                    if (repeat.equalsIgnoreCase("7")) {

                                        work.setDay("Saturday");
                                        work.setTime_value(sTime + "-" + eTime);

                                    }
                                    if (repeat.equalsIgnoreCase("1")) {

                                        work.setDay("Sunday");
                                        work.setTime_value(sTime + "-" + eTime);

                                    }

                                    workingModelArrayList.add(work);
                                }
                                // }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                        searchDetailViewFragment.onMethodWorkingCallback(workingModelArrayList, searchdetailList.getSearchViewDetail().getBusinessName());
                        //   Config.logV("Working-----------" + workingModelArrayList.size() + "tt" + searchdetailList.getTitle());
                    }
                }
            });
        }
    }

    private void handleProfileInfo(MyViewHolder myViewHolder, DepartmentUserSearchModel searchdetailList) {
        SearchViewDetail getBusinessData = searchdetailList.getSearchViewDetail();
        handleJaldeeVerification(myViewHolder, getBusinessData);
        setSpecializations(myViewHolder, getBusinessData);
//        if (getBusinessData.getSocialMedia() != null) {
//            if (getBusinessData.getSocialMedia().size() > 0) {
//                LsocialMedia.setVisibility(View.VISIBLE);
//                for (int i = 0; i < getBusinessData.getSocialMedia().size(); i++) {
//                    if (getBusinessData.getSocialMedia().get(i).getResource().equalsIgnoreCase("facebook")) {
////                        tv_SocialMedia.setVisibility(View.VISIBLE);
//                        ic_fac.setVisibility(View.VISIBLE);
//                        final int finalI = i;
//                        ic_fac.setOnClickListener(new View.OnClickListener() {
//                            @Override
//                            public void onClick(View v) {
//                                try {
//                                    Intent myIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(getBusinessData.getSocialMedia().get(finalI).getValue()));
//                                    startActivity(myIntent);
//                                } catch (ActivityNotFoundException e) {
//                                    Toast.makeText(mContext, "No application can handle this request."
//                                            + " Please install a webbrowser", Toast.LENGTH_LONG).show();
//                                    e.printStackTrace();
//                                }
//                            }
//                        });
//                    }
//                    if (getBusinessData.getSocialMedia().get(i).getResource().equalsIgnoreCase("googleplus")) {
////                        tv_SocialMedia.setVisibility(View.VISIBLE);
//                        ic_gplus.setVisibility(View.VISIBLE);
//                        final int finalI3 = i;
//                        ic_gplus.setOnClickListener(new View.OnClickListener() {
//                            @Override
//                            public void onClick(View v) {
//                                try {
//                                    Intent myIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(getBusinessData.getSocialMedia().get(finalI3).getValue()));
//                                    startActivity(myIntent);
//                                } catch (ActivityNotFoundException e) {
//                                    Toast.makeText(mContext, "No application can handle this request."
//                                            + " Please install a webbrowser", Toast.LENGTH_LONG).show();
//                                    e.printStackTrace();
//                                }
//                            }
//                        });
//                    }
//                    if (getBusinessData.getSocialMedia().get(i).getResource().equalsIgnoreCase("twitter")) {
////                        tv_SocialMedia.setVisibility(View.VISIBLE);
//                        ic_twitt.setVisibility(View.VISIBLE);
//                        final int finalI1 = i;
//                        ic_twitt.setOnClickListener(new View.OnClickListener() {
//                            @Override
//                            public void onClick(View v) {
//                                try {
//                                    Intent myIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(getBusinessData.getSocialMedia().get(finalI1).getValue()));
//                                    startActivity(myIntent);
//                                } catch (ActivityNotFoundException e) {
//                                    Toast.makeText(mContext, "No application can handle this request."
//                                            + " Please install a webbrowser", Toast.LENGTH_LONG).show();
//                                    e.printStackTrace();
//                                }
//                            }
//                        });
//                    }
//                    if (getBusinessData.getSocialMedia().get(i).getResource().equalsIgnoreCase("linkedin")) {
////                        tv_SocialMedia.setVisibility(View.VISIBLE);
//                        ic_link.setVisibility(View.VISIBLE);
//                        final int finalI5 = i;
//                        ic_link.setOnClickListener(new View.OnClickListener() {
//                            @Override
//                            public void onClick(View v) {
//                                try {
//                                    Intent myIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(getBusinessData.getSocialMedia().get(finalI5).getValue()));
//                                    startActivity(myIntent);
//                                } catch (ActivityNotFoundException e) {
//                                    Toast.makeText(mContext, "No application can handle this request."
//                                            + " Please install a webbrowser", Toast.LENGTH_LONG).show();
//                                    e.printStackTrace();
//                                }
//                            }
//                        });
//                    }
//                    if (getBusinessData.getSocialMedia().get(i).getResource().equalsIgnoreCase("pinterest")) {
////                        tv_SocialMedia.setVisibility(View.VISIBLE);
//                        ic_pin.setVisibility(View.VISIBLE);
//                        final int finalI4 = i;
//                        ic_pin.setOnClickListener(new View.OnClickListener() {
//                            @Override
//                            public void onClick(View v) {
//                                try {
//                                    Intent myIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(getBusinessData.getSocialMedia().get(finalI4).getValue()));
//                                    startActivity(myIntent);
//                                } catch (ActivityNotFoundException e) {
//                                    Toast.makeText(mContext, "No application can handle this request."
//                                            + " Please install a webbrowser", Toast.LENGTH_LONG).show();
//                                    e.printStackTrace();
//                                }
//                            }
//                        });
//                    }
//                    if (getBusinessData.getSocialMedia().get(i).getResource().equalsIgnoreCase("youtube")) {
////                        tv_SocialMedia.setVisibility(View.VISIBLE);
//                        ic_yout.setVisibility(View.VISIBLE);
//                        final int finalI2 = i;
//                        ic_yout.setOnClickListener(new View.OnClickListener() {
//                            @Override
//                            public void onClick(View v) {
//                                try {
//                                    Intent myIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(getBusinessData.getSocialMedia().get(finalI2).getValue()));
//                                    startActivity(myIntent);
//                                } catch (ActivityNotFoundException e) {
//                                    Toast.makeText(mContext, "No application can handle this request."
//                                            + " Please install a webbrowser", Toast.LENGTH_LONG).show();
//                                    e.printStackTrace();
//                                }
//                            }
//                        });
//                    }
//                }
//            } else {
//                LsocialMedia.setVisibility(View.GONE);
//                tv_SocialMedia.setVisibility(View.GONE);
//            }
//        } else {
//            LsocialMedia.setVisibility(View.GONE);
//            tv_SocialMedia.setVisibility(View.GONE);
//        }
//        contactDetail.clear();
//        if (getBusinessData.getPhoneNumbers().size() > 0) {
//            for (int i = 0; i < getBusinessData.getPhoneNumbers().size(); i++) {
//                Config.logV("Phone @@@@@@@@@@@@" + getBusinessData.getPhoneNumbers().get(i).getInstance());
//                ContactModel contact = new ContactModel();
//                contact.setInstance(getBusinessData.getPhoneNumbers().get(i).getInstance());
//                contact.setResource(getBusinessData.getPhoneNumbers().get(i).getResource());
//                contact.setLabel(getBusinessData.getPhoneNumbers().get(i).getLabel());
//                contactDetail.add(contact);
//            }
//        }
//        if (getBusinessData.getEmails().size() > 0) {
//            for (int i = 0; i < getBusinessData.getEmails().size(); i++) {
//                ContactModel contact = new ContactModel();
//                contact.setInstance(getBusinessData.getEmails().get(i).getInstance());
//                contact.setResource(getBusinessData.getEmails().get(i).getResource());
//                contact.setLabel(getBusinessData.getEmails().get(i).getLabel());
//                contactDetail.add(contact);
//            }
//        }
//
//        if (getBusinessData.getPhoneNumbers().size() > 0 || getBusinessData.getEmails().size() > 0 && contactDetail.size() > 0) {
//            tv_contact.setVisibility(View.VISIBLE);
//            tv_contact.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    if (!isContact) {
//                        Config.logV("Open");
//                        isContact = true;
//                        tv_contact.setText("Contact");
//                        tv_contact.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.contact_selected, 0, 0);
//                        BottomSheetContactDialog();
//                    } else {
//                        Config.logV("CLosed");
//                    }
//                }
//            });
//        } else {
//            tv_contact.setVisibility(View.GONE);
//        }
//        tv_msg.setEnabled(true);
        Typeface tyface_confm = Typeface.createFromAsset(context.getAssets(),
                "fonts/Montserrat_Bold.otf");
        myViewHolder.tv_name.setTypeface(tyface_confm);
        myViewHolder.tv_name.setText(getBusinessData.getBusinessName());

//        myViewHolder.rating.setRating(getBusinessData.getAvgRating());

        try {

            int rate = Math.round(getBusinessData.getAvgRating());
            if (rate < 4) {
                myViewHolder.rating.setVisibility(View.GONE);
            } else {
                myViewHolder.rating.setVisibility(View.VISIBLE);
                myViewHolder.rating.setRating(rate);
            }

        } catch (Exception e) {

        }
//        if (getBusinessData.getServiceSector().getDisplayName() != null && getBusinessData.getServiceSubSector().getDisplayName() != null) {
//            myViewHolder.tv_domain.setText(getBusinessData.getServiceSector().getDisplayName() + " " + "(" + getBusinessData.getServiceSubSector().getDisplayName() + ")");
//        }
//        if (getBusinessData.getBusinessDesc() != null) {
//            myViewHolder.m.setVisibility(View.VISIBLE);
//            tv_desc.setText(getBusinessData.getBusinessDesc());
//            tv_desc.post(new Runnable() {
//                @Override
//                public void run() {
//                    int lineCount = tv_desc.getLineCount();
//                    //Config.logV("No of line---------------" + lineCount + "Name" + inboxList.getUserName());
//                    if (lineCount > 3) {
//                        ResizableCustomView.doResizeTextView(mContext, tv_desc, 3, "..more", true);
//                    } else {
//                    }
//                    // Use lineCount here
//                }
//            });
//        } else {
//            tv_desc.setVisibility(View.GONE);
//        }
        Picasso.Builder builder = new Picasso.Builder(context);
        builder.listener(new Picasso.Listener() {
            @Override
            public void onImageLoadFailed(Picasso picasso, Uri uri, Exception exception) {
                exception.printStackTrace();
            }
        });
        if (searchdetailList.getSearchViewDetail().getLogo() != null) {
            PicassoTrustAll.getInstance(context).load(searchdetailList.getSearchViewDetail().getLogo().getUrl()).placeholder(R.drawable.icon_noimage).error(R.drawable.icon_noimage).transform(new CircleTransform()).fit().into(myViewHolder.profile);
            myViewHolder.profile.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // Config.logV("Unique Id List", searchdetailList.getUniqueid());
                    // ApiSearchGallery(searchdetailList.getParentSearchViewDetail().getUniqueId(), searchdetailList);
                    ArrayList<String> mGalleryList = new ArrayList<>();
                    if (searchdetailList.getSearchViewDetail().getLogo() != null) {
                        mGalleryList.add(searchdetailList.getSearchViewDetail().getLogo().getUrl());
                    }
                    boolean mValue = SwipeGalleryImage.SetGalleryList(mGalleryList, context);
                    if (mValue) {
                        Intent intent = new Intent(context, SwipeGalleryImage.class);
                        intent.putExtra("pos", 0);
                        context.startActivity(intent);
                    }
                }


            });
        }

    }


    @Override
    public int getItemCount() {
        if (searchList != null) {
            return searchList.size();
        }
        return 0;

    }


    private void apiJDN(String uniqueID) {
        ApiInterface apiService =
                ApiClient.getClientS3Cloud(activity).create(ApiInterface.class);
        final Dialog mDialog = Config.getProgressDialog(activity, activity.getResources().getString(R.string.dialog_log_in));
        mDialog.show();
        Date currentTime = new Date();
        final SimpleDateFormat sdf = new SimpleDateFormat(
                "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US);
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        System.out.println("UTC time: " + sdf.format(currentTime));
        Call<JdnResponse> call = apiService.getJdnList(Integer.parseInt(uniqueID), sdf.format(currentTime));
        call.enqueue(new Callback<JdnResponse>() {
            @Override
            public void onResponse(Call<JdnResponse> call, Response<JdnResponse> response) {
                try {
                    if (mDialog.isShowing())
                        Config.closeDialog(activity, mDialog);
                    Config.logV("URL-----1111----------" + response.raw().request().url().toString().trim());
                    Config.logV("Response--code-----detail--------------------" + response.code());
                    if (response.code() == 200) {
                        jdnList = response.body();
                        jdnDiscount = jdnList.getDiscPercentage();
                        jdnMaxvalue = jdnList.getDiscMax();
                        if (new Gson().toJson(jdnList).equals("{}")) {
                            tv_jdn.setVisibility(View.GONE);
                        } else {
                            tv_jdn.setVisibility(View.VISIBLE);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<JdnResponse> call, Throwable t) {
                // Log error here since request failed
                Config.logV("Fail---------------" + t.toString());
                if (mDialog.isShowing())
                    Config.closeDialog(activity, mDialog);
            }
        });
    }

    public String convertSlotTime(String date) {
        final String OLD_FORMAT = "HH:mm";
        final String NEW_FORMAT = "hh:mm aa";

        String slotTime = "";
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(OLD_FORMAT);
            Date d = sdf.parse(date);
            sdf.applyPattern(NEW_FORMAT);
            slotTime = sdf.format(d);
        } catch (ParseException e) {
            // TODO: handle exception
        }
        String str = slotTime.replace("am", "AM").replace("pm", "PM");
        return str;
    }


    private String formatDate(String availableDate) {

        String dtStart = availableDate;
        Date dateParse = null;
        SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd");
        try {
            dateParse = format1.parse(dtStart);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        SimpleDateFormat df = new SimpleDateFormat("MMM dd");
        String nAvailDate = df.format(dateParse);

        return nAvailDate;
    }

}
