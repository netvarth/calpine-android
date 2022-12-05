package com.jaldeeinc.jaldee.adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.jaldeeinc.jaldee.R;
import com.jaldeeinc.jaldee.callback.ContactAdapterCallback;
import com.jaldeeinc.jaldee.common.Config;
import com.jaldeeinc.jaldee.model.ContactModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sharmila on 3/12/18.
 */

public class ContactDetailAdapter extends RecyclerView.Adapter<ContactDetailAdapter.MyViewHolder> {

    private List<ContactModel> contactDetail = new ArrayList<>();
    Context mContext;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView name, txtcontact_value;
        LinearLayout contact_list_row_item;

        public MyViewHolder(View view) {
            super(view);
            name = (TextView) view.findViewById(R.id.txtcontact);
            txtcontact_value = (TextView) view.findViewById(R.id.txtcontact_value);
            contact_list_row_item= (LinearLayout) view.findViewById(R.id.contact_list_row_item);

        }
    }


    Activity mActivity;
    ContactAdapterCallback mInterface;

    public ContactDetailAdapter(List<ContactModel> mcontactDetail, Context mContext, Activity mActivity, ContactAdapterCallback mInterface) {
        this.mContext = mContext;
        this.contactDetail = mcontactDetail;
        this.mActivity = mActivity;
        this.mInterface=mInterface;


    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.contact_list_row, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder myViewHolder, final int position) {
        final ContactModel mcontactDetail = contactDetail.get(position);

        Typeface tyface= Typeface.createFromAsset(mContext.getAssets(),
                "fonts/Montserrat_Bold.otf");
        myViewHolder.name.setTypeface(tyface);

        myViewHolder.name.setText(Config.toTitleCase(mcontactDetail.getLabel()));
        myViewHolder.txtcontact_value.setText(mcontactDetail.getInstance());
        myViewHolder.contact_list_row_item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mInterface.onMethodContactCallback(mcontactDetail.getResource(),mcontactDetail.getInstance());
            }
        });

    }


    @Override
    public int getItemCount() {
        return contactDetail.size();
    }
}