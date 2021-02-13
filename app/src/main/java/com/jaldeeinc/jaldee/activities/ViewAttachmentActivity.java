package com.jaldeeinc.jaldee.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.jaldeeinc.jaldee.CustomSwipe.DiscreteScrollView;
import com.jaldeeinc.jaldee.CustomSwipe.transform.ScaleTransformer;
import com.jaldeeinc.jaldee.R;
import com.jaldeeinc.jaldee.adapter.OrderListImagesAdapter;
import com.jaldeeinc.jaldee.response.ShoppingList;
import com.jaldeeinc.jaldee.response.ViewAttachments;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ViewAttachmentActivity extends AppCompatActivity {

    @BindView(R.id.rv_preview)
    DiscreteScrollView rvPreview;

    private Context mContext;
    private ArrayList<ShoppingList> imagesList= new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_attachment);
        ButterKnife.bind(ViewAttachmentActivity.this);
        mContext = ViewAttachmentActivity.this;

        Intent intent= getIntent();
        imagesList = (ArrayList<ShoppingList>) intent.getSerializableExtra("imagesList");

        if (imagesList != null && imagesList.size() > 0){

            OrderListImagesAdapter imagePreviewAdapter = new OrderListImagesAdapter(imagesList, mContext, false);
            rvPreview.setAdapter(imagePreviewAdapter);
            rvPreview.setItemTransformer(new ScaleTransformer.Builder()
                    .setMinScale(0.8f)
                    .build());
        }

    }
}