package com.ua.ricardomartins.qualar.models;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by ricardo on 29/04/16.
 */
public class IndexChartData {


    @SerializedName("index_chart")
    private ArrayList<ArrayList<String>> values;

    public ArrayList<ArrayList<String>> getValues() {
        return values;
    }

}
