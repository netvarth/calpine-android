package com.jaldeeinc.jaldee.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.jaldeeinc.jaldee.R;
import com.jaldeeinc.jaldee.custom.CustomTextViewMedium;
import com.jaldeeinc.jaldee.custom.KeyValue;

import java.util.ArrayList;

public class FilterCardsAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<KeyValue> list;

    public FilterCardsAdapter(Context context, ArrayList<KeyValue> list) {
        this.context = context;
        this.list = list;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {

        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        ViewHolder viewHolder = null;

        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.domain_layout, null);
            viewHolder = new ViewHolder();
            viewHolder.tvName = (CustomTextViewMedium) view.findViewById(R.id.tv_domainName);

            view.setTag(viewHolder);
        } else {

            viewHolder = (ViewHolder) view.getTag();
        }

        viewHolder.tvName.setText(list.get(position).getKey());

        return view;
    }

    public class ViewHolder {
        public CustomTextViewMedium tvName;

    }
}