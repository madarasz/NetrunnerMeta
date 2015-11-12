package com.madarasz.netrunnerstats.database.DOs.stats;

import com.madarasz.netrunnerstats.database.DOs.stats.entries.MDSEntry;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by madarasz on 11/10/15.
 * Output data for deck similarity multidimensional scaling
 */
public class IdentityMDS {
    private String DPName;
    private String identityTitle;
    private int decknum;
    private int topdecknum;
    private List<MDSEntry> decks;

    public IdentityMDS(String DPName, String identityTitle, int decknum, int topdecknum) {
        this.DPName = DPName;
        this.identityTitle = identityTitle;
        this.decknum = decknum;
        this.topdecknum = topdecknum;
        this.decks = new ArrayList<MDSEntry>();
    }

    public void addDeck(MDSEntry deck) {
        decks.add(deck);
    }

    public String getDPName() {
        return DPName;
    }

    public String getIdentityTitle() {
        return identityTitle;
    }

    public int getDecknum() {
        return decknum;
    }

    public int getTopdecknum() {
        return topdecknum;
    }

    public List<MDSEntry> getDecks() {
        return decks;
    }
}
