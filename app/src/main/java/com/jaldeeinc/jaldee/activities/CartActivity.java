package com.jaldeeinc.jaldee.activities;

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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.jaldeeinc.jaldee.Interface.ICartInterface;
import com.jaldeeinc.jaldee.Interface.IDataGrid;
import com.jaldeeinc.jaldee.Interface.IItemInterface;
import com.jaldeeinc.jaldee.Interface.ISaveNotes;
import com.jaldeeinc.jaldee.Interface.IServiceOption;
import com.jaldeeinc.jaldee.R;
import com.jaldeeinc.jaldee.adapter.CartItemServceOptnAdapter;
import com.jaldeeinc.jaldee.adapter.DetailPageItemsAdapter;
import com.jaldeeinc.jaldee.adapter.ItemsAdapter;
import com.jaldeeinc.jaldee.adapter.SelectedItemsAdapter;
import com.jaldeeinc.jaldee.common.Config;
import com.jaldeeinc.jaldee.connection.ApiClient;
import com.jaldeeinc.jaldee.connection.ApiInterface;
import com.jaldeeinc.jaldee.custom.CustomNotes;
import com.jaldeeinc.jaldee.custom.CustomTextViewMedium;
import com.jaldeeinc.jaldee.custom.CustomTextViewSemiBold;
import com.jaldeeinc.jaldee.custom.KeyPairBoolData;
import com.jaldeeinc.jaldee.custom.OrderitemServiceoptionadditemDialog;
import com.jaldeeinc.jaldee.database.DatabaseHandler;
import com.jaldeeinc.jaldee.model.AnswerLine;
import com.jaldeeinc.jaldee.model.BookingModel;
import com.jaldeeinc.jaldee.model.CartItemModel;
import com.jaldeeinc.jaldee.model.DataGrid;
import com.jaldeeinc.jaldee.model.GridColumnAnswerLine;
import com.jaldeeinc.jaldee.model.QuestionnairInpt;
import com.jaldeeinc.jaldee.response.Catalog;
import com.jaldeeinc.jaldee.response.CatalogItem;
import com.jaldeeinc.jaldee.response.Questionnaire;
import com.jaldeeinc.jaldee.response.SearchViewDetail;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.StringJoiner;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CartActivity extends AppCompatActivity implements IItemInterface, ICartInterface, ISaveNotes, IDataGrid, IServiceOption {

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
    /**
     * ServiceOption
     **/
    IDataGrid iDataGrid;
    IServiceOption iServiceOptionListOptionChange;
    private Questionnaire questionnaire = new Questionnaire();
    private IItemInterface iItemInterface;

    /**
     * ServiceOption
     **/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);
        ButterKnife.bind(CartActivity.this);
        mContext = CartActivity.this;
        iCartInterface = (ICartInterface) this;
        iSaveNotes = (ISaveNotes) this;
        iDataGrid = (IDataGrid) this;
        iItemInterface = (IItemInterface) this;
        iServiceOptionListOptionChange = (IServiceOption) this;

        Intent intent = getIntent();
        catalogInfo = (Catalog) intent.getSerializableExtra("catalogInfo");
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
            selectedItemsAdapter = new SelectedItemsAdapter(cartItemsList, mContext, false, iCartInterface, true, iDataGrid, false);
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

                        if (questionnaire != null && questionnaire.getQuestionnaireId() != null && questionnaire.getQuestionsList() != null && questionnaire.getQuestionsList().size() > 0) {
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

    @Override
    public void onEditClick(DataGrid gridObj, int position) {

    }

    @Override
    public void onEditClick(Questionnaire questionnaire, QuestionnairInpt answerGridObj, int position, CartItemModel itemdDetails, boolean isEdit) {
        String qnr = new Gson().toJson(questionnaire);
        String qnrInput = new Gson().toJson(answerGridObj);

        OrderitemServiceoptionadditemDialog orderitemServiceoptionadditemDialog = OrderitemServiceoptionadditemDialog.newInstance(qnr, qnrInput, position, itemdDetails, isEdit, iServiceOptionListOptionChange);
        final FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.container, orderitemServiceoptionadditemDialog).addToBackStack("DataGrid")
                .commit();
    }

    @Override
    public void onDeleteClick(int position) {

    }

    @Override
    public void onAddClick(int position) {

    }

    @Override
    public void onAddClick(CatalogItem catalogItem, ItemsAdapter.ViewHolder viewHolder, boolean isDecreaseQty, int newValue) {

    }

    QuestionnairInpt answerLine;

    @Override
    public void onAddClick(CartItemModel cartItemModel, SelectedItemsAdapter.ViewHolder viewHolder, boolean isDecreaseQty, int newValue) {
        SelectedItemsAdapter.ViewHolder vHolder = viewHolder;
        if (isDecreaseQty) {
            Dialog removeItemSrvcoption;

            removeItemSrvcoption = new Dialog(mContext);
            removeItemSrvcoption.getWindow().getAttributes().windowAnimations = R.style.slidingUpAndDown;
            removeItemSrvcoption.requestWindowFeature(Window.FEATURE_NO_TITLE);
            removeItemSrvcoption.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            removeItemSrvcoption.setContentView(R.layout.remove_cart_item_serviceoption_dialog);
            DisplayMetrics metrics = mContext.getResources().getDisplayMetrics();
            int width = (int) (metrics.widthPixels * 1);
            removeItemSrvcoption.setCancelable(false);
            removeItemSrvcoption.getWindow().setLayout(width, LinearLayout.LayoutParams.WRAP_CONTENT);
            removeItemSrvcoption.getWindow().setGravity(Gravity.BOTTOM);
            ImageView iv_close = removeItemSrvcoption.findViewById(R.id.iv_close);
            CardView cv_cancel = removeItemSrvcoption.findViewById(R.id.cv_cancel);
            CardView cv_done = removeItemSrvcoption.findViewById(R.id.cv_done);
            RecyclerView rv_itemDetails = removeItemSrvcoption.findViewById(R.id.rv_itemDetails);

            removeItemSrvcoption.show();
            iv_close.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    removeItemSrvcoption.cancel();
                    int qty = Integer.parseInt(vHolder.numberButton.getNumber());
                    vHolder.numberButton.setNumber(String.valueOf(qty + 1));   // for corecting quantity of items

                }
            });
            cv_cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    removeItemSrvcoption.cancel();
                    int qty = Integer.parseInt(vHolder.numberButton.getNumber());
                    vHolder.numberButton.setNumber(String.valueOf(qty + 1));   // for corecting quantity of items
                }
            });
            cv_done.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    removeItemSrvcoption.cancel();
                    if (answerLine != null && answerLine.getAnswerLines() != null && answerLine.getAnswerLines().size() != 0 && itemDetails1 != null) {
                        String inputImages = db.getServiceOptioniputImages(itemDetails1.getItemId());
                        String qnr = db.getServiceOptionQnr(itemDetails1.getItemId());

                        float serviceOtpionPrice = 0;
                        if (qnr.trim() != null && answerLine != null && !answerLine.getAnswerLines().isEmpty()) {
                            Gson gson = new Gson();
                            try {
                                ItemsActivity itemsActivity = new ItemsActivity();
                                serviceOtpionPrice = itemsActivity.calculateTotalPrice(itemDetails1.getItemId(), qnr, gson.toJson(answerLine));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        boolean result = db.updateServiceOptionInput(itemDetails1.getItemId(), new Gson().toJson(answerLine), inputImages, serviceOtpionPrice);
                        if (result) {
                            db.addQuantity(itemDetails1.getItemId(), newValue);
                            checkCartCount();
                        }
                    } else {
                        int qty = Integer.parseInt(vHolder.numberButton.getNumber());
                        vHolder.numberButton.setNumber(String.valueOf(qty + 1));   // for corecting quantity of items
                    }
                }
            });

            //CartItemModel cartItemModel = new CartItemModel();
            //cartItemModel.setItemId(catalogItem.getItems().getItemId());

            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mContext);
            rv_itemDetails.setLayoutManager(linearLayoutManager);
            CartItemServceOptnAdapter cartItemServceOptnAdapter = new CartItemServceOptnAdapter(cartItemModel, mContext, iDataGrid, false, true);
            rv_itemDetails.setAdapter(cartItemServceOptnAdapter);
            rv_itemDetails.setVisibility(View.VISIBLE);
        } else {
            float totalPrice = 0;

            answerLine = new QuestionnairInpt();
            String itemServcOptionName = "";
            StringJoiner joiner = new StringJoiner(",");

            String input = db.getServiceOptioniput(cartItemModel.getItemId());
            String inputImages = db.getServiceOptioniputImages(cartItemModel.getItemId());
//
//        if (qnr != null && !qnr.trim().isEmpty()) {
//            Gson gson = new Gson();
//            this.questionaire = gson.fromJson(qnr, Questionnaire.class);
//        }

            Dialog serviceOptionrepeatSameDialog;

            serviceOptionrepeatSameDialog = new Dialog(mContext);
            serviceOptionrepeatSameDialog.getWindow().getAttributes().windowAnimations = R.style.slidingUpAndDown;
            serviceOptionrepeatSameDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            serviceOptionrepeatSameDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            serviceOptionrepeatSameDialog.setContentView(R.layout.service_option_repeat_same_dialog);
            DisplayMetrics metrics = mContext.getResources().getDisplayMetrics();
            int width = (int) (metrics.widthPixels * 1);
            serviceOptionrepeatSameDialog.setCancelable(false);
            serviceOptionrepeatSameDialog.getWindow().setLayout(width, LinearLayout.LayoutParams.WRAP_CONTENT);
            serviceOptionrepeatSameDialog.getWindow().setGravity(Gravity.BOTTOM);
            ImageView iv_close = serviceOptionrepeatSameDialog.findViewById(R.id.iv_close);
            LinearLayout ll_Add_new = serviceOptionrepeatSameDialog.findViewById(R.id.ll_Add_new);
            LinearLayout ll_repeat_same = serviceOptionrepeatSameDialog.findViewById(R.id.ll_repeat_same);
            TextView tv_itemName = serviceOptionrepeatSameDialog.findViewById(R.id.tv_itemName);
            TextView tv_item_Hint = serviceOptionrepeatSameDialog.findViewById(R.id.tv_item_Hint);
            TextView tv_item_price = serviceOptionrepeatSameDialog.findViewById(R.id.tv_item_price);
            TextView tv_total_price = serviceOptionrepeatSameDialog.findViewById(R.id.tv_total_price);
            LinearLayout llDivider = serviceOptionrepeatSameDialog.findViewById(R.id.ll_divider);
            serviceOptionrepeatSameDialog.show();

            if (input != null && !input.trim().isEmpty()) {
                Gson gson = new Gson();
                answerLine = gson.fromJson(input, QuestionnairInpt.class);
            }
            if (answerLine != null && !answerLine.getAnswerLines().isEmpty()) {
                ArrayList<AnswerLine> als = answerLine.getAnswerLines();
                for (AnswerLine al : als) {
                    DataGrid dataGrid = new DataGrid();
                    ArrayList<DataGrid> dataGridList = new ArrayList<>();
                    dataGridList = al.getDataGridListList();
                    dataGrid = dataGridList.get(dataGridList.size() - 1);
                    ArrayList<GridColumnAnswerLine> dataGridListColumn = dataGrid.dataGridListColumn;
                    for (int i = 0; i < dataGridListColumn.size(); i++) {
                        GridColumnAnswerLine gridColumnAnswerLine = dataGridListColumn.get(i);
                        JsonArray ja = gridColumnAnswerLine.getColumn().getAsJsonArray("list");
                        if (i == 0) {
                            if (ja.get(0).toString().trim() != null && !ja.get(0).toString().trim().isEmpty()) {
                                joiner.add(ja.get(0).getAsString());
                            }
                        }
                        totalPrice = totalPrice + gridColumnAnswerLine.getPrice();
                    }
                }

                if (cartItemModel != null && cartItemModel.getItemName().trim() != null) {
                    tv_itemName.setText(cartItemModel.getItemName());
                } else {
                    tv_itemName.setVisibility(View.GONE);
                }
                if (totalPrice > 0) {
                    tv_item_price.setText("₹ " + Config.getAmountNoOrTwoDecimalPoints(totalPrice));
                    tv_total_price.setText("₹ " + Config.getAmountNoOrTwoDecimalPoints(totalPrice));
                } else {
                    tv_total_price.setText("₹ 0");
                }
                String hint = "";
                if (joiner.length() > 0) {
                    hint = joiner.toString();
                    tv_item_Hint.setText(hint);
                    tv_item_Hint.setVisibility(View.VISIBLE);
                } else {
                    tv_item_Hint.setVisibility(View.GONE);
                }
            } else {
                tv_itemName.setVisibility(View.GONE);
                tv_item_Hint.setVisibility(View.GONE);
                //tv_iteme_dit.setVisibility(View.GONE);
                tv_total_price.setVisibility(View.GONE);
            }
            iv_close.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    serviceOptionrepeatSameDialog.cancel();
                    int qty = Integer.parseInt(vHolder.numberButton.getNumber());
                    if (qty > 0) {
                        vHolder.numberButton.setNumber(String.valueOf(qty - 1));   // for corecting quantity of items
                    }
                }
            });
            ll_Add_new.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // llAdd.performClick();
                    serviceOptionrepeatSameDialog.cancel();
                    apiGetOrderItemServiceOptionQnr(cartItemModel, providerAccountId, true);

                    //checkItemQuantity(catalogItem, (DetailPageItemsAdapter.ViewHolder) null);

                }
            });
            ll_repeat_same.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    serviceOptionrepeatSameDialog.cancel();
                    Toast.makeText(mContext, "item added", Toast.LENGTH_LONG).show();
