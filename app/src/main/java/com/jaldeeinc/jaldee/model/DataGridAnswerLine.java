package com.jaldeeinc.jaldee.model;

import java.io.Serializable;
import java.util.ArrayList;

public class DataGridAnswerLine implements Serializable {

    public ArrayList<GridColumnAnswerLine> dataGrid;

    public ArrayList<GridColumnAnswerLine> getDataGrid() {
        return dataGrid;
    }

    public void setDataGrid(ArrayList<GridColumnAnswerLine> dataGrid) {
        this.dataGrid = dataGrid;
    }
}
