package com.jaldeeinc.jaldee.custom;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
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

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.jaldeeinc.jaldee.Interface.ICartInterface;
import com.jaldeeinc.jaldee.Interface.IDataGrid;
import com.jaldeeinc.jaldee.Interface.IDialogInterface;
import com.jaldeeinc.jaldee.Interface.IItemInterface;
import com.jaldeeinc.jaldee.Interface.IServiceOption;
import com.jaldeeinc.jaldee.R;
import com.jaldeeinc.jaldee.activities.ItemsActivity;
import com.jaldeeinc.jaldee.adapter.CartItemServceOptnAdapter;
import com.jaldeeinc.jaldee.adapter.DetailPageItemsAdapter;
import com.jaldeeinc.jaldee.adapter.ItemsAdapter;
import com.jaldeeinc.jaldee.adapter.SelectedItemsAdapter;
import com.jaldeeinc.jaldee.common.Config;
import com.jaldeeinc.jaldee.connection.ApiClient;
import com.jaldeeinc.jaldee.connection.ApiInterface;
import com.jaldeeinc.jaldee.database.DatabaseHandler;
import com.jaldeeinc.jaldee.model.AnswerLine;
import com.jaldeeinc.jaldee.model.CartItemModel;
import com.jaldeeinc.jaldee.model.DataGrid;
import com.jaldeeinc.jaldee.model.GridColumnAnswerLine;
import com.jaldeeinc.jaldee.model.QuestionnairInpt;
import com.jaldeeinc.jaldee.response.CatalogItem;
import com.jaldeeinc.jaldee.response.Questionnaire;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.StringJoiner;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Response;

public class SelectedItemsDialog extends Dialog implements ICartInterface, IDataGrid, IServiceOption, IItemInterface {

    @BindView(R.id.rv_items)
    RecyclerView rvItems;

    @BindView(R.id.tv_clearCart)
    CustomTextViewSemiBold tvClearCart;

    @BindView(R.id.tv_continue)
    CustomTextViewSemiBold tvContinue;

    private Context mContext;
    private CatalogItem itemDetails;
    private LinearLayoutManager linearLayoutManager;
    private SelectedItemsAdapter selectedItemsAdapter;
    private ICartInterface iCartInterface;
    private IDialogInterface iDialogInterface;
    DatabaseHandler db;
    ArrayList<CartItemModel> cartItemsList = new ArrayList<>();
    IDataGrid iDataGrid;
    QuestionnairInpt answerLine;
    CartItemModel itemDetails1;
    private Questionnaire questionnaire = new Questionnaire();
    IServiceOption iServiceOptionListOptionChange;
    private IItemInterface iItemInterface;
    int providerAccountId;
    boolean fromSelectecItemsDialog = true;

    public SelectedItemsDialog(Context mContext, IDialogInterface iDialogInterface, int providerAccountId) {
        super(mContext);
        this.mContext = mContext;
        this.iDialogInterface = iDialogInterface;
        this.providerAccountId = providerAccountId;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.selected_items);
        ButterKnife.bind(this);
        iCartInterface = (ICartInterface) this;
        iDataGrid = (IDataGrid) this;
        iServiceOptionListOptionChange = (IServiceOption) this;
        iItemInterface = (IItemInterface) this;

        db = new DatabaseHandler(mContext);
        cartItemsList = db.getCartItems();

        if (cartItemsList != null && cartItemsList.size() > 0) {

            linearLayoutManager = new LinearLayoutManager(mContext);
            rvItems.setLayoutManager(linearLayoutManager);
            selectedItemsAdapter = new SelectedItemsAdapter(cartItemsList, mContext, false, iCartInterface,false, iDataGrid, fromSelectecItemsDialog);
            rvItems.setAdapter(selectedItemsAdapter);

        }

        tvContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                iDialogInterface.onContinueClick();
                dismiss();
            }
        });

        tvClearCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                db.DeleteCart();
                iDialogInterface.onClearClick();
                dismiss();
            }
        });

    }


    public void onRefresh() {

        db = new DatabaseHandler(mContext);
        cartItemsList.clear();
        cartItemsList = db.getCartItems();

        if (cartItemsList != null && cartItemsList.size() > 0) {

            linearLayoutManager = new LinearLayoutManager(mContext);
            rvItems.setLayoutManager(linearLayoutManager);
            selectedItemsAdapter = new SelectedItemsAdapter(cartItemsList, mContext, false, iCartInterface,false, iDataGrid, fromSelectecItemsDialog);
            rvItems.setAdapter(selectedItemsAdapter);

        }
    }


    @Override
    public void checkCartCount() {
        if (db.getCartCount() <= 0) {
            iDialogInterface.onClearClick();
            dismiss();
        } else {
            onRefresh();
        }
    }

    @Override
    public void openNotes(int itemId, String instruction) {

    }

    @Override
    public void onEditClick(DataGrid gridObj, int position) {

    }

    @Override
    public void onEditClick(Questionnaire qnr, QuestionnairInpt answerGridObj, int position, CartItemModel itemDetails, boolean isEdit) {

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
                        if (qnr.trim() != null && answerLine != null && !answerLine.getAnswerLines().isEmpty() ) {
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
                        if (qnr.trim() != null && answerLine != null && !answerLine.getAnswerLines().isEmpty() ) {
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

    @Override
    public void onRemoveClick(int position, Questionnaire questionnaire, QuestionnairInpt answerLine, CartItemModel itemDetails) {
        this.answerLine = answerLine;
        this.itemDetails1 = itemDetails;
        try {
            ItemsActivity itemsActivity = new ItemsActivity();
            float price =  itemsActivity.calculateTotalPrice(itemDetails.getItemId(), new Gson().toJson(questionnaire), new Gson().toJson(answerLine));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    private void apiGetOrderItemServiceOptionQnr(CartItemModel itemDetailsModel, int providerId, boolean fromAddedAsNew) {
        final ApiInterface apiService =
                ApiClient.getClient(mContext).create(ApiInterface.class);
        //final Dialog mDialog = Config.getProgressDialog(mContext, mContext.getResources().getString(R.string.dialog_log_in));
        //mDialog.show();
        Call<Questionnaire> call = apiService.getOrderItemServiceOptionQnr(itemDetailsModel.getItemId(), providerId);
        call.enqueue(new retrofit2.Callback<Questionnaire>() {
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
                        /*final FragmentManager fragmentManager = mContext.getSupportFragmentManager();
                        fragmentManager.beginTransaction()
                                .replace(R.id.container, orderitemServiceoptionadditemDialog).addToBackStack("DataGrid")
                                .commit();*/
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
    public void updateTotalPrice() {

    }

    @Override
    public void radioListItemSelected(String s, Float price) {

    }

    @Override
    public void savetoDataBase(CatalogItem itemDetails, String serviceOption, String serviceOptionAtachedImages) {

    }

    @Override
    public KeyPairBoolData openImageOptions(KeyPairBoolData fileObject, String qLabelName, HashMap<String, View> viewsList) {
        return null;
    }

    @Override
    public void onItemClick(CatalogItem catalogItem) {

    }

    @Override
    public void checkItemQuantity() {

    }

    @Override
    public void checkItemQuantity(CatalogItem itemDetails, ItemsAdapter.ViewHolder viewHolder) {

    }

    @Override
    public void checkItemQuantity(CatalogItem itemDetails, DetailPageItemsAdapter.ViewHolder viewHolder) {

    }
}
