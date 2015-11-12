package com.madarasz.netrunnerstats.springMVC.gchartConverter;

import com.madarasz.netrunnerstats.database.DOs.Deck;
import com.madarasz.netrunnerstats.database.DOs.stats.entries.DeckInfo;
import com.madarasz.netrunnerstats.database.DOs.stats.IdentityMDS;
import com.madarasz.netrunnerstats.database.DOs.stats.entries.MDSEntry;
import com.madarasz.netrunnerstats.database.DRs.DeckRepository;
import com.madarasz.netrunnerstats.springMVC.gchart.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by madarasz on 11/10/15.
 * Converts MDS data to Google Chart DataTable
 */
@Component
public class MDSToGchart {

    @Autowired
    DeckRepository deckRepository;

    private static final String STYLE_TOPDECK = "point {size: 8; shape-type: star; stroke-color: #FFFFFF;}";
    private static final String STYLE_NOT_TOPDECK = "point {stroke-color: #FFFFFF;}";

    public DataTable converter(IdentityMDS stats) {
        List<Column> columns = new ArrayList<Column>();
        columns.add(new Column("x", "number"));
        columns.add(new Column("y",  "number"));
        columns.add(new Column("", "", "string", "style"));
        columns.add(new Column("", "", new Formatting(true), "string", "tooltip"));
        List<Row> rows = new ArrayList<Row>();
        for (MDSEntry entry : stats.getDecks()) {
            List<Cell> rowdata = new ArrayList<Cell>();
            CellNumber x = new CellNumber(entry.getX());
            CellNumber y = new CellNumber(entry.getY());
            CellString style = null;
            Deck deck = deckRepository.findByUrl(entry.getDeckURL());
            CellString tooltip = new CellString(new DeckInfo(deck).getDigest());
            rowdata.add(x);
            rowdata.add(y);
            if (entry.isTopdeck()) {
                style = new CellString(STYLE_TOPDECK);
            } else {
                style = new CellString(STYLE_NOT_TOPDECK);
            }
            rowdata.add(style);
            rowdata.add(tooltip);
            rows.add(new Row(rowdata));
        }
        return new DataTable(columns, rows);
    }
}
