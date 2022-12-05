package com.jaldeeinc.jaldee.custom;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;

public class CustomTextViewNormalItalic extends androidx.appcompat.widget.AppCompatTextView
{


    public CustomTextViewNormalItalic(Context context, AttributeSet attrs) {
        super(context, attrs);
        // TODO Auto-generated constructor stub

        Typeface font = Typeface.createFromAsset(context.getAssets(),  "fonts/JosefinSans-LightItalic.ttf");

        this.setTypeface(font);
    }

    public CustomTextViewNormalItalic(Context context) {
        super(context);
        // TODO Auto-generated constructor stub

        Typeface font = Typeface.createFromAsset(context.getAssets(),  "fonts/JosefinSans-LightItalic.ttf");

        this.setTypeface(font);
    }
}
