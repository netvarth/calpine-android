package com.jaldeeinc.jaldee.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.jaldeeinc.jaldee.R;
import com.jaldeeinc.jaldee.common.Config;
import com.jaldeeinc.jaldee.custom.CustomTextViewBoldItalic;
import com.jaldeeinc.jaldee.custom.CustomTextViewLight;
import com.jaldeeinc.jaldee.custom.CustomTextViewSemiBold;
import com.jaldeeinc.jaldee.custom.ElegantNumberButton;
import com.jaldeeinc.jaldee.model.QuestionnairInpt;
import com.jaldeeinc.jaldee.response.ActiveOrders;
import com.jaldeeinc.jaldee.response.ItemDetails;

import java.util.ArrayList;

public class ShowCartServiceOptionItemsAdapter extends RecyclerView.Adapter<ShowCartServiceOptionItemsAdapter.ViewHolder> {
    Context context;
    ActiveOrders activeOrders;
    ArrayList<ItemDetails> itemsList;
    private boolean isRv_itemDetailsShowing = false;
    private LinearLayoutManager linearLayoutManager;

    public ShowCartServiceOptionItemsAdapter(Context context, ActiveOrders activeOrders) {
        this.context = context;
        this.activeOrders = activeOrders;
        itemsList = activeOrders.getItemsList();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.cart_item, parent, false);
        return new ViewHolder(v, false);


    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {
        final ItemDetails itemDetail = itemsList.get(position);
        viewHolder.numberButton.setVisibilityOfAddRemoveButtons(false);
        viewHolder.numberButton.setNumber(String.valueOf(itemDetail.getQuantity()));

        viewHolder.tvItemName.setText(itemDetail.getItemName());

        if (itemDetail.getTotalPrice() != null && !itemDetail.getTotalPrice().trim().isEmpty()) {

            viewHolder.tvDiscountedPrice.setVisibility(View.VISIBLE);
            double amount = Double.parseDouble(itemDetail.getTotalPrice());
            viewHolder.tvDiscountedPrice.setText("₹ " + Config.getAmountNoOrTwoDecimalPoints(amount));
            viewHolder.tvPrice.setVisibility(View.GONE);
        }
        if(itemDetail.getSrvAnswers() != null){
            QuestionnairInpt answerLine;
            JsonObject jsonObject = itemDetail.getSrvAnswers();
            Gson gson = new Gson();
            answerLine = gson.fromJson(jsonObject, QuestionnairInpt.class);
            if(answerLine.getAnswerLines().size() > 0){
                viewHolder.cv_itemDetails.setVisibility(View.VISIBLE);
                linearLayoutManager = new LinearLayoutManager(context);
                viewHolder.rv_itemDetails.setLayoutManager(linearLayoutManager);
                ShowCartItemServceOptnAdapter showCartItemServceOptnAdapter = new ShowCartItemServceOptnAdapter(context, answerLine);
                viewHolder.rv_itemDetails.setAdapter(showCartItemServceOptnAdapter);

                viewHolder.cv_itemDetails.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (isRv_itemDetailsShowing) {
                            isRv_itemDetailsShowing = false;
                            viewHolder.rv_itemDetails.setVisibility(View.GONE);

                        } else {
                            isRv_itemDetailsShowing = true;
                            viewHolder.rv_itemDetails.setVisibility(View.VISIBLE);

                        }
                    }
                });
            }
        }
    }

    @Override
    public int getItemCount() {
        return itemsList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private CustomTextViewSemiBold tvItemName;
        private CustomTextViewLight tvPrice;
        private CustomTextViewBoldItalic tvDiscountedPrice;
        public ElegantNumberButton numberButton;
        private CardView cvLayout;
        private LinearLayout llAddNote;
        private CustomTextViewBoldItalic tvNote;
        private RecyclerView rv_itemDetails;
        private CardView cv_itemDetails;

        public ViewHolder(@NonNull View itemView, boolean isLoading) {

            super(itemView);

            if (!isLoading) {
                tvItemName = itemView.findViewById(R.id.tv_itemName);
                tvPrice = itemView.findViewById(R.id.tv_price);
                tvDiscountedPrice = itemView.findViewById(R.id.tv_discountedPrice);
                numberButton = itemView.findViewById(R.id.number_button);
                cvLayout = itemView.findViewById(R.id.cv_layout);
                llAddNote = itemView.findViewById(R.id.ll_note);
                tvNote = itemView.findViewById(R.id.tv_note);
                rv_itemDetails = itemView.findViewById(R.id.rv_itemDetails);
                cv_itemDetails = itemView.findViewById(R.id.cv_itemDetails);

            }
        }
    }
}
