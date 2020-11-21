package com.jaldeeinc.jaldee.custom;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;

public class CustomItalicTextViewNormal extends androidx.appcompat.widget.AppCompatTextView
{


    public CustomItalicTextViewNormal(Context context, AttributeSet attrs) {
        super(context, attrs);
        // TODO Auto-generated constructor stub

        Typeface font = Typeface.createFromAsset(context.getAssets(),  "fonts/JosefinSans-LightItalic.ttf");
        this.setTypeface(font);
    }

    public CustomItalicTextViewNormal(Context context) {
        super(context);
        // TODO Auto-generated constructor stub

        Typeface font = Typeface.createFromAsset(context.getAssets(),  "fonts/JosefinSans-LightItalic.ttf");

        this.setTypeface(font);
    }
}
