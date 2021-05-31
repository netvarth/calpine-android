package com.jaldeeinc.jaldee.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.jaldeeinc.jaldee.Interface.IEditAddress;
import com.jaldeeinc.jaldee.R;
import com.jaldeeinc.jaldee.custom.CustomTextViewMedium;
import com.jaldeeinc.jaldee.custom.CustomTextViewSemiBold;
import com.jaldeeinc.jaldee.model.Address;
import com.jaldeeinc.jaldee.model.QuestionnaireCheckbox;
import com.jaldeeinc.jaldee.response.Questionnaire;

import java.util.ArrayList;

public class CheckBoxAdapter extends RecyclerView.Adapter<CheckBoxAdapter.ViewHolder> {

    ArrayList<QuestionnaireCheckbox> checkboxList = new ArrayList<>();
    public Context context;

    public CheckBoxAdapter(ArrayList<QuestionnaireCheckbox> checkBoxList, Context context) {
        this.checkboxList = checkBoxList;
        this.context = context;
    }

    @NonNull
    @Override
    public CheckBoxAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.checkboxes_item, parent, false);
        return new CheckBoxAdapter.ViewHolder(v);

    }

    @Override
    public void onBindViewHolder(@NonNull CheckBoxAdapter.ViewHolder viewHolder, int position) {

        final QuestionnaireCheckbox checkbox = checkboxList.get(position);

        viewHolder.checkBox.setText(checkbox.getText());
        viewHolder.checkBox.setChecked(checkbox.isChecked());

        viewHolder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

                checkboxList.get(position).setChecked(b);
                notifyDataSetChanged();
            }
        });

    }

    @Override
    public int getItemCount() {
        return checkboxList.size();
    }

    public int getCheckedCount(){

        int count = 0;
        for (QuestionnaireCheckbox obj: checkboxList) {

            if (obj.isChecked()){
                count++;
            }
        }

        return count;
    }

    public ArrayList<QuestionnaireCheckbox> getSelectedCheckboxes(){

        ArrayList<QuestionnaireCheckbox> selectedCheckboxes = new ArrayList<>();
        for (QuestionnaireCheckbox obj: checkboxList) {

            if (obj.isChecked()){

                selectedCheckboxes.add(obj);

            }
        }

        return selectedCheckboxes;
    }


    public class ViewHolder extends RecyclerView.ViewHolder {

        public CheckBox checkBox;

        public ViewHolder(@NonNull View itemView) {

            super(itemView);

            checkBox = itemView.findViewById(R.id.cb_checkBox);

        }
    }

}
