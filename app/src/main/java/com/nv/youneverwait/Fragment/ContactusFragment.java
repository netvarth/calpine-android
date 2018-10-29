package com.nv.youneverwait.Fragment;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.nv.youneverwait.R;
import com.nv.youneverwait.common.Config;

/**
 * Created by sharmila on 25/10/18.
 */

public class ContactusFragment extends RootFragment {
        Context mContext;
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            // Inflate the layout for this fragment

            View row = inflater.inflate(R.layout.contactus, container, false);

            mContext = getActivity();

            TextView tv_title = (TextView) row.findViewById(R.id.toolbartitle);
            TextView txt_appname=(TextView) row.findViewById(R.id.txt_appname);
            TextView txtreach=(TextView) row.findViewById(R.id.txt_appname);
            Typeface tyface1 = Typeface.createFromAsset(mContext.getAssets(),
                    "fonts/Montserrat_Bold.otf");
            tv_title.setTypeface(tyface1);
            txt_appname.setTypeface(tyface1);
            txtreach.setTypeface(tyface1);


            ImageView iBackPress=(ImageView)row.findViewById(R.id.backpress) ;
            iBackPress.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // what do you want here
                    getFragmentManager().popBackStack();
                }
            });
            tv_title.setText("Contact Us");





            return row;
        }
}
