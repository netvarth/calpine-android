package com.jaldeeinc.jaldee.Fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.jaldeeinc.jaldee.R;
import com.jaldeeinc.jaldee.adapter.DepartmentsListAdapter;
import com.jaldeeinc.jaldee.adapter.DeptListAdapter;
import com.jaldeeinc.jaldee.adapter.SpecializationListAdapter;
import com.jaldeeinc.jaldee.model.SearchListModel;
import com.jaldeeinc.jaldee.response.SearchDepartment;
import com.jaldeeinc.jaldee.response.SearchViewDetail;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class DeptListFragment extends RootFragment {
    Context mContext;
    ArrayList<SearchDepartment> department_List;
    TextView tv_subtitle;
    RecyclerView departmentList;
    DepartmentsListAdapter mAdapter;
    String from;
    String businessName;
    ArrayList department;
    HashMap<String, List<SearchListModel>> mdepartmentMap;


    public DeptListFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View row = inflater.inflate(R.layout.fragment_dept_list, container, false);
        mContext = getActivity();
        Bundle bundle = this.getArguments();
        department_List = new ArrayList<>();

        if (bundle != null) {
            businessName = bundle.getString("businessName", "");
            from = bundle.getString("from", "");
            department = bundle.getStringArrayList("Departments");

        if (department != null && department.size() > 0) {
            for (int i = 0; i < department.size(); i++) {
                SearchDepartment data = new SearchDepartment();
                data.setDepartmentName(department.get(i).toString());
                department_List.add(data);
            }
        }

        departmentList = (RecyclerView) row.findViewById(R.id.mrecycle_department);
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
        tv_subtitle = (TextView) row.findViewById(R.id.txt_departments);
        tv_subtitle.setText("Departments");

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(mContext);
        departmentList.setLayoutManager(mLayoutManager);
        mAdapter = new DepartmentsListAdapter(department_List, mContext, from, businessName);
        mAdapter.setFields(department_List,mdepartmentMap,businessName);
        departmentList.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();



    }

        return row;


    }


}

