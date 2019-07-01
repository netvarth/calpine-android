package com.nv.youneverwait.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.graphics.drawable.LayerDrawable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RatingBar;
import android.widget.Switch;
import android.widget.TextView;

import com.google.gson.Gson;
import com.nv.youneverwait.R;
import com.nv.youneverwait.callback.FilterAdapterCallback;
import com.nv.youneverwait.common.Config;
import com.nv.youneverwait.response.RefinedFilters;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by sharmila on 7/6/19.
 */

public class FilterAdapter extends RecyclerView.Adapter<FilterAdapter.MyViewHolder> {

    private List<RefinedFilters> mFilterList;
    Context mContext;
    FilterAdapterCallback filterAdapterCallback;


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

    Activity activity;

    public FilterAdapter(List<RefinedFilters> mFilterList, Context mContext, Activity mActivity, FilterAdapterCallback filterAdapterCallback) {
        this.mContext = mContext;
        this.mFilterList = mFilterList;
        this.activity = mActivity;
        this.filterAdapterCallback = filterAdapterCallback;

        for(int i=0;i<mFilterList.size();i++){
            if(mFilterList.get(i).getDisplayName().equalsIgnoreCase("Other Filter")) {
                Collections.swap(mFilterList, i, mFilterList.size()-1);
            }
        }
    }

