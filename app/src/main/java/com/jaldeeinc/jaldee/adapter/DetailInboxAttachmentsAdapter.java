package com.jaldeeinc.jaldee.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jaldeeinc.jaldee.R;
import com.jaldeeinc.jaldee.callback.DetailInboxAdapterCallback;
import com.jaldeeinc.jaldee.common.Config;
import com.jaldeeinc.jaldee.response.InboxModel;
import com.jaldeeinc.jaldee.utils.SharedPreference;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class DetailInboxAttachmentsAdapter extends RecyclerView.Adapter<DetailInboxAttachmentsAdapter.MyViewHolder> {
    private List<InboxModel> mInboxList;
    Context mContext;

    public class MyViewHolder extends RecyclerView.ViewHolder {
      ImageView  iv_attachImage;



        public MyViewHolder(View view) {
            super(view);
            iv_attachImage = view.findViewById(R.id.file_attach_detail);

        }
    }
    DetailInboxAdapterCallback callback;

    public DetailInboxAttachmentsAdapter(ArrayList<InboxModel> mDetailInboxList, Context mContext, DetailInboxAdapterCallback callback) {
        this.mInboxList = mDetailInboxList;
        this.mContext = mContext;
        this.callback = callback;
    }

    @Override
    public DetailInboxAttachmentsAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.detaill_inboxlist_attachments_row, parent, false);


        return new DetailInboxAttachmentsAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final DetailInboxAttachmentsAdapter.MyViewHolder myViewHolder, final int position) {
        final InboxModel inboxList = mInboxList.get(position);
        if(inboxList.getAttachments()!= null) {
            for (int i = 0; i < inboxList.getAttachments().size(); i++) {
                myViewHolder.iv_attachImage.setImageURI(Uri.parse(inboxList.getAttachments().get(i).toString()));
            }
        }
    }

    @Override
    public int getItemCount() {
        return mInboxList.size();
    }
}

