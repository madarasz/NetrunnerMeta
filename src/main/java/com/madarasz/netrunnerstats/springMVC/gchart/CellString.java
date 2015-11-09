package com.madarasz.netrunnerstats.springMVC.gchart;

/**
 * Created by imadaras on 11/9/15.
 * Object representation of the "cell" property with string value in Google Charts.
 */
public class CellString extends Cell {
    private String v;

    public CellString(String v) {
        super();
        this.v = v;
    }

    public CellString(String v, String style) {
        super(v, style);
        this.v = v;
    }

    public String getV() {
        return v;
    }
}
