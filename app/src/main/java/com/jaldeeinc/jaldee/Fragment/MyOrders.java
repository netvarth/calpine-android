package com.jaldeeinc.jaldee.Fragment;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.jaldeeinc.jaldee.Interface.IActions;
import com.jaldeeinc.jaldee.Interface.ISelectedBooking;
import com.jaldeeinc.jaldee.Interface.ISelectedOrder;
import com.jaldeeinc.jaldee.R;
import com.jaldeeinc.jaldee.activities.Constants;
import com.jaldeeinc.jaldee.activities.Home;
import com.jaldeeinc.jaldee.activities.OrderDetailActivity;
import com.jaldeeinc.jaldee.activities.ReleasedQNRActivity;
import com.jaldeeinc.jaldee.adapter.TodayOrdersAdapter;
import com.jaldeeinc.jaldee.common.Config;
import com.jaldeeinc.jaldee.connection.ApiClient;
import com.jaldeeinc.jaldee.connection.ApiInterface;
import com.jaldeeinc.jaldee.custom.CustomTextViewItalicSemiBold;
import com.jaldeeinc.jaldee.custom.OrderActionsDialog;
import com.jaldeeinc.jaldee.model.RlsdQnr;
import com.jaldeeinc.jaldee.response.ActiveOrders;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.stream.Collectors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MyOrders extends RootFragment implements ISelectedOrder, IActions {
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
    private IActions iActions;
    private OrderActionsDialog orderActionsDialog;
    List<RlsdQnr> fReleasedQNR, fReleasedQNR1;

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
        iActions = this;
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
                        if (ordersList == null) {
                            ordersList = new ArrayList<>();
                        }
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
                        ordersListFuture = new ArrayList<>();
                        ordersListFuture = response.body();
                        ArrayList<ActiveOrders> oList = new ArrayList<>(ordersList);
                        if (ordersListFuture != null) {

                            oList.removeAll(ordersListFuture);
                            oList.addAll(ordersListFuture);
                        }
                        ArrayList<ActiveOrders> totalOrdersLists = new ArrayList<>(oList);

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
    public void onOrderClick(ActiveOrders orderInfo) {
        boolean isActive = false;
        if (orderInfo != null && orderInfo.getOrderStatus() != null) {
            if (!orderInfo.getOrderStatus().equalsIgnoreCase("Cancelled") && !orderInfo.getOrderStatus().equalsIgnoreCase("Completed")) {
                isActive = true;
            }
        }
        if (orderInfo != null) {
            if (orderInfo.getReleasedQnr() != null) {
                fReleasedQNR = orderInfo.getReleasedQnr().stream()
                        .filter(p -> p.getStatus().equalsIgnoreCase("released")).collect(Collectors.toList());

                fReleasedQNR1 = orderInfo.getReleasedQnr().stream()
                        .filter(p -> !p.getStatus().equalsIgnoreCase("unReleased")).collect(Collectors.toList());
                orderInfo.getReleasedQnr().clear();
                orderInfo.setReleasedQnr((ArrayList<RlsdQnr>) fReleasedQNR); // remove releasedqnr response and add rlsdqnr without "unReleased" status

            }
            if (fReleasedQNR != null && !fReleasedQNR.isEmpty() && fReleasedQNR.size() > 0) {
                Gson gson = new Gson();
                String myJson = gson.toJson(orderInfo);

                Intent intent = new Intent(mContext, ReleasedQNRActivity.class);
                intent.putExtra("bookingInfo", myJson);
                intent.putExtra("from", Constants.ORDERS);
                mContext.startActivity(intent);

            } else {
                Intent intent = new Intent(getActivity(), OrderDetailActivity.class);

                //intent.putExtra("orderInfo", String.valueOf(orderInfo));
                intent.putExtra("uuid", orderInfo.getUid());
                intent.putExtra("account", String.valueOf(orderInfo.getProviderAccount().getId()));

                intent.putExtra("isActive", isActive);
                startActivity(intent);
            }
        }
    }

    @Override
    public void onOptionsClick(ActiveOrders orderInfo) {


        boolean isActive = false;
        if (orderInfo != null && orderInfo.getOrderStatus() != null) {
            if (!orderInfo.getOrderStatus().equalsIgnoreCase("Cancelled") && !orderInfo.getOrderStatus().equalsIgnoreCase("Completed")) {
                isActive = true;
            }
        }
        orderActionsDialog = new OrderActionsDialog(mContext, isActive, orderInfo, iActions);
        orderActionsDialog.getWindow().getAttributes().windowAnimations = R.style.slidingUpAndDown;
        orderActionsDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        orderActionsDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        orderActionsDialog.show();
        DisplayMetrics metrics = mContext.getResources().getDisplayMetrics();
        int width = (int) (metrics.widthPixels * 1);
        orderActionsDialog.getWindow().setGravity(Gravity.BOTTOM);
        orderActionsDialog.getWindow().setLayout(width, LinearLayout.LayoutParams.WRAP_CONTENT);

    }

    @Override
    public void onCancel() {

        apiGetAllOrders();
    }
}
