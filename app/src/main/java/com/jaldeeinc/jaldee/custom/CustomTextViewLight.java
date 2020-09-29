package com.jaldeeinc.jaldee.custom;
import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.EditText;
import android.widget.TextView;


public class CustomTextViewLight extends androidx.appcompat.widget.AppCompatTextView
{


    public CustomTextViewLight(Context context, AttributeSet attrs) {
        super(context, attrs);
        // TODO Auto-generated constructor stub

        Typeface font = Typeface.createFromAsset(context.getAssets(),  "fonts/JosefinSans-Light.ttf");
        this.setTypeface(font);
    }

    public CustomTextViewLight(Context context) {
        super(context);
        // TODO Auto-generated constructor stub

        Typeface font = Typeface.createFromAsset(context.getAssets(),  "fonts/JosefinSans-Light.ttf");

        this.setTypeface(font);
    }
}

