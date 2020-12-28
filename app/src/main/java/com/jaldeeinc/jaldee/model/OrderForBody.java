package com.jaldeeinc.jaldee.model;

import java.io.Serializable;

public class OrderForBody implements Serializable {

    private int id;

    public OrderForBody(){

    }

    public OrderForBody(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
