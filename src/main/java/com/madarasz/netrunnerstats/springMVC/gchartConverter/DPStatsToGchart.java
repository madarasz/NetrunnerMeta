package com.madarasz.netrunnerstats.springMVC.gchartConverter;

import com.madarasz.netrunnerstats.database.DOs.stats.entries.CountDeckStands;
import com.madarasz.netrunnerstats.database.DOs.stats.DPStatistics;
import com.madarasz.netrunnerstats.springMVC.gchart.*;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by madarasz on 11/9/15.
 * Converts DPStats data to Google Chart DataTable
 */
@Component
public class DPStatsToGchart extends DPToChart{

    public DataTable converter(DPStatistics stats, String sidecode, String stattype) {
        List<Column> columns = new ArrayList<>();
        columns.add(new Column("Faction", "string"));
        columns.add(new Column("player number", "number"));
        columns.add(new Column("", "", "string", "style"));
        List<Row> rows = new ArrayList<>();

        for (CountDeckStands info : filter(stats, sidecode, stattype)) {
            CellString title = new CellString(info.getTitle());
            CellNumber count = new CellNumber(info.getStandingnum());
            CellString style = new CellString(info.getColorcode());
            List<Cell> rowdata = new ArrayList<>();
            rowdata.add(title);
            rowdata.add(count);
            rowdata.add(style);
            rows.add(new Row(rowdata));
        }
        return new DataTable(columns, rows);
    }
}
