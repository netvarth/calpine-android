package com.netvarth.youneverwait.adapter;

/**
 * Created by sharmila on 18/7/18.
 */

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.support.v7.widget.SearchView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.netvarth.youneverwait.Fragment.FamilyMemberFragment;
import com.netvarth.youneverwait.Fragment.SearchDetailFragment;
import com.netvarth.youneverwait.Fragment.UpdateProfileFragment;
import com.netvarth.youneverwait.R;
import com.netvarth.youneverwait.common.Config;
import com.netvarth.youneverwait.connection.ApiClient;
import com.netvarth.youneverwait.connection.ApiInterface;
import com.netvarth.youneverwait.custom.CustomTypefaceSpan;
import com.netvarth.youneverwait.model.LanLong;
import com.netvarth.youneverwait.model.ListCell;
import com.netvarth.youneverwait.response.SearchAWsResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SearchListAdpter extends BaseAdapter implements Filterable {

    LayoutInflater inflater;
    ValueFilter valueFilter;
    private ArrayList<ListCell> items;
    private ArrayList<ListCell> filteredItems;
    Context mContext;
    double lantitude, longitude;
    String mSector;
    Fragment mSearchFrag;
    SearchView searchView;

    public SearchListAdpter(Context context, ArrayList<ListCell> item, double lant, double longt, Fragment fragment, SearchView mSearchView) {
        mContext = context;
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
            name.setText(highlight(text, "Business Name As " + cell.getMdisplayname()));
        } else {
            domain.setVisibility(View.VISIBLE);
            domain.setText(cell.getCategory());
            name.setText(highlight(text, cell.getMdisplayname()));
        }

        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Config.logV(" Click---------------");

                //  String AWS_URL=SharedPreference.getInstance(mContext).getStringValue("AWS_URL","");

                searchView.clearFocus();
                searchView.setQuery("", false);

                LanLong Lanlong = getLocationNearBy(lantitude, longitude);
                double upperLeftLat = Lanlong.getUpperLeftLat();
                double upperLeftLon = Lanlong.getUpperLeftLon();
                double lowerRightLat = Lanlong.getLowerRightLat();
                double lowerRightLon = Lanlong.getLowerRightLon();
                String locationRange = "['" + lowerRightLat + "," + lowerRightLon + "','" + upperLeftLat + "," + upperLeftLon + "']";
                String sub = "";
                String querycreate = "";
                mSector = cell.getMsector();
                Config.logV("Sector--------------" + mSector);

                if (cell.getCategory().equalsIgnoreCase("Specializations")) {
                    sub = "specialization:" + "'" + cell.getName() + "'";
                    querycreate = "sector:" + "'" + mSector + "'" + " " + sub;
                }
                if (cell.getCategory().equalsIgnoreCase("Sub Domain")) {

                    sub = "sub_sector:" + "'" + cell.getName() + "'";
                    querycreate = "sector:" + "'" + mSector + "'" + " " + sub;
                }

                if (cell.getCategory().equalsIgnoreCase("Suggested Search")) {
                    Config.logV("Query------------" + cell.getMsector());
                    String requiredString = cell.getMsector().substring(cell.getMsector().indexOf("]") + 1, cell.getMsector().indexOf(")"));
                    Config.logV("Second---------" + requiredString);
                    querycreate = requiredString;
                }

                if (cell.getCategory().equalsIgnoreCase("Business Name as")) {


                    if (mSector.equalsIgnoreCase("All")) {
                        querycreate = "title:" + "'" + cell.getName() + "'";
                    } else {
                        sub = "title:" + "'" + cell.getName() + "'";
                        querycreate = "sector:" + "'" + mSector + "'" + " " + sub;
                    }
                }


                Config.logV("Query-----------" + querycreate);


                String pass = "haversin(11.751416900900901,75.3701820990991, location1.latitude, location1.longitude)";


                //  String newpass = "distance asc, title asc&expr.distance=haversin(" + lantitude + "," + longitude + ", location1.latitude, location1.longitude)";

                Bundle bundle = new Bundle();
                bundle.putString("query", "(and location1:['11.751416900900901,75.3701820990991','9.9496150990991,77.171983900900'] " + querycreate + ")");
                bundle.putString("url", pass);


                //VALID QUERY PASS
               /* bundle.putString("query", "(and location1:"+ locationRange + querycreate + ")");
                  bundle.putString("url",newpass);*/
                SearchDetailFragment pfFragment = new SearchDetailFragment();
                pfFragment.setArguments(bundle);

                FragmentTransaction transaction = mSearchFrag.getFragmentManager().beginTransaction();
                // Store the Fragment in stack
                transaction.addToBackStack(null);
                transaction.replace(R.id.mainlayout, pfFragment).commit();

               /* String pass="distance asc, title asc&expr.distance=haversin(" + lantitude + "," + longitude + ", location1.latitude, location1.longitude)";
                 ApiSEARCHAWS("(and location1:"+ locationRange + querycreate + ")",pass);*/
            }
        });
        return v;
    }


    public LanLong getLocationNearBy(double lant, double longt) {

        double distance = 5;/*;DISTANCE_AREA: 5, // in Km*/

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

                            Config.logV("Same---------------"+filteredItems.get(i).getMdisplayname());
                            ListCell searchmodel = new ListCell(filteredItems.get(i).getName(), filteredItems.get(i).getCategory(), filteredItems.get(i).getMsector(),filteredItems.get(i).getMdisplayname());
                            filterList.add(searchmodel);



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
            items = (ArrayList<ListCell>) results.values;
            notifyDataSetChanged();
        }

    }
    public  CharSequence highlight(String search, String originalText) {
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
                highlighted.setSpan( new CustomTypefaceSpan("sans-serif",tyface1), spanStart, spanEnd, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

            }

            return highlighted;
        }
    }

}