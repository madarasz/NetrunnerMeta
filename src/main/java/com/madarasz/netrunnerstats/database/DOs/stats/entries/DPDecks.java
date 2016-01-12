package com.madarasz.netrunnerstats.database.DOs.stats.entries;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by madarasz on 1/11/16.
 * Deck infos in a datapack
 */
public class DPDecks {
    private int count;
    private String dptitle;
    private List<String> deckLinks;

    public DPDecks() {
        this.deckLinks = new ArrayList<>();
        this.count = 0;
    }

    public DPDecks(String title, int count) {
        this.dptitle = title;
        this.deckLinks = new ArrayList<>();
        this.count = count;
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

    public void addDeckLink(String deckLink) {
        deckLinks.add(deckLink);
    }
}
