package com.jaldeeinc.jaldee.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.jaldeeinc.jaldee.R;
import com.jaldeeinc.jaldee.activities.Appointment;
import com.jaldeeinc.jaldee.activities.CheckIn;
import com.jaldeeinc.jaldee.activities.SearchServiceActivity;
import com.jaldeeinc.jaldee.callback.SearchLocationAdpterCallback;
import com.jaldeeinc.jaldee.common.Config;
import com.jaldeeinc.jaldee.custom.AppointmentServiceInfoDialog;
import com.jaldeeinc.jaldee.custom.CustomTypefaceSpan;
import com.jaldeeinc.jaldee.custom.LocationAmenitiesDialog;
import com.jaldeeinc.jaldee.custom.ServiceInfoDialog;
import com.jaldeeinc.jaldee.custom.UserAppServicesDialog;
import com.jaldeeinc.jaldee.database.DatabaseHandler;
import com.jaldeeinc.jaldee.model.DepartmentModal;
import com.jaldeeinc.jaldee.model.DepartmentUserSearchModel;
import com.jaldeeinc.jaldee.model.SearchModel;
import com.jaldeeinc.jaldee.model.WorkingModel;
import com.jaldeeinc.jaldee.response.SearchAWsResponse;
import com.jaldeeinc.jaldee.response.SearchAppointmentDepartmentServices;
import com.jaldeeinc.jaldee.response.SearchCheckInMessage;
import com.jaldeeinc.jaldee.response.SearchDepartmentServices;
import com.jaldeeinc.jaldee.response.SearchLocation;
import com.jaldeeinc.jaldee.response.SearchService;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import kotlin.Unit;

import static com.jaldeeinc.jaldee.adapter.SearchLocationAdapter.getWaitingTime;
import static com.jaldeeinc.jaldee.connection.ApiClient.context;
import static com.jaldeeinc.jaldee.utils.DialogUtilsKt.showUIDialog;

public class UserDetailAdapter extends RecyclerView.Adapter<UserDetailAdapter.MyViewHolder> {

    private Context context;
    ArrayList<DepartmentUserSearchModel> userDetails = new ArrayList<DepartmentUserSearchModel>();
    private String subDomain;
    private String Domain;
    SearchModel domainList = new SearchModel();
    boolean mShowWaitTime = false;
    ArrayList<ParkingModel> listType = new ArrayList<>();
    SearchAWsResponse mSearchAWSResponse;
    ArrayList<SearchDepartmentServices> mSearchDepartmentList;
    String terminology;
    private SearchLocationAdpterCallback adaptercallback;
    List<SearchCheckInMessage> mCheckInMessage;
    ArrayList<SearchService> servicesList = new ArrayList<>();
    ArrayList<SearchService> appServices = new ArrayList<>();
    ArrayList<SearchAppointmentDepartmentServices> apptList = new ArrayList<>();
    Typeface tyface;
    List<SearchService> apptServices;
    ServiceInfoDialog serviceInfoDialog;
    AppointmentServiceInfoDialog appServInfoDialog;
    UserAppServicesDialog userAppServicesDialog;
    List<SearchAppointmentDepartmentServices> appointServices;


    public UserDetailAdapter(Context mContext, ArrayList<DepartmentUserSearchModel> userDetails, SearchAWsResponse mSearchAWSResponse, ArrayList<SearchDepartmentServices> mDepartmentsList, String terminology, SearchLocationAdpterCallback mInterface, ArrayList<SearchCheckInMessage> mSearchmCheckMessageList) {

        this.context = mContext;
        this.userDetails = userDetails;
        this.mSearchAWSResponse = mSearchAWSResponse;
        this.mSearchDepartmentList = mDepartmentsList;
        this.terminology = terminology;
        this.adaptercallback = mInterface;
        this.mCheckInMessage = mSearchmCheckMessageList;


    }

