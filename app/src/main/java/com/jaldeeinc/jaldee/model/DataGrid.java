package com.jaldeeinc.jaldee.model;

import java.io.Serializable;
import java.util.ArrayList;

public class DataGrid implements Serializable {

    public ArrayList<GridColumnAnswerLine> dataGridColumn;


    public ArrayList<GridColumnAnswerLine> getDataGridColumn() {
        return dataGridColumn;
    }

    public void setDataGridColumn(ArrayList<GridColumnAnswerLine> dataGridColumn) {
        this.dataGridColumn = dataGridColumn;
    }
}
