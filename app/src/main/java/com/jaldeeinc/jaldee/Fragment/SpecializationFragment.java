package com.jaldeeinc.jaldee.Fragment;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.jaldeeinc.jaldee.R;
import com.jaldeeinc.jaldee.adapter.SpecializationListAdapter;
import com.jaldeeinc.jaldee.response.SearchViewDetail;

import java.util.ArrayList;

public class SpecializationFragment extends Fragment {
    public SpecializationFragment() {
        // Required empty public constructor
    }

    Context mContext;
    ArrayList<SearchViewDetail> specializationList_Detail;
    TextView tv_subtitle;
    RecyclerView mrecycle_specialization;
    SpecializationListAdapter mAdapter;
    String from;
    String businessName;
    ArrayList specialization;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View row = inflater.inflate(R.layout.fragment_specialization, container, false);
        mContext = getActivity();
        Bundle bundle = this.getArguments();
        specializationList_Detail = new ArrayList<>();

        if (bundle != null) {
            businessName = bundle.getString("businessName", "");
            from = bundle.getString("from", "");
            specialization = bundle.getStringArrayList("Specialization_displayname");

            if(specialization !=null && specialization.size() > 0) {
                for (int i = 0; i < specialization.size(); i++) {
                    SearchViewDetail data = new SearchViewDetail();
                    data.setName(specialization.get(i).toString());
                    specializationList_Detail.add(data);
                }
            }
            mrecycle_specialization = (RecyclerView) row.findViewById(R.id.mrecycle_specialization);
            TextView tv_title = (TextView) row.findViewById(R.id.toolbartitle);
            Typeface tyface1 = Typeface.createFromAsset(mContext.getAssets(),
                    "fonts/Montserrat_Bold.otf");
            tv_title.setTypeface(tyface1);

            ImageView iBackPress = (ImageView) row.findViewById(R.id.backpress);
            iBackPress.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // what do you want here
                    getFragmentManager().popBackStack();
                }
            });
            tv_title.setText(businessName);
            tv_subtitle = (TextView) row.findViewById(R.id.txt_specialization);
            tv_subtitle.setText("Specializations");

            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(mContext);
            mrecycle_specialization.setLayoutManager(mLayoutManager);
            mAdapter = new SpecializationListAdapter(specializationList_Detail, mContext, from, businessName);
            mrecycle_specialization.setAdapter(mAdapter);
            mAdapter.notifyDataSetChanged();

        }
        return row;
    }
}
