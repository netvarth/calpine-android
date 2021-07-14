package com.jaldeeinc.jaldee.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.view.View;
import android.widget.MediaController;
import android.widget.VideoView;

import com.bumptech.glide.Glide;
import com.jaldeeinc.jaldee.R;
import com.jaldeeinc.jaldee.widgets.TouchImageView;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;

public class VideoActivity extends AppCompatActivity {

    @BindView(R.id.vd_videoView)
    VideoView videoView;

    @BindView(R.id.cv_back)
    CardView cvBack;


    String urlOrPath = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);

        ButterKnife.bind(VideoActivity.this);

        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());

        Intent intent = getIntent();
        urlOrPath = intent.getStringExtra("urlOrPath");

        if (urlOrPath != null && !urlOrPath.trim().equalsIgnoreCase("")) {

            if (urlOrPath.contains("http://") || urlOrPath.contains("https://")) {


            } else {

                //Creating MediaController
                MediaController mediaController = new MediaController(this);
                mediaController.setAnchorView(videoView);

                //specify the location of media file
//                Uri uri = Uri.parse(urlOrPath);
                Uri path=Uri.fromFile(new File(Environment.getExternalStorageDirectory().getPath()+urlOrPath));
                //Setting MediaController and URI, then starting the videoView
                videoView.setMediaController(mediaController);
                videoView.setVideoPath(urlOrPath);
                videoView.requestFocus();
                videoView.start();


            }
            videoView.setVisibility(View.VISIBLE);
        }

        cvBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                finish();
            }
        });

    }

}