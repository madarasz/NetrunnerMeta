package com.madarasz.netrunnerstats.springMVC.gchartConverter;

import com.madarasz.netrunnerstats.DOs.stats.CountDeckStands;
import com.madarasz.netrunnerstats.DOs.stats.DPStatistics;
import com.madarasz.netrunnerstats.springMVC.gchart.*;
import org.json.JSONArray;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by madarasz on 11/9/15.
 * Converts DPStats data to Google Chart DataTable
 */
@Component
public class DPStatsToGchart {

    public JSONArray colorRunnerFactions(DPStatistics stats) {
        JSONArray result = new JSONArray();
        result.put("#FF0000");
        result.put("#FF0000");
        result.put("#FF0000");
        return result;
    }

    public DataTable converter(DPStatistics stats, String sidecode, String stattype) {
        List<Column> columns = new ArrayList<Column>();
        columns.add(new Column("", "Faction", "", "string"));
        columns.add(new Column("", "player number", "", "number"));
        List<Row> rows = new ArrayList<Row>();

        List<CountDeckStands> data;
        if (sidecode.equals("runner")) {
            if (stattype.equals("identity")) {
                data = stats.getRunnerIdentities();
            } else if (stattype.equals("faction")){
                data = stats.getRunnerFactions();
            } else {
                return new DataTable();
            }
        } else if (sidecode.equals("corp")) {
            if (stattype.equals("identity")) {
                data = stats.getCorpIdentities();
            } else if (stattype.equals("faction")){
                data = stats.getCorpFactions();
            } else {
                return new DataTable();
            }
        } else {
            return new DataTable();
        }

        for (CountDeckStands faction : data) {
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
