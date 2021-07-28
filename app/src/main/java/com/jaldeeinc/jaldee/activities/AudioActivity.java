package com.jaldeeinc.jaldee.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.FileProvider;

import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

import com.jaldeeinc.jaldee.BuildConfig;
import com.jaldeeinc.jaldee.R;

import java.io.File;
import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AudioActivity extends AppCompatActivity {

    @BindView(R.id.cv_back)
    CardView cvBack;

    String urlOrPath = null,name = null;

    private MediaPlayer mp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audio);

        ButterKnife.bind(AudioActivity.this);

        Intent intent = getIntent();
        urlOrPath = intent.getStringExtra("urlOrPath");
        name = intent.getStringExtra("name");


        cvBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                finish();
            }
        });


        if (urlOrPath != null && !urlOrPath.trim().equalsIgnoreCase("")) {

            if (urlOrPath.contains("http://") || urlOrPath.contains("https://")) {

                mp = new MediaPlayer();
                try {
                    mp.setDataSource(urlOrPath);//Write your location here
                    mp.prepare();
                    mp.start();

                } catch (Exception e) {
                    e.printStackTrace();
                }

            } else {

                Intent i = new Intent(android.content.Intent.ACTION_VIEW);
                i.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                i.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                i.setDataAndType(FileProvider.getUriForFile(this, BuildConfig.APPLICATION_ID + ".provider",new File(urlOrPath)), "audio/*");
                startActivity(i);

            }

        }


    }

    @Override
    protected void onStop() {
        mp.stop();
        super.onStop();
    }
}