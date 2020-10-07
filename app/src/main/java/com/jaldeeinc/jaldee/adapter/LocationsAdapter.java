package com.jaldeeinc.jaldee.adapter;

import android.content.Context;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.jaldeeinc.jaldee.Interface.ISelectLocation;
import com.jaldeeinc.jaldee.R;
import com.jaldeeinc.jaldee.activities.Constants;
import com.jaldeeinc.jaldee.custom.CustomTextViewBold;
import com.jaldeeinc.jaldee.custom.CustomTextViewMedium;
import com.jaldeeinc.jaldee.custom.CustomTextViewSemiBold;
import com.jaldeeinc.jaldee.custom.PicassoTrustAll;
import com.jaldeeinc.jaldee.response.DepServiceInfo;
import com.jaldeeinc.jaldee.response.SearchLocation;

import java.util.ArrayList;

import static com.jaldeeinc.jaldee.common.MyApplication.getContext;

public class LocationsAdapter extends RecyclerView.Adapter<LocationsAdapter.ViewHolder> {

    ArrayList<SearchLocation> locationList;
    public Context context;
    int row_index = -1;
    private ISelectLocation iSelectLocation;

    public LocationsAdapter(ArrayList<SearchLocation> locationList, Context context, ISelectLocation iSelectLocation) {
        this.locationList = locationList;
        this.context = context;
        this.iSelectLocation = iSelectLocation;
    }

    @NonNull
    @Override
    public LocationsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.locations_item, viewGroup, false);
        return new LocationsAdapter.ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final LocationsAdapter.ViewHolder viewHolder, final int position) {

        if (locationList != null) {
            viewHolder.tvLocation.setText(locationList.get(position).getAddress());

            viewHolder.cvLocation.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    viewHolder.cvLocation.setBackgroundResource(R.drawable.rounded_popularsearch_green);

                    row_index = position;
                    notifyDataSetChanged();

                        iSelectLocation.sendSelectedAddress(locationList.get(position).getAddress(),locationList.get(position).getId(),locationList.get(position).getPlace());

                }
            });

            if (row_index == position) {
                viewHolder.cvLocation.setBackgroundResource(R.color.location_theme);
                viewHolder.tvLocation.setTextColor(ContextCompat.getColor(context, R.color.white));
                viewHolder.ivLocationIcon.setColorFilter(ContextCompat.getColor(context, R.color.white));

            } else {
                viewHolder.cvLocation.setBackgroundResource(R.color.white);
                viewHolder.tvLocation.setTextColor(ContextCompat.getColor(context, R.color.title_color));
                viewHolder.ivLocationIcon.setColorFilter(ContextCompat.getColor(context, R.color.location_theme));

            }
        }
    }

    @Override
    public int getItemCount() {

        return locationList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private CustomTextViewSemiBold tvLocation;
        private CardView cvLocation;
        private ImageView ivLocationIcon;

        public ViewHolder(@NonNull View itemView) {

            super(itemView);

            tvLocation = itemView.findViewById(R.id.tv_location);
            cvLocation = itemView.findViewById(R.id.cv_location);
            ivLocationIcon = itemView.findViewById(R.id.iv_locationIcon);


        }
    }

}
