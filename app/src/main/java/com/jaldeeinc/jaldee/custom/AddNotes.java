package com.jaldeeinc.jaldee.custom;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.core.content.ContextCompat;

import com.jaldeeinc.jaldee.Interface.ISendMessage;
import com.jaldeeinc.jaldee.R;
import com.jaldeeinc.jaldee.common.Config;
import com.jaldeeinc.jaldee.connection.ApiClient;
import com.jaldeeinc.jaldee.connection.ApiInterface;
import com.pranavpandey.android.dynamic.toasts.DynamicToast;

import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddNotes extends Dialog {

    private Context context;
    private CustomTextViewSemiBold tvTo;
    private EditText etMessage;
    private Button btCancel, btSend;
    String providerName;
    private ISendMessage iSendMessage;
    private String userMessage;

    public AddNotes(@NonNull Context context, String businessName, ISendMessage iSendMessage, String userMessage) {
        super(context);
        this.context = context;
        this.providerName = businessName;
        this.iSendMessage = iSendMessage;
        this.userMessage = userMessage;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.enquiry);


        tvTo = findViewById(R.id.tv_to);
        etMessage = findViewById(R.id.edt_message);
        btCancel = findViewById(R.id.btn_cancel);
        btSend = findViewById(R.id.btn_send);

        if (userMessage != null) {

            etMessage.setText(userMessage);
        }

        Typeface font_style = Typeface.createFromAsset(context.getAssets(), "fonts/JosefinSans-SemiBold.ttf");
        btSend.setTypeface(font_style);
        btCancel.setTypeface(font_style);
        btSend.setText("Save");

        if (providerName != null) {

            String name = providerName;
            name = name.substring(0, 1).toUpperCase() + name.substring(1).toLowerCase();
            tvTo.setText(name);
        }

//        etMessage.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void afterTextChanged(Editable arg0) {
//                if (etMessage.getText().toString().length() >= 1 && !etMessage.getText().toString().trim().isEmpty()) {
//                    btSend.setEnabled(true);
//                    btSend.setClickable(true);
//                    btSend.setBackground(context.getResources().getDrawable(R.color.location_theme));
//                } else {
//                    btSend.setEnabled(false);
//                    btSend.setClickable(false);
//                    btSend.setBackground(context.getResources().getDrawable(R.color.button_grey));
//                }
//            }
//
//            @Override
//            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//            }
//
//            @Override
//            public void onTextChanged(CharSequence s, int start, int before, int count) {
//            }
//        });

        btSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                iSendMessage.getMessage(etMessage.getText().toString());
                dismiss();

            }
        });
        btCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

    }


}
