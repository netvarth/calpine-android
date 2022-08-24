package com.jaldeeinc.jaldee.adapter;

import android.content.Context;
import android.os.Build;
import android.telecom.Conference;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.jaldeeinc.jaldee.Interface.IDataGrid;
import com.jaldeeinc.jaldee.R;
import com.jaldeeinc.jaldee.common.Config;
import com.jaldeeinc.jaldee.custom.QuestionnaireGridItemView;
import com.jaldeeinc.jaldee.model.DataGrid;
import com.jaldeeinc.jaldee.model.GridColumnAnswerLine;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.StringJoiner;
import java.util.stream.Collectors;

public class ServiceOptionDataGridAdapter extends RecyclerView.Adapter<ServiceOptionDataGridAdapter.ViewHolder> {
    ArrayList<DataGrid> gridList = new ArrayList<>();
    public Context context;
    public IDataGrid iDataGrid;
    public boolean isEdit;

    public ServiceOptionDataGridAdapter(ArrayList<DataGrid> gridList, Context context, IDataGrid iDataGrid, boolean isEdit) {
        this.gridList = gridList;
        this.context = context;
        this.iDataGrid = iDataGrid;
        this.isEdit = isEdit;
    }

    @NonNull
    @Override
    public ServiceOptionDataGridAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.service_option_grid_item, parent, false);
        return new ServiceOptionDataGridAdapter.ViewHolder(v, false);

    }

    @Override
    public void onBindViewHolder(@NonNull ServiceOptionDataGridAdapter.ViewHolder viewHolder, int position) {

        final DataGrid gridObj = gridList.get(position);
        ArrayList<GridColumnAnswerLine> datagridListColumns = gridObj.getDataGridListColumn();

        if (datagridListColumns.size() > 0) {
            String hint = "";
            StringJoiner joiner = new StringJoiner(",");
            float itemPrice = 0;
            for (int i = 0; i < datagridListColumns.size(); i++) {
                JsonArray ja = datagridListColumns.get(i).getColumn().getAsJsonArray("list");
                if (i == 0) {
                    if (ja.get(0).toString().trim() != null && !ja.get(0).toString().trim().isEmpty()) {
                        viewHolder.tv_itemName.setText(ja.get(0).getAsString());
                    } else {
                        viewHolder.tv_itemName.setVisibility(View.GONE);
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
                viewHolder.tv_item_Hint.setText(hint);
                viewHolder.tv_item_Hint.setVisibility(View.VISIBLE);
            } else {
                viewHolder.tv_item_Hint.setVisibility(View.GONE);
            }
            viewHolder.tv_item_price.setText("₹ " + Config.getAmountNoOrTwoDecimalPoints(itemPrice));
            String qty = (String) viewHolder.number_counter.getText();
            Integer qnty = Integer.parseInt(qty);
            if (qnty != null && qnty > 0) {
                viewHolder.tv_total_price.setText("₹ " + Config.getAmountNoOrTwoDecimalPoints(itemPrice * qnty));
            } else {
                viewHolder.tv_total_price.setText("₹ 0");
            }
        } else {
            viewHolder.tv_itemName.setVisibility(View.GONE);
            viewHolder.tv_item_Hint.setVisibility(View.GONE);
            viewHolder.tv_iteme_dit.setVisibility(View.GONE);
            viewHolder.tv_total_price.setVisibility(View.GONE);
        }
        if (gridList.size() == position + 1) {
            viewHolder.llDivider.setVisibility(View.GONE);
        } else {
            viewHolder.llDivider.setVisibility(View.VISIBLE);
        }
        if (isEdit) {

            viewHolder.tv_iteme_dit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    iDataGrid.onEditClick(gridObj, position);

                }
            });

            viewHolder.subtract_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    iDataGrid.onDeleteClick(position);

                }
            });
            viewHolder.add_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    iDataGrid.onAddClick(position);
                }
            });
        } else {
            viewHolder.ll_layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    iDataGrid.onEditClick(gridObj, position);

                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return gridList.size();
    }

    public void updateDataList(ArrayList<DataGrid> list) {

        if (list != null) {
            gridList = list;
            notifyDataSetChanged();
        }
    }

    public void updateData(DataGrid data) {

        gridList.add(data);
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public QuestionnaireGridItemView qGridItemView;
        public ImageView subtract_btn;
        public TextView tv_itemName, tv_item_price, tv_item_Hint, tv_iteme_dit, tv_total_price, number_counter;
        CardView add_btn;
        LinearLayout llDivider, number_button, ll_layout;

        public ViewHolder(@NonNull View itemView, boolean isLoading) {

            super(itemView);

            //qGridItemView = itemView.findViewById(R.id.q_gridItemView);
            // ivEdit = itemView.findViewById(R.id.iv_edit);
            //  ivDelete = itemView.findViewById(R.id.iv_delete);
            tv_itemName = itemView.findViewById(R.id.tv_itemName);
            tv_item_price = itemView.findViewById(R.id.tv_item_price);
            tv_item_Hint = itemView.findViewById(R.id.tv_item_Hint);
            tv_iteme_dit = itemView.findViewById(R.id.tv_iteme_dit);
            tv_total_price = itemView.findViewById(R.id.tv_total_price);
            llDivider = itemView.findViewById(R.id.ll_divider);
            subtract_btn = itemView.findViewById(R.id.subtract_btn);
            add_btn = itemView.findViewById(R.id.cv_add);
            number_counter = itemView.findViewById(R.id.number_counter);
            number_button = itemView.findViewById(R.id.number_button);
            ll_layout = itemView.findViewById(R.id.ll_layout);
            if (isEdit) {
                tv_iteme_dit.setVisibility(View.VISIBLE);
                number_button.setVisibility(View.VISIBLE);
                //ll_layout.setAlpha(1f);
            } else {
                tv_iteme_dit.setVisibility(View.GONE);
                number_button.setVisibility(View.GONE);
                //ll_layout.setAlpha(0.5f);
            }
        }
    }
}
