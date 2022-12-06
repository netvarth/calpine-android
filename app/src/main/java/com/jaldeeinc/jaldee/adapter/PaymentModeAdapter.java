package com.jaldeeinc.jaldee.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.jaldeeinc.jaldee.Interface.IPaymentGateway;
import com.jaldeeinc.jaldee.R;
import com.jaldeeinc.jaldee.response.PayMode;

import java.util.ArrayList;

public class PaymentModeAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<PayMode> payModes = new ArrayList<>();
    private IPaymentGateway iPaymentGateway;
    private String selectedMode = null;
    private int selectedPosition = -1;
    int row_index = -1;

    private FrameLayout cv_paymentMode;
    private LinearLayout ll_paymentMode;
    private LinearLayout ll_circle_shape;
    private ImageView iv_paymentMode;
    private TextView tv_payMode;

    public PaymentModeAdapter(Context context, ArrayList<PayMode> payModes, IPaymentGateway iPaymentGateway) {
        this.context = context;
        this.payModes = payModes;
        this.iPaymentGateway = iPaymentGateway;

    }

    @Override
    public int getCount() {
        return payModes.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View viewHolder, ViewGroup viewGroup) {

        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view;
        view = new View(context);
        view = inflater.inflate(R.layout.payment_mode, null);
        cv_paymentMode = view.findViewById(R.id.cv_paymentMode);
        ll_paymentMode = view.findViewById(R.id.ll_paymentMode);
        ll_circle_shape = view.findViewById(R.id.ll_circle_shape);
        iv_paymentMode = view.findViewById(R.id.iv_paymentMode);
        tv_payMode = view.findViewById(R.id.tv_payMode);

        if (payModes != null && payModes.size() > 0) {
            PayMode payMode = payModes.get(i);
            try {
                switch (payMode.getMode()) {
                    case "UPI":
                        tv_payMode.setText("UPI Payment");
                        Glide.with(context).load(R.drawable.icon_paymentmode_upi).placeholder(R.drawable.icon_noimage).into(iv_paymentMode);
                        break;
                    case "CC":
                        tv_payMode.setText("Credit Card");
                        Glide.with(context).load(R.drawable.icon_paymentmode_credit_card).placeholder(R.drawable.icon_noimage).into(iv_paymentMode);
                        break;
                    case "DC":
                        tv_payMode.setText("Debit Card");
                        Glide.with(context).load(R.drawable.icon_paymentmode_debit_card).placeholder(R.drawable.icon_noimage).into(iv_paymentMode);
                        break;
                    case "NB":
                        tv_payMode.setText("Net Banking");
                        //PicassoTrustAll.getInstance(context).load(R.drawable.icon_paymentmode_nb).placeholder(R.drawable.icon_noimage).error(R.drawable.icon_noimage).transform(new CircleTransform()).fit().into(myViewHolder.iv_paymentMode);

                        Glide.with(context).load(R.drawable.icon_paymentmode_netbank).placeholder(R.drawable.icon_noimage).into(iv_paymentMode);
                        break;
                    case "WALLET":
                        tv_payMode.setText("Wallet");
                        Glide.with(context).load(R.drawable.icon_paymentmode_wallet1).placeholder(R.drawable.icon_noimage).into(iv_paymentMode);

                        break;
                    case "PAYLATER":
                        tv_payMode.setText("Pay later");
                        Glide.with(context).load(R.drawable.icon_paymentmode_paylater1).placeholder(R.drawable.icon_noimage).into(iv_paymentMode);

                        break;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (i == selectedPosition) {
                ll_circle_shape.setBackgroundResource(R.drawable.circle_bordergrey_yellowfilled);
                ll_paymentMode.setBackgroundResource(R.drawable.rctngle_borderyellow_roundededge_whitefilled);

            } else {
                ll_circle_shape.setBackgroundResource(R.drawable.circle_bordergrey_greyfilled);
                ll_paymentMode.setBackgroundResource(R.drawable.rctngle_bordergrey_roundededge15_whitefilled);
            }
            setAnimation(cv_paymentMode, i);


            cv_paymentMode.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    int currentPosition = i;
                    if (selectedPosition != currentPosition) {
                        // Temporarily save the last selected position
                        int lastSelectedPosition = selectedPosition;
                        // Save the new selected position
                        selectedPosition = currentPosition;
                        // update the previous selected row
                        notifyDataSetChanged();
                        // select the clicked row
                        ll_circle_shape.setBackgroundResource(R.drawable.circle_bordergrey_yellowfilled);
                        ll_paymentMode.setBackgroundResource(R.drawable.rctngle_borderyellow_roundededge_whitefilled);

                        iPaymentGateway.selectedPaymentMode(payMode.getMode());
                    }
                }
            });
        }

        return view;
    }

    private void setAnimation(View viewToAnimate, int position) {
        // If the bound view wasn't previously displayed on screen, it's animated
        if (position > row_index) {
            Animation animation = AnimationUtils.loadAnimation(context, android.R.anim.slide_in_left);
            viewToAnimate.startAnimation(animation);
            row_index = position;
        }
    }
}