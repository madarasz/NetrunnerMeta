package com.madarasz.netrunnerstats.helper.gchartConverter;

import com.madarasz.netrunnerstats.Statistics;
import com.madarasz.netrunnerstats.database.DOs.stats.CardPoolStats;
import com.madarasz.netrunnerstats.database.DOs.stats.entries.CardPool;
import com.madarasz.netrunnerstats.database.DOs.stats.entries.CountDeckStands;
import com.madarasz.netrunnerstats.database.DRs.CardRepository;
import com.madarasz.netrunnerstats.helper.ColorPicker;
import com.madarasz.netrunnerstats.helper.gchart.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by madarasz on 11/9/15.
 * Converts DPStats data over time to Google Chart DataTable
 */
@Component
public class DPStatsToOverTimeGchart extends DPToChart {

    @Autowired
    Statistics statistics;

    @Autowired
    CardRepository cardRepository;

    @Autowired
    ColorPicker colorPicker;

    public DataTable converter(String sidecode) {

        CardPoolStats cardPoolStats = statistics.getCardPoolStats();
        List<String> factions = cardRepository.findFactionTitlesBySide(sidecode);

        // prepare columns
        List<Column> columns = new ArrayList<>();
        columns.add(new Column("pack", "string"));
        for (String faction : factions) {
            columns.add(new Column(colorPicker.colorFaction(faction), faction, "number", "")); // putting color code into id field
        }
        List<Row> rows = new ArrayList<>();

        List<CardPool> sortedPool = cardPoolStats.getSortedCardpool();
        Collections.reverse(sortedPool);

        // calculate rows
        for (CardPool cardPool : sortedPool) {
            List<Cell> rowdata = new ArrayList<>();
            String cardpoolname = cardPool.getTitle();
            List<CountDeckStands> stats = filter(statistics.getPackStats(cardpoolname, true), sidecode, "faction");
            CellString title = new CellString(cardpoolname);
            rowdata.add(title);

            for (String faction : factions) {
                boolean found = false;
                for (CountDeckStands countDeckStands : stats) {
                    if (countDeckStands.getTitle().equals(faction)) {
                        double percentage = countDeckStands.getPercentage();
                        rowdata.add(new CellNumber(percentage, String.format("%,.1f%%", percentage * 100)));
                        found = true;
                    }
                }

                // if there was no data
                if ((cardPool.getCyclenumber() >=9) && (!found)) {
                    rowdata.add(new CellNumber(0, "0%"));
                }
            }
            rows.add(new Row(rowdata));
        }

        return new DataTable(columns, rows);
    }

}
