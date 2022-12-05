package com.jaldeeinc.jaldee.custom;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;

import com.jaldeeinc.jaldee.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ViewNotesDialog extends Dialog {

    private Context context;
    private String message,itemName;

    @BindView(R.id.tv_itemName)
    CustomTextViewSemiBold tvItemName;

    @BindView(R.id.tv_notes)
    CustomTextViewMedium tvNotes;

    @BindView(R.id.iv_close)
    ImageView ivClose;


    public ViewNotesDialog(@NonNull Context context, String  notes, String itemName) {
        super(context);
        this.context = context;
        this.message = notes;
        this.itemName = itemName;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.notes);
        ButterKnife.bind(this);

        ivClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dismiss();
            }
        });

        if (message != null){
            tvNotes.setText(message);
        }

        if (itemName != null){
            tvItemName.setText(itemName);
        }
    }
}
