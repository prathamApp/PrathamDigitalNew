package com.pratham.prathamdigital.models;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Administrator on 8/31/2015.
 */
public class RaspProgram {
    @SerializedName("id")
    public String id;
    @SerializedName("data")
    public ModalProgram data;
    @SerializedName("filter_name")
    public String filter_name;
    @SerializedName("table_name")
    public String table_name;
    @SerializedName("facility")
    public String facility;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public ModalProgram getData() {
        return data;
    }

    public void setData(ModalProgram data) {
        this.data = data;
    }

    public String getFilter_name() {
        return filter_name;
    }

    public void setFilter_name(String filter_name) {
        this.filter_name = filter_name;
    }

    public String getTable_name() {
        return table_name;
    }

    public void setTable_name(String table_name) {
        this.table_name = table_name;
    }

    public String getFacility() {
        return facility;
    }

    public void setFacility(String facility) {
        this.facility = facility;
    }
}