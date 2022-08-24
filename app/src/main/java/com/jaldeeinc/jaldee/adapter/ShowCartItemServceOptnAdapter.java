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

import com.google.gson.JsonArray;
import com.jaldeeinc.jaldee.R;
import com.jaldeeinc.jaldee.common.Config;
import com.jaldeeinc.jaldee.model.AnswerLine;
import com.jaldeeinc.jaldee.model.DataGrid;
import com.jaldeeinc.jaldee.model.GridColumnAnswerLine;
import com.jaldeeinc.jaldee.model.QuestionnairInpt;

import java.util.ArrayList;
import java.util.StringJoiner;

public class ShowCartItemServceOptnAdapter extends RecyclerView.Adapter<ShowCartItemServceOptnAdapter.ViewHolder> {
    QuestionnairInpt answerLine = new QuestionnairInpt();
    Context context;
    boolean isLoading;
    DataGrid dataGrid = new DataGrid();

    public ShowCartItemServceOptnAdapter(Context context, QuestionnairInpt answerLine) {
        this.context = context;
        this.answerLine = answerLine;
        this.isLoading = false;

    }

    @NonNull
    @Override
    public ShowCartItemServceOptnAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.cart_item_servc_optns, parent, false);
        return new ShowCartItemServceOptnAdapter.ViewHolder(v, false);
    }

    @Override
    public void onBindViewHolder(@NonNull ShowCartItemServceOptnAdapter.ViewHolder viewHolder, int position) {
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
            viewHolder.cv_itemSrvcOptnEdit.setVisibility(View.GONE);
            viewHolder.cv_itemSrvcOptnRemove.setVisibility(View.GONE);
            if (totalPrice > 0) {
                String price = Config.getAmountNoOrTwoDecimalPoints(totalPrice);
                viewHolder.cv_itemPrice.setVisibility(View.VISIBLE);
                viewHolder.tv_itemPrice.setText("₹ " + price);
                viewHolder.tv_itemDetailsPrice.setVisibility(View.GONE);
//                viewHolder.tv_itemDetailsPrice.setText("₹ " + price);

            } else {
                viewHolder.tv_itemDetailsPrice.setVisibility(View.INVISIBLE);
            }
        } else {
            ShowCartItemServceOptnAdapter.ViewHolder skeletonViewHolder = (ShowCartItemServceOptnAdapter.ViewHolder) viewHolder;
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            skeletonViewHolder.itemView.setLayoutParams(params);
        }
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
                tv_itemPrice = itemView.findViewById(R.id.tv_itemPrice);
                cv_itemPrice = itemView.findViewById(R.id.cv_itemPrice);

                llDivider = itemView.findViewById(R.id.ll_divider);
            }
        }
    }
}
