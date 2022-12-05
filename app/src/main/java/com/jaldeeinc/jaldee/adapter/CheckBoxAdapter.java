package com.jaldeeinc.jaldee.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.jaldeeinc.jaldee.Interface.IServiceOption;
import com.jaldeeinc.jaldee.R;
import com.jaldeeinc.jaldee.common.Config;
import com.jaldeeinc.jaldee.model.QuestionnaireCheckbox;

import java.util.ArrayList;

public class CheckBoxAdapter extends RecyclerView.Adapter<CheckBoxAdapter.ViewHolder> {

    ArrayList<QuestionnaireCheckbox> checkboxList = new ArrayList<>();
    public Context context;
    int maxAnswerable;
    private RadioButton lastCheckedRB = null;
    private IServiceOption iServiceOptionListOptionChange;

    public CheckBoxAdapter(ArrayList<QuestionnaireCheckbox> checkBoxList, Context context) {
        this.checkboxList = checkBoxList;
        this.context = context;
    }

    public CheckBoxAdapter(ArrayList<QuestionnaireCheckbox> checkBoxList, int maxAnswerable, Context context, IServiceOption iServiceOptionListOptionChange) {
        this.checkboxList = checkBoxList;
        this.context = context;
        this.maxAnswerable = maxAnswerable;
        this.iServiceOptionListOptionChange = iServiceOptionListOptionChange;
    }

    public CheckBoxAdapter(ArrayList<QuestionnaireCheckbox> checkBoxList, int maxAnswerable, Context context) {
        this.checkboxList = checkBoxList;
        this.context = context;
        this.maxAnswerable = maxAnswerable;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.checkboxes_item, parent, false);
        return new ViewHolder(v);

    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {

        final QuestionnaireCheckbox checkbox = checkboxList.get(position);

        if (maxAnswerable == 1) {
            viewHolder.checkBox.setVisibility(View.GONE);
            viewHolder.radioButton.setVisibility(View.VISIBLE);
            viewHolder.radioButton.setText(checkbox.getText());
            viewHolder.radioButton.setChecked(checkbox.isChecked());
            if (checkbox.isChecked()) {
                lastCheckedRB = viewHolder.radioButton;
            }

        } else {
            viewHolder.checkBox.setVisibility(View.VISIBLE);
            viewHolder.radioButton.setVisibility(View.GONE);
            viewHolder.checkBox.setText(checkbox.getText());
            viewHolder.checkBox.setChecked(checkbox.isChecked());
        }
        viewHolder.tvItemAmount.setVisibility(View.VISIBLE);
        if (checkbox.getPrice() != null) {
            if (checkbox.isBase) {
                viewHolder.tvItemAmount.setText("₹" + Config.getAmountNoOrTwoDecimalPoints(checkbox.getPrice()));
                viewHolder.tvItemAmount.setVisibility(View.VISIBLE);
            } else {
                viewHolder.tvItemAmount.setText("+ ₹" + Config.getAmountNoOrTwoDecimalPoints(checkbox.getPrice()));
                viewHolder.tvItemAmount.setVisibility(View.VISIBLE);
            }
        }
        if (checkboxList.size() == position + 1) {
            viewHolder.llDivider.setVisibility(View.GONE);
        } else {
            viewHolder.llDivider.setVisibility(View.VISIBLE);
        }
        //


        viewHolder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

                checkboxList.get(position).setChecked(b);
                if (iServiceOptionListOptionChange != null) {
                    iServiceOptionListOptionChange.updateTotalPrice();
                }

//                try {
//                    notifyDataSetChanged();
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
            }
        });
        viewHolder.radioButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (lastCheckedRB != null) {
                    lastCheckedRB.setChecked(false);
                }
                //store the clicked radiobutton
                lastCheckedRB = viewHolder.radioButton;
                Config.logV("lastCheckedRB------@@@@--------------" + lastCheckedRB);

                checkboxList.get(position).setChecked(isChecked);
                if (iServiceOptionListOptionChange != null) {
                    iServiceOptionListOptionChange.updateTotalPrice();
                }
                //iFamillyListSelected.SelectedPincodeLocation(pincodeLocations.get(position));

            }
        });

    }

    @Override
    public int getItemCount() {
        return checkboxList.size();
    }

    public int getCheckedCount() {

        int count = 0;
        for (QuestionnaireCheckbox obj : checkboxList) {

            if (obj.isChecked()) {
                count++;
            }
        }

        return count;
    }

    public ArrayList<QuestionnaireCheckbox> getSelectedCheckboxes() {

        ArrayList<QuestionnaireCheckbox> selectedCheckboxes = new ArrayList<>();
        for (QuestionnaireCheckbox obj : checkboxList) {

            if (obj.isChecked()) {

                selectedCheckboxes.add(obj);

            }
        }

        return selectedCheckboxes;
    }


    public class ViewHolder extends RecyclerView.ViewHolder {

        public CheckBox checkBox;
        public RadioButton radioButton;

        public TextView tvItemAmount;
        public LinearLayout llDivider;

        public ViewHolder(@NonNull View itemView) {

            super(itemView);

            checkBox = itemView.findViewById(R.id.cb_checkBox);
            radioButton = itemView.findViewById(R.id.rb_radioButton);
            tvItemAmount = itemView.findViewById(R.id.tv_item_amount);
            llDivider = itemView.findViewById(R.id.ll_divider);

        }
    }
}
