package com.madarasz.netrunnerstats.DOs;

import com.madarasz.netrunnerstats.DOs.relationships.DeckHasCard;
import com.madarasz.netrunnerstats.helper.CardMapValueComparator;
import java.util.*;

/**
 * Statistics for archetype
 * Created by madarasz on 2015-06-16.
 */
public class Archetype {

    private String name;
    // TODO: model
    private Map<Card, Integer> cards;
    private CardMapValueComparator comparator;
    private int deckcount;
    private Card identity;

    public Archetype() {
        cards = new HashMap<Card, Integer>();
        comparator = new CardMapValueComparator(cards);
    }

    public Archetype(String name, List<Deck> deckList, Card identity) {
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
        this.deckcount = deckList.size();
        this.cards = stats;
        this.name = name;
        this.identity = identity;
        this.comparator = new CardMapValueComparator(cards);
    }

    public TreeMap<Card, Integer> getOrderedCardSubset(String filter) {
        TreeMap<Card, Integer> sortedmap = new TreeMap<Card, Integer>(comparator);
        if (filter.equals("")) {
            sortedmap.putAll(cards);
        } else {
            Iterator iterator = cards.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry pair = (Map.Entry) iterator.next();
                int quantity = (Integer) pair.getValue();
                Card card = (Card) pair.getKey();
                if ((card.getType_code().equals(filter)) || (card.getSubtype_code().contains(filter))) {
                    sortedmap.put(card, quantity);
                }
            }
        }
        return sortedmap;
    }

    public String getStatsForFilter(String filter) {
        String resultString = "";
        if (!filter.equals("")) {
            resultString += String.format("\n*** %s *** \n", filter);
        }
        TreeMap<Card, Integer> sortedmap = new TreeMap<Card, Integer>(comparator);
        sortedmap.putAll(getOrderedCardSubset(filter));
        Iterator iterator = sortedmap.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry pair = (Map.Entry) iterator.next();
            int quantity = Integer.valueOf((Integer)pair.getValue());
            Card card = (Card)pair.getKey();
            resultString += String.format("%s: %d\n", card, quantity);
        }
        return resultString;
    }

    public String getStandardStats() {
        String resultString = String.format("--- Archetype %s (%d decks) ---\n", name, deckcount);
        ArrayList<String> categories;
        if (identity.getSide_code().equals("runner")) {
            categories = new ArrayList<String>(Arrays.asList("event", "hardware", "icebreaker", "program", "resource"));
        } else {
            categories = new ArrayList<String>(Arrays.asList("agenda", "asset", "ice", "operation", "upgrade"));
        }
        for (String category: categories) {
            resultString += getStatsForFilter(category);
        }
        return resultString;
    }

    @Override
    public String toString() {
        return getStandardStats();
    }
}
