package com.jaldeeinc.jaldee.model;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;

public class AnswerLine implements Serializable {

    private String labelName = "";

    @SerializedName("answer")
    @Expose
    private JsonObject answer = new JsonObject();


    public String getLabelName() {
        return labelName;
    }

    public void setLabelName(String labelName) {
        this.labelName = labelName;
    }

    public JsonObject getAnswer() {
        return answer;
    }

    public void setAnswer(JsonObject answer) {
        this.answer = answer;
    }

    public ArrayList<DataGrid> getDataGridList() {

        try {

            ArrayList<DataGrid> result = new ArrayList<>();

            JsonArray dataGridList = answer.getAsJsonArray("dataGrid");

            if (dataGridList != null && dataGridList.size() > 0) {

                for (JsonElement e : dataGridList) {

                    DataGrid gridObj = new DataGrid();
                    JsonObject gridItem = e.getAsJsonObject();
                    JsonArray columnsList = gridItem.getAsJsonArray("dataGridColumn");

                    ArrayList<GridColumnAnswerLine> columnAnswersList = new ArrayList<>();

                    if (columnsList != null && columnsList.size() > 0) {

                        for (JsonElement ce : columnsList) {

                            JsonObject columnItem = ce.getAsJsonObject();
                            GridColumnAnswerLine columnAnswer = new GridColumnAnswerLine();
                            columnAnswer.setColumnId(columnItem.get("columnId").getAsString());
                            columnAnswer.setColumn(columnItem.getAsJsonObject("column"));

                            columnAnswersList.add(columnAnswer);

                        }

                    }

                    gridObj.setDataGridColumn(columnAnswersList);
                    result.add(gridObj);
                }

            }

            return result;

        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }


    }
}
