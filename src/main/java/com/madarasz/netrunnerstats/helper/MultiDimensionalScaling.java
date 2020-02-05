package com.madarasz.netrunnerstats.helper;

import com.madarasz.netrunnerstats.database.DOs.Card;
import com.madarasz.netrunnerstats.database.DOs.Deck;
import com.madarasz.netrunnerstats.database.DOs.relationships.DeckHasCard;
import mdsj.MDSJ;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * Created by madarasz on 2015.07.10..
 * Calculates multidimensional scaling for decks
 */
@Component
public class MultiDimensionalScaling {

    public double getDeckDistance(Deck decka, Deck deckb) {
        String factionA = decka.getIdentity().getFaction_code();
        String factionB = deckb.getIdentity().getFaction_code();
        double influenceSensitivity = 1.2; // cards costing influence result in bigger distance
        if (!factionA.equals(factionB)) {
            influenceSensitivity = 1;
        }

        // get all cards
        Set<DeckHasCard> deckACards = decka.getCards();
        Set<DeckHasCard> deckBCards = deckb.getCards();
        Map<Card, Integer> deckAMap = new HashMap<>();
        Map<Card, Integer> deckBMap = new HashMap<>();
        Set<Card> cards = new HashSet<>();
        for (DeckHasCard deckHasCard : deckACards) {
            cards.add(deckHasCard.getCard());
            deckAMap.put(deckHasCard.getCard(), deckHasCard.getQuantity());
        }
        for (DeckHasCard deckHasCard : deckBCards) {
            cards.add(deckHasCard.getCard());
            deckBMap.put(deckHasCard.getCard(), deckHasCard.getQuantity());
        }

        // calculate Manhattan distance
        double distance = 0;
        for (Card card : cards) {
            int difference;
            if (!deckAMap.containsKey(card)) {
                difference = deckBMap.get(card);
            } else if (!deckBMap.containsKey(card)) {
                difference = deckAMap.get(card);
            } else {
                difference = Math.abs(deckAMap.get(card)-deckBMap.get(card));
            }

            // calculate additional influence sensitivity
            if (card.getFaction_code().equals(factionA)) {
                distance += difference;
            } else {
                distance += difference * Math.pow(influenceSensitivity, card.getFactioncost());
            }
        }

        return distance;
    }

    public double[][] getDistanceMatrix(ArrayList<Deck> decks) {
        int size = decks.size();
        double[][] result = new double[size][size];
        int i = 0;
        for (Deck deck1 : decks) {
            int u = 0;
            for (Deck deck2 : decks) {
                result[i][u] = getDeckDistance(deck1, deck2);
                u++;
            }
            i++;
        }
        return result;
    }

    public double[][] calculateMDS(double[][] input) {
        return MDSJ.classicalScaling(input);
    }
}
