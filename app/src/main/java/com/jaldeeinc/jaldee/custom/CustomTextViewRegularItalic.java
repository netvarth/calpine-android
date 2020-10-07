package com.jaldeeinc.jaldee.custom;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;

public class CustomTextViewRegularItalic extends androidx.appcompat.widget.AppCompatTextView {


    public CustomTextViewRegularItalic(Context context, AttributeSet attrs) {
        super(context, attrs);
        // TODO Auto-generated constructor stub

        Typeface font = Typeface.createFromAsset(context.getAssets(), "fonts/JosefinSans-ExtraLightItalic.ttf");
        this.setTypeface(font);
    }

    public CustomTextViewRegularItalic(Context context) {
        super(context);
        // TODO Auto-generated constructor stub

        Typeface font = Typeface.createFromAsset(context.getAssets(), "fonts/JosefinSans-ExtraLightItalic.ttf");

        this.setTypeface(font);
    }
}

