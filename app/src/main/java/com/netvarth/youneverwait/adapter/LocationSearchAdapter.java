package com.netvarth.youneverwait.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;
import com.netvarth.youneverwait.R;
import com.netvarth.youneverwait.common.Config;
import com.netvarth.youneverwait.response.LocationResponse;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Locale;

/**
 * Created by sharmila on 3/8/18.
 */


public class LocationSearchAdapter extends RecyclerView.Adapter<LocationSearchAdapter.MyViewHolder> implements Filterable {

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView tv_loc;


        public MyViewHolder(View view) {
            super(view);
            tv_loc = (TextView) view.findViewById(R.id.txtLoc);

        }
    }

    Context mContext;
    LocationSearchAdapter.ValueFilter valueFilter;
    ArrayList<LocationResponse> items;
    ArrayList<LocationResponse> filteredItems;
    String text = "";

    public LocationSearchAdapter(Context context, ArrayList<LocationResponse> item) {
        mContext = context;
        items = item;
        filteredItems = item;


    }

    @Override
    public LocationSearchAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.searchlocation_row, parent, false);

        return new LocationSearchAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(LocationSearchAdapter.MyViewHolder myViewHolder, final int position) {
        final LocationResponse searchdetailList = items.get(position);
        myViewHolder.tv_loc.setText(searchdetailList.getName());



    }


    @Override
    public Filter getFilter() {
        if (valueFilter == null) {
            valueFilter = new LocationSearchAdapter.ValueFilter();
        }
        return valueFilter;
    }


    private class ValueFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults results = new FilterResults();

            if (constraint != null && constraint.length() > 0) {
                text = constraint.toString();
                ArrayList<LocationResponse> filterList = new ArrayList<LocationResponse>();
                for (int i = 0; i < filteredItems.size(); i++) {

                  /*  if ((String.valueOf(filteredItems.get(i).getName()).toLowerCase())
                            .contains(constraint.toString().toLowerCase())) {*/


                    if ((String.valueOf(filteredItems.get(i).getName()).toLowerCase())
                            .contains(constraint.toString().toLowerCase())) {

                        Config.logV("Same---------------" + filteredItems.get(i).getName());
                        LocationResponse loc=new LocationResponse();
                        loc.setName(filteredItems.get(i).getName());
                        filterList.add(loc);


                    }
                }


                results.count = filterList.size();
                results.values = filterList;
            } else {
                results.count = filteredItems.size();
                results.values = filteredItems;
            }

            return results;

        }

        @Override
        protected void publishResults(CharSequence constraint,
                                      FilterResults results) {
            items = (ArrayList<LocationResponse>) results.values;
            notifyDataSetChanged();
        }

    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public CharSequence highlight(String search, String originalText) {
        // ignore case and accents
        // the same thing should have been done for the search text
        String normalizedText = Normalizer
                .normalize(originalText, Normalizer.Form.NFD)
                .replaceAll("\\p{InCombiningDiacriticalMarks}+", "")
                .toLowerCase(Locale.ENGLISH);

        int start = normalizedText.indexOf(search.toLowerCase(Locale.ENGLISH));
        if (start < 0) {
            // not found, nothing to to
            return originalText;
        } else {
            // highlight each appearance in the original text
            // while searching in normalized text
            Spannable highlighted = new SpannableString(originalText);
            while (start >= 0) {
                int spanStart = Math.min(start, originalText.length());
                int spanEnd = Math.min(start + search.length(),
                        originalText.length());

                highlighted.setSpan(new ForegroundColorSpan(mContext.getResources().getColor(R.color.black)),
                        spanStart, spanEnd, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

                start = normalizedText.indexOf(search, spanEnd);
            }

            return highlighted;
        }
    }
}
