package com.jaldeeinc.jaldee.custom;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;


public class CustomTextViewThin extends androidx.appcompat.widget.AppCompatTextView {


    public CustomTextViewThin(Context context, AttributeSet attrs) {
        super(context, attrs);
        // TODO Auto-generated constructor stub

        Typeface font = Typeface.createFromAsset(context.getAssets(), "fonts/JosefinSans-Thin.ttf");
        this.setTypeface(font);
    }

    public CustomTextViewThin(Context context) {
        super(context);
        // TODO Auto-generated constructor stub

        Typeface font = Typeface.createFromAsset(context.getAssets(), "fonts/JosefinSans-Thin.ttf");

        this.setTypeface(font);
    }
}



