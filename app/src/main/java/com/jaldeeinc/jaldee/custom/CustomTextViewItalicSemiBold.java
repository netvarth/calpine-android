package com.jaldeeinc.jaldee.custom;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;


public class CustomTextViewItalicSemiBold extends androidx.appcompat.widget.AppCompatTextView {


    public CustomTextViewItalicSemiBold(Context context, AttributeSet attrs) {
        super(context, attrs);
        // TODO Auto-generated constructor stub

        Typeface font = Typeface.createFromAsset(context.getAssets(), "fonts/JosefinSans-MediumItalic.ttf");
        this.setTypeface(font);
    }

    public CustomTextViewItalicSemiBold(Context context) {
        super(context);
        // TODO Auto-generated constructor stub

        Typeface font = Typeface.createFromAsset(context.getAssets(), "fonts/JosefinSans-MediumItalic.ttf");

        this.setTypeface(font);
    }
}

