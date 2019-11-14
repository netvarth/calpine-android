package com.jaldeeinc.jaldee.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.jaldeeinc.jaldee.R;
import com.jaldeeinc.jaldee.activities.DetailInboxList;
import com.jaldeeinc.jaldee.database.DatabaseHandler;
import com.jaldeeinc.jaldee.response.InboxModel;



import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by sharmila on 14/8/18.
 */
public class InboxAdapter extends RecyclerView.Adapter<InboxAdapter.MyViewHolder> {

    private List<InboxModel> mInboxCompleteList;
    private List<InboxModel> mInboxList;
    Context mContext;
    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView tv_provider,tv_date,tv_message;
        LinearLayout linear_inbox_layout;
        RecyclerView recylce_inbox_detail;
        public MyViewHolder(View view) {
            super(view);
            tv_provider=(TextView)view.findViewById(R.id.txt_provider);
            tv_date=(TextView)view.findViewById(R.id.txt_date);
            tv_message=(TextView)view.findViewById(R.id.txt_message);
            linear_inbox_layout=(LinearLayout) view.findViewById(R.id.inbox_layout);



        }
    }

    Activity activity;
    ArrayList<InboxModel> mDetailInboxList=new ArrayList<>();
    public InboxAdapter(List<InboxModel> mInboxList, Context mContext, Activity mActivity,List<InboxModel> mInboxCompleteList) {
        this.mContext = mContext;
        this.mInboxList = mInboxList;
        this.activity = mActivity;
        this.mInboxCompleteList=mInboxCompleteList;

    }

    @Override
    public InboxAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.inboxlist_row, parent, false);


        return new InboxAdapter.MyViewHolder(itemView);
    }
    public static String toTitleCase(String str) {

        if (str == null) {
            return null;
        }

        boolean space = true;
        StringBuilder builder = new StringBuilder(str);
        final int len = builder.length();

        for (int i = 0; i < len; ++i) {
            char c = builder.charAt(i);
            if (space) {
                if (!Character.isWhitespace(c)) {
                    // Convert to title case and switch out of whitespace mode.
                    builder.setCharAt(i, Character.toTitleCase(c));
                    space = false;
                }
            } else if (Character.isWhitespace(c)) {
                space = true;
            } else {
                builder.setCharAt(i, Character.toLowerCase(c));
            }
        }

        return builder.toString();
    }
    @Override
    public void onBindViewHolder(final InboxAdapter.MyViewHolder myViewHolder, final int position) {
        final InboxModel inboxList = mInboxList.get(position);

        myViewHolder.tv_message.setText(inboxList.getMsg());


        DateFormat formatter = new SimpleDateFormat("dd-MM-yyyy hh:mm a");
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(inboxList.getTimeStamp());
        myViewHolder.tv_date.setText(formatter.format(calendar.getTime()));

        Typeface tyface = Typeface.createFromAsset(mContext.getAssets(),
                "fonts/Montserrat_Bold.otf");
        myViewHolder.tv_provider.setTypeface(tyface);

       /* String[] strArray = inboxList.getUserName().split(" ");
        StringBuilder builder = new StringBuilder();
        for (String s : strArray) {
            String cap = s.substring(0, 1).toUpperCase() + s.substring(1);
            builder.append(cap + " ");
        }*/


       // String cap_Provider = inboxList.getUserName().substring(0, 1).toUpperCase() + inboxList.getUserName().substring(1);
        myViewHolder.tv_provider.setText(toTitleCase(inboxList.getUserName()));
        myViewHolder.linear_inbox_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


               /* String mKey= inboxList.getId() + "_" + inboxList.getUserName();
                Config.logV("mInboxCompleteList-----------"+mInboxCompleteList.size());
                mDetailInboxList.clear();
                for(int i=0;i<mInboxCompleteList.size();i++){
                    //String mChkKey= mInboxCompleteList.get(i).getId() + "_" + String.valueOf(mInboxCompleteList.get(i).getUserName()).toLowerCase();

                    int activeConsumerId = SharedPreference.getInstance(mContext).getIntValue("consumerId", 0);

                    String senderName = String.valueOf(mInboxCompleteList.get(i).getUserName()).toLowerCase();

                    int senderId = mInboxCompleteList.get(i).getId();
                    String messageStatus = "in";

                    if (senderId == activeConsumerId) {

                        senderId = mInboxCompleteList.get(i).getReceiverId();
                        senderName = String.valueOf(mInboxCompleteList.get(i).getReceiverName()).toLowerCase();
                        messageStatus = "out";
                    }

                    Config.logV("SenderID------------" + senderId);
                    String mChkKey = senderId + "_" + senderName;

                    if(mKey.equalsIgnoreCase(mChkKey)) {
                        InboxModel inbox = new InboxModel();
                        inbox.setKey(mChkKey);
                        inbox.setMsg(mInboxCompleteList.get(i).getMsg());
                        inbox.setUserName(mInboxCompleteList.get(i).getUserName());
                        inbox.setTimeStamp(mInboxCompleteList.get(i).getTimeStamp());
                        inbox.setId(mInboxCompleteList.get(i).getId());
                        inbox.setIs_see(false);
                        inbox.setWaitlistId(mInboxCompleteList.get(i).getWaitlistId());
                        inbox.setMessageStatus(messageStatus);

                        mDetailInboxList.add(inbox);
                        DetailInboxList.setInboxList(mDetailInboxList);


                    }
                    if(i==mInboxCompleteList.size()-1){
                        Config.logV("Detail list--------------"+mDetailInboxList.size());
                        Intent iInbox=new Intent(v.getContext(),DetailInboxList.class);
                        iInbox.putExtra("provider",inboxList.getUserName());
                        mContext.startActivity(iInbox);
                    }
                }*/


                DatabaseHandler db=new DatabaseHandler(mContext);
                //Config.logV("inboxList.getUniqueID()"+inboxList.getUniqueID());
                mDetailInboxList= db.getInboxDetail(inboxList.getUniqueID());
               if(DetailInboxList.setInboxList(mDetailInboxList))

               {
                   Intent iInbox = new Intent(v.getContext(), DetailInboxList.class);
                   iInbox.putExtra("provider",inboxList.getUserName());
                   mContext.startActivity(iInbox);
               }


            }
        });




    }


    @Override
    public int getItemCount() {
        return mInboxList.size();
    }
}