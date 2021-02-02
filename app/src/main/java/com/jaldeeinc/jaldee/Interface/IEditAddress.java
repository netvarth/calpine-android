package com.jaldeeinc.jaldee.Interface;

import com.jaldeeinc.jaldee.model.Address;

public interface IEditAddress {

    void onAddressClick(Address address);

    void onEditClick(Address address, int position);

    void onDeleteClick(Address address, int position);
}
