package com.jaldeeinc.jaldee.model;

import com.google.gson.JsonObject;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class GridColumnAnswerLine implements Serializable {

    public String columnId;
    public JsonObject column;

    public String getColumnId() {
        return columnId;
    }

    public void setColumnId(String columnId) {
        this.columnId = columnId;
    }

    public JsonObject getColumn() {
        return column;
    }

    public void setColumn(JsonObject column) {
        this.column = column;
    }
}
