package com.jaldeeinc.jaldee.custom;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;

import com.jaldeeinc.jaldee.R;

public class CustomerNotes extends Dialog {

    Context mContext;
    ImageView ivClose;
    CustomTextViewSemiBold tvTitle;
    CustomTextViewMedium tvCustomerNotes;
    String title = "";
    String notes = "";


    public CustomerNotes(@NonNull Context context, String consumerNoteTitle, String consumerNote) {
        super(context);
        this.mContext = context;
        this.title = consumerNoteTitle;
        this.notes = consumerNote;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.customer_notes);

        ivClose = findViewById(R.id.iv_close);
        tvTitle = findViewById(R.id.tv_title);
        tvCustomerNotes = findViewById(R.id.tv_customerNotes);

        ivClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                dismiss();
            }
        });

        if (title != null) {
            tvTitle.setText(title);
        }else {
            tvTitle.setText("Customer Notes");
        }
        tvCustomerNotes.setText(notes);



    }
}
