package com.madarasz.netrunnerstats.database.DOs;

import com.madarasz.netrunnerstats.database.DOs.relationships.DeckHasCard;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created by madarasz on 2015.07.26..
 */
public class AvarageDeck {
    private Map<Card, Float> cards;

    public AvarageDeck() {

    }

    public AvarageDeck(ArrayList<Deck> decks) {
        int size = decks.size();
        Map<Card, Float> stats = new HashMap<Card, Float>();
        for (Deck deck : decks) {
            Set<DeckHasCard> hasCards = deck.getCards();
            for (DeckHasCard hasCard : hasCards) {
                Card card = hasCard.getCard();
                int quantity = hasCard.getQuantity();
                if (stats.containsKey(card)) {
                    stats.put(card, stats.get(card) + quantity / size);
                } else {
                    stats.put(card, new Float(quantity / size));
                }
            }
        }
        cards = stats;
    }
}
