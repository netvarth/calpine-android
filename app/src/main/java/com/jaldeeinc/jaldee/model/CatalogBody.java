package com.jaldeeinc.jaldee.model;

import java.io.Serializable;

public class CatalogBody implements Serializable {

    private int id;

    public CatalogBody(){

    }

    public CatalogBody(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
