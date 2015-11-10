package com.madarasz.netrunnerstats.springMVC.gchartConverter;

import com.madarasz.netrunnerstats.DOs.stats.entries.CountDeckStands;
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

    public List<String> colorConverter(DPStatistics stats, String sidecode, String stattype) {
        List<String> result = new ArrayList<String>();
        for (CountDeckStands info : filter(stats, sidecode, stattype)) {
            result.add(info.getColorcode());
        }
        return result;
    }

    public DataTable converter(DPStatistics stats, String sidecode, String stattype) {
        List<Column> columns = new ArrayList<Column>();
        columns.add(new Column("Faction", "string"));
        columns.add(new Column("player number", "number"));
        List<Row> rows = new ArrayList<Row>();

        for (CountDeckStands info : filter(stats, sidecode, stattype)) {
            CellString title = new CellString(info.getTitle());
            CellNumber count = new CellNumber(info.getStandingnum());
            List<Cell> rowdata = new ArrayList<Cell>();
            rowdata.add(title);
            rowdata.add(count);
            rows.add(new Row(rowdata));
        }
        return new DataTable(columns, rows);
    }

    private List<CountDeckStands> filter (DPStatistics stats, String sidecode, String stattype){
        List<CountDeckStands> data;
        if (sidecode.equals("runner")) {
            if (stattype.equals("identity")) {
                data = stats.getRunnerIdentities();
            } else if (stattype.equals("faction")){
                data = stats.getRunnerFactions();
            } else {
                return new ArrayList<CountDeckStands>();
            }
        } else if (sidecode.equals("corp")) {
            if (stattype.equals("identity")) {
                data = stats.getCorpIdentities();
            } else if (stattype.equals("faction")){
                data = stats.getCorpFactions();
            } else {
                return new ArrayList<CountDeckStands>();
            }
        } else {
            return new ArrayList<CountDeckStands>();
        }
        return data;
    }

}
