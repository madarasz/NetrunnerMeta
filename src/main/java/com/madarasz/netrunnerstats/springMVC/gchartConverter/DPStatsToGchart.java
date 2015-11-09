package com.madarasz.netrunnerstats.springMVC.gchartConverter;

import com.madarasz.netrunnerstats.DOs.stats.CountDeckStands;
import com.madarasz.netrunnerstats.DOs.stats.DPStatistics;
import com.madarasz.netrunnerstats.springMVC.gchart.*;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by madarasz on 11/9/15.
 * Converts DPStats data to Google Chart DataTable
 */
@Component
public class DPStatsToGchart {

    public DataTable convertRunnerFactions (DPStatistics stats) {
        List<Column> columns = new ArrayList<Column>();
        columns.add(new Column("", "Faction", "", "string"));
        columns.add(new Column("", "player number", "", "number"));
        List<Row> rows = new ArrayList<Row>();
        for (CountDeckStands faction : stats.getRunnerFactions()) {
            CellString title = new CellString(faction.getTitle());
            CellNumber count = new CellNumber(faction.getStandingnum());
            List<Cell> rowdata = new ArrayList<Cell>();
            rowdata.add(title);
            rowdata.add(count);
            rows.add(new Row(rowdata));
        }
        return new DataTable(columns, rows);
    }
}