package com.nv.youneverwait.adapter;

import android.content.Context;

import com.nv.youneverwait.callback.AdapterCallback;
import com.nv.youneverwait.model.Domain_Spinner;
import com.nv.youneverwait.model.SearchModel;
import com.nv.youneverwait.response.RefinedFilters;

import java.util.ArrayList;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RatingBar;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TableRow;
import android.widget.TextView;

import com.google.gson.Gson;
import com.nv.youneverwait.R;
import com.nv.youneverwait.callback.FilterAdapterCallback;
import com.nv.youneverwait.common.Config;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Collections;
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

    String domainNAme="";

    ArrayList<String> passFormula = new ArrayList<>();

    ArrayList<String> passedFormulaArray = new ArrayList<>();

    public MoreFilterAdapter(List<RefinedFilters> mFilterList, Context mContext, Activity mActivity, AdapterCallback filterAdapterCallback, RecyclerView recyclepop, String passFormula1, String domainSelected, String subDomain, ArrayList<String> passedFormulaArray) {
        this.mContext = mContext;
        this.mFilterList = mFilterList;
        this.activity = mActivity;
        this.filterAdapterCallback = filterAdapterCallback;
        this.recyclview_popup = recyclepop;
        this.passFormulaApi = passFormula1;
        this.domainSelect = domainSelected;
        this.subDomianSelect = subDomain;
        this.passedFormulaArray = passedFormulaArray;

        for (int i = 0; i < passedFormulaArray.size(); i++) {
            passFormula.add(passedFormulaArray.get(i));
        }
        Config.logV("Domain SELECTED" + domainSelected);
        if (!domainSelected.equalsIgnoreCase("Select")) {
            String query = "sector:'" + domainSelected + "'";
            passFormula.add(query);

        }


        if (!subDomianSelect.equalsIgnoreCase("Select")) {
            String query = "sub_sector:'" + subDomianSelect + "'";
            passFormula.add(query);
            Config.logV("PRINT SUB ADDDED " + query);

        }


        keyFormula.add("sector");
        keyFormula.add("sub_sector");

        for (int i = 0; i < mFilterList.size(); i++) {
            if (mFilterList.get(i).getDisplayName().equalsIgnoreCase("Other Filter")) {
                Collections.swap(mFilterList, i, mFilterList.size() - 1);
            }
        }


    }

    @Override
    public MoreFilterAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.morefilter_row, parent, false);

        return new MoreFilterAdapter.MyViewHolder(itemView);
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public void onBindViewHolder(final MoreFilterAdapter.MyViewHolder myViewHolder, final int position) {
        final RefinedFilters filterList = mFilterList.get(position);


        Typeface tyface = Typeface.createFromAsset(mContext.getAssets(),
                "fonts/Montserrat_Bold.otf");
        myViewHolder.tv_name.setTypeface(tyface);

        myViewHolder.tv_name.setText(filterList.getDisplayName());

        if (position == mFilterList.size() - 1) {
            myViewHolder.mView.setVisibility(View.GONE);
        } else {
            myViewHolder.mView.setVisibility(View.VISIBLE);
        }


        myViewHolder.Ldisplay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


               Config.logV("RATING $$$$$$$$$$$$$$$$$$$$$$$");
                //Config.logV("filterList.isExpand()" + filterList.isExpand()+"DISPLAY"+filterList.getDataType()+"DDD"+filterList.getDisplayName());
                if (!filterList.isExpand()) {
                    filterList.setExpand(true);

                    Config.logV("RATING $$$$$$$$$$$FFFFFFF$$$$$$$$$$$$");

                    if (filterList.getDataType().equalsIgnoreCase("EnumList") || filterList.getDataType().equalsIgnoreCase("Enum") || filterList.getDataType().equalsIgnoreCase("Gender")) {
                        myViewHolder.LexpandView.setVisibility(View.VISIBLE);
                        myViewHolder.LexpandView.removeAllViews();


                        if (filterList.getEnumeratedConstants() instanceof String) {
                            Config.logV("String ");
                        } else {

                            Config.logV("Array " + new Gson().toJson(filterList.getEnumeratedConstants()));
                            String resp = new Gson().toJson(filterList.getEnumeratedConstants());

                            boolean fAvailable = false;
                            for (int i = 0; i < keyFormula.size(); i++) {
                                if (keyFormula.get(i).toString().equalsIgnoreCase(filterList.getCloudSearchIndex().replace("*", "1"))) {
                                    //No neeed to add
                                    fAvailable = true;
                                    break;
                                }
                            }

                            if (!fAvailable) {
                                keyFormula.add(filterList.getCloudSearchIndex().replace("*", "1"));
                            }
                            try {
                                JSONArray jsonArray = new JSONArray(resp);

                                if (jsonArray.length() > 5) {
                                    funCheckBoxMore(jsonArray, mContext, myViewHolder.LexpandView, filterList, 5);
                                } else {
                                    funCheckBoxMore(jsonArray, mContext, myViewHolder.LexpandView, filterList, jsonArray.length());
                                }


                            } catch (JSONException e) {
                                e.printStackTrace();
                            }


                        }

                    } else if (filterList.getDataType().equalsIgnoreCase("Rating")) {
                        Config.logV("RATING @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
                        myViewHolder.LexpandView.setVisibility(View.VISIBLE);

                        boolean fAvailable = false;
                        for (int i = 0; i < keyFormula.size(); i++) {
                            if (keyFormula.get(i).toString().equalsIgnoreCase(filterList.getCloudSearchIndex())) {
                                //No neeed to add
                                fAvailable = true;
                                break;
                            }
                        }

                        if (!fAvailable) {
                            keyFormula.add(filterList.getCloudSearchIndex());
                        }


                        final LinearLayout parent = new LinearLayout(mContext);

                        parent.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                        parent.setOrientation(LinearLayout.HORIZONTAL);
                        parent.setGravity(Gravity.CENTER);

                        //final RatingBar ratingBar = new RatingBar(mContext);
                      //  final RatingBar ratingBar = new RatingBar(mContext, null, R.style.ImageRatingBar);
                        ContextThemeWrapper contextThemeWrapper = new ContextThemeWrapper(mContext, R.style.ImageRatingBar);
                        final RatingBar ratingBar = new RatingBar(contextThemeWrapper, null, 0);

                        // linearLayout.addView(ratingBar);
                        ratingBar.setNumStars(5);
                        ratingBar.setStepSize((float) 0.5);
                        ratingBar.setScaleX((float) .75);
                        ratingBar.setScaleY((float) .75);


                        LinearLayout.LayoutParams layoutParams1=new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                        layoutParams1.gravity=Gravity.CENTER;
                        ratingBar.setLayoutParams(layoutParams1);


                        for (int j = 0; j < passFormula.size(); j++) {
                            String splitsFormula[]=passFormula.get(j).toString().split(":");
                            Config.logV("PRINT SUBSPINNERKK ##@@DD ##"+splitsFormula[0]+splitsFormula[1]);
                            if (splitsFormula[0].equalsIgnoreCase("rating")) {

                                float val=Float.parseFloat(splitsFormula[1].toString().replace("'",""));
                                ratingBar.setRating(val);
                            }
                        }


                        final ImageView img=new ImageView(mContext);
                        if (parent!= null) {



                            img.setImageDrawable(mContext.getResources().getDrawable(R.drawable.close));

                            // img.setImageResource(R.drawable.close);
                            LinearLayout.LayoutParams layoutParams=new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                            layoutParams.gravity=Gravity.CENTER;
                            layoutParams.setMargins(0,-15,0,0);
                            img.setLayoutParams(layoutParams);

                            parent.addView(ratingBar);
                            parent.addView(img);
                            img.setVisibility(View.GONE);

                            img.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    ratingBar.setRating(0);
                                    for (int i = 0; i < passFormula.size(); i++) {

                                        if (passFormula.get(i).toString().contains(filterList.getCloudSearchIndex().toString())) {
                                            passFormula.remove(i);
                                        }
                                    }


                                    filterAdapterCallback.onMethodQuery(passFormula, keyFormula);

                                    img.setVisibility(View.GONE);
                                }
                            });
                        }




                        if (myViewHolder.LexpandView != null) {
                            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams)myViewHolder.LexpandView.getLayoutParams();
                            params.setMargins(0, 5, 0, 5);

                            myViewHolder.LexpandView.setLayoutParams(params);
                            myViewHolder.LexpandView.setPadding(0,0,0,0);
                            myViewHolder.LexpandView.addView(parent);
                        }




                        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
                            @Override
                            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                                float rateValue = ratingBar.getRating();
                                Config.logV("Rating Vlue @@@@@@@@" + rateValue);

                                Config.logV("Rating FFFF@@@@@@@@@@@@@@" + filterList.getCloudSearchIndex());
                                img.setVisibility(View.VISIBLE);

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

                            boolean fAvailable = false;
                            for (int j = 0; j < keyFormula.size(); j++) {
                                if (keyFormula.get(j).toString().equalsIgnoreCase(filterList.getCloudIndexvalue().get(i).toString())) {
                                    //No neeed to add

                                    fAvailable = true;
                                    break;
                                }
                            }

                            if (!fAvailable) {
                                keyFormula.add(filterList.getCloudIndexvalue().get(i).toString().replace("*", "1"));
                            }


                            for (int j = 0; j < passFormula.size(); j++) {
                                if (passFormula.get(j).toString().contains(filterList.getCloudIndexvalue().get(i).toString().replace("*", "1"))) {

                                    sw.setChecked(true);
                                }
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

                                    domainNAme= spinnerSelect.getDomain();
                                   /* for (int i = 0; i < passFormula.size(); i++) {
                                        //if (passFormula.get(i).contains("sector")) {

                                            passFormula.remove(i);
                                       // }
                                    }

                                    passFormula.add(query);*/

                                    filterAdapterCallback.onMethodFilterRefined(query, recyclview_popup, spinnerSelect.getDomain());

                                   /* for (int i = 0; i < passFormula.size(); i++) {

                                        if(passFormula.get(i).toString().contains("sub_sector")){

                                            passFormula.remove(i);
                                        }
                                    }*/

                                    passFormula = new ArrayList<>();
                                    passFormula.add(query);


                                    filterAdapterCallback.onMethodQuery(passFormula, keyFormula);


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


                                    String query = "sub_sector:'" + spinnerSelect.getName() + "'";

                                    for (int i = 0; i < passFormula.size(); i++) {
                                        //   if (passFormula.get(i).contains("sub_sector")) {
                                        String splitsFormula[] = passFormula.get(i).toString().split(":");
                                        Config.logV("PRINT SUBSPINNERKK ##@@DD ##" + splitsFormula[0] + keyFormula.get(i).toString());
                                        if (splitsFormula[0].equalsIgnoreCase("sub_sector")) {

                                            passFormula.remove(i);
                                        }
                                    }


                                    passFormula.add(query);

                                    Config.logV("DOMAIN NAME @@@@@@@@@@@@@@@@@@"+domainNAme+"Domain Select"+domainSelect);

                                    filterAdapterCallback.onMethodSubDomainFilter(query, recyclview_popup, spinnerSelect.getName(),domainSelect);
                                    filterAdapterCallback.onMethodQuery(passFormula, keyFormula);

                                    // Config.logV("PRINT VAL FORMULA@@WWWWWWWW" + query);

                                    for (String str : passFormula) {

                                        Config.logV("PRINT  SUBSPINNERKK@ ####@@@@@@@@@@@@@@" + str);
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

                        boolean fAvailable = false;
                        for (int i = 0; i < keyFormula.size(); i++) {
                            if (keyFormula.get(i).toString().equalsIgnoreCase(filterList.getCloudSearchIndex())) {
                                //No neeed to add
                                fAvailable = true;
                                break;
                            }
                        }

                        if (!fAvailable) {
                            keyFormula.add(filterList.getCloudSearchIndex());
                        }


                        myViewHolder.LexpandView.setVisibility(View.VISIBLE);

                        final EditText editText = new EditText(mContext);
                        editText.setTextSize(15);
                        editText.setHint(filterList.getDisplayName());
                        myViewHolder.LexpandView.addView(editText);
                        editText.setImeOptions(EditorInfo.IME_ACTION_DONE);
                        editText.setSingleLine();
                        editText.setMaxLines(1);

                        for (int j = 0; j < passFormula.size(); j++) {
                            String splitsFormula[] = passFormula.get(j).toString().split(":");

                            if (splitsFormula[0].equalsIgnoreCase(filterList.getCloudSearchIndex())) {


                                editText.setText(splitsFormula[1].toString().replace("'", ""));
                            }
                        }

                        editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                            @Override
                            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                                if (actionId == EditorInfo.IME_ACTION_DONE) {
                                    // do your stuff here

                                    for (int i = 0; i < passFormula.size(); i++) {

                                        String splitsFormula[] = passFormula.get(i).toString().split(":");
                                        Config.logV("PRINT REMOVEWWWW @@@@@@@@@@@" + splitsFormula[0]);
                                        if (splitsFormula[0].equalsIgnoreCase(filterList.getCloudSearchIndex())) {

                                            Config.logV("PRINT REMOVE @@@@@@@@@@@" + filterList.getCloudSearchIndex());
                                            passFormula.remove(i);


                                        }
                                    }


                                    passFormula.add(filterList.getCloudSearchIndex() + ": '" + editText.getText().toString() + "' ");

                                    filterAdapterCallback.onMethodQuery(passFormula, keyFormula);
                                }
                                return false;
                            }
                        });

                        editText.addTextChangedListener(new TextWatcher() {
                            @Override
                            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                            }

                            @Override
                            public void onTextChanged(CharSequence s, int start, int before, int count) {
                                for (int i = 0; i < passFormula.size(); i++) {

                                    String splitsFormula[] = passFormula.get(i).toString().split(":");
                                    Config.logV("PRINT REMOVEWWWW @@@@@@@@@@@" + splitsFormula[0]);
                                    if (splitsFormula[0].equalsIgnoreCase(filterList.getCloudSearchIndex())) {

                                        Config.logV("PRINT REMOVE @@@@@@@@@@@" + filterList.getCloudSearchIndex());
                                        passFormula.remove(i);


                                    }
                                }

                                if (editText.getText().toString().length() > 0)
                                    passFormula.add(filterList.getCloudSearchIndex() + ": '" + editText.getText().toString() + "' ");

                            }

                            @Override
                            public void afterTextChanged(Editable s) {

                            }
                        });
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


    public void funCheckBoxMore(final JSONArray jsonArray, Context context, final LinearLayout lLayout, final RefinedFilters filterList, final int checksize) {
        try {
            lLayout.removeAllViews();
            for (int i = 0; i < checksize; i++) {
                String valueJson = jsonArray.getString(i);
                Log.e("json", i + "=" + valueJson);
                final JSONObject jsonObj = new JSONObject(valueJson);
                final CheckBox cb = new CheckBox(context);
                cb.setText(jsonObj.getString("displayName"));
                final String name = jsonObj.getString("name");
                for (int j = 0; j < passFormula.size(); j++) {
                    String splitsFormula[] = passFormula.get(j).toString().split(":");

                    if (splitsFormula[0].equalsIgnoreCase(filterList.getCloudSearchIndex().replace("*", "1"))) {
                        if (passFormula.get(j).toString().contains(name)) {

                            cb.setChecked(true);
                        }
                    }
                }
                cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if (isChecked) {


                            for (String str : passFormula) {

                                Config.logV("PRINT  SCHECKNBOX@ ####@@@@@@@@@@@@@@" + str);
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
                lLayout.addView(cb);
            }

            if (jsonArray.length() > 5) {
                TextView txtMore = new TextView(mContext);
                Typeface tyface = Typeface.createFromAsset(mContext.getAssets(),
                        "fonts/Montserrat_Regular.otf");
                txtMore.setTypeface(tyface);
                txtMore.setText("Show More");
                txtMore.setTextColor(mContext.getResources().getColor(R.color.title_consu));
                lLayout.addView(txtMore);
                txtMore.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int checksize = jsonArray.length();
                        funCheckBoxLess(jsonArray, mContext, lLayout, filterList, checksize);
                    }
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void funCheckBoxLess(final JSONArray jsonArray, Context context, final LinearLayout lLayout, final RefinedFilters filterList, final int checksize) {
        try {
            lLayout.removeAllViews();
            for (int i = 0; i < checksize; i++) {
                String valueJson = jsonArray.getString(i);
                Log.e("json", i + "=" + valueJson);
                final JSONObject jsonObj = new JSONObject(valueJson);
                final CheckBox cb = new CheckBox(context);
                cb.setText(jsonObj.getString("displayName"));
                final String name = jsonObj.getString("name");
                for (int j = 0; j < passFormula.size(); j++) {
                    String splitsFormula[] = passFormula.get(j).toString().split(":");

                    if (splitsFormula[0].equalsIgnoreCase(filterList.getCloudSearchIndex().replace("*", "1"))) {
                        if (passFormula.get(j).toString().contains(name)) {

                            cb.setChecked(true);
                        }
                    }
                }
                cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if (isChecked) {


                            for (String str : passFormula) {

                                Config.logV("PRINT  SCHECKNBOX@ ####@@@@@@@@@@@@@@" + str);
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
                lLayout.addView(cb);
            }

            TextView txtless = new TextView(mContext);
            Typeface tyface = Typeface.createFromAsset(mContext.getAssets(),
                    "fonts/Montserrat_Regular.otf");
            txtless.setTypeface(tyface);
            txtless.setText("Show Less");
            txtless.setTextColor(mContext.getResources().getColor(R.color.title_consu));
            lLayout.addView(txtless);
            txtless.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    funCheckBoxMore(jsonArray, mContext, lLayout, filterList, 5);
                    ;
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
