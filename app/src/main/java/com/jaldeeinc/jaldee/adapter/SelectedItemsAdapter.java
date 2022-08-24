package com.jaldeeinc.jaldee.adapter;

import android.content.Context;
import android.graphics.Paint;
import android.os.Handler;
import android.os.Vibrator;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.jaldeeinc.jaldee.Interface.ICartInterface;
import com.jaldeeinc.jaldee.Interface.IDataGrid;
import com.jaldeeinc.jaldee.Interface.ISaveNotes;
import com.jaldeeinc.jaldee.R;
import com.jaldeeinc.jaldee.common.Config;
import com.jaldeeinc.jaldee.custom.CustomTextViewBoldItalic;
import com.jaldeeinc.jaldee.custom.CustomTextViewLight;
import com.jaldeeinc.jaldee.custom.CustomTextViewSemiBold;
import com.jaldeeinc.jaldee.custom.ElegantNumberButton;
import com.jaldeeinc.jaldee.database.DatabaseHandler;
import com.jaldeeinc.jaldee.model.CartItemModel;

import java.util.ArrayList;

public class SelectedItemsAdapter extends RecyclerView.Adapter<SelectedItemsAdapter.ViewHolder> {

    ArrayList<CartItemModel> cartItemsList = new ArrayList<>();
    public Context context;
    private boolean isLoading = true;
    private int lastPosition = -1;
    private int selectedPosition = 0;
    private ICartInterface iCartInterface;
    Vibrator vibe;
    private DatabaseHandler db;
    ProgressBar progressBar;
    private CardView cvPlus;
    private boolean isAddNote = false;
    private ISaveNotes iSaveNotes;
    private boolean isRv_itemDetailsShowing = false;

    private LinearLayoutManager linearLayoutManager;
    IDataGrid iDataGrid;
    boolean fromSelectedItemsDialog;

    public SelectedItemsAdapter(ArrayList<CartItemModel> cartItemsList, Context context, boolean isLoading, ICartInterface iCartInterface, boolean isAddNote, IDataGrid iDataGrid, boolean fromSelectedItemsDialog) {
        this.cartItemsList = cartItemsList;
        this.context = context;
        this.isLoading = isLoading;
        this.iCartInterface = iCartInterface;
        this.isAddNote = isAddNote;
        this.iDataGrid = iDataGrid;
        this.fromSelectedItemsDialog = fromSelectedItemsDialog;
        db = new DatabaseHandler(context);
        vibe = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
    }

