package com.jaldeeinc.jaldee.custom;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import com.jaldeeinc.jaldee.R;
import com.jaldeeinc.jaldee.activities.Constants;
import com.jaldeeinc.jaldee.model.Bookings;
import com.jaldeeinc.jaldee.response.ActiveAppointment;
import com.jaldeeinc.jaldee.response.ActiveCheckIn;

import java.io.File;

public class PrescriptionDialog extends Dialog {
    private Context mContext;
    private boolean isActive = false;
    private Bookings bookings = new Bookings();
    CustomTextViewMedium tv_download, tv_share;
    private ActiveCheckIn checkInInfo;
    private ActiveAppointment appointmentInfo;
    private String type = "";
    private String url ="";


    public PrescriptionDialog(@NonNull Context context, boolean isActive, Bookings bookings) {
        super(context);
        this.mContext = context;
        this.isActive = isActive;
        this.bookings = bookings;
    }

    public PrescriptionDialog(Context mContext, boolean isActive, ActiveCheckIn checkInInfo, String type) {
        super(mContext);
        this.mContext = mContext;
        this.isActive = isActive;
        this.checkInInfo = checkInInfo;
        this.type = type;
    }

    public PrescriptionDialog(Context mContext, boolean isActive, ActiveAppointment appointmentInfo, String type) {
        super(mContext);
        this.mContext = mContext;
        this.isActive = isActive;
        this.appointmentInfo = appointmentInfo;
        this.type = type;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.prescriptipn_layout);

        tv_download = findViewById(R.id.presc_download);
        tv_share = findViewById(R.id.presc_share);


        if(bookings.getCheckInInfo()!=null && bookings.getCheckInInfo().getPrescUrl()!=null){
            url = bookings.getCheckInInfo().getPrescUrl();
        }
        else if(bookings.getAppointmentInfo()!=null && bookings.getAppointmentInfo().getPrescUrl()!=null){
            url = bookings.getAppointmentInfo().getPrescUrl();
        }
        else if(checkInInfo!=null && checkInInfo.getPrescUrl()!=null){
            url = checkInInfo.getPrescUrl();
        }
        else if(appointmentInfo!=null && appointmentInfo.getPrescUrl()!=null){
            url = appointmentInfo.getPrescUrl();
        }


        tv_share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                        Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                        sharingIntent.setType("text/html");
                        String shareBody = url;
                        sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Prescription details");
                        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
                        view.getContext().startActivity(Intent.createChooser(sharingIntent, "Share via"));
            }
        });


        tv_download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(ActivityCompat.checkSelfPermission(mContext, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(mContext, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
                    ActivityCompat.requestPermissions((Activity) view.getContext(), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                    // this will request for permission when permission is not true
                }else{
                    // Download code here

                    File file  = new File(Uri.parse(url).toString());
                    DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
                    request.setDescription(file.getName());
                    request.setTitle(file.getName());
                    // request.setMimeType(".jpg");
// in order for this if to run, you must use the android 3.2 to compile your app
                    request.allowScanningByMediaScanner();
                    request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                    request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, url);

// get download service and enqueue file
                    DownloadManager manager = (DownloadManager) mContext.getSystemService(Context.DOWNLOAD_SERVICE);
                    if (manager != null) {
                        manager.enqueue(request);
                    }

                }
            }
        });




    }
}
