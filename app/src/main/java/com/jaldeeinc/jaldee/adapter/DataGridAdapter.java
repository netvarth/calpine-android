package com.jaldeeinc.jaldee.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.jaldeeinc.jaldee.Interface.IEditAddress;
import com.jaldeeinc.jaldee.R;
import com.jaldeeinc.jaldee.custom.CustomTextViewBold;
import com.jaldeeinc.jaldee.custom.CustomTextViewMedium;
import com.jaldeeinc.jaldee.custom.CustomTextViewSemiBold;
import com.jaldeeinc.jaldee.model.Address;
import com.jaldeeinc.jaldee.model.DataGridModel;

import java.util.ArrayList;

public class DataGridAdapter extends RecyclerView.Adapter<DataGridAdapter.ViewHolder> {

    ArrayList<DataGridModel> list = new ArrayList<>();
    public Context context;
    private boolean isLoading = true;


    public DataGridAdapter(ArrayList<DataGridModel> list, Context context) {
        this.list = list;
        this.context = context;

    }

    @NonNull
    @Override
    public DataGridAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.data_grid_item, parent, false);
        return new DataGridAdapter.ViewHolder(v, true);

    }

    @Override
    public void onBindViewHolder(@NonNull DataGridAdapter.ViewHolder viewHolder, int position) {

        if (!isLoading) {
            final DataGridModel model = list.get(position);

            viewHolder.tvText.setText(model.getText());


        } else {

            DataGridAdapter.ViewHolder skeletonViewHolder = (DataGridAdapter.ViewHolder) viewHolder;
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            skeletonViewHolder.itemView.setLayoutParams(params);

        }
    }

    @Override
    public int getItemCount() {
        return isLoading ? 10 : list.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {

        CustomTextViewBold tvText;

        public ViewHolder(@NonNull View itemView, boolean isLoading) {

            super(itemView);

            tvText = itemView.findViewById(R.id.tv_text);

        }
    }

}
