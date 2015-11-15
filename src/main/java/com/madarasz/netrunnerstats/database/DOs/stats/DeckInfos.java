package com.madarasz.netrunnerstats.database.DOs.stats;

import com.madarasz.netrunnerstats.database.DOs.Deck;
import com.madarasz.netrunnerstats.database.DOs.stats.entries.DeckInfo;
import com.madarasz.netrunnerstats.helper.comparator.DeckInfoComparator;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by madarasz on 11/11/15.
 */
public class DeckInfos {
    private List<DeckInfo> infos;

    public DeckInfos(List<Deck> decks) {
        infos = new ArrayList<DeckInfo>();
        for (Deck deck : decks) {
            infos.add(new DeckInfo(deck));
        }
        sortInfos();
    }

    public List<DeckInfo> getInfos() {
        return infos;
    }

    public void sortInfos() {
        DeckInfoComparator comparator = new DeckInfoComparator();
        infos.sort(comparator);
    }
}
