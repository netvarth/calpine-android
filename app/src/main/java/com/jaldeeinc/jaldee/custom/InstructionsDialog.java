package com.jaldeeinc.jaldee.custom;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;

import com.jaldeeinc.jaldee.R;

public class InstructionsDialog extends Dialog {

    String postInstructions = "";
    String title = "";
    Context mContext;
    CustomTextViewSemiBold tvPostInfoTitle;
    CustomTextViewMedium tvPostInfo;
    ImageView ivClose;

    public InstructionsDialog(@NonNull Context context, String infoText, String infoTitle) {
        super(context);
        this.mContext = context;
        this.postInstructions = infoText;
        this.title = infoTitle;

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.instructions);

        ivClose = findViewById(R.id.iv_close);
        tvPostInfo = findViewById(R.id.tv_postInfo);
        tvPostInfoTitle = findViewById(R.id.tv_postInfoTitle);


        if (title != null) {
            tvPostInfoTitle.setText(title);
        }
        if (postInstructions != null) {
            tvPostInfo.setText(Html.fromHtml(postInstructions));
        }

        ivClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                dismiss();
            }
        });

    }
}
