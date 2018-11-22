package com.nv.youneverwait.adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;
import com.nv.youneverwait.R;
import com.nv.youneverwait.common.Config;
import com.nv.youneverwait.response.PaymentModel;
import com.nv.youneverwait.response.SearchVirtualFields;

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


    public VirtualFieldAdapter(List<SearchVirtualFields> virtualFieldList, Context context) {
        this.virtualFieldList = virtualFieldList;
        this.context = context;
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
        Object getrow = virtualFieldList.get(position);
        LinkedTreeMap<Object,Object> t = (LinkedTreeMap) getrow;
        String name = t.get("displayName").toString();

        String dataType=t.get("dataType").toString();
        myViewHolder.tv_head.setText(name);
        Typeface tyface = Typeface.createFromAsset(context.getAssets(),
                "fonts/Montserrat_Bold.otf");
        myViewHolder.tv_head.setTypeface(tyface);

        if(dataType.equalsIgnoreCase("Enum")||dataType.equalsIgnoreCase("EnumList")||dataType.equalsIgnoreCase("DataGrid")){

            String mergeValue="";
            ArrayList value= (ArrayList) t.get("value");


            String response1=new Gson().toJson(value);
            Config.logV("mergeValue "+response1);

        }else{

            String value=t.get("value").toString();

            if(dataType.equalsIgnoreCase("Boolean")){
                if(value.equalsIgnoreCase("true")){
                    myViewHolder.tv_value.setText("Yes");
                }else{
                    myViewHolder.tv_value.setText("No");
                }
            }else{
                myViewHolder.tv_value.setText(value);
            }

        }



    }


    @Override
    public int getItemCount() {
        return virtualFieldList.size();
    }

    public class VirtualFieldAdapterViewHolder extends RecyclerView.ViewHolder {
        TextView tv_head,tv_value;


        public VirtualFieldAdapterViewHolder(View view) {
            super(view);
            tv_head = view.findViewById(R.id.tv_heading);
            tv_value = view.findViewById(R.id.tv_value);


        }
    }
}