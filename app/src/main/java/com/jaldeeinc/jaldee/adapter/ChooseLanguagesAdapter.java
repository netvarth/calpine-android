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

import com.jaldeeinc.jaldee.R;
import com.jaldeeinc.jaldee.model.FamilyArrayModel;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ChooseLanguagesAdapter extends RecyclerView.Adapter<ChooseLanguagesAdapter.MyViewHolder> {
    Context mContext;
    Activity activity;
    private List<String> languageList = new ArrayList<>(List.of("Hindi", "Kannada", "Malayalam", "Tamil", "Telugu"));
    ArrayList<String> preferredLanguages = new ArrayList<>(), preferredLanguages1 = new ArrayList<>();


    public ChooseLanguagesAdapter(Context mContext, Activity mActivity, ArrayList<String> preferredLanguages) {
        this.mContext = mContext;
        this.activity = mActivity;
        this.preferredLanguages = preferredLanguages;
        this.preferredLanguages1.addAll(preferredLanguages);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public CheckBox cb_language;

        public MyViewHolder(View view) {
            super(view);
            cb_language = (CheckBox) view.findViewById(R.id.cb_language);
            Typeface font = Typeface.createFromAsset(mContext.getAssets(), "fonts/JosefinSans-SemiBold.ttf");
            cb_language.setTypeface(font);
        }
    }

    @Override
    public ChooseLanguagesAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.checkbox_select_preferred_languages, parent, false);

        return new ChooseLanguagesAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final ChooseLanguagesAdapter.MyViewHolder myViewHolder, final int position) {
        final String language = languageList.get(position);
        myViewHolder.cb_language.setVisibility(View.VISIBLE);
        myViewHolder.cb_language.setText(language);
        if (preferredLanguages1.stream().anyMatch(language::equalsIgnoreCase)) {
            myViewHolder.cb_language.setChecked(true);
        } else {
            myViewHolder.cb_language.setChecked(false);
        }
        myViewHolder.cb_language.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    if (!preferredLanguages1.stream().anyMatch(buttonView.getText().toString()::equalsIgnoreCase)) {
                        preferredLanguages1.add(buttonView.getText().toString());
                    }
                } else {
                    if (preferredLanguages1.stream().anyMatch(buttonView.getText().toString()::equalsIgnoreCase)) {
                        preferredLanguages1 = (ArrayList<String>) preferredLanguages1.stream().filter(s -> !s.equalsIgnoreCase(buttonView.getText().toString())).collect(Collectors.toList());
                    }

                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return languageList.size();
    }

    public ArrayList<String> onItemSelected() {

        /*ArrayList<FamilyArrayModel> familyArrayList = new ArrayList<FamilyArrayModel>();


        for (int i = 0; i < familyList.size(); i++) {
            FamilyArrayModel family1 = new FamilyArrayModel();
            family1.setFirstName(familyList.get(i).getFirstName());
            family1.setLastName(familyList.get(i).getLastName());
            family1.setId(familyList.get(i).getId());
            family1.setCheck(familyList.get(i).isCheck());
            familyArrayList.add(family1);

        }*/

        return preferredLanguages1;
    }

}
