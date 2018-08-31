package com.netvarth.youneverwait.Fragment;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.google.gson.Gson;
import com.netvarth.youneverwait.R;
import com.netvarth.youneverwait.adapter.FamilyListAdapter;
import com.netvarth.youneverwait.common.Config;
import com.netvarth.youneverwait.connection.ApiClient;
import com.netvarth.youneverwait.connection.ApiInterface;
import com.netvarth.youneverwait.model.FamilyArrayModel;
import com.netvarth.youneverwait.model.FamilyModel;
import com.netvarth.youneverwait.utils.SharedPreference;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by sharmila on 11/7/18.
 */

public class FamilyListFragment extends RootFragment {
    Context mContext;
    Toolbar toolbar;
    static RecyclerView mRecyclefamilyMember;
    static FamilyListAdapter familyAdapter;
    static List<FamilyArrayModel> userProfileList=new ArrayList<>();
    ImageView btnadd;
    Fragment Family;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View row = inflater.inflate(R.layout.fragment_listmember, container, false);

        mContext = getActivity();

        Family=getParentFragment();

        toolbar = (Toolbar) row.findViewById(R.id.toolbar);
        mRecyclefamilyMember=(RecyclerView)row.findViewById(R.id.familyMember);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayShowHomeEnabled(true);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(" Members ");
        btnadd=(ImageView)row.findViewById(R.id.btnadd) ;
        btnadd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString("familymember", "profile");


                FamilyMemberFragment pfFragment = new FamilyMemberFragment();
                pfFragment.setArguments(bundle);
                FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
                // Store the Fragment in stack
                transaction.addToBackStack(null);
                transaction.replace(R.id.mainlayout, pfFragment).commit();
            }
        });

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(mContext);
        mRecyclefamilyMember.setLayoutManager(mLayoutManager);
        familyAdapter = new FamilyListAdapter(userProfileList, mContext,getActivity(),Family);
        mRecyclefamilyMember.setAdapter(familyAdapter);
        familyAdapter.notifyDataSetChanged();
        return row;

    }
    public static  boolean  getFamilyList(List<FamilyArrayModel> muserProfileList, Context mContext){

        userProfileList=muserProfileList;
       return true;
    }

    @Override
    public void onResume() {
        super.onResume();


    }
}
