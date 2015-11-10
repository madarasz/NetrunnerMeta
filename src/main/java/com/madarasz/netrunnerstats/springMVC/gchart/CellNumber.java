package com.madarasz.netrunnerstats.springMVC.gchart;

/**
 * Created by imadaras on 11/9/15.
 * Object representation of the "cell" property with numeric value in Google Charts.
 */
public class CellNumber extends Cell {
    private double v;

    public CellNumber(double v) {
        super();
        this.v = v;
    }

    public CellNumber(int v, String style) {
        super(new Integer(v).toString(), style);
        this.v = v;
    }

    public double getV() {
        return v;
    }
}
