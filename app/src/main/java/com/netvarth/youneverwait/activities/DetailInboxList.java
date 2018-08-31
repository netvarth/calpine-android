package com.netvarth.youneverwait.activities;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.netvarth.youneverwait.R;
import com.netvarth.youneverwait.adapter.DetailInboxAdapter;
import com.netvarth.youneverwait.common.Config;
import com.netvarth.youneverwait.response.InboxModel;

import java.util.ArrayList;

/**
 * Created by sharmila on 27/8/18.
 */

public class DetailInboxList extends AppCompatActivity {
    RecyclerView recylce_inbox_detail;
    Context mContext;
    DetailInboxAdapter mDetailAdapter;
    static ArrayList<InboxModel> mDetailInboxList=new ArrayList<>();
    @Override
    public void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detailinbox);
        recylce_inbox_detail=(RecyclerView) findViewById(R.id.recylce_inbox_detail);

        mContext=this;


        Config.logV("mDetailInboxList SIZE #############"+mDetailInboxList.size());
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        recylce_inbox_detail.setLayoutManager(mLayoutManager);
        mDetailAdapter = new DetailInboxAdapter(mDetailInboxList, mContext);
        recylce_inbox_detail.setAdapter(mDetailAdapter);
        mDetailAdapter.notifyDataSetChanged();
    }

    public static boolean setInboxList(ArrayList<InboxModel> data){
        mDetailInboxList= data;
        Config.logV("mDetailInboxList SIZE"+mDetailInboxList.size());
        return true;

    }
}