    @Override
    public FilterAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.filter_row, parent, false);

        return new FilterAdapter.MyViewHolder(itemView);
    }

    ArrayList<String> passFormula = new ArrayList<>();
    ArrayList<String> keyFormula = new ArrayList<>();

    @Override
    public void onBindViewHolder(final FilterAdapter.MyViewHolder myViewHolder, final int position) {
        final RefinedFilters filterList = mFilterList.get(position);

        Typeface tyface = Typeface.createFromAsset(mContext.getAssets(),
                "fonts/Montserrat_Bold.otf");
        myViewHolder.tv_name.setText(filterList.getDisplayName());

        myViewHolder.tv_name.setTypeface(tyface);

        if (position == mFilterList.size() - 1) {
            myViewHolder.mView.setVisibility(View.GONE);
        } else {
            myViewHolder.mView.setVisibility(View.VISIBLE);
        }

        myViewHolder.Ldisplay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Config.logV("filterList.isExpand()" + filterList.isExpand());
                if (!filterList.isExpand()) {
                    filterList.setExpand(true);

                    if (filterList.getDataType().equalsIgnoreCase("EnumList") || filterList.getDataType().equalsIgnoreCase("Enum")) {
                        myViewHolder.LexpandView.setVisibility(View.VISIBLE);
                        myViewHolder.LexpandView.removeAllViews();
                        if (filterList.getEnumeratedConstants() instanceof String) {
                            Config.logV("String ");
                        } else {
                            Config.logV("Array " + new Gson().toJson(filterList.getEnumeratedConstants()));
                            final String resp = new Gson().toJson(filterList.getEnumeratedConstants());
                            //  keyFormula.add(filterList.getCloudSearchIndex().replace("*", "1"));

                            boolean fAvailable=false;
                            for(int i=0;i<keyFormula.size();i++){
                                if(keyFormula.get(i).toString().equalsIgnoreCase(filterList.getCloudSearchIndex().replace("*", "1"))){
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


                                if(jsonArray.length()>5){
                                    funCheckBoxMore(jsonArray,mContext,myViewHolder.LexpandView,filterList,5);
                                }else{
                                    try {
                                        for (int i = 0; i < jsonArray.length(); i++) {
                                            String valueJson = jsonArray.getString(i);
                                            Log.e("json", i + "=" + valueJson);
                                            final JSONObject jsonObj = new JSONObject(valueJson);
                                            final CheckBox cb = new CheckBox(v.getContext());
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


                                                        passFormula.add(filterList.getCloudSearchIndex().replace("*", "1") + ": '" + name + "' ");
                                                        filterAdapterCallback.onMethodFilterCallback(passFormula, keyFormula);

                                                    } else {
                                                        for (int i = 0; i < passFormula.size(); i++) {
                                                            if (passFormula.get(i).toString().contains(name)) {
                                                                Config.logV("Remove @@@@@@@@@@@@@@@@@@@" + passFormula.get(i).toString());
                                                                passFormula.remove(i);
                                                                filterAdapterCallback.onMethodFilterCallback(passFormula, keyFormula);
                                                            }
                                                        }
                                                    }
                                                }
                                            });
                                            myViewHolder.LexpandView.addView(cb);


                                        }
                                    }catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }


                            } catch (JSONException e) {
                                e.printStackTrace();
                            }


                        }

                    } else if (filterList.getDataType().equalsIgnoreCase("Rating")) {
                        myViewHolder.LexpandView.setVisibility(View.VISIBLE);
                        // keyFormula.add(filterList.getCloudSearchIndex());
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
                   /* RatingBar rating = new RatingBar(v.getContext());
                    LayerDrawable stars = (LayerDrawable) rating.getProgressDrawable();
                    stars.getDrawable(2).setColorFilter(ContextCompat.getColor(mContext, R.color.active_yellow), PorterDuff.Mode.SRC_ATOP);
                    myViewHolder.LexpandView.addView(rating);*/

                        LinearLayout parent = new LinearLayout(mContext);

                        parent.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                        parent.setOrientation(LinearLayout.HORIZONTAL);


                        ContextThemeWrapper contextThemeWrapper = new ContextThemeWrapper(mContext, R.style.ImageRatingBar);
                        final RatingBar ratingBar = new RatingBar(contextThemeWrapper, null, 0);
                        //final RatingBar ratingBar = new RatingBar(mContext);

                        // linearLayout.addView(ratingBar);
                        ratingBar.setNumStars(5);
                        ratingBar.setStepSize((float) 0.5);

                        ratingBar.setScaleX((float) .75);
                        ratingBar.setScaleY((float) .75);




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
                                    for (int j = 0; j < passFormula.size(); j++) {
                                        String splitsFormula[]=passFormula.get(j).toString().split(":");
                                        Config.logV("passF RemoNERKK ##@@DD ##"+splitsFormula[0]);
                                        if (splitsFormula[0].equalsIgnoreCase("rating")) {
                                            Config.logV("passF Remove"+passFormula.get(j));
                                            passFormula.remove(j);

                                        }
                                    }


                                    filterAdapterCallback.onMethodFilterCallback(passFormula, keyFormula);
                                    img.setVisibility(View.GONE);
                                }
                            });
                        }


                        if (myViewHolder.LexpandView!= null) {
                            myViewHolder.LexpandView.addView(parent);
                        }


                        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
                            @Override
                            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                                float rateValue = ratingBar.getRating();
                                Config.logV("Rating Vlue @@@@@@@@" + rateValue);

                                Config.logV("Rating FFFF@@@@@@@@@@@@@@" + filterList.getCloudSearchIndex());

                                img.setVisibility(View.VISIBLE);
                                for (int j = 0; j < passFormula.size(); j++) {
                                    String splitsFormula[]=passFormula.get(j).toString().split(":");
                                    Config.logV("passF RemoNERKK ##@@DD ##"+splitsFormula[0]);
                                    if (splitsFormula[0].equalsIgnoreCase("rating")) {
                                        Config.logV("passF Remove"+passFormula.get(j));
                                        passFormula.remove(j);

                                    }
                                }

                                passFormula.add(filterList.getCloudSearchIndex() + ": '" + rateValue + "' ");

                                filterAdapterCallback.onMethodFilterCallback(passFormula, keyFormula);
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
                            // keyFormula.add(filterList.getCloudIndexvalue().get(i).toString().replace("*", "1"));
                            boolean fAvailable=false;
                            for(int j=0;j<keyFormula.size();j++){
                                if(keyFormula.get(j).toString().equalsIgnoreCase(filterList.getCloudIndexvalue().get(i).toString().replace("*", "1"))){
                                    //No neeed to add

                                    fAvailable=true;
                                    break;
                                }
                            }

                            if(!fAvailable){
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

                                        filterAdapterCallback.onMethodFilterCallback(passFormula, keyFormula);
                                    } else {

                                        for (int i = 0; i < passFormula.size(); i++) {

                                            if (passFormula.get(i).toString().contains(filterList.getCloudIndexvalue().get(finalI).toString().replace("*", "1"))) {
                                                passFormula.remove(i);
                                                filterAdapterCallback.onMethodFilterCallback(passFormula, keyFormula);

                                            }
                                        }
                                    }
                                }
                            });
                        }






                    }
                } else {
                    myViewHolder.LexpandView.setVisibility(View.GONE);
                    myViewHolder.LexpandView.removeAllViews();
                    filterList.setExpand(false);
                }


            }
        });


    }


    @Override
    public int getItemCount() {
        return mFilterList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    public void funCheckBoxMore(final JSONArray jsonArray, Context context, final LinearLayout lLayout, final RefinedFilters filterList, final int checksize){
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


                            passFormula.add(filterList.getCloudSearchIndex().replace("*", "1") + ": '" + name + "' ");
                            filterAdapterCallback.onMethodFilterCallback(passFormula, keyFormula);

                        } else {
                            for (int i = 0; i < passFormula.size(); i++) {
                                if (passFormula.get(i).toString().contains(name)) {
                                    Config.logV("Remove @@@@@@@@@@@@@@@@@@@" + passFormula.get(i).toString());
                                    passFormula.remove(i);
                                    filterAdapterCallback.onMethodFilterCallback(passFormula, keyFormula);
                                }
                            }
                        }
                    }
                });
                lLayout.addView(cb);


            }

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
                    int checksize=jsonArray.length();
                    funCheckBoxLess(jsonArray,mContext,lLayout,filterList,checksize)  ;
                }
            });
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void funCheckBoxLess(final JSONArray jsonArray, Context context, final LinearLayout lLayout, final RefinedFilters filterList, final int checksize){
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


                            passFormula.add(filterList.getCloudSearchIndex().replace("*", "1") + ": '" + name + "' ");
                            filterAdapterCallback.onMethodFilterCallback(passFormula, keyFormula);

                        } else {
                            for (int i = 0; i < passFormula.size(); i++) {
                                if (passFormula.get(i).toString().contains(name)) {
                                    Config.logV("Remove @@@@@@@@@@@@@@@@@@@" + passFormula.get(i).toString());
                                    passFormula.remove(i);
                                    filterAdapterCallback.onMethodFilterCallback(passFormula, keyFormula);
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

                    funCheckBoxMore(jsonArray,mContext,lLayout,filterList,5);  ;
                }
            });
        }catch (Exception e) {
            e.printStackTrace();
        }
    }
}
