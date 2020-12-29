package com.jaldeeinc.jaldee.custom;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;

import com.jaldeeinc.jaldee.Interface.IEditContact;
import com.jaldeeinc.jaldee.Interface.ISendMessage;
import com.jaldeeinc.jaldee.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class EditContactDialog extends Dialog {

    private Context mContext;

    @BindView(R.id.iv_close)
    ImageView ivClose;

    @BindView(R.id.et_code)
    CustomEditTextRegular etCode;

    @BindView(R.id.et_phone)
    CustomEditTextRegular etPhone;

    @BindView(R.id.et_mail)
    CustomEditTextRegular etMail;

    @BindView(R.id.tv_error)
    CustomTextViewSemiBold tvError;

    @BindView(R.id.cv_submit)
    CardView cvSubmit;

    Animation animShake;
    IEditContact iEditContact;
    String phoneNumber = "";
    String emailAddress = "";
    String cCode = "";


    public EditContactDialog(@NonNull Context context, IEditContact iEditContact, String phoneNumber, String email, String countryCode) {
        super(context);
        this.mContext = context;
        this.iEditContact = iEditContact;
        this.phoneNumber = phoneNumber;
        this.emailAddress = email;
        this.cCode = countryCode;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_contactinfo);
        ButterKnife.bind(this);

        animShake = AnimationUtils.loadAnimation(mContext, R.anim.shake);

        etCode.setText(cCode);
        etMail.setText(emailAddress);
        etPhone.setText(phoneNumber);


        ivClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dismiss();
            }
        });

        etCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                etCode.setText("+91");
            }
        });

        cvSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                validate();

            }
        });

    }

    private void validate() {

        String countryCode = etCode.getText().toString();
        String number = etPhone.getText().toString();
        String mail = etMail.getText().toString();

        if (!countryCode.trim().equalsIgnoreCase("")&& !number.trim().equalsIgnoreCase("") && !mail.trim().equalsIgnoreCase("")){

            tvError.setVisibility(View.GONE);
            iEditContact.onEdit(countryCode,number,mail);
            dismiss();

        } else {

            tvError.setVisibility(View.VISIBLE);
            tvError.startAnimation(animShake);
        }

    }
}
