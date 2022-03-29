package com.jaldeeinc.jaldee.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;

import com.jaldeeinc.jaldee.Interface.ICartInterface;
import com.jaldeeinc.jaldee.Interface.ISaveNotes;
import com.jaldeeinc.jaldee.R;
import com.jaldeeinc.jaldee.adapter.SelectedItemsAdapter;
import com.jaldeeinc.jaldee.common.Config;
import com.jaldeeinc.jaldee.connection.ApiClient;
import com.jaldeeinc.jaldee.connection.ApiInterface;
import com.jaldeeinc.jaldee.custom.CustomNotes;
import com.jaldeeinc.jaldee.custom.CustomTextViewMedium;
import com.jaldeeinc.jaldee.custom.CustomTextViewSemiBold;
import com.jaldeeinc.jaldee.database.DatabaseHandler;
import com.jaldeeinc.jaldee.model.BookingModel;
import com.jaldeeinc.jaldee.model.CartItemModel;
import com.jaldeeinc.jaldee.response.Catalog;
import com.jaldeeinc.jaldee.response.CatalogItem;
import com.jaldeeinc.jaldee.response.Questionnaire;
import com.jaldeeinc.jaldee.response.SearchViewDetail;
import com.jaldeeinc.jaldee.utils.SharedPreference;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CartActivity extends AppCompatActivity implements ICartInterface, ISaveNotes {

    @BindView(R.id.rv_items)
    RecyclerView rvItems;

    @BindView(R.id.tv_subTotal)
    CustomTextViewMedium tvSubTotal;

    @BindView(R.id.tv_discountedPrice)
    CustomTextViewSemiBold tvDiscountedPrice;

    @BindView(R.id.cv_checkOut)
    CardView cvCheckOut;

    @BindView(R.id.ll_noItems)
    LinearLayout llNoItems;

    @BindView(R.id.cv_itemsCart)
    CardView cvItemsCart;

    @BindView(R.id.cv_back)
    CardView cvBack;

    private Context mContext;
    private int providerAccountId, serviceId;
    private CatalogItem itemDetails;
    private LinearLayoutManager linearLayoutManager;
    private SelectedItemsAdapter selectedItemsAdapter;
    private ICartInterface iCartInterface;
    DatabaseHandler db;
    private ISaveNotes iSaveNotes;
    private CustomNotes customNotes;
    private SearchViewDetail mBusinessDataList = new SearchViewDetail();
    ArrayList<CartItemModel> cartItemsList = new ArrayList<>();
    private Catalog catalogInfo = new Catalog();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);
        ButterKnife.bind(CartActivity.this);
        mContext = CartActivity.this;
        iCartInterface = (ICartInterface) this;
        iSaveNotes = (ISaveNotes) this;

        Intent intent = getIntent();
        catalogInfo = (Catalog)  intent.getSerializableExtra("catalogInfo");
        serviceId = intent.getIntExtra("serviceId", 0);
        providerAccountId = intent.getIntExtra("providerAccountId", 0);
        mBusinessDataList = (SearchViewDetail) intent.getSerializableExtra("providerInfo");

        updateUI();

        cvCheckOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getQuestionnaire(serviceId, providerAccountId);
            }
        });

        cvBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void updateUI() {

        db = new DatabaseHandler(mContext);

        cartItemsList.clear();
        cartItemsList = db.getCartItems();

        if (cartItemsList != null && cartItemsList.size() > 0) {
            llNoItems.setVisibility(View.GONE);
            cvItemsCart.setVisibility(View.VISIBLE);
            rvItems.setVisibility(View.VISIBLE);
            linearLayoutManager = new LinearLayoutManager(mContext);
            rvItems.setLayoutManager(linearLayoutManager);
            selectedItemsAdapter = new SelectedItemsAdapter(cartItemsList, mContext, false, iCartInterface, true);
            rvItems.setAdapter(selectedItemsAdapter);

            if (db.getCartPrice() == db.getCartDiscountedPrice()) {
                tvSubTotal.setVisibility(View.GONE);
                tvDiscountedPrice.setVisibility(View.VISIBLE);
                String discountedPrice = Config.getAmountNoOrTwoDecimalPoints(db.getCartPrice());
                tvDiscountedPrice.setText("₹ " + discountedPrice);
            } else {

                tvSubTotal.setVisibility(View.VISIBLE);
                String amount = Config.getAmountNoOrTwoDecimalPoints(db.getCartPrice());
                tvSubTotal.setText("₹ " + amount);
                tvSubTotal.setPaintFlags(tvSubTotal.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                tvDiscountedPrice.setVisibility(View.VISIBLE);
                String discountedPrice = Config.getAmountNoOrTwoDecimalPoints(db.getCartDiscountedPrice());
                tvDiscountedPrice.setText("₹ " + discountedPrice);
            }

        } else {

            rvItems.setVisibility(View.GONE);
            cvItemsCart.setVisibility(View.GONE);
            llNoItems.setVisibility(View.VISIBLE);

        }

    }

    @Override
    public void checkCartCount() {

        updateUI();

    }

    @Override
    public void openNotes(int itemId, String instruction) {

        customNotes = new CustomNotes(mContext, itemId, iSaveNotes, instruction);
        customNotes.getWindow().getAttributes().windowAnimations = R.style.slidingUpAndDown;
        customNotes.requestWindowFeature(Window.FEATURE_NO_TITLE);
        customNotes.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        customNotes.setCancelable(false);
        customNotes.show();
        DisplayMetrics metrics = mContext.getResources().getDisplayMetrics();
        int width = (int) (metrics.widthPixels * 1);
        customNotes.getWindow().setGravity(Gravity.BOTTOM);
        customNotes.getWindow().setLayout(width, LinearLayout.LayoutParams.WRAP_CONTENT);

    }

    @Override
    public void saveMessage(String message, int itemId) {

        db.addInstructions(itemId, message);
        updateUI();
    }

    private void getQuestionnaire(int serviceId, int providerAccountId) {

        final ApiInterface apiService =
                ApiClient.getClient(mContext).create(ApiInterface.class);
        final Dialog mDialog = Config.getProgressDialog(mContext, mContext.getResources().getString(R.string.dialog_log_in));
        mDialog.show();
        Call<Questionnaire> call = apiService.getOrdersQuestions(serviceId, providerAccountId);
        call.enqueue(new Callback<Questionnaire>() {

            @Override
            public void onResponse(Call<Questionnaire> call, Response<Questionnaire> response) {
                try {
                    if (mDialog.isShowing())
                        Config.closeDialog(getParent(), mDialog);
                    Config.logV("URL------ACTIVE CHECKIN---------" + response.raw().request().url().toString().trim());
                    Config.logV("Response--code-------------------------" + response.code());
                    if (response.code() == 200) {
                        Questionnaire questionnaire = response.body();
                        BookingModel model = new BookingModel();
                        model.setAccountId(providerAccountId);
                        model.setCatalogInfo(catalogInfo);
                        model.setQuestionnaire(questionnaire);
                        model.setFrom(Constants.ORDERS);

                        if (questionnaire != null && questionnaire.getQuestionsList() != null) {
                            Intent intent = new Intent(CartActivity.this, CustomQuestionnaire.class);
                            intent.putExtra("data", model);
                            intent.putExtra("from", Constants.ORDERS);
                            startActivity(intent);
                        } else {

                            Intent intent = new Intent(CartActivity.this, CheckoutItemsActivity.class);
                            intent.putExtra("accountId", providerAccountId);
                            intent.putExtra("providerInfo", mBusinessDataList);
                            startActivity(intent);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<Questionnaire> call, Throwable t) {
                if (mDialog.isShowing())
                    Config.closeDialog(getParent(), mDialog);
            }
        });
    }
}