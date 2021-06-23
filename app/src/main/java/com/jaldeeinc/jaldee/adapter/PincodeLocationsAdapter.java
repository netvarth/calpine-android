package com.jaldeeinc.jaldee.adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RadioButton;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.jaldeeinc.jaldee.Interface.IFamillyListSelected;
import com.jaldeeinc.jaldee.R;
import com.jaldeeinc.jaldee.common.Config;
import com.jaldeeinc.jaldee.model.FamilyArrayModel;

import java.util.Map;

public class PincodeLocationsAdapter extends RecyclerView.Adapter<PincodeLocationsAdapter.MyViewHolder> {
    Context mContext;
    JsonArray pincodeLocations;
    Activity activity;
    private RadioButton lastCheckedRB = null;
    private IFamillyListSelected iFamillyListSelected;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public RadioButton location;

        public MyViewHolder(View view) {
            super(view);
            location = (RadioButton) view.findViewById(R.id.rb_location);
            Typeface font = Typeface.createFromAsset(mContext.getAssets(),  "fonts/JosefinSans-SemiBold.ttf");
            location.setTypeface(font);
        }
    }

    public PincodeLocationsAdapter(Context mContext, Activity mActivity, JsonArray pincodeLocations, IFamillyListSelected iFamillyListSelected) {
        this.mContext = mContext;
        this.activity = mActivity;
        this.pincodeLocations = pincodeLocations;
        this.iFamillyListSelected = iFamillyListSelected;
        pincodeLocations.get(0).getAsJsonObject().addProperty("isCheck", true);
    }

    @NonNull
    @Override
    public PincodeLocationsAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.radio_pincode_location_row, parent, false);

        return new PincodeLocationsAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final PincodeLocationsAdapter.MyViewHolder myViewHolder, final int position) {
        final JsonObject locationObject = pincodeLocations.get(position).getAsJsonObject();
        myViewHolder.location.setVisibility(View.VISIBLE);
        myViewHolder.location.setText(locationObject.get("Name").getAsString()+", "+locationObject.get("State").getAsString());
        if(locationObject.get("isCheck") != null && locationObject.get("isCheck").getAsBoolean()) {
            myViewHolder.location.setChecked(true);

            //store the clicked radiobutton
            lastCheckedRB = myViewHolder.location;

            iFamillyListSelected.SelectedPincodeLocation(locationObject);
        }

        myViewHolder.location.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (lastCheckedRB != null) {
                    lastCheckedRB.setChecked(false);
                }
                //store the clicked radiobutton
                lastCheckedRB = myViewHolder.location;
                Config.logV("lastCheckedRB------@@@@--------------" + lastCheckedRB);


                pincodeLocations.get(position).getAsJsonObject().addProperty("isCheck", isChecked);
                iFamillyListSelected.SelectedPincodeLocation(pincodeLocations.get(position).getAsJsonObject());


                //  familyList.get(position).setCheck(isChecked);
                //familyList.get(myViewHolder.getAdapterPosition()).setCheck(isChecked);
                // CheckinFamilyMember.changeMemberName(myViewHolder.name.getText().toString(), familylist.getId());
               // iFamillyListSelected.changeMemberName(myViewHolder.name.getText().toString(), familylist);
            }
        });

    }

    @Override
    public int getItemCount() {
        return pincodeLocations.size(); }

}
