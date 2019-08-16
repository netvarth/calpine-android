package com.jaldeeinc.jaldee.Fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jaldeeinc.jaldee.R;
import com.jaldeeinc.jaldee.adapter.DeptListAdapter;
import com.jaldeeinc.jaldee.callback.SearchLocationAdpterCallback;
import com.jaldeeinc.jaldee.model.SearchListModel;
import com.jaldeeinc.jaldee.response.SearchDepartment;
import com.jaldeeinc.jaldee.response.SearchLocation;
import com.jaldeeinc.jaldee.response.SearchService;

import java.util.List;


public class DepartmentFragment extends Fragment {
    Context mContext;
    String mTitle;
    List<SearchService> mSearchServiceList;
    private SearchLocationAdpterCallback adaptercallback;
    SearchDepartment department;
    List<SearchListModel> msearchList;
    //   RecyclerView mdepartment_searchresult;
    private DeptListAdapter deptListAdapter;
    LinearLayoutManager linearLayoutManager;
    SearchDetailViewFragment searchDetailViewFragment;
    private List<SearchLocation> mSearchLocationList;

    public DepartmentFragment() {
        // Required empty public constructor
    }

    @SuppressLint("ValidFragment")
    public DepartmentFragment(SearchDepartment department, List<SearchListModel> searchList, SearchDetailViewFragment searchDetailViewFragment) {
        this.msearchList = searchList;
        this.department = department;
        this.searchDetailViewFragment = searchDetailViewFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View row = inflater.inflate(R.layout.fragment_department, container, false);
        TextView tv_mdepartment, tv_mdepartmentCode,tv_service,tv_doctor;
        LinearLayout L_service;
        tv_mdepartment = (TextView) row.findViewById(R.id.departmentName);
        tv_mdepartmentCode = (TextView) row.findViewById(R.id.departmentCode);
        L_service = (LinearLayout)row.findViewById(R.id.Dservice);
        tv_service = (TextView) row.findViewById(R.id.service);
        tv_doctor = (TextView) row.findViewById(R.id.Ddoctor);

        int count = 0;
        if (msearchList != null) {
            count = msearchList.size();

            tv_mdepartment.setText(department.getDepartmentName());
            tv_mdepartmentCode.setText(department.getDepartmentCode());
        } else {
            tv_mdepartment.setVisibility(View.GONE);
            tv_mdepartmentCode.setVisibility(View.GONE
            );
        }

        // Inflate the layout for this fragment

        for (int i = 0; i < mSearchServiceList.size(); i++) {
            final int finalI = i;
            tv_service.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    adaptercallback.onMethodServiceCallback(mSearchServiceList.get(finalI).getmAllService(), mTitle);
                }
            });
        }
        return inflater.inflate(R.layout.fragment_department, container, false);
    }
}
