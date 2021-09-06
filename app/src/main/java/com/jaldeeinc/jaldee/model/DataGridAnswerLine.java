package com.jaldeeinc.jaldee.model;

import java.io.Serializable;
import java.util.ArrayList;

public class DataGridAnswerLine implements Serializable {

    public ArrayList<DataGrid> dataGrid;

    public ArrayList<DataGrid> getDataGrid() {
        return dataGrid;
    }

    public void setDataGrid(ArrayList<DataGrid> dataGrid) {
        this.dataGrid = dataGrid;
    }
}
