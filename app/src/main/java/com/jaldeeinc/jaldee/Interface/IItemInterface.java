package com.jaldeeinc.jaldee.Interface;

import com.jaldeeinc.jaldee.adapter.DetailPageItemsAdapter;
import com.jaldeeinc.jaldee.adapter.ItemsAdapter;
import com.jaldeeinc.jaldee.response.CatalogItem;

public interface IItemInterface {


    void onItemClick(CatalogItem catalogItem);

    void checkItemQuantity();
    void checkItemQuantity(CatalogItem itemDetails, ItemsAdapter.ViewHolder viewHolder);
    void checkItemQuantity(CatalogItem itemDetails, DetailPageItemsAdapter.ViewHolder viewHolder);


}
