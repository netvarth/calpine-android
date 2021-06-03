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
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.jaldeeinc.jaldee.Interface.IClearFilter;
import com.jaldeeinc.jaldee.Interface.IFilterOptions;
import com.jaldeeinc.jaldee.R;
import com.jaldeeinc.jaldee.custom.CustomTextViewMedium;
import com.jaldeeinc.jaldee.custom.KeyValue;
import com.jaldeeinc.jaldee.model.FilterChips;

import java.util.ArrayList;

public class FilterChipsAdapter extends RecyclerView.Adapter<FilterChipsAdapter.ViewHolder> {

    private Context context;
    private boolean isLoading = false;
    private int lastPosition = -1;
    private IClearFilter iClearAppliedFilter;
    ArrayList<FilterChips> filterChipsList;
    String selectedSubDomain = "";


    public FilterChipsAdapter(Context context, ArrayList<FilterChips> filterChipsList, IClearFilter iClearAppliedFilter) {
        this.context = context;
        this.filterChipsList = filterChipsList;
        this.iClearAppliedFilter = iClearAppliedFilter;
    }


    @NonNull
    @Override
    public FilterChipsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        if (isLoading) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_line_shimmer, parent, false);
            return new FilterChipsAdapter.ViewHolder(v, true);

        } else {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.filter_chip, parent, false);
            return new FilterChipsAdapter.ViewHolder(v, false);

        }

    }

    @Override
    public void onBindViewHolder(@NonNull FilterChipsAdapter.ViewHolder viewHolder, int position) {

        if (!isLoading) {

            FilterChips filterChips = filterChipsList.get(position);

            viewHolder.tvFilter.setText(filterChips.getName());

            if (filterChips.isDeletable()) {

                viewHolder.ivClose.setVisibility(View.VISIBLE);
            } else {
                viewHolder.ivClose.setVisibility(View.GONE);
            }

            viewHolder.ivClose.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    iClearAppliedFilter.clearSelectedFilter(filterChips, position, filterChipsList.size()-1);
                    filterChipsList.remove(position);
                    notifyDataSetChanged();

                }
            });


        } else {

            FilterChipsAdapter.ViewHolder skeletonViewHolder = (FilterChipsAdapter.ViewHolder) viewHolder;
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            skeletonViewHolder.itemView.setLayoutParams(params);

        }
    }

    @Override
    public int getItemCount() {
        return isLoading ? 10 : filterChipsList.size();
    }

    public void setChipsList(Context mContext, ArrayList<FilterChips> fChipsList, IClearFilter iClearFilter) {

        context = mContext;
        iClearAppliedFilter = iClearFilter;
        fChipsList = fChipsList == null ? new ArrayList<>() : fChipsList;
        filterChipsList = fChipsList;
        notifyDataSetChanged();

    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private CustomTextViewMedium tvFilter;
        private ImageView ivClose;

        public ViewHolder(@NonNull View itemView, boolean isLoading) {

            super(itemView);

            if (!isLoading) {

                tvFilter = itemView.findViewById(R.id.tv_filter);
                ivClose = itemView.findViewById(R.id.iv_close);

            }
        }
    }


}

