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

import com.jaldeeinc.jaldee.Interface.IFilterOptions;
import com.jaldeeinc.jaldee.R;
import com.jaldeeinc.jaldee.custom.CustomTextViewMedium;
import com.jaldeeinc.jaldee.custom.KeyValue;

import java.util.ArrayList;

public class SubDomainAdapter extends RecyclerView.Adapter<SubDomainAdapter.ViewHolder> {

    private Context context;
    private boolean isLoading = false;
    private int lastPosition = -1;
    private IFilterOptions iFilterOptions;
    private ArrayList<KeyValue> list;
    String selectedSubDomain = "";


    public SubDomainAdapter(Context context, ArrayList<KeyValue> list) {
        this.context = context;
        this.list = list;
    }

    public SubDomainAdapter(Context context, ArrayList<KeyValue> list, String subDomain) {
        this.context = context;
        this.list = list;
        this.selectedSubDomain = subDomain;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        if (isLoading) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_line_shimmer, parent, false);
            return new ViewHolder(v, true);

        } else {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.domain_layout, parent, false);
            return new ViewHolder(v, false);

        }

    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {

        if (!isLoading) {

            KeyValue keyValue = list.get(position);

            setAnimation(viewHolder.cvDomain, position);

            viewHolder.tvDomainName.setText(keyValue.getKey());

            if (keyValue.getValue().equalsIgnoreCase(selectedSubDomain)) {

                viewHolder.cvDomain.setBackgroundResource(R.drawable.selected_lightcolor);
                viewHolder.ivIcon.setVisibility(View.VISIBLE);
            } else {
                viewHolder.cvDomain.setBackgroundResource(R.drawable.selected_whitecolor);
                viewHolder.ivIcon.setVisibility(View.GONE);
            }


        } else {

            ViewHolder skeletonViewHolder = (ViewHolder) viewHolder;
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            skeletonViewHolder.itemView.setLayoutParams(params);

        }
    }

    @Override
    public int getItemCount() {
        return isLoading ? 10 : list.size();
    }

    public void setSelectedSubDomain(String subDomain) {

        selectedSubDomain = subDomain;
        notifyDataSetChanged();
    }

    public KeyValue getSelectedSubDomain(int position) {

        return list.get(position);
    }


    public class ViewHolder extends RecyclerView.ViewHolder {

        private CustomTextViewMedium tvDomainName;
        private CardView cvDomain;
        private ImageView ivIcon;

        public ViewHolder(@NonNull View itemView, boolean isLoading) {

            super(itemView);

            if (!isLoading) {

                tvDomainName = itemView.findViewById(R.id.tv_domainName);
                cvDomain = itemView.findViewById(R.id.cv_domain);
                ivIcon = itemView.findViewById(R.id.iv_icon);

            }
        }
    }

    private void setAnimation(View viewToAnimate, int position) {
        // If the bound view wasn't previously displayed on screen, it's animated
        if (position > lastPosition) {
            Animation animation = AnimationUtils.loadAnimation(context, android.R.anim.slide_in_left);
            viewToAnimate.startAnimation(animation);
            lastPosition = position;
        }
    }


}

