package com.jaldeeinc.jaldee.custom;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;

public class CustomAutoSemiBold extends AutofitTextView {


    public CustomAutoSemiBold(Context context, AttributeSet attrs) {
        super(context, attrs);
        // TODO Auto-generated constructor stub

        Typeface font = Typeface.createFromAsset(context.getAssets(), "fonts/JosefinSans-Bold.ttf");
        this.setTypeface(font);
    }

    public CustomAutoSemiBold(Context context) {
        super(context);
        // TODO Auto-generated constructor stub

        Typeface font = Typeface.createFromAsset(context.getAssets(), "fonts/JosefinSans-Bold.ttf");

        this.setTypeface(font);
    }
}