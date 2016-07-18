package com.ua.ricardomartins.qualar.models;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by ricardo on 21/06/16.
 */
public class Alert {

    @SerializedName("alerts")
    private ArrayList<String[]> alerts;


    /**
     * @return The values
     */
    public ArrayList<String[]> getAlerts() {
        return alerts;
    }

    /**
     * @param index The values
     */
    public void setAlerts(ArrayList<String[]> index) {
        this.alerts = index;
    }
}
