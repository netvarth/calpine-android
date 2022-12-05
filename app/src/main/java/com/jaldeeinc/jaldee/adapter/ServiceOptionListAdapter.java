package com.jaldeeinc.jaldee.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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


public class ServiceOptionListAdapter extends RecyclerView.Adapter<ServiceOptionListAdapter.ViewHolder> {
    private ArrayList<QuestionnaireCheckbox> questionnaireCheckboxes;
    private RadioButton lastCheckedRB = null;
    private LinearLayout lastSelectedLl = null;
    private Context mContext;
    IServiceOption iServiceOptionListOptionChange;
    public ServiceOptionListAdapter(Context mContext, ArrayList<QuestionnaireCheckbox> questionnaireCheckboxes, IServiceOption iServiceOptionListOptionChange) {
        this.mContext = mContext;
        this.questionnaireCheckboxes = questionnaireCheckboxes;
        this.iServiceOptionListOptionChange = iServiceOptionListOptionChange;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.radio_service_option_list, parent, false);
        return new ViewHolder(v);

    }
    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {
        final QuestionnaireCheckbox checkbox = questionnaireCheckboxes.get(position);
        viewHolder.radioButton.setVisibility(View.VISIBLE);
        viewHolder.radioButton.setText(checkbox.getText());
        viewHolder.tvItemAmount.setVisibility(View.VISIBLE);
        if(checkbox.getPrice() != null && !checkbox.getPrice().equals("")) {
            viewHolder.tvItemAmount.setText("₹ " + Config.getAmountNoOrTwoDecimalPoints(checkbox.getPrice()));
            viewHolder.tvItemAmount.setVisibility(View.VISIBLE);
        }
        viewHolder.radioButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (lastCheckedRB != null) {
                    lastCheckedRB.setChecked(false);
                }
                //store the clicked radiobutton
                lastCheckedRB = viewHolder.radioButton;
                Config.logV("lastCheckedRB------@@@@--------------" + lastCheckedRB);

                questionnaireCheckboxes.get(position).setChecked(isChecked);
                iServiceOptionListOptionChange.radioListItemSelected(questionnaireCheckboxes.get(position).getText(), questionnaireCheckboxes.get(position).getPrice());
                //iFamillyListSelected.SelectedPincodeLocation(pincodeLocations.get(position));

            }
        });
    }

    @Override
    public int getItemCount() {
        return questionnaireCheckboxes.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public RadioButton radioButton;
        public TextView tvItemAmount;
        public LinearLayout llRadioButton;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            radioButton = itemView.findViewById(R.id.rb_radioButton);
            tvItemAmount = itemView.findViewById(R.id.tv_item_amount);
            llRadioButton = itemView.findViewById(R.id.ll_radioButton);
        }
    }
}
