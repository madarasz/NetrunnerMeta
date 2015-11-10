package com.madarasz.netrunnerstats.DOs.stats;

import com.madarasz.netrunnerstats.DOs.Deck;
import com.madarasz.netrunnerstats.DOs.relationships.DeckHasCard;
import com.madarasz.netrunnerstats.helper.comparator.DeckHasCardComparator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

/**
 * Created by madarasz on 11/10/15.
 * For deck information digest.
 */
public class DeckInfo {
    private String title;
    private String url;
    private String player;
    private String htmlDigest;

    public DeckInfo() {
    }

    public DeckInfo(Deck deck) {
        title = deck.getName();
        url = deck.getUrl();
        player = deck.getPlayer();
        htmlDigest = htmlDigest(deck);
    }

    public String getTitle() {
        return title;
    }

    public String getUrl() {
        return url;
    }

    public String getPlayer() {
        return player;
    }

    public String getHtmlDigest() {
        return htmlDigest;
    }


    public String htmlDigest(Deck deck) {
        String result = "";
        List<String> categories;
        if (deck.getIdentity().getSide_code().equals("runner")) {
            categories = new ArrayList<String>(Arrays.asList("event", "hardware", "icebreaker", "program", "resource"));
        } else {
            categories = new ArrayList<String>(Arrays.asList("agenda", "asset", "operation", "upgrade", "barrier", "code gate", "sentry", "mythic", "trap"));
        }

        Set<DeckHasCard> cards = deck.getCards();
        for (String category : categories) {
            List<DeckHasCard> selectedCards = filterDeck(cards, category);
            String capitalized = category.substring(0, 1).toUpperCase() + category.substring(1);
            result += "<strong>" + capitalized + " ("+ countCards(selectedCards) + ")</strong><br />\n";
            for (DeckHasCard card : selectedCards) {
                result += card.getQuantity() + "x " + card.getCard().getTitle() + "<br />\n";
            }
        }
        return result;
    }

    private List<DeckHasCard> filterDeck(Set<DeckHasCard> cards, String typeFilter) {
        List<DeckHasCard> result = new ArrayList<DeckHasCard>();
        for (DeckHasCard card : cards) {
            if (((card.getCard().getType_code().contains(typeFilter)) ||
                    (card.getCard().getSubtype_code().contains(typeFilter))) &&
                    ((!typeFilter.equals("program")) || (!card.getCard().getSubtype_code().contains("icebreaker")))){
                result.add(card);
            }
        }
        DeckHasCardComparator comparator = new DeckHasCardComparator();
        result.sort(comparator);
        return result;
    }

    private int countCards(List<DeckHasCard> cards) {
        int result = 0;
        for (DeckHasCard card : cards) {
            result += card.getQuantity();
        }
        return result;
    }
}
