package com.jaldeeinc.jaldee.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.jaldeeinc.jaldee.Interface.IDataGrid;
import com.jaldeeinc.jaldee.R;
import com.jaldeeinc.jaldee.custom.QuestionnaireGridItemView;
import com.jaldeeinc.jaldee.model.DataGrid;

import java.util.ArrayList;

public class DataGridAdapter extends RecyclerView.Adapter<DataGridAdapter.ViewHolder> {

    ArrayList<DataGrid> gridList = new ArrayList<>();
    public Context context;
    public IDataGrid iDataGrid;


    public DataGridAdapter(ArrayList<DataGrid> gridList, Context context, IDataGrid iDataGrid) {
        this.gridList = gridList;
        this.context = context;
        this.iDataGrid = iDataGrid;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.questionnaire_grid_item, parent, false);
        return new ViewHolder(v, false);

    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {

        final DataGrid gridObj = gridList.get(position);

        viewHolder.qGridItemView.setData(gridObj.getDataGridColumn());

        viewHolder.ivEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                iDataGrid.onEditClick(gridObj, position);

            }
        });

        viewHolder.ivDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                iDataGrid.onDeleteClick(position);

            }
        });

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
        public ImageView ivEdit, ivDelete;

        public ViewHolder(@NonNull View itemView, boolean isLoading) {

            super(itemView);

            qGridItemView = itemView.findViewById(R.id.q_gridItemView);
            ivEdit = itemView.findViewById(R.id.iv_edit);
            ivDelete = itemView.findViewById(R.id.iv_delete);


        }
    }

}
