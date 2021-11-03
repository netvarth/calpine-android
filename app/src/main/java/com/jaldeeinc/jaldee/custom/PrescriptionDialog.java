package com.jaldeeinc.jaldee.custom;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.app.DownloadManager;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.TypedArray;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.webkit.MimeTypeMap;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import com.jaldeeinc.jaldee.R;
import com.jaldeeinc.jaldee.activities.Constants;
import com.jaldeeinc.jaldee.model.Bookings;
import com.jaldeeinc.jaldee.response.ActiveAppointment;
import com.jaldeeinc.jaldee.response.ActiveCheckIn;

import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

public class PrescriptionDialog extends Dialog {
    private Context mContext;
    private boolean isActive = false;
    private Bookings bookings = new Bookings();
    CustomTextViewMedium tv_download, tv_share;
    private ActiveCheckIn checkInInfo;
    private ActiveAppointment appointmentInfo;
    private String type = "";
    private String url = "";
    private String longUrl = "";


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

        if (bookings.getCheckInInfo() != null) {
            if (bookings.getCheckInInfo().getPrescShortUrl() != null) {
                url = bookings.getCheckInInfo().getPrescShortUrl();
            }
            if (bookings.getCheckInInfo().getPrescUrl() != null) {
                longUrl = bookings.getCheckInInfo().getPrescUrl();
            }
        } else if (bookings.getAppointmentInfo() != null) {
            if (bookings.getAppointmentInfo().getPrescShortUrl() != null) {
                url = bookings.getAppointmentInfo().getPrescShortUrl();
            }
            if (bookings.getAppointmentInfo().getPrescUrl() != null) {
                longUrl = bookings.getAppointmentInfo().getPrescUrl();
            }
        } else if (checkInInfo != null) {
            if (checkInInfo.getPrescShortUrl() != null) {
                url = checkInInfo.getPrescShortUrl();
            }
            if (checkInInfo.getPrescUrl() != null) {
                longUrl = checkInInfo.getPrescUrl();
            }
        } else if (appointmentInfo != null) {
            if (appointmentInfo.getPrescShortUrl() != null) {
                url = appointmentInfo.getPrescShortUrl();
            }
            if (appointmentInfo.getPrescUrl() != null) {
                longUrl = appointmentInfo.getPrescUrl();
            }
        }

        tv_share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (url != null && !url.equalsIgnoreCase("")) {
                    sharePrescription(url, view);
                } else if (longUrl != null && !longUrl.equalsIgnoreCase("")) {
                    sharePrescription(longUrl, view);
                }
            }
        });

        tv_download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (url != null && !url.equalsIgnoreCase("")) {
                    downloadPrescription(url, longUrl, mContext, view);
                } else if (longUrl != null && !longUrl.equalsIgnoreCase("")) {
                    downloadPrescription(longUrl, longUrl, mContext, view);
                }
            }
        });
    }

    public static void sharePrescription(String sUrl, View view) {
        Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
        sharingIntent.setType("text/html");
        String shareBody = sUrl;
        sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Prescription details");
        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
        view.getContext().startActivity(Intent.createChooser(sharingIntent, "Share via"));
    }

    public static void downloadPrescription(String url, String longUrl, Context mContext, View view) {
        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(mContext, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions((Activity) unwrap(view.getContext()), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
            // this will request for permission when permission is not true
        } else {
            // Download code here
            URL url1 = null;
            try {
                url1 = new URL(url);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
            String filname= FilenameUtils.getName(url1.getPath());

            File file = new File(Uri.parse(url).toString());
            Uri uri = Uri.parse(url);
            DownloadManager.Request request = new DownloadManager.Request(uri);
            request.setDescription(file.getName());
            request.setTitle(filname);
            request.setMimeType(getMimeType(longUrl));
//                        request.setMimeType("application/pdf");
//                        request.setMimeType("image/jpeg");
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

    public static String getMimeType(String url) {
        String type = null;
        String extension = MimeTypeMap.getFileExtensionFromUrl(url);
        if (extension != null) {
            type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
        }
        return type;
    }

    private static Activity unwrap(Context context) {
        while (!(context instanceof Activity) && context instanceof ContextWrapper) {
            context = ((ContextWrapper) context).getBaseContext();
        }
        return (Activity) context;
    }
}
