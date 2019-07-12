package com.jaldeeinc.jaldee.adapter;


import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jaldeeinc.jaldee.R;
import com.jaldeeinc.jaldee.callback.HistoryAdapterCallback;
import com.jaldeeinc.jaldee.common.Config;
import com.jaldeeinc.jaldee.custom.CustomTypefaceSpan;
import com.jaldeeinc.jaldee.response.ActiveCheckIn;
import com.jaldeeinc.jaldee.response.FavouriteModel;

/**
 * Created by sharmila on 14/1/19.
 */


public class ExpandableListAdapter extends BaseExpandableListAdapter {

    private Context mContext;
    private List<String> headerData; // header titles
    // Child data in format of header title, child title
    private HashMap<String, ArrayList<ActiveCheckIn>> child;
    Activity activity;
    HistoryAdapterCallback callback;
    String header;
    ArrayList<FavouriteModel> FavList;

    boolean mTodayFlag=false,mOldFlag=false,mFutureFlag=false;


    public ExpandableListAdapter(ArrayList<FavouriteModel> mFavList, Context mContext, Activity mActivity, HistoryAdapterCallback callback,List<String> listDataHeader, HashMap<String, ArrayList<ActiveCheckIn>> listChildData,boolean mTodayFlag,boolean mFutureFlag,boolean mOldFlag) {
        this.mContext = mContext;
        this.headerData = listDataHeader;
        this.child = listChildData;
        this.activity = mActivity;
        this.callback = callback;

        this.FavList = mFavList;
        this.mFutureFlag=mFutureFlag;
        this.mTodayFlag=mTodayFlag;
        this.mOldFlag=mOldFlag;
    }

    @Override
    public int getGroupCount() {
        // Get header size
        return this.headerData.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        // return children count
        return this.child.get(this.headerData.get(groupPosition)).size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        // Get header position
        return this.headerData.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        // This will return the child
        return this.child.get(this.headerData.get(groupPosition)).get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {

        // Getting header title
        String headerTitle = (String) getGroup(groupPosition);
        // header = headerTitle.toLowerCase();


        // Inflating header layout and setting text
        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this.mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.header, parent, false);
        }

        TextView txtnocheckold = (TextView) convertView.findViewById(R.id.txtnocheckold);

        Config.logV("No Child" + getChildrenCount(groupPosition));
        if (getChildrenCount(groupPosition) == 0) {
            if (groupPosition == 0) {
                txtnocheckold.setText("No Check-ins for today");
            }
            if (groupPosition == 1) {
                txtnocheckold.setText("No Future Check-ins");
            }
            if (groupPosition == 2) {
                txtnocheckold.setText("No Old Check-ins");
            }
            txtnocheckold.setVisibility(View.VISIBLE);
        } else {
            txtnocheckold.setVisibility(View.GONE);
        }

