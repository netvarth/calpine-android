package com.jaldeeinc.jaldee.response;

import java.util.ArrayList;

/**
 * Created by sharmila on 8/8/18.
 */

public class PaymentModel {
    ArrayList<PayModes> payModes;
    ArrayList<String> payGateways;

    public ArrayList<PayModes> getPayModes() {
        return payModes;
    }

    public ArrayList<String> getPayGateways() {
        return payGateways;
    }
}
