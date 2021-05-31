package com.jaldeeinc.jaldee.custom;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
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
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.gson.JsonObject;
import com.jaldeeinc.jaldee.Interface.ISendMessage;
import com.jaldeeinc.jaldee.R;
import com.jaldeeinc.jaldee.activities.Constants;
import com.jaldeeinc.jaldee.adapter.LocationsAdapter;
import com.jaldeeinc.jaldee.common.Config;
import com.jaldeeinc.jaldee.connection.ApiClient;
import com.jaldeeinc.jaldee.connection.ApiInterface;
import com.jaldeeinc.jaldee.response.SearchLocation;
import com.pranavpandey.android.dynamic.toasts.DynamicToast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.jaldeeinc.jaldee.connection.ApiClient.context;

public class EnquiryDialog extends Dialog {

    private Context context;
    private CustomTextViewSemiBold tvTo;
    private EditText etMessage;
    private Button btCancel, btSend;
    int uniqueId,providerid;
    String providerName;
    private ISendMessage iSendMessage;

    public EnquiryDialog(@NonNull Context context, String businessName, ISendMessage iSendMessage, int id, int providerid) {
        super(context);
        this.context = context;
        this.providerName = businessName;
        this.iSendMessage = iSendMessage;
        this.uniqueId = id;
        this.providerid = providerid;

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.enquiry);


        tvTo = findViewById(R.id.tv_to);
        etMessage = findViewById(R.id.edt_message);
        btCancel = findViewById(R.id.btn_cancel);
        btSend = findViewById(R.id.btn_send);

        Typeface font_style = Typeface.createFromAsset(context.getAssets(), "fonts/JosefinSans-SemiBold.ttf");
        btSend.setTypeface(font_style);
        btCancel.setTypeface(font_style);

        if (providerName != null) {

            String name = providerName;
            tvTo.setText(name);
        }

        etMessage.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable arg0) {
                if (etMessage.getText().toString().length() >= 1 && !etMessage.getText().toString().trim().isEmpty()) {
                    btSend.setEnabled(true);
                    btSend.setClickable(true);
                    btSend.setBackground(context.getResources().getDrawable(R.color.location_theme));
                } else {
                    btSend.setEnabled(false);
                    btSend.setClickable(false);
                    btSend.setBackground(context.getResources().getDrawable(R.color.button_grey));
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
        });

        btSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ApiCommunicate(String.valueOf(uniqueId), etMessage.getText().toString());
            }
        });
        btCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

    }

    private void ApiCommunicate(String accountID, String message) {
        ApiInterface apiService =
                ApiClient.getClient(context).create(ApiInterface.class);
        final Dialog mDialog = Config.getProgressDialog(context, context.getResources().getString(R.string.dialog_log_in));
        mDialog.show();
        MediaType type = MediaType.parse("*/*");
        MultipartBody.Builder mBuilder = new MultipartBody.Builder();
        mBuilder.setType(MultipartBody.FORM);
        JsonObject obj = new JsonObject();
        obj.addProperty("msg", message);
        obj.addProperty("messageType", "ENQUIRY");

        RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), obj.toString());
        mBuilder.addFormDataPart("message","blob",body);
        RequestBody requestBody = mBuilder.build();

        JSONObject jsonObj = new JSONObject();
        try {
            jsonObj.put("communicationMessage", message);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Call<ResponseBody> call = apiService.postProviderMessage(accountID,String.valueOf(providerid), requestBody);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    if (mDialog.isShowing())
                        Config.closeDialog(getOwnerActivity(), mDialog);
                    Config.logV("URL---------------" + response.raw().request().url().toString().trim());
                    Config.logV("Response--code-------------------------" + response.code());
                    if (response.code() == 200) {
                        DynamicToast.make(context, "Message sent successfully", AppCompatResources.getDrawable(
                                context, R.drawable.adt_ic_success),
                                ContextCompat.getColor(context, R.color.white), ContextCompat.getColor(context, R.color.green), Toast.LENGTH_SHORT).show();
                        dismiss();
                    } else if (response.code() == 403) {
                        Toast.makeText(context, "Please complete the details of profile name,location and working hours to continue", Toast.LENGTH_LONG).show();
                    } else if (response.code() == 404) {
                        Toast.makeText(context, "The account doesn't exist", Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                // Log error here since request failed
                Config.logV("Fail---------------" + t.toString());
                dismiss();
                if (mDialog.isShowing())
                    Config.closeDialog(getOwnerActivity(), mDialog);
            }
        });
    }

}