//                DynamicToast.make(context, "item added",
//                        ContextCompat.getColor(context, R.color.white), ContextCompat.getColor(context, R.color.green), Toast.LENGTH_SHORT).show();
                    if (answerLine != null && !answerLine.getAnswerLines().isEmpty()) {
                        ArrayList<AnswerLine> als = answerLine.getAnswerLines();
                        for (int k = 0; k < als.size(); k++) {

                            AnswerLine al = als.get(k);
                            DataGrid dataGrid = new DataGrid();
                            ArrayList<DataGrid> dataGridList = new ArrayList<>();
                            dataGridList = al.getDataGridListList();
                            dataGrid = dataGridList.get(dataGridList.size() - 1);
                            dataGridList.add(dataGrid);
                            JsonObject answer = new JsonObject();
                            Gson gson = new Gson();
                            JsonElement element = gson.toJsonTree(dataGridList);
                            answer.add("dataGridList", element);
                            answerLine.getAnswerLines().get(k).setAnswer(answer);
                        }
                        String qnr = db.getServiceOptionQnr(cartItemModel.getItemId());

                        float serviceOtpionPrice = 0;
                        if (qnr.trim() != null && answerLine != null && !answerLine.getAnswerLines().isEmpty()) {
                            Gson gson = new Gson();
                            try {
                                ItemsActivity itemsActivity = new ItemsActivity();
                                serviceOtpionPrice = itemsActivity.calculateTotalPrice(cartItemModel.getItemId(), qnr, gson.toJson(answerLine));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        db.addQuantity(cartItemModel.getItemId(), newValue);
                        db.updateServiceOptionInput(cartItemModel.getItemId(), new Gson().toJson(answerLine), inputImages, serviceOtpionPrice);
                        checkCartCount();
                    }
                }
            });
        }
    }

    @Override
    public void onAddClick(CatalogItem catalogItem, DetailPageItemsAdapter.ViewHolder viewHolder, boolean isDecreaseQty, int newValue) {

    }

    CartItemModel itemDetails1;

    @Override
    public void onRemoveClick(int position, Questionnaire questionnaire, QuestionnairInpt answerLine, CartItemModel itemDetails) {
        this.answerLine = answerLine;
        this.itemDetails1 = itemDetails;
        try {
            ItemsActivity itemsActivity = new ItemsActivity();
            float price = itemsActivity.calculateTotalPrice(itemDetails.getItemId(), new Gson().toJson(questionnaire), new Gson().toJson(answerLine));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void updateTotalPrice() {

    }

    @Override
    public void radioListItemSelected(String s, Float price) {

    }

    @Override
    public void savetoDataBase(CatalogItem itemDetails, String serviceOption, String serviceOptionAtachedImages) {
        try {
            String qnr = db.getServiceOptionQnr(itemDetails.getItems().getItemId());
            float serviceOtpionPrice = 0;

            if (qnr.trim() != null && serviceOption.trim() != null) {

                ItemsActivity itemsActivity = new ItemsActivity();
                serviceOtpionPrice = itemsActivity.calculateTotalPrice(itemDetails.getItems().getItemId(), qnr, serviceOption);

            }
            boolean isUpdated = db.updateServiceOptionInput(itemDetails.getItems().getItemId(), serviceOption, serviceOptionAtachedImages, serviceOtpionPrice);
            if (isUpdated) {
                updateUI();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public KeyPairBoolData openImageOptions(KeyPairBoolData fileObject, String qLabelName, HashMap<String, View> viewsList) {
        return null;
    }

    private void apiGetOrderItemServiceOptionQnr(CartItemModel itemDetailsModel, int providerId, boolean fromAddedAsNew) {
        final ApiInterface apiService =
                ApiClient.getClient(mContext).create(ApiInterface.class);
        //final Dialog mDialog = Config.getProgressDialog(mContext, mContext.getResources().getString(R.string.dialog_log_in));
        //mDialog.show();
        Call<Questionnaire> call = apiService.getOrderItemServiceOptionQnr(itemDetailsModel.getItemId(), providerId);
        call.enqueue(new Callback<Questionnaire>() {
            @Override
            public void onResponse(Call<Questionnaire> call, Response<Questionnaire> response) {
                Config.logV("URL------getQNR response---------" + response.raw().request().url().toString().trim());
                Config.logV("Response--code-------------------------" + response.code());
                if (response.code() == 200) {
                    questionnaire = response.body();
                    //                        Map<String, Object> retMap = new Gson().fromJson(
//                                questionnaire.getQuestionsList().get(0).getGetQuestion().getPriceGridList(), new TypeToken<HashMap<String, Object>>() {
//                                }.getType()
//                        );
                    if (questionnaire != null && questionnaire.getQuestionnaireId() != null && questionnaire.getQuestionsList() != null && questionnaire.getQuestionsList().size() > 0) {
                        OrderitemServiceoptionadditemDialog orderitemServiceoptionadditemDialog = OrderitemServiceoptionadditemDialog.newInstance(questionnaire, null, itemDetailsModel, iServiceOptionListOptionChange, iItemInterface, fromAddedAsNew);
                        final FragmentManager fragmentManager = getSupportFragmentManager();
                        fragmentManager.beginTransaction()
                                .replace(R.id.container, orderitemServiceoptionadditemDialog).addToBackStack("DataGrid")
                                .commit();
                    } else {
                        //saveToDB(itemDetails, null, null);
                    }

                }
            }

            @Override
            public void onFailure(Call<Questionnaire> call, Throwable t) {

            }
        });
    }

    @Override
    public void onItemClick(CatalogItem catalogItem) {
        checkCartCount();
    }

    @Override
    public void checkItemQuantity() {
        updateUI();
    }

    @Override
    public void checkItemQuantity(CatalogItem itemDetails, ItemsAdapter.ViewHolder viewHolder) {

    }

    @Override
    public void checkItemQuantity(CatalogItem itemDetails, DetailPageItemsAdapter.ViewHolder viewHolder) {

    }
}