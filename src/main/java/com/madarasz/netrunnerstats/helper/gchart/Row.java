package com.madarasz.netrunnerstats.helper.gchart;

import java.util.List;

/**
 * Created by madarasz on 11/9/15.
 * Object representation of the "cols" property in Google Charts.
 */
public class Row {
    private List<Cell> c;

    public Row(List<Cell> c) {
        this.c = c;
    }

    public List<Cell> getC() {
        return c;
    }
}
