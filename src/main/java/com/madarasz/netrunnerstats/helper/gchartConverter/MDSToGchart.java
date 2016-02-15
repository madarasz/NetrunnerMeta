package com.madarasz.netrunnerstats.helper.gchartConverter;

import com.madarasz.netrunnerstats.Statistics;
import com.madarasz.netrunnerstats.database.DOs.Deck;
import com.madarasz.netrunnerstats.database.DOs.Tournament;
import com.madarasz.netrunnerstats.database.DOs.stats.IdentityMDS;
import com.madarasz.netrunnerstats.database.DOs.stats.entries.MDSEntry;
import com.madarasz.netrunnerstats.database.DRs.DeckRepository;
import com.madarasz.netrunnerstats.database.DRs.TournamentRepository;
import com.madarasz.netrunnerstats.helper.LastThree;
import com.madarasz.netrunnerstats.helper.gchart.*;
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

    @Autowired
    Statistics statistics;

    @Autowired
    LastThree lastThree;

    @Autowired
    TournamentRepository tournamentRepository;

    private static final String STYLE_TOPDECK = "point {size: 8; shape-type: star; color: %s; stroke-color: #FFFFFF;}";
    private static final String STYLE_NOT_TOPDECK = "point {color: %s; stroke-color: #FFFFFF;}";
    private static final String[] COLORS = {"#3769cd", "#cd3737", "#cd9637"};

    public DataTable converter(IdentityMDS stats) {
        List<Column> columns = new ArrayList<>();
        List<String> last3Names = lastThree.getLastThreeCardpoolNames();
        String color;
        columns.add(new Column("x", "number"));
        columns.add(new Column("y",  "number"));
        columns.add(new Column("", "", "string", "style"));
        columns.add(new Column("", "", new Formatting(true), "string", "tooltip"));
        List<Row> rows = new ArrayList<>();
        for (MDSEntry entry : stats.getSortedDecks()) {
            List<Cell> rowdata = new ArrayList<>();
            CellNumber x = new CellNumber(entry.getX());
            CellNumber y = new CellNumber(entry.getY());
            CellString style;
            Deck deck = deckRepository.findByUrl(entry.getDeckURL());
            CellString tooltip = new CellString(statistics.getDeckInfo(deck).getDigest());
            rowdata.add(x);
            rowdata.add(y);

            // style color
            if (stats.getDpname().equals(statistics.LAST_3)) {
                Tournament tournament = tournamentRepository.getTournamentByDeckUrl(deck.getUrl());
                color = COLORS[last3Names.indexOf(tournament.getCardpool().getName())];
            } else {
                color = COLORS[0];
            }

            // style shape
            if (entry.isTopdeck()) {
                style = new CellString(String.format(STYLE_TOPDECK, color));
            } else {
                style = new CellString(String.format(STYLE_NOT_TOPDECK, color));
            }
            rowdata.add(style);
            rowdata.add(tooltip);
            rows.add(new Row(rowdata));
        }
        return new DataTable(columns, rows);
    }
}
