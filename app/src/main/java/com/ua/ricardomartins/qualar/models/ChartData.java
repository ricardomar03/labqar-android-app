package com.ua.ricardomartins.qualar.models;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by ricardo on 19/04/16.
 */
public class ChartData {

    @SerializedName("value")
    private ArrayList<ArrayList<String>> value;

    public ArrayList<ArrayList<String>> getValues() {
        return value;
    }
}