    @NonNull
    @Override
    public UserDetailAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.user_detail, parent, false);
        return new UserDetailAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull UserDetailAdapter.MyViewHolder holder, int position) {

        DepartmentUserSearchModel departmentUserSearchModel = userDetails.get(position);

        holder.rlLocationLayout.setVisibility(View.GONE);
        // getting subdomain from local db
        DatabaseHandler db = new DatabaseHandler(context);
        domainList = db.getSubDomainsByFilter(userDetails.get(position).getSearchViewDetail().getServiceSector().getDisplayName(), userDetails.get(position).getSearchViewDetail().getUserSubdomain());

        Domain = userDetails.get(position).getParentSearchViewDetail().getServiceSector().getDisplayName();

        tyface = Typeface.createFromAsset(context.getAssets(),
                "fonts/Montserrat_Bold.otf");
        holder.tv_place.setTypeface(tyface);
        holder.tv_open.setTypeface(tyface);
        holder.btn_checkin.setTypeface(tyface);
        holder.btn_appointments.setTypeface(tyface);
        holder.btn_donations.setTypeface(tyface);

        for (int i = 0; i < mCheckInMessage.size(); i++) {
            holder.tv_checkin.setVisibility(View.VISIBLE);
            //  myViewHolder.tv_checkin.setText("You have "+mCheckInMessage.get(i).getmAllSearch_checkIn().size()+" Check-In at this location");
//                Config.logV("Locationttt-----kkkk###########@@@@@@" + searchLoclist.getId());
            Config.logV("Locationttt-----aaaa###########@@@@@@" + mCheckInMessage.get(i).getmAllSearch_checkIn().size());
            if (terminology != null) {
                String firstWord = "You have ";
                String secondWord = mCheckInMessage.get(i).getmAllSearch_checkIn().size() + " " + terminology;
                String thirdword = " for this provider";
                Spannable spannable = new SpannableString(firstWord + secondWord + thirdword);
                Typeface tyface_edittext2 = Typeface.createFromAsset(context.getAssets(),
                        "fonts/Montserrat_Bold.otf");
                spannable.setSpan(new CustomTypefaceSpan("sans-serif", tyface_edittext2), firstWord.length(), firstWord.length() + secondWord.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                holder.tv_checkin.setText(spannable);

            }
        }
        holder.tv_checkin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                adaptercallback.onMethodCheckinCallback(userDetails.get(position).getLocation().getId(), "show", userDetails.get(position).getLocation().getPlace());
            }
        });
        // location
        if (userDetails.get(position).getLocation().getAddress() != null && userDetails.get(position).getLocation().getAddress().contains(userDetails.get(position).getLocation().getPlace())) {
            holder.tv_place.setText(userDetails.get(position).getLocation().getPlace());
        } else {
            holder.tv_place.setText(userDetails.get(position).getLocation().getPlace() + " " + "," + " " + userDetails.get(position).getLocation().getAddress());
        }

        if (userDetails.get(position).getLocation().getGoogleMapUrl() != null && !userDetails.get(position).getLocation().getGoogleMapUrl().equalsIgnoreCase("")) {
            holder.Ldirectionlayout.setVisibility(View.VISIBLE);
        } else {
            holder.Ldirectionlayout.setVisibility(View.GONE);
        }
        holder.txtdirection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Config.logV("googlemap url--------" + userDetails.get(position).getLocation().getGoogleMapUrl());
                String geoUri = userDetails.get(position).getLocation().getGoogleMapUrl();
                try {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(geoUri));
                    context.startActivity(intent);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        listType.clear();
        handleLocationAmenities(holder, departmentUserSearchModel);
        if (userDetails.get(position).getLocation().getAddress() != null && userDetails.get(position).getLocation().getAddress().contains(userDetails.get(position).getLocation().getPlace())) {
            holder.tv_place.setText(userDetails.get(position).getLocation().getPlace());
        } else {
            holder.tv_place.setText(userDetails.get(position).getLocation().getPlace() + " " + "," + " " + userDetails.get(position).getLocation().getAddress());
        }
        if (userDetails.get(position).getLocation().getbSchedule() != null) {
            if (userDetails.get(position).getLocation().getbSchedule().getTimespec().size() > 0) {
                holder.tv_working.setVisibility(View.VISIBLE);
            } else {
                holder.tv_working.setVisibility(View.GONE);
            }
        }
        if (position == 0) {
            holder.mLayouthide.setVisibility(View.VISIBLE);
            holder.img_arrow.setImageResource(R.drawable.icon_angle_up);
//            departmentUserSearchModel.setExpandFlag(true);
            holder.LexpandCheckin.setVisibility(View.GONE);

        } else {
            holder.mLayouthide.setVisibility(View.GONE);
            holder.img_arrow.setImageResource(R.drawable.icon_angle_down);
            holder.LexpandCheckin.setVisibility(View.GONE);
        }

        if (userDetails.get(position).getQueueList().getNextAvailableQueue() != null) {
            if (userDetails.get(position).getSearchViewDetail().isOnlinePresence() && userDetails.get(position).getQueueList().getNextAvailableQueue().isWaitlistEnabled()) {
                if (userDetails.get(position).getQueueList().getNextAvailableQueue() != null && userDetails.get(position).getQueueList().getNextAvailableQueue().getAvailableDate() != null && userDetails.get(position).getSettings().isFutureDateWaitlist()) {
//                        myViewHolder.txt_diffdate.setVisibility(View.VISIBLE);
//                        myViewHolder.txt_diffdate_expand.setVisibility(View.VISIBLE);
                } else {
                    holder.txt_diffdate.setVisibility(View.GONE);
                    holder.txt_diffdate_expand.setVisibility(View.GONE);
                }
            } else {
                holder.txt_diffdate.setVisibility(View.GONE);
                holder.txt_diffdate_expand.setVisibility(View.GONE);
            }
        } else {
            holder.txt_diffdate.setVisibility(View.GONE);
            holder.txt_diffdate_expand.setVisibility(View.GONE);
        }

        holder.txt_diffdate_expand.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Config.logV("DETAIL !!!!!!!!!!!!!------"+);
                Intent iCheckIn = new Intent(v.getContext(), CheckIn.class);
                iCheckIn.putExtra("serviceId", userDetails.get(position).getLocation().getId());
                iCheckIn.putExtra("uniqueID", userDetails.get(position).getParentSearchViewDetail().getUniqueId());
                iCheckIn.putExtra("accountID", userDetails.get(position).getSearchViewDetail().getId());
                iCheckIn.putExtra("from", "searchdetail_future");
                iCheckIn.putExtra("title", userDetails.get(position).getSearchViewDetail().getBusinessName());
                iCheckIn.putExtra("place", userDetails.get(position).getLocation().getPlace());
                iCheckIn.putExtra("googlemap", userDetails.get(position).getLocation().getGoogleMapUrl());
                iCheckIn.putExtra("sector", Domain);
                iCheckIn.putExtra("subsector", domainList.getDisplayname());
                iCheckIn.putExtra("terminology", terminology);
                iCheckIn.putExtra("userId", Integer.parseInt(userDetails.get(position).getQueueList().getProvider().getId()));
                iCheckIn.putExtra("userName", userDetails.get(position).getSearchViewDetail().getBusinessName());
                if (userDetails.get(position).getServices().size() != 0) {
                    iCheckIn.putExtra("departmentId", String.valueOf(userDetails.get(position).getServices().get(0).getDepartment()));
                }
                iCheckIn.putExtra("virtualservices", userDetails.get(position).getSearchViewDetail().isVirtualServices());
                if (userDetails.get(position).getQueueList().getNextAvailableQueue() != null) {
                    iCheckIn.putExtra("getAvail_date", userDetails.get(position).getQueueList().getNextAvailableQueue().getAvailableDate());
                }
                context.startActivity(iCheckIn);
            }
        });
        holder.txt_diffdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Config.logV("DETAIL !!!!!!!!!!!!!------"+);
                Intent iCheckIn = new Intent(v.getContext(), CheckIn.class);
                iCheckIn.putExtra("serviceId", userDetails.get(position).getLocation().getId());
                iCheckIn.putExtra("uniqueID", userDetails.get(position).getParentSearchViewDetail().getUniqueId());
                iCheckIn.putExtra("accountID", userDetails.get(position).getSearchViewDetail().getId());
                iCheckIn.putExtra("from", "searchdetail_future");
                iCheckIn.putExtra("title", userDetails.get(position).getSearchViewDetail().getBusinessName());
                iCheckIn.putExtra("place", userDetails.get(position).getLocation().getPlace());
                iCheckIn.putExtra("googlemap", userDetails.get(position).getLocation().getGoogleMapUrl());
                iCheckIn.putExtra("sector", Domain);
                iCheckIn.putExtra("subsector", domainList.getDisplayname());
                iCheckIn.putExtra("terminology", terminology);
                iCheckIn.putExtra("userId", Integer.parseInt(userDetails.get(position).getQueueList().getProvider().getId()));
                iCheckIn.putExtra("userName", userDetails.get(position).getSearchViewDetail().getBusinessName());
                if (userDetails.get(position).getServices().size() != 0) {
                    iCheckIn.putExtra("departmentId", String.valueOf(userDetails.get(position).getServices().get(0).getDepartment()));
                }
                iCheckIn.putExtra("virtualservices", userDetails.get(position).getSearchViewDetail().isVirtualServices());
                if (userDetails.get(position).getQueueList().getNextAvailableQueue() != null) {
                    iCheckIn.putExtra("getAvail_date", userDetails.get(position).getQueueList().getNextAvailableQueue().getAvailableDate());
                }
                context.startActivity(iCheckIn);
            }
        });


        holder.btn_checkin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent iCheckIn = new Intent(v.getContext(), CheckIn.class);
                iCheckIn.putExtra("serviceId", userDetails.get(position).getLocation().getId());
                iCheckIn.putExtra("uniqueID", userDetails.get(position).getParentSearchViewDetail().getUniqueId());
                iCheckIn.putExtra("accountID", String.valueOf(userDetails.get(position).getSearchViewDetail().getId()));
                iCheckIn.putExtra("from", "multiusercheckin");
                iCheckIn.putExtra("title", userDetails.get(position).getSearchViewDetail().getBusinessName());
                iCheckIn.putExtra("place", userDetails.get(position).getLocation().getPlace());
                iCheckIn.putExtra("googlemap", userDetails.get(position).getLocation().getGoogleMapUrl());
                iCheckIn.putExtra("sector", Domain);
                iCheckIn.putExtra("subsector", domainList.getDisplayname());
                iCheckIn.putExtra("terminology", terminology);
                iCheckIn.putExtra("userId", Integer.parseInt(userDetails.get(position).getQueueList().getProvider().getId()));
                iCheckIn.putExtra("userName", userDetails.get(position).getSearchViewDetail().getBusinessName());
                if (userDetails.get(position).getServices().size() != 0) {
                    iCheckIn.putExtra("departmentId", String.valueOf(userDetails.get(position).getServices().get(0).getDepartment()));
                }
                iCheckIn.putExtra("virtualservices", userDetails.get(position).getSearchViewDetail().isVirtualServices());
                if (userDetails.get(position).getQueueList().getNextAvailableQueue() != null) {
                    iCheckIn.putExtra("getAvail_date", userDetails.get(position).getQueueList().getNextAvailableQueue().getAvailableDate());
                }
                context.startActivity(iCheckIn);
            }
        });

        holder.btn_checkin_expand.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent iCheckIn = new Intent(v.getContext(), CheckIn.class);
                iCheckIn.putExtra("serviceId", userDetails.get(position).getLocation().getId());
                iCheckIn.putExtra("uniqueID", userDetails.get(position).getParentSearchViewDetail().getUniqueId());
                iCheckIn.putExtra("accountID", userDetails.get(position).getSearchViewDetail().getId());
                iCheckIn.putExtra("from", "multiusercheckin");
                iCheckIn.putExtra("title", userDetails.get(position).getSearchViewDetail().getBusinessName());
                iCheckIn.putExtra("place", userDetails.get(position).getLocation().getPlace());
                iCheckIn.putExtra("googlemap", userDetails.get(position).getLocation().getGoogleMapUrl());
                iCheckIn.putExtra("sector", Domain);
                iCheckIn.putExtra("subsector", domainList.getDisplayname());
                iCheckIn.putExtra("terminology", terminology);
                iCheckIn.putExtra("userId", Integer.parseInt(userDetails.get(position).getQueueList().getProvider().getId()));
                iCheckIn.putExtra("userName", userDetails.get(position).getSearchViewDetail().getBusinessName());
                if (userDetails.get(position).getServices().size() != 0) {
                    iCheckIn.putExtra("departmentId", String.valueOf(userDetails.get(position).getServices().get(0).getDepartment()));
                }
                iCheckIn.putExtra("virtualservices", userDetails.get(position).getSearchViewDetail().isVirtualServices());
                if (userDetails.get(position).getQueueList().getNextAvailableQueue() != null) {
                    iCheckIn.putExtra("getAvail_date", userDetails.get(position).getQueueList().getNextAvailableQueue().getAvailableDate());
                }
                context.startActivity(iCheckIn);
            }
        });
        holder.btn_appointments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent iAppointment = new Intent(v.getContext(), Appointment.class);
                iAppointment.putExtra("serviceId", userDetails.get(position).getLocation().getId());
                iAppointment.putExtra("uniqueID", userDetails.get(position).getParentSearchViewDetail().getUniqueId());
                iAppointment.putExtra("accountID", String.valueOf(userDetails.get(position).getSearchViewDetail().getId()));
                iAppointment.putExtra("from", "multiusercheckin");
                iAppointment.putExtra("title", userDetails.get(position).getSearchViewDetail().getBusinessName());
                iAppointment.putExtra("place", userDetails.get(position).getLocation().getPlace());
                iAppointment.putExtra("googlemap", userDetails.get(position).getLocation().getGoogleMapUrl());
                iAppointment.putExtra("sector", Domain);
                iAppointment.putExtra("subsector", domainList.getDisplayname());
                iAppointment.putExtra("terminology", terminology);
                iAppointment.putExtra("userId", Integer.parseInt(userDetails.get(position).getScheduleList().getProvider().getId()));
                iAppointment.putExtra("userName", userDetails.get(position).getSearchViewDetail().getBusinessName());
                if (userDetails.get(position).getServices().size() != 0) {
                    iAppointment.putExtra("departmentId", String.valueOf(userDetails.get(position).getServices().get(0).getDepartment()));
                }
                iAppointment.putExtra("virtualservices", userDetails.get(position).getSearchViewDetail().isVirtualServices());
                context.startActivity(iAppointment);
            }
        });

        if (userDetails.get(position).getServices() != null) {

            if (userDetails.get(position).getServices().size() > 0) {
                servicesList.clear();
                servicesList = SomeConstructor(userDetails.get(position).getServices());
                if (servicesList != null) {

                    if (servicesList.size() > 0) {

                        holder.LService_2.setVisibility(View.VISIBLE);
                        holder.txtservices.setVisibility(View.VISIBLE);
                        if (servicesList.size() == 1) {

                            holder.txtservice1.setVisibility(View.VISIBLE);
                            holder.txtservice2.setVisibility(View.GONE);
                            holder.txtSeeAll.setVisibility(View.GONE);
                            String name = servicesList.get(0).getName();
                            name = name.substring(0, 1).toUpperCase() + name.substring(1).toLowerCase();
                            holder.txtservice1.setText(name);
                            try{
                            if(servicesList.get(0).getServiceType().equalsIgnoreCase("virtualservice")){
                                if(servicesList.get(0).getVirtualCallingModes().get(0).getCallingMode().equalsIgnoreCase("Zoom")){
                                    holder.txtservice1.setCompoundDrawablesWithIntrinsicBounds(R.drawable.zoomicon_sized,0,0,0);
                                    holder.txtservice1.setCompoundDrawablePadding(10);
                                }
                                else if(servicesList.get(0).getVirtualCallingModes().get(0).getCallingMode().equalsIgnoreCase("GoogleMeet")){
                                    holder.txtservice1.setCompoundDrawablesWithIntrinsicBounds(R.drawable.googlemeet_sized,0,0,0);
                                    holder.txtservice1.setCompoundDrawablePadding(10);
                                }
                                else if(servicesList.get(0).getVirtualCallingModes().get(0).getCallingMode().equalsIgnoreCase("WhatsApp")){
                                    holder.txtservice1.setCompoundDrawablesWithIntrinsicBounds(R.drawable.whatsappicon_sized,0,0,0);
                                    holder.txtservice1.setCompoundDrawablePadding(10);
                                }
                                else if(servicesList.get(0).getVirtualCallingModes().get(0).getCallingMode().equalsIgnoreCase("phone")){
                                    holder.txtservice1.setCompoundDrawablesWithIntrinsicBounds(R.drawable.phoneiconsized_small,0,0,0);
                                    holder.txtservice1.setCompoundDrawablePadding(10);
                                }
                            }
                            }
                            catch(Exception e){
                                e.printStackTrace();
                            }
                            holder.txtservice1.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {

                                    serviceInfoDialog = new ServiceInfoDialog(v.getContext(), servicesList.get(0));
                                    serviceInfoDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                                    serviceInfoDialog.show();
                                    DisplayMetrics metrics = v.getContext().getResources().getDisplayMetrics();
                                    int width = (int) (metrics.widthPixels * 1);
                                    serviceInfoDialog.getWindow().setLayout(width, LinearLayout.LayoutParams.WRAP_CONTENT);
                                }
                            });

//                             Toast.makeText(mContext, "set single line", Toast.LENGTH_SHORT).show();
                        } else if (servicesList.size() >= 2 && servicesList.get(0).getName().length() <= 20 && servicesList.get(1).getName().length() <= 20) {

                            if (servicesList.size() == 2) {

                                holder.txtservice1.setVisibility(View.VISIBLE);
                                holder.txtservice2.setVisibility(View.VISIBLE);
                                holder.txtSeeAll.setVisibility(View.GONE);
                                String name1 = servicesList.get(0).getName();
                                name1 = name1.substring(0, 1).toUpperCase() + name1.substring(1).toLowerCase();
                                holder.txtservice1.setText(name1 + ",");
                                try{
                                if(servicesList.get(0).getServiceType().equalsIgnoreCase("virtualservice")){
                                    if(servicesList.get(0).getVirtualCallingModes().get(0).getCallingMode().equalsIgnoreCase("Zoom")){
                                        holder.txtservice1.setCompoundDrawablesWithIntrinsicBounds(R.drawable.zoomicon_sized,0,0,0);
                                        holder.txtservice1.setCompoundDrawablePadding(10);
                                    }
                                    else if(servicesList.get(0).getVirtualCallingModes().get(0).getCallingMode().equalsIgnoreCase("GoogleMeet")){
                                        holder.txtservice1.setCompoundDrawablesWithIntrinsicBounds(R.drawable.googlemeet_sized,0,0,0);
                                        holder.txtservice1.setCompoundDrawablePadding(10);
                                    }
                                    else if(servicesList.get(0).getVirtualCallingModes().get(0).getCallingMode().equalsIgnoreCase("WhatsApp")){
                                        holder.txtservice1.setCompoundDrawablesWithIntrinsicBounds(R.drawable.whatsappicon_sized,0,0,0);
                                        holder.txtservice1.setCompoundDrawablePadding(10);
                                    }
                                    else if(servicesList.get(0).getVirtualCallingModes().get(0).getCallingMode().equalsIgnoreCase("phone")){
                                        holder.txtservice1.setCompoundDrawablesWithIntrinsicBounds(R.drawable.phoneiconsized_small,0,0,0);
                                        holder.txtservice1.setCompoundDrawablePadding(10);
                                    }
                                }
                                }
                                catch(Exception e){
                                    e.printStackTrace();
                                }
                                String name2 = servicesList.get(1).getName();
                                name2 = name2.substring(0, 1).toUpperCase() + name2.substring(1).toLowerCase();
                                holder.txtservice2.setText(name2);
                                try{
                                if(servicesList.get(1).getServiceType().equalsIgnoreCase("virtualservice")){
                                    if(servicesList.get(1).getVirtualCallingModes().get(0).getCallingMode().equalsIgnoreCase("Zoom")){
                                        holder.txtservice2.setCompoundDrawablesWithIntrinsicBounds(R.drawable.zoomicon_sized,0,0,0);
                                        holder.txtservice2.setCompoundDrawablePadding(10);
                                    }
                                    else if(servicesList.get(1).getVirtualCallingModes().get(0).getCallingMode().equalsIgnoreCase("GoogleMeet")){
                                        holder.txtservice2.setCompoundDrawablesWithIntrinsicBounds(R.drawable.googlemeet_sized,0,0,0);
                                        holder.txtservice2.setCompoundDrawablePadding(10);
                                    }
                                    else if(servicesList.get(1).getVirtualCallingModes().get(0).getCallingMode().equalsIgnoreCase("WhatsApp")){
                                        holder.txtservice2.setCompoundDrawablesWithIntrinsicBounds(R.drawable.whatsappicon_sized,0,0,0);
                                        holder.txtservice2.setCompoundDrawablePadding(10);
                                    }
                                    else if(servicesList.get(1).getVirtualCallingModes().get(0).getCallingMode().equalsIgnoreCase("phone")){
                                        holder.txtservice2.setCompoundDrawablesWithIntrinsicBounds(R.drawable.phoneiconsized_small,0,0,0);
                                        holder.txtservice2.setCompoundDrawablePadding(10);
                                    }
                                }
                                }
                                catch(Exception e){
                                    e.printStackTrace();
                                }
                                holder.txtservice1.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        serviceInfoDialog = new ServiceInfoDialog(v.getContext(), servicesList.get(0));
                                        serviceInfoDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                                        serviceInfoDialog.show();
                                        DisplayMetrics metrics = v.getContext().getResources().getDisplayMetrics();
                                        int width = (int) (metrics.widthPixels * 1);
                                        serviceInfoDialog.getWindow().setLayout(width, LinearLayout.LayoutParams.WRAP_CONTENT);
                                    }
                                });

                                holder.txtservice2.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        serviceInfoDialog = new ServiceInfoDialog(v.getContext(), servicesList.get(1));
                                        serviceInfoDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                                        serviceInfoDialog.show();
                                        DisplayMetrics metrics = v.getContext().getResources().getDisplayMetrics();
                                        int width = (int) (metrics.widthPixels * 1);
                                        serviceInfoDialog.getWindow().setLayout(width, LinearLayout.LayoutParams.WRAP_CONTENT);
                                    }
                                });

                                // Toast.makeText(mContext, "set text with comma seperated without seemore", Toast.LENGTH_SHORT).show();
                            } else {
                                holder.txtservice1.setVisibility(View.VISIBLE);
                                holder.txtservice2.setVisibility(View.VISIBLE);
                                holder.txtSeeAll.setVisibility(View.VISIBLE);
                                String name1 = servicesList.get(0).getName();
                                name1 = name1.substring(0, 1).toUpperCase() + name1.substring(1).toLowerCase();
                                holder.txtservice1.setText(name1 + ",");
                                try{
                                if(servicesList.get(0).getServiceType().equalsIgnoreCase("virtualservice")){
                                    if(servicesList.get(0).getVirtualCallingModes().get(0).getCallingMode().equalsIgnoreCase("Zoom")){
                                        holder.txtservice1.setCompoundDrawablesWithIntrinsicBounds(R.drawable.zoomicon_sized,0,0,0);
                                        holder.txtservice1.setCompoundDrawablePadding(10);
                                    }
                                    else if(servicesList.get(0).getVirtualCallingModes().get(0).getCallingMode().equalsIgnoreCase("GoogleMeet")){
                                        holder.txtservice1.setCompoundDrawablesWithIntrinsicBounds(R.drawable.googlemeet_sized,0,0,0);
                                        holder.txtservice1.setCompoundDrawablePadding(10);
                                    }
                                    else if(servicesList.get(0).getVirtualCallingModes().get(0).getCallingMode().equalsIgnoreCase("WhatsApp")){
                                        holder.txtservice1.setCompoundDrawablesWithIntrinsicBounds(R.drawable.whatsappicon_sized,0,0,0);
                                        holder.txtservice1.setCompoundDrawablePadding(10);
                                    }
                                    else if(servicesList.get(0).getVirtualCallingModes().get(0).getCallingMode().equalsIgnoreCase("phone")){
                                        holder.txtservice1.setCompoundDrawablesWithIntrinsicBounds(R.drawable.phoneiconsized_small,0,0,0);
                                        holder.txtservice1.setCompoundDrawablePadding(10);
                                    }
                                }
                                }
                                catch(Exception e){
                                    e.printStackTrace();
                                }
                                String name2 = servicesList.get(1).getName();
                                name2 = name2.substring(0, 1).toUpperCase() + name2.substring(1).toLowerCase();
                                holder.txtservice2.setText(name2 + ",");
                                try{
                                if(servicesList.get(1).getServiceType().equalsIgnoreCase("virtualservice")){
                                    if(servicesList.get(1).getVirtualCallingModes().get(0).getCallingMode().equalsIgnoreCase("Zoom")){
                                        holder.txtservice2.setCompoundDrawablesWithIntrinsicBounds(R.drawable.zoomicon_sized,0,0,0);
                                        holder.txtservice2.setCompoundDrawablePadding(10);
                                    }
                                    else if(servicesList.get(1).getVirtualCallingModes().get(0).getCallingMode().equalsIgnoreCase("GoogleMeet")){
                                        holder.txtservice2.setCompoundDrawablesWithIntrinsicBounds(R.drawable.googlemeet_sized,0,0,0);
                                        holder.txtservice2.setCompoundDrawablePadding(10);
                                    }
                                    else if(servicesList.get(1).getVirtualCallingModes().get(0).getCallingMode().equalsIgnoreCase("WhatsApp")){
                                        holder.txtservice2.setCompoundDrawablesWithIntrinsicBounds(R.drawable.whatsappicon_sized,0,0,0);
                                        holder.txtservice2.setCompoundDrawablePadding(10);
                                    }
                                    else if(servicesList.get(1).getVirtualCallingModes().get(0).getCallingMode().equalsIgnoreCase("phone")){
                                        holder.txtservice2.setCompoundDrawablesWithIntrinsicBounds(R.drawable.phoneiconsized_small,0,0,0);
                                        holder.txtservice2.setCompoundDrawablePadding(10);
                                    }
                                } }
                                catch(Exception e){
                                    e.printStackTrace();
                                }
                                holder.txtservice1.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        serviceInfoDialog = new ServiceInfoDialog(v.getContext(), servicesList.get(0));
                                        serviceInfoDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                                        serviceInfoDialog.show();
                                        DisplayMetrics metrics = v.getContext().getResources().getDisplayMetrics();
                                        int width = (int) (metrics.widthPixels * 1);
                                        serviceInfoDialog.getWindow().setLayout(width, LinearLayout.LayoutParams.WRAP_CONTENT);
                                    }
                                });

                                holder.txtservice2.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        serviceInfoDialog = new ServiceInfoDialog(v.getContext(), servicesList.get(1));
                                        serviceInfoDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                                        serviceInfoDialog.show();
                                        DisplayMetrics metrics = v.getContext().getResources().getDisplayMetrics();
                                        int width = (int) (metrics.widthPixels * 1);
                                        serviceInfoDialog.getWindow().setLayout(width, LinearLayout.LayoutParams.WRAP_CONTENT);
                                    }
                                });
                                holder.txtSeeAll.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        adaptercallback.onMethodServiceCallback(servicesList, userDetails.get(position).getSearchViewDetail().getBusinessName(), mSearchDepartmentList);
                                    }
                                });
                                //  Toast.makeText(mContext, "set text with comma seperated with seemore", Toast.LENGTH_SHORT).show();
                            }
                        } else {

                            for (int i = 0; i < servicesList.size(); i++) {

                                if (i == 0) {

                                    holder.txtservice1.setVisibility(View.VISIBLE);
                                    holder.txtservice2.setVisibility(View.GONE);
                                    String name1 = servicesList.get(0).getName();
                                    name1 = name1.substring(0, 1).toUpperCase() + name1.substring(1).toLowerCase();
                                    holder.txtservice1.setText(name1 + ",");
                                    try{
                                    if(servicesList.get(0).getServiceType().equalsIgnoreCase("virtualservice")){
                                        if(servicesList.get(0).getVirtualCallingModes().get(0).getCallingMode().equalsIgnoreCase("Zoom")){
                                            holder.txtservice1.setCompoundDrawablesWithIntrinsicBounds(R.drawable.zoomicon_sized,0,0,0);
                                            holder.txtservice1.setCompoundDrawablePadding(10);
                                        }
                                        else if(servicesList.get(0).getVirtualCallingModes().get(0).getCallingMode().equalsIgnoreCase("GoogleMeet")){
                                            holder.txtservice1.setCompoundDrawablesWithIntrinsicBounds(R.drawable.googlemeet_sized,0,0,0);
                                            holder.txtservice1.setCompoundDrawablePadding(10);
                                        }
                                        else if(servicesList.get(0).getVirtualCallingModes().get(0).getCallingMode().equalsIgnoreCase("WhatsApp")){
                                            holder.txtservice1.setCompoundDrawablesWithIntrinsicBounds(R.drawable.whatsappicon_sized,0,0,0);
                                            holder.txtservice1.setCompoundDrawablePadding(10);
                                        }
                                        else if(servicesList.get(0).getVirtualCallingModes().get(0).getCallingMode().equalsIgnoreCase("phone")){
                                            holder.txtservice1.setCompoundDrawablesWithIntrinsicBounds(R.drawable.phoneiconsized_small,0,0,0);
                                            holder.txtservice1.setCompoundDrawablePadding(10);
                                        }
                                    }
                                    }
                                    catch(Exception e){
                                        e.printStackTrace();
                                    }
                                    holder.txtservice1.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            serviceInfoDialog = new ServiceInfoDialog(v.getContext(), servicesList.get(0));
                                            serviceInfoDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                                            serviceInfoDialog.show();
                                            DisplayMetrics metrics = v.getContext().getResources().getDisplayMetrics();
                                            int width = (int) (metrics.widthPixels * 1);
                                            serviceInfoDialog.getWindow().setLayout(width, LinearLayout.LayoutParams.WRAP_CONTENT);
                                        }
                                    });
                                    holder.txtSeeAll.setVisibility(View.VISIBLE);
                                    holder.txtSeeAll.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            adaptercallback.onMethodServiceCallback(servicesList, userDetails.get(position).getSearchViewDetail().getBusinessName(), mSearchDepartmentList);
                                        }
                                    });

                                    // Toast.makeText(mContext, "set single line and see more", Toast.LENGTH_SHORT).show();
                                    break;
                                }
                            }
