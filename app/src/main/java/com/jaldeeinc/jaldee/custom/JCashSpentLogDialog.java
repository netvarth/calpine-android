package com.jaldeeinc.jaldee.custom;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.jaldeeinc.jaldee.R;
import com.jaldeeinc.jaldee.activities.JaldeeCashActivity;
import com.jaldeeinc.jaldee.adapter.JCashSpentLogAdapter;
import com.jaldeeinc.jaldee.common.Config;
import com.jaldeeinc.jaldee.connection.ApiClient;
import com.jaldeeinc.jaldee.connection.ApiInterface;
import com.jaldeeinc.jaldee.response.JCashSpentDetails;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class JCashSpentLogDialog extends Dialog {

    private Context mContext;
    RecyclerView recycle_spentlist;
    ArrayList<JCashSpentDetails> listJCashSpentDetails = new ArrayList<JCashSpentDetails>();
    JCashSpentLogAdapter jCashSpentLogAdapter;
    private ImageView ivClose;

    public JCashSpentLogDialog(@NonNull Context context, ArrayList<JCashSpentDetails> listJCashSpentDetails) {
        super(context);
        this.mContext = context;
        this.listJCashSpentDetails = listJCashSpentDetails;
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.jcash_spentlog_dialog);
        ivClose = findViewById(R.id.iv_close);
        recycle_spentlist = findViewById(R.id.recycle_spentlist);
        jcashSpentDetailsDialog(mContext, listJCashSpentDetails);

        ivClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }
    public void jcashSpentDetailsDialog(Context context, ArrayList<JCashSpentDetails> listJCashSpentDtls){
        jCashSpentLogAdapter = new JCashSpentLogAdapter(context, listJCashSpentDtls);
        RecyclerView.LayoutManager mChooseLanguagesLayoutManager = new LinearLayoutManager(mContext);
        recycle_spentlist.setLayoutManager(mChooseLanguagesLayoutManager);
        recycle_spentlist.setAdapter(jCashSpentLogAdapter);
        jCashSpentLogAdapter.notifyDataSetChanged();
    }
}
