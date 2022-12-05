package com.jaldeeinc.jaldee.custom;
import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;


public class CustomTextViewMedium extends androidx.appcompat.widget.AppCompatTextView
{


    public CustomTextViewMedium(Context context, AttributeSet attrs) {
        super(context, attrs);
        // TODO Auto-generated constructor stub

        Typeface font = Typeface.createFromAsset(context.getAssets(),  "fonts/JosefinSans-Regular.ttf");
        this.setTypeface(font);
    }

    public CustomTextViewMedium(Context context) {
        super(context);
        // TODO Auto-generated constructor stub

        Typeface font = Typeface.createFromAsset(context.getAssets(),  "fonts/JosefinSans-Regular.ttf");

        this.setTypeface(font);
    }
}

