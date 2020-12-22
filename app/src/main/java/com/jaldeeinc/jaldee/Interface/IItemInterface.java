package com.jaldeeinc.jaldee.Interface;

import com.jaldeeinc.jaldee.response.CatalogItem;

public interface IItemInterface {


    void onItemClick(CatalogItem catalogItem);

    void checkItemQuantity();
}
