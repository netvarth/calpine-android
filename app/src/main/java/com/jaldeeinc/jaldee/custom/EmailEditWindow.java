package com.jaldeeinc.jaldee.custom;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.jaldeeinc.jaldee.Interface.IMailSubmit;
import com.jaldeeinc.jaldee.R;
import com.jaldeeinc.jaldee.database.DatabaseHandler;
import com.jaldeeinc.jaldee.response.ProfileModel;


public class EmailEditWindow extends Dialog {
    Context context;
    EditText email;
    Button btnsave;
    DatabaseHandler db;
    ProfileModel profileDetails;
    CustomTextViewMedium tvErrorMessage;
    private IMailSubmit iMailSubmit;
    String currentMailId;

    public EmailEditWindow(Context mContext, ProfileModel profileDetails, IMailSubmit iMailSubmit, String mailId) {
        super(mContext);
        this.context = mContext;
        this.profileDetails = profileDetails;
        this.iMailSubmit = iMailSubmit;
        currentMailId = mailId;
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.email_info);
        email = findViewById(R.id.email);
        btnsave = findViewById(R.id.btnSave);
        tvErrorMessage = findViewById(R.id.error_mesg);
        Typeface tyface = Typeface.createFromAsset(context.getAssets(),
                "fonts/JosefinSans-SemiBold.ttf");
        email.setTypeface(tyface);
        btnsave.setTypeface(tyface);
        if (currentMailId != null) {
            email.setText(currentMailId);
        }

        btnsave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkMail();
            }
        });
    }

    boolean isEmailValid(CharSequence email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email)
                .matches();
    }

    private void checkMail() {
        String mailid = email.getText().toString();
        if (mailid.trim().length() > 0) {
            if (isEmailValid(mailid)) {
                iMailSubmit.mailUpdated(mailid);
                dismiss();
            } else {
                tvErrorMessage.setVisibility(View.VISIBLE);
                tvErrorMessage.setText("Enter valid mail Id");
            }
        } else {
            tvErrorMessage.setVisibility(View.VISIBLE);
            tvErrorMessage.setText("This field is required");
        }
    }
}