        TextView header_text = (TextView) convertView.findViewById(R.id.header);
        if (getChildrenCount(groupPosition) > 0) {
            header_text.setText(headerTitle + " ( " + getChildrenCount(groupPosition) + " )");
        } else {
            header_text.setText(headerTitle);
        }
        // If group is expanded then change the text into bold and change the
        // icon
        if (isExpanded) {
            Config.logV("Open@@@@"+groupPosition);
            if (groupPosition == 0) {
                mTodayFlag=true;
            }
            if (groupPosition == 1) {
                mFutureFlag=true;
            }
            if (groupPosition == 2) {
                mOldFlag=true;
            }
            //header_text.setTypeface(null, Typeface.BOLD);
            if (getChildrenCount(groupPosition) > 0) {
                header_text.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.icon_up_light, 0);
            }else{
                header_text.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
            }
            header_text.setBackground(mContext.getResources().getDrawable(R.drawable.input_border_top));
        } else {
            // If group is not expanded then change the text back into normal
            // and change the icon
            if (getChildrenCount(groupPosition) == 0) {
                Config.logV("Open@@@@ FFFF"+groupPosition);
                if (groupPosition == 0) {
                    header_text.setCompoundDrawablesWithIntrinsicBounds(0, 0,0, 0);
                    header_text.setBackground(mContext.getResources().getDrawable(R.drawable.input_border_top));
                    mTodayFlag=true;
                }
                if (groupPosition == 1) {
                    header_text.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                    header_text.setBackground(mContext.getResources().getDrawable(R.drawable.input_border_top));
                    mFutureFlag=true;
                }
                if (groupPosition == 2) {
                    header_text.setCompoundDrawablesWithIntrinsicBounds(0, 0,0, 0);
                    header_text.setBackground(mContext.getResources().getDrawable(R.drawable.input_border_top));
                    mOldFlag=true;
                }
            }else {
                //header_text.setTypeface(null, Typeface.NORMAL);
                header_text.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.icon_down_light, 0);
                header_text.setBackground(mContext.getResources().getDrawable(R.drawable.input_background_opaque_round));
                Config.logV("Close@@@@"+groupPosition);
                if (groupPosition == 0) {
                    mTodayFlag=false;
                }
                if (groupPosition == 1) {
                    mFutureFlag=false;
                }
                if (groupPosition == 2) {
                    mOldFlag=false;
                }
            }
        }

        return convertView;
    }


    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View view, ViewGroup parent) {
        // Getting child text
        final ActiveCheckIn activelist = (ActiveCheckIn) getChild(groupPosition, childPosition);

        if(groupPosition==0)
            header="today";
        if(groupPosition==1)
            header="future";
        if(groupPosition==2)
            header="old";


        //  activelist = activeChekinList.get(position);
        // Inflating child layout and setting textview
        if (view == null) {
            LayoutInflater infalInflater = (LayoutInflater) this.mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = infalInflater.inflate(R.layout.child_checkinhistory_layout_copy, parent, false);
        }


        TextView tv_businessname = (TextView) view.findViewById(R.id.txt_businessname);
        TextView tv_estTime = (TextView) view.findViewById(R.id.txt_esttime);
        TextView icon_bill = (TextView) view.findViewById(R.id.icon_bill);
        TextView tv_service = (TextView) view.findViewById(R.id.txt_service);
        TextView tv_place = (TextView) view.findViewById(R.id.txt_location);
        TextView tv_personahead = (TextView) view.findViewById(R.id.txt_personahead);
        TextView tv_token = (TextView) view.findViewById(R.id.txt_token);
        TextView tv_time_queue = (TextView) view.findViewById(R.id.time_queue);
        //  tv_waitsatus=(TextView)view.findViewById(R.id.txt_waitsatus);
        TextView icon_fav = (TextView) view.findViewById(R.id.icon_fav);
        TextView icon_message = (TextView) view.findViewById(R.id.icon_message);
        TextView icon_cancel = (TextView) view.findViewById(R.id.icon_cancel);
        TextView icon_rate = (TextView) view.findViewById(R.id.icon_rate);
        LinearLayout layout_token = (LinearLayout) view.findViewById(R.id.layout_token);
        TextView tv_status = (TextView) view.findViewById(R.id.txt_status);
        TextView tv_date = (TextView) view.findViewById(R.id.txt_date);
        TextView tv_partysize = (TextView) view.findViewById(R.id.txt_partysizevalue);

        //  ActiveCheckIn activelist = activeChekinList.get(position);


        tv_businessname.setText(Config.toTitleCase(activelist.getProvider().getBusinessName()));


        icon_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callback.onMethodDelecteCheckinCallback(activelist.getYnwUuid(), activelist.getProvider().getId(),mTodayFlag,mFutureFlag,mOldFlag);
            }
        });
        Typeface tyface = Typeface.createFromAsset(mContext.getAssets(),
                "fonts/Montserrat_Bold.otf");
        tv_businessname.setTypeface(tyface);

        tv_businessname.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callback.onMethodActiveCallback(activelist.getProvider().getUniqueId());
            }
        });

       /* if (activelist.getQueue() != null) {
            if (activelist.getQueue().getLocation().getPlace() != null) {
                myViewHolder.tv_place.setVisibility(View.VISIBLE);
                myViewHolder.tv_place.setText(activelist.getQueue().getLocation().getPlace());
            } else {
                myViewHolder.tv_place.setVisibility(View.GONE);
            }
        }*/


        try {
            if (activelist.getQueue() != null) {
                String geoUri = activelist.getQueue().getLocation().getGoogleMapUrl();
                if (activelist.getQueue().getLocation().getPlace() != null && geoUri != null && !geoUri.equalsIgnoreCase("")) {

                    tv_place.setVisibility(View.VISIBLE);
                    tv_place.setText(activelist.getQueue().getLocation().getPlace());
                } else {
                    tv_place.setVisibility(View.GONE);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        icon_fav.setText("Favourite");
        icon_fav.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.icon_favourite_line, 0, 0);

        for (int i = 0; i < FavList.size(); i++) {
            Config.logV("Fav List-----##&&&-----" + FavList.get(i).getId());


            if (FavList.get(i).getId() == activelist.getProvider().getId()) {
                Config.logV("Fav Fav List--------%%%%--" + activelist.getProvider().getId());
                icon_fav.setVisibility(View.VISIBLE);
                icon_fav.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.icon_favourited, 0, 0);
                activelist.setFavFlag(true);
                icon_fav.setText("Favourite");
            }
        }

        icon_fav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (activelist.isFavFlag()) {

                    callback.onMethodDeleteFavourite(activelist.getProvider().getId(),mTodayFlag,mFutureFlag,mOldFlag);



                   /* AlertDialog myQuittingDialogBox =new AlertDialog.Builder(mContext)
                            //set message, title, and icon
                            .setTitle("Delete")
                            .setMessage("Do you want to remove "+toTitleCase(activelist.getProvider().getBusinessName())+" from favourite list?")


                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {

                                public void onClick(DialogInterface dialog, int whichButton) {
                                    //your deleting code
                                    dialog.dismiss();
                                    callback.onMethodDeleteFavourite(activelist.getProvider().getId());
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
                    Config.logV("Fav Addd" + activelist.getProvider().getId());
                    callback.onMethodAddFavourite(activelist.getProvider().getId(),mTodayFlag,mFutureFlag,mOldFlag);
                }
            }
        });
        tv_place.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Config.logV("googlemap url--------" + activelist.getQueue().getLocation().getGoogleMapUrl());
                String geoUri = activelist.getQueue().getLocation().getGoogleMapUrl();
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(geoUri));
                mContext.startActivity(intent);
            }
        });

        tv_service.setVisibility(View.GONE);

        if (activelist.getService() != null) {
            if (activelist.getService().getName() != null) {
                tv_service.setVisibility(View.VISIBLE);

                Typeface tyface1 = Typeface.createFromAsset(mContext.getAssets(),
                        "fonts/Montserrat_Bold.otf");
                String firstWord = activelist.getService().getName();
                String secondWord = " for ";
                String thirdWord = activelist.getWaitlistingFor().get(0).getFirstName() + " " + activelist.getWaitlistingFor().get(0).getLastName();
                Spannable spannable = new SpannableString(firstWord + secondWord + thirdWord);
                spannable.setSpan(new CustomTypefaceSpan("sans-serif", tyface1), 0, firstWord.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                spannable.setSpan(new CustomTypefaceSpan("sans-serif", tyface1), firstWord.length() + secondWord.length(), firstWord.length() + secondWord.length() + thirdWord.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                tv_service.setText(spannable);
            } else {
                tv_service.setVisibility(View.GONE);
            }
        }

        if (activelist.getPaymentStatus().equalsIgnoreCase("FullyPaid")) {
            icon_bill.setText("Receipt");
        } else {
            icon_bill.setText("Bill");
        }

        Config.logV("Bill------------" + activelist.getWaitlistStatus());
        if (activelist.getBillViewStatus() != null) {
            if (activelist.getBillViewStatus().equalsIgnoreCase("Show")) {
                icon_bill.setVisibility(View.VISIBLE);
            } else {
                icon_bill.setVisibility(View.GONE);
            }
        } else

        {
            icon_bill.setVisibility(View.GONE);
        }


        icon_bill.setOnClickListener(new View.OnClickListener()

        {
            @Override
            public void onClick(View v) {
                callback.onMethodBillIconCallback(activelist.getPaymentStatus(), activelist.getYnwUuid(), activelist.getProvider().getBusinessName(), String.valueOf(activelist.getProvider().getId()));
            }
        });


        Config.logV("Date------------" + activelist.getDate());


        //  tv_estTime.setVisibility(View.VISIBLE);

        if (activelist.getServiceTime() != null)

        {

            String date = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
            if (date.equalsIgnoreCase(activelist.getDate())) {

                Typeface tyface1 = Typeface.createFromAsset(mContext.getAssets(),
                        "fonts/Montserrat_Bold.otf");
                String firstWord = "";
               /* if(header.equalsIgnoreCase("future")){
                    firstWord = "Est Service Time ";
                }

                if(header.equalsIgnoreCase("today")){
                    firstWord = "Est Service Time ";
                }*/
                firstWord = "Checked in for ";

                String secondWord = "Today, " + activelist.getServiceTime();
                Spannable spannable = new SpannableString(firstWord + secondWord);
                spannable.setSpan(new CustomTypefaceSpan("sans-serif", tyface1), firstWord.length(), firstWord.length() + secondWord.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                spannable.setSpan(new ForegroundColorSpan(mContext.getResources().getColor(R.color.violet)),
                        firstWord.length(), firstWord.length() + secondWord.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

                if (activelist.getWaitlistStatus().equalsIgnoreCase("cancelled")) {
                    tv_status.setVisibility(View.VISIBLE);
                    tv_status.setText(secondWord + " - Cancelled ");
                    tv_status.setTextColor(mContext.getResources().getColor(R.color.red));
                }

                if (!activelist.getWaitlistStatus().equalsIgnoreCase("done") && !activelist.getWaitlistStatus().equalsIgnoreCase("cancelled") && !header.equalsIgnoreCase("old")) {
                    tv_estTime.setVisibility(View.VISIBLE);
                    tv_estTime.setText(spannable);
                } else {
                    tv_estTime.setVisibility(View.GONE);
                }


            } else {


                DateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd");
                DateFormat outputFormat = new SimpleDateFormat("dd-MM-yyyy");
                String inputDateStr = activelist.getDate();
                Date datechange = null;
                try {
                    datechange = inputFormat.parse(inputDateStr);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                String outputDateStr = outputFormat.format(datechange);


                Typeface tyface1 = Typeface.createFromAsset(mContext.getAssets(),
                        "fonts/Montserrat_Bold.otf");
                String firstWord = "";
                /*if(header.equalsIgnoreCase("future")){
                    firstWord = "Est Service Time ";
                }

                if(header.equalsIgnoreCase("today")){
                    firstWord = "Est Wait Time ";
                }*/
                firstWord = "Checked in for ";

                //   String secondWord = outputDateStr + ", " + activelist.getServiceTime();

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
                String secondWord = yourDate + ", " + activelist.getServiceTime();


                Spannable spannable = new SpannableString(firstWord + secondWord);
                spannable.setSpan(new CustomTypefaceSpan("sans-serif", tyface1), firstWord.length(), firstWord.length() + secondWord.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                spannable.setSpan(new ForegroundColorSpan(mContext.getResources().getColor(R.color.violet)),
                        firstWord.length(), firstWord.length() + secondWord.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

                if (activelist.getWaitlistStatus().equalsIgnoreCase("cancelled")) {
                    tv_status.setVisibility(View.VISIBLE);
                    tv_status.setText(secondWord + " - Cancelled ");
                    tv_status.setTextColor(mContext.getResources().getColor(R.color.red));
                }
                if (!activelist.getWaitlistStatus().equalsIgnoreCase("done") && !activelist.getWaitlistStatus().equalsIgnoreCase("cancelled") && !header.equalsIgnoreCase("old")) {
                    tv_estTime.setVisibility(View.VISIBLE);
                    tv_estTime.setText(spannable);
                } else {
                    tv_estTime.setVisibility(View.GONE);
                }


                // myViewHolder.tv_estTime.setText(Html.fromHtml("Approx Wait Time " +"<font color=\"#6065FF\"><b>"+ activelist.getAppxWaitingTime() +" Mins "+"</font><b>")) ;

            }


        } else

        {
            String date = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
            if (date.equalsIgnoreCase(activelist.getDate())) {
                Config.logV("getAppxWaitingTime------------" + activelist.getAppxWaitingTime());
                if (activelist.getAppxWaitingTime() == 0) {
                    // myViewHolder.tv_estTime.setText("Estimated Time Now");
                    /* tv_estTime.setVisibility(View.VISIBLE);*/
                    Typeface tyface1 = Typeface.createFromAsset(mContext.getAssets(),
                            "fonts/Montserrat_Bold.otf");
                    String firstWord = "Est Time ";
                    String secondWord = "Now";
                    Spannable spannable = new SpannableString(firstWord + secondWord);
                    spannable.setSpan(new CustomTypefaceSpan("sans-serif", tyface1), firstWord.length(), firstWord.length() + secondWord.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    spannable.setSpan(new ForegroundColorSpan(mContext.getResources().getColor(R.color.violet)),
                            firstWord.length(), firstWord.length() + secondWord.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);


                    if (activelist.getWaitlistStatus().equalsIgnoreCase("cancelled")) {
                        tv_status.setVisibility(View.VISIBLE);
                        tv_status.setText("Cancelled " + secondWord);
                        tv_status.setTextColor(mContext.getResources().getColor(R.color.red));
                    }
                    if (!activelist.getWaitlistStatus().equalsIgnoreCase("done") && !activelist.getWaitlistStatus().equalsIgnoreCase("cancelled") && !header.equalsIgnoreCase("old")) {
                        tv_estTime.setVisibility(View.VISIBLE);
                        tv_estTime.setText(spannable);
                    } else {
                        tv_estTime.setVisibility(View.GONE);
                    }


                } else {
                    if (activelist.getAppxWaitingTime() == -1) {
                        tv_estTime.setVisibility(View.GONE);
                        if (activelist.getWaitlistStatus().equalsIgnoreCase("cancelled")) {
                            tv_status.setVisibility(View.VISIBLE);
                            tv_status.setText(" Cancelled ");
                            tv_status.setTextColor(mContext.getResources().getColor(R.color.red));
                        }
                    } else {
                        //tv_estTime.setVisibility(View.VISIBLE);
                        Typeface tyface1 = Typeface.createFromAsset(mContext.getAssets(),
                                "fonts/Montserrat_Bold.otf");
                        String firstWord = "";
                        if (header.equalsIgnoreCase("future")) {
                            firstWord = "Checked in for ";
                        }

                        if (header.equalsIgnoreCase("today")) {
                            firstWord = "Est Wait Time ";
                        }


                        String secondWord = activelist.getAppxWaitingTime() + " Mins ";
                        Spannable spannable = new SpannableString(firstWord + secondWord);
                        spannable.setSpan(new CustomTypefaceSpan("sans-serif", tyface1), firstWord.length(), firstWord.length() + secondWord.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                        spannable.setSpan(new ForegroundColorSpan(mContext.getResources().getColor(R.color.violet)),
                                firstWord.length(), firstWord.length() + secondWord.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);


                        if (activelist.getWaitlistStatus().equalsIgnoreCase("cancelled")) {
                            tv_status.setVisibility(View.VISIBLE);
                            tv_status.setText(secondWord + " - Cancelled ");
                            tv_status.setTextColor(mContext.getResources().getColor(R.color.red));
                        }


                        if (!activelist.getWaitlistStatus().equalsIgnoreCase("done") && !activelist.getWaitlistStatus().equalsIgnoreCase("cancelled") && !header.equalsIgnoreCase("old")) {
                            tv_estTime.setVisibility(View.VISIBLE);
                            tv_estTime.setText(spannable);
                        } else {
                            tv_estTime.setVisibility(View.GONE);
                        }
                    }


                    // myViewHolder.tv_estTime.setText(Html.fromHtml("Approx Wait Time " +"<font color=\"#6065FF\"><b>"+ activelist.getAppxWaitingTime() +" Mins "+"</font><b>")) ;

                }
            } else {

                Config.logV("response.body().get(i).getQueue().getQueueStartTime()" + activelist.getQueue().getQueueStartTime());
                //Calulate appxtime+questime
                Config.logV("Quueue Time----------------" + activelist.getQueue().getQueueStartTime());
                Config.logV("Quueue End Time----------------" + activelist.getQueue().getQueueEndTime());
                Config.logV("App Time----------------" + activelist.getAppxWaitingTime());
                long appwaittime;
                if (activelist.getAppxWaitingTime() != -1) {
                    appwaittime = TimeUnit.MINUTES.toMillis(activelist.getAppxWaitingTime());
                } else {
                    appwaittime = 0;
                }

                if (activelist.getQueue().getQueueStartTime() != null) {

                    SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a", Locale.ENGLISH);
                    Date Timeconvert = null;
                    long millis = 0;
                    try {
                        // sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
                        Timeconvert = sdf.parse(activelist.getQueue().getQueueStartTime());
                        millis = Timeconvert.getTime();
                        Config.logV("millsss----" + millis);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }


                    long finalcheckin = appwaittime + millis;


                    String timeFORAMT = getDate(finalcheckin, "hh:mm a");

                    Typeface tyface1 = Typeface.createFromAsset(mContext.getAssets(),
                            "fonts/Montserrat_Bold.otf");
                    String firstWord = "";

                    /*if(header.equalsIgnoreCase("future")){
                        firstWord = "Checked in for ";
                    }

                    if(header.equalsIgnoreCase("today")){
                        firstWord = "Est Wait Time ";
                    }
*/
                    DateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd");
                    DateFormat outputFormat = new SimpleDateFormat("dd-MM-yyyy");
                    String inputDateStr = activelist.getDate();
                    Date datechange = null;
                    try {
                        datechange = inputFormat.parse(inputDateStr);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    String outputDateStr = outputFormat.format(datechange);


                    firstWord = "Checked in for ";
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


                    String secondWord = yourDate + ", " + timeFORAMT;
                    Spannable spannable = new SpannableString(firstWord + secondWord);
                    spannable.setSpan(new CustomTypefaceSpan("sans-serif", tyface1), firstWord.length(), firstWord.length() + secondWord.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    spannable.setSpan(new ForegroundColorSpan(mContext.getResources().getColor(R.color.violet)),
                            firstWord.length(), firstWord.length() + secondWord.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

                    if (activelist.getWaitlistStatus().equalsIgnoreCase("cancelled")) {
                        tv_status.setVisibility(View.VISIBLE);
                        tv_status.setText(secondWord + " - Cancelled ");
                        tv_status.setTextColor(mContext.getResources().getColor(R.color.red));
                    }


                    if (!activelist.getWaitlistStatus().equalsIgnoreCase("done") && !activelist.getWaitlistStatus().equalsIgnoreCase("cancelled") && !header.equalsIgnoreCase("old")) {
                        tv_estTime.setVisibility(View.VISIBLE);
                        tv_estTime.setText(spannable);
                    } else {
                        tv_estTime.setVisibility(View.GONE);
                    }
                } else {

                    if (activelist.getAppxWaitingTime() != -1) {
                        String sTime = null;
                        String firstWord = "";
                        try {
                            String startTime = "00:00";
                            String newtime;
                            int minutes = activelist.getAppxWaitingTime();
                            int h = minutes / 60 + Integer.parseInt(startTime.substring(0, 1));
                            int m = minutes % 60 + Integer.parseInt(startTime.substring(3, 4));

                            if (header.equalsIgnoreCase("future")) {
                                firstWord = "Checked in for ";
                            }

                            if (header.equalsIgnoreCase("today")) {

                                firstWord = "Est Wait Time ";
                            }


                            //  firstWord = "Est Wait Time ";

                            if (m > 0 && h > 0) {
                                newtime = h + " Hour :" + m + " Minutes";
                            } else if (h > 0 && m == 0) {
                                newtime = h + " Hour";
                            } else {
                                newtime = m + " Minutes";
                            }
                            sTime = newtime;
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        Typeface tyface1 = Typeface.createFromAsset(mContext.getAssets(),
                                "fonts/Montserrat_Bold.otf");

                        DateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd");
                        DateFormat outputFormat = new SimpleDateFormat("dd-MM-yyyy");
                        String inputDateStr = activelist.getDate();
                        Date datechange = null;
                        try {
                            datechange = inputFormat.parse(inputDateStr);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        String outputDateStr = outputFormat.format(datechange);


                        //firstWord = "Checked in for ";
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

                        String secondWord = yourDate + ", " + sTime;
                        Spannable spannable = new SpannableString(firstWord + secondWord);
                        spannable.setSpan(new CustomTypefaceSpan("sans-serif", tyface1), firstWord.length(), firstWord.length() + secondWord.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                        spannable.setSpan(new ForegroundColorSpan(mContext.getResources().getColor(R.color.violet)),
                                firstWord.length(), firstWord.length() + secondWord.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);


                        if (activelist.getWaitlistStatus().equalsIgnoreCase("cancelled")) {
                            tv_status.setVisibility(View.VISIBLE);
                            tv_status.setText(secondWord + " - Cancelled ");
                            tv_status.setTextColor(mContext.getResources().getColor(R.color.red));
                        }

                        if (!activelist.getWaitlistStatus().equalsIgnoreCase("done") && !activelist.getWaitlistStatus().equalsIgnoreCase("cancelled") && !header.equalsIgnoreCase("old")) {
                            tv_estTime.setVisibility(View.VISIBLE);
                            tv_estTime.setText(spannable);
                        } else {
                            tv_estTime.setVisibility(View.GONE);
                        }
                    } else {
                        tv_estTime.setVisibility(View.GONE);
                    }
                }

            }

        }
        /*} else {
            myViewHolder.tv_estTime.setVisibility(View.GONE);
        }*/

        layout_token.setVisibility(View.GONE);
        if (!header.equalsIgnoreCase("old") && !activelist.getWaitlistStatus().

                equalsIgnoreCase("started") && !activelist.getWaitlistStatus().

                equalsIgnoreCase("cancelled") && !(activelist.getWaitlistStatus().

                equalsIgnoreCase("done")))

        {
            Typeface tyface2 = Typeface.createFromAsset(mContext.getAssets(),
                    "fonts/Montserrat_Bold.otf");
            if(activelist.getToken()!=-1&&activelist.getToken()>=0) {
                tv_token.setVisibility(View.VISIBLE);
                tv_time_queue.setVisibility(View.VISIBLE);
                layout_token.setVisibility(View.VISIBLE);
                String firstWord = "Token # ";


                Config.logV("Token------------" + activelist.getToken());
                String secondWord = String.valueOf(activelist.getToken());
                String queStart = String.valueOf(activelist.getQueue().getQueueStartTime());
                String queEnd = String.valueOf(activelist.getQueue().getQueueEndTime());
                String queueWindow = String.valueOf(activelist.getQueue().getName());
                Spannable spannable = new SpannableString(firstWord + secondWord);
                spannable.setSpan(new ForegroundColorSpan(mContext.getResources().getColor(R.color.sec_title_grey)),
                        0, firstWord.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

                spannable.setSpan(new CustomTypefaceSpan("sans-serif", tyface2), firstWord.length(), firstWord.length() + secondWord.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

                spannable.setSpan(new ForegroundColorSpan(mContext.getResources().getColor(R.color.violet)),
                        firstWord.length(), firstWord.length() + secondWord.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                tv_token.setText(spannable);
                tv_time_queue.setText(queueWindow+" "+"["+" "+queStart+" "+"-"+" "+queEnd+" "+"]");
            }else{
                tv_token.setVisibility(View.GONE);
                tv_time_queue.setVisibility(View.GONE);
            }


            if (String.valueOf(activelist.getPartySize()) != null) {
                if (activelist.getPartySize() > 1) {
                    layout_token.setVisibility(View.VISIBLE);
                    String partyWord = "Party Size ";
                    String ValueWord = String.valueOf(activelist.getPartySize());
                    Spannable spannable1 = new SpannableString(partyWord + ValueWord);
                    spannable1.setSpan(new ForegroundColorSpan(mContext.getResources().getColor(R.color.sec_title_grey)),
                            0, partyWord.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    tv_partysize.setVisibility(View.VISIBLE);
                    // myViewHolder.tv_partysize.setText(String.valueOf(activelist.getPartySize()));

                    spannable1.setSpan(new CustomTypefaceSpan("sans-serif", tyface2), partyWord.length(), partyWord.length() + ValueWord.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

                    spannable1.setSpan(new ForegroundColorSpan(mContext.getResources().getColor(R.color.violet)),
                            partyWord.length(), partyWord.length() + ValueWord.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    tv_partysize.setText(spannable1);
                } else {
                    tv_partysize.setVisibility(View.GONE);
                }
            } else {
                tv_partysize.setVisibility(View.GONE);
            }


        } else

        {
            layout_token.setVisibility(View.GONE);
            tv_estTime.setVisibility(View.GONE);
        }


        if (activelist.getPersonsAhead() != -1 && !activelist.getWaitlistStatus().

                equalsIgnoreCase("cancelled")&&!header.equalsIgnoreCase("old"))

        {
            layout_token.setVisibility(View.VISIBLE);
            tv_personahead.setVisibility(View.VISIBLE);
            String firstWord1 = "People ahead of you ";
            String secondWord1 = String.valueOf(activelist.getPersonsAhead());
            String nobody_ahead = "You are first in line ";
            String one_person_ahead = "1 person ahead of you ";

            if(activelist.getPersonsAhead() == 0){

                Spannable spannable1 = new SpannableString(nobody_ahead);
                spannable1.setSpan(new ForegroundColorSpan(mContext.getResources().getColor(R.color.sec_title_grey)),
                        0, nobody_ahead.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
//                Typeface tyface3 = Typeface.createFromAsset(mContext.getAssets(),
//                        "fonts/Montserrat_Bold.otf");
//                spannable1.setSpan(new CustomTypefaceSpan("sans-serif", tyface3), nobody_ahead.length(), nobody_ahead.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
//
//                spannable1.setSpan(new ForegroundColorSpan(mContext.getResources().getColor(R.color.violet)),
//                        nobody_ahead.length(), nobody_ahead.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                tv_personahead.setText(spannable1);

            }
            else if(activelist.getPersonsAhead() == 1){

                Spannable spannable1 = new SpannableString(one_person_ahead);
                spannable1.setSpan(new ForegroundColorSpan(mContext.getResources().getColor(R.color.sec_title_grey)),
                        0, one_person_ahead.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
//                Typeface tyface3 = Typeface.createFromAsset(mContext.getAssets(),
//                        "fonts/Montserrat_Bold.otf");
//                spannable1.setSpan(new CustomTypefaceSpan("sans-serif", tyface3), one_person_ahead.length(), one_person_ahead.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
//
//                spannable1.setSpan(new ForegroundColorSpan(mContext.getResources().getColor(R.color.violet)),
//                        one_person_ahead.length(), one_person_ahead.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                tv_personahead.setText(spannable1);
            }

            else {

                Spannable spannable1 = new SpannableString(secondWord1 +" "+ firstWord1);
                spannable1.setSpan(new ForegroundColorSpan(mContext.getResources().getColor(R.color.sec_title_grey)),
                        0, firstWord1.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
//                Typeface tyface3 = Typeface.createFromAsset(mContext.getAssets(),
//                        "fonts/Montserrat_Bold.otf");
//                spannable1.setSpan(new CustomTypefaceSpan("sans-serif", tyface3), firstWord1.length(), firstWord1.length() + secondWord1.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
//
//                spannable1.setSpan(new ForegroundColorSpan(mContext.getResources().getColor(R.color.violet)),
//                        firstWord1.length(), firstWord1.length() + secondWord1.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                tv_personahead.setText(spannable1);

            }


        } else

        {
            tv_personahead.setVisibility(View.INVISIBLE);
        }


        icon_message.setOnClickListener(new View.OnClickListener()

        {
            @Override
            public void onClick(View v) {
                callback.onMethodMessageCallback(activelist.getYnwUuid(), String.valueOf(activelist.getProvider().getId()), activelist.getProvider().getBusinessName());

            }
        });


        if (header.equalsIgnoreCase("old"))

        {


            tv_date.setVisibility(View.VISIBLE);

            try {

                String mDate = Config.ChangeDateFormat(activelist.getDate());
                if (mDate != null)
                    tv_date.setText(mDate);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        } else

        {

            tv_date.setVisibility(View.GONE);
        }


        tv_status.setVisibility(View.VISIBLE);
        Typeface tyface1 = Typeface.createFromAsset(mContext.getAssets(),
                "fonts/Montserrat_Bold.otf");
        tv_status.setTypeface(tyface1);

        // myViewHolder.tv_status.setText(activelist.getWaitlistStatus());
        if (activelist.getWaitlistStatus().

                equalsIgnoreCase("done"))

        {
            tv_status.setText("Complete");
            tv_status.setVisibility(View.VISIBLE);
            tv_status.setTextColor(mContext.getResources().getColor(R.color.green));
        }

        if (activelist.getWaitlistStatus().

                equalsIgnoreCase("arrived"))

        {
            tv_status.setText("Arrived");
            tv_status.setVisibility(View.VISIBLE);
            tv_status.setTextColor(mContext.getResources().getColor(R.color.arrived_green));
        }

        if (activelist.getWaitlistStatus().

                equalsIgnoreCase("checkedIn"))

        {
            tv_status.setVisibility(View.GONE);
            //  myViewHolder.tv_status.setText("Checked In");
            //  myViewHolder.tv_status.setTextColor(mContext.getResources().getColor(R.color.violet));
        }

          /* if(activelist.getWaitlistStatus().equalsIgnoreCase("cancelled")) {
               myViewHolder.tv_status.setText("Cancelled");
               myViewHolder.tv_status.setTextColor(mContext.getResources().getColor(R.color.red));
           }*/
        if (activelist.getWaitlistStatus().

                equalsIgnoreCase("started"))

        {
            tv_status.setText("Started");
            tv_status.setVisibility(View.VISIBLE);
            tv_status.setTextColor(mContext.getResources().getColor(R.color.cyan));
        }

        if (activelist.getWaitlistStatus().

                equalsIgnoreCase("prepaymentPending"))

        {
            tv_status.setText("Prepayment Pending");
            tv_status.setVisibility(View.VISIBLE);
            tv_status.setTextColor(mContext.getResources().getColor(R.color.gray));
        }

        /*if(header.equalsIgnoreCase("old")) {*/
        if (activelist.getWaitlistStatus().

                equalsIgnoreCase("done"))

        {
            icon_rate.setVisibility(View.VISIBLE);
        } else

        {
            icon_rate.setVisibility(View.GONE);
        }

        icon_rate.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.icon_star_line, 0, 0);
        if (activelist.getRating() != null) {
            Config.logV("Rating " + activelist.getRating().getStars() + "Activepr" + activelist.getProvider().getBusinessName());
            if (Integer.parseInt(activelist.getRating().getStars()) > 0) {

                icon_rate.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.star_full, 0, 0);
            } else {
                icon_rate.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.icon_star_line, 0, 0);
            }
        }

        /*}else {
            myViewHolder.icon_rate.setVisibility(View.GONE);
        }*/
        icon_rate.setOnClickListener(new View.OnClickListener()

        {
            @Override
            public void onClick(View v) {

                callback.onMethodRating(String.valueOf(activelist.getProvider().getId()), activelist.getYnwUuid(),mTodayFlag,mFutureFlag,mOldFlag);
            }
        });

        if (header.equalsIgnoreCase("old"))

        {
            icon_cancel.setVisibility(View.GONE);
        } else

        {

            if (activelist.getWaitlistStatus().equalsIgnoreCase("checkedIn") || activelist.getWaitlistStatus().equalsIgnoreCase("arrived")) {
                icon_cancel.setVisibility(View.VISIBLE);
            } else {
                icon_cancel.setVisibility(View.GONE);
            }
        }


        Config.logV("Header Title" + header+"Title"+activelist.getProvider().getBusinessName()+"Group"+groupPosition);


        return view;
    }

    public static String getDate(long milliSeconds, String dateFormat) {
        // Create a DateFormatter object for displaying date in specified format.
        SimpleDateFormat formatter = new SimpleDateFormat(dateFormat);
        // formatter.setTimeZone(TimeZone.getTimeZone("UTC"));
        // Create a calendar object that will convert the date and time value in milliseconds to date.
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(milliSeconds);
        return formatter.format(calendar.getTime());
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        // TODO Auto-generated method stub
        return true;
    }

}
