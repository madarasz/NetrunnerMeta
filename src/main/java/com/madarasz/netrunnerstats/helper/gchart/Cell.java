package com.madarasz.netrunnerstats.helper.gchart;

/**
 * Created by madarasz on 11/9/15.
 * Object representation of the "cell" property in Google Charts.
 */
public abstract class Cell {
    protected String f;   // formatted value
    protected Style p;   // style properties

    public Cell() {
        f = null;
        p = null;
    }

    public Cell(String f) {
        this.f = f;
        p = null;
    }

    public Cell(String f, String style) {
        this.f = f;
        this.p = new Style(style);
    }

    public String getF() {
        return f;
    }

    public Style getP() {
        return p;
    }
}
