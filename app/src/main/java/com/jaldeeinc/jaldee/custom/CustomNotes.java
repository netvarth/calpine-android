package com.jaldeeinc.jaldee.custom;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import androidx.cardview.widget.CardView;

import com.jaldeeinc.jaldee.Interface.ISaveNotes;
import com.jaldeeinc.jaldee.R;

public class CustomNotes extends Dialog {

    private Context context;
    private CustomEditTextRegular etMessage;
    private CardView cvNo, cvYes;
    int itemId;
    String message;
    private ISaveNotes iSaveNotes;
    private CustomTextViewMedium tvErrorMessage;
    Animation animShake;
    private ImageView ivClose;

    public CustomNotes(Context context, int itemId, ISaveNotes iSaveNotes, String instruction) {
        super(context);
        this.context = context;
        this.itemId = itemId;
        this.iSaveNotes = iSaveNotes;
        this.message = instruction;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.item_notes);

        animShake = AnimationUtils.loadAnimation(context, R.anim.shake);

        ivClose = findViewById(R.id.iv_close);
        etMessage = findViewById(R.id.edt_message);
        cvNo = findViewById(R.id.cv_no);
        cvYes = findViewById(R.id.cv_yes);
        tvErrorMessage = findViewById(R.id.tv_errorMessage);

        if (message != null && !message.trim().equalsIgnoreCase("")) {

            etMessage.setText(message);
        } else {

            etMessage.setText("");
        }

        cvYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (etMessage.getText().toString().trim().equalsIgnoreCase("")) {

                    tvErrorMessage.setVisibility(View.VISIBLE);
                    tvErrorMessage.startAnimation(animShake);
                } else {
                    tvErrorMessage.setVisibility(View.GONE);
                    iSaveNotes.saveMessage(etMessage.getText().toString(), itemId);
                    dismiss();
                }

            }
        });

        cvNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        ivClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

    }

}
