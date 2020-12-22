package com.jaldeeinc.jaldee.custom;
import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;


public class CustomEditTextRegular extends androidx.appcompat.widget.AppCompatEditText
{


    public CustomEditTextRegular(Context context, AttributeSet attrs) {
        super(context, attrs);
        // TODO Auto-generated constructor stub

        Typeface font = Typeface.createFromAsset(context.getAssets(),  "fonts/JosefinSans-Regular.ttf");
        this.setTypeface(font);
    }

    public CustomEditTextRegular(Context context) {
        super(context);
        // TODO Auto-generated constructor stub

        Typeface font = Typeface.createFromAsset(context.getAssets(),  "fonts/JosefinSans-Regular.ttf");

        this.setTypeface(font);
    }
}

