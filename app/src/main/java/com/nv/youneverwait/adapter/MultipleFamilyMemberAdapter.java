package com.nv.youneverwait.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.nv.youneverwait.R;
import com.nv.youneverwait.activities.CheckinFamilyMember;
import com.nv.youneverwait.common.Config;
import com.nv.youneverwait.model.FamilyArrayModel;
import com.nv.youneverwait.utils.SharedPreference;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by sharmila on 15/10/18.
 */

public class MultipleFamilyMemberAdapter extends RecyclerView.Adapter<MultipleFamilyMemberAdapter.MyViewHolder> {

    private ArrayList<FamilyArrayModel> familyList;
    Context mContext;


    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView tv_name, txtaddmember;


        public MyViewHolder(View view) {
            super(view);
            tv_name = (TextView) view.findViewById(R.id.tv_name);
            txtaddmember = (TextView) view.findViewById(R.id.txtaddmember);


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
    public MultipleFamilyMemberAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.name_familytlist_row, parent, false);

        return new MultipleFamilyMemberAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MultipleFamilyMemberAdapter.MyViewHolder myViewHolder, final int position) {
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
