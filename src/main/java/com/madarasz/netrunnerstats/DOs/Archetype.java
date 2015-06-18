package com.madarasz.netrunnerstats.DOs;

import com.madarasz.netrunnerstats.DOs.relationships.DeckHasCard;
import com.madarasz.netrunnerstats.helper.CardMapValueComparator;
import java.util.*;

/**
 * Statistics for archetype
 * Created by madarasz on 2015-06-16.
 */
// TODO: refactor as DB component
public class Archetype {

    private String name;
    private Map<Card, Integer> cardcount;   // how many cards overall
    private Map<Card, Integer> cardisused;  // how many decks use the card
    private CardMapValueComparator comparator;
    private int deckcount;
    private Card identity;

    public Archetype() {
        cardcount = new HashMap<Card, Integer>();
        cardisused = new HashMap<Card, Integer>();
        comparator = new CardMapValueComparator(cardcount);
    }

    public Archetype(String name, List<Deck> deckList, Card identity) {
        Map<Card, Integer> ccount = new HashMap<Card, Integer>();
        Map<Card, Integer> dcount = new HashMap<Card, Integer>();
        for (Deck deck : deckList) {
            Set<DeckHasCard> cardSet = deck.getCards();
            for (DeckHasCard deckHasCard : cardSet) {
                Card card = deckHasCard.getCard();
                int quantity = deckHasCard.getQuantity();
                if (ccount.containsKey(card)) {
                    ccount.put(card, ccount.get(card) + quantity);
                    dcount.put(card, dcount.get(card) + 1);
                } else {
                    ccount.put(card, quantity);
                    dcount.put(card, 1);
                }
            }
        }
        this.deckcount = deckList.size();
        this.cardcount = new HashMap<Card, Integer>(ccount);
        this.cardisused = new HashMap<Card, Integer>(dcount);
        this.name = name;
        this.identity = identity;
        this.comparator = new CardMapValueComparator(cardcount);
    }

    /**
     * Returns card usage subset filtered on card type or subtype.
     * If filtered from "program" type, cards with "icebreaker" subtype will be not included.
     * @param filter
     * @return
     */
    public TreeMap<Card, Integer> getOrderedCardSubset(String filter) {
        TreeMap<Card, Integer> sortedmap = new TreeMap<Card, Integer>(comparator);
        if (filter.equals("")) {
            sortedmap.putAll(cardcount);
        } else {
            Iterator iterator = cardcount.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry pair = (Map.Entry) iterator.next();
                int quantity = (Integer) pair.getValue();
                Card card = (Card) pair.getKey();

                // "program" special case, has to filter out icebreakers
                if (filter.equals("program")) {
                    if ((card.getType_code().equals(filter)) && (!card.getSubtype_code().contains("icebreaker"))) {
                        sortedmap.put(card, quantity);
                    }
                } else if ((card.getType_code().equals(filter)) || (card.getSubtype_code().contains(filter))) { // normal filtering
                    sortedmap.put(card, quantity);
                }
            }
        }
        return sortedmap;
    }

    /**
     * Providing statistics for a filtered card set.
     * Stats provided: percentage of deck using, average number of cards if used, average number
     * @param filter filter for card type or containing subtype, empty filter uses all cards
     * @return
     */
    public String getStatsForFilter(String filter) {
        String resultString = "";
        TreeMap<Card, Integer> sortedmap = new TreeMap<Card, Integer>(comparator);
        sortedmap.putAll(getOrderedCardSubset(filter));
        if ((!filter.equals("")) && (sortedmap.size() > 0)) {
            resultString += String.format("\n************************* %s *************************\n", filter);
        }

        Iterator iterator = sortedmap.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry pair = (Map.Entry) iterator.next();
            int quantity = (Integer)pair.getValue();
            Card card = (Card)pair.getKey();
            int usage = cardisused.get(card);
            resultString += String.format("%30s - %3.0f%% - AIU: %4.2f - A: %4.2f\n", card.toString(), (float)usage/deckcount*100, (float)quantity/usage, (float)quantity/deckcount);
        }
        return resultString;
    }

    public String getStandardStats() {
        String resultString = String.format("------------- Archetype %s (%d decks) -------------\n", name, deckcount);
        ArrayList<String> categories;
        if (identity.getSide_code().equals("runner")) {
            categories = new ArrayList<String>(Arrays.asList("event", "hardware", "icebreaker", "program", "resource"));
        } else {
            categories = new ArrayList<String>(Arrays.asList("agenda", "asset", "operation", "upgrade", "barrier", "code gate", "sentry", "mythic", "trap"));
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
