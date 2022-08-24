package com.jaldeeinc.jaldee.custom;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;

import androidx.core.content.res.ResourcesCompat;

import com.jaldeeinc.jaldee.R;


public class CustomTextViewBoldItalic extends androidx.appcompat.widget.AppCompatTextView {


    public CustomTextViewBoldItalic(Context context, AttributeSet attrs) {
        super(context, attrs);
        // TODO Auto-generated constructor stub

        Typeface font = Typeface.createFromAsset(context.getAssets(),  "fonts/JosefinSans-MediumItalic.ttf");
        //Typeface font = ResourcesCompat.getFont(context, R.font.font_bolditalic);

        this.setTypeface(font);
    }

    public CustomTextViewBoldItalic(Context context) {
        super(context);
        // TODO Auto-generated constructor stub
        Typeface font = Typeface.createFromAsset(context.getAssets(),  "fonts/JosefinSans-MediumItalic.ttf");

        //Typeface font = Typeface.createFromAsset(context.getAssets(), "font/font_bolditalic");

        this.setTypeface(font);
    }
}

