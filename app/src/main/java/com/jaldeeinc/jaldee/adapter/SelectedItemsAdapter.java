package com.jaldeeinc.jaldee.adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.os.Vibrator;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.jaldeeinc.jaldee.Interface.ICartInterface;
import com.jaldeeinc.jaldee.Interface.IImageInterface;
import com.jaldeeinc.jaldee.Interface.ISaveNotes;
import com.jaldeeinc.jaldee.Interface.ISendMessage;
import com.jaldeeinc.jaldee.R;
import com.jaldeeinc.jaldee.custom.AddressDialog;
import com.jaldeeinc.jaldee.custom.BorderImageView;
import com.jaldeeinc.jaldee.custom.CustomNotes;
import com.jaldeeinc.jaldee.custom.CustomTextViewItalicSemiBold;
import com.jaldeeinc.jaldee.custom.CustomTextViewSemiBold;
import com.jaldeeinc.jaldee.custom.ElegantNumberButton;
import com.jaldeeinc.jaldee.custom.PicassoTrustAll;
import com.jaldeeinc.jaldee.database.DatabaseHandler;
import com.jaldeeinc.jaldee.model.CartItemModel;
import com.jaldeeinc.jaldee.response.ItemImages;
import com.omjoonkim.skeletonloadingview.SkeletonLoadingView;
import com.squareup.picasso.Callback;

import java.text.DecimalFormat;
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

    public SelectedItemsAdapter(ArrayList<CartItemModel> cartItemsList, Context context, boolean isLoading, ICartInterface iCartInterface,boolean isAddNote) {
        this.cartItemsList = cartItemsList;
        this.context = context;
        this.isLoading = isLoading;
        this.iCartInterface = iCartInterface;
        this.isAddNote = isAddNote;
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

            setAnimation(viewHolder.llLayout, position);

            viewHolder.tvItemName.setText(cartItem.getItemName());

            if (isAddNote){
                viewHolder.llAddNote.setVisibility(View.VISIBLE);
                if (cartItem.getInstruction() != null && !cartItem.getInstruction().trim().equalsIgnoreCase("")){
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
                viewHolder.tvPrice.setText("₹" + convertAmountToDecimals(String.valueOf(amount)));
                viewHolder.tvPrice.setPaintFlags(viewHolder.tvPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                double discountAmount = cartItem.getDiscountedPrice() * cartItem.getQuantity();
                viewHolder.tvDiscountedPrice.setText("₹"+ convertAmountToDecimals(String.valueOf(discountAmount)));

            } else {
                viewHolder.tvPrice.setVisibility(View.VISIBLE);
                double amount = cartItem.getItemPrice() * cartItem.getQuantity();
                viewHolder.tvPrice.setText("₹" +convertAmountToDecimals(String.valueOf(amount)) );
                viewHolder.tvDiscountedPrice.setVisibility(View.GONE);

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

                        if (newValue == cartItem.getMaxQuantity()){

                            // give fadeout color for plus
                            cvPlus.setBackgroundResource(R.drawable.disabled_plus);
                            cvPlus.setClickable(false);
                        }

                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                //Do something after 100ms

                                db.addQuantity(cartItem.getItemId(), newValue);
                                progressBar.setVisibility(View.GONE);
                                iCartInterface.checkCartCount();

                                if (cartItem.getIsPromotional() == 1) {

                                    viewHolder.tvPrice.setVisibility(View.VISIBLE);
                                    viewHolder.tvDiscountedPrice.setVisibility(View.VISIBLE);
                                    double amount = cartItem.getItemPrice() * newValue;
                                    viewHolder.tvPrice.setText("₹" + convertAmountToDecimals(String.valueOf(amount)));
                                    viewHolder.tvPrice.setPaintFlags(viewHolder.tvPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                                    double discountPrice = cartItem.getDiscountedPrice() * newValue;
                                    viewHolder.tvDiscountedPrice.setText("₹"+ convertAmountToDecimals(String.valueOf(discountPrice)));

                                } else {
                                    viewHolder.tvPrice.setVisibility(View.VISIBLE);
                                    double amount = cartItem.getItemPrice() * newValue;
                                    viewHolder.tvPrice.setText("₹" + convertAmountToDecimals(String.valueOf(amount)));
                                    viewHolder.tvDiscountedPrice.setVisibility(View.GONE);

                                }

                            }
                        }, 500);
                        // plus and minus icons are disabled based on minQuantity and new value

                    } else {

                        // give fadeout color for plus
                        cvPlus.setBackgroundResource(R.drawable.disabled_plus);
                        cvPlus.setClickable(false);
                        iCartInterface.checkCartCount();

                    }
                }
            });

            viewHolder.numberButton.setNumber(String.valueOf(cartItem.getQuantity()));

            if (cartItem.getMaxQuantity() == Integer.parseInt(viewHolder.numberButton.getNumber())){
                cvPlus = viewHolder.numberButton.findViewById(R.id.cv_add);
                cvPlus.setBackgroundResource(R.drawable.disabled_plus);
                cvPlus.setClickable(false);
            }

            viewHolder.llAddNote.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    iCartInterface.openNotes(cartItem.getItemId(),cartItem.getInstruction());



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
        private CustomTextViewItalicSemiBold tvPrice, tvDiscountedPrice;
        private ElegantNumberButton numberButton;
        private LinearLayout llLayout;
        private LinearLayout llAddNote;
        private CustomTextViewItalicSemiBold tvNote;

        public ViewHolder(@NonNull View itemView, boolean isLoading) {

            super(itemView);

            if (!isLoading) {
                tvItemName = itemView.findViewById(R.id.tv_itemName);
                tvPrice = itemView.findViewById(R.id.tv_price);
                tvDiscountedPrice = itemView.findViewById(R.id.tv_discountedPrice);
                numberButton = itemView.findViewById(R.id.number_button);
                llLayout = itemView.findViewById(R.id.ll_layout);
                llAddNote = itemView.findViewById(R.id.ll_note);
                tvNote = itemView.findViewById(R.id.tv_note);
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

    public static String convertAmountToDecimals(String price) {

        double a = Double.parseDouble(price);
        DecimalFormat decim = new DecimalFormat("0.00");
        Double price2 = Double.parseDouble(decim.format(a));
        String amount = decim.format(price2);
        return amount;

    }
}
