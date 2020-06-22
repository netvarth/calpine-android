package com.jaldeeinc.jaldee.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.jaldeeinc.jaldee.R;
import com.jaldeeinc.jaldee.activities.CheckIn;
import com.jaldeeinc.jaldee.activities.PaymentDetail;
import com.jaldeeinc.jaldee.common.Config;
import com.jaldeeinc.jaldee.response.MyPayments;
import java.util.List;

public class MyPaymentAdapter  extends ArrayAdapter<MyPayments> {

    List<MyPayments> paymentsList;
    Context mContext;


    public MyPaymentAdapter(@NonNull Context context, int resource, List<MyPayments> paymentsList) {
        super(context, resource, paymentsList);

        this.mContext = context;
        this.paymentsList = paymentsList;

    }

    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {


        View listItem = convertView;
        if(listItem == null)
            listItem = LayoutInflater.from(this.getContext()).inflate(R.layout.paymentview,parent,false);


        MyPayments myPayments = paymentsList.get(position);

        TextView accountName = (TextView) listItem.findViewById(R.id.accountName);
        TextView amount = (TextView) listItem.findViewById(R.id.amount);
        TextView acceptPaymentBy = (TextView) listItem.findViewById(R.id.acceptPaymentBy);
        TextView paymentOn = (TextView) listItem.findViewById(R.id.paymentOn);
        TextView status = (TextView) listItem.findViewById(R.id.status);
        TextView txnType = (TextView) listItem.findViewById(R.id.txnType);
        CardView card = (CardView) listItem.findViewById(R.id.card);

        if(paymentsList.get(position).getAccountName()!=null){
            accountName.setText(paymentsList.get(position).getAccountName());
            accountName.setVisibility(View.VISIBLE);
        }else{
            accountName.setVisibility(View.INVISIBLE);
        }

        if(paymentsList.get(position).getAmount()!=null){
            amount.setText("₹ "+paymentsList.get(position).getAmount());
            amount.setVisibility(View.VISIBLE);
        }else{
            amount.setVisibility(View.INVISIBLE);
        }

        if(paymentsList.get(position).getAcceptPaymentBy()!=null){
            acceptPaymentBy.setText(paymentsList.get(position).getAcceptPaymentBy());
            acceptPaymentBy.setVisibility(View.VISIBLE);
        }else{
            acceptPaymentBy.setVisibility(View.INVISIBLE);
        }

        if(paymentsList.get(position).getPaymentOn()!=null){
            paymentOn.setText(paymentsList.get(position).getPaymentOn());
            paymentOn.setVisibility(View.VISIBLE);
        }else{
            paymentOn.setVisibility(View.INVISIBLE);
        }

        if(paymentsList.get(position).getStatus()!=null){
            status.setText(paymentsList.get(position).getStatus());
            status.setVisibility(View.VISIBLE);
        }else{
            status.setVisibility(View.INVISIBLE);
        }


        if(paymentsList.get(position).getTxnType()!=null){
            if(paymentsList.get(position).getTxnType().equalsIgnoreCase("Waitlist")){
                txnType.setText("C");
            }else if(paymentsList.get(position).getTxnType().equalsIgnoreCase("Appointment")){
                txnType.setText("A");
            }else if (paymentsList.get(position).getTxnType().equalsIgnoreCase("Donation")){
                txnType.setText("D");
            }
            txnType.setVisibility(View.VISIBLE);
        }else{
            txnType.setVisibility(View.INVISIBLE);
        }







        accountName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent iPaymentDetail = new Intent(v.getContext(), PaymentDetail.class);
                    iPaymentDetail.putExtra("myPaymentID",String.valueOf(paymentsList.get(position).getId()));
                mContext.startActivity(iPaymentDetail);
            }

        });

        card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent iPaymentDetail = new Intent(v.getContext(), PaymentDetail.class);
                iPaymentDetail.putExtra("myPaymentID",String.valueOf(paymentsList.get(position).getId()));
                mContext.startActivity(iPaymentDetail);
            }

        });

        txnType.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent iPaymentDetail = new Intent(v.getContext(), PaymentDetail.class);
                iPaymentDetail.putExtra("myPaymentID",String.valueOf(paymentsList.get(position).getId()));
                mContext.startActivity(iPaymentDetail);
            }

        });


        return listItem;
    }

}
