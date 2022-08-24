package com.jaldeeinc.jaldee.custom;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;

import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.jaldeeinc.jaldee.Interface.IDataGrid;
import com.jaldeeinc.jaldee.Interface.IDataGridListener;
import com.jaldeeinc.jaldee.R;
import com.jaldeeinc.jaldee.adapter.DataGridAdapter;
import com.jaldeeinc.jaldee.adapter.DetailPageItemsAdapter;
import com.jaldeeinc.jaldee.adapter.ItemsAdapter;
import com.jaldeeinc.jaldee.adapter.SelectedItemsAdapter;
import com.jaldeeinc.jaldee.model.CartItemModel;
import com.jaldeeinc.jaldee.model.DataGrid;
import com.jaldeeinc.jaldee.model.QuestionnairInpt;
import com.jaldeeinc.jaldee.response.CatalogItem;
import com.jaldeeinc.jaldee.response.GetQuestion;
import com.jaldeeinc.jaldee.response.Questionnaire;

import java.util.ArrayList;

/**
 * Created by Sravan Velnati on 24/08/21.
 */

public class QuestionnaireGridView extends LinearLayout implements IDataGrid {

    private Context context;
    private AttributeSet attrs;
    private int styleAttr;

    private CustomTextViewSemiBold tvQuestionName;
    private CustomTextViewBold tvManditory;
    private LinearLayout llAdd, llDivider;
    private CustomTextViewMedium tvHint;
    private CustomTextViewNormalItalic tvError;
    private RecyclerView rvDataTable;
    private GetQuestion question;
    private DataGridAdapter dataGridAdapter;
    public ArrayList<DataGrid> gridDataList = new ArrayList<>();
    FragmentManager fragmentManager;
    IDataGrid iDataGrid;
    IDataGridListener iDataGridListener;

    public QuestionnaireGridView(Context context) {
        super(context);
        this.context = context;
        iDataGrid = this;
        initView();
    }

    public QuestionnaireGridView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        this.attrs = attrs;
        initView();
    }

    public QuestionnaireGridView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        this.attrs = attrs;
        this.styleAttr = defStyleAttr;
        initView();
    }


    private void initView() {
        inflate(context, R.layout.data_grid, this);

        tvQuestionName = findViewById(R.id.tv_questionName);
        tvManditory = findViewById(R.id.tv_manditory);
        llAdd = findViewById(R.id.ll_add);
        tvHint = findViewById(R.id.tv_hint);
        tvError = findViewById(R.id.tv_error);
        rvDataTable = findViewById(R.id.rv_dataTable);
        llDivider = findViewById(R.id.ll_divider);

        rvDataTable.setLayoutManager(new LinearLayoutManager(context));
        dataGridAdapter = new DataGridAdapter(gridDataList, context, iDataGrid);
        rvDataTable.setAdapter(dataGridAdapter);

        if (gridDataList != null && gridDataList.size() > 0) {

            llDivider.setVisibility(VISIBLE);
            rvDataTable.setVisibility(VISIBLE);

        } else {

            llDivider.setVisibility(GONE);
            rvDataTable.setVisibility(GONE);
        }

    }

    public void setQuestionData(GetQuestion q) {

        question = q;

        setQuestionName(q.getLabel());

    }

    public void updateDataGrid(DataGrid dataGridObj, int position) {

        llDivider.setVisibility(VISIBLE);
        rvDataTable.setVisibility(VISIBLE);

        if (position >= 0) {
            gridDataList.set(position, dataGridObj);
        } else {
            gridDataList.add(dataGridObj);
        }

        rvDataTable.setLayoutManager(new LinearLayoutManager(context));
        dataGridAdapter = new DataGridAdapter(gridDataList, context, iDataGrid);
        rvDataTable.setAdapter(dataGridAdapter);
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

        if (tvHint != null) {
            tvHint.setText(hint);
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

            llDivider.setVisibility(VISIBLE);
            rvDataTable.setVisibility(VISIBLE);

        } else {

            llDivider.setVisibility(GONE);
            rvDataTable.setVisibility(GONE);
        }
        dataGridAdapter.updateDataList(gridDataList);

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
        dataGridAdapter.updateDataList(gridDataList);
    }

    @Override
    public void onAddClick(int position) {

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

