package com.jaldeeinc.jaldee.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import com.jaldeeinc.jaldee.R;
import com.jaldeeinc.jaldee.activities.CheckinFamilyMember;
import com.jaldeeinc.jaldee.common.Config;
import com.jaldeeinc.jaldee.custom.CustomTextViewBold;
import com.jaldeeinc.jaldee.custom.CustomTextViewMedium;
import com.jaldeeinc.jaldee.model.FamilyArrayModel;
import com.jaldeeinc.jaldee.utils.SharedPreference;

import java.util.ArrayList;

/**
 * Created by sharmila on 15/10/18.
 */

public class MultipleFamilyMemberAdapter extends RecyclerView.Adapter<MultipleFamilyMemberAdapter.MyViewHolder> {

    private ArrayList<FamilyArrayModel> familyList;
    Context mContext;


    public class MyViewHolder extends RecyclerView.ViewHolder {
        public CustomTextViewBold tv_name;
        public CustomTextViewMedium txtaddmember;


        public MyViewHolder(View view) {
            super(view);
            tv_name = (CustomTextViewBold) view.findViewById(R.id.tv_name);
            txtaddmember = (CustomTextViewMedium) view.findViewById(R.id.txtaddmember);


        }
    }

    Activity activity;
    boolean multiple;

    public MultipleFamilyMemberAdapter(ArrayList<FamilyArrayModel> mfamilyList, Context mContext, Activity mActivity) {
        this.mContext = mContext;
        this.familyList = mfamilyList;
        this.activity = mActivity;

    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.name_familytlist_row, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder myViewHolder, final int position) {
        final FamilyArrayModel familylist = familyList.get(position);
        int consumerId = SharedPreference.getInstance(mContext).getIntValue("consumerId", 0);
        if (familylist.getId() == consumerId) {

            myViewHolder.tv_name.setText(familylist.getFirstName() + " " + familylist.getLastName());
        } else {
            myViewHolder.tv_name.setText(familylist.getFirstName() + " " + familylist.getLastName());
        }

        if (position == 0) {
            myViewHolder.txtaddmember.setVisibility(View.VISIBLE);
        } else {
            myViewHolder.txtaddmember.setVisibility(View.GONE);
        }
        Config.logV("ID@@@@@@@@@@@@@@@@@@@@@@" + familylist.getId());
        myViewHolder.txtaddmember.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String mFirstName = SharedPreference.getInstance(mContext).getStringValue("firstname", "");
                String mLastName = SharedPreference.getInstance(mContext).getStringValue("lastname", "");
                int consumerID = SharedPreference.getInstance(mContext).getIntValue("consumerId", 0);


                Intent iFamily = new Intent(v.getContext(), CheckinFamilyMember.class);
                iFamily.putExtra("multiple", true);
                iFamily.putExtra("firstname", mFirstName);
                iFamily.putExtra("lastname", mLastName);
                iFamily.putExtra("consumerID", consumerID);
                iFamily.putExtra("update", 1);
                iFamily.putExtra("checklist", (ArrayList<FamilyArrayModel>) familyList);
                mContext.startActivity(iFamily);

            }
        });

    }


    @Override
    public int getItemCount() {
        return familyList.size();
    }

}
