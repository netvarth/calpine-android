package com.jaldeeinc.jaldee.response;

import java.io.Serializable;

public class AccountProfile implements Serializable {

    private String businessName;
    private String providerBusinessName;

    public String getBusinessName() {
        return businessName;
    }

    public void setBusinessName(String businessName) {
        this.businessName = businessName;
    }

    public String getProviderBusinessName() {
        return providerBusinessName;
    }

    public void setProviderBusinessName(String providerBusinessName) {
        this.providerBusinessName = providerBusinessName;
    }
}
