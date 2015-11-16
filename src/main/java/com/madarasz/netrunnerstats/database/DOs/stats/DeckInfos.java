package com.madarasz.netrunnerstats.database.DOs.stats;

import com.madarasz.netrunnerstats.database.DOs.stats.entries.DeckInfo;
import com.madarasz.netrunnerstats.helper.comparator.DeckInfoComparator;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by madarasz on 11/11/15.
 */
public class DeckInfos {
    private List<DeckInfo> infos;

    public DeckInfos() {
        infos = new ArrayList<DeckInfo>();
    }

    public List<DeckInfo> getInfos() {
        return infos;
    }

    public void addDeckInfo(DeckInfo deckInfo) {
        infos.add(deckInfo);
    }

    public void sortInfos() {
        DeckInfoComparator comparator = new DeckInfoComparator();
        infos.sort(comparator);
    }
}
