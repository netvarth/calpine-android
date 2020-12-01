package com.jaldeeinc.jaldee.adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.jaldeeinc.jaldee.Interface.ClickListener;
import com.jaldeeinc.jaldee.R;
import com.jaldeeinc.jaldee.callback.AdapterCallback;
import com.jaldeeinc.jaldee.common.Config;
import com.jaldeeinc.jaldee.custom.CustomTextViewMedium;
import com.jaldeeinc.jaldee.custom.KeyValue;
import com.jaldeeinc.jaldee.database.DatabaseHandler;
import com.jaldeeinc.jaldee.model.Domain_Spinner;
import com.jaldeeinc.jaldee.model.FilterChips;
import com.jaldeeinc.jaldee.model.SearchModel;
import com.jaldeeinc.jaldee.response.RefinedFilters;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SearchFiltersAdapter extends RecyclerView.Adapter<SearchFiltersAdapter.MyViewHolder> {

    private List<RefinedFilters> mFilterList;
    Context mContext;
    AdapterCallback filterAdapterCallback;
    private GridLayoutManager gridLayoutManager;
    ArrayList<Domain_Spinner> domainList = new ArrayList<>();
    ArrayList<SearchModel> subDomainsList = new ArrayList<>();
    DatabaseHandler databaseHandler;
    ArrayList<SearchModel> mSubDomain = new ArrayList<>();
    private String selectedDomain = "", selectedSubDomain = "", selectedDomainName = "";
    ArrayList<FilterChips> filterChipsList = new ArrayList<>();


    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView tv_name;
        View mView;
        LinearLayout LexpandView, Ldisplay;
        RecyclerView recyclerView, rvSubDomains;


        public MyViewHolder(View view) {
            super(view);

            Ldisplay = (LinearLayout) view.findViewById(R.id.Ldisplay);
            tv_name = (TextView) view.findViewById(R.id.name);
            mView = (View) view.findViewById(R.id.mView);
            LexpandView = (LinearLayout) view.findViewById(R.id.LexpandView);
            recyclerView = view.findViewById(R.id.rv_recyclerView);
            rvSubDomains = view.findViewById(R.id.rv_subDomains);

        }
    }

    ArrayList<String> keyFormula = new ArrayList<>();
    Activity activity;
    RecyclerView recyclview_popup;
    String passFormulaApi = "";
    String domainSelect;
    String subDomianSelect;

    String domainNAme = "";

    ArrayList<String> passFormula = new ArrayList<>();

    ArrayList<String> passedFormulaArray = new ArrayList<>();
    String subdomainDisplayName;

    public SearchFiltersAdapter(List<RefinedFilters> mFilterList, Context mContext, Activity mActivity, AdapterCallback filterAdapterCallback, RecyclerView recyclepop, String passFormula1, String domainSelected, String subDomain, ArrayList<String> passedFormulaArray, String subdomainDisplayName) {

        Config.logV("PASSED FORMULA ARRAY WWWW GGGKKKG@@" + passFormula.size());
        this.mContext = mContext;
        this.mFilterList = mFilterList;
        this.activity = mActivity;
        this.filterAdapterCallback = filterAdapterCallback;
        this.recyclview_popup = recyclepop;
        this.passFormulaApi = passFormula1;
        this.domainSelect = domainSelected;
        this.subDomianSelect = subDomain;
        this.passedFormulaArray = passedFormulaArray;
        this.subdomainDisplayName = subdomainDisplayName;

        for (int i = 0; i < passedFormulaArray.size(); i++) {
            Config.logV("PRINT VAL FORMULA@@111" + passedFormulaArray.get(i));
            passFormula.add(passedFormulaArray.get(i));
        }
        Config.logV("Domain SELECTED" + domainSelected);
        if (!domainSelected.equalsIgnoreCase("Select")) {
            for (int i = 0; i < mFilterList.size(); i++) {
                if (mFilterList.get(i).getDataType().equalsIgnoreCase("Spinner")) {
                    String query = "sector:'" + domainSelected + "'";
                    passFormula.add(query);
                }
            }

        }


        if (!subDomianSelect.equalsIgnoreCase("Select")) {
            for (int i = 0; i < mFilterList.size(); i++) {
                if (mFilterList.get(i).getDataType().equalsIgnoreCase("Spinner_subdomain")) {
                    String query = "sub_sector:'" + subDomianSelect + "'";
                    passFormula.add(query);
                    Config.logV("PRINT SUB ADDDED " + query);
                }
            }

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
    public SearchFiltersAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.morefilter_row, parent, false);

        return new SearchFiltersAdapter.MyViewHolder(itemView);
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public void onBindViewHolder(final SearchFiltersAdapter.MyViewHolder myViewHolder, final int position) {
        final RefinedFilters filterList = mFilterList.get(position);


        Typeface tyface = Typeface.createFromAsset(mContext.getAssets(),
                "fonts/JosefinSans-SemiBold.ttf");
        myViewHolder.tv_name.setTypeface(tyface);

        myViewHolder.tv_name.setText(filterList.getDisplayName());

        if (position == mFilterList.size() - 1) {
            myViewHolder.mView.setVisibility(View.GONE);
        } else {
            myViewHolder.mView.setVisibility(View.VISIBLE);
        }


        if (filterList.getDataType().equalsIgnoreCase("EnumList") || filterList.getDataType().equalsIgnoreCase("Enum") || filterList.getDataType().equalsIgnoreCase("Gender")) {
            // myViewHolder.LexpandView.setVisibility(View.VISIBLE);
            myViewHolder.LexpandView.removeAllViews();


            if (filterList.getEnumeratedConstants() instanceof String) {
                Config.logV("String ");
            } else {

//                Config.logV("Array " + new Gson().toJson(filterList.getEnumeratedConstants()));
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
                        funCheckBoxMore(jsonArray, mContext, myViewHolder.LexpandView, filterList, 5, "show");
                    } else {
                        funCheckBoxMore(jsonArray, mContext, myViewHolder.LexpandView, filterList, jsonArray.length(), "show");
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }

        } else if (filterList.getDataType().equalsIgnoreCase("Rating")) {
            Config.logV("RATING @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
            // myViewHolder.LexpandView.setVisibility(View.VISIBLE);
            myViewHolder.LexpandView.removeAllViews();

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


            LinearLayout.LayoutParams layoutParams1 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            layoutParams1.gravity = Gravity.CENTER;
            ratingBar.setLayoutParams(layoutParams1);


            for (int j = 0; j < passFormula.size(); j++) {
                String splitsFormula[] = passFormula.get(j).toString().split(":");
                Config.logV("PRINT SUBSPINNERKK ##@@DD ##" + splitsFormula[0] + splitsFormula[1]);
                if (splitsFormula[0].equalsIgnoreCase("rating")) {

                    filterList.setExpand(true);
                    myViewHolder.LexpandView.setVisibility(View.VISIBLE);
                    float val = Float.parseFloat(splitsFormula[1].toString().replace("'", ""));
                    ratingBar.setRating(val);
                }
            }


            final ImageView img = new ImageView(mContext);
            if (parent != null) {


                img.setImageDrawable(mContext.getResources().getDrawable(R.drawable.close));

                // img.setImageResource(R.drawable.close);
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                layoutParams.gravity = Gravity.CENTER;
                layoutParams.setMargins(0, -15, 0, 0);
                img.setLayoutParams(layoutParams);

                parent.addView(ratingBar);
                parent.addView(img);
                img.setVisibility(View.GONE);

                img.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ratingBar.setRating(0);
                        filterChipsList = removeRatingsFromChips(filterChipsList);

                        for (int i = 0; i < passFormula.size(); i++) {

                            if (passFormula.get(i).toString().contains(filterList.getCloudSearchIndex().toString())) {
                                passFormula.remove(i);
                            }
                        }

                        filterAdapterCallback.onMethodQuery(passFormula, keyFormula, filterChipsList);

                        img.setVisibility(View.GONE);
                    }
                });
            }


            if (myViewHolder.LexpandView != null) {
                LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) myViewHolder.LexpandView.getLayoutParams();
                params.setMargins(0, 5, 0, 5);

                myViewHolder.LexpandView.setLayoutParams(params);
                myViewHolder.LexpandView.setPadding(0, 0, 0, 0);
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

                    FilterChips checkBox = new FilterChips(String.valueOf(rateValue), String.valueOf(rateValue), filterList.getDataType(),img, true);
                    filterChipsList.add(checkBox);
                    filterChipsList = getFilteredChipsList(filterChipsList);
                    filterAdapterCallback.onMethodQuery(passFormula, keyFormula, filterChipsList);
                }
            });


        } else if (filterList.getDataType().equalsIgnoreCase("Boolean")) {
            // myViewHolder.LexpandView.setVisibility(View.VISIBLE);
            myViewHolder.LexpandView.removeAllViews();
            Config.logV("Rating @@@@@@@@@@@@@@");


            for (int i = 0; i < filterList.getItemName().size(); i++) {

                LinearLayout parent = new LinearLayout(mContext);

                parent.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                parent.setOrientation(LinearLayout.HORIZONTAL);

                final Switch sw = new Switch(mContext);
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                layoutParams.setMargins(15, 15, 15, 15);
                sw.setLayoutParams(layoutParams);
                // sw.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
                // sw.setText(filterList.getItemName().get(i).toString());
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

                        filterList.setExpand(true);
                        myViewHolder.LexpandView.setVisibility(View.VISIBLE);
                    }
                }


                CustomTextViewMedium txt = new CustomTextViewMedium(mContext);
                txt.setText(filterList.getItemName().get(i).toString());
                LinearLayout.LayoutParams layoutParams1 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                layoutParams1.setMargins(15, 15, 15, 15);
                txt.setLayoutParams(layoutParams1);
                txt.setTextColor(mContext.getResources().getColor(R.color.title_grey));

                if (parent != null) {
                    parent.addView(sw);
                    parent.addView(txt);
                }
                // Add Switch to LinearLayout
                if (myViewHolder.LexpandView != null) {
                    myViewHolder.LexpandView.addView(parent);
                }

                final int finalI = i;
                sw.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if (isChecked) {


                            passFormula.add("not " + filterList.getCloudIndexvalue().get(finalI).toString().replace("*", "1") + ":  '0')");

                            FilterChips checkBox = new FilterChips(txt.getText().toString(), txt.getText().toString(), filterList.getDataType(),sw, true);
                            filterChipsList.add(checkBox);
                            filterChipsList = getFilteredChipsList(filterChipsList);
                            filterAdapterCallback.onMethodQuery(passFormula, keyFormula, filterChipsList);
                        } else {

                            FilterChips checkBox = new FilterChips(txt.getText().toString(), txt.getText().toString(), filterList.getDataType(),sw, true);
                            filterChipsList = removeChipFromList(filterChipsList, checkBox);

                            for (int i = 0; i < passFormula.size(); i++) {


                                if (passFormula.get(i).toString().contains(filterList.getCloudIndexvalue().get(finalI).toString().replace("*", "1"))) {

                                    passFormula.remove(i);
                                    filterAdapterCallback.onMethodQuery(passFormula, keyFormula, filterChipsList);

                                }
                            }
                        }
                    }
                });
            }


        } else if (filterList.getDataType().equalsIgnoreCase("Spinner")) {

            myViewHolder.LexpandView.setVisibility(View.VISIBLE);
//            myViewHolder.LexpandView.removeAllViews();
            myViewHolder.recyclerView.setVisibility(View.VISIBLE);
            myViewHolder.rvSubDomains.setVisibility(View.GONE);
            ArrayList<KeyValue> domains = new ArrayList<>();
            databaseHandler = new DatabaseHandler(mContext);
            domainList = databaseHandler.getDomain();
            for (Domain_Spinner domain : domainList) {

                if (!domain.getDisplayName().equalsIgnoreCase("All")) {

                    domains.add(new KeyValue(domain.getDisplayName(), domain.getDomain()));
                }

            }

            DomainsAdapter cardsAdapter = new DomainsAdapter(mContext, domains, selectedDomain);
            gridLayoutManager = new GridLayoutManager(mContext, 2, GridLayoutManager.HORIZONTAL, false);
            myViewHolder.recyclerView.setLayoutManager(gridLayoutManager);
            myViewHolder.recyclerView.setAdapter(cardsAdapter);


            myViewHolder.recyclerView.addOnItemTouchListener(new RecyclerTouchListener(mContext,
                    myViewHolder.recyclerView, new ClickListener() {
                @Override
                public void onClick(View view, final int position) {
                    //Values are passing to activity & to fragment as well

                    filterChipsList = new ArrayList<FilterChips>();
                    KeyValue obj = cardsAdapter.getSelectedDomain(position);
                    selectedDomainName = obj.getKey();
                    selectedDomain = obj.getValue();
                    cardsAdapter.setSelectedDomain(obj.getValue());
                    String query = "sector:'" + obj.getValue() + "'";

                    filterAdapterCallback.onMethodFilterRefined(query, recyclview_popup, obj.getValue());

                    passFormula = new ArrayList<>();
                    passFormula.add(query);
                    filterAdapterCallback.onMethodQuery(passFormula, keyFormula, filterChipsList);
                }

                @Override
                public void onLongClick(View view, int position) {
                    Toast.makeText(mContext, "Long press on position :" + position,
                            Toast.LENGTH_LONG).show();
                }
            }));


        } else if (filterList.getDataType().equalsIgnoreCase("Spinner_subdomain")) {
////              myViewHolder.LexpandView.setVisibility(View.VISIBLE);
//            myViewHolder.LexpandView.removeAllViews();
//
//            final Spinner spinner = new Spinner(mContext, Spinner.MODE_DIALOG);
//            ArrayAdapter<SearchModel> adapter = new ArrayAdapter<SearchModel>(mContext, android.R.layout.simple_spinner_item, (ArrayList<SearchModel>) filterList.getEnumeratedConstants()) {
//                @Override
//                public boolean isEnabled(int position) {
//                    if (position == 0) {
//                        // Disable the first item from Spinner
//                        // First item will be use for hint
//                        return false;
//                    } else {
//                        return true;
//                    }
//                }
//
//                @Override
//                public View getDropDownView(int position, View convertView,
//                                            ViewGroup parent) {
//                    View view = super.getDropDownView(position, convertView, parent);
//                    TextView tv = (TextView) view;
//                    if (position == 0) {
//                        // Set the hint text color gray
//                        tv.setTextColor(Color.GRAY);
//                    } else {
//                        tv.setTextColor(Color.BLACK);
//                    }
//                    return view;
//                }
//            };
//
//            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//
//            final int pos = getIndexSubDomain((ArrayList<SearchModel>) filterList.getEnumeratedConstants(), subdomainDisplayName);
//
//            // spinner.setOnItemSelectedListener(new CustomSubDomainSpinner(filterAdapterCallback, recyclview_popup, passFormulaApi, pos));
//            spinner.setAdapter(adapter);
//            spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//                @Override
//                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                    if (position > 0 && position != pos) {
//                        SearchModel spinnerSelect = (SearchModel) parent.getSelectedItem();
//
//
//                        String query = "sub_sector:'" + spinnerSelect.getName() + "'";
//
//                        for (int i = 0; i < passFormula.size(); i++) {
//                            //   if (passFormula.get(i).contains("sub_sector")) {
//                            String splitsFormula[] = passFormula.get(i).toString().split(":");
//                            // Config.logV("PRINT SUBSPINNERKK ##@@DD ##" + splitsFormula[0] + keyFormula.get(i).toString());
//                            if (splitsFormula[0].equalsIgnoreCase("sub_sector")) {
//
//                                passFormula.remove(i);
//                            }
//                        }
//
//
//                        passFormula = new ArrayList<>();
//                        passFormula.add(query);
//                        String query_sector = "sector:'" + domainSelect + "'";
//                        passFormula.add(query_sector);
//
//                        Config.logV("DOMAIN NAME @@@@@@@@@@@@@@@@@@" + domainNAme + "Domain Select" + domainSelect);
//
//                        filterAdapterCallback.onMethodSubDomainFilter(query, recyclview_popup, spinnerSelect.getName(), domainSelect, spinnerSelect.getDisplayname());
//                        filterAdapterCallback.onMethodQuery(passFormula, keyFormula);
//
//                        // Config.logV("PRINT VAL FORMULA@@WWWWWWWW" + query);
//
//                        for (String str : passFormula) {
//
//                            Config.logV("PRINT VAL FORMULA@@HHHH" + str);
//                        }
//
//                    }
//                }
//
//                @Override
//                public void onNothingSelected(AdapterView<?> parent) {
//
//                }
//            });
//
//            for (int j = 0; j < passFormula.size(); j++) {
//                String splitsFormula[] = passFormula.get(j).toString().split(":");
//
//                if (splitsFormula[0].equalsIgnoreCase("sub_sector")) {
//
//                    filterList.setExpand(true);
//                    myViewHolder.LexpandView.setVisibility(View.VISIBLE);
//
//                }
//            }
//
//            if (subDomianSelect.equalsIgnoreCase("Select")) {
//                spinner.setSelection(0);
//            } else {
//                spinner.setSelection(pos);
//            }
//
//            myViewHolder.LexpandView.addView(spinner);

            myViewHolder.LexpandView.setVisibility(View.VISIBLE);
            myViewHolder.recyclerView.setVisibility(View.GONE);
            myViewHolder.rvSubDomains.setVisibility(View.VISIBLE);
            ArrayList<KeyValue> subDomains = new ArrayList<>();
            subDomainsList = (ArrayList<SearchModel>) filterList.getEnumeratedConstants();
            for (SearchModel subDomain : subDomainsList) {

                if (!subDomain.getDisplayname().equalsIgnoreCase("Select")) {

                    subDomains.add(new KeyValue(subDomain.getDisplayname(), subDomain.getName()));
                }

            }
            ArrayList<KeyValue> filteredSubDomains = getFilteredSubDomains(subDomains);

            SubDomainAdapter cardsAdapter = new SubDomainAdapter(mContext, filteredSubDomains, selectedSubDomain);
            gridLayoutManager = new GridLayoutManager(mContext, 2, GridLayoutManager.HORIZONTAL, false);
            myViewHolder.rvSubDomains.setLayoutManager(gridLayoutManager);
            myViewHolder.rvSubDomains.setAdapter(cardsAdapter);


            myViewHolder.rvSubDomains.addOnItemTouchListener(new RecyclerTouchListener(mContext,
                    myViewHolder.rvSubDomains, new ClickListener() {
                @Override
                public void onClick(View view, final int position) {
                    //Values are passing to activity & to fragment as well

                    filterChipsList = new ArrayList<FilterChips>();
                    KeyValue obj = cardsAdapter.getSelectedSubDomain(position);
                    cardsAdapter.setSelectedSubDomain(obj.getValue());
                    String query = "sub_sector:'" + obj.getValue() + "'";

                    FilterChips subDomainChip = new FilterChips(obj.getKey(), obj.getValue(), filterList.getDataType(),myViewHolder.rvSubDomains, false);
                    int index = filterChipsList.indexOf(obj);
                    if (index > -1) {
                        filterChipsList.get(index).setValue(obj.getValue());
                    } else {
                        filterChipsList.add(subDomainChip);
                    }
                    for (int i = 0; i < passFormula.size(); i++) {
                        //   if (passFormula.get(i).contains("sub_sector")) {
                        String splitsFormula[] = passFormula.get(i).toString().split(":");

                        if (splitsFormula[0].equalsIgnoreCase("sub_sector")) {

                            passFormula.remove(i);
                        }
                    }

                    passFormula = new ArrayList<>();
                    passFormula.add(query);
                    String query_sector = "sector:'" + domainSelect + "'";

                    passFormula.add(query_sector);

                    filterAdapterCallback.onMethodSubDomainFilter(query, recyclview_popup, obj.getValue(), domainSelect, obj.getValue());
                    filterAdapterCallback.onMethodQuery(passFormula, keyFormula, filterChipsList);

                }

                @Override
                public void onLongClick(View view, int position) {
                    Toast.makeText(mContext, "Long press on position :" + position,
                            Toast.LENGTH_LONG).show();
                }
            }));


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


            // myViewHolder.LexpandView.setVisibility(View.VISIBLE);
            myViewHolder.LexpandView.removeAllViews();


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

                    filterList.setExpand(true);
                    myViewHolder.LexpandView.setVisibility(View.VISIBLE);
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

                        FilterChips eText = new FilterChips(editText.getText().toString(), editText.getText().toString(), filterList.getDataType(),editText, true);
                        filterChipsList.add(eText);
                        filterChipsList = getFilteredChipsList(filterChipsList);
                        passFormula.add(filterList.getCloudSearchIndex() + ": '" + editText.getText().toString() + "' ");

                        filterAdapterCallback.onMethodQuery(passFormula, keyFormula, filterChipsList);
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

        myViewHolder.Ldisplay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                Config.logV("RATING $$$$$$$$$$$$$$$$$$$$$$$");
                //Config.logV("filterList.isExpand()" + filterList.isExpand()+"DISPLAY"+filterList.getDataType()+"DDD"+filterList.getDisplayName());
                if (!filterList.isExpand()) {
                    filterList.setExpand(true);
                    myViewHolder.LexpandView.setVisibility(View.VISIBLE);

                    if (filterList.getDataType().equalsIgnoreCase("EnumList") || filterList.getDataType().equalsIgnoreCase("Enum") || filterList.getDataType().equalsIgnoreCase("Gender")) {
                        // myViewHolder.LexpandView.setVisibility(View.VISIBLE);
                        myViewHolder.LexpandView.removeAllViews();


                        if (filterList.getEnumeratedConstants() instanceof String) {
                            Config.logV("String ");
                        } else {

//                            Config.logV("Array " + new Gson().toJson(filterList.getEnumeratedConstants()));
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
                                    funCheckBoxMore(jsonArray, mContext, myViewHolder.LexpandView, filterList, 5, "show");
                                } else {
                                    funCheckBoxMore(jsonArray, mContext, myViewHolder.LexpandView, filterList, jsonArray.length(), "show");
                                }


                            } catch (JSONException e) {
                                e.printStackTrace();
                            }


                        }

                    } else if (filterList.getDataType().equalsIgnoreCase("Rating")) {
                        Config.logV("RATING @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
                        // myViewHolder.LexpandView.setVisibility(View.VISIBLE);
                        myViewHolder.LexpandView.removeAllViews();

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


                        LinearLayout.LayoutParams layoutParams1 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                        layoutParams1.gravity = Gravity.CENTER;
                        ratingBar.setLayoutParams(layoutParams1);


                        for (int j = 0; j < passFormula.size(); j++) {
                            String splitsFormula[] = passFormula.get(j).toString().split(":");
                            Config.logV("PRINT SUBSPINNERKK ##@@DD ##" + splitsFormula[0] + splitsFormula[1]);
                            if (splitsFormula[0].equalsIgnoreCase("rating")) {

                                float val = Float.parseFloat(splitsFormula[1].toString().replace("'", ""));
                                ratingBar.setRating(val);
                            }
                        }

                        final ImageView img = new ImageView(mContext);
                        if (parent != null) {


                            img.setImageDrawable(mContext.getResources().getDrawable(R.drawable.close));

                            // img.setImageResource(R.drawable.close);
                            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                            layoutParams.gravity = Gravity.CENTER;
                            layoutParams.setMargins(0, -15, 0, 0);
                            img.setLayoutParams(layoutParams);

                            parent.addView(ratingBar);
                            parent.addView(img);
                            img.setVisibility(View.GONE);

                            img.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    ratingBar.setRating(0);
                                    filterChipsList = removeRatingsFromChips(filterChipsList);

                                    for (int i = 0; i < passFormula.size(); i++) {

                                        if (passFormula.get(i).toString().contains(filterList.getCloudSearchIndex().toString())) {
                                            passFormula.remove(i);
                                        }
                                    }


                                    filterAdapterCallback.onMethodQuery(passFormula, keyFormula, filterChipsList);

                                    img.setVisibility(View.GONE);
                                }
                            });
                        }


                        if (myViewHolder.LexpandView != null) {
                            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) myViewHolder.LexpandView.getLayoutParams();
                            params.setMargins(0, 5, 0, 5);

                            myViewHolder.LexpandView.setLayoutParams(params);
                            myViewHolder.LexpandView.setPadding(0, 0, 0, 0);
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

                                FilterChips checkBox = new FilterChips(String.valueOf(rateValue), String.valueOf(rateValue), filterList.getDataType(),img, true);
                                filterChipsList.add(checkBox);
                                filterChipsList = getFilteredChipsList(filterChipsList);

                                filterAdapterCallback.onMethodQuery(passFormula, keyFormula, filterChipsList);
                            }
                        });


                    } else if (filterList.getDataType().equalsIgnoreCase("Boolean")) {
                        // myViewHolder.LexpandView.setVisibility(View.VISIBLE);
                        myViewHolder.LexpandView.removeAllViews();
                        Config.logV("Rating @@@@@@@@@@@@@@");


                        for (int i = 0; i < filterList.getItemName().size(); i++) {

                            LinearLayout parent = new LinearLayout(mContext);

                            parent.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                            parent.setOrientation(LinearLayout.HORIZONTAL);

                            final Switch sw = new Switch(mContext);
                            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                            layoutParams.setMargins(15, 15, 15, 15);
                            sw.setLayoutParams(layoutParams);
                            // sw.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
                            // sw.setText(filterList.getItemName().get(i).toString());
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
                            CustomTextViewMedium txt = new CustomTextViewMedium(mContext);
                            txt.setText(filterList.getItemName().get(i).toString());
                            LinearLayout.LayoutParams layoutParams1 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                            layoutParams1.setMargins(15, 15, 15, 15);
                            txt.setLayoutParams(layoutParams1);
                            txt.setTextColor(mContext.getResources().getColor(R.color.title_grey));

                            if (parent != null) {
                                parent.addView(sw);
                                parent.addView(txt);
                            }
                            // Add Switch to LinearLayout
                            if (myViewHolder.LexpandView != null) {
                                myViewHolder.LexpandView.addView(parent);
                            }

                            final int finalI = i;
                            sw.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                                @Override
                                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                    if (isChecked) {

                                        passFormula.add("not " + filterList.getCloudIndexvalue().get(finalI).toString().replace("*", "1") + ":  '0')");
                                        FilterChips checkBox = new FilterChips(txt.getText().toString(), txt.getText().toString(), filterList.getDataType(),sw, true);
                                        filterChipsList.add(checkBox);
                                        filterChipsList = getFilteredChipsList(filterChipsList);
                                        filterAdapterCallback.onMethodQuery(passFormula, keyFormula, filterChipsList);
                                    } else {

                                        FilterChips checkBox = new FilterChips(txt.getText().toString(), txt.getText().toString(), filterList.getDataType(),sw, true);
                                        filterChipsList = removeChipFromList(filterChipsList, checkBox);

                                        for (int i = 0; i < passFormula.size(); i++) {

                                            if (passFormula.get(i).toString().contains(filterList.getCloudIndexvalue().get(finalI).toString().replace("*", "1"))) {

                                                passFormula.remove(i);
                                                filterAdapterCallback.onMethodQuery(passFormula, keyFormula, filterChipsList);

                                            }
                                        }
                                    }
                                }
                            });
                        }


                    } else if (filterList.getDataType().equalsIgnoreCase("Spinner")) {

//                        // myViewHolder.LexpandView.setVisibility(View.VISIBLE);
//                        myViewHolder.LexpandView.removeAllViews();
//                        // keyFormula.add("sector");
//
//                        Config.logV("UPDATE SPINNER @@@@@@@@@@@@@@@" + domainSelect);
//                        Spinner spinner = new Spinner(mContext, Spinner.MODE_DIALOG);
//                        ArrayAdapter<Domain_Spinner> adapter = new ArrayAdapter<Domain_Spinner>(mContext, android.R.layout.simple_spinner_item, (ArrayList<Domain_Spinner>) filterList.getEnumeratedConstants()) {
//                            @Override
//                            public boolean isEnabled(int position) {
//                                if (position == 0) {
//                                    // Disable the first item from Spinner
//                                    // First item will be use for hint
//                                    return false;
//                                } else {
//                                    return true;
//                                }
//                            }
//
//                            @Override
//                            public View getDropDownView(int position, View convertView,
//                                                        ViewGroup parent) {
//                                View view = super.getDropDownView(position, convertView, parent);
//                                TextView tv = (TextView) view;
//                                if (position == 0) {
//                                    // Set the hint text color gray
//                                    tv.setTextColor(Color.GRAY);
//                                } else {
//                                    tv.setTextColor(Color.BLACK);
//                                }
//                                return view;
//                            }
//                        };
//
//                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//
//
//                        final int pos = getIndex((ArrayList<Domain_Spinner>) filterList.getEnumeratedConstants(), domainSelect);
//
//
//                        //spinner.setOnItemSelectedListener(new CustomSpinner(filterAdapterCallback, recyclview_popup,pos,passFormula,keyFormula));
//                        spinner.setAdapter(adapter);
//
//                        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//                            @Override
//                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                                if (position > 0 && position != pos) {
//                                    Domain_Spinner spinnerSelect = (Domain_Spinner) parent.getSelectedItem();
//                                    Config.logV("Spinner Data@@@@@@@@@@" + spinnerSelect.getDomain());
//                                    String query = "sector:'" + spinnerSelect.getDomain() + "'";
//
//                                    domainNAme = spinnerSelect.getDomain();
//
////                                    filterAdapterCallback.onMethodFilterRefined(query, recyclview_popup, spinnerSelect.getDomain());
//
//                                   /* for (int i = 0; i < passFormula.size(); i++) {
//
//                                        if(passFormula.get(i).toString().contains("sub_sector")){
//
//                                            passFormula.remove(i);
//                                        }
//                                    }*/
//
//                                    passFormula = new ArrayList<>();
//                                    passFormula.add(query);
//
//
////                                    filterAdapterCallback.onMethodQuery(passFormula, keyFormula);
//
//
//                                }
//                            }
//
//                            @Override
//                            public void onNothingSelected(AdapterView<?> parent) {
//
//                            }
//                        });
//                        if (domainSelect.equalsIgnoreCase("Select")) {
//                            spinner.setSelection(0);
//                        } else {
//                            spinner.setSelection(pos);
//                        }
//                        Config.logV("Select Pos @@@@@@@@@@@@@@@@@" + pos);
//                        myViewHolder.LexpandView.addView(spinner);
                        myViewHolder.LexpandView.setVisibility(View.VISIBLE);
                        myViewHolder.recyclerView.setVisibility(View.VISIBLE);
                        myViewHolder.rvSubDomains.setVisibility(View.GONE);
                        ArrayList<KeyValue> domains = new ArrayList<>();
                        databaseHandler = new DatabaseHandler(mContext);
                        domainList = databaseHandler.getDomain();
                        for (Domain_Spinner domain : domainList) {

                            if (!domain.getDisplayName().equalsIgnoreCase("All")) {

                                domains.add(new KeyValue(domain.getDisplayName(), domain.getDomain()));
                            }

                        }

                        DomainsAdapter cardsAdapter = new DomainsAdapter(mContext, domains, selectedDomain);
                        gridLayoutManager = new GridLayoutManager(mContext, 2, GridLayoutManager.HORIZONTAL, false);
                        myViewHolder.recyclerView.setLayoutManager(gridLayoutManager);
                        myViewHolder.recyclerView.setAdapter(cardsAdapter);


                        myViewHolder.recyclerView.addOnItemTouchListener(new RecyclerTouchListener(mContext,
                                myViewHolder.recyclerView, new ClickListener() {
                            @Override
                            public void onClick(View view, final int position) {
                                //Values are passing to activity & to fragment as well

                                filterChipsList = new ArrayList<FilterChips>();
                                KeyValue obj = cardsAdapter.getSelectedDomain(position);
                                selectedDomainName = obj.getKey();
                                selectedDomain = obj.getValue();
                                cardsAdapter.setSelectedDomain(obj.getValue());
                                String query = "sector:'" + obj.getValue() + "'";

                                filterAdapterCallback.onMethodFilterRefined(query, recyclview_popup, obj.getValue());

                                passFormula = new ArrayList<>();
                                passFormula.add(query);
                                filterAdapterCallback.onMethodQuery(passFormula, keyFormula, filterChipsList);

                            }

                            @Override
                            public void onLongClick(View view, int position) {
                                Toast.makeText(mContext, "Long press on position :" + position,
                                        Toast.LENGTH_LONG).show();
                            }
                        }));

                    } else if (filterList.getDataType().equalsIgnoreCase("Spinner_subdomain")) {
//                        //  myViewHolder.LexpandView.setVisibility(View.VISIBLE);
//                        myViewHolder.LexpandView.removeAllViews();
//
//                        Spinner spinner = new Spinner(mContext, Spinner.MODE_DIALOG);
//                        ArrayAdapter<SearchModel> adapter = new ArrayAdapter<SearchModel>(mContext, android.R.layout.simple_spinner_item, (ArrayList<SearchModel>) filterList.getEnumeratedConstants()) {
//                            @Override
//                            public boolean isEnabled(int position) {
//                                if (position == 0) {
//                                    // Disable the first item from Spinner
//                                    // First item will be use for hint
//                                    return false;
//                                } else {
//                                    return true;
//                                }
//                            }
//
//                            @Override
//                            public View getDropDownView(int position, View convertView,
//                                                        ViewGroup parent) {
//                                View view = super.getDropDownView(position, convertView, parent);
//                                TextView tv = (TextView) view;
//                                if (position == 0) {
//                                    // Set the hint text color gray
//                                    tv.setTextColor(Color.GRAY);
//                                } else {
//                                    tv.setTextColor(Color.BLACK);
//                                }
//                                return view;
//                            }
//                        };
//
//                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//
//                        final int pos = getIndexSubDomain((ArrayList<SearchModel>) filterList.getEnumeratedConstants(), subdomainDisplayName);
//
//                        // spinner.setOnItemSelectedListener(new CustomSubDomainSpinner(filterAdapterCallback, recyclview_popup, passFormulaApi, pos));
//                        spinner.setAdapter(adapter);
//                        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//                            @Override
//                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                                if (position > 0 && position != pos) {
//                                    SearchModel spinnerSelect = (SearchModel) parent.getSelectedItem();
//
//
//                                    String query = "sub_sector:'" + spinnerSelect.getName() + "'";
//
//                                    for (int i = 0; i < passFormula.size(); i++) {
//                                        //   if (passFormula.get(i).contains("sub_sector")) {
//                                        String splitsFormula[] = passFormula.get(i).toString().split(":");
//                                        // Config.logV("PRINT SUBSPINNERKK ##@@DD ##" + splitsFormula[0] + keyFormula.get(i).toString());
//                                        if (splitsFormula[0].equalsIgnoreCase("sub_sector")) {
//
//                                            passFormula.remove(i);
//                                        }
//                                    }
//
//                                    passFormula = new ArrayList<>();
//                                    passFormula.add(query);
//                                    String query_sector = "sector:'" + domainSelect + "'";
//                                    passFormula.add(query_sector);
//
//                                    Config.logV("DOMAIN NAME @@@@@@@@@@@@@@@@@@" + domainNAme + "Domain Select" + domainSelect);
//
//                                    filterAdapterCallback.onMethodSubDomainFilter(query, recyclview_popup, spinnerSelect.getName(), domainSelect, spinnerSelect.getDisplayname());
//                                    filterAdapterCallback.onMethodQuery(passFormula, keyFormula);
//
//                                    // Config.logV("PRINT VAL FORMULA@@WWWWWWWW" + query);
//
//                                    for (String str : passFormula) {
//
//                                        Config.logV("PRINT VAL FORMULA@@HHHHVVV" + str);
//                                    }
//
//                                }
//                            }
//
//                            @Override
//                            public void onNothingSelected(AdapterView<?> parent) {
//
//                            }
//                        });
//
//                        if (subDomianSelect.equalsIgnoreCase("Select")) {
//                            spinner.setSelection(0);
//                        } else {
//                            spinner.setSelection(pos);
//                        }
//
//                        myViewHolder.LexpandView.addView(spinner);

                        myViewHolder.LexpandView.setVisibility(View.VISIBLE);
                        myViewHolder.recyclerView.setVisibility(View.GONE);
                        myViewHolder.rvSubDomains.setVisibility(View.VISIBLE);
                        ArrayList<KeyValue> subDomains = new ArrayList<>();
                        subDomainsList = (ArrayList<SearchModel>) filterList.getEnumeratedConstants();
                        for (SearchModel subDomain : subDomainsList) {

                            if (!subDomain.getDisplayname().equalsIgnoreCase("Select")) {

                                subDomains.add(new KeyValue(subDomain.getDisplayname(), subDomain.getName()));
                            }

                        }

                        ArrayList<KeyValue> filteredSubDomains = getFilteredSubDomains(subDomains);

                        SubDomainAdapter cardsAdapter = new SubDomainAdapter(mContext, filteredSubDomains, selectedSubDomain);
                        gridLayoutManager = new GridLayoutManager(mContext, 2, GridLayoutManager.HORIZONTAL, false);
                        myViewHolder.rvSubDomains.setLayoutManager(gridLayoutManager);
                        myViewHolder.rvSubDomains.setAdapter(cardsAdapter);


                        myViewHolder.rvSubDomains.addOnItemTouchListener(new RecyclerTouchListener(mContext,
                                myViewHolder.rvSubDomains, new ClickListener() {
                            @Override
                            public void onClick(View view, final int position) {
                                //Values are passing to activity & to fragment as well

                                filterChipsList = new ArrayList<>();
                                KeyValue obj = cardsAdapter.getSelectedSubDomain(position);
                                cardsAdapter.setSelectedSubDomain(obj.getValue());
                                String query = "sub_sector:'" + obj.getValue() + "'";

                                FilterChips subDomainChip = new FilterChips(obj.getKey(), obj.getValue(), filterList.getDataType(),myViewHolder.rvSubDomains, false);
                                int index = filterChipsList.indexOf(obj);
                                if (index > -1) {
                                    filterChipsList.get(index).setValue(obj.getValue());
                                } else {
                                    filterChipsList.add(subDomainChip);
                                }
                                for (int i = 0; i < passFormula.size(); i++) {
                                    //   if (passFormula.get(i).contains("sub_sector")) {
                                    String splitsFormula[] = passFormula.get(i).toString().split(":");

                                    if (splitsFormula[0].equalsIgnoreCase("sub_sector")) {

                                        passFormula.remove(i);
                                    }
                                }

                                passFormula = new ArrayList<>();
                                passFormula.add(query);
                                String query_sector = "sector:'" + domainSelect + "'";

                                passFormula.add(query_sector);

                                filterAdapterCallback.onMethodSubDomainFilter(query, recyclview_popup, obj.getValue(), domainSelect, obj.getValue());
                                filterAdapterCallback.onMethodQuery(passFormula, keyFormula, filterChipsList);

                            }

                            @Override
                            public void onLongClick(View view, int position) {
                                Toast.makeText(mContext, "Long press on position :" + position,
                                        Toast.LENGTH_LONG).show();
                            }
                        }));

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


                        // myViewHolder.LexpandView.setVisibility(View.VISIBLE);
                        myViewHolder.LexpandView.removeAllViews();


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

                                    FilterChips eText = new FilterChips(editText.getText().toString(), editText.getText().toString(), filterList.getDataType(),editText, true);
                                    filterChipsList.add(eText);
                                    filterChipsList = getFilteredChipsList(filterChipsList);

                                    passFormula.add(filterList.getCloudSearchIndex() + ": '" + editText.getText().toString() + "' ");

                                    filterAdapterCallback.onMethodQuery(passFormula, keyFormula, filterChipsList);
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

    private ArrayList<FilterChips> removeRatingsFromChips(ArrayList<FilterChips> filterChipsList) {

        ArrayList<FilterChips> chipsList = new ArrayList<>();

        chipsList.addAll(filterChipsList);

        for (int i = 0; i < chipsList.size(); i++) {
            if (chipsList.get(i).getType().equalsIgnoreCase("Rating")) {
                chipsList.remove(i);
            }
        }
        return chipsList;
    }

    public ArrayList<KeyValue> getFilteredSubDomains(ArrayList<KeyValue> subDomainsList) {
        Set<String> values = new HashSet<>();
        ArrayList<KeyValue> subDomains = new ArrayList<>();
        for (final KeyValue keyValue : subDomainsList) {
            values.add(keyValue.getValue());
        }

        for (String value : values) {

            KeyValue obj = new KeyValue();
            obj.setValue(value);

            for (KeyValue subDomain : subDomainsList) {

                if (value.equalsIgnoreCase(subDomain.getValue())) {

                    if (obj.getKey() != null && obj.getKey().trim().length() > 0) {
                        obj.setKey(obj.getKey() + " / " + subDomain.getKey());
                    } else {
                        obj.setKey(subDomain.getKey());
                    }
                }
            }
            subDomains.add(obj);
        }

        return subDomains;
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
            if (spinner.get(i).getDisplayname().equals(myString)) {
                index = i;
            }
        }
        return index;
    }

    @Override
    public int getItemCount() {
        return mFilterList.size();
    }

    public void setSelectedDomain(String domain) {

        this.selectedDomain = domain;
    }

    public void setSelectedSubDomain(String subDomain) {

        this.selectedSubDomain = subDomain;
    }

    public void setDomainAndSubDomain(String domain, String subDomain) {

        this.selectedDomain = domain;
        this.selectedSubDomain = subDomain;
    }

    public void setFilterList(List<RefinedFilters> filterList) {

        filterList = filterList == null ? new ArrayList<>() : filterList;
        mFilterList = filterList;
        notifyDataSetChanged();
    }


    public void funCheckBoxMore(final JSONArray jsonArray, Context context,
                                final LinearLayout lLayout, final RefinedFilters filterList, final int checksize, String
                                        show) {
        try {

            boolean flagShowFull = false;
            if (!show.equalsIgnoreCase("not")) {
                for (int i = 0; i < jsonArray.length(); i++) {
                    String valueJson = jsonArray.getString(i);
                    final JSONObject jsonObj = new JSONObject(valueJson);
                    final String name = jsonObj.getString("name");
                    for (int j = 0; j < passFormula.size(); j++) {
                        String splitsFormula[] = passFormula.get(j).toString().split(":");

                        if (splitsFormula[0].equalsIgnoreCase(filterList.getCloudSearchIndex().replace("*", "1"))) {
                            if (passFormula.get(j).toString().contains(name)) {

                                if (i > 5) {
                                    Config.logV("TRUE @@@@@@@@@@@@@@@@@11111@@");
                                    flagShowFull = true;
                                    break;
                                }

                            }
                        }
                    }
                }
            }
            lLayout.removeAllViews();
            Typeface tyface = Typeface.createFromAsset(mContext.getAssets(),
                    "fonts/JosefinSans-Regular.ttf");
            for (int i = 0; i < checksize; i++) {
                String valueJson = jsonArray.getString(i);
                Log.e("json", i + "=" + valueJson);
                final JSONObject jsonObj = new JSONObject(valueJson);
                final CheckBox cb = new CheckBox(context);
                cb.setText(jsonObj.getString("displayName"));
                cb.setTypeface(tyface);
                final String name = jsonObj.getString("name");
                for (int j = 0; j < passFormula.size(); j++) {
                    String splitsFormula[] = passFormula.get(j).toString().split(":");

                    if (splitsFormula[0].equalsIgnoreCase(filterList.getCloudSearchIndex().replace("*", "1"))) {
                        if (passFormula.get(j).toString().contains(name)) {

                            cb.setChecked(true);
                            filterList.setExpand(true);
                            lLayout.setVisibility(View.VISIBLE);
                            FilterChips checkBox = new FilterChips(cb.getText().toString(), name, filterList.getDataType(),cb, true);
                            filterChipsList.add(checkBox);
                            filterChipsList = getFilteredChipsList(filterChipsList);
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

                            FilterChips checkBox = new FilterChips(cb.getText().toString(), name, filterList.getDataType(),cb, true);
                            filterChipsList.add(checkBox);
                            filterChipsList = getFilteredChipsList(filterChipsList);

                            passFormula.add(filterList.getCloudSearchIndex().replace("*", "1") + ": '" + name + "' ");

                            filterAdapterCallback.onMethodQuery(passFormula, keyFormula, filterChipsList);

                        } else {
                            FilterChips checkBox = new FilterChips(cb.getText().toString(), name, filterList.getDataType(),cb, true);
                            filterChipsList = removeChipFromList(filterChipsList, checkBox);

                            for (int i = 0; i < passFormula.size(); i++) {
                                if (passFormula.get(i).toString().contains(name)) {
                                    Config.logV("Remove @@@@@@@@@@@@@@@@@@@" + passFormula.get(i).toString());
                                    passFormula.remove(i);
                                    filterAdapterCallback.onMethodQuery(passFormula, keyFormula, filterChipsList);
                                }
                            }
                        }
                    }
                });
                lLayout.addView(cb);

            }

            if (flagShowFull) {
                int checksize1 = jsonArray.length();
                Config.logV("TRUE @@@@@@@@@@2222@@@@@@@@@");
                funCheckBoxLess(jsonArray, mContext, lLayout, filterList, checksize1);
            } else if (jsonArray.length() > 5) {
                CustomTextViewMedium txtMore = new CustomTextViewMedium(mContext);
                txtMore.setText("Show More");
                txtMore.setTextColor(mContext.getResources().getColor(R.color.appoint_theme));
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

    private ArrayList<FilterChips> removeChipFromList(ArrayList<FilterChips> filterChipsList, FilterChips checkBox) {

        ArrayList<FilterChips> chipsList = new ArrayList<>();

        chipsList.addAll(filterChipsList);

        for (int i = 0; i < chipsList.size(); i++) {
            if (chipsList.get(i).getValue().equalsIgnoreCase(checkBox.getValue()) && chipsList.get(i).getType().equalsIgnoreCase(checkBox.getType())) {
                chipsList.remove(i);
            }
        }
        return chipsList;
    }

    private ArrayList<FilterChips> getFilteredChipsList(ArrayList<FilterChips> filterChipsList) {

        ArrayList<FilterChips> chipsList = new ArrayList<>();
        HashMap<String, String> values = new HashMap<String, String>();

        int j = 0;
        for (int i = filterChipsList.size() - 1; i >= 0; i--) {
            if (filterChipsList.get(i).getType().equalsIgnoreCase("Rating")) {
                j = j + 1;
                if (j > 1) {
                    filterChipsList.remove(i);
                }
            }
        }

        for (FilterChips chip : filterChipsList) {
            values.put(chip.getValue(), chip.getType());
        }

        for (String i : values.keySet()) {

            chipsList.add(new FilterChips(i, values.get(i)));
        }

        for (FilterChips obj : chipsList) {

            for (FilterChips chip : filterChipsList) {

                if (chip.getType().equalsIgnoreCase(obj.getType()) && chip.getValue().equalsIgnoreCase(obj.getValue())) {

                    obj.setName(chip.getName());
                    obj.setDeletable(chip.isDeletable());
                    obj.setView(chip.getView());
                }
            }
        }

        return chipsList;
    }

    public void funCheckBoxLess(final JSONArray jsonArray, Context context,
                                final LinearLayout lLayout, final RefinedFilters filterList, final int checksize) {
        try {
            Config.logV("TRUE @@@@@@@@@33333@@@@@@@@@@");
            lLayout.removeAllViews();
            Typeface tyface = Typeface.createFromAsset(mContext.getAssets(),
                    "fonts/JosefinSans-Regular.ttf");
            for (int i = 0; i < checksize; i++) {
                String valueJson = jsonArray.getString(i);
                Log.e("json", i + "=" + valueJson);
                final JSONObject jsonObj = new JSONObject(valueJson);
                final CheckBox cb = new CheckBox(context);
                cb.setText(jsonObj.getString("displayName"));
                cb.setTypeface(tyface);
                final String name = jsonObj.getString("name");
                for (int j = 0; j < passFormula.size(); j++) {
                    String splitsFormula[] = passFormula.get(j).toString().split(":");

                    if (splitsFormula[0].equalsIgnoreCase(filterList.getCloudSearchIndex().replace("*", "1"))) {
                        if (passFormula.get(j).toString().contains(name)) {

                            cb.setChecked(true);
                            filterList.setExpand(true);
                            lLayout.setVisibility(View.VISIBLE);
                            FilterChips checkBox = new FilterChips(cb.getText().toString(), name, filterList.getDataType(),cb, true);
                            filterChipsList.add(checkBox);
                            filterChipsList = getFilteredChipsList(filterChipsList);

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
                            FilterChips checkBox = new FilterChips(cb.getText().toString(), name, filterList.getDataType(),cb, true);

                            filterChipsList.add(checkBox);
                            filterChipsList = getFilteredChipsList(filterChipsList);
                            filterAdapterCallback.onMethodQuery(passFormula, keyFormula, filterChipsList);


                        } else {

                            FilterChips checkBox = new FilterChips(cb.getText().toString(), name, filterList.getDataType(),cb, true);
                            filterChipsList = removeChipFromList(filterChipsList, checkBox);

                            for (int i = 0; i < passFormula.size(); i++) {
                                if (passFormula.get(i).toString().contains(name)) {
                                    Config.logV("Remove @@@@@@@@@@@@@@@@@@@" + passFormula.get(i).toString());
                                    passFormula.remove(i);
                                    filterAdapterCallback.onMethodQuery(passFormula, keyFormula, filterChipsList);
                                }
                            }
                        }
                    }
                });
                lLayout.addView(cb);
            }

            CustomTextViewMedium txtless = new CustomTextViewMedium(mContext);

            txtless.setText("Show Less");
            txtless.setTextColor(mContext.getResources().getColor(R.color.appoint_theme));
            lLayout.addView(txtless);
            txtless.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    funCheckBoxMore(jsonArray, mContext, lLayout, filterList, 5, "not");

                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static class RecyclerTouchListener implements RecyclerView.OnItemTouchListener {

        private ClickListener clicklistener;
        private GestureDetector gestureDetector;

        public RecyclerTouchListener(Context context, final RecyclerView recycleView, final ClickListener clicklistener) {

            this.clicklistener = clicklistener;
            gestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
                @Override
                public boolean onSingleTapUp(MotionEvent e) {
                    return true;
                }

                @Override
                public void onLongPress(MotionEvent e) {
                    View child = recycleView.findChildViewUnder(e.getX(), e.getY());
                    if (child != null && clicklistener != null) {
                        clicklistener.onLongClick(child, recycleView.getChildAdapterPosition(child));
                    }
                }
            });
        }

        @Override
        public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
            View child = rv.findChildViewUnder(e.getX(), e.getY());
            if (child != null && clicklistener != null && gestureDetector.onTouchEvent(e)) {
                clicklistener.onClick(child, rv.getChildAdapterPosition(child));
            }

            return false;
        }

        @Override
        public void onTouchEvent(RecyclerView rv, MotionEvent e) {

        }

        @Override
        public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

        }
    }

    public void onChipRemoved(View view) {

        if (view instanceof CheckBox) {
            CheckBox cb = (CheckBox) view;
            cb.setChecked(false);
        } else if (view instanceof ImageView){
            ImageView ivRating = (ImageView) view;
            ivRating.callOnClick();
        } else if (view instanceof Switch){
            Switch aSwitch = (Switch) view;
            aSwitch.setChecked(false);
        } else if (view instanceof EditText){
            EditText editText = (EditText) view;
            editText.setText("");
        }
    }


}
