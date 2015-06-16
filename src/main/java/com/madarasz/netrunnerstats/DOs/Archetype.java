package com.madarasz.netrunnerstats.DOs;

import com.madarasz.netrunnerstats.DOs.relationships.DeckHasCard;

import java.util.*;

/**
 * Statistics for archetype
 * Created by madarasz on 2015-06-16.
 */
public class Archetype {

    private String name;
    // TODO: model
    private Map<Card, Integer> cards;
    private int deckcount;

    public Archetype() {

    }

    public Archetype(String name, List<Deck> deckList) {
        Map<Card, Integer> stats = new HashMap<Card, Integer>();
        for (Deck deck : deckList) {
            Set<DeckHasCard> cardSet = deck.getCards();
            for (DeckHasCard deckHasCard : cardSet) {
                Card card = deckHasCard.getCard();
                int quantity = deckHasCard.getQuantity();
                if (stats.containsKey(card)) {
                    stats.put(card, stats.get(card) + quantity);
                } else {
                    stats.put(card, quantity);
                }
            }
        }
        deckcount = deckList.size();
        cards = stats;
        this.name = name;
    }

    @Override
    public String toString() {
        String result ="";
        result += String.format("%s - number of cards: %d\n", name, deckcount);
        Iterator iterator = cards.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry pair = (Map.Entry) iterator.next();
            result += String.format("%s - %d\n", pair.getKey().toString(), pair.getValue());
        }
        return result;
    }
}
