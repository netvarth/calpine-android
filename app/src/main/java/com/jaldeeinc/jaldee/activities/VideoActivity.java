package com.jaldeeinc.jaldee.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.FileProvider;

import android.app.ProgressDialog;
import android.content.Intent;

import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;

import android.view.View;

import android.widget.MediaController;
import android.widget.VideoView;

import com.jaldeeinc.jaldee.BuildConfig;
import com.jaldeeinc.jaldee.R;

import java.io.File;
import java.util.Objects;

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

        Intent intent = getIntent();
        urlOrPath = intent.getStringExtra("urlOrPath");

        if (urlOrPath != null && !urlOrPath.trim().equalsIgnoreCase("")) {

            if (urlOrPath.contains("http://") || urlOrPath.contains("https://")) {

                videoView.setVisibility(View.VISIBLE);

               ProgressDialog pDialog = new ProgressDialog(this);

                // Set progressbar message
                pDialog.setMessage("Buffering...");
                pDialog.setIndeterminate(false);
                pDialog.setCancelable(false);
                // Show progressbar
                pDialog.show();

                try {
                    // Start the MediaController
                    MediaController mediacontroller = new MediaController(this);
                    mediacontroller.setAnchorView(videoView);

                    Uri videoUri = Uri.parse(urlOrPath);
                    videoView.setMediaController(mediacontroller);
                    videoView.setVideoURI(videoUri);
                    videoView.setZOrderOnTop(true);
                    videoView.requestFocus();

                } catch (Exception e) {

                    e.printStackTrace();
                }

                videoView.requestFocus();
                videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                    // Close the progress bar and play the video
                    public void onPrepared(MediaPlayer mp) {
                        pDialog.dismiss();
                        videoView.start();
                    }
                });
                videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

                    public void onCompletion(MediaPlayer mp) {
                        if (pDialog.isShowing()) {
                            pDialog.dismiss();
                        }
                        finish();
                    }
                });

            } else {

                videoView.setVisibility(View.VISIBLE);

                //Creating MediaController
                MediaController mediaController = new MediaController(this);
                mediaController.setMediaPlayer(videoView);

                Uri uri = FileProvider.getUriForFile(Objects.requireNonNull(getApplicationContext()),
                        BuildConfig.APPLICATION_ID + ".provider", new File(urlOrPath));
                videoView.setVideoURI(uri);
                videoView.setMediaController(mediaController);
                videoView.requestFocus();
                videoView.start();


            }
        }

        cvBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                finish();
            }
        });

    }

}