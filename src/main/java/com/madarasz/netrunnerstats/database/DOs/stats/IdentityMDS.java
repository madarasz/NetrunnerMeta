package com.madarasz.netrunnerstats.database.DOs.stats;

import com.madarasz.netrunnerstats.database.DOs.stats.entries.MDSEntry;
import com.madarasz.netrunnerstats.helper.comparator.MDSEntryComparator;
import org.springframework.data.neo4j.annotation.Fetch;
import org.springframework.data.neo4j.annotation.GraphId;
import org.springframework.data.neo4j.annotation.NodeEntity;
import org.springframework.data.neo4j.annotation.RelatedTo;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by madarasz on 11/10/15.
 * Output data for deck similarity multidimensional scaling
 */
@NodeEntity
public class IdentityMDS {
    @GraphId
    private Long id;
    private String dpname;
    private String identitytitle;
    private int decknum;
    private int topdecknum;
    @RelatedTo(type = "IDENT") private @Fetch Set<MDSEntry> decks;

    public IdentityMDS() {
    }

    public IdentityMDS(String dpname, String identitytitle, int decknum, int topdecknum) {
        this.dpname = dpname;
        this.identitytitle = identitytitle;
        this.decknum = decknum;
        this.topdecknum = topdecknum;
        this.decks = new HashSet<MDSEntry>();
    }

    public void addDeck(MDSEntry deck) {
        decks.add(deck);
    }

    public String getDpname() {
        return dpname;
    }

    public String getIdentitytitle() {
        return identitytitle;
    }

    public int getDecknum() {
        return decknum;
    }

    public int getTopdecknum() {
        return topdecknum;
    }

    public Set<MDSEntry> getDecks() {
        return decks;
    }

    public void setDpname(String dpname) {
        this.dpname = dpname;
    }

    public void setIdentitytitle(String identitytitle) {
        this.identitytitle = identitytitle;
    }

    public void setDecknum(int decknum) {
        this.decknum = decknum;
    }

    public void setTopdecknum(int topdecknum) {
        this.topdecknum = topdecknum;
    }

    public void setDecks(Set<MDSEntry> decks) {
        this.decks = decks;
    }

    public List<MDSEntry> getSortedDecks() {
        MDSEntryComparator comparator = new MDSEntryComparator();
        List<MDSEntry> result = new ArrayList<MDSEntry>(decks);
        result.sort(comparator);
        return result;
    }
}
