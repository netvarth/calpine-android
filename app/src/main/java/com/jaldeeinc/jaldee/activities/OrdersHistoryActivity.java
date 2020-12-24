package com.jaldeeinc.jaldee.activities;

import androidx.appcompat.app.AppCompatActivity;
import android.app.Dialog;
import android.os.Bundle;
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

public class OrdersHistoryActivity extends AppCompatActivity implements ISelectedBooking {

    RecyclerView rvHistory;
    CardView cvBack;
    LinearLayout llNoBookings;
    ArrayList<ActiveOrders> ordersList = new ArrayList<>();
    LinearLayoutManager linearLayoutManager;
    private ISelectedBooking iSelectedBooking;
    private TodayOrdersAdapter todayOrdersAdapter;
    boolean hideMoreInfo = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_orders_history);
        iSelectedBooking = this;
        initializations();

        linearLayoutManager = new LinearLayoutManager(getContext());
        rvHistory.setLayoutManager(linearLayoutManager);
        todayOrdersAdapter = new TodayOrdersAdapter(ordersList, OrdersHistoryActivity.this, true, iSelectedBooking, hideMoreInfo);
        rvHistory.setAdapter(todayOrdersAdapter);

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

                        if(ordersList.size()>0) {
                            llNoBookings.setVisibility(View.GONE);
                            linearLayoutManager = new LinearLayoutManager(OrdersHistoryActivity.this);
                            rvHistory.setLayoutManager(linearLayoutManager);
                            todayOrdersAdapter = new TodayOrdersAdapter(ordersList, OrdersHistoryActivity.this, false, iSelectedBooking, hideMoreInfo);
                            rvHistory.setAdapter(todayOrdersAdapter);
                        }


                        if(ordersList.size()==0 ){
                            llNoBookings.setVisibility(View.VISIBLE);

                        }
                        else{
                            llNoBookings.setVisibility(View.GONE);

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
    public void sendBookingInfo(Bookings bookings) {


    }

    @Override
    public void sendSelectedBookingActions(Bookings bookings) {

    }
}