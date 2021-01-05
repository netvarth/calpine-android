package com.jaldeeinc.jaldee.custom;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Html;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.view.View;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.jaldeeinc.jaldee.Interface.ICartInterface;
import com.jaldeeinc.jaldee.Interface.IDialogInterface;
import com.jaldeeinc.jaldee.R;
import com.jaldeeinc.jaldee.adapter.SelectedItemsAdapter;
import com.jaldeeinc.jaldee.database.DatabaseHandler;
import com.jaldeeinc.jaldee.model.CartItemModel;
import com.jaldeeinc.jaldee.response.CatalogItem;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SuccessDialog extends Dialog {


    @BindView(R.id.tv_number)
    CustomTextViewItalicSemiBold tvOrderNo;

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
