package com.jaldeeinc.jaldee.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

public class DataGridProperties implements Serializable {

    @SerializedName("dataGridColumns")
    @Expose
    private ArrayList<DataGridColumns> gridList;

    @SerializedName("dataGridListColumns")  // for serviceOptionQNR
    @Expose
    private ArrayList<DataGridColumns> gridListColumns;

    public ArrayList<DataGridColumns> getGridListColumns() {
        return gridListColumns;
    }

    public void setGridListColumns(ArrayList<DataGridColumns> gridListColumns) {
        this.gridListColumns = gridListColumns;
    }

    public ArrayList<DataGridColumns> getGridList() {
        return gridList;
    }

    public void setGridList(ArrayList<DataGridColumns> gridList) {
        this.gridList = gridList;
    }
}
