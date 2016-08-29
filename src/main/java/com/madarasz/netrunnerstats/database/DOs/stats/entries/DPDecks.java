package com.madarasz.netrunnerstats.database.DOs.stats.entries;

import org.springframework.data.neo4j.annotation.GraphId;
import org.springframework.data.neo4j.annotation.NodeEntity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by madarasz on 1/11/16.
 * Deck links (with a certain card) filtered for a  datapack
 */
@NodeEntity
public class DPDecks {
    @GraphId
    private Long id;
    private int count;
    private int dpnumber;
    private int cyclenumber;
    private String dptitle;
    private List<String> deckLinks;

    public DPDecks() {
        this.deckLinks = new ArrayList<>();
        this.count = 0;
    }

    public DPDecks(String title, int count, int dpnumber, int cyclenumber) {
        this.dptitle = title;
        this.deckLinks = new ArrayList<>();
        this.count = count;
        this.dpnumber = dpnumber;
        this.cyclenumber = cyclenumber;
    }

    public String getDptitle() {
        return dptitle;
    }

    public List<String> getDeckLinks() {
        return deckLinks;
    }

    public int getCount() {
        return count;
    }

    public int getDpnumber() {
        return dpnumber;
    }

    public int getCyclenumber() {
        return cyclenumber;
    }

    public void addDeckLink(String deckLink) {
        deckLinks.add(deckLink);
    }
}
