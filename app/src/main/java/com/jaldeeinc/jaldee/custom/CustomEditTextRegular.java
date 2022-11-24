package com.jaldeeinc.jaldee.custom;
import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;

import androidx.core.content.res.ResourcesCompat;

import com.jaldeeinc.jaldee.R;


public class CustomEditTextRegular extends androidx.appcompat.widget.AppCompatEditText
{


    public CustomEditTextRegular(Context context, AttributeSet attrs) {
        super(context, attrs);
        // TODO Auto-generated constructor stub
        Typeface font = ResourcesCompat.getFont(context, R.font.font_regular);

        //Typeface font = Typeface.createFromAsset(context.getAssets(),  context.getResources().getFont(font.font_regular));
        this.setTypeface(font);
    }

    public CustomEditTextRegular(Context context) {
        super(context);
        // TODO Auto-generated constructor stub
        Typeface font = ResourcesCompat.getFont(context, R.font.font_regular);

        //Typeface font = Typeface.createFromAsset(context.getAssets(), String.valueOf(R.font.font_regular));

        this.setTypeface(font);
    }
}

