package com.madarasz.netrunnerstats.helper.gchartConverter;

import com.madarasz.netrunnerstats.Statistics;
import com.madarasz.netrunnerstats.database.DOs.stats.CardStat;
import com.madarasz.netrunnerstats.database.DOs.stats.entries.CardUsage;
import com.madarasz.netrunnerstats.database.DRs.CardRepository;
import com.madarasz.netrunnerstats.helper.gchart.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by madarasz on 11/9/15.
 * Converts CardStat data over time to Google Chart DataTable
 */
@Component
public class CardToOverTimeGchart {

    @Autowired
    Statistics statistics;

    @Autowired
    CardRepository cardRepository;

    public DataTable converter(String cardTitle) {

        CardStat cardStat = statistics.getCardStats(cardTitle);

        // prepare columns
        List<Column> columns = new ArrayList<>();
        columns.add(new Column("pack", "string"));
        columns.add(new Column("all", "number"));
        columns.add(new Column("top 30%", "number"));
        List<Row> rows = new ArrayList<>();

        // calculate rows
        for (CardUsage cardUsage : cardStat.getOverTime()) {
            List<Cell> rowdata = new ArrayList<>();
            String cardpoolname = cardUsage.getCardpacktitle();
            CellString title = new CellString(cardpoolname);
            rowdata.add(title);
            double topfraction = cardUsage.getTopdeckfraction();
            double fraction = cardUsage.getDeckfraction();
            rowdata.add(new CellNumber(fraction, String.format("%,.1f%%", fraction * 100)));
            rowdata.add(new CellNumber(topfraction, String.format("%,.1f%%", topfraction * 100)));
            rows.add(new Row(rowdata));
        }

        return new DataTable(columns, rows);
    }

}
