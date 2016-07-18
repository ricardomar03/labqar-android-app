package com.ua.ricardomartins.qualar.models;

import com.google.gson.annotations.SerializedName;

/**
 * Created by ricardo on 18/04/16.
 */
public class Pollutant {

    @SerializedName("value")
    private String value;

    @SerializedName("stat")
    private String stat;

    @SerializedName("name")
    private String name;

    @SerializedName("unit")
    private String unit;


    public String getValue() {
        return value;
    }

    public String getStat() {
        return stat;
    }

    public String getName() {
        return name;
    }

    public String getUnit() {
        return unit;
    }
}
