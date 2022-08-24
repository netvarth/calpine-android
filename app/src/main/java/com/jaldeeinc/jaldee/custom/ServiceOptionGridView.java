package com.jaldeeinc.jaldee.custom;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.AttributeSet;
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

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.jaldeeinc.jaldee.Interface.IDataGrid;
import com.jaldeeinc.jaldee.Interface.IDataGridListener;
import com.jaldeeinc.jaldee.Interface.IServiceOption;
import com.jaldeeinc.jaldee.R;
import com.jaldeeinc.jaldee.adapter.DetailPageItemsAdapter;
import com.jaldeeinc.jaldee.adapter.ItemsAdapter;
import com.jaldeeinc.jaldee.adapter.SelectedItemsAdapter;
import com.jaldeeinc.jaldee.adapter.ServiceOptionDataGridAdapter;
import com.jaldeeinc.jaldee.common.Config;
import com.jaldeeinc.jaldee.model.CartItemModel;
import com.jaldeeinc.jaldee.model.DataGrid;
import com.jaldeeinc.jaldee.model.GridColumnAnswerLine;
import com.jaldeeinc.jaldee.model.QuestionnairInpt;
import com.jaldeeinc.jaldee.response.CatalogItem;
import com.jaldeeinc.jaldee.response.GetQuestion;
import com.jaldeeinc.jaldee.response.Questionnaire;

import java.util.ArrayList;
import java.util.StringJoiner;

public class ServiceOptionGridView extends LinearLayout implements IDataGrid {
    private Context context;
    private AttributeSet attrs;
    private int styleAttr;
    IDataGrid iDataGrid;
    IDataGridListener iDataGridListener;

    public ArrayList<DataGrid> gridDataList = new ArrayList<>();

    private TextView tvQuestionName, tvHint;
    private TextView tvError;
    private TextView tvManditory;
    private LinearLayout llAdd;
    private CardView cvAdd;
    private RecyclerView rvDataTable;
    private ServiceOptionDataGridAdapter serviceOptionDataGridAdapter;
    private IServiceOption iServiceOptionListOptionChange;
    Dialog serviceOptionrepeatSameDialog;
    boolean isEdit = false;

    public ServiceOptionGridView(Context context, boolean isEdit) {
        super(context);
        this.context = context;
        iDataGrid = this;
        this.isEdit = isEdit;
        initView();
    }

    public ServiceOptionGridView(Context context, IServiceOption iServiceOptionListOptionChange, boolean isEdit) {
        super(context);
        this.context = context;
        this.iServiceOptionListOptionChange = iServiceOptionListOptionChange;
        iDataGrid = this;
        this.isEdit = isEdit;
        initView();
    }

    public ServiceOptionGridView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        this.attrs = attrs;
        initView();
    }

    public ServiceOptionGridView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        this.attrs = attrs;
        this.styleAttr = defStyleAttr;
        initView();
    }

    private void initView() {
        inflate(context, R.layout.service_option_data_grid, this);
        tvQuestionName = findViewById(R.id.tv_questionName);
        tvManditory = findViewById(R.id.tv_manditory);
        llAdd = findViewById(R.id.ll_add);
        cvAdd = findViewById(R.id.cv_add);
        tvError = findViewById(R.id.tv_error);
        rvDataTable = findViewById(R.id.rv_dataTable);
        tvHint = findViewById(R.id.tv_item_Hint);
        if (isEdit) {
            cvAdd.setVisibility(VISIBLE);
        } else {
            cvAdd.setVisibility(GONE);
        }
        rvDataTable.setLayoutManager(new LinearLayoutManager(context));
        serviceOptionDataGridAdapter = new ServiceOptionDataGridAdapter(gridDataList, context, iDataGrid, isEdit);
        rvDataTable.setAdapter(serviceOptionDataGridAdapter);
        rvDataTable.addOnLayoutChangeListener(new OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View view, int i, int i1, int i2, int i3, int i4, int i5, int i6, int i7) {
                if (iServiceOptionListOptionChange != null) {
                    iServiceOptionListOptionChange.updateTotalPrice();
                }
            }
        });
        if (gridDataList != null && gridDataList.size() > 0) {

            //llDivider.setVisibility(VISIBLE);
            rvDataTable.setVisibility(VISIBLE);

        } else {

            //llDivider.setVisibility(GONE);
            rvDataTable.setVisibility(GONE);
        }
    }

    public void setQuestionData(GetQuestion q) {

        setQuestionName(q.getLabel());
        setHint(q.getHint());

    }

    public void updateDataGrid(DataGrid dataGridObj, int position) {

        //llDivider.setVisibility(VISIBLE);
        rvDataTable.setVisibility(VISIBLE);

        if (position >= 0) {
            gridDataList.set(position, dataGridObj);
        } else {
            gridDataList.add(dataGridObj);
        }

        rvDataTable.setLayoutManager(new LinearLayoutManager(context));
        serviceOptionDataGridAdapter = new ServiceOptionDataGridAdapter(gridDataList, context, iDataGrid, isEdit);
        rvDataTable.setAdapter(serviceOptionDataGridAdapter);

    }

    public void setQuestionName(String questionName) {

        if (tvQuestionName != null && questionName != null) {
            tvQuestionName.setText(questionName);
        }
    }

    public void setMandatory(String mandatory) {
        if (tvManditory != null) {
            tvManditory.setText(mandatory);
        }
    }

    public void setHint(String hint) {
        hint = (hint == null) ? "" : hint;
        tvHint.setText(hint);
        if (hint.trim().equalsIgnoreCase("")) {
            tvHint.setVisibility(View.GONE);
        } else {
            tvHint.setVisibility(View.VISIBLE);
        }
    }


    public void setError(String error) {

        if (tvError != null) {
            tvError.setText(error);
        }
    }


    public LinearLayout getLlAdd() {
        return llAdd;
    }


    public void setGridDataList(ArrayList<DataGrid> gridDataList) {
        this.gridDataList = (gridDataList == null) ? new ArrayList<>() : gridDataList;
        if (gridDataList != null && gridDataList.size() > 0) {

            //llDivider.setVisibility(VISIBLE);
            rvDataTable.setVisibility(VISIBLE);

        } else {

            //llDivider.setVisibility(GONE);
            rvDataTable.setVisibility(GONE);
        }
        serviceOptionDataGridAdapter.updateDataList(gridDataList);

    }

    public ArrayList<DataGrid> getGridDataList() {

        return (gridDataList == null) ? new ArrayList<>() : gridDataList;
    }

    public void setiDataGridListener(IDataGridListener iDataGridListener) {
        this.iDataGridListener = iDataGridListener;
    }


    @Override
    public void onEditClick(DataGrid gridObj, int position) {

        iDataGridListener.onEditClick(gridObj, position);

    }

    @Override
    public void onEditClick(Questionnaire qnr, QuestionnairInpt answerGridObj, int position, CartItemModel itemDetails, boolean isEdit) {

    }


    @Override
    public void onDeleteClick(int position) {

        gridDataList.remove(position);
        serviceOptionDataGridAdapter.updateDataList(gridDataList);
    }

    @Override
    public void onAddClick(int position) {

        final DataGrid gridObj = gridDataList.get(position);

        serviceOptionrepeatSameDialog = new Dialog(context);
        serviceOptionrepeatSameDialog.getWindow().getAttributes().windowAnimations = R.style.slidingUpAndDown;
        serviceOptionrepeatSameDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        serviceOptionrepeatSameDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        serviceOptionrepeatSameDialog.setContentView(R.layout.service_option_repeat_same_dialog);
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
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
        iv_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                serviceOptionrepeatSameDialog.cancel();
            }
        });
        ll_Add_new.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                llAdd.performClick();
                serviceOptionrepeatSameDialog.cancel();

            }
        });
        ll_repeat_same.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                gridDataList.add(gridObj);
                serviceOptionDataGridAdapter.updateDataList(gridDataList);
                serviceOptionrepeatSameDialog.cancel();
                Toast.makeText(context, "item added", Toast.LENGTH_LONG).show();