//        for (int i = 0; i < mSearchServiceList.size(); i++) {
//            Config.logV("1--" + searchLoclist.getId() + "  2--" + mSearchServiceList.get(i).getLocid());
//            String services = "";
//            if (searchLoclist.getId() == mSearchServiceList.get(i).getLocid()) {
//                int size = mSearchServiceList.get(i).getmAllService().size();
//                if (size == 1) {
//                    size = 1;
//                } else if (size >= 2) {
//                    size = 2;
//                }
//                if (size > 0) {
//                    if (size == 1) {
//                        myViewHolder.mLSeriveLayout.setVisibility(View.GONE);
//                        myViewHolder.txtservice1.setVisibility(View.VISIBLE);
//                        myViewHolder.txtservice2.setVisibility(View.GONE);
//                        myViewHolder.txtSeeAll.setVisibility(View.GONE);
//                        myViewHolder.txtservice1.setText(mSearchServiceList.get(i).getmAllService().get(0).getName());
//
//                        final String mServicename = mSearchServiceList.get(i).getmAllService().get(0).getName();
//                        final String mServiceprice = mSearchServiceList.get(i).getmAllService().get(0).getTotalAmount();
//                        final String mServicedesc = mSearchServiceList.get(i).getmAllService().get(0).getDescription();
//                        final String mServiceduration = mSearchServiceList.get(i).getmAllService().get(0).getServiceDuration();
//                        final boolean mTaxable = mSearchServiceList.get(i).getmAllService().get(0).isTaxable();
//                        final ArrayList<SearchService> mServiceGallery = mSearchServiceList.get(i).getmAllService().get(0).getServicegallery();
//                        final int mDepartmentCode = mSearchServiceList.get(i).getmAllService().get(0).getDepartment();
//
//                        final boolean isPrepayment = mSearchServiceList.get(i).getmAllService().get(0).isPrePayment();
//                        final String minPrepayment = mSearchServiceList.get(i).getmAllService().get(0).getMinPrePaymentAmount();
//                        myViewHolder.txtservice1.setOnClickListener(new View.OnClickListener() {
//                            @Override
//                            public void onClick(View v) {
//                                Intent iService = new Intent(v.getContext(), SearchServiceActivity.class);
//                                iService.putExtra("name", mServicename);
//                                iService.putExtra("duration", mServiceduration);
//                                iService.putExtra("price", mServiceprice);
//                                iService.putExtra("desc", mServicedesc);
//                                iService.putExtra("servicegallery", mServiceGallery);
//                                iService.putExtra("taxable", mTaxable);
//                                iService.putExtra("title", mTitle);
//                                iService.putExtra("isPrePayment", isPrepayment);
//                                iService.putExtra("MinPrePaymentAmount", minPrepayment);
//                                iService.putExtra("departmentName", mDepartmentCode);
//                                iService.putExtra("from", "chk");
//                                mContext.startActivity(iService);
//                            }
//                        });
//
//                    } else {
//                        myViewHolder.mLSeriveLayout.setVisibility(View.GONE);
//                        myViewHolder.txtservice1.setVisibility(View.VISIBLE);
//                        myViewHolder.txtservice2.setVisibility(View.VISIBLE);
//                        if (mSearchServiceList.get(i).getmAllService().size() == 2) {
//                            myViewHolder.txtSeeAll.setVisibility(View.GONE);
//                        } else {
//                            myViewHolder.txtSeeAll.setVisibility(View.VISIBLE);
//                        }
//                        if (mSearchServiceList.get(i).getmAllService().get(0).getDepartment() != 0) {
//                            String deptName1 = getDepartmentName(mSearchServiceList.get(i).getmAllService().get(0).getDepartment());
//                            String deptName2 = getDepartmentName(mSearchServiceList.get(i).getmAllService().get(1).getDepartment());
//                            myViewHolder.txtservice1.setText(mSearchServiceList.get(i).getmAllService().get(0).getName().concat(" (").concat(deptName1).concat(")"));
//                            myViewHolder.txtservice2.setText(mSearchServiceList.get(i).getmAllService().get(1).getName().concat(" (").concat(deptName2).concat(")"));
//                        } else {
//                            myViewHolder.txtservice1.setText(mSearchServiceList.get(i).getmAllService().get(0).getName());
//                            myViewHolder.txtservice2.setText(mSearchServiceList.get(i).getmAllService().get(1).getName());
//                        }
//
//
//                        final int finalI = i;
//                        myViewHolder.txtSeeAll.setOnClickListener(new View.OnClickListener() {
//                            @Override
//                            public void onClick(View v) {
//                                adaptercallback.onMethodServiceCallback(mSearchServiceList.get(finalI).getmAllService(), mTitle, mSearchDepartmentList);
//                            }
//                        });
//                        String mServicename;
//                        if (mSearchServiceList.get(i).getmAllService().get(0).getDepartment() != 0) {
//                            String deptName1 = getDepartmentName(mSearchServiceList.get(i).getmAllService().get(0).getDepartment());
//                            mServicename = mSearchServiceList.get(i).getmAllService().get(0).getName().concat(" (").concat(deptName1).concat(")");
//                        } else {
//                            mServicename = mSearchServiceList.get(i).getmAllService().get(0).getName();
//                        }
//
//                        final String mServiceprice = mSearchServiceList.get(i).getmAllService().get(0).getTotalAmount();
//                        final String mServicedesc = mSearchServiceList.get(i).getmAllService().get(0).getDescription();
//                        final String mServiceduration = mSearchServiceList.get(i).getmAllService().get(0).getServiceDuration();
//                        final boolean mTaxable = mSearchServiceList.get(i).getmAllService().get(0).isTaxable();
//                        final ArrayList<SearchService> mServiceGallery = mSearchServiceList.get(i).getmAllService().get(0).getServicegallery();
//
//                        final boolean isPrepayment = mSearchServiceList.get(i).getmAllService().get(0).isPrePayment();
//                        final String minPrepayment = mSearchServiceList.get(i).getmAllService().get(0).getMinPrePaymentAmount();
//                        final String serviceName1 = mServicename;
//                        myViewHolder.txtservice1.setOnClickListener(new View.OnClickListener() {
//                            @Override
//                            public void onClick(View v) {
//                                Intent iService = new Intent(v.getContext(), SearchServiceActivity.class);
//                                iService.putExtra("name", serviceName1);
//                                iService.putExtra("duration", mServiceduration);
//                                iService.putExtra("price", mServiceprice);
//                                iService.putExtra("desc", mServicedesc);
//                                iService.putExtra("servicegallery", mServiceGallery);
//                                iService.putExtra("taxable", mTaxable);
//                                iService.putExtra("title", mTitle);
//                                iService.putExtra("isPrePayment", isPrepayment);
//                                iService.putExtra("MinPrePaymentAmount", minPrepayment);
//                                iService.putExtra("from", "chk");
//                                mContext.startActivity(iService);
//                            }
//                        });
//                        String servicename1;
//                        if (mSearchServiceList.get(i).getmAllService().get(1).getDepartment() != 0) {
//                            String deptName1 = getDepartmentName(mSearchServiceList.get(i).getmAllService().get(1).getDepartment());
//                            servicename1 = mSearchServiceList.get(i).getmAllService().get(1).getName().concat(" (").concat(deptName1).concat(")");
//                        } else {
//                            servicename1 = mSearchServiceList.get(i).getmAllService().get(1).getName();
//                        }
//                        final String mServicename1 = servicename1;
////                        String deptName = getDepartmentName(mSearchServiceList.get(i).getmAllService().get(1).getDepartment());
////                        final String mServicename1 = mSearchServiceList.get(i).getmAllService().get(1).getName().concat(" (").concat(deptName).concat(")");
//                        final String mServiceprice1 = mSearchServiceList.get(i).getmAllService().get(1).getTotalAmount();
//                        final String mServicedesc1 = mSearchServiceList.get(i).getmAllService().get(1).getDescription();
//                        final String mServiceduration1 = mSearchServiceList.get(i).getmAllService().get(1).getServiceDuration();
//                        final boolean mTaxable1 = mSearchServiceList.get(i).getmAllService().get(1).isTaxable();
//                        final ArrayList<SearchService> mServiceGallery1 = mSearchServiceList.get(i).getmAllService().get(1).getServicegallery();
//
//                        final boolean isPrepayment1 = mSearchServiceList.get(i).getmAllService().get(1).isPrePayment();
//                        final String minPrepayment1 = mSearchServiceList.get(i).getmAllService().get(1).getMinPrePaymentAmount();
//                        myViewHolder.txtservice2.setOnClickListener(new View.OnClickListener() {
//                            @Override
//                            public void onClick(View v) {
//                                Intent iService = new Intent(v.getContext(), SearchServiceActivity.class);
//                                iService.putExtra("name", mServicename1);
//                                iService.putExtra("duration", mServiceduration1);
//                                iService.putExtra("price", mServiceprice1);
//                                iService.putExtra("desc", mServicedesc1);
//                                iService.putExtra("servicegallery", mServiceGallery1);
//                                iService.putExtra("taxable", mTaxable1);
//                                iService.putExtra("title", mTitle);
//                                iService.putExtra("isPrePayment", isPrepayment1);
//                                iService.putExtra("MinPrePaymentAmount", minPrepayment1);
//                                iService.putExtra("from", "chk");
//                                mContext.startActivity(iService);
//                            }
//                        });
//                    }
//                }
//            }
//        }
                        }
                    }
                }
            } else {

                holder.mLSeriveLayout.setVisibility(View.GONE);
                holder.txtservice1.setVisibility(View.GONE);
                holder.txtservice2.setVisibility(View.GONE);
                holder.txtSeeAll.setVisibility(View.GONE);
                holder.txtservices.setVisibility(View.GONE);
            }
        }


        Date c = Calendar.getInstance().getTime();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        String formattedDate = df.format(c);
        System.out.println("Current time => " + formattedDate);
        if (userDetails.get(position).getSettings().getCalculationMode() != null) {
            if (!userDetails.get(position).getSettings().getCalculationMode().equalsIgnoreCase("NoCalc")) {
                mShowWaitTime = true;
            } else {
                if (userDetails.get(position).getQueueList().getNextAvailableQueue() != null) {
                    if (userDetails.get(position).getSettings().getCalculationMode().equalsIgnoreCase("NoCalc") && userDetails.get(position).getQueueList().getNextAvailableQueue().isShowToken()) {
                        mShowWaitTime = true;
                    } else {
                        mShowWaitTime = false;
                    }
                }
            }
        }

        if (userDetails.get(position).getScheduleList() != null) {
            if (userDetails.get(position).getSearchViewDetail().isOnlinePresence()) {
                if (userDetails.get(position).getScheduleList().isCheckinAllowed()) {
                    holder.LAppointment.setVisibility(View.VISIBLE);
                    holder.LApp_Services.setVisibility(View.VISIBLE);
                    holder.txt_apptservices.setVisibility(View.VISIBLE);
                    holder.rlAppServices.setVisibility(View.VISIBLE);

                } else {
                    holder.LAppointment.setVisibility(View.GONE);
                    holder.LApp_Services.setVisibility(View.GONE);
                    holder.txt_apptservices.setVisibility(View.GONE);
                    holder.txtapptSeeAll.setVisibility(View.GONE);
                    holder.rlAppServices.setVisibility(View.GONE);

                }
            } else {
                holder.LAppointment.setVisibility(View.GONE);
                holder.LApp_Services.setVisibility(View.GONE);
                holder.txt_apptservices.setVisibility(View.GONE);
                holder.txtapptSeeAll.setVisibility(View.GONE);
                holder.rlAppServices.setVisibility(View.GONE);

            }

            if (userDetails.get(position).getUserAppointmentServices() != null) {

                if (userDetails.get(position).getUserAppointmentServices().size() > 0) {

                    setUserAppointmentServices(holder, position);
                } else {

                    holder.LAppointment.setVisibility(View.GONE);
                    holder.LApp_Services.setVisibility(View.GONE);
                    holder.txt_apptservices.setVisibility(View.GONE);
                    holder.txtapptSeeAll.setVisibility(View.GONE);
                    holder.rlAppServices.setVisibility(View.GONE);
                }
            } else if (userDetails.get(position).getAppointmentServices() != null) {

                if (userDetails.get(position).getAppointmentServices().size() > 0) {

                    setAppointmentServices(holder, position);
                } else {
                    holder.LAppointment.setVisibility(View.GONE);
                    holder.LApp_Services.setVisibility(View.GONE);
                    holder.txt_apptservices.setVisibility(View.GONE);
                    holder.txtapptSeeAll.setVisibility(View.GONE);
                    holder.rlAppServices.setVisibility(View.GONE);
                }
            } else {
                holder.LAppointment.setVisibility(View.GONE);
                holder.LApp_Services.setVisibility(View.GONE);
                holder.txt_apptservices.setVisibility(View.GONE);
                holder.txtapptSeeAll.setVisibility(View.GONE);
                holder.rlAppServices.setVisibility(View.GONE);
            }

        }

        if (userDetails.get(position).getQueueList().getNextAvailableQueue() != null) {
            //open Now
            if (userDetails.get(position).getQueueList().getNextAvailableQueue().isOpenNow()) {
                holder.tv_open.setVisibility(View.GONE); // Management asked to hide open now
            } else {
                holder.tv_open.setVisibility(View.GONE);
            }

            //Check-In Button
            Date date1 = null, date2 = null;
            try {
                date1 = df.parse(formattedDate);
                if (userDetails.get(position).getQueueList().getNextAvailableQueue().getAvailableDate() != null)
                    date2 = df.parse(userDetails.get(position).getQueueList().getNextAvailableQueue().getAvailableDate());
            } catch (ParseException e) {
                e.printStackTrace();
            }
            if (mSearchAWSResponse != null) {
                if (mSearchAWSResponse.getHits() != null) {
                    if (mSearchAWSResponse.getHits().getHit() != null) {
                        for (int k = 0; k < mSearchAWSResponse.getHits().getHit().size(); k++) {
                            if (!mSearchAWSResponse.getHits().getHit().isEmpty()) {
                                if (mSearchAWSResponse.getHits().getHit().get(k).getFields() != null && mSearchAWSResponse.getHits().getHit().get(k).getFields().getFuture_checkins() != null) {
                                    if (mSearchAWSResponse.getHits().getHit().get(k).getFields().getFuture_checkins().equals("1")) {
                                        if (userDetails.get(position).getQueueList().getNextAvailableQueue().isShowToken()) {
                                            holder.txt_diffdate.setText("Do you want to Get Token for another day?");
                                            holder.txt_diffdate_expand.setText("Do you want to Get Token for another day?");


                                        } else {
                                            holder.txt_diffdate.setText("Do you want to " + " " + terminology + " for another day?");
                                            holder.txt_diffdate_expand.setText("Do you want to " + " " + terminology + " for another day?");
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
            if (userDetails.get(position).getSearchViewDetail().isOnlinePresence() && userDetails.get(position).getQueueList().getNextAvailableQueue().isWaitlistEnabled()) {
                disableCheckinFeature(holder);
                if (userDetails.get(position).getQueueList().getNextAvailableQueue().isShowToken()) {
                    holder.btn_checkin.setText("GET TOKEN");
                    holder.btn_checkin_expand.setText("GET TOKEN");
                    holder.txtservices.setText("Token Services");
                } else {
                    holder.btn_checkin.setText("Check-in".toUpperCase());
                    holder.txtservices.setText("CheckIn Services");
                    holder.btn_checkin_expand.setText("Check-in".toUpperCase());
                }
                if (userDetails.get(position).getQueueList().getNextAvailableQueue().getAvailableDate() != null) {
                    if (userDetails.get(position).getQueueList().getNextAvailableQueue().isOnlineCheckIn() && userDetails.get(position).getQueueList().getNextAvailableQueue().isAvailableToday() && formattedDate.equalsIgnoreCase(userDetails.get(position).getQueueList().getNextAvailableQueue().getAvailableDate())) { //Today
                        enableCheckinButton(holder);
                        if (userDetails.get(position).getQueueList().getNextAvailableQueue().isShowToken()) {
                            holder.btn_checkin.setText("GET TOKEN");
                            holder.btn_checkin_expand.setText("GET TOKEN");

                            if (userDetails.get(position).getQueueList().getNextAvailableQueue().getCalculationMode().equalsIgnoreCase("NoCalc")) { // NoCalc without show waiting time
                                String message = Config.getPersonsAheadText(userDetails.get(position).getQueueList().getNextAvailableQueue().getPersonAhead());
                                holder.tv_waittime.setText(message);
                                holder.txtwaittime_expand.setText(message);
                                holder.tv_waittime.setVisibility(View.VISIBLE);
                                holder.txtwaittime_expand.setVisibility(View.VISIBLE);
                                holder.txt_peopleahead.setVisibility(View.GONE);
                            } else { // Conventional (Token with Waiting time)
                                holder.tv_waittime.setVisibility(View.VISIBLE);
                                holder.txtwaittime_expand.setVisibility(View.VISIBLE);
                                String spannable = getWaitingTime(userDetails.get(position).getQueueList().getNextAvailableQueue());
                                holder.tv_waittime.setText(spannable);
                                holder.txtwaittime_expand.setText(spannable);
                                holder.txt_peopleahead.setVisibility(View.VISIBLE);
                                String message = Config.getPersonsAheadText(userDetails.get(position).getQueueList().getNextAvailableQueue().getPersonAhead());
                                holder.txt_peopleahead.setText(message);
                            }
                        } else { // Conventional/Fixed
                            holder.btn_checkin.setText("Check-in".toUpperCase());
                            holder.btn_checkin_expand.setText("Check-in".toUpperCase());
                            holder.tv_waittime.setVisibility(View.VISIBLE);
                            holder.txtwaittime_expand.setVisibility(View.VISIBLE);
                            String spannable = getWaitingTime(userDetails.get(position).getQueueList().getNextAvailableQueue());
                            holder.tv_waittime.setText(spannable);
                            holder.txtwaittime_expand.setText(spannable);
                            holder.txt_peopleahead.setVisibility(View.VISIBLE);
                            String message = Config.getPersonsAheadText(userDetails.get(position).getQueueList().getNextAvailableQueue().getPersonAhead());
                            holder.txt_peopleahead.setText(message);
                        }
                    } else {
                        //  disableCheckinButton(myViewHolder);
                        enableCheckinButton(holder);
                    }
                    if (date2 != null && date1.compareTo(date2) < 0) {
                        holder.tv_waittime.setVisibility(View.VISIBLE);
                        holder.txtwaittime_expand.setVisibility(View.VISIBLE);
                        String spannable = getWaitingTime(userDetails.get(position).getQueueList().getNextAvailableQueue());
                        holder.tv_waittime.setText(spannable);
                        holder.txtwaittime_expand.setText(spannable);
                        holder.txt_peopleahead.setVisibility(View.VISIBLE);
                        String message = Config.getPersonsAheadText(userDetails.get(position).getQueueList().getNextAvailableQueue().getPersonAhead());
                        holder.txt_peopleahead.setText(message);
                    }
                    //Future Checkin
                    if (userDetails.get(position).getSettings().isFutureDateWaitlist() && userDetails.get(position).getQueueList().getNextAvailableQueue().getAvailableDate() != null) {
//                                    myViewHolder.txt_diffdate.setVisibility(View.VISIBLE);
//                                    myViewHolder.txt_diffdate_expand.setVisibility(View.VISIBLE);
                        if (userDetails.get(position).getQueueList().getNextAvailableQueue().isShowToken()) {
                            holder.txt_diffdate.setText("Do you want to Get Token for another day?");
                            holder.txt_diffdate_expand.setText("Do you want to Get Token for another day?");
                        } else {
                            holder.txt_diffdate.setText("Do you want to" + " Check-in for another day?");
                            holder.txt_diffdate_expand.setText("Do you want to Get Token for another day?");
                        }
                    } else {
                        holder.txt_diffdate.setVisibility(View.GONE);
                        holder.txt_diffdate_expand.setVisibility(View.GONE);
                    }
                }
            }
        }

    }

    private void setAppointmentServices(MyViewHolder holder, int position) {

        ArrayList<SearchAppointmentDepartmentServices> tempList = new ArrayList<>();
//        for (int m = 0; m < userDetails.get(position).getAppointmentServices().size(); m++) {
//            if (userDetails.get(position).getAppointmentServices() != null) {
//                if (userDetails.get(position).getAppointmentServices().size() > 0) {
//
//                    holder.LApp_Services.removeAllViews();
//                    holder.LApp_Services.setVisibility(View.VISIBLE);
//                    int size = 0;
//                    if (userDetails.get(position).getAppointmentServices().size() == 1) {
//                        size = 1;
//                    } else {
//                        if (userDetails.get(position).getAppointmentServices().size() == 2)
//                            size = 2;
//                        else
//                            size = 3;
//                    }
//                    for (int i = 0; i < size; i++) {
//                        TextView dynaText = new TextView(context);
//                        tyface = Typeface.createFromAsset(context.getAssets(),
//                                "fonts/Montserrat_Regular.otf");
//                        dynaText.setTypeface(tyface);
//                        for (int j = 0; j < userDetails.get(position).getAppointmentServices().get(i).getServices().size(); j++) {
//                            dynaText.setText(aServicesList.get(i).getServices().get(j).getName() + " (" + aServicesList.get(i).getDepartmentName() + " )");
//                            dynaText.setTextSize(13);
//                            dynaText.setPadding(5, 0, 5, 0);
//                            dynaText.setTextColor(mContext.getResources().getColor(R.color.black));
//                            dynaText.setMaxLines(1);
//                        }
//
//                        final int finalI = i;
//                        int finalM = m;
//                        dynaText.setOnClickListener(new View.OnClickListener() {
//                            @Override
//                            public void onClick(View v) {
//                                for (int i = 0; i < aServicesList.size(); i++) {
//                                    for (int j = 0; j < aServicesList.get(i).getServices().size(); j++) {
//                                        if (aServicesList.get(i).getServices().get(j).getName().toLowerCase().equalsIgnoreCase(aServicesList.get(i).getServices().get(j).getName().toLowerCase())) {
//
//                                            appServInfoDialog = new AppointmentServiceInfoDialog(v.getContext(), aServicesList.get(i).getServices().get(j));
//                                            appServInfoDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
//                                            appServInfoDialog.show();
//                                            DisplayMetrics metrics = v.getContext().getResources().getDisplayMetrics();
//                                            int width = (int) (metrics.widthPixels * 1);
//                                            appServInfoDialog.getWindow().setLayout(width, LinearLayout.LayoutParams.WRAP_CONTENT);
//
//                                        }
//                                    }
//                                }
//                            }
//
//                        });
//                        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
//                        params.setMargins(0, 0, 20, 0);
//
//                        dynaText.setLayoutParams(params);
//                        holder.LApp_Services.addView(dynaText);
//                    }
//                    if (size > 3) {
//                        TextView dynaText = new TextView(mContext);
//                        dynaText.setOnClickListener(new View.OnClickListener() {
//                            @Override
//                            public void onClick(View v) {
//                                //                                     mAdapterCallback.onMethodServiceCallback(serviceNames, searchdetailList.getTitle(), searchdetailList.getUniqueid());
//                            }
//                        });
//                        dynaText.setGravity(Gravity.CENTER);
//                        dynaText.setTextColor(mContext.getResources().getColor(R.color.black));
//                        dynaText.setText(" ... ");
//                        holder.LApp_Services.addView(dynaText);
//                    }
//                    tempList.addAll(userDetails.get(position).getAppointmentServices());
//                    apptList.clear();
//                    apptList.addAll(tempList);
//
//                }
//            }
//        }
//
//        holder.rlAppServices.setVisibility(View.VISIBLE);
//        holder.rlAppServices.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                adaptercallback.onMethodServiceCallbackAppointment(apptList, userDetails.get(position).getSearchViewDetail().getBusinessName(), mSearchDepartmentList);
//
//            }
//        });

        appointServices = userDetails.get(position).getAppointmentServices();

        appointServices = appointServices == null ? new ArrayList<SearchAppointmentDepartmentServices>() : appointServices;

        if (appointServices.size() > 0) {

            holder.LApp_Services.setVisibility(View.VISIBLE);
            holder.txt_apptservices.setVisibility(View.VISIBLE);
            if (appointServices.size() == 1) {

                holder.tvAppService1.setVisibility(View.VISIBLE);
                holder.tvAppService2.setVisibility(View.GONE);
                holder.tvAppSeeAll.setVisibility(View.GONE);
                String name = appointServices.get(0).getName();
                name = name.substring(0, 1).toUpperCase() + name.substring(1).toLowerCase();
                holder.tvAppService1.setText(name);
                try{
                if(appointServices.get(0).getServiceType().equalsIgnoreCase("virtualservice")){
                    if(appointServices.get(0).getVirtualCallingModes().get(0).getCallingMode().equalsIgnoreCase("Zoom")){
                        holder.tvAppService1.setCompoundDrawablesWithIntrinsicBounds(R.drawable.zoomicon_sized,0,0,0);
                        holder.tvAppService1.setCompoundDrawablePadding(10);
                    }
                    else if(appointServices.get(0).getVirtualCallingModes().get(0).getCallingMode().equalsIgnoreCase("GoogleMeet")){
                        holder.tvAppService1.setCompoundDrawablesWithIntrinsicBounds(R.drawable.googlemeet_sized,0,0,0);
                        holder.tvAppService1.setCompoundDrawablePadding(10);
                    }
                    else if(appointServices.get(0).getVirtualCallingModes().get(0).getCallingMode().equalsIgnoreCase("WhatsApp")){
                        holder.tvAppService1.setCompoundDrawablesWithIntrinsicBounds(R.drawable.whatsappicon_sized,0,0,0);
                        holder.tvAppService1.setCompoundDrawablePadding(10);
                    }
                    else if(appointServices.get(0).getVirtualCallingModes().get(0).getCallingMode().equalsIgnoreCase("phone")){
                        holder.tvAppService1.setCompoundDrawablesWithIntrinsicBounds(R.drawable.phoneiconsized_small,0,0,0);
                        holder.tvAppService1.setCompoundDrawablePadding(10);
                    }
                }
                }
                catch(Exception e){
                    e.printStackTrace();
                }
                holder.tvAppService1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        appServInfoDialog = new AppointmentServiceInfoDialog(v.getContext(), appointServices.get(0));
                        appServInfoDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                        appServInfoDialog.show();
                        DisplayMetrics metrics = v.getContext().getResources().getDisplayMetrics();
                        int width = (int) (metrics.widthPixels * 1);
                        appServInfoDialog.getWindow().setLayout(width, LinearLayout.LayoutParams.WRAP_CONTENT);

                    }
                });


            } else if (appointServices.size() >= 2 && appointServices.get(0).getName().length() <= 20 && appointServices.get(1).getName().length() <= 20) {

                if (appointServices.size() == 2) {
                    holder.tvAppService1.setVisibility(View.VISIBLE);
                    holder.tvAppService2.setVisibility(View.VISIBLE);
                    holder.tvAppSeeAll.setVisibility(View.GONE);
                    String name1 = appointServices.get(0).getName();
                    name1 = name1.substring(0, 1).toUpperCase() + name1.substring(1).toLowerCase();
                    holder.tvAppService1.setText(name1 + ",");
                    try{
                    if(appointServices.get(0).getServiceType().equalsIgnoreCase("virtualservice")){
                        if(appointServices.get(0).getVirtualCallingModes().get(0).getCallingMode().equalsIgnoreCase("Zoom")){
                            holder.tvAppService1.setCompoundDrawablesWithIntrinsicBounds(R.drawable.zoomicon_sized,0,0,0);
                            holder.tvAppService1.setCompoundDrawablePadding(10);
                        }
                        else if(appointServices.get(0).getVirtualCallingModes().get(0).getCallingMode().equalsIgnoreCase("GoogleMeet")){
                            holder.tvAppService1.setCompoundDrawablesWithIntrinsicBounds(R.drawable.googlemeet_sized,0,0,0);
                            holder.tvAppService1.setCompoundDrawablePadding(10);
                        }
                        else if(appointServices.get(0).getVirtualCallingModes().get(0).getCallingMode().equalsIgnoreCase("WhatsApp")){
                            holder.tvAppService1.setCompoundDrawablesWithIntrinsicBounds(R.drawable.whatsappicon_sized,0,0,0);
                            holder.tvAppService1.setCompoundDrawablePadding(10);
                        }
                        else if(appointServices.get(0).getVirtualCallingModes().get(0).getCallingMode().equalsIgnoreCase("phone")){
                            holder.tvAppService1.setCompoundDrawablesWithIntrinsicBounds(R.drawable.phoneiconsized_small,0,0,0);
                            holder.tvAppService1.setCompoundDrawablePadding(10);
                        }
                    }
                    }
                    catch(Exception e){
                        e.printStackTrace();
                    }
                    String name2 = appointServices.get(1).getName();
                    name2 = name2.substring(0, 1).toUpperCase() + name2.substring(1).toLowerCase();
                    holder.tvAppService2.setText(name2);
                    try{
                    if(appointServices.get(1).getServiceType().equalsIgnoreCase("virtualservice")){
                        if(appointServices.get(1).getVirtualCallingModes().get(0).getCallingMode().equalsIgnoreCase("Zoom")){
                            holder.tvAppService2.setCompoundDrawablesWithIntrinsicBounds(R.drawable.zoomicon_sized,0,0,0);
                            holder.tvAppService2.setCompoundDrawablePadding(10);
                        }
                        else if(appointServices.get(1).getVirtualCallingModes().get(0).getCallingMode().equalsIgnoreCase("GoogleMeet")){
                            holder.tvAppService2.setCompoundDrawablesWithIntrinsicBounds(R.drawable.googlemeet_sized,0,0,0);
                            holder.tvAppService2.setCompoundDrawablePadding(10);
                        }
                        else if(appointServices.get(1).getVirtualCallingModes().get(0).getCallingMode().equalsIgnoreCase("WhatsApp")){
                            holder.tvAppService2.setCompoundDrawablesWithIntrinsicBounds(R.drawable.whatsappicon_sized,0,0,0);
                            holder.tvAppService2.setCompoundDrawablePadding(10);
                        }
                        else if(appointServices.get(1).getVirtualCallingModes().get(0).getCallingMode().equalsIgnoreCase("phone")){
                            holder.tvAppService2.setCompoundDrawablesWithIntrinsicBounds(R.drawable.phoneiconsized_small,0,0,0);
                            holder.tvAppService2.setCompoundDrawablePadding(10);
                        }
                    }
                    }
                    catch(Exception e){
                        e.printStackTrace();
                    }
                    holder.tvAppService1.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            appServInfoDialog = new AppointmentServiceInfoDialog(v.getContext(), appointServices.get(0));
                            appServInfoDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                            appServInfoDialog.show();
                            DisplayMetrics metrics = v.getContext().getResources().getDisplayMetrics();
                            int width = (int) (metrics.widthPixels * 1);
                            appServInfoDialog.getWindow().setLayout(width, LinearLayout.LayoutParams.WRAP_CONTENT);

                        }
                    });

                    holder.tvAppService2.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            appServInfoDialog = new AppointmentServiceInfoDialog(v.getContext(), appointServices.get(1));
                            appServInfoDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                            appServInfoDialog.show();
                            DisplayMetrics metrics = v.getContext().getResources().getDisplayMetrics();
                            int width = (int) (metrics.widthPixels * 1);
                            appServInfoDialog.getWindow().setLayout(width, LinearLayout.LayoutParams.WRAP_CONTENT);

                        }
                    });

                } else {
                    holder.tvAppService1.setVisibility(View.VISIBLE);
                    holder.tvAppService2.setVisibility(View.VISIBLE);
                    holder.tvAppSeeAll.setVisibility(View.VISIBLE);
                    String name1 = appointServices.get(0).getName();
                    name1 = name1.substring(0, 1).toUpperCase() + name1.substring(1).toLowerCase();
                    holder.tvAppService1.setText(name1 + ",");
                    try{
                    if(appointServices.get(1).getServiceType().equalsIgnoreCase("virtualservice")){
                        if(appointServices.get(1).getVirtualCallingModes().get(0).getCallingMode().equalsIgnoreCase("Zoom")){
                            holder.tvAppService1.setCompoundDrawablesWithIntrinsicBounds(R.drawable.zoomicon_sized,0,0,0);
                            holder.tvAppService1.setCompoundDrawablePadding(10);
                        }
                        else if(appointServices.get(0).getVirtualCallingModes().get(0).getCallingMode().equalsIgnoreCase("GoogleMeet")){
                            holder.tvAppService1.setCompoundDrawablesWithIntrinsicBounds(R.drawable.googlemeet_sized,0,0,0);
                            holder.tvAppService1.setCompoundDrawablePadding(10);
                        }
                        else if(appointServices.get(0).getVirtualCallingModes().get(0).getCallingMode().equalsIgnoreCase("WhatsApp")){
                            holder.tvAppService1.setCompoundDrawablesWithIntrinsicBounds(R.drawable.whatsappicon_sized,0,0,0);
                            holder.tvAppService1.setCompoundDrawablePadding(10);
                        }
                        else if(appointServices.get(0).getVirtualCallingModes().get(0).getCallingMode().equalsIgnoreCase("phone")){
                            holder.tvAppService1.setCompoundDrawablesWithIntrinsicBounds(R.drawable.phoneiconsized_small,0,0,0);
                            holder.tvAppService1.setCompoundDrawablePadding(10);
                        }
                    }
                    }
                    catch(Exception e){
                        e.printStackTrace();
                    }
                    String name2 = appointServices.get(1).getName();
                    name2 = name2.substring(0, 1).toUpperCase() + name2.substring(1).toLowerCase();
                    holder.tvAppService2.setText(name2 + ",");
                    try{
                    if(appointServices.get(1).getServiceType().equalsIgnoreCase("virtualservice")){
                        if(appointServices.get(1).getVirtualCallingModes().get(0).getCallingMode().equalsIgnoreCase("Zoom")){
                            holder.tvAppService2.setCompoundDrawablesWithIntrinsicBounds(R.drawable.zoomicon_sized,0,0,0);
                            holder.tvAppService2.setCompoundDrawablePadding(10);
                        }
                        else if(appointServices.get(1).getVirtualCallingModes().get(0).getCallingMode().equalsIgnoreCase("GoogleMeet")){
                            holder.tvAppService2.setCompoundDrawablesWithIntrinsicBounds(R.drawable.googlemeet_sized,0,0,0);
                            holder.tvAppService2.setCompoundDrawablePadding(10);
                        }
                        else if(appointServices.get(1).getVirtualCallingModes().get(0).getCallingMode().equalsIgnoreCase("WhatsApp")){
                            holder.tvAppService2.setCompoundDrawablesWithIntrinsicBounds(R.drawable.whatsappicon_sized,0,0,0);
                            holder.tvAppService2.setCompoundDrawablePadding(10);
                        }
                        else if(appointServices.get(1).getVirtualCallingModes().get(0).getCallingMode().equalsIgnoreCase("phone")){
                            holder.tvAppService2.setCompoundDrawablesWithIntrinsicBounds(R.drawable.phoneiconsized_small,0,0,0);
                            holder.tvAppService2.setCompoundDrawablePadding(10);
                        }
                    }
                    }
                    catch(Exception e){
                        e.printStackTrace();
                    }
                    holder.tvAppService1.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            appServInfoDialog = new AppointmentServiceInfoDialog(v.getContext(), appointServices.get(0));
                            appServInfoDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                            appServInfoDialog.show();
                            DisplayMetrics metrics = v.getContext().getResources().getDisplayMetrics();
                            int width = (int) (metrics.widthPixels * 1);
                            appServInfoDialog.getWindow().setLayout(width, LinearLayout.LayoutParams.WRAP_CONTENT);
                        }
                    });

                    holder.tvAppService2.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            appServInfoDialog = new AppointmentServiceInfoDialog(v.getContext(), appointServices.get(1));
                            appServInfoDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                            appServInfoDialog.show();
                            DisplayMetrics metrics = v.getContext().getResources().getDisplayMetrics();
                            int width = (int) (metrics.widthPixels * 1);
                            appServInfoDialog.getWindow().setLayout(width, LinearLayout.LayoutParams.WRAP_CONTENT);
                        }
                    });
                    holder.tvAppSeeAll.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            tempList.clear();
                            tempList.addAll(appointServices);
                            adaptercallback.onMethodServiceCallbackAppointment(tempList, userDetails.get(position).getSearchViewDetail().getBusinessName(), mSearchDepartmentList);
                        }
                    });
                }
            } else {

                for (int i = 0; i < appointServices.size(); i++) {

                    if (i == 0) {
                        holder.tvAppService1.setVisibility(View.VISIBLE);
                        holder.tvAppService2.setVisibility(View.GONE);
                        String name1 = appointServices.get(0).getName();
                        name1 = name1.substring(0, 1).toUpperCase() + name1.substring(1).toLowerCase();
                        holder.tvAppService1.setText(name1 + ",");
                        try{
                        if(appointServices.get(0).getServiceType().equalsIgnoreCase("virtualservice")){
                            if(appointServices.get(0).getVirtualCallingModes().get(0).getCallingMode().equalsIgnoreCase("Zoom")){
                                holder.tvAppService1.setCompoundDrawablesWithIntrinsicBounds(R.drawable.zoomicon_sized,0,0,0);
                                holder.tvAppService1.setCompoundDrawablePadding(10);
                            }
                            else if(appointServices.get(0).getVirtualCallingModes().get(0).getCallingMode().equalsIgnoreCase("GoogleMeet")){
                                holder.tvAppService1.setCompoundDrawablesWithIntrinsicBounds(R.drawable.googlemeet_sized,0,0,0);
                                holder.tvAppService1.setCompoundDrawablePadding(10);
                            }
                            else if(appointServices.get(0).getVirtualCallingModes().get(0).getCallingMode().equalsIgnoreCase("WhatsApp")){
                                holder.tvAppService1.setCompoundDrawablesWithIntrinsicBounds(R.drawable.whatsappicon_sized,0,0,0);
                                holder.tvAppService1.setCompoundDrawablePadding(10);
                            }
                            else if(appointServices.get(0).getVirtualCallingModes().get(0).getCallingMode().equalsIgnoreCase("phone")){
                                holder.tvAppService1.setCompoundDrawablesWithIntrinsicBounds(R.drawable.phoneiconsized_small,0,0,0);
                                holder.tvAppService1.setCompoundDrawablePadding(10);
                            }
                        }
                        }
                        catch(Exception e){
                            e.printStackTrace();
                        }
                        holder.tvAppService1.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                appServInfoDialog = new AppointmentServiceInfoDialog(v.getContext(), appointServices.get(0));
                                appServInfoDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                                appServInfoDialog.show();
                                DisplayMetrics metrics = v.getContext().getResources().getDisplayMetrics();
                                int width = (int) (metrics.widthPixels * 1);
                                appServInfoDialog.getWindow().setLayout(width, LinearLayout.LayoutParams.WRAP_CONTENT);
                            }
                        });
                        holder.tvAppSeeAll.setVisibility(View.VISIBLE);
                        holder.tvAppSeeAll.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                tempList.clear();
                                tempList.addAll(appointServices);
                                adaptercallback.onMethodServiceCallbackAppointment(tempList, userDetails.get(position).getSearchViewDetail().getBusinessName(), mSearchDepartmentList);
                            }
                        });
                        Toast.makeText(context, "set single line and see more", Toast.LENGTH_SHORT).show();
                        break;
                    }

                }

            }
        } else {

            holder.txt_apptservices.setVisibility(View.GONE);
            holder.LApp_Services.setVisibility(View.GONE);
        }

