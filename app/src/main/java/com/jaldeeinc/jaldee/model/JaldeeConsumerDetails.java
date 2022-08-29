package com.jaldeeinc.jaldee.model;

import java.io.Serializable;

public class JaldeeConsumerDetails implements Serializable {

    private int id;
    private boolean favourite;
    private boolean SignedUp;
    private boolean prodAccount;
    private int accountId;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean isFavourite() {
        return favourite;
    }

    public void setFavourite(boolean favourite) {
        this.favourite = favourite;
    }

    public boolean isSignedUp() {
        return SignedUp;
    }

    public void setSignedUp(boolean signedUp) {
        SignedUp = signedUp;
    }

    public boolean isProdAccount() {
        return prodAccount;
    }

    public void setProdAccount(boolean prodAccount) {
        this.prodAccount = prodAccount;
    }

    public int getAccountId() {
        return accountId;
    }

    public void setAccountId(int accountId) {
        this.accountId = accountId;
    }
}
