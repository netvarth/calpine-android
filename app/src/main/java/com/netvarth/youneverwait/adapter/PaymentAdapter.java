package com.netvarth.youneverwait.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.TextView;

import com.netvarth.youneverwait.R;
import com.netvarth.youneverwait.response.PaymentModel;

import java.util.List;

/**
 * Created by sharmila on 8/8/18.
 */

public class PaymentAdapter extends RecyclerView.Adapter<PaymentAdapter.PaymentAdapterViewHolder> {
    private List<PaymentModel> horizontalPaymentList;
    Context context;
    private RadioButton lastCheckedRB = null;

    public PaymentAdapter(List<PaymentModel> horizontalPaymentList, Context context) {
        this.horizontalPaymentList = horizontalPaymentList;
        this.context = context;
    }

    @Override
    public PaymentAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //inflate the layout file
        View queueView = LayoutInflater.from(parent.getContext()).inflate(R.layout.horizontal_paymentlist, parent, false);
        PaymentAdapterViewHolder gvh = new PaymentAdapterViewHolder(queueView);
        return gvh;
    }

    @Override
    public void onBindViewHolder(final PaymentAdapterViewHolder holder, int position) {
        holder.radio_payment.setText(horizontalPaymentList.get(position).getDisplayname());
        holder.radio_payment.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (lastCheckedRB != null) {
                    lastCheckedRB.setChecked(false);
                }
                //store the clicked radiobutton
                lastCheckedRB = holder.radio_payment;
            }
        });

    }


    @Override
    public int getItemCount() {
        return horizontalPaymentList.size();
    }

    public class PaymentAdapterViewHolder extends RecyclerView.ViewHolder {
        RadioButton radio_payment;


        public PaymentAdapterViewHolder(View view) {
            super(view);
            radio_payment = view.findViewById(R.id.mRadioPayment);


        }
    }
}
