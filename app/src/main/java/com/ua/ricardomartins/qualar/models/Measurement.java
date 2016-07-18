package com.ua.ricardomartins.qualar.models;

import com.google.gson.annotations.SerializedName;
import com.google.gson.internal.LinkedHashTreeMap;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

public class Measurement {

    @SerializedName("date_time")
    private String dateTime;


    @SerializedName("values")
    private LinkedHashMap<String,Pollutant> values = new LinkedHashMap<>();


    @SerializedName("campaign")
    private String campaign;

    public String getCampaign() {
        return campaign;
    }

    public void setCampaign(String campaign) {
        this.campaign = campaign;
    }

    /**
     * @return The dateTime
     */
    public String getDateTime() {
        return dateTime;
    }

    /**
     * @param dateTime The date_time
     */
    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    /**
     * @return The values
     */
    public LinkedHashMap<String,Pollutant> getValues() {
        return values;
    }

    /**
     * @param values The values
     */
    public void setValues(LinkedHashMap<String,Pollutant> values) {
        this.values = values;
    }
}

