package com.jaldeeinc.jaldee.custom;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.Html;

import com.jaldeeinc.jaldee.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SuccessDialog extends Dialog {


    @BindView(R.id.tv_number)
    CustomTextViewBoldItalic tvOrderNo;

    private Context mContext;
    private String orderNo;


    public SuccessDialog(Context mContext, String orderNumber) {
        super(mContext);
        this.mContext = mContext;
        this.orderNo = orderNumber;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.success_dialog);
        ButterKnife.bind(this);

        if (orderNo != null) {

            String displayText = "Your order  " + "<b> #" + orderNo + "</b>" + "  has been placed successfully";

            tvOrderNo.setText(Html.fromHtml(displayText));

        }

    }


}
