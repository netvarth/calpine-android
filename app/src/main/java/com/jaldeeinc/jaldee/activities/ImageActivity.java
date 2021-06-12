package com.jaldeeinc.jaldee.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;

import com.bumptech.glide.Glide;
import com.jaldeeinc.jaldee.R;
import com.jaldeeinc.jaldee.widgets.TouchImageView;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ImageActivity extends AppCompatActivity {

    @BindView(R.id.imageView)
    TouchImageView ivImage;

    @BindView(R.id.cv_back)
    CardView cvBack;


    String urlOrPath = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image);
        ButterKnife.bind(ImageActivity.this);

        Intent intent = getIntent();
        urlOrPath = intent.getStringExtra("urlOrPath");

        if (urlOrPath != null && !urlOrPath.trim().equalsIgnoreCase("")) {

            if (urlOrPath.contains("http://") || urlOrPath.contains("https://")) {

                Glide.with(ImageActivity.this).load(urlOrPath).into(ivImage);
            } else {
                ivImage.setImageBitmap(BitmapFactory.decodeFile(urlOrPath));

            }
            ivImage.setVisibility(View.VISIBLE);
        }

        cvBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                finish();
            }
        });

    }
}