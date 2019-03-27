package com.nv.youneverwait.Fragment;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.nv.youneverwait.R;
import com.nv.youneverwait.common.Config;
import com.nv.youneverwait.model.WorkingModel;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;


/**
 * Created by sharmila on 31/8/18.
 */

public class WorkingHourFragment extends RootFragment {


    public WorkingHourFragment() {
        // Required empty public constructor
    }


    Context mContext;

    TextView txt_workinghr_mon,txt_workinghr_tue,txt_workinghr_wed,txt_workinghr_thu,txt_workinghr_fri,txt_workinghr_sat,txt_workinghr_sun;
    ArrayList<WorkingModel> workingHrList=new ArrayList<>();
    String txtdataMon="",txtdataTue="",txtdataWed="",txtdataThu="",txtdataFri="",txtdataSat="",txtdataSun="";
    TextView tv_Monday,tv_Tuesday,tv_Wednesday,tv_Thursay,tv_Friday,tv_Saturday,tv_Sunday;
    TextView tv_subtitle;
    String title,uniqueid;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View row = inflater.inflate(R.layout.workinghrs, container, false);

        mContext = getActivity();
        Bundle bundle = this.getArguments();
         txtdataMon="";txtdataTue="";txtdataWed="";txtdataThu="";txtdataFri="";txtdataSat="";txtdataSun="";
        if (bundle != null) {
            workingHrList = (ArrayList<WorkingModel>)getArguments().getSerializable("workinghrlist");
            title=bundle.getString("title", "");
            uniqueid=bundle.getString("uniqueID", "");
        }


        Config.logV("Working Hrs------------------"+workingHrList.size());

        tv_subtitle=(TextView)row.findViewById(R.id.txttitle) ;
        txt_workinghr_mon=(TextView)row.findViewById(R.id.txt_workinghr_mon);
        txt_workinghr_tue=(TextView)row.findViewById(R.id.txt_workinghr_tue);
        txt_workinghr_wed=(TextView)row.findViewById(R.id.txt_workinghr_wed);
        txt_workinghr_thu=(TextView)row.findViewById(R.id.txt_workinghr_thu);
        txt_workinghr_fri=(TextView)row.findViewById(R.id.txt_workinghr_fri);
        txt_workinghr_sat=(TextView)row.findViewById(R.id.txt_workinghr_sat);
        txt_workinghr_sun=(TextView)row.findViewById(R.id.txt_workinghr_sun);


        tv_Monday=(TextView)row.findViewById(R.id.txtMonday);
        tv_Tuesday=(TextView)row.findViewById(R.id.txtTuesday);
        tv_Wednesday=(TextView)row.findViewById(R.id.txtWednesday);
        tv_Thursay=(TextView)row.findViewById(R.id.txtThursday);
        tv_Friday=(TextView)row.findViewById(R.id.txtFriday);
        tv_Saturday=(TextView)row.findViewById(R.id.txtSaturday);
        tv_Sunday=(TextView)row.findViewById(R.id.txtSunday);

        Typeface tyface = Typeface.createFromAsset(getActivity().getAssets(),
                "fonts/Montserrat_Bold.otf");
        tv_Monday.setTypeface(tyface);
        tv_Tuesday.setTypeface(tyface);
        tv_Wednesday.setTypeface(tyface);
        tv_Thursay.setTypeface(tyface);
        tv_Friday.setTypeface(tyface);
        tv_Saturday.setTypeface(tyface);
        tv_Sunday.setTypeface(tyface);





        tv_subtitle.setText(title);

        tv_subtitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();

                SearchDetailViewFragment pfFragment = new SearchDetailViewFragment();

