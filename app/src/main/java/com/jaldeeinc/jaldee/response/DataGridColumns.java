package com.jaldeeinc.jaldee.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.json.JSONObject;

import java.io.Serializable;

public class DataGridColumns implements Serializable {

    @SerializedName("label")
    @Expose
    private String label;

    @SerializedName("columnId")
    @Expose
    private String columnId;

    @SerializedName("dataType")
    @Expose
    private String dataType;

    @SerializedName("mandatory")
    @Expose
    private boolean mandatory;

    @SerializedName("lableValues")
    @Expose
    private Object labelValues;

    @SerializedName("filePropertie")
    @Expose
    private FileProperties fileProperties;

    @SerializedName("listPropertie")
    @Expose
    private ListProperties listProperties;

    @SerializedName("dateProperties")
    @Expose
    private DateProperties dateProperties;

    @SerializedName("plainTextPropertie")
    @Expose
    private PlainTextProperties plainTextProperties;

    @SerializedName("numberPropertie")
    @Expose
    private NumberProperties numberProperties;


    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getDataType() {
        return dataType;
    }

    public void setDataType(String dataType) {
        this.dataType = dataType;
    }

    public boolean isMandatory() {
        return mandatory;
    }

    public void setMandatory(boolean mandatory) {
        this.mandatory = mandatory;
    }

    public FileProperties getFileProperties() {
        return fileProperties;
    }

    public void setFileProperties(FileProperties fileProperties) {
        this.fileProperties = fileProperties;
    }

    public ListProperties getListProperties() {
        return listProperties;
    }

    public void setListProperties(ListProperties listProperties) {
        this.listProperties = listProperties;
    }

    public DateProperties getDateProperties() {
        return dateProperties;
    }

    public void setDateProperties(DateProperties dateProperties) {
        this.dateProperties = dateProperties;
    }

    public PlainTextProperties getPlainTextProperties() {
        return plainTextProperties;
    }

    public void setPlainTextProperties(PlainTextProperties plainTextProperties) {
        this.plainTextProperties = plainTextProperties;
    }

    public NumberProperties getNumberProperties() {
        return numberProperties;
    }

    public void setNumberProperties(NumberProperties numberProperties) {
        this.numberProperties = numberProperties;
    }

    public Object getLabelValues() {
        return labelValues;
    }

    public void setLabelValues(Object labelValues) {
        this.labelValues = labelValues;
    }

    public String getColumnId() {
        return columnId;
    }

    public void setColumnId(String columnId) {
        this.columnId = columnId;
    }
}
