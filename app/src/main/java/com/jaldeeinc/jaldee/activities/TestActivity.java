package com.jaldeeinc.jaldee.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.jaldeeinc.jaldee.Fragment.HomeSearchFragment;
import com.jaldeeinc.jaldee.R;
import com.jaldeeinc.jaldee.custom.BorderImageView;
import com.jaldeeinc.jaldee.custom.PicassoTrustAll;

import butterknife.BindView;
import butterknife.ButterKnife;
import jp.wasabeef.picasso.transformations.BlurTransformation;

public class TestActivity extends AppCompatActivity {

//    @BindView(R.id.iv_image)
//    ImageView ivImage;
    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_new);
        ButterKnife.bind(TestActivity.this);
        mContext = TestActivity.this;


//        Glide.with(mContext)
//                .load(R.drawable.new_back)
//                .override(10, 10) // (change according to your wish)
//                .error(R.drawable.ynw_logo_big)
//                .into(ivImage);

    }
}