package com.nv.youneverwait.adapter;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RadioButton;

import com.nv.youneverwait.R;
import com.nv.youneverwait.activities.CheckinFamilyMember;
import com.nv.youneverwait.common.Config;
import com.nv.youneverwait.model.FamilyArrayModel;
import com.nv.youneverwait.utils.SharedPreference;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sharmila on 8/8/18.
 */

public class CheckIn_FamilyMemberListAdapter extends RecyclerView.Adapter<CheckIn_FamilyMemberListAdapter.MyViewHolder> {

    private List<FamilyArrayModel> familyList;
    Context mContext;
    private RadioButton lastCheckedRB = null;
    private List<FamilyArrayModel> checkedfamilyList=new ArrayList<>();

    //private List<FamilyArrayModel> checkeditemList=new ArrayList<>();
    public class MyViewHolder extends RecyclerView.ViewHolder {
        public RadioButton name;
        CheckBox Checkmemeber;

        public MyViewHolder(View view) {
            super(view);
            name = (RadioButton) view.findViewById(R.id.Rmemeber);
            Checkmemeber = (CheckBox) view.findViewById(R.id.Checkmemeber);


        }
    }

    Activity activity;
    boolean multiple;
    int memId,update;
    Button changemem;

    public CheckIn_FamilyMemberListAdapter(Button changemem,int update,int memId, boolean multiple, List<FamilyArrayModel> mfamilyList, Context mContext, Activity mActivity) {
        this.mContext = mContext;
        this.familyList = mfamilyList;
        this.activity = mActivity;
        this.multiple = multiple;
        this.memId = memId;
        this.update=update;
        this.changemem=changemem;
        Config.logV("multiplemem------222---------------" + multiple);

    }

    ArrayList<FamilyArrayModel> CheckList = new ArrayList<>();

    public CheckIn_FamilyMemberListAdapter(Button changemem,ArrayList<FamilyArrayModel> mCheckList, boolean multiple, List<FamilyArrayModel> mfamilyList, Context mContext, Activity mActivity) {
        this.mContext = mContext;
        this.familyList = mfamilyList;
        this.activity = mActivity;
        this.multiple = multiple;
        this.CheckList = mCheckList;
        this.changemem=changemem;


    }

    @Override
    public CheckIn_FamilyMemberListAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.radio_familytlist_row, parent, false);

        return new CheckIn_FamilyMemberListAdapter.MyViewHolder(itemView);
    }
    public int getItemViewType(int position) {
        return position;
    }
    @Override
    public void onBindViewHolder(final CheckIn_FamilyMemberListAdapter.MyViewHolder myViewHolder, final int position) {
        final FamilyArrayModel familylist = familyList.get(position);
        int consumerId = SharedPreference.getInstance(mContext).getIntValue("consumerId", 0);


        if (multiple) {

            /*if(update==0){
                int consumerId1 = SharedPreference.getInstance(mContext).getIntValue("consumerId", 0);
                if (consumerId1 == familylist.getId()) {
                    familylist.setCheck(true);

                }
            }*/

            myViewHolder.Checkmemeber.setVisibility(View.VISIBLE);

            myViewHolder.Checkmemeber.setText(familylist.getFirstName() + " " + familylist.getLastName());

            myViewHolder.name.setVisibility(View.GONE);


            myViewHolder.Checkmemeber.setOnCheckedChangeListener(null);

            //if true, your checkbox will be selected, else unselected
            myViewHolder.Checkmemeber.setChecked(familylist.isCheck());

            Config.logV("Checkin@@@"+familylist.getFirstName()+"status-----"+familylist.isCheck());

        } else {
            myViewHolder.Checkmemeber.setVisibility(View.GONE);
            myViewHolder.name.setVisibility(View.VISIBLE);
            myViewHolder.name.setText(familylist.getFirstName() + " " + familylist.getLastName());
           /* if (memId == familylist.getId()) {
                familylist.setCheck(true);
            }*/


          //  Config.logV("Checkin@@@"+familylist.getFirstName()+"status-----"+familylist.isCheck());
            myViewHolder.name.setChecked(familylist.isCheck());


        }
        myViewHolder.name.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (lastCheckedRB != null) {
                    lastCheckedRB.setChecked(false);
                }
                //store the clicked radiobutton
                lastCheckedRB = myViewHolder.name;
                Config.logV("lastCheckedRB------@@@@--------------" + lastCheckedRB);



                    familyList.get(position).setCheck(isChecked);


              //  familyList.get(position).setCheck(isChecked);
                //familyList.get(myViewHolder.getAdapterPosition()).setCheck(isChecked);
                CheckinFamilyMember.changeMemberName(myViewHolder.name.getText().toString(), familylist.getId());
            }
        });

        myViewHolder.Checkmemeber.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Config.logV("Family @@@@" + isChecked);


                //familylist.setCheck(isChecked);
                familyList.get(myViewHolder.getAdapterPosition()).setCheck(isChecked);

                checkedfamilyList.clear();
                for (int i = 0; i < familyList.size(); i++) {

                    if (familyList.get(i).isCheck()) {
                        FamilyArrayModel family = new FamilyArrayModel();
                        family.setId(familyList.get(i).getId());
                        family.setFirstName(familyList.get(i).getFirstName());
                        family.setLastName(familyList.get(i).getLastName());
                        family.setCheck(true);
                        checkedfamilyList.add(family);
                    }
                }
                CheckinFamilyMember.CheckedFamilyList(checkedfamilyList);

                Config.logV("Check Family List-------------"+checkedfamilyList.size());
                if(checkedfamilyList.size()>0){
                    changemem.setBackground(mContext.getResources().getDrawable(R.drawable.roundedrect_blue));
                    changemem.setTextColor(mContext.getResources().getColor(R.color.app_background));
                    changemem.setEnabled(true);
                }else{
                    changemem.setBackground(mContext.getResources().getDrawable(R.drawable.btn_checkin_grey));
                    changemem.setTextColor(mContext.getResources().getColor(R.color.button_grey));
                    changemem.setEnabled(false);
                }


                /*if (isChecked) {
                    myViewHolder.Checkmemeber.setChecked(true);
                    familylist.setCheck(true);

                } else {
                    myViewHolder.Checkmemeber.setChecked(false);
                    familylist.setCheck(false);

                }*/


            }
        });


        //  myViewHolder.Checkmemeber.setChecked(familylist.isCheck());


        Config.logV("Family @@@@" + familylist.getFirstName() + " #####" + familylist.isCheck());

    }

    public ArrayList<FamilyArrayModel> onItemSelected() {

        ArrayList<FamilyArrayModel> familyArrayList = new ArrayList<FamilyArrayModel>();


        for (int i = 0; i < familyList.size(); i++) {
            FamilyArrayModel family1 = new FamilyArrayModel();
            family1.setFirstName(familyList.get(i).getFirstName());
            family1.setLastName(familyList.get(i).getLastName());
            family1.setId(familyList.get(i).getId());
            family1.setCheck(familyList.get(i).isCheck());
            familyArrayList.add(family1);

        }

        return familyArrayList;
    }

    @Override
    public int getItemCount() {
        return familyList.size();
    }


}