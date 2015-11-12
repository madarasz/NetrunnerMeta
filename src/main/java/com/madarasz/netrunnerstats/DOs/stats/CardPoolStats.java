package com.madarasz.netrunnerstats.DOs.stats;

import com.madarasz.netrunnerstats.DOs.stats.entries.CardPool;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by madarasz on 11/12/15.
 */
public class CardPoolStats {
    private List<CardPool> cardpool;

    public CardPoolStats() {
        cardpool = new ArrayList<CardPool>();
    }

    public CardPoolStats(List<CardPool> cardpool) {
        this.cardpool = cardpool;
    }

    public List<CardPool> getCardpool() {
        return cardpool;
    }

    public void addCardPool (CardPool newCardPool) {
        cardpool.add(newCardPool);
    }
}
