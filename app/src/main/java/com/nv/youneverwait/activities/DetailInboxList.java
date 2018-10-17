package com.nv.youneverwait.activities;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.design.widget.BottomSheetDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.nv.youneverwait.R;
import com.nv.youneverwait.adapter.DetailInboxAdapter;
import com.nv.youneverwait.callback.DetailInboxAdapterCallback;
import com.nv.youneverwait.common.Config;
import com.nv.youneverwait.connection.ApiClient;
import com.nv.youneverwait.connection.ApiInterface;
import com.nv.youneverwait.response.InboxModel;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by sharmila on 27/8/18.
 */

public class DetailInboxList extends AppCompatActivity implements DetailInboxAdapterCallback{
    RecyclerView recylce_inbox_detail;
    Context mContext;
    DetailInboxAdapter mDetailAdapter;
    static ArrayList<InboxModel> mDetailInboxList=new ArrayList<>();
    TextView txtprovider;
    String provider;
    DetailInboxAdapterCallback mInterface;
    @Override
    public void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detailinbox);
        recylce_inbox_detail=(RecyclerView) findViewById(R.id.recylce_inbox_detail);

        mContext=this;
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            provider = extras.getString("provider");

        }

        ImageView iBackPress=(ImageView)findViewById(R.id.backpress) ;
        iBackPress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // what do you want here
                finish();
            }
        });
        TextView tv_title = (TextView)findViewById(R.id.toolbartitle);
        tv_title.setText(provider);


        Typeface tyface = Typeface.createFromAsset(getAssets(),
                "fonts/Montserrat_Bold.otf");
        tv_title.setTypeface(tyface);




        //tv_title.setGravity(Gravity.CENTER);
        txtprovider= (TextView) findViewById(R.id.txtprovider);

        tv_title.setTypeface(tyface);
        txtprovider.setTypeface(tyface);
        txtprovider.setText(provider);


        mInterface=(DetailInboxAdapterCallback) this;
        Config.logV("mDetailInboxList SIZE #############"+mDetailInboxList.size());
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        recylce_inbox_detail.setLayoutManager(mLayoutManager);

        Collections.sort(mDetailInboxList, new Comparator<InboxModel>() {
            @Override
            public int compare(InboxModel r1, InboxModel r2) {
                return new Long(r2.getTimeStamp()).compareTo(new Long(r1.getTimeStamp()));
            }
        });


        mDetailAdapter = new DetailInboxAdapter(mDetailInboxList, mContext,mInterface);
        recylce_inbox_detail.setAdapter(mDetailAdapter);
        mDetailAdapter.notifyDataSetChanged();
    }

    public static boolean setInboxList(ArrayList<InboxModel> data){
        mDetailInboxList= data;
        Config.logV("mDetailInboxList SIZE"+mDetailInboxList.size());
        return true;

    }

    @Override
    public void onMethodCallback(final String waitListId, final int accountID) {
        final BottomSheetDialog dialog = new BottomSheetDialog(mContext,R.style.DialogStyle);
        dialog.setContentView(R.layout.reply);
        dialog.show();

        Button btn_send=(Button)dialog.findViewById(R.id.btn_send);
        Button btn_cancel=(Button)dialog.findViewById(R.id.btn_cancel);
        final EditText edt_message=(EditText) dialog.findViewById(R.id.edt_message);
        TextView txtsendmsg=(TextView) dialog.findViewById(R.id.txtsendmsg);

        btn_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ApiCommunicate(waitListId,String.valueOf(accountID),edt_message.getText().toString(),dialog);

            }
        });

        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }

    private void ApiCommunicate(String waitListId, String accountID, String message, final BottomSheetDialog dialog) {


        ApiInterface apiService =
                ApiClient.getClient(mContext).create(ApiInterface.class);


        final Dialog mDialog = Config.getProgressDialog(mContext, mContext.getResources().getString(R.string.dialog_log_in));
        mDialog.show();


        JSONObject jsonObj = new JSONObject();
        try {
            jsonObj.put("communicationMessage", message);



        } catch (JSONException e) {
            e.printStackTrace();
        }
        RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), jsonObj.toString());
        Call<ResponseBody> call = apiService.WaitListMessage(waitListId,String.valueOf(accountID),body);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                try {

                    if (mDialog.isShowing())
                        Config.closeDialog(getParent(), mDialog);

                    Config.logV("URL---------------" + response.raw().request().url().toString().trim());
                    Config.logV("Response--code-------------------------" + response.code());

                    if (response.code() == 200) {

                       dialog.dismiss();



                    }


                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                // Log error here since request failed
                Config.logV("Fail---------------" + t.toString());
                if (mDialog.isShowing())
                    Config.closeDialog(getParent(), mDialog);

            }
        });


    }
}
