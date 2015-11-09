package com.madarasz.netrunnerstats.springMVC.gchart;

/**
 * Created by imadaras on 11/9/15.
 * Object representation of the "cell" property with numeric value in Google Charts.
 */
public class CellNumber extends Cell {
    private int v;

    public CellNumber(int v) {
        super();
        this.v = v;
    }

    public int getV() {
        return v;
    }
}
