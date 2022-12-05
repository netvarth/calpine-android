package com.jaldeeinc.jaldee.adapter;

/**
 * Created by sharmila on 18/7/18.
 */

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;

import com.jaldeeinc.jaldee.R;
import com.jaldeeinc.jaldee.common.Config;
import com.jaldeeinc.jaldee.custom.CustomTypefaceSpan;
import com.jaldeeinc.jaldee.model.LanLong;
import com.jaldeeinc.jaldee.model.ListCell;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Locale;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;


public class SearchListAdpter extends BaseAdapter implements Filterable {

    private LayoutInflater inflater;
    private ValueFilter valueFilter;
    private ArrayList<ListCell> items;
    private ArrayList<ListCell> filteredItems;
    Context mContext;
    private double lantitude, longitude;
    String mSector;
    Fragment mSearchFrag;
    private SearchView searchView;
    String mCheckClass;
    boolean first = false;
    Activity mActivity;

    public SearchListAdpter(String mCheckClass, Context context, ArrayList<ListCell> item, double lant, double longt, Fragment fragment, SearchView mSearchView) {
        mContext = context;
        this.mCheckClass = mCheckClass;
        items = item;
        filteredItems = item;
        mSearchFrag = fragment;
        searchView = mSearchView;

        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        Config.logV("Item---------------" + items.size());

        lantitude = lant;
        longitude = longt;
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public ListCell getItem(int position) {
        return items.get(position);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        final ListCell cell = getItem(position);

        v = inflater.inflate(R.layout.searchdomain_list_row, null);


        TextView name = (TextView) v.findViewById(R.id.txtservice);
        TextView domain = (TextView) v.findViewById(R.id.txtdomain);

        if (cell.getCategory().equalsIgnoreCase("Business Name as")) {
            domain.setVisibility(View.GONE);
            name.setText(highlight(text, "Business Name/Keyword As " + cell.getMdisplayname()));
        } else if (cell.getCategory().equalsIgnoreCase("Business Id as")) {
            domain.setVisibility(View.GONE);
            name.setText(highlight(text, "Business Id As " + cell.getMdisplayname()));
        } else {
            domain.setVisibility(View.VISIBLE);
            domain.setText(cell.getCategory());
            name.setText(highlight(text, cell.getMdisplayname()));
        }
        if (cell.getCategory().equalsIgnoreCase("Sub Domain")) {
            domain.setText("Suggested Search ");
            name.setText(highlight(text, cell.getMdisplayname()));
        }

        return v;
    }


    public LanLong getLocationNearBy(double lant, double longt) {

        double distance = 60;/*;DISTANCE_AREA: 5, // in Km*/

        double distInDegree = distance / 111;
        double upperLeftLat = lant - distInDegree;
        double upperLeftLon = longt + distInDegree;
        double lowerRightLat = lant + distInDegree;
        double lowerRightLon = longt - distInDegree;
        /*double locationRange = '[\'' + lowerRightLat + ',' + lowerRightLon + '\',\'' + upperLeftLat + ',' + upperLeftLon + '\']';
        double retarr = {'locationRange':locationRange, 'upperLeftLat':upperLeftLat, 'upperLeftLon':
        upperLeftLon, 'lowerRightLat':lowerRightLat, 'lowerRightLon':lowerRightLon};*/

        LanLong lan = new LanLong();
        lan.setUpperLeftLat(upperLeftLat);
        lan.setUpperLeftLon(upperLeftLon);
        lan.setLowerRightLat(lowerRightLat);
        lan.setLowerRightLon(lowerRightLon);

        return lan;

    }

    @Override
    public Filter getFilter() {
        if (valueFilter == null) {
            valueFilter = new ValueFilter();
        }
        return valueFilter;
    }

    String text = "";


    private class ValueFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults results = new FilterResults();

            if (constraint != null && constraint.length() > 0) {
                text = constraint.toString();
                ArrayList<ListCell> filterList = new ArrayList<ListCell>();
                for (int i = 0; i < filteredItems.size(); i++) {

                  /*  if ((String.valueOf(filteredItems.get(i).getName()).toLowerCase())
                            .contains(constraint.toString().toLowerCase())) {*/


                    if ((String.valueOf(filteredItems.get(i).getMdisplayname()).toLowerCase())
                            .contains(constraint.toString().toLowerCase())) {

                        Config.logV("Same---------------" + filteredItems.get(i).getMdisplayname());
                        ListCell searchmodel = new ListCell(filteredItems.get(i).getName(), filteredItems.get(i).getCategory(), filteredItems.get(i).getMsector(), filteredItems.get(i).getMdisplayname());
                        filterList.add(searchmodel);


                    }
                }
                if (filterList != null && !filterList.isEmpty()) {

                    Set<ListCell> filterList1 = filterList.stream().collect(Collectors.toCollection(() -> new TreeSet<>(Comparator.comparing(ListCell::getName))));
                    ArrayList<ListCell> searchmodel1 = filterList.stream().filter(p -> p.getCategory().equals("Business Name as")).collect(Collectors.toCollection(() -> new ArrayList<ListCell>()));
                    filterList.clear();
                    filterList.addAll(filterList1);
                    filterList.addAll(searchmodel1);
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
            items = (ArrayList<ListCell>) results.values;
//           Config.logV("Push Reuslt @@@@@@@@@@@@@@@"+items.size());
            notifyDataSetChanged();
        }

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

                highlighted.setSpan(new ForegroundColorSpan(mContext.getResources().getColor(R.color.title_grey)),
                        spanStart, spanEnd, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

                start = normalizedText.indexOf(search, spanEnd);
                Typeface tyface1 = Typeface.createFromAsset(mContext.getAssets(),
                        "fonts/Montserrat_Bold.otf");
                highlighted.setSpan(new CustomTypefaceSpan("sans-serif", tyface1), spanStart, spanEnd, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

            }

            return highlighted;
        }
    }

}