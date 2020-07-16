package com.jaldeeinc.jaldee.adapter;

import android.content.Context;

import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;
import com.jaldeeinc.jaldee.R;
import com.jaldeeinc.jaldee.common.Config;
import com.jaldeeinc.jaldee.response.SearchVirtualFields;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by sharmila on 22/11/18.
 */

public class VirtualFieldAdapter extends RecyclerView.Adapter<VirtualFieldAdapter.VirtualFieldAdapterViewHolder> {
    private List<SearchVirtualFields> virtualFieldList;
    Context context;
    int mSizeshown;

    public VirtualFieldAdapter(List<SearchVirtualFields> virtualFieldList, Context context,int mSizeshown) {
        this.virtualFieldList = virtualFieldList;
        this.context = context;
        this.mSizeshown=mSizeshown;

    }

    @Override
    public VirtualFieldAdapter.VirtualFieldAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //inflate the layout file
        View queueView = LayoutInflater.from(parent.getContext()).inflate(R.layout.virtuallist_listrow, parent, false);
        VirtualFieldAdapterViewHolder gvh = new VirtualFieldAdapterViewHolder(queueView);
        return gvh;
    }

    @Override
    public void onBindViewHolder(final VirtualFieldAdapterViewHolder myViewHolder, int position) {
        //SearchVirtualFields virtualList = virtualFieldList.get(position);
        try {
            Object getrow = virtualFieldList.get(position);
            LinkedTreeMap<Object, Object> t = (LinkedTreeMap) getrow;

            String name = t.get("displayName").toString();
            Log.i("cvbcvb",name);

            String dataType = null;
            if (t.containsKey("dataType")) {
                dataType = t.get("dataType").toString();
            }

            if (name.equalsIgnoreCase("Gender")) {
                myViewHolder.tv_head.setVisibility(View.GONE);
            } else {
                myViewHolder.tv_head.setVisibility(View.VISIBLE);
                myViewHolder.tv_head.setText(name);
            }
//            Typeface tyface = Typeface.createFromAsset(context.getAssets(),
//                    "fonts/Montserrat_Bold.otf");
//            myViewHolder.tv_head.setTypeface(tyface);

            if (dataType != null) {
                if (dataType.equalsIgnoreCase("Enum") || dataType.equalsIgnoreCase("EnumList") || dataType.equalsIgnoreCase("DataGrid")) {
                    myViewHolder.tv_value.setVisibility(View.VISIBLE);

                    if (dataType.equalsIgnoreCase("DataGrid")) {
                        String mergeValue = null;

                        ArrayList value = (ArrayList) t.get("value");


                        String response = new Gson().toJson(value);
                        Config.logV("mergeValue " + response);
                        try {
                            // jsonString is a string variable that holds the JSON
                            JSONArray itemArray = new JSONArray(response);
                            for (int i = 0; i < itemArray.length(); i++) {

                                Config.logV("mergeValue###" + mergeValue);
                                if (i > 0)
                                    mergeValue += "\n";

                                String valueJson = itemArray.getString(i);
                                Log.e("json", i + "=" + valueJson);
                                JSONObject jsonObj = new JSONObject(valueJson);
                                Iterator<?> iter = jsonObj.keys();
                                while (iter.hasNext()) {
                                    String key = (String) iter.next();
                                    Config.logV("key###" + key);
                                    if (i == 0) {
                                        if (mergeValue != null) {
                                            mergeValue += ", " + jsonObj.getString(key);
                                        } else {
                                            mergeValue = jsonObj.getString(key);
                                        }
                                    } else {
                                        mergeValue += jsonObj.getString(key) + ", ";
                                    }
                                    Config.logV("Value###" + mergeValue);
                                }

                                if (mergeValue.endsWith(",")) {
                                    mergeValue = mergeValue.substring(0, mergeValue.length() - 1);
                                }

                            }
                            myViewHolder.tv_value.setText(mergeValue);
                        } catch (JSONException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    } else {
                        myViewHolder.tv_value.setVisibility(View.VISIBLE);
                        Config.logV("Dataype---------" + dataType);
                        String mergeValue = null;

                        ArrayList value = (ArrayList) t.get("value");
                        String response = new Gson().toJson(value);
                        Config.logV("mergeValue " + response);
                        try {
                            // jsonString is a string variable that holds the JSON
                            JSONArray itemArray = new JSONArray(response);
                            for (int i = 0; i < itemArray.length(); i++) {
                                String valueJson = itemArray.getString(i);
                                Log.e("json", i + "=" + valueJson);


                                JSONObject jsonObj = new JSONObject(valueJson);


                                if (mergeValue != null) {
                                    mergeValue += ", " + jsonObj.getString("displayName");
                                } else {
                                    mergeValue = jsonObj.getString("displayName");
                                }

                                Config.logV("Value###" + mergeValue);
                            }

                   /* if (mergeValue.endsWith(",")) {
                        mergeValue = mergeValue.substring(0, mergeValue.length() - 1);
                    }*/
                            myViewHolder.tv_value.setText(mergeValue);
                        } catch (JSONException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    }

                } else {

                    String value = t.get("value").toString();

                    if (dataType.equalsIgnoreCase("Boolean")) {
                        myViewHolder.tv_value.setVisibility(View.VISIBLE);
                        if (value.equalsIgnoreCase("true")) {
                            myViewHolder.tv_value.setText("Yes");
                        } else {
                            myViewHolder.tv_value.setText("No");
                        }
                    } else if (dataType.equalsIgnoreCase("Gender")) {
                        myViewHolder.tv_value.setVisibility(View.GONE);
                    } else {
                        myViewHolder.tv_value.setVisibility(View.VISIBLE);
                        myViewHolder.tv_value.setText(value);
                        if(value.equalsIgnoreCase("")){
                            if(dataType.equalsIgnoreCase("URL") || dataType.equalsIgnoreCase("Enum") || dataType.equalsIgnoreCase("EnumList")){
                                myViewHolder.tv_head.setVisibility(View.GONE);
                            }
                        }
                    }

                }

            } else {
                String value = t.get("value").toString();
                if (name.equalsIgnoreCase("Gender")) {
                    myViewHolder.tv_value.setVisibility(View.GONE);
                } else {
                    myViewHolder.tv_value.setVisibility(View.VISIBLE);
                    myViewHolder.tv_value.setText(value);
                }


            }

        } catch (Exception e) {
            e.printStackTrace();
        }


    }


    @Override
    public int getItemCount() {
        return /*virtualFieldList.size();*/
        mSizeshown;
    }

    public class VirtualFieldAdapterViewHolder extends RecyclerView.ViewHolder {
        TextView tv_head, tv_value;


        public VirtualFieldAdapterViewHolder(View view) {
            super(view);
            tv_head = view.findViewById(R.id.tv_heading);
            tv_value = view.findViewById(R.id.tv_value);



        }
    }
}