package com.jaldeeinc.jaldee.custom;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.jaldeeinc.jaldee.Interface.ICartInterface;
import com.jaldeeinc.jaldee.Interface.IDialogInterface;
import com.jaldeeinc.jaldee.R;

import com.jaldeeinc.jaldee.adapter.SelectedItemsAdapter;
import com.jaldeeinc.jaldee.database.DatabaseHandler;
import com.jaldeeinc.jaldee.model.CartItemModel;
import com.jaldeeinc.jaldee.response.CatalogItem;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SelectedItemsDialog extends Dialog implements ICartInterface {

    @BindView(R.id.rv_items)
    RecyclerView rvItems;

    @BindView(R.id.tv_clearCart)
    CustomTextViewSemiBold tvClearCart;

    @BindView(R.id.tv_continue)
    CustomTextViewSemiBold tvContinue;

    private Context mContext;
    private int accountId;
    private CatalogItem itemDetails;
    private LinearLayoutManager linearLayoutManager;
    private SelectedItemsAdapter selectedItemsAdapter;
    private ICartInterface iCartInterface;
    private IDialogInterface iDialogInterface;
    DatabaseHandler db;
    ArrayList<CartItemModel> cartItemsList = new ArrayList<>();


    public SelectedItemsDialog(Context mContext, IDialogInterface iDialogInterface) {
        super(mContext);
        this.mContext = mContext;
        this.iDialogInterface = iDialogInterface;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.selected_items);
        ButterKnife.bind(this);
        iCartInterface = (ICartInterface) this;
        db = new DatabaseHandler(mContext);
        cartItemsList = db.getCartItems();

        if (cartItemsList != null && cartItemsList.size() > 0) {

            linearLayoutManager = new LinearLayoutManager(mContext);
            rvItems.setLayoutManager(linearLayoutManager);
            selectedItemsAdapter = new SelectedItemsAdapter(cartItemsList, mContext, false, iCartInterface,false);
            rvItems.setAdapter(selectedItemsAdapter);

        }

        tvContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                iDialogInterface.onContinueClick();
                dismiss();
            }
        });

        tvClearCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                db.DeleteCart();
                iDialogInterface.onClearClick();
                dismiss();
            }
        });

    }


    public void onRefresh() {

        db = new DatabaseHandler(mContext);
        cartItemsList.clear();
        cartItemsList = db.getCartItems();

        if (cartItemsList != null && cartItemsList.size() > 0) {

            linearLayoutManager = new LinearLayoutManager(mContext);
            rvItems.setLayoutManager(linearLayoutManager);
            selectedItemsAdapter = new SelectedItemsAdapter(cartItemsList, mContext, false, iCartInterface,false);
            rvItems.setAdapter(selectedItemsAdapter);

        }
    }


    @Override
    public void checkCartCount() {
        if (db.getCartCount() <= 0) {
            iDialogInterface.onClearClick();
            dismiss();
        } else {
            onRefresh();
        }
    }

    @Override
    public void openNotes(int itemId, String instruction) {

    }
}
