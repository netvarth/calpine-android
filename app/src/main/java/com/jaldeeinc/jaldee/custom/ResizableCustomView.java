package com.jaldeeinc.jaldee.custom;

/**
 * Created by sharmila on 26/9/18.
 */
import android.content.Context;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.TextView;

import com.jaldeeinc.jaldee.R;


public class ResizableCustomView {
    public static void doResizeTextView(final Context mContext, final TextView tv, final int maxLine, final String expandText, final boolean viewMore) {

        if (tv.getTag() == null) {
            tv.setTag(tv.getText());
        }
        ViewTreeObserver vto = tv.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {

                ViewTreeObserver obs = tv.getViewTreeObserver();
                obs.removeGlobalOnLayoutListener(this);
                if (maxLine == 0) {
                    int lineEndIndex = tv.getLayout().getLineEnd(0);
                    String text = tv.getText().subSequence(0, lineEndIndex - expandText.length() + 1) + " " + expandText;
                    tv.setText(text);
                    tv.setMovementMethod(LinkMovementMethod.getInstance());
                    tv.setText(
                            addClickablePartTextViewResizable(mContext,Html.fromHtml(tv.getText().toString()), tv, maxLine, expandText,
                                    viewMore), TextView.BufferType.SPANNABLE);
                } else if (maxLine > 0 && tv.getLineCount() >= maxLine) {
                    int lineEndIndex = tv.getLayout().getLineEnd(maxLine - 1);
                    String text = tv.getText().subSequence(0, lineEndIndex - expandText.length() + 1) + " " + expandText;
                    tv.setText(text);
                    tv.setMovementMethod(LinkMovementMethod.getInstance());
                    tv.setText(
                            addClickablePartTextViewResizable(mContext,Html.fromHtml(tv.getText().toString()), tv, maxLine, expandText,
                                    viewMore), TextView.BufferType.SPANNABLE);
                } else {
                    int lineEndIndex = tv.getLayout().getLineEnd(tv.getLayout().getLineCount() - 1);
                    String text = tv.getText().subSequence(0, lineEndIndex) + " " + expandText;
                    tv.setText(text);
                    tv.setMovementMethod(LinkMovementMethod.getInstance());
                    tv.setText(
                            addClickablePartTextViewResizable(mContext,Html.fromHtml(tv.getText().toString()), tv, lineEndIndex, expandText,
                                    viewMore), TextView.BufferType.SPANNABLE);
                }
            }
        });

    }

    private static SpannableStringBuilder addClickablePartTextViewResizable(final Context context, final Spanned strSpanned, final TextView tv,
                                                                            final int maxLine, final String spanableText, final boolean viewMore) {
        String str = strSpanned.toString();
        final SpannableStringBuilder ssb = new SpannableStringBuilder(strSpanned);


        if (str.contains(spanableText)) {
            ssb.setSpan(new ClickableSpan() {

                @Override
                public void onClick(View widget) {

                    if (viewMore) {
                        tv.setLayoutParams(tv.getLayoutParams());


                        tv.setText(tv.getTag().toString(), TextView.BufferType.SPANNABLE);

                        tv.invalidate();
                        doResizeTextView(context,tv, -1, "less", false);
                    } else {
                        tv.setLayoutParams(tv.getLayoutParams());
                        tv.setText(tv.getTag().toString(), TextView.BufferType.SPANNABLE);

                        tv.invalidate();
                        doResizeTextView(context,tv, 3, "View more", true);
                    }

                }
            }, str.indexOf(spanableText), str.indexOf(spanableText) + spanableText.length(), 0);
            ssb.setSpan(new ForegroundColorSpan(context.getResources().getColor(R.color.location_theme)), str.indexOf(spanableText), str.indexOf(spanableText) + spanableText.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        return ssb;

    }
}