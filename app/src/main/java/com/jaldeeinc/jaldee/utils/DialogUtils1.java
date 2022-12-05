package com.jaldeeinc.jaldee.utils;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.Window;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jaldeeinc.jaldee.R;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import kotlin.jvm.functions.Function0;

public class DialogUtils1 {
    public static final void showUIDialog(@NotNull Context context, @NotNull String title, @Nullable String message, @NotNull final Function0 success) {
        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.custom_dialog);
        TextView tvTitle = (TextView) dialog.findViewById(R.id.mTitle);
        TextView tvMessage = (TextView) dialog.findViewById(R.id.mMessage);
        tvTitle.setText((CharSequence) title);
        tvMessage.setText(message != null ? (CharSequence) message : (CharSequence) "Unable to get message from server.");
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        dialog.show();
        TextView acceptButtonText = (TextView) dialog.findViewById(R.id.buttonOk);
        acceptButtonText.setText((CharSequence) "ok");
        RelativeLayout acceptButton = (RelativeLayout) dialog.findViewById(R.id.rl_positive);
        acceptButton.setOnClickListener((View.OnClickListener) (new View.OnClickListener() {
            public final void onClick(View it) {
                dialog.dismiss();
                success.invoke();
            }
        }));

    }
}
