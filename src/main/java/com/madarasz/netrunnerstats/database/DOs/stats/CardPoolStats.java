package com.madarasz.netrunnerstats.database.DOs.stats;

import com.madarasz.netrunnerstats.database.DOs.stats.entries.CardPool;
import com.madarasz.netrunnerstats.helper.comparator.CardPoolComparator;
import org.springframework.data.neo4j.annotation.Fetch;
import org.springframework.data.neo4j.annotation.GraphId;
import org.springframework.data.neo4j.annotation.NodeEntity;
import org.springframework.data.neo4j.annotation.RelatedTo;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by madarasz on 11/12/15.
 * Statistical DB entry for CardpoolStats. 1 entry in entire application.
 */
@NodeEntity
public class CardPoolStats {
    @GraphId
    private Long id;
    @RelatedTo(type = "DPS") private @Fetch Set<CardPool> cardpool;

    public CardPoolStats() {
        this.cardpool = new HashSet<>();
    }

    public Set<CardPool> getCardpool() {
        return cardpool;
    }

    public List<CardPool> getSortedCardpool() {
        CardPoolComparator comparator = new CardPoolComparator();
        List<CardPool> result = new ArrayList<>(cardpool);
        result.sort(comparator);
        return result;
    }

    public void addCardPool (CardPool newCardPool) {
        cardpool.add(newCardPool);
    }
}
