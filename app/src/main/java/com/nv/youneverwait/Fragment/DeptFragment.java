package com.nv.youneverwait.Fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.nv.youneverwait.R;
import com.nv.youneverwait.adapter.DeptListAdapter;
import com.nv.youneverwait.adapter.PaginationAdapter;
import com.nv.youneverwait.model.SearchListModel;
import com.nv.youneverwait.response.SearchDepartment;
import java.util.List;

@SuppressLint("ValidFragment")
public class DeptFragment extends RootFragment {

    Context mContext;

    SearchDepartment department;
    List<SearchListModel> msearchList;
    RecyclerView mdepartment_searchresult;
    private DeptListAdapter deptListAdapter;
    LinearLayoutManager linearLayoutManager;
    SearchDetailViewFragment searchDetailViewFragment;




    public DeptFragment(SearchDepartment department, List<SearchListModel> searchList, SearchDetailViewFragment searchDetailViewFragment) {
        this.msearchList = searchList;
        this.department = department;
        this.searchDetailViewFragment = searchDetailViewFragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View row = inflater.inflate(R.layout.departmentview, container, false);
        mdepartment_searchresult = (RecyclerView) row.findViewById(R.id.department_searchresult);



        TextView tv_title = (TextView) row.findViewById(R.id.toolbartitle);

        ImageView iBackPress=(ImageView)row.findViewById(R.id.backpress) ;
        iBackPress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // what do you want here
                getFragmentManager().popBackStack();
            }
        });

        int count = 0;
        if(msearchList!= null){
            count = msearchList.size();
        }
        tv_title.setText(department.getDepartmentName()+ " "+"("+ count +")");


        linearLayoutManager = new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false);
        deptListAdapter = new DeptListAdapter(getActivity(),msearchList,searchDetailViewFragment);
        mdepartment_searchresult.setAdapter(deptListAdapter);
        mdepartment_searchresult.setLayoutManager(linearLayoutManager);
        deptListAdapter.notifyDataSetChanged();


        return row;


    }
}
