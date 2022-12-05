package com.jaldeeinc.jaldee.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.jaldeeinc.jaldee.Interface.ISelectedPopularSearch;
import com.jaldeeinc.jaldee.R;
import com.jaldeeinc.jaldee.custom.CustomTextViewSemiBold;
import com.jaldeeinc.jaldee.model.SearchModel;

import java.util.ArrayList;

public class PopularSearchAdapter extends RecyclerView.Adapter<PopularSearchAdapter.ViewHolder> {

    public Context context;
    private boolean isLoading = true;
    private int lastPosition = -1;
    ArrayList<SearchModel> popularSearchList = new ArrayList<>();
    private ISelectedPopularSearch iSelectedPopularSearch;


    public PopularSearchAdapter(ArrayList<SearchModel> popularSearchList, Context context, boolean isLoading, ISelectedPopularSearch iSelectedPopularSearch) {
        this.context = context;
        this.isLoading = isLoading;
        this.popularSearchList = popularSearchList;
        this.iSelectedPopularSearch = iSelectedPopularSearch;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        if (isLoading) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_line_shimmer, parent, false);
            return new ViewHolder(v, true);

        } else {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.popular_search, parent, false);
            return new ViewHolder(v, false);

        }

    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {

        if (!isLoading) {

            final SearchModel popularSearch = popularSearchList.get(position);
            viewHolder.tvPopularSearch.setText(popularSearch.getDisplayname());

            viewHolder.cvPopularSearch.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    iSelectedPopularSearch.selectedPopularSearch(popularSearch);
                }
            });

        } else {

            ViewHolder skeletonViewHolder = (ViewHolder) viewHolder;
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            skeletonViewHolder.itemView.setLayoutParams(params);

        }
    }

    @Override
    public int getItemCount() {
        return isLoading ? 10 : popularSearchList.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {

        private CustomTextViewSemiBold tvPopularSearch;
        private CardView cvPopularSearch;

        public ViewHolder(@NonNull View itemView, boolean isLoading) {

            super(itemView);

            if (!isLoading) {

                tvPopularSearch = itemView.findViewById(R.id.tv_popularSearch);
                cvPopularSearch = itemView.findViewById(R.id.cv_popularSearch);

            }
        }
    }


}
