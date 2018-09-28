package com.netvarth.youneverwait.adapter;

import android.app.Activity;
import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import com.netvarth.youneverwait.R;
import com.netvarth.youneverwait.activities.CheckIn;
import com.netvarth.youneverwait.activities.CheckinFamilyMember;
import com.netvarth.youneverwait.common.Config;
import com.netvarth.youneverwait.model.FamilyArrayModel;
import com.netvarth.youneverwait.utils.SharedPreference;

import java.util.List;
/**
 * Created by sharmila on 8/8/18.
 */

public class CheckIn_FamilyMemberListAdapter extends RecyclerView.Adapter<CheckIn_FamilyMemberListAdapter.MyViewHolder> {

    private List<FamilyArrayModel> familyList;
    Context mContext;
    private RadioButton lastCheckedRB = null;
    public class MyViewHolder extends RecyclerView.ViewHolder {
        public RadioButton name;
        //LinearLayout lfamily;

        public MyViewHolder(View view) {
            super(view);
            name = (RadioButton) view.findViewById(R.id.Rmemeber);
           // lfamily = (LinearLayout) view.findViewById(R.id.lfamily);

        }
    }

    Activity activity;

    public CheckIn_FamilyMemberListAdapter(List<FamilyArrayModel> mfamilyList, Context mContext, Activity mActivity) {
        this.mContext = mContext;
        this.familyList = mfamilyList;
        this.activity = mActivity;

    }

    @Override
    public CheckIn_FamilyMemberListAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.radio_familytlist_row, parent, false);

        return new CheckIn_FamilyMemberListAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final CheckIn_FamilyMemberListAdapter.MyViewHolder myViewHolder, final int position) {
        final FamilyArrayModel familylist = familyList.get(position);
        int consumerId = SharedPreference.getInstance(mContext).getIntValue("consumerId", 0);
        if(familylist.getId()==consumerId){

            myViewHolder.name.setText(familylist.getFirstName()+" "+familylist.getLastName());
        }else {
            myViewHolder.name.setText(familylist.getUserProfile().getFirstName()+" "+familylist.getUserProfile().getLastName());
        }
        myViewHolder.name.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (lastCheckedRB != null) {
                    lastCheckedRB.setChecked(false);
                }
                //store the clicked radiobutton
                lastCheckedRB = myViewHolder.name;
                Config.logV("Selected--------------------"+myViewHolder.name.getText().toString());

                Config.logV("Selected--------------------"+myViewHolder.name.getText().toString());
                CheckinFamilyMember.changeMemberName(myViewHolder.name.getText().toString(),Integer.parseInt(familylist.getUser()));
            }
        });



    }


    @Override
    public int getItemCount() {
        return familyList.size();
    }
}