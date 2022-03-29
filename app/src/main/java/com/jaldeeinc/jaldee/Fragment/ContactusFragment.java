package com.jaldeeinc.jaldee.Fragment;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.core.content.ContextCompat;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.jaldeeinc.jaldee.R;
import com.jaldeeinc.jaldee.common.Config;

/**
 * Created by sharmila on 25/10/18.
 */

public class ContactusFragment extends RootFragment {
    Context mContext;
    LinearLayout layout_phone, maillayout, layout_whatsapp;
    private final int CALL_REQUEST = 100;
    TextView tv_phone, tv_support;
    String supportPhoneNumber1, whatsappNumber,supportMail;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View row = inflater.inflate(R.layout.contactus, container, false);

        mContext = getActivity();
        supportMail = getString(R.string.support_mail);
        supportPhoneNumber1 = getString(R.string.support_number_1);
        whatsappNumber = getString(R.string.whatsapp_number);

        TextView tv_title = (TextView) row.findViewById(R.id.toolbartitle);
        TextView txt_appname = (TextView) row.findViewById(R.id.txt_appname);
        TextView txtreach = (TextView) row.findViewById(R.id.txtreach);

        tv_phone = (TextView) row.findViewById(R.id.txtphone);
        tv_support = (TextView) row.findViewById(R.id.txtwhatsapp);
        LinearLayout contactLayout = (LinearLayout) row.findViewById(R.id.contactLayout);
        layout_phone = (LinearLayout) row.findViewById(R.id.layout_phone);
        maillayout = (LinearLayout) row.findViewById(R.id.maillayout);
        layout_whatsapp = (LinearLayout) row.findViewById(R.id.layout_whatsapp);

        contactLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        Typeface tyface1 = Typeface.createFromAsset(mContext.getAssets(),
                "fonts/JosefinSans-Bold.ttf");
        tv_title.setTypeface(tyface1);


        ImageView iBackPress = (ImageView) row.findViewById(R.id.backpress);
        iBackPress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // what do you want here
                getFragmentManager().popBackStack();
            }
        });
        tv_title.setText("Contact Us");


        layout_phone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callPhoneNumber();
            }
        });
        maillayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                        "mailto", supportMail, null));
                emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Jaldee Feedback");
                emailIntent.putExtra(Intent.EXTRA_TEXT, "");
                try {
                    startActivity(Intent.createChooser(emailIntent, "Send mail..."));
                } catch (android.content.ActivityNotFoundException ex) {
                    Toast.makeText(mContext, "There are no email clients installed.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        layout_whatsapp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openWhatsapp();
            }
        });
        return row;
    }

    public void callPhoneNumber() {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (ContextCompat.checkSelfPermission(mContext, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling

                    //requestPermissions(getActivity(), new String[]{Manifest.permission.CALL_PHONE}, CALL_REQUEST);

                    requestPermissions(new String[]{Manifest.permission.CALL_PHONE}, CALL_REQUEST);

                    return;
                } else {
                    if (supportPhoneNumber1 != null && !supportPhoneNumber1.isEmpty() && !supportPhoneNumber1.equals("")) {
                        Intent callIntent = new Intent(Intent.ACTION_CALL);
                        callIntent.setData(Uri.parse("tel:" + supportPhoneNumber1));
                        startActivity(callIntent);
                    }
                }
            } else {
                if (supportPhoneNumber1 != null && !supportPhoneNumber1.isEmpty() && !supportPhoneNumber1.equals("")) {
                    Intent callIntent = new Intent(Intent.ACTION_CALL);
                    callIntent.setData(Uri.parse("tel:" + supportPhoneNumber1));
                    startActivity(callIntent);
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void openWhatsapp() {
        //So you can get the edittext value
        String message = "";
        boolean installed = appInstalledOrNot("com.whatsapp");
        if (installed) {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse("http://api.whatsapp.com/send?phone=" + "+91" + whatsappNumber + "&text=" + message));
            startActivity(intent);
        } else {
            Toast.makeText(getActivity(), "Whats app not installed on your device", Toast.LENGTH_SHORT).show();
        }
    }

    //Create method appInstalledOrNot
    private boolean appInstalledOrNot(String url) {
        PackageManager packageManager = getActivity().getPackageManager();
        boolean app_installed;
        try {
            packageManager.getPackageInfo(url, PackageManager.GET_ACTIVITIES);
            app_installed = true;
        } catch (PackageManager.NameNotFoundException e) {
            app_installed = false;
        }
        return app_installed;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        if (requestCode == CALL_REQUEST) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Config.logV("CALL GRANTED @@@@@@@@@@@@@@");
                if (supportPhoneNumber1 != null && !supportPhoneNumber1.isEmpty() && !supportPhoneNumber1.equals("")) {
                    Intent callIntent = new Intent(Intent.ACTION_CALL);
                    callIntent.setData(Uri.parse("tel:" + supportPhoneNumber1));
                    startActivity(callIntent);
                }
            } else {
                Toast.makeText(mContext, getResources().getString(R.string.call_permission_denied_message), Toast.LENGTH_SHORT).show();
            }
        }
    }
}