//        for (int j = 0; j < appointServices.size(); j++) {
//            if (j < 2) {
//                TextView dynaText = new TextView(context);
//                tyface = Typeface.createFromAsset(context.getAssets(),
//                        "fonts/Montserrat_Regular.otf");
//                dynaText.setTypeface(tyface, Typeface.BOLD);
//                dynaText.setText(appointServices.get(j).getName());
//                dynaText.setTextSize(13);
//                dynaText.setPadding(5, 0, 5, 0);
//                dynaText.setTextColor(context.getResources().getColor(R.color.black));
//                dynaText.setMaxLines(1);
//                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
//                params.setMargins(0, 0, 20, 0);
//                dynaText.setLayoutParams(params);
//
//                holder.LApp_Services.addView(dynaText);
//            } else if (j >= 2) {
//
//                holder.txtapptSeeAll.setVisibility(View.VISIBLE);
//                holder.txtapptSeeAll.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//
//                        apptList.clear();
//                        tempList.addAll(appointServices);
//                        if (tempList != null) {
//
//                            adaptercallback.onMethodServiceCallbackAppointment(tempList, userDetails.get(position).getSearchViewDetail().getBusinessName(), mSearchDepartmentList);
//                        }
//                    }
//                });
//                break;
//
//            }
//        }
    }

    private void setUserAppointmentServices(MyViewHolder holder, int position) {
        apptServices = userDetails.get(position).getUserAppointmentServices();

        apptServices = apptServices == null ? new ArrayList<SearchService>() : apptServices;
        if (apptServices.size() > 0) {

            holder.LApp_Services.setVisibility(View.VISIBLE);
            holder.txt_apptservices.setVisibility(View.VISIBLE);
            if (apptServices.size() == 1) {

                holder.tvAppService1.setVisibility(View.VISIBLE);
                holder.tvAppService2.setVisibility(View.GONE);
                holder.tvAppSeeAll.setVisibility(View.GONE);
                String name = apptServices.get(0).getName();
                name = name.substring(0, 1).toUpperCase() + name.substring(1).toLowerCase();
                holder.tvAppService1.setText(name);
                try{
                if(apptServices.get(0).getServiceType().equalsIgnoreCase("virtualservice")){
                    if(apptServices.get(0).getVirtualCallingModes().get(0).getCallingMode().equalsIgnoreCase("Zoom")){
                        holder.tvAppService1.setCompoundDrawablesWithIntrinsicBounds(R.drawable.zoomicon_sized,0,0,0);
                        holder.tvAppService1.setCompoundDrawablePadding(10);
                    }
                    else if(apptServices.get(0).getVirtualCallingModes().get(0).getCallingMode().equalsIgnoreCase("GoogleMeet")){
                        holder.tvAppService1.setCompoundDrawablesWithIntrinsicBounds(R.drawable.googlemeet_sized,0,0,0);
                        holder.tvAppService1.setCompoundDrawablePadding(10);
                    }
                    else if(apptServices.get(0).getVirtualCallingModes().get(0).getCallingMode().equalsIgnoreCase("WhatsApp")){
                        holder.tvAppService1.setCompoundDrawablesWithIntrinsicBounds(R.drawable.whatsappicon_sized,0,0,0);
                        holder.tvAppService1.setCompoundDrawablePadding(10);
                    }
                    else if(apptServices.get(0).getVirtualCallingModes().get(0).getCallingMode().equalsIgnoreCase("phone")){
                        holder.tvAppService1.setCompoundDrawablesWithIntrinsicBounds(R.drawable.phoneiconsized_small,0,0,0);
                        holder.tvAppService1.setCompoundDrawablePadding(10);
                    }
                }
                }
                catch(Exception e){
                    e.printStackTrace();
                }
                holder.tvAppService1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        userAppServicesDialog = new UserAppServicesDialog(v.getContext(), apptServices.get(0));
                        userAppServicesDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                        userAppServicesDialog.show();
                        DisplayMetrics metrics = v.getContext().getResources().getDisplayMetrics();
                        int width = (int) (metrics.widthPixels * 1);
                        userAppServicesDialog.getWindow().setLayout(width, LinearLayout.LayoutParams.WRAP_CONTENT);

                    }
                });


            } else if (apptServices.size() >= 2 && apptServices.get(0).getName().length() <= 20 && apptServices.get(1).getName().length() <= 20) {

                if (apptServices.size() == 2) {
                    holder.tvAppService1.setVisibility(View.VISIBLE);
                    holder.tvAppService2.setVisibility(View.VISIBLE);
                    holder.tvAppSeeAll.setVisibility(View.GONE);
                    String name1 = apptServices.get(0).getName();
                    name1 = name1.substring(0, 1).toUpperCase() + name1.substring(1).toLowerCase();
                    holder.tvAppService1.setText(name1 + ",");
                    try{
                    if(apptServices.get(0).getServiceType().equalsIgnoreCase("virtualservice")){
                        if(apptServices.get(0).getVirtualCallingModes().get(0).getCallingMode().equalsIgnoreCase("Zoom")){
                            holder.tvAppService1.setCompoundDrawablesWithIntrinsicBounds(R.drawable.zoomicon_sized,0,0,0);
                            holder.tvAppService1.setCompoundDrawablePadding(10);
                        }
                        else if(apptServices.get(0).getVirtualCallingModes().get(0).getCallingMode().equalsIgnoreCase("GoogleMeet")){
                            holder.tvAppService1.setCompoundDrawablesWithIntrinsicBounds(R.drawable.googlemeet_sized,0,0,0);
                            holder.tvAppService1.setCompoundDrawablePadding(10);
                        }
                        else if(apptServices.get(0).getVirtualCallingModes().get(0).getCallingMode().equalsIgnoreCase("WhatsApp")){
                            holder.tvAppService1.setCompoundDrawablesWithIntrinsicBounds(R.drawable.whatsappicon_sized,0,0,0);
                            holder.tvAppService1.setCompoundDrawablePadding(10);
                        }
                        else if(apptServices.get(0).getVirtualCallingModes().get(0).getCallingMode().equalsIgnoreCase("phone")){
                            holder.tvAppService1.setCompoundDrawablesWithIntrinsicBounds(R.drawable.phoneiconsized_small,0,0,0);
                            holder.tvAppService1.setCompoundDrawablePadding(10);
                        }
                    }
                    }
                    catch(Exception e){
                        e.printStackTrace();
                    }
                    String name2 = apptServices.get(1).getName();
                    name2 = name2.substring(0, 1).toUpperCase() + name2.substring(1).toLowerCase();
                    holder.tvAppService2.setText(name2);
                    try{
                    if(apptServices.get(1).getServiceType().equalsIgnoreCase("virtualservice")){
                        if(apptServices.get(1).getVirtualCallingModes().get(0).getCallingMode().equalsIgnoreCase("Zoom")){
                            holder.tvAppService2.setCompoundDrawablesWithIntrinsicBounds(R.drawable.zoomicon_sized,0,0,0);
                            holder.tvAppService2.setCompoundDrawablePadding(10);
                        }
                        else if(apptServices.get(1).getVirtualCallingModes().get(0).getCallingMode().equalsIgnoreCase("GoogleMeet")){
                            holder.tvAppService2.setCompoundDrawablesWithIntrinsicBounds(R.drawable.googlemeet_sized,0,0,0);
                            holder.tvAppService2.setCompoundDrawablePadding(10);
                        }
                        else if(apptServices.get(1).getVirtualCallingModes().get(0).getCallingMode().equalsIgnoreCase("WhatsApp")){
                            holder.tvAppService2.setCompoundDrawablesWithIntrinsicBounds(R.drawable.whatsappicon_sized,0,0,0);
                            holder.tvAppService2.setCompoundDrawablePadding(10);
                        }
                        else if(apptServices.get(1).getVirtualCallingModes().get(0).getCallingMode().equalsIgnoreCase("phone")){
                            holder.tvAppService2.setCompoundDrawablesWithIntrinsicBounds(R.drawable.phoneiconsized_small,0,0,0);
                            holder.tvAppService2.setCompoundDrawablePadding(10);
                        }
                    }
                    }
                    catch(Exception e){
                        e.printStackTrace();
                    }
                    holder.tvAppService1.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            userAppServicesDialog = new UserAppServicesDialog(v.getContext(), apptServices.get(0));
                            userAppServicesDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                            userAppServicesDialog.show();
                            DisplayMetrics metrics = v.getContext().getResources().getDisplayMetrics();
                            int width = (int) (metrics.widthPixels * 1);
                            userAppServicesDialog.getWindow().setLayout(width, LinearLayout.LayoutParams.WRAP_CONTENT);

                        }
                    });

                    holder.tvAppService2.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            userAppServicesDialog = new UserAppServicesDialog(v.getContext(), apptServices.get(1));
                            userAppServicesDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                            userAppServicesDialog.show();
                            DisplayMetrics metrics = v.getContext().getResources().getDisplayMetrics();
                            int width = (int) (metrics.widthPixels * 1);
                            userAppServicesDialog.getWindow().setLayout(width, LinearLayout.LayoutParams.WRAP_CONTENT);

                        }
                    });

                } else {
                    holder.tvAppService1.setVisibility(View.VISIBLE);
                    holder.tvAppService2.setVisibility(View.VISIBLE);
                    holder.tvAppSeeAll.setVisibility(View.VISIBLE);
                    String name1 = apptServices.get(0).getName();
                    name1 = name1.substring(0, 1).toUpperCase() + name1.substring(1).toLowerCase();
                    holder.tvAppService1.setText(name1 + ",");
                    try{
                    if(apptServices.get(0).getServiceType().equalsIgnoreCase("virtualservice")){
                        if(apptServices.get(0).getVirtualCallingModes().get(0).getCallingMode().equalsIgnoreCase("Zoom")){
                            holder.tvAppService1.setCompoundDrawablesWithIntrinsicBounds(R.drawable.zoomicon_sized,0,0,0);
                            holder.tvAppService1.setCompoundDrawablePadding(10);
                        }
                        else if(apptServices.get(0).getVirtualCallingModes().get(0).getCallingMode().equalsIgnoreCase("GoogleMeet")){
                            holder.tvAppService1.setCompoundDrawablesWithIntrinsicBounds(R.drawable.googlemeet_sized,0,0,0);
                            holder.tvAppService1.setCompoundDrawablePadding(10);
                        }
                        else if(apptServices.get(0).getVirtualCallingModes().get(0).getCallingMode().equalsIgnoreCase("WhatsApp")){
                            holder.tvAppService1.setCompoundDrawablesWithIntrinsicBounds(R.drawable.whatsappicon_sized,0,0,0);
                            holder.tvAppService1.setCompoundDrawablePadding(10);
                        }
                        else if(apptServices.get(0).getVirtualCallingModes().get(0).getCallingMode().equalsIgnoreCase("phone")){
                            holder.tvAppService1.setCompoundDrawablesWithIntrinsicBounds(R.drawable.phoneiconsized_small,0,0,0);
                            holder.tvAppService1.setCompoundDrawablePadding(10);
                        }
                    }
                    }
                    catch(Exception e){
                        e.printStackTrace();
                    }
                    String name2 = apptServices.get(1).getName();
                    name2 = name2.substring(0, 1).toUpperCase() + name2.substring(1).toLowerCase();
                    holder.tvAppService2.setText(name2 + ",");
                    try{
                    if(apptServices.get(1).getServiceType().equalsIgnoreCase("virtualservice")){
                        if(apptServices.get(1).getVirtualCallingModes().get(0).getCallingMode().equalsIgnoreCase("Zoom")){
                            holder.tvAppService2.setCompoundDrawablesWithIntrinsicBounds(R.drawable.zoomicon_sized,0,0,0);
                            holder.tvAppService2.setCompoundDrawablePadding(10);
                        }
                        else if(apptServices.get(1).getVirtualCallingModes().get(0).getCallingMode().equalsIgnoreCase("GoogleMeet")){
                            holder.tvAppService2.setCompoundDrawablesWithIntrinsicBounds(R.drawable.googlemeet_sized,0,0,0);
                            holder.tvAppService2.setCompoundDrawablePadding(10);
                        }
                        else if(apptServices.get(1).getVirtualCallingModes().get(0).getCallingMode().equalsIgnoreCase("WhatsApp")){
                            holder.tvAppService2.setCompoundDrawablesWithIntrinsicBounds(R.drawable.whatsappicon_sized,0,0,0);
                            holder.tvAppService2.setCompoundDrawablePadding(10);
                        }
                        else if(apptServices.get(1).getVirtualCallingModes().get(0).getCallingMode().equalsIgnoreCase("phone")){
                            holder.tvAppService2.setCompoundDrawablesWithIntrinsicBounds(R.drawable.phoneiconsized_small,0,0,0);
                            holder.tvAppService2.setCompoundDrawablePadding(10);
                        }
                    }
                    }
                    catch(Exception e){
                        e.printStackTrace();
                    }
                    holder.tvAppService1.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            userAppServicesDialog = new UserAppServicesDialog(v.getContext(), apptServices.get(0));
                            userAppServicesDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                            userAppServicesDialog.show();
                            DisplayMetrics metrics = v.getContext().getResources().getDisplayMetrics();
                            int width = (int) (metrics.widthPixels * 1);
                            userAppServicesDialog.getWindow().setLayout(width, LinearLayout.LayoutParams.WRAP_CONTENT);

                        }
                    });

                    holder.tvAppService2.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            userAppServicesDialog = new UserAppServicesDialog(v.getContext(), apptServices.get(1));
                            userAppServicesDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                            userAppServicesDialog.show();
                            DisplayMetrics metrics = v.getContext().getResources().getDisplayMetrics();
                            int width = (int) (metrics.widthPixels * 1);
                            userAppServicesDialog.getWindow().setLayout(width, LinearLayout.LayoutParams.WRAP_CONTENT);

                        }
                    });
                    holder.tvAppSeeAll.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            if (apptServices != null) {

                                adaptercallback.onUserAppointmentServices(apptServices, userDetails.get(position).getSearchViewDetail().getBusinessName(), mSearchDepartmentList);
                            }
                        }
                    });
                }
            } else {

                for (int i = 0; i < apptServices.size(); i++) {

                    if (i == 0) {
                        holder.tvAppService1.setVisibility(View.VISIBLE);
                        holder.tvAppService2.setVisibility(View.GONE);
                        String name1 = apptServices.get(0).getName();
                        name1 = name1.substring(0, 1).toUpperCase() + name1.substring(1).toLowerCase();
                        holder.tvAppService1.setText(name1 + ",");
                        try{
                        if(apptServices.get(0).getServiceType().equalsIgnoreCase("virtualservice")){
                            if(apptServices.get(0).getVirtualCallingModes().get(0).getCallingMode().equalsIgnoreCase("Zoom")){
                                holder.tvAppService1.setCompoundDrawablesWithIntrinsicBounds(R.drawable.zoomicon_sized,0,0,0);
                                holder.tvAppService1.setCompoundDrawablePadding(10);
                            }
                            else if(apptServices.get(0).getVirtualCallingModes().get(0).getCallingMode().equalsIgnoreCase("GoogleMeet")){
                                holder.tvAppService1.setCompoundDrawablesWithIntrinsicBounds(R.drawable.googlemeet_sized,0,0,0);
                                holder.tvAppService1.setCompoundDrawablePadding(10);
                            }
                            else if(apptServices.get(0).getVirtualCallingModes().get(0).getCallingMode().equalsIgnoreCase("WhatsApp")){
                                holder.tvAppService1.setCompoundDrawablesWithIntrinsicBounds(R.drawable.whatsappicon_sized,0,0,0);
                                holder.tvAppService1.setCompoundDrawablePadding(10);
                            }
                            else if(apptServices.get(0).getVirtualCallingModes().get(0).getCallingMode().equalsIgnoreCase("phone")){
                                holder.tvAppService1.setCompoundDrawablesWithIntrinsicBounds(R.drawable.phoneiconsized_small,0,0,0);
                                holder.tvAppService1.setCompoundDrawablePadding(10);
                            }
                        }
                        }
                        catch(Exception e){
                            e.printStackTrace();
                        }
                        holder.tvAppService1.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                userAppServicesDialog = new UserAppServicesDialog(v.getContext(), apptServices.get(0));
                                userAppServicesDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                                userAppServicesDialog.show();
                                DisplayMetrics metrics = v.getContext().getResources().getDisplayMetrics();
                                int width = (int) (metrics.widthPixels * 1);
                                userAppServicesDialog.getWindow().setLayout(width, LinearLayout.LayoutParams.WRAP_CONTENT);

                            }
                        });
                        holder.tvAppSeeAll.setVisibility(View.VISIBLE);
                        holder.tvAppSeeAll.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                if (apptServices != null) {
                                    adaptercallback.onUserAppointmentServices(apptServices, userDetails.get(position).getSearchViewDetail().getBusinessName(), mSearchDepartmentList);
                                }
                            }
                        });
                        Toast.makeText(context, "set single line and see more", Toast.LENGTH_SHORT).show();
                        break;
                    }
                }
            }
        } else {

            holder.txt_apptservices.setVisibility(View.GONE);
            holder.LApp_Services.setVisibility(View.GONE);
        }
