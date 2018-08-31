package com.netvarth.youneverwait.custom;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.design.widget.TextInputLayout;
import android.util.AttributeSet;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Created by sharmila on 10/8/18.
 */

public class CustomTextInputLayout extends TextInputLayout {

    private Object collapsingTextHelper;
    private Method setCollapsedTypefaceMethod;
    private Method setExpandedTypefaceMethod;

    public CustomTextInputLayout(Context context) {
        this(context, null);
    }

    public CustomTextInputLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CustomTextInputLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        init();
    }


    private void init() {
        try {
            Field cthField = TextInputLayout.class
                    .getDeclaredField("mCollapsingTextHelper");
            cthField.setAccessible(true);
            collapsingTextHelper = cthField.get(this);

            setCollapsedTypefaceMethod = collapsingTextHelper
                    .getClass().getDeclaredMethod("setCollapsedTypeface", Typeface.class);
            setCollapsedTypefaceMethod.setAccessible(true);

            setExpandedTypefaceMethod = collapsingTextHelper
                    .getClass().getDeclaredMethod("setExpandedTypeface", Typeface.class);
            setExpandedTypefaceMethod.setAccessible(true);
        }
        catch (Exception e) {
            collapsingTextHelper = null;
            setCollapsedTypefaceMethod = null;
            setExpandedTypefaceMethod = null;
            e.printStackTrace();
        }
    }

    public void setCollapsedTypeface(Typeface typeface) {
        if (collapsingTextHelper == null) {
            return;
        }

        try {
            setCollapsedTypefaceMethod.invoke(collapsingTextHelper, typeface);
        }
        catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    public void setExpandedTypeface(Typeface typeface) {
        if (collapsingTextHelper == null) {
            return;
        }

        try {
            setExpandedTypefaceMethod.invoke(collapsingTextHelper, typeface);
        }
        catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }
}