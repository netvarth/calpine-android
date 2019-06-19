package com.nv.youneverwait.adapter;

import android.content.Context;

import com.nv.youneverwait.callback.AdapterCallback;
import com.nv.youneverwait.model.Domain_Spinner;
import com.nv.youneverwait.model.SearchModel;
import com.nv.youneverwait.response.RefinedFilters;

import java.util.ArrayList;

import android.app.Activity;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RatingBar;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;

import com.google.gson.Gson;
import com.nv.youneverwait.R;
import com.nv.youneverwait.callback.FilterAdapterCallback;
import com.nv.youneverwait.common.Config;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

/**
 * Created by sharmila on 7/6/19.
 */

public class MoreFilterAdapter extends RecyclerView.Adapter<MoreFilterAdapter.MyViewHolder> {

    private List<RefinedFilters> mFilterList;
    Context mContext;
    AdapterCallback filterAdapterCallback;


    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView tv_name;
        View mView;
        LinearLayout LexpandView, Ldisplay;


        public MyViewHolder(View view) {
            super(view);

            Ldisplay = (LinearLayout) view.findViewById(R.id.Ldisplay);
            tv_name = (TextView) view.findViewById(R.id.name);
            mView = (View) view.findViewById(R.id.mView);
            LexpandView = (LinearLayout) view.findViewById(R.id.LexpandView);
        }
    }

    ArrayList<String> keyFormula = new ArrayList<>();
    Activity activity;
    RecyclerView recyclview_popup;
    String passFormulaApi = "";
    String domainSelect;
    String subDomianSelect;


    ArrayList<String> passFormula = new ArrayList<>();

    public MoreFilterAdapter(List<RefinedFilters> mFilterList, Context mContext, Activity mActivity, AdapterCallback filterAdapterCallback, RecyclerView recyclepop, String passFormula1, String domainSelected, String subDomain) {
        this.mContext = mContext;
        this.mFilterList = mFilterList;
        this.activity = mActivity;
        this.filterAdapterCallback = filterAdapterCallback;
        this.recyclview_popup = recyclepop;
        this.passFormulaApi = passFormula1;
        this.domainSelect = domainSelected;
        this.subDomianSelect = subDomain;
        Config.logV("Domain SELECTED" + domainSelected);
        if (!domainSelected.equalsIgnoreCase("Select")) {
            String query = "sector:'" + domainSelected + "'";
            passFormula.add(query);

        }


        if (!subDomianSelect.equalsIgnoreCase("Select")) {
            String query = "sub_sector:'" + subDomianSelect + "'";
            passFormula.add(query);
            Config.logV("PRINT SUB ADDDED "+query);

        }

        keyFormula.add("sector");
        keyFormula.add("sub_sector");
        /*for(int i=0;i<mFilterList.size();i++){
            Config.logV("PRINT @@DATTYPE"+mFilterList.get(i).getDataType());
            keyFormula.add(mFilterList.get(i).getCloudSearchIndex());
            if(mFilterList.get(i).getDataType().equalsIgnoreCase("Boolean")){
                for(int j=0;j<mFilterList.get(i).getCloudIndexvalue().size();j++) {
                    keyFormula.add(mFilterList.get(i).getCloudIndexvalue().get(j).toString());
                }
            }
        }*/


    }

    @Override
    public MoreFilterAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.morefilter_row, parent, false);

        return new MoreFilterAdapter.MyViewHolder(itemView);
    }

    @Override
    public int getItemViewType(int position)
    {
        return position;
    }
    @Override
    public void onBindViewHolder(final MoreFilterAdapter.MyViewHolder myViewHolder, final int position) {
        final RefinedFilters filterList = mFilterList.get(position);



        myViewHolder.tv_name.setText(filterList.getDisplayName());

        if (position == mFilterList.size() - 1) {
            myViewHolder.mView.setVisibility(View.GONE);
        } else {
            myViewHolder.mView.setVisibility(View.VISIBLE);
        }




        myViewHolder.Ldisplay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Config.logV("filterList.isExpand()" + filterList.isExpand()+"DISPLAY"+filterList.getDataType()+"DDD"+filterList.getDisplayName());
                if (!filterList.isExpand()) {
                    filterList.setExpand(true);

                    if (filterList.getDataType().equalsIgnoreCase("EnumList") || filterList.getDataType().equalsIgnoreCase("Enum")|| filterList.getDataType().equalsIgnoreCase("Gender")) {
                        myViewHolder.LexpandView.setVisibility(View.VISIBLE);
                        myViewHolder.LexpandView.removeAllViews();


                        if (filterList.getEnumeratedConstants() instanceof String) {
                            Config.logV("String ");
                        } else {

                            Config.logV("Array " + new Gson().toJson(filterList.getEnumeratedConstants()));
                            String resp = new Gson().toJson(filterList.getEnumeratedConstants());

                            boolean fAvailable=false;
                            for(int i=0;i<keyFormula.size();i++){
                                if(keyFormula.get(i).toString().equalsIgnoreCase(filterList.getCloudSearchIndex())){
                                    //No neeed to add
                                    fAvailable=true;
                                    break;
                                }
                            }

                            if(!fAvailable){
                                keyFormula.add(filterList.getCloudSearchIndex().replace("*", "1"));
                            }
                            try {
                                JSONArray jsonArray = new JSONArray(resp);
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    String valueJson = jsonArray.getString(i);
                                    Log.e("json", i + "=" + valueJson);
                                    final JSONObject jsonObj = new JSONObject(valueJson);
                                    final CheckBox cb = new CheckBox(v.getContext());
                                    cb.setText(jsonObj.getString("displayName"));
                                    final String name = jsonObj.getString("name");
                                    cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                                        @Override
                                        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                            if (isChecked) {



                                                for(String str : passFormula) {

                                                    Config.logV("PRINT  SCHECKNBOX@ ####@@@@@@@@@@@@@@"+str);
                                                }

                                                passFormula.add(filterList.getCloudSearchIndex().replace("*", "1") + ": '" + name + "' ");
                                                filterAdapterCallback.onMethodQuery(passFormula, keyFormula);


                                            } else {
                                                for (int i = 0; i < passFormula.size(); i++) {
                                                    if (passFormula.get(i).toString().contains(name)) {
                                                        Config.logV("Remove @@@@@@@@@@@@@@@@@@@" + passFormula.get(i).toString());
                                                        passFormula.remove(i);
                                                        filterAdapterCallback.onMethodQuery(passFormula, keyFormula);
                                                    }
                                                }
                                            }
                                        }
                                    });
                                    myViewHolder.LexpandView.addView(cb);


                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }


                        }

                    } else if (filterList.getDataType().equalsIgnoreCase("Rating")) {
                        myViewHolder.LexpandView.setVisibility(View.VISIBLE);

                        boolean fAvailable=false;
                        for(int i=0;i<keyFormula.size();i++){
                            if(keyFormula.get(i).toString().equalsIgnoreCase(filterList.getCloudSearchIndex())){
                                //No neeed to add
                                fAvailable=true;
                                break;
                            }
                        }

                        if(!fAvailable){
                            keyFormula.add(filterList.getCloudSearchIndex());
                        }


                        RatingBar ratingBar = new RatingBar(mContext);

                        // linearLayout.addView(ratingBar);
                        ratingBar.setNumStars(5);
                        ratingBar.setStepSize((float) 0.5);
                        ratingBar.setScaleX((float) .75);
                        ratingBar.setScaleY((float) .75);



                        if (myViewHolder.LexpandView != null) {
                            myViewHolder.LexpandView.addView(ratingBar);
                        }

                        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
                            @Override
                            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                                float rateValue = ratingBar.getRating();
                                Config.logV("Rating Vlue @@@@@@@@" + rateValue);

                                Config.logV("Rating FFFF@@@@@@@@@@@@@@" + filterList.getCloudSearchIndex());


                                for (int i = 0; i < passFormula.size(); i++) {

                                    if (passFormula.get(i).toString().contains(filterList.getCloudSearchIndex().toString())) {
                                        passFormula.remove(i);
                                    }
                                }

                                passFormula.add(filterList.getCloudSearchIndex() + ": '" + rateValue + "' ");

                                filterAdapterCallback.onMethodQuery(passFormula, keyFormula);
                            }
                        });


                    } else if (filterList.getDataType().equalsIgnoreCase("Boolean")) {
                        myViewHolder.LexpandView.setVisibility(View.VISIBLE);
                        Config.logV("Rating @@@@@@@@@@@@@@");


                        for (int i = 0; i < filterList.getItemName().size(); i++) {
                            final Switch sw = new Switch(mContext);
                            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                            layoutParams.setMargins(15, 15, 15, 15);
                            sw.setLayoutParams(layoutParams);
                            sw.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
                            sw.setText(filterList.getItemName().get(i).toString());
                           // keyFormula.add(filterList.getCloudIndexvalue().get(i).toString().replace("*", "1"));

                            boolean fAvailable=false;
                            for(int j=0;j<keyFormula.size();j++){
                                if(keyFormula.get(j).toString().equalsIgnoreCase(filterList.getCloudIndexvalue().get(i).toString())){
                                    //No neeed to add

                                    fAvailable=true;
                                    break;
                                }
                            }

                            if(!fAvailable){
                                keyFormula.add(filterList.getCloudIndexvalue().get(i).toString().replace("*", "1"));
                            }
                            // Add Switch to LinearLayout
                            if (myViewHolder.LexpandView != null) {
                                myViewHolder.LexpandView.addView(sw);
                            }

                            final int finalI = i;
                            sw.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                                @Override
                                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                    if (isChecked) {


                                        passFormula.add(filterList.getCloudIndexvalue().get(finalI).toString().replace("*", "1") + ":  '1'");

                                        filterAdapterCallback.onMethodQuery(passFormula, keyFormula);
                                    } else {

                                        for (int i = 0; i < passFormula.size(); i++) {


                                            if (passFormula.get(i).toString().contains(filterList.getCloudIndexvalue().get(finalI).toString().replace("*", "1"))) {

                                                passFormula.remove(i);
                                                filterAdapterCallback.onMethodQuery(passFormula, keyFormula);

                                            }
                                        }
                                    }
                                }
                            });
                        }





                    } else if (filterList.getDataType().equalsIgnoreCase("Spinner")) {
                        myViewHolder.LexpandView.setVisibility(View.VISIBLE);
                        // keyFormula.add("sector");

                        Config.logV("UPDATE SPINNER @@@@@@@@@@@@@@@" + domainSelect);
                        Spinner spinner = new Spinner(v.getContext(), Spinner.MODE_DIALOG);
                        ArrayAdapter<Domain_Spinner> adapter = new ArrayAdapter<Domain_Spinner>(v.getContext(), android.R.layout.simple_spinner_item, (ArrayList<Domain_Spinner>) filterList.getEnumeratedConstants()) {
                            @Override
                            public boolean isEnabled(int position) {
                                if (position == 0) {
                                    // Disable the first item from Spinner
                                    // First item will be use for hint
                                    return false;
                                } else {
                                    return true;
                                }
                            }

                            @Override
                            public View getDropDownView(int position, View convertView,
                                                        ViewGroup parent) {
                                View view = super.getDropDownView(position, convertView, parent);
                                TextView tv = (TextView) view;
                                if (position == 0) {
                                    // Set the hint text color gray
                                    tv.setTextColor(Color.GRAY);
                                } else {
                                    tv.setTextColor(Color.BLACK);
                                }
                                return view;
                            }
                        };

                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);


                        final int pos = getIndex((ArrayList<Domain_Spinner>) filterList.getEnumeratedConstants(), domainSelect);


                        //spinner.setOnItemSelectedListener(new CustomSpinner(filterAdapterCallback, recyclview_popup,pos,passFormula,keyFormula));
                        spinner.setAdapter(adapter);

                        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                if (position > 0 && position != pos) {
                                    Domain_Spinner spinnerSelect = (Domain_Spinner) parent.getSelectedItem();
                                    Config.logV("Spinner Data@@@@@@@@@@" + spinnerSelect.getDomain());
                                    String query = "sector:'" + spinnerSelect.getDomain() + "'";

                                    for (int i = 0; i < passFormula.size(); i++) {
                                        //if (passFormula.get(i).contains("sector")) {

                                            passFormula.remove(i);
                                       // }
                                    }

                                    passFormula.add(query);

                                    filterAdapterCallback.onMethodFilterRefined(query, recyclview_popup, spinnerSelect.getDomain());

                                    for (int i = 0; i < passFormula.size(); i++) {

                                        if(passFormula.get(i).toString().contains("sub_sector")){

                                            passFormula.remove(i);
                                        }
                                    }
                                    filterAdapterCallback.onMethodQuery(passFormula, keyFormula);

                                    // Config.logV("PRINT VAL FORMULA@@WWWWWWWW" + query);

                                }
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> parent) {

                            }
                        });
                        if (domainSelect.equalsIgnoreCase("Select")) {
                            spinner.setSelection(0);
                        } else {
                            spinner.setSelection(pos);
                        }
                        Config.logV("Select Pos @@@@@@@@@@@@@@@@@" + pos);
                        myViewHolder.LexpandView.addView(spinner);


                    } else if (filterList.getDataType().equalsIgnoreCase("Spinner_subdomain")) {
                        myViewHolder.LexpandView.setVisibility(View.VISIBLE);


                        Spinner spinner = new Spinner(v.getContext(), Spinner.MODE_DIALOG);
                        ArrayAdapter<SearchModel> adapter = new ArrayAdapter<SearchModel>(v.getContext(), android.R.layout.simple_spinner_item, (ArrayList<SearchModel>) filterList.getEnumeratedConstants()) {
                            @Override
                            public boolean isEnabled(int position) {
                                if (position == 0) {
                                    // Disable the first item from Spinner
                                    // First item will be use for hint
                                    return false;
                                } else {
                                    return true;
                                }
                            }

                            @Override
                            public View getDropDownView(int position, View convertView,
                                                        ViewGroup parent) {
                                View view = super.getDropDownView(position, convertView, parent);
                                TextView tv = (TextView) view;
                                if (position == 0) {
                                    // Set the hint text color gray
                                    tv.setTextColor(Color.GRAY);
                                } else {
                                    tv.setTextColor(Color.BLACK);
                                }
                                return view;
                            }
                        };

                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                        final int pos = getIndexSubDomain((ArrayList<SearchModel>) filterList.getEnumeratedConstants(), subDomianSelect);

                       // spinner.setOnItemSelectedListener(new CustomSubDomainSpinner(filterAdapterCallback, recyclview_popup, passFormulaApi, pos));
                        spinner.setAdapter(adapter);
                        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                if (position > 0 && position != pos) {
                                    SearchModel spinnerSelect = (SearchModel) parent.getSelectedItem();



                                    String query="sub_sector:'"+spinnerSelect.getName()+"'";

                                    for (int i = 0; i < passFormula.size(); i++) {
                                     //   if (passFormula.get(i).contains("sub_sector")) {
                                        String splitsFormula[]=passFormula.get(i).toString().split(":");
                                        Config.logV("PRINT SUBSPINNERKK ##@@DD ##"+splitsFormula[0]+keyFormula.get(i).toString());
                                        if (splitsFormula[0].equalsIgnoreCase("sub_sector")) {

                                            passFormula.remove(i);
                                        }
                                    }


                                    passFormula.add(query);


                                    filterAdapterCallback.onMethodSubDomainFilter(query,recyclview_popup,spinnerSelect.getName());
                                    filterAdapterCallback.onMethodQuery(passFormula, keyFormula);

                                    // Config.logV("PRINT VAL FORMULA@@WWWWWWWW" + query);

                                    for(String str : passFormula) {

                                        Config.logV("PRINT  SUBSPINNERKK@ ####@@@@@@@@@@@@@@"+str);
                                    }

                                }
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> parent) {

                            }
                        });

                        if (subDomianSelect.equalsIgnoreCase("Select")) {
                            spinner.setSelection(0);
                        } else {
                            spinner.setSelection(pos);
                        }

                        myViewHolder.LexpandView.addView(spinner);


                    } else if (filterList.getDataType().equalsIgnoreCase("TEXT") || filterList.getDataType().equalsIgnoreCase("TEXT_MED")) {

                       // keyFormula.add(filterList.getCloudSearchIndex().replace("*", "1"));

                        boolean fAvailable=false;
                        for(int i=0;i<keyFormula.size();i++){
                            if(keyFormula.get(i).toString().equalsIgnoreCase(filterList.getCloudSearchIndex())){
                                //No neeed to add
                                fAvailable=true;
                                break;
                            }
                        }

                        if(!fAvailable){
                            keyFormula.add(filterList.getCloudSearchIndex().replace("*", "1"));
                        }


                        myViewHolder.LexpandView.setVisibility(View.VISIBLE);

                        EditText editText = new EditText(mContext);
                        editText.setTextSize(15);
                        editText.setHint(filterList.getDisplayName());
                        myViewHolder.LexpandView.addView(editText);
                    }
                } else {
                    myViewHolder.LexpandView.setVisibility(View.GONE);
                    myViewHolder.LexpandView.removeAllViews();
                    filterList.setExpand(false);
                }


            }
        });


    }

    private int getIndex(ArrayList<Domain_Spinner> spinner, String myString) {

        int index = 0;

        for (int i = 0; i < spinner.size(); i++) {
            if (spinner.get(i).getDomain().equals(myString)) {
                index = i;
            }
        }
        return index;
    }

    private int getIndexSubDomain(ArrayList<SearchModel> spinner, String myString) {

        int index = 0;

        for (int i = 0; i < spinner.size(); i++) {
            if (spinner.get(i).getName().equals(myString)) {
                index = i;
            }
        }
        return index;
    }

    @Override
    public int getItemCount() {
        return mFilterList.size();
    }


}