//        for (int j = 0; j < apptServices.size(); j++) {
//            if (j < 2) {
//                TextView dynaText = new TextView(context);
//                tyface = Typeface.createFromAsset(context.getAssets(),
//                        "fonts/Montserrat_Regular.otf");
//                dynaText.setTypeface(tyface, Typeface.BOLD);
//                dynaText.setText(apptServices.get(j).getName());
//                dynaText.setTextSize(13);
//                dynaText.setPadding(5, 0, 5, 0);
//                dynaText.setTextColor(context.getResources().getColor(R.color.black));
//                dynaText.setMaxLines(1);
//                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
//                params.setMargins(0, 0, 20, 0);
//                dynaText.setLayoutParams(params);
//
//                holder.LApp_Services.addView(dynaText);
//            } else if (j >= 2) {
//
//                holder.txtapptSeeAll.setVisibility(View.VISIBLE);
//                holder.txtapptSeeAll.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//
//                        if (apptServices != null) {
//
//                            adaptercallback.onUserAppointmentServices(apptServices, userDetails.get(position).getSearchViewDetail().getBusinessName(), mSearchDepartmentList);
//                        }
//                    }
//                });
//                break;
//
//            }
//        }
    }

    @Override
    public int getItemCount() {
        return userDetails.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView tv_place, tv_working, tv_open, tv_waittime, txt_diffdate, txt_msg, txt_peopleahead;
        Button btn_checkin;
        LinearLayout mLSeriveLayout, mLayouthide, LexpandCheckin, Ldirectionlayout, LService_2, LWorkinHrs, LDepartment_2, LAppointment, LApp_Services, LDonation, LDont_Services;
        ImageView img_arrow;
        RecyclerView recycle_parking;
        RelativeLayout layout_exapnd;
        TextView txtdirection, tv_checkin;
        Button btn_checkin_expand;
        TextView txtwaittime_expand, txt_diffdate_expand, txtlocation_amentites, txtparkingSeeAll, txtservices, txtdayofweek;
        TextView txtservice1, txtservice2, txtSeeAll, txtwork1, txtworkSeeAll, txtworking;
        TextView txt_earliestAvailable, txt_apptservices, txt_dontservices, txtapptSeeAll, txtdntSeeAll;
        Button btn_appointments, btn_donations;
        private RelativeLayout rlAppServices, rlLocationLayout;
        TextView tvAppService1, tvAppService2, tvAppSeeAll, tvAvailDate;
        TextView tvDntService1, tvDntService2, tvDntSeeAll;


        ArrayList<WorkingModel> workingModelArrayList = new ArrayList<>();
        String txtdataMon = "";
        String txtdataTue = "";
        String txtdataWed = "";
        String txtdataThu = "";
        String txtdataFri = "";
        String txtdataSat = "";
        String txtdataSun = "";

        public MyViewHolder(View view) {
            super(view);
            tv_checkin = (TextView) view.findViewById(R.id.txtcheckin);
            tv_place = (TextView) view.findViewById(R.id.txtLoc);
            tv_working = (TextView) view.findViewById(R.id.txtworking);
            btn_checkin = (Button) view.findViewById(R.id.btn_checkin);
            tv_open = (TextView) view.findViewById(R.id.txtopen);
            tv_waittime = (TextView) view.findViewById(R.id.txtwaittime);
            mLSeriveLayout = (LinearLayout) view.findViewById(R.id.lServicelayout);
            img_arrow = (ImageView) view.findViewById(R.id.img_arrow);
            mLayouthide = (LinearLayout) view.findViewById(R.id.mLayouthide);
            recycle_parking = (RecyclerView) view.findViewById(R.id.recycle_parking);
            txt_diffdate = (TextView) view.findViewById(R.id.txt_diffdate);
            layout_exapnd = (RelativeLayout) view.findViewById(R.id.layout_exapnd);
            txtdirection = (TextView) view.findViewById(R.id.txtdirection);
            txtwaittime_expand = (TextView) view.findViewById(R.id.txtwaittime_expand);
            txt_diffdate_expand = (TextView) view.findViewById(R.id.txt_diffdate_expand);
            btn_checkin_expand = (Button) view.findViewById(R.id.btn_checkin_expand);
            LexpandCheckin = (LinearLayout) view.findViewById(R.id.LexpandCheckin);
            Ldirectionlayout = (LinearLayout) view.findViewById(R.id.Ldirectionlayout);
            txtlocation_amentites = (TextView) view.findViewById(R.id.txtlocation_amentites);
            txtparkingSeeAll = (TextView) view.findViewById(R.id.txtparkingSeeAll);
            txtservices = (TextView) view.findViewById(R.id.txtservices);
            LService_2 = (LinearLayout) view.findViewById(R.id.LService_2);
            txtservice1 = (TextView) view.findViewById(R.id.txtservice1);
            txtservice2 = (TextView) view.findViewById(R.id.txtservice2);
            txtSeeAll = (TextView) view.findViewById(R.id.txtSeeAll);
            txtdayofweek = (TextView) view.findViewById(R.id.txtdayofweek);
            txtwork1 = (TextView) view.findViewById(R.id.txtwork1);
            txtworkSeeAll = (TextView) view.findViewById(R.id.txtworkSeeAll);
            LWorkinHrs = (LinearLayout) view.findViewById(R.id.LWorkinHrs);
            txtworking = (TextView) view.findViewById(R.id.txtworking);
            LDepartment_2 = (LinearLayout) view.findViewById(R.id.LDepartment_2);
            txt_msg = (TextView) view.findViewById(R.id.txt_msg);
            txt_peopleahead = (TextView) view.findViewById(R.id.txt_PeopleAhead);
            LAppointment = view.findViewById(R.id.appoinmentLayouts);
            btn_appointments = view.findViewById(R.id.btnappointments);
            LApp_Services = view.findViewById(R.id.appointmentList);
            txt_apptservices = view.findViewById(R.id.txtapptservices);
            LDonation = view.findViewById(R.id.donationLayouts);
            btn_donations = view.findViewById(R.id.btndonations);
            LDont_Services = view.findViewById(R.id.donationList);
            txt_dontservices = view.findViewById(R.id.txtdontservices);
            txtapptSeeAll = view.findViewById(R.id.txtapptSeeAll);
            txtdntSeeAll = view.findViewById(R.id.txtdntSeeAll);
            rlAppServices = view.findViewById(R.id.rlAppServices);
            rlLocationLayout = view.findViewById(R.id.locationlayout);
            tvAppService1 = view.findViewById(R.id.tv_appService1);
            tvAppService2 = view.findViewById(R.id.tv_appservice2);
            tvAppSeeAll = view.findViewById(R.id.tv_appSeeAll);
        }
    }

    private void handleLocationAmenities(final MyViewHolder myViewHolder, final DepartmentUserSearchModel userSearchModel) {
        if (userSearchModel.getLocation().getParkingType() != null) {
            if (userSearchModel.getLocation().getParkingType().equalsIgnoreCase("free") || userSearchModel.getLocation().getParkingType().equalsIgnoreCase("valet") || userSearchModel.getLocation().getParkingType().equalsIgnoreCase("street") || userSearchModel.getLocation().getParkingType().equalsIgnoreCase("privatelot") || userSearchModel.getLocation().getParkingType().equalsIgnoreCase("paid")) {
                ParkingModel mType = new ParkingModel();
                mType.setId("1");
                mType.setTypename(Config.toTitleCase(userSearchModel.getLocation().getParkingType()) + " Parking ");
                listType.add(mType);
            }
        }
        if (userSearchModel.getLocation().getLocationVirtualFields() != null) {
            if (userSearchModel.getLocation().getLocationVirtualFields().getDocambulance() != null) {
                if (userSearchModel.getLocation().getLocationVirtualFields().getDocambulance().equalsIgnoreCase("true")) {
                    ParkingModel mType = new ParkingModel();
                    mType.setId("4");
                    mType.setTypename("Ambulance");
                    listType.add(mType);
                }
            }
            if (userSearchModel.getLocation().getLocationVirtualFields().getFirstaid() != null) {
                if (userSearchModel.getLocation().getLocationVirtualFields().getFirstaid().equalsIgnoreCase("true")) {
                    ParkingModel mType = new ParkingModel();
                    mType.setId("5");
                    mType.setTypename("First Aid");
                    listType.add(mType);
                }
            }
            if (userSearchModel.getLocation().getLocationVirtualFields().getTraumacentre() != null) {
                if (userSearchModel.getLocation().getLocationVirtualFields().getTraumacentre().equalsIgnoreCase("true")) {
                    ParkingModel mType = new ParkingModel();
                    mType.setId("7");
                    mType.setTypename("Trauma");
                    listType.add(mType);
                }
            }
            if (userSearchModel.getLocation().getLocationVirtualFields().getPhysiciansemergencyservices() != null) {
                if (userSearchModel.getLocation().getLocationVirtualFields().getPhysiciansemergencyservices().equalsIgnoreCase("true")) {
                    ParkingModel mType = new ParkingModel();
                    mType.setId("3");
                    mType.setTypename("Emergency");
                    listType.add(mType);
                }
            }
            if (userSearchModel.getLocation().getLocationVirtualFields().getHosemergencyservices() != null) {
                if (userSearchModel.getLocation().getLocationVirtualFields().getHosemergencyservices().equalsIgnoreCase("true")) {
                    ParkingModel mType = new ParkingModel();
                    mType.setId("3");
                    mType.setTypename("Emergency");
                    listType.add(mType);
                }
            }
            if (userSearchModel.getLocation().getLocationVirtualFields().getDentistemergencyservices() != null) {
                if (userSearchModel.getLocation().getLocationVirtualFields().getDentistemergencyservices().equalsIgnoreCase("true")) {
                    ParkingModel mType = new ParkingModel();
                    mType.setId("6");
                    mType.setTypename("Emergency");
                    listType.add(mType);
                }
            }
        }
        try {
            if (userSearchModel.getLocation().isOpen24hours()) {
                ParkingModel mType = new ParkingModel();
                mType.setId("2");
                mType.setTypename("24 Hours");
                listType.add(mType);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (listType.size() > 0) {
            myViewHolder.txtlocation_amentites.setVisibility(View.VISIBLE);
            myViewHolder.txtlocation_amentites.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

//                    adaptercallback.onMethodServiceCallback(listType, mTitle, mSearchDepartmentList);

                    LocationAmenitiesDialog locationAmenitiesDialog = new LocationAmenitiesDialog(context, listType);
                    locationAmenitiesDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    locationAmenitiesDialog.show();
                    DisplayMetrics metrics = context.getResources().getDisplayMetrics();
                    int width = (int) (metrics.widthPixels * 1);
                    locationAmenitiesDialog.getWindow().setLayout(width, LinearLayout.LayoutParams.WRAP_CONTENT);

                }
            });

        } else {
            myViewHolder.txtparkingSeeAll.setVisibility(View.GONE);
            myViewHolder.txtlocation_amentites.setVisibility(View.GONE);
            myViewHolder.recycle_parking.setVisibility(View.GONE);
        }

        myViewHolder.txtparkingSeeAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!userSearchModel.getLocation().isLocationAmentOpen()) {
                    ParkingTypesAdapter mParkTypeAdapter = new ParkingTypesAdapter(listType, context, listType.size());
                    LinearLayoutManager horizontalLayoutManager = new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false);
                    myViewHolder.recycle_parking.setLayoutManager(horizontalLayoutManager);
                    myViewHolder.recycle_parking.setAdapter(mParkTypeAdapter);
                    myViewHolder.txtparkingSeeAll.setText("See Less");
                    userSearchModel.getLocation().setLocationAmentOpen(true);
                } else {
                    int size = listType.size();
                    if (size == 1) {
                        size = 1;
                    } else {
                        size = 2;
                    }
                    ParkingTypesAdapter mParkTypeAdapter = new ParkingTypesAdapter(listType, context, size);
                    LinearLayoutManager horizontalLayoutManager = new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false);
                    myViewHolder.recycle_parking.setLayoutManager(horizontalLayoutManager);
                    myViewHolder.recycle_parking.setAdapter(mParkTypeAdapter);
                    myViewHolder.txtparkingSeeAll.setText("See All");
                    userSearchModel.getLocation().setLocationAmentOpen(false);
                }
            }
        });
    }


    public void disableCheckinFeature(MyViewHolder myViewHolder) {
        disableCheckinButton(myViewHolder);
        myViewHolder.btn_checkin.setVisibility(View.GONE);
        myViewHolder.btn_checkin_expand.setVisibility(View.GONE);
        myViewHolder.LService_2.setVisibility(View.VISIBLE);
        myViewHolder.txtservices.setVisibility(View.VISIBLE);
    }

    public void disableCheckinButton(MyViewHolder myViewHolder) {
        myViewHolder.btn_checkin.setBackgroundColor(Color.parseColor("#cfcfcf"));
        myViewHolder.btn_checkin_expand.setBackgroundColor(Color.parseColor("#cfcfcf"));
        myViewHolder.btn_checkin.setTextColor(context.getResources().getColor(R.color.button_grey));
        myViewHolder.btn_checkin_expand.setTextColor(context.getResources().getColor(R.color.button_grey));
        myViewHolder.btn_checkin.setEnabled(false);
        myViewHolder.btn_checkin_expand.setEnabled(false);
        myViewHolder.btn_checkin.setVisibility(View.VISIBLE);
        myViewHolder.btn_checkin_expand.setVisibility(View.VISIBLE);
    }

    public void enableCheckinButton(MyViewHolder myViewHolder) {
        myViewHolder.btn_checkin.setBackgroundColor(context.getResources().getColor(R.color.dark_blue));
        myViewHolder.btn_checkin.setTextColor(context.getResources().getColor(R.color.white));
        myViewHolder.btn_checkin.setEnabled(true);
        myViewHolder.btn_checkin.setVisibility(View.VISIBLE);
        myViewHolder.btn_checkin_expand.setBackgroundColor(context.getResources().getColor(R.color.green));
        myViewHolder.btn_checkin_expand.setTextColor(context.getResources().getColor(R.color.white));
        myViewHolder.btn_checkin_expand.setEnabled(true);
        myViewHolder.btn_checkin_expand.setVisibility(View.VISIBLE);
        myViewHolder.LService_2.setVisibility(View.VISIBLE);
        myViewHolder.txtservices.setVisibility(View.VISIBLE);
    }


    public ArrayList<SearchService> SomeConstructor(List<SearchService> services) {
        servicesList.clear();
        servicesList.addAll(services);
        return servicesList;
    }

    public ArrayList<SearchAppointmentDepartmentServices> ApptConstructor(List<SearchService> services) {
        appServices.addAll(services);
        return apptList;
    }
}

