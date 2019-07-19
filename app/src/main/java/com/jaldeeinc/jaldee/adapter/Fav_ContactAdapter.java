package com.jaldeeinc.jaldee.adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jaldeeinc.jaldee.callback.ContactAdapterCallback;
import com.jaldeeinc.jaldee.R;
import com.jaldeeinc.jaldee.callback.ContactAdapterCallback;
import com.jaldeeinc.jaldee.common.Config;
import com.jaldeeinc.jaldee.model.ContactModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sharmila on 12/7/19.
 */

public class Fav_ContactAdapter extends RecyclerView.Adapter<Fav_ContactAdapter.MyViewHolder> {

    private List<ContactModel> contactDetail = new ArrayList<>();
    Context mContext;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView name, txtcontact_value;
        LinearLayout contact_list_row_item;

        public MyViewHolder(View view) {
            super(view);
            name = (TextView) view.findViewById(R.id.txtcontact);
            txtcontact_value = (TextView) view.findViewById(R.id.txtcontact_value);
            contact_list_row_item = (LinearLayout) view.findViewById(R.id.contact_list_row_item);

        }
    }


    Activity mActivity;
    ContactAdapterCallback mInterface;

    public Fav_ContactAdapter(List<ContactModel> mcontactDetail, Context mContext,ContactAdapterCallback mInterface) {
        this.mContext = mContext;
        this.contactDetail = mcontactDetail;
        this.mInterface=mInterface;
        Config.logV("Contact Detail @@@@@@@@@@@" + contactDetail.size());
        Config.logV("Contact Detail @@@@@@@@@@@" + contactDetail.get(0).getInstance());

    }

    @Override
    public Fav_ContactAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.favcontact_list_row, parent, false);

        return new Fav_ContactAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(Fav_ContactAdapter.MyViewHolder myViewHolder, final int position) {
        final ContactModel mcontactDetail = contactDetail.get(position);

       /* Typeface tyface= Typeface.createFromAsset(mContext.getAssets(),
                "fonts/Montserrat_Bold.otf");
        myViewHolder.name.setTypeface(tyface);*/
        if (!mcontactDetail.getLabel().equalsIgnoreCase("")) {
            myViewHolder.name.setVisibility(View.VISIBLE);
            myViewHolder.name.setText(Config.toTitleCase(mcontactDetail.getLabel()));
        } else {
            myViewHolder.name.setVisibility(View.GONE);
        }
        myViewHolder.txtcontact_value.setText(mcontactDetail.getInstance());
        Config.logV("Contact Detail @@@@@@@@333333@@@" + mcontactDetail.getInstance());
        myViewHolder.contact_list_row_item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                  mInterface.onMethodContactCallback(mcontactDetail.getResource(),mcontactDetail.getInstance());
            }
        });

    }


    @Override
    public int getItemCount() {
        Config.logV("Contact Detail @@@contactDetail.size()@" + contactDetail.size());
        return contactDetail.size();

    }
}

