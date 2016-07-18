package com.ua.ricardomartins.qualar.models;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by ricardo on 18/04/16.
 */
public class AirIndex {
    @SerializedName("date_time")
    private String dateTime;


    @SerializedName("index")
    private String[] index;


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
    public String[] getIndex() {
        return index;
    }

    /**
     * @param index The values
     */
    public void setIndex(String[] index) {
        this.index = index;
    }
}
