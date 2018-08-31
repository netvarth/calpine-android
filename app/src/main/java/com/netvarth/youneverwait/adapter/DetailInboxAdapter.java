package com.netvarth.youneverwait.adapter;

/**
 * Created by sharmila on 27/8/18.
 */

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.netvarth.youneverwait.R;
import com.netvarth.youneverwait.response.InboxModel;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

/**
 * Created by sharmila on 14/8/18.
 */
public class DetailInboxAdapter extends RecyclerView.Adapter<DetailInboxAdapter.MyViewHolder> {

    private List<InboxModel> mInboxList;
    Context mContext;

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView tv_provider,tv_date,tv_message;
        LinearLayout linear_inbox_layout;
        public MyViewHolder(View view) {
            super(view);
            tv_provider=(TextView)view.findViewById(R.id.txt_provider);
            tv_date=(TextView)view.findViewById(R.id.txt_date);
            tv_message=(TextView)view.findViewById(R.id.txt_message);
            linear_inbox_layout=(LinearLayout) view.findViewById(R.id.inbox_layout);



        }
    }



    public DetailInboxAdapter(List<InboxModel> mInboxList, Context mContext) {
        this.mContext = mContext;
        this.mInboxList = mInboxList;
       // this.activity = mActivity;

    }

    @Override
    public DetailInboxAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.detaill_inboxlist_row, parent, false);


        return new DetailInboxAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final DetailInboxAdapter.MyViewHolder myViewHolder, final int position) {
        final InboxModel inboxList = mInboxList.get(position);

        myViewHolder.tv_message.setText(inboxList.getMsg());


        DateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm");
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(inboxList.getTimeStamp());
        myViewHolder.tv_date.setText(formatter.format(calendar.getTime()));


       // myViewHolder.tv_provider.setText(inboxList.getUserName());





    }


    @Override
    public int getItemCount() {
        return mInboxList.size();
    }
}