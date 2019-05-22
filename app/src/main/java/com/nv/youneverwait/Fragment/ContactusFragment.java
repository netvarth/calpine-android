package com.nv.youneverwait.Fragment;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.nv.youneverwait.R;
import com.nv.youneverwait.common.Config;
import com.payu.magicretry.MainActivity;

/**
 * Created by sharmila on 25/10/18.
 */

public class ContactusFragment extends RootFragment {
    Context mContext;
    LinearLayout layout_phone,maillayout;
    private final int CALL_REQUEST = 100;
    TextView tv_phone;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View row = inflater.inflate(R.layout.contactus, container, false);

        mContext = getActivity();

        TextView tv_title = (TextView) row.findViewById(R.id.toolbartitle);
        TextView txt_appname = (TextView) row.findViewById(R.id.txt_appname);
        TextView txtreach = (TextView) row.findViewById(R.id.txtreach);

        tv_phone = (TextView) row.findViewById(R.id.txtphone);
        LinearLayout contactLayout = (LinearLayout) row.findViewById(R.id.contactLayout);
        layout_phone = (LinearLayout) row.findViewById(R.id.layout_phone);
        maillayout= (LinearLayout) row.findViewById(R.id.maillayout);

        contactLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        Typeface tyface1 = Typeface.createFromAsset(mContext.getAssets(),
                "fonts/Montserrat_Bold.otf");
        tv_title.setTypeface(tyface1);
        txt_appname.setTypeface(tyface1);
        txtreach.setTypeface(tyface1);


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
                        "mailto","support@jaldee.com", null));
                emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Jaldee Feedback");
                emailIntent.putExtra(Intent.EXTRA_TEXT, "");
                try {
                    startActivity(Intent.createChooser(emailIntent, "Send mail..."));
                } catch (android.content.ActivityNotFoundException ex) {
                    Toast.makeText(mContext, "There are no email clients installed.", Toast.LENGTH_SHORT).show();
                }
            }
        });


        return row;
    }
    /*@Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        final InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getView().getWindowToken(), 0);
    }*/
    public void callPhoneNumber() {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (ContextCompat.checkSelfPermission(mContext, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling

                    //requestPermissions(getActivity(), new String[]{Manifest.permission.CALL_PHONE}, CALL_REQUEST);

                    requestPermissions(new String[]{
                            Manifest.permission.CALL_PHONE}, CALL_REQUEST);

                    return;
                }else{
                    Intent callIntent = new Intent(Intent.ACTION_CALL);
                    callIntent.setData(Uri.parse("tel:0487-2325650"));
                    startActivity(callIntent);
                }
            }else {

                Intent callIntent = new Intent(Intent.ACTION_CALL);
                callIntent.setData(Uri.parse("tel:0487-2325650"));
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
                callIntent.setData(Uri.parse("tel:0487-2325650"));
                startActivity(callIntent);
            } else {
                Toast.makeText(mContext, getResources().getString(R.string.call_permission_denied_message), Toast.LENGTH_SHORT).show();
            }
        }
    }
}
