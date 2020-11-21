package com.jaldeeinc.jaldee.adapter;

import android.content.Context;
import android.text.Html;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.jaldeeinc.jaldee.Interface.ISelectedPopularSearch;
import com.jaldeeinc.jaldee.Interface.ISelectedProviderService;
import com.jaldeeinc.jaldee.R;
import com.jaldeeinc.jaldee.activities.Constants;
import com.jaldeeinc.jaldee.custom.AppointmentServiceDialog;
import com.jaldeeinc.jaldee.custom.CustomTextViewBold;
import com.jaldeeinc.jaldee.custom.CustomTextViewMedium;
import com.jaldeeinc.jaldee.custom.CustomTextViewSemiBold;
import com.jaldeeinc.jaldee.custom.DonationServiceDialog;
import com.jaldeeinc.jaldee.custom.PicassoTrustAll;
import com.jaldeeinc.jaldee.custom.ServiceInfoDialog;
import com.jaldeeinc.jaldee.model.SearchModel;
import com.jaldeeinc.jaldee.response.DepServiceInfo;
import com.pranavpandey.android.dynamic.toasts.DynamicToast;

import java.util.ArrayList;

public class PopularSearchAdapter extends RecyclerView.Adapter<PopularSearchAdapter.ViewHolder> {

    public Context context;
    private boolean isLoading = true;
    private int lastPosition = -1;
    ArrayList<SearchModel> popularSearchList = new ArrayList<>();
    private ISelectedPopularSearch iSelectedPopularSearch;


    public PopularSearchAdapter(ArrayList<SearchModel> popularSearchList, Context context, boolean isLoading,ISelectedPopularSearch iSelectedPopularSearch) {
        this.context = context;
        this.isLoading = isLoading;
        this.popularSearchList = popularSearchList;
        this.iSelectedPopularSearch = iSelectedPopularSearch;
    }


    @NonNull
    @Override
    public PopularSearchAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        if (isLoading) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_line_shimmer, parent, false);
            return new PopularSearchAdapter.ViewHolder(v, true);

        } else {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.popular_search, parent, false);
            return new PopularSearchAdapter.ViewHolder(v, false);

        }

    }

    @Override
    public void onBindViewHolder(@NonNull PopularSearchAdapter.ViewHolder viewHolder, int position) {

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

            PopularSearchAdapter.ViewHolder skeletonViewHolder = (PopularSearchAdapter.ViewHolder) viewHolder;
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
