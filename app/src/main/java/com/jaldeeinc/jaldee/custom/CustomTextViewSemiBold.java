package com.jaldeeinc.jaldee.custom;
import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;


public class CustomTextViewSemiBold extends androidx.appcompat.widget.AppCompatTextView
{


    public CustomTextViewSemiBold(Context context, AttributeSet attrs) {
        super(context, attrs);
        // TODO Auto-generated constructor stub

        Typeface font = Typeface.createFromAsset(context.getAssets(),  "fonts/JosefinSans-SemiBold.ttf");
        this.setTypeface(font);
    }

    public CustomTextViewSemiBold(Context context) {
        super(context);
        // TODO Auto-generated constructor stub

        Typeface font = Typeface.createFromAsset(context.getAssets(),  "fonts/JosefinSans-SemiBold.ttf");

        this.setTypeface(font);
    }
}

