package com.jaldeeinc.jaldee.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.jaldeeinc.jaldee.Interface.IDataGrid;
import com.jaldeeinc.jaldee.R;
import com.jaldeeinc.jaldee.common.Config;
import com.jaldeeinc.jaldee.database.DatabaseHandler;
import com.jaldeeinc.jaldee.model.AnswerLine;
import com.jaldeeinc.jaldee.model.CartItemModel;
import com.jaldeeinc.jaldee.model.DataGrid;
import com.jaldeeinc.jaldee.model.GridColumnAnswerLine;
import com.jaldeeinc.jaldee.model.QuestionnairInpt;
import com.jaldeeinc.jaldee.response.Questionnaire;

import java.util.ArrayList;
import java.util.StringJoiner;

public class CartItemServceOptnAdapter extends RecyclerView.Adapter<CartItemServceOptnAdapter.ViewHolder> {
    boolean isLoading;
    Context context;
    private DatabaseHandler db;
    String input;
    String qnr;
    QuestionnairInpt answerLine = new QuestionnairInpt();
    QuestionnairInpt answerLineCopy = new QuestionnairInpt();

    boolean isEdit = false;
    IDataGrid iDataGrid;
    DataGrid dataGrid = new DataGrid();
    Questionnaire questionaire;
    CartItemModel itemDetails;
    boolean isFromRemove;
    boolean fromSelectedItemsDialog;
    public CartItemServceOptnAdapter(CartItemModel itemDetails, Context context, IDataGrid iDataGrid, boolean isEdit, boolean isFromRemove) {
        this.itemDetails = itemDetails;
        this.context = context;
        this.isLoading = false;
        this.isEdit = isEdit;
        this.iDataGrid = iDataGrid;
        this.isFromRemove = isFromRemove;
        db = new DatabaseHandler(context);
        this.input = db.getServiceOptioniput(itemDetails.getItemId());
        this.qnr = db.getServiceOptionQnr(itemDetails.getItemId());
        if (input != null && !input.trim().isEmpty()) {
            Gson gson = new Gson();
            this.answerLine = gson.fromJson(input, QuestionnairInpt.class);

        }
        if (qnr != null && !qnr.trim().isEmpty()) {
            Gson gson = new Gson();
            this.questionaire = gson.fromJson(qnr, Questionnaire.class);
        }
    }
    public CartItemServceOptnAdapter(CartItemModel itemDetails, Context context, IDataGrid iDataGrid, boolean isEdit, boolean isFromRemove, boolean fromSelectedItemsDialog) {
        this.itemDetails = itemDetails;
        this.context = context;
        this.isLoading = false;
        this.isEdit = isEdit;
        this.iDataGrid = iDataGrid;
        this.isFromRemove = isFromRemove;
        this.fromSelectedItemsDialog = fromSelectedItemsDialog;
        db = new DatabaseHandler(context);
        this.input = db.getServiceOptioniput(itemDetails.getItemId());
        this.qnr = db.getServiceOptionQnr(itemDetails.getItemId());
        if (input != null && !input.trim().isEmpty()) {
            Gson gson = new Gson();
            this.answerLine = gson.fromJson(input, QuestionnairInpt.class);

        }
        if (qnr != null && !qnr.trim().isEmpty()) {
            Gson gson = new Gson();
            this.questionaire = gson.fromJson(qnr, Questionnaire.class);
        }
    }
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        if (isLoading) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_line_shimmer, parent, false);
            return new ViewHolder(v, true);

        } else {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.cart_item_servc_optns, parent, false);
            return new ViewHolder(v, false);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {
        if (!isLoading) {
            float totalPrice = 0;
            ArrayList<AnswerLine> als = answerLine.getAnswerLines();
            String itemServcOptionName = "";
            StringJoiner joiner = new StringJoiner(",");
            for (AnswerLine al : als) {
                ArrayList<DataGrid> dataGridList = new ArrayList<>();
                dataGridList = al.getDataGridListList();
                dataGrid = dataGridList.get(position);
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
                if (dataGridList.size() == position + 1) {
                    viewHolder.llDivider.setVisibility(View.GONE);
                } else {
                    viewHolder.llDivider.setVisibility(View.VISIBLE);
                }
            }

            if (joiner.length() > 0) {
                itemServcOptionName = joiner.toString();
                viewHolder.tv_itemDetailsName.setText(itemServcOptionName);
                viewHolder.tv_itemDetailsName.setVisibility(View.VISIBLE);
            } else {
                viewHolder.tv_itemDetailsName.setVisibility(View.GONE);
            }

            if (isFromRemove) { // from remove dialog
                viewHolder.cv_itemSrvcOptnEdit.setVisibility(View.GONE);
                viewHolder.cv_itemSrvcOptnRemove.setVisibility(View.VISIBLE);
            } else {
                viewHolder.cv_itemSrvcOptnRemove.setVisibility(View.GONE);
                if (isEdit) {
                    viewHolder.cv_itemSrvcOptnEdit.setVisibility(View.VISIBLE);
                    viewHolder.cv_itemSrvcOptnEdit.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            iDataGrid.onEditClick(questionaire, answerLine, position, itemDetails, isEdit);

                        }
                    });
                } else {
                    viewHolder.cv_itemSrvcOptnEdit.setVisibility(View.GONE);
                }
            }
            if (totalPrice > 0) {
                String price = Config.getAmountNoOrTwoDecimalPoints(totalPrice);
                if(fromSelectedItemsDialog){
                    viewHolder.cv_itemSrvcOptnEdit.setVisibility(View.GONE);
                    viewHolder.tv_itemDetailsPrice.setVisibility(View.GONE);
                    viewHolder.cv_itemPrice.setVisibility(View.VISIBLE);
                    viewHolder.tv_itemPrice.setText("₹ " + price);
                } else {
                    viewHolder.tv_itemDetailsPrice.setVisibility(View.VISIBLE);
                    viewHolder.tv_itemDetailsPrice.setText("₹ " + price);
                }
            } else {
                viewHolder.tv_itemDetailsPrice.setVisibility(View.INVISIBLE);
            }

        } else {
            ViewHolder skeletonViewHolder = (ViewHolder) viewHolder;
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            skeletonViewHolder.itemView.setLayoutParams(params);
        }
        viewHolder.cv_itemSrvcOptnRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<AnswerLine> als = answerLine.getAnswerLines();
                for (int k = 0; k < als.size(); k++) {

                    AnswerLine al = als.get(k);
                    ArrayList<DataGrid> dataGridList = new ArrayList<>();
                    dataGridList = al.getDataGridListList();
                    dataGridList.remove(position);

                    JsonObject answer = new JsonObject();
                    Gson gson = new Gson();
                    JsonElement element = gson.toJsonTree(dataGridList);
                    answer.add("dataGridList", element);
                    answerLine.getAnswerLines().get(k).setAnswer(answer);
                }
                notifyDataSetChanged();
                iDataGrid.onRemoveClick(position, questionaire, answerLine, itemDetails);

            }
        });
    }

    @Override
    public int getItemCount() {
        ArrayList<DataGrid> dataGridList = new ArrayList<>();
        ArrayList<AnswerLine> als = answerLine.getAnswerLines();
        AnswerLine al = als.get(0);
        dataGridList = al.getDataGridListList();
        return isLoading ? 10 : dataGridList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView tv_itemDetailsName;
        private TextView tv_itemDetailsPrice, tv_itemPrice;
        private CardView cv_cart_item_srvc_option, cv_itemSrvcOptnEdit, cv_itemSrvcOptnRemove, cv_itemPrice;
        private LinearLayout llDivider;

        public ViewHolder(@NonNull View itemView, boolean isLoading) {

            super(itemView);

            if (!isLoading) {
                cv_cart_item_srvc_option = itemView.findViewById(R.id.cv_cart_item_srvc_option);
                tv_itemDetailsName = itemView.findViewById(R.id.tv_itemDetailsName);
                tv_itemDetailsPrice = itemView.findViewById(R.id.tv_itemDetailsPrice);
                cv_itemSrvcOptnEdit = itemView.findViewById(R.id.cv_itemSrvcOptnEdit);
                cv_itemSrvcOptnRemove = itemView.findViewById(R.id.cv_itemSrvcOptnRemove);
                tv_itemPrice= itemView.findViewById(R.id.tv_itemPrice);
                cv_itemPrice= itemView.findViewById(R.id.cv_itemPrice);

                llDivider = itemView.findViewById(R.id.ll_divider);
            }
        }
    }
}
