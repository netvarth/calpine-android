package com.jaldeeinc.jaldee.model;

import java.io.Serializable;

public class RlsdQnr implements Serializable {
    int id;
    String status;
    String qnrName;

    public String getQnrName() {
        return qnrName;
    }

    public void setQnrName(String qnrName) {
        this.qnrName = qnrName;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getId() {
        return id;
    }

    public String getStatus() {
        return status;
    }
}
