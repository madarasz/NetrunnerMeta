package com.madarasz.netrunnerstats.springMVC.gchart;

/**
 * Created by madarasz on 11/9/15.
 * Object representation of the "cell" property in Google Charts.
 */
public abstract class Cell {
    private String f;   // formatted value
//    private String p; - not using it now

    public Cell() {
        f = null;
    }

    public Cell(String f) {
        this.f = f;
    }

    public String getF() {
        return f;
    }
}
