package com.jaldeeinc.jaldee.custom;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;

import com.google.i18n.phonenumbers.NumberParseException;
import com.jaldeeinc.jaldee.Interface.IEditContact;
import com.jaldeeinc.jaldee.R;
import com.jaldeeinc.jaldee.common.Config;

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

        etPhone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                tvError.setVisibility(View.INVISIBLE);
            }
        });

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

        if (!countryCode.trim().equalsIgnoreCase("") && !mail.trim().equalsIgnoreCase("")) {

            try {
                if (Config.validatePhoneNumberWithCountryCode(countryCode, number)) {

                    tvError.setVisibility(View.INVISIBLE);

                    if (isValidEmail(mail)) {
                        tvError.setVisibility(View.INVISIBLE);
                        iEditContact.onEdit(countryCode, number, mail);
                        dismiss();
                    } else {

                        tvError.setVisibility(View.VISIBLE);
                        tvError.setText("* Enter valid email address");
                        tvError.startAnimation(animShake);
                    }

                } else {
                    tvError.setVisibility(View.VISIBLE);
                    tvError.setText("Enter valid mobile number");
                    //tvError.startAnimation(animShake);
                }
            } catch (NumberParseException e) {
                if (e.getErrorType().equals(NumberParseException.ErrorType.INVALID_COUNTRY_CODE)) {
                    tvError.setVisibility(View.VISIBLE);
                    tvError.setText("Invalid country code");
                } else if (e.getErrorType().equals(NumberParseException.ErrorType.NOT_A_NUMBER)) {
                    tvError.setVisibility(View.VISIBLE);
                    tvError.setText("Enter valid mobile number");
                }
            }

        } else {

            tvError.setVisibility(View.VISIBLE);
            tvError.setText("* All fields are mandatory");
            tvError.startAnimation(animShake);
        }
    }
    public static boolean isValidEmail(CharSequence target) {
        return (!TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches());
    }
}