                bundle.putString("uniqueID",uniqueid);
                pfFragment.setArguments(bundle);

                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                //transaction.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left,R.anim.slide_out_right, R.anim.slide_in_left);
                // Store the Fragment in stack
                transaction.addToBackStack(null);
                transaction.add(R.id.mainlayout, pfFragment).commit();
            }
        });

        TextView tv_title = (TextView) row.findViewById(R.id.toolbartitle);
        Typeface tyface1 = Typeface.createFromAsset(mContext.getAssets(),
                "fonts/Montserrat_Bold.otf");
        tv_title.setTypeface(tyface1);

        ImageView iBackPress=(ImageView)row.findViewById(R.id.backpress) ;
        iBackPress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // what do you want here
               getFragmentManager().popBackStack();
            }
        });
        tv_title.setText("Working Hours");

        for(int i=0;i<workingHrList.size();i++){
            if(workingHrList.get(i).getDay().equalsIgnoreCase("Monday")){
                if(workingHrList.get(i).getTime_value()!=null)
                txtdataMon+=workingHrList.get(i).getTime_value()+"\n";
            }
            if(workingHrList.get(i).getDay().equalsIgnoreCase("Tuesday")){
                if(workingHrList.get(i).getTime_value()!=null)
                txtdataTue+=workingHrList.get(i).getTime_value()+"\n";
            }
            if(workingHrList.get(i).getDay().equalsIgnoreCase("Wednesday")){
                if(workingHrList.get(i).getTime_value()!=null)
                txtdataWed+=workingHrList.get(i).getTime_value()+"\n";
            }
            if(workingHrList.get(i).getDay().equalsIgnoreCase("Thursday")){
                if(workingHrList.get(i).getTime_value()!=null)
                txtdataThu+=workingHrList.get(i).getTime_value()+"\n";
            }
            if(workingHrList.get(i).getDay().equalsIgnoreCase("Friday")){
                if(workingHrList.get(i).getTime_value()!=null)
                txtdataFri+=workingHrList.get(i).getTime_value()+"\n";
            }
            if(workingHrList.get(i).getDay().equalsIgnoreCase("Saturday")){
                if(workingHrList.get(i).getTime_value()!=null)
                txtdataSat+=workingHrList.get(i).getTime_value()+"\n";
            }
            if(workingHrList.get(i).getDay().equalsIgnoreCase("Sunday")){
                if(workingHrList.get(i).getTime_value()!=null)
                txtdataSun+=workingHrList.get(i).getTime_value()+"\n";
            }

        }


        SimpleDateFormat sdf = new SimpleDateFormat("EEEE");
        Date d = new Date();
        String dayOfTheWeek = sdf.format(d);

       if(dayOfTheWeek.equalsIgnoreCase("Sunday")) {
           txt_workinghr_sun.setTextColor(mContext.getResources().getColor(R.color.title_grey));
           tv_Sunday.setTextColor(mContext.getResources().getColor(R.color.title_grey));
           txt_workinghr_sun.setTypeface(tyface1);
       }

        if(dayOfTheWeek.equalsIgnoreCase("Saturday")) {
            txt_workinghr_sat.setTextColor(mContext.getResources().getColor(R.color.title_grey));
            tv_Saturday.setTextColor(mContext.getResources().getColor(R.color.title_grey));
            txt_workinghr_sat.setTypeface(tyface1);
        }
        if(dayOfTheWeek.equalsIgnoreCase("Monday")) {
            txt_workinghr_mon.setTextColor(mContext.getResources().getColor(R.color.title_grey));
            tv_Monday.setTextColor(mContext.getResources().getColor(R.color.title_grey));
            txt_workinghr_mon.setTypeface(tyface1);
        }
        if(dayOfTheWeek.equalsIgnoreCase("Tuesday")) {
            txt_workinghr_tue.setTextColor(mContext.getResources().getColor(R.color.title_grey));
            tv_Tuesday.setTextColor(mContext.getResources().getColor(R.color.title_grey));
            txt_workinghr_tue.setTypeface(tyface1);
        }
        if(dayOfTheWeek.equalsIgnoreCase("Wednesday")) {
            txt_workinghr_wed.setTextColor(mContext.getResources().getColor(R.color.title_grey));
            tv_Wednesday.setTextColor(mContext.getResources().getColor(R.color.title_grey));
            txt_workinghr_wed.setTypeface(tyface1);
        }
        if(dayOfTheWeek.equalsIgnoreCase("Thursday")) {
            txt_workinghr_thu.setTextColor(mContext.getResources().getColor(R.color.title_grey));
            tv_Thursay.setTextColor(mContext.getResources().getColor(R.color.title_grey));
            txt_workinghr_thu.setTypeface(tyface1);
        }
        if(dayOfTheWeek.equalsIgnoreCase("Friday")) {
            txt_workinghr_fri.setTextColor(mContext.getResources().getColor(R.color.title_grey));
            tv_Friday.setTextColor(mContext.getResources().getColor(R.color.title_grey));
            txt_workinghr_fri.setTypeface(tyface1);
        }



        if(!txtdataSun.equalsIgnoreCase("")) {
            txt_workinghr_sun.setText(txtdataSun);
        }else{
            txt_workinghr_sun.setText("CLOSED"+"\n");
        }

        if(!txtdataMon.equalsIgnoreCase("")) {
            txt_workinghr_mon.setText(txtdataMon);
        }else{
            txt_workinghr_mon.setText("CLOSED"+"\n");
        }


        if(!txtdataTue.equalsIgnoreCase("")) {
            txt_workinghr_tue.setText(txtdataTue);
        }else{
            txt_workinghr_tue.setText("CLOSED"+"\n");
        }

        if(!txtdataWed.equalsIgnoreCase("")) {
            txt_workinghr_wed.setText(txtdataWed);
        }else{
            txt_workinghr_wed.setText("CLOSED"+"\n");
        }

        if(!txtdataThu.equalsIgnoreCase("")) {
            txt_workinghr_thu.setText(txtdataThu);
        }else {

            txt_workinghr_thu.setText("CLOSED"+"\n");
        }



        if(!txtdataFri.equalsIgnoreCase("")) {
            txt_workinghr_fri.setText(txtdataFri);
        }else {

            txt_workinghr_fri.setText("CLOSED"+"\n");
        }


        if(!txtdataSat.equalsIgnoreCase("")) {
            txt_workinghr_sat.setText(txtdataSat);
        }else {

            txt_workinghr_sat.setText("CLOSED"+"\n");
        }





        return row;
    }


}