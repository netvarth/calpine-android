package com.jaldeeinc.jaldee.custom;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import com.jaldeeinc.jaldee.R;
import com.jaldeeinc.jaldee.common.Config;

public class CustomDailogVerification extends Dialog {

    public Context c;
    public Dialog d;
    ImageView image_logo;
    String mynw_level;
    String providername;

    public CustomDailogVerification(Context a, String ynw_level, String providername) {
        super(a);
        // TODO Auto-generated constructor stub
        this.c = a;
        mynw_level = ynw_level;
        this.providername = providername;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.custom_jaldeeverified);
        CustomTextViewSemiBold tv_title = (CustomTextViewSemiBold) findViewById(R.id.toolbartitle);

        tv_title.setText(Config.toTitleCase(providername));

        image_logo = (ImageView) findViewById(R.id.imglogo);
        if (mynw_level.equalsIgnoreCase("2")) {
            image_logo.setImageResource(R.drawable.jaldee_basic);

        } else if (mynw_level.equalsIgnoreCase("3")) {

            image_logo.setImageResource(R.drawable.jaldee_basicplus);


        } else if (mynw_level.equalsIgnoreCase("4")) {

            image_logo.setImageResource(R.drawable.jaldee_adv);

        }
        ImageView ivClose = (ImageView) findViewById(R.id.iv_close);
        ivClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // what do you want here
                dismiss();
            }
        });


    }
}



