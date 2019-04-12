package com.nv.youneverwait.adapter;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nv.youneverwait.R;
import com.nv.youneverwait.callback.LocationCheckinCallback;
import com.nv.youneverwait.common.Config;
import com.nv.youneverwait.connection.ApiClient;
import com.nv.youneverwait.connection.ApiInterface;
import com.nv.youneverwait.response.SearchCheckInMessage;


import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class LocationCheckinAdapter extends RecyclerView.Adapter<LocationCheckinAdapter.MyViewHolder> {

    private List<SearchCheckInMessage> checkList;
    Context mContext;
    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView name, tv_waittime,txtservice,txttoken;

        ImageView ic_delete;
        LinearLayout lfamily;


        public MyViewHolder(View view) {
            super(view);
            name = (TextView) view.findViewById(R.id.txtname);
            tv_waittime = (TextView) view.findViewById(R.id.txtwaittime);
            txtservice= (TextView) view.findViewById(R.id.txtservice);
            ic_delete = (ImageView) view.findViewById(R.id.delete);
            lfamily = (LinearLayout) view.findViewById(R.id.lfamily);
            txttoken= (TextView) view.findViewById(R.id.txttoken);
          //  tv_nocheckin=(TextView) view.findViewById(R.id.tv_nocheckin);

        }
    }

    LocationCheckinCallback callback;
    Activity mActivity;
    String accountID;
    public LocationCheckinAdapter(LocationCheckinCallback callback,String accountID,List<SearchCheckInMessage> mcheckList, Context mContext, Activity mActivity) {
        this.mContext = mContext;
        this.checkList = mcheckList;
        this.callback=callback;
        this.mActivity=mActivity;
        this.accountID=accountID;


    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.checkin_list_row, parent, false);

        return new LocationCheckinAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder myViewHolder, final int position) {
        final SearchCheckInMessage checklist = checkList.get(position);


        myViewHolder.name.setText(checklist.getWaitlistingFor().get(0).getFirstName());
        myViewHolder.txtservice.setText(checklist.getService().getName());
        myViewHolder.txttoken.setText(checklist.getToken());

        myViewHolder.ic_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //ApiDeleteCheckin(checklist.getYnwUuid(),accountID,position);
                AlertDialog diaBox =  AskOption(checklist.getYnwUuid(),accountID,position);
                diaBox.show();
            }
        });


            myViewHolder.tv_waittime.setVisibility(View.VISIBLE);
            if (checklist.getServiceTime() != null) {

                String date = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
                if (date.equalsIgnoreCase(checklist.getDate())) {

                    myViewHolder.tv_waittime.setText("Today, \n"+ checklist.getServiceTime());
                } else {
                    DateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd");
                    DateFormat outputFormat = new SimpleDateFormat("dd-MM-yyyy");
                    String inputDateStr=checklist.getDate();
                    Date datechange = null;
                    try {
                        datechange = inputFormat.parse(inputDateStr);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    String outputDateStr = outputFormat.format(datechange);
                    myViewHolder.tv_waittime.setText( outputDateStr + ", \n"+checklist.getServiceTime());
                }


            } else {
                String date = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
                if (date.equalsIgnoreCase(checklist.getDate())) {
                    Config.logV("getAppxWaitingTime------------" + checklist.getAppxWaitingTime());
                    if (checklist.getAppxWaitingTime() == 0) {
                        myViewHolder.tv_waittime.setText("Today");

                    } else {
                        myViewHolder.tv_waittime.setText(checklist.getAppxWaitingTime() + " Mins ");
                    }
                } else {


                    //Calulate appxtime+questime
                    Config.logV("Quueue Time----------------" + checklist.getQueue().getQueueStartTime());
                    Config.logV("App Time----------------" + checklist.getAppxWaitingTime());
                    long appwaittime = TimeUnit.MINUTES.toMillis(checklist.getAppxWaitingTime());


                    SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a", Locale.ENGLISH);
                    Date Timeconvert = null;
                    long millis = 0;
                    try {
                        // sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
                        Timeconvert = sdf.parse(checklist.getQueue().getQueueStartTime());
                        millis = Timeconvert.getTime();
                        Config.logV("millsss----" + millis);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                    long finalcheckin = appwaittime + millis;

                    String timeFORAMT = getDate(finalcheckin, "hh:mm a");

                    myViewHolder.tv_waittime.setText(timeFORAMT);

                }

            }


    }


    public static String getDate(long milliSeconds, String dateFormat)
    {
        // Create a DateFormatter object for displaying date in specified format.
        SimpleDateFormat formatter = new SimpleDateFormat(dateFormat);
        // formatter.setTimeZone(TimeZone.getTimeZone("UTC"));
        // Create a calendar object that will convert the date and time value in milliseconds to date.
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(milliSeconds);
        return formatter.format(calendar.getTime());
    }

    @Override
    public int getItemCount() {
        return checkList.size();
    }

    private void ApiDeleteCheckin(String ynwuuid, String accountID, final int pos) {


        ApiInterface apiService =
                ApiClient.getClient(mContext).create(ApiInterface.class);


        final Dialog mDialog = Config.getProgressDialog(mContext, mContext.getResources().getString(R.string.dialog_log_in));
        mDialog.show();


        Call<ResponseBody> call = apiService.deleteActiveCheckIn(ynwuuid, String.valueOf(accountID));

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                try {

                    if (mDialog.isShowing())
                        Config.closeDialog(mActivity, mDialog);

                    Config.logV("URL---------------" + response.raw().request().url().toString().trim());
                    Config.logV("Response--code-------------------------" + response.code());

                    if (response.code() == 200) {

                        if (response.body().string().equalsIgnoreCase("true")) {
                            checkList.remove(pos);
                            notifyDataSetChanged();
                            if(checkList.size()==0){
                                callback.onMethodCallback();

                            }

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
                    Config.closeDialog(mActivity, mDialog);

            }
        });


    }

    private AlertDialog AskOption(final String ynwuuid, final String accountID, final int pos)
    {
        AlertDialog myQuittingDialogBox =new AlertDialog.Builder(mContext)
                //set message, title, and icon
                .setTitle("Delete")
                .setMessage("Are you sure you want to cancel this Check-in?")


                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int whichButton) {
                        //your deleting code
                        dialog.dismiss();
                        ApiDeleteCheckin(ynwuuid,accountID,pos);
                    }

                })



                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        dialog.dismiss();

                    }
                })
                .create();
        return myQuittingDialogBox;

    }
}
