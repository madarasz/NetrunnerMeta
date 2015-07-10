package com.madarasz.netrunnerstats.helper;

import com.madarasz.netrunnerstats.DOs.Card;
import com.madarasz.netrunnerstats.DOs.Deck;
import com.madarasz.netrunnerstats.DOs.relationships.DeckHasCard;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by madarasz on 2015.07.10..
 * Calculates multidimensional scaling for decks
 */
@Component
public class MultiDimensionalScaling {

    public int getDeckDistance(Deck decka, Deck deckb) {

        // get all cards
        Set<DeckHasCard> deckACards = decka.getCards();
        Set<DeckHasCard> deckBCards = deckb.getCards();
        Map<Card, Integer> deckAMap = new HashMap<Card, Integer>();
        Map<Card, Integer> deckBMap = new HashMap<Card, Integer>();
        Set<Card> cards = new HashSet<Card>();
        for (DeckHasCard deckHasCard : deckACards) {
            cards.add(deckHasCard.getCard());
            deckAMap.put(deckHasCard.getCard(), deckHasCard.getQuantity());
        }
        for (DeckHasCard deckHasCard : deckBCards) {
            cards.add(deckHasCard.getCard());
            deckBMap.put(deckHasCard.getCard(), deckHasCard.getQuantity());
        }

        // calculate Manhattan distance
        int distance = 0;
        for (Card card : cards) {
            if (!deckAMap.containsKey(card)) {
                distance += deckBMap.get(card);
            } else if (!deckBMap.containsKey(card)) {
                distance += deckAMap.get(card);
            } else {
                distance += Math.abs(deckAMap.get(card)-deckBMap.get(card));
            }
        }

        return distance;
    }
}