    @NonNull
    @Override
    public SelectedItemsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        if (isLoading) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_line_shimmer, parent, false);
            return new SelectedItemsAdapter.ViewHolder(v, true);

        } else {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.cart_item, parent, false);
            return new SelectedItemsAdapter.ViewHolder(v, false);

        }

    }

    @Override
    public void onBindViewHolder(@NonNull SelectedItemsAdapter.ViewHolder viewHolder, int position) {

        if (!isLoading) {
            final CartItemModel cartItem = cartItemsList.get(position);

            setAnimation(viewHolder.cvLayout, position);
            if(fromSelectedItemsDialog){
                viewHolder.numberButton.setVisibilityOfAddRemoveButtons(false);
            }
            viewHolder.tvItemName.setText(cartItem.getItemName());

            if (isAddNote) {
                //viewHolder.llAddNote.setVisibility(View.VISIBLE);
                viewHolder.llAddNote.setVisibility(View.GONE); //temprarly set as visbility gone
                if (cartItem.getInstruction() != null && !cartItem.getInstruction().trim().equalsIgnoreCase("")) {
                    viewHolder.tvNote.setText("Edit note");
                } else {
                    viewHolder.tvNote.setText("Add note");
                }
            } else {
                viewHolder.llAddNote.setVisibility(View.GONE);
            }

                if (cartItem.getIsPromotional() == 1) {

                viewHolder.tvPrice.setVisibility(View.VISIBLE);
                viewHolder.tvDiscountedPrice.setVisibility(View.VISIBLE);
                double amount = cartItem.getItemPrice() * cartItem.getQuantity();
                viewHolder.tvPrice.setText("₹ " + Config.getAmountNoOrTwoDecimalPoints(amount));
                viewHolder.tvPrice.setPaintFlags(viewHolder.tvPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                double discountAmount = cartItem.getDiscountedPrice() * cartItem.getQuantity();
                viewHolder.tvDiscountedPrice.setText("₹ " + Config.getAmountNoOrTwoDecimalPoints(discountAmount));

            } else {
                viewHolder.tvDiscountedPrice.setVisibility(View.VISIBLE);
                double amount = cartItem.getItemPrice() * cartItem.getQuantity();
                viewHolder.tvDiscountedPrice.setText("₹ " + Config.getAmountNoOrTwoDecimalPoints(amount));
                viewHolder.tvPrice.setVisibility(View.GONE);

            }
            boolean isAddedServiceOption = db.isAddedServiceOption(cartItem.getItemId());
                if (isAddedServiceOption) {
                viewHolder.cv_itemDetails.setVisibility(View.VISIBLE);
                linearLayoutManager = new LinearLayoutManager(context);
                viewHolder.rv_itemDetails.setLayoutManager(linearLayoutManager);
                CartItemServceOptnAdapter cartItemServceOptnAdapter = new CartItemServceOptnAdapter(cartItem, context, iDataGrid, true, false, fromSelectedItemsDialog);
                viewHolder.rv_itemDetails.setAdapter(cartItemServceOptnAdapter);
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
            } else {
                viewHolder.cv_itemDetails.setVisibility(View.GONE);
            }
            viewHolder.numberButton.setOnValueChangeListener(new ElegantNumberButton.OnValueChangeListener() {
                @Override
                public void onValueChange(ElegantNumberButton view, int oldValue, int newValue) {

                    cvPlus = view.findViewById(R.id.cv_add);

                    if (newValue < 1) {

                        db.addQuantity(cartItem.getItemId(), 0);
                        viewHolder.numberButton.setVisibility(View.GONE);

                        iCartInterface.checkCartCount();

                    } else if (newValue <= cartItem.getMaxQuantity()) {

                        // fadein cardview here
                        cvPlus.setBackgroundResource(R.drawable.plus_background);
                        cvPlus.setClickable(true);
                        progressBar = view.findViewById(R.id.myProgress);
                        progressBar.setVisibility(View.VISIBLE);
                        vibe.vibrate(100);

                        if (newValue == cartItem.getMaxQuantity()) {

                            // give fadeout color for plus
                            cvPlus.setBackgroundResource(R.drawable.disabled_plus);
                            cvPlus.setClickable(false);
                        }
                        boolean isAddedServiceOption = db.isAddedServiceOption(cartItem.getItemId());
                        if (isAddedServiceOption) {
                            progressBar.setVisibility(View.GONE);
                            boolean isDecreaseQty = false;
                            if(newValue < oldValue){
                                isDecreaseQty = true;
                            } else if(newValue > oldValue) {
                                isDecreaseQty = false;
                            }
                            iDataGrid.onAddClick(cartItem , viewHolder, isDecreaseQty, newValue);
                        } else {
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    //Do something after 100ms

                                    db.addQuantity(cartItem.getItemId(), newValue);
                                    iCartInterface.checkCartCount();
                                    progressBar.setVisibility(View.GONE);


                                    if (cartItem.getIsPromotional() == 1) {

                                        viewHolder.tvPrice.setVisibility(View.VISIBLE);
                                        viewHolder.tvDiscountedPrice.setVisibility(View.VISIBLE);
                                        double amount = cartItem.getItemPrice() * newValue;
                                        viewHolder.tvPrice.setText("₹ " + Config.getAmountNoOrTwoDecimalPoints(amount));
                                        viewHolder.tvPrice.setPaintFlags(viewHolder.tvPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                                        double discountPrice = cartItem.getDiscountedPrice() * newValue;
                                        viewHolder.tvDiscountedPrice.setText("₹ " + Config.getAmountNoOrTwoDecimalPoints(discountPrice));

                                    } else {
                                        viewHolder.tvPrice.setVisibility(View.VISIBLE);
                                        double amount = cartItem.getItemPrice() * newValue;
                                        viewHolder.tvPrice.setText("₹ " + Config.getAmountNoOrTwoDecimalPoints(amount));
                                        viewHolder.tvDiscountedPrice.setVisibility(View.GONE);

                                    }

                                }
                            }, 500);
                            // plus and minus icons are disabled based on minQuantity and new value
                        }

                    } else {

                        // give fadeout color for plus
                        cvPlus.setBackgroundResource(R.drawable.disabled_plus);
                        cvPlus.setClickable(false);
                        iCartInterface.checkCartCount();

                    }
                }
            });

            viewHolder.numberButton.setNumber(String.valueOf(cartItem.getQuantity()));

            if (cartItem.getMaxQuantity() == Integer.parseInt(viewHolder.numberButton.getNumber())) {
                cvPlus = viewHolder.numberButton.findViewById(R.id.cv_add);
                cvPlus.setBackgroundResource(R.drawable.disabled_plus);
                cvPlus.setClickable(false);
            }

            viewHolder.llAddNote.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    iCartInterface.openNotes(cartItem.getItemId(), cartItem.getInstruction());


                }
            });

        } else {

            SelectedItemsAdapter.ViewHolder skeletonViewHolder = (SelectedItemsAdapter.ViewHolder) viewHolder;
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            skeletonViewHolder.itemView.setLayoutParams(params);

        }
    }

    @Override
    public int getItemCount() {
        return isLoading ? 10 : cartItemsList.size();
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

    private void setAnimation(View viewToAnimate, int position) {
        // If the bound view wasn't previously displayed on screen, it's animated
        if (position > lastPosition) {
            Animation animation = AnimationUtils.loadAnimation(context, R.anim.bounce);
            viewToAnimate.startAnimation(animation);
            lastPosition = position;
        }
    }
}
