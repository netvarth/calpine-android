package com.jaldeeinc.jaldee.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.jaldeeinc.jaldee.Fragment.SearchDetailViewFragment;
import com.jaldeeinc.jaldee.R;

public class LinkRedirectionActivity extends AppCompatActivity {

    SearchDetailViewFragment searchDetailViewFragment;
    String keyWord;
    String path;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_link_redirection);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            keyWord = extras.getString("detail_id", "");
            path = extras.getString("path", "");
            Log.i("detailsofDetail",keyWord);
            Log.i("detailsofDetail",path);
        }

        if (keyWord != null) {
            searchDetailViewFragment = new SearchDetailViewFragment();
            Bundle bundle = new Bundle();
            bundle.putString("homeUniqueId", keyWord);
            bundle.putString("home", "home");
            searchDetailViewFragment.setArguments(bundle);
            final FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.container, searchDetailViewFragment).commit();
        }
        else {

            Intent homeIntent = new Intent(LinkRedirectionActivity.this,Home.class);
            startActivity(homeIntent);
            finish();
        }
    }

}