package com.madarasz.netrunnerstats.helper;

import com.madarasz.netrunnerstats.database.DOs.Card;

/**
 * Created by madarasz on 1/7/16.
 * A card and a count
 */
public class CardCount {
    private Card card;
    private int count;

    public CardCount(Card card, int count) {
        this.card = card;
        this.count = count;
    }

    public Card getCard() {
        return card;
    }

    public int getCount() {
        return count;
    }

    public void incrementCount(int x) {
        this.count += x;
    }
}