//                DynamicToast.make(context, "item added",
//                        ContextCompat.getColor(context, R.color.white), ContextCompat.getColor(context, R.color.green), Toast.LENGTH_SHORT).show();
            }
        });

        ArrayList<GridColumnAnswerLine> datagridListColumns = gridObj.getDataGridListColumn();

        if (datagridListColumns.size() > 0) {
            String hint = "";
            StringJoiner joiner = new StringJoiner(",");
            float itemPrice = 0;
            for (int i = 0; i < datagridListColumns.size(); i++) {
                JsonArray ja = datagridListColumns.get(i).getColumn().getAsJsonArray("list");
                if (i == 0) {
                    if (ja.get(0).toString().trim() != null && !ja.get(0).toString().trim().isEmpty()) {
                        tv_itemName.setText(ja.get(0).getAsString());
                    } else {
                        tv_itemName.setVisibility(View.GONE);
                    }
                } else if (i > 0) {
                    if (ja != null && ja.size() > 0) {
                        for (JsonElement j : ja) {
                            joiner.add(j.getAsString());
                        }
                    }
                }
                itemPrice = itemPrice + datagridListColumns.get(i).getPrice();
            }
            if (joiner.length() > 0) {
                hint = joiner.toString();
                tv_item_Hint.setText(hint);
                tv_item_Hint.setVisibility(View.VISIBLE);
            } else {
                tv_item_Hint.setVisibility(View.GONE);
            }
            tv_item_price.setText("₹ " + Config.getAmountNoOrTwoDecimalPoints(itemPrice));
            //String qty = (String) viewHolder.number_counter.getText();
            String qty = "1";

            Integer qnty = Integer.parseInt(qty);
            if (qnty != null && qnty > 0) {
                tv_total_price.setText("₹ " + Config.getAmountNoOrTwoDecimalPoints(itemPrice * qnty));
            } else {
                tv_total_price.setText("₹ 0");
            }
        } else {
            tv_itemName.setVisibility(View.GONE);
            tv_item_Hint.setVisibility(View.GONE);
            //tv_iteme_dit.setVisibility(View.GONE);
            tv_total_price.setVisibility(View.GONE);
        }
        if (gridDataList.size() == position + 1) {
            llDivider.setVisibility(View.GONE);
        } else {
            llDivider.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onAddClick(CatalogItem catalogItem, ItemsAdapter.ViewHolder viewHolder, boolean isDecreaseQty, int newValue) {

    }

    @Override
    public void onAddClick(CartItemModel cartItemModel, SelectedItemsAdapter.ViewHolder viewHolder, boolean isDecreaseQty, int newValue) {

    }

    @Override
    public void onAddClick(CatalogItem catalogItem, DetailPageItemsAdapter.ViewHolder viewHolder, boolean isDecreaseQty, int newValue) {

    }

    @Override
    public void onRemoveClick(int position, Questionnaire questionnaire, QuestionnairInpt answerLine, CartItemModel itemDetails) {

    }
}
