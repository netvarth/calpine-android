package com.jaldeeinc.jaldee.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.jaldeeinc.jaldee.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MaintananceActivity  extends AppCompatActivity {

    @BindView(R.id.cv_submit)
    CardView cv_submit;
    Context mContext;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maintanance);
        mContext = this;

        ButterKnife.bind(MaintananceActivity.this);

        cv_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MaintananceActivity.this, Home.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            }
        });
    }
}
