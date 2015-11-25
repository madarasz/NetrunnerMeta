package com.madarasz.netrunnerstats.springMVC.gchartConverter;

import com.madarasz.netrunnerstats.database.DOs.stats.DPStatistics;
import com.madarasz.netrunnerstats.database.DOs.stats.entries.CountDeckStands;
import com.madarasz.netrunnerstats.springMVC.gchart.*;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by madarasz on 11/9/15.
 * Converts DPStats data compare (top/all) to Google Chart DataTable
 */
@Component
public class DPStatsToCompareGchart extends DPToChart {

    public DataTable converter(DPStatistics topstats, DPStatistics allstats, String sidecode, String stattype) {
        List<Column> columns = new ArrayList<>();
        columns.add(new Column("category", "string"));
        columns.add(new Column("all", "number"));
        columns.add(new Column("top 30%", "number"));
//        columns.add(new Column("", "", "string", "style"));
        List<Row> rows = new ArrayList<>();
        List<CountDeckStands> topstands = filter(topstats, sidecode, stattype);
        List<CountDeckStands> allstands = filter(allstats, sidecode, stattype);
        int sumtop;
        int sumall;
        if (sidecode.equals("runner")) {
            sumtop = topstats.getRunnerStatnum();
            sumall = allstats.getRunnerStatnum();
        } else {
            sumtop = topstats.getCorpStatnum();
            sumall = allstats.getCorpStatnum();
        }

        for (CountDeckStands allstand : allstands) {
            String identity = allstand.getTitle();
            CellString title = new CellString(identity);
            double fractionall = ((double)allstand.getStandingnum()) / sumall;
            double fractiontop = 0;
            for (CountDeckStands topstand : topstands) {
                if (topstand.getTitle().equals(identity)) {
                    fractiontop = ((double) topstand.getStandingnum()) / sumtop;
                    break;
                }
            }
            CellNumber countall = new CellNumber(fractionall, String.format("%,.1f%%", fractionall * 100));
            CellNumber counttop = new CellNumber(fractiontop, String.format("%,.1f%%", fractiontop * 100));
//            CellString style = new CellString(topstands.get(i).getColorcode());
            List<Cell> rowdata = new ArrayList<Cell>();
            rowdata.add(title);
            rowdata.add(countall);
            rowdata.add(counttop);
//            rowdata.add(style);
            rows.add(new Row(rowdata));
        }

        return new DataTable(columns, rows);
    }

}
