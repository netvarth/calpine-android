package com.jaldeeinc.jaldee.activities;

import androidx.appcompat.app.AppCompatActivity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;

import com.jaldeeinc.jaldee.Interface.ISelectedOrder;
import com.jaldeeinc.jaldee.R;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;
import com.jaldeeinc.jaldee.Interface.ISelectedBooking;
import com.jaldeeinc.jaldee.adapter.TodayOrdersAdapter;
import com.jaldeeinc.jaldee.common.Config;
import com.jaldeeinc.jaldee.connection.ApiClient;
import com.jaldeeinc.jaldee.connection.ApiInterface;
import com.jaldeeinc.jaldee.model.Bookings;
import com.jaldeeinc.jaldee.response.ActiveOrders;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import static com.jaldeeinc.jaldee.common.MyApplication.getContext;

public class OrdersHistoryActivity extends AppCompatActivity implements ISelectedOrder {

    RecyclerView rvHistory;
    CardView cvBack;
    LinearLayout llNoBookings;
    ArrayList<ActiveOrders> ordersList = new ArrayList<>();
    LinearLayoutManager linearLayoutManager;
    private ISelectedOrder iSelectedOrder;
    private TodayOrdersAdapter todayOrdersAdapter;
    boolean hideMoreInfo = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_orders_history);
        iSelectedOrder = (ISelectedOrder) this;
        initializations();
        
        cvBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                finish();
            }
        });

        try {
            if (Config.isOnline(OrdersHistoryActivity.this)) {
                apiGetAllOrdersHistory();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    private void initializations() {

        cvBack = findViewById(R.id.cv_back);
        rvHistory = findViewById(R.id.rv_history);
        llNoBookings = findViewById(R.id.ll_noBookings);
    }

    private void apiGetAllOrdersHistory() {

        ApiInterface apiService =
                ApiClient.getClient(this).create(ApiInterface.class);

        final Dialog mDialog = Config.getProgressDialog(OrdersHistoryActivity.this, OrdersHistoryActivity.this.getResources().getString(R.string.dialog_log_in));
        mDialog.show();
        Date currentTime = new Date();
        final SimpleDateFormat sdf = new SimpleDateFormat(
                "yyyy-MM-dd");
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        Map<String, String> orderFilter = new HashMap<String, String>();
        orderFilter.put("orderDate-eq", sdf.format(currentTime));
        Call<ArrayList<ActiveOrders>> call = apiService.getOrdersHistory();

        call.enqueue(new Callback<ArrayList<ActiveOrders>>() {
            @Override
            public void onResponse(Call<ArrayList<ActiveOrders>> call, Response<ArrayList<ActiveOrders>> response) {
                try {
                    if (mDialog.isShowing())
                        Config.closeDialog(OrdersHistoryActivity.this.getParent(), mDialog);
                    if (response.code() == 200) {
                        ordersList = response.body();

                        if(ordersList != null && ordersList.size()>0) {
                            llNoBookings.setVisibility(View.GONE);
                            linearLayoutManager = new LinearLayoutManager(OrdersHistoryActivity.this);
                            rvHistory.setLayoutManager(linearLayoutManager);
                            todayOrdersAdapter = new TodayOrdersAdapter(ordersList, OrdersHistoryActivity.this, false, iSelectedOrder, hideMoreInfo);
                            rvHistory.setAdapter(todayOrdersAdapter);
                        } else {

                            llNoBookings.setVisibility(View.VISIBLE);
                        }

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ArrayList<ActiveOrders>> call, Throwable t) {
                // Log error here since request failed
                Config.logV("Fail---------------" + t.toString());
                if (mDialog.isShowing())
                    Config.closeDialog(OrdersHistoryActivity.this.getParent(), mDialog);

            }
        });

    }


    @Override
    public void onOrderClick(ActiveOrders orders) {

        Intent intent = new Intent(OrdersHistoryActivity.this,OrderDetailActivity.class);
        intent.putExtra("orderInfo",orders);
        startActivity(intent);

    }
}