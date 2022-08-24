package com.jaldeeinc.jaldee.model;

import java.io.Serializable;
import java.util.ArrayList;

public class DataGrid implements Serializable {

    public ArrayList<GridColumnAnswerLine> dataGridColumn;
    public ArrayList<GridColumnAnswerLine> dataGridListColumn;

    public ArrayList<GridColumnAnswerLine> getDataGridListColumn() {
        return dataGridListColumn;
    }

    public void setDataGridListColumn(ArrayList<GridColumnAnswerLine> dataGridListColumn) {
        this.dataGridListColumn = dataGridListColumn;
    }

    public ArrayList<GridColumnAnswerLine> getDataGridColumn() {
        return dataGridColumn;
    }

    public void setDataGridColumn(ArrayList<GridColumnAnswerLine> dataGridColumn) {
        this.dataGridColumn = dataGridColumn;
    }
}
