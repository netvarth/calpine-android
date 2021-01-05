package com.jaldeeinc.jaldee.Fragment;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.jaldeeinc.jaldee.Interface.ISelectedBooking;
import com.jaldeeinc.jaldee.Interface.ISelectedOrder;
import com.jaldeeinc.jaldee.R;
import com.jaldeeinc.jaldee.activities.Home;
import com.jaldeeinc.jaldee.activities.OrderDetailActivity;
import com.jaldeeinc.jaldee.adapter.TodayOrdersAdapter;
import com.jaldeeinc.jaldee.common.Config;
import com.jaldeeinc.jaldee.connection.ApiClient;
import com.jaldeeinc.jaldee.connection.ApiInterface;
import com.jaldeeinc.jaldee.custom.CustomTextViewItalicSemiBold;
import com.jaldeeinc.jaldee.model.Bookings;
import com.jaldeeinc.jaldee.response.ActiveOrders;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MyOrders extends RootFragment implements ISelectedOrder {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private String mParam1;
    private String mParam2;
    private Context mContext;
    private Activity mActivity;
    private CustomTextViewItalicSemiBold tvToday, tvUpcoming;
    private LinearLayout llNoBookingsForToday, llNoBookingsForFuture, llNoBookings, llBookings;
    private RecyclerView rvTodays, rvUpcomings;
    private TodayOrdersAdapter todayOrdersAdapter;
    private LinearLayoutManager linearLayoutManager;
    private ISelectedOrder iSelectedOrder;
    ArrayList<ActiveOrders> ordersList = new ArrayList<>();
    ArrayList<ActiveOrders> ordersListFuture = new ArrayList<>();
    Animation slideUp, slideRight;
    boolean hideMoreInfo = false;


    public MyOrders() {
        // Required empty public constructor
    }


    public static MyOrders newInstance(String param1, String param2) {
        MyOrders fragment = new MyOrders();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public void onResume() {

        try {
            if (Config.isOnline(mContext)) {
                apiGetAllOrders();

            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        super.onResume();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_my_orders, container, false);
        mContext = getActivity();
        iSelectedOrder = (ISelectedOrder) this;
        Home.doubleBackToExitPressedOnce = false;
        initializations(view);

        return view;
    }


    private void initializations(View view) {

        rvTodays = view.findViewById(R.id.rv_todays);
        rvUpcomings = view.findViewById(R.id.rv_upcoming);
        llBookings = view.findViewById(R.id.ll_bookings);
        llNoBookings = view.findViewById(R.id.ll_noBookings);
        llNoBookingsForFuture = view.findViewById(R.id.ll_noFutureBookings);
        llNoBookingsForToday = view.findViewById(R.id.ll_noTodayBookings);
        tvToday = view.findViewById(R.id.tv_today);
        tvUpcoming = view.findViewById(R.id.tv_upcoming);
        slideUp = AnimationUtils.loadAnimation(mContext, R.anim.slide_up_in);
        slideRight = AnimationUtils.loadAnimation(mContext, R.anim.slide_up_out);

    }


    private void apiGetAllOrders() {

        ApiInterface apiService =
                ApiClient.getClient(mContext).create(ApiInterface.class);

        final Dialog mDialog = Config.getProgressDialog(mContext, mContext.getResources().getString(R.string.dialog_log_in));
        mDialog.show();
        Date currentTime = new Date();
        final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        Map<String, String> orderFilter = new HashMap<String, String>();
        orderFilter.put("orderDate-eq", sdf.format(currentTime));
        Call<ArrayList<ActiveOrders>> call = apiService.getOrders(orderFilter);

        call.enqueue(new Callback<ArrayList<ActiveOrders>>() {
            @Override
            public void onResponse(Call<ArrayList<ActiveOrders>> call, Response<ArrayList<ActiveOrders>> response) {
                try {
                    if (mDialog.isShowing())
                        Config.closeDialog(mActivity.getParent(), mDialog);
                    if (response.code() == 200) {
                        ordersList.clear();
                        ordersList = response.body();
                        apiGetAllOrdersFuture();

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
                    Config.closeDialog(mActivity.getParent(), mDialog);

            }
        });

    }


    private void apiGetAllOrdersFuture() {

        ApiInterface apiService = ApiClient.getClient(mContext).create(ApiInterface.class);
        final Dialog mDialog = Config.getProgressDialog(mContext, mContext.getResources().getString(R.string.dialog_log_in));
        mDialog.show();
        Date currentTime = new Date();
        final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        Map<String, String> orderFilter = new HashMap<String, String>();
        orderFilter.put("orderDate-eq", sdf.format(currentTime));
        Call<ArrayList<ActiveOrders>> call = apiService.getOrdersFuture();

        call.enqueue(new Callback<ArrayList<ActiveOrders>>() {
            @Override
            public void onResponse(Call<ArrayList<ActiveOrders>> call, Response<ArrayList<ActiveOrders>> response) {
                try {
                    if (mDialog.isShowing())
                        Config.closeDialog(mActivity.getParent(), mDialog);
                    if (response.code() == 200) {
                        ordersListFuture.clear();
                        ordersListFuture = response.body();

                        ordersList.removeAll(ordersListFuture);
                        ordersList.addAll(ordersListFuture);
                        ArrayList<ActiveOrders> totalOrdersLists = new ArrayList<>(ordersList);

                        if (totalOrdersLists.size() > 0) {

                            llNoBookings.setVisibility(View.GONE);
                            llBookings.setVisibility(View.VISIBLE);
                            updateTodayUI(ordersList);
                            updateFutureUI(ordersListFuture);

                        } else {

                            llNoBookings.setVisibility(View.VISIBLE);
                            llBookings.setVisibility(View.GONE);
                        }

                    } else {

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
                    Config.closeDialog(mActivity.getParent(), mDialog);

            }
        });


    }

    private void updateTodayUI(ArrayList<ActiveOrders> ordersList) {

        if (ordersList != null && ordersList.size() > 0) {
            llNoBookings.setVisibility(View.GONE);
            tvToday.setVisibility(View.VISIBLE);
            rvTodays.setVisibility(View.VISIBLE);
            linearLayoutManager = new LinearLayoutManager(getContext());
            rvTodays.setLayoutManager(linearLayoutManager);
            todayOrdersAdapter = new TodayOrdersAdapter(ordersList, getContext(), false, iSelectedOrder, hideMoreInfo);
            rvTodays.setAdapter(todayOrdersAdapter);
        } else {
            tvToday.setVisibility(View.GONE);
            rvTodays.setVisibility(View.GONE);
        }

    }

    private void updateFutureUI(ArrayList<ActiveOrders> ordersListFuture) {

        if (ordersListFuture != null && ordersListFuture.size() > 0) {
            llNoBookings.setVisibility(View.GONE);
            tvUpcoming.setVisibility(View.VISIBLE);
            rvUpcomings.setVisibility(View.VISIBLE);
            linearLayoutManager = new LinearLayoutManager(getContext());
            rvUpcomings.setLayoutManager(linearLayoutManager);
            todayOrdersAdapter = new TodayOrdersAdapter(ordersListFuture, getContext(), false, iSelectedOrder, hideMoreInfo);
            rvUpcomings.setAdapter(todayOrdersAdapter);
        } else {
            tvUpcoming.setVisibility(View.GONE);
            rvUpcomings.setVisibility(View.GONE);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mActivity = getActivity();
    }


    @Override
    public void onOrderClick(ActiveOrders orders) {

        Intent intent = new Intent(getActivity(), OrderDetailActivity.class);
        intent.putExtra("orderInfo", orders);
        startActivity(intent);
    }
}
