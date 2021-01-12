package com.jaldeeinc.jaldee.custom;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.jaldeeinc.jaldee.Interface.IActions;
import com.jaldeeinc.jaldee.R;
import com.jaldeeinc.jaldee.activities.BillActivity;
import com.jaldeeinc.jaldee.activities.Constants;
import com.jaldeeinc.jaldee.activities.OrderDetailActivity;
import com.jaldeeinc.jaldee.common.Config;
import com.jaldeeinc.jaldee.connection.ApiClient;
import com.jaldeeinc.jaldee.connection.ApiInterface;
import com.jaldeeinc.jaldee.model.Bookings;
import com.jaldeeinc.jaldee.response.ActiveOrders;
import com.pranavpandey.android.dynamic.toasts.DynamicToast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.jaldeeinc.jaldee.connection.ApiClient.context;

public class OrderActionsDialog extends Dialog {

    private Context mContext;
    private boolean isActive = false;
    private ActiveOrders orderInfo = new ActiveOrders();
    private IActions iActions;

    @BindView(R.id.ll_cancel)
    LinearLayout llCancel;

    @BindView(R.id.ll_bill)
    LinearLayout llBill;

    @BindView(R.id.ll_details)
    LinearLayout llDetails;

    @BindView(R.id.tv_bill)
    CustomTextViewMedium tvBill;


    public OrderActionsDialog(@NonNull Context context, boolean isActive, ActiveOrders activeOrder,IActions iActions) {
        super(context);
        this.mContext = context;
        this.isActive = isActive;
        this.orderInfo = activeOrder;
        this.iActions = iActions;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.order_actions);
        ButterKnife.bind(this);

        if (isActive) {

            llCancel.setVisibility(View.VISIBLE);
            llCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    showAlertDialog();
                }
            });

        } else {
            llCancel.setVisibility(View.GONE);
        }

        // To show Bill details
        if (orderInfo.getBill() != null) {
            if (orderInfo.getBill().getBillPaymentStatus().equalsIgnoreCase("FullyPaid") || orderInfo.getBill().getBillPaymentStatus().equalsIgnoreCase("Refund")) {
                tvBill.setText("Receipt");
            } else {
                tvBill.setText("Pay Bill");
            }

            if (orderInfo.getBill().getBillViewStatus() != null && !orderInfo.getOrderStatus().equalsIgnoreCase("cancelled")) {
                if (orderInfo.getBill().getBillViewStatus().equalsIgnoreCase("Show")) {
                    llBill.setVisibility(View.VISIBLE);
                } else {
                    hideView(llBill);
                }

            } else {
                if (!orderInfo.getBill().getBillPaymentStatus().equalsIgnoreCase("NotPaid")) {
                    llBill.setVisibility(View.VISIBLE);
                } else {
                    hideView(llBill);
                }
            }

            llBill.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    try {
                        if (orderInfo != null) {
                            Intent iBill = new Intent(mContext, BillActivity.class);
                            iBill.putExtra("ynwUUID", orderInfo.getUid());
                            iBill.putExtra("provider", orderInfo.getProviderAccount().getBusinessName());
                            iBill.putExtra("accountID", String.valueOf(orderInfo.getProviderAccount().getId()));
                            iBill.putExtra("payStatus", orderInfo.getBill().getBillPaymentStatus());
                            iBill.putExtra("purpose", Constants.PURPOSE_BILLPAYMENT);
                            iBill.putExtra("consumer", orderInfo.getOrderFor().getFirstName() + " " + orderInfo.getOrderFor().getLastName());
                            iBill.putExtra("uniqueId", String.valueOf(orderInfo.getProviderAccount().getUniqueId()));
                            mContext.startActivity(iBill);
                            dismiss();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        } else {

            hideView(llBill);
        }

        llDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(mContext, OrderDetailActivity.class);
                intent.putExtra("orderInfo", orderInfo);
                mContext.startActivity(intent);
                dismiss();
            }
        });


    }

    private void showAlertDialog() {

        final BottomSheetDialog dialog = new BottomSheetDialog(mContext);
        dialog.setContentView(R.layout.cancelcheckin);
        dialog.show();
        Button btSend = (Button) dialog.findViewById(R.id.btn_send);
        Button btCancel = (Button) dialog.findViewById(R.id.btn_cancel);
        Typeface tyface1 = Typeface.createFromAsset(mContext.getAssets(),
                "fonts/JosefinSans-SemiBold.ttf");
        btSend.setTypeface(tyface1);
        btCancel.setTypeface(tyface1);
        final EditText edt_message = (EditText) dialog.findViewById(R.id.edt_message);
        TextView txtsendmsg = (TextView) dialog.findViewById(R.id.txtsendmsg);
        txtsendmsg.setText("Do you want to cancel this Order?");
        btSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //api call
                if (orderInfo != null && orderInfo.getUid() != null && orderInfo.getProviderAccount() != null) {
                    cancelOrder(orderInfo, dialog);
                }
            }
        });
        btCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }

    private void cancelOrder(ActiveOrders orderInfo, BottomSheetDialog dialog) {

        ApiInterface apiService = ApiClient.getClient(mContext).create(ApiInterface.class);
        final Dialog mDialog = Config.getProgressDialog(mContext, mContext.getResources().getString(R.string.dialog_log_in));
        mDialog.show();
        Call<ResponseBody> call = apiService.cancelOrder(orderInfo.getUid(), orderInfo.getProviderAccount().getId());

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    if (mDialog.isShowing())
                        Config.closeDialog(getOwnerActivity(), mDialog);
                    if (response.code() == 200) {

                        DynamicToast.make(mContext, "Order cancelled successfully",
                                ContextCompat.getColor(mContext, R.color.white), ContextCompat.getColor(mContext, R.color.appoint_theme), Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                        dismiss();
                        iActions.onCancel();

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                // Log error here since request failed
                Config.logV("Fail---------------" + t.toString());
                if (mDialog.isShowing())
                    Config.closeDialog(getOwnerActivity(), mDialog);

            }
        });

    }

    private void hideView(View view) {
        GridLayout gridLayout = (GridLayout) view.getParent();
        if (gridLayout != null) {
            for (int i = 0; i < gridLayout.getChildCount(); i++) {
                if (view == gridLayout.getChildAt(i)) {
                    gridLayout.removeViewAt(i);
                    break;
                }
            }
        }
    }

}
