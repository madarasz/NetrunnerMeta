package com.madarasz.netrunnerstats.springMVC.gchart;

import java.util.List;

/**
 * Created by madarasz on 11/9/15.
 * Object representation of the "DataTable" in Google Charts.
 */
public class DataTable {
    private List<Column> cols;
    private List<Row> rows;

    public DataTable(List<Column> cols, List<Row> rows) {
        this.cols = cols;
        this.rows = rows;
    }

    public List<Column> getCols() {
        return cols;
    }

    public List<Row> getRows() {
        return rows;
    }
}
