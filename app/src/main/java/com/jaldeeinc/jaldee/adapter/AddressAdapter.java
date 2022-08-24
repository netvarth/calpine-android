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
import com.jaldeeinc.jaldee.custom.CustomTextViewMedium;
import com.jaldeeinc.jaldee.custom.CustomTextViewSemiBold;
import com.jaldeeinc.jaldee.model.Address;

import java.util.ArrayList;

public class AddressAdapter extends RecyclerView.Adapter<AddressAdapter.ViewHolder> {

    ArrayList<Address> addressList = new ArrayList<>();
    public Context context;
    private boolean isLoading = true;
    private int lastPosition = -1;
    private int selectedPosition = 0;
    private IEditAddress iEditAddress;

    public AddressAdapter(ArrayList<Address> addressList, Context context, boolean isLoading, IEditAddress iEditAddress) {
        this.addressList = addressList;
        this.context = context;
        this.isLoading = isLoading;
        this.iEditAddress = iEditAddress;
           }

    @NonNull
    @Override
    public AddressAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        if (isLoading) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_line_shimmer, parent, false);
            return new AddressAdapter.ViewHolder(v, true);

        } else {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.address, parent, false);
            return new AddressAdapter.ViewHolder(v, false);

        }

    }

    @Override
    public void onBindViewHolder(@NonNull AddressAdapter.ViewHolder viewHolder, int position) {

        if (!isLoading) {
            final Address address = addressList.get(position);

            setAnimation(viewHolder.llLayout, position);

            int number = position +1;
            viewHolder.tvHeader.setText("Address "+ number);

            viewHolder.tvName.setText(address.getFirstName()+" "+ address.getLastName());

            String fullAddress = addressList.get(position).getLandMark()+","+ addressList.get(position).getAddress()+","+ addressList.get(position).getCity()+ ","+ addressList.get(position).getPostalCode();

            viewHolder.tvAddress.setText(fullAddress);

            viewHolder.tvMobileNumber.setText(address.getCountryCode() +" "+ address.getPhoneNumber());

            viewHolder.tvEmail.setText(address.getEmail());

            viewHolder.ivEdit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    iEditAddress.onEditClick(address,position);
                }
            });

            viewHolder.llLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    iEditAddress.onAddressClick(address);

                }
            });

            viewHolder.ivDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    iEditAddress.onDeleteClick(address,position);
                }
            });




        } else {

            AddressAdapter.ViewHolder skeletonViewHolder = (AddressAdapter.ViewHolder) viewHolder;
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            skeletonViewHolder.itemView.setLayoutParams(params);

        }
    }

    @Override
    public int getItemCount() {
        return isLoading ? 10 : addressList.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {

       private CustomTextViewSemiBold tvHeader;
       private CustomTextViewMedium tvName,tvAddress,tvMobileNumber,tvEmail;
       private LinearLayout llLayout;
       private ImageView ivEdit,ivDelete;

        public ViewHolder(@NonNull View itemView, boolean isLoading) {

            super(itemView);

            if (!isLoading) {

                tvHeader = itemView.findViewById(R.id.tv_header);
                tvName = itemView.findViewById(R.id.tv_name);
                tvAddress = itemView.findViewById(R.id.tv_address);
                tvMobileNumber = itemView.findViewById(R.id.tv_number);
                tvEmail = itemView.findViewById(R.id.tv_mailId);
                llLayout = itemView.findViewById(R.id.ll_layout);
                ivEdit = itemView.findViewById(R.id.iv_edit);
                ivDelete = itemView.findViewById(R.id.iv_delete);

            }
        }
    }

    private void setAnimation(View viewToAnimate, int position) {
        // If the bound view wasn't previously displayed on screen, it's animated
        if (position > lastPosition) {
            Animation animation = AnimationUtils.loadAnimation(context, R.anim.bounce);
            viewToAnimate.startAnimation(animation);
            lastPosition = position;
        }
    }
}
