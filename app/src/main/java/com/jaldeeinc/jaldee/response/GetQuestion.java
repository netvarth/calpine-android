package com.jaldeeinc.jaldee.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.json.JSONObject;

import java.util.ArrayList;

public class GetQuestion {

    @SerializedName("id")
    @Expose
    private int id;

    @SerializedName("labelName")
    @Expose
    private String labelName;

    @SerializedName("sequnceId")
    @Expose
    private String sequenceId;

    @SerializedName("fieldDataType")
    @Expose
    private String fieldDataType;

    @SerializedName("fieldScope")
    @Expose
    private String fieldScope;

    @SerializedName("label")
    @Expose
    private String label;

    @SerializedName("labelValues")
    @Expose
    private Object labelValues;

    @SerializedName("hint")
    @Expose
    private String hint;

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

    @SerializedName("billable")
    @Expose
    private boolean billable;

    @SerializedName("mandatory")
    @Expose
    private boolean mandatory;


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getLabelName() {
        return labelName;
    }

    public void setLabelName(String labelName) {
        this.labelName = labelName;
    }

    public String getSequenceId() {
        return sequenceId;
    }

    public void setSequenceId(String sequenceId) {
        this.sequenceId = sequenceId;
    }

    public String getFieldDataType() {
        return fieldDataType;
    }

    public void setFieldDataType(String fieldDataType) {
        this.fieldDataType = fieldDataType;
    }

    public String getFieldScope() {
        return fieldScope;
    }

    public void setFieldScope(String fieldScope) {
        this.fieldScope = fieldScope;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public Object getLabelValues() {
        return labelValues;
    }

    public void setLabelValues(Object labelValues) {
        this.labelValues = labelValues;
    }

    public String getHint() {
        return hint;
    }

    public void setHint(String hint) {
        this.hint = hint;
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

    public boolean isBillable() {
        return billable;
    }

    public void setBillable(boolean billable) {
        this.billable = billable;
    }

    public boolean isMandatory() {
        return mandatory;
    }

    public void setMandatory(boolean mandatory) {
        this.mandatory = mandatory;
    }
}
