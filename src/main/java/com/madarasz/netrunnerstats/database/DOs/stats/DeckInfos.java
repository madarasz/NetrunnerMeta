package com.madarasz.netrunnerstats.database.DOs.stats;

import com.madarasz.netrunnerstats.database.DOs.stats.entries.DeckInfo;
import com.madarasz.netrunnerstats.helper.comparator.DeckInfoComparator;
import org.springframework.data.neo4j.annotation.GraphId;
import org.springframework.data.neo4j.annotation.NodeEntity;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by madarasz on 11/11/15.
 */
@NodeEntity
public class DeckInfos {
    @GraphId
    private Long id;
    private Set<DeckInfo> infos;
    private String cardpoolname;
    private String identitytitle;

    public DeckInfos() {
        infos = new HashSet<DeckInfo>();
    }

    public DeckInfos(String cardpoolname, String identitytitle) {
        this.cardpoolname = cardpoolname;
        this.identitytitle = identitytitle;
        this.infos = new HashSet<DeckInfo>();
    }

    public Set<DeckInfo> getInfos() {
        return infos;
    }

    public String getIdentitytitle() {
        return identitytitle;
    }

    public String getCardpoolname() {
        return cardpoolname;
    }

    public void addDeckInfo(DeckInfo deckInfo) {
        infos.add(deckInfo);
    }

    public List<DeckInfo> getSortedInfos() {
        DeckInfoComparator comparator = new DeckInfoComparator();
        List<DeckInfo> result = new ArrayList<DeckInfo>(infos);
        result.sort(comparator);
        return result;
    }
}
