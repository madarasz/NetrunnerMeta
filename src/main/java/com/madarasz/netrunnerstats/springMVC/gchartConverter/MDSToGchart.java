package com.madarasz.netrunnerstats.springMVC.gchartConverter;

import com.madarasz.netrunnerstats.DOs.stats.IdentityMDS;
import com.madarasz.netrunnerstats.DOs.stats.entries.MDSEntry;
import com.madarasz.netrunnerstats.springMVC.gchart.*;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by madarasz on 11/10/15.
 * Converts MDS data to Google Chart DataTable
 */
@Component
public class MDSToGchart {

    public DataTable converter(IdentityMDS stats) {
        List<Column> columns = new ArrayList<Column>();
        columns.add(new Column("", "x", "", "number"));
        columns.add(new Column("", "y", "", "number"));
        columns.add(new Column("", "", "", ""));
        List<Row> rows = new ArrayList<Row>();
        for (MDSEntry entry : stats.getDecks()) {
            CellNumber x = new CellNumber(entry.getX());
            CellNumber y = new CellNumber(entry.getY());
            List<Cell> rowdata = new ArrayList<Cell>();
            rowdata.add(x);
            rowdata.add(y);
            rows.add(new Row(rowdata));
        }
        return new DataTable(columns, rows);
    }
}
