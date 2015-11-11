package com.madarasz.netrunnerstats.DOs.stats.entries;

import com.madarasz.netrunnerstats.DOs.Card;
import com.madarasz.netrunnerstats.DOs.Deck;
import com.madarasz.netrunnerstats.DOs.relationships.DeckHasCard;
import com.madarasz.netrunnerstats.helper.ColorPicker;
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
    private String shortHtmlDigest;
    private String htmlDigest;
    private String digest;

    public DeckInfo() {
    }

    public DeckInfo(Deck deck) {
        this.shortHtmlDigest = shortHtmlDigest(deck);
        this.htmlDigest = htmlDigest(deck);
        this.digest = digest(deck);
    }

    public String getShortHtmlDigest() {
        return shortHtmlDigest;
    }

    public String getHtmlDigest() {
        return htmlDigest;
    }

    public String getDigest() {
        return digest;
    }

    public String digest(Deck deck) {
        String result = deck.getName();
        if (!deck.getPlayer().equals("")) {
            result += " by " + deck.getPlayer();
        }
        result += " (" + deck.countCards() + " cards)";
        return result;
    }

    /**
     * Html output with deck name, player, identity and card count
     * @return html string
     */
    public String shortHtmlDigest(Deck deck) {
        String result = "<strong>" + deck.getName() + "</strong>";
        if (!deck.getPlayer().equals("")) {
            result += " by " + deck.getPlayer();
        }
        result += "<br />\n";
        result += "<em>" + deck.getIdentity().getTitle() + "</em> (" + deck.countCards()
                + " cards)";
        return result;
    }

    public String htmlDigest(Deck deck) {
        String result = shortHtmlDigest(deck) + "<br />\n<br />\n";

        List<String> categories;
        if (deck.getIdentity().getSide_code().equals("runner")) {
            categories = new ArrayList<String>(Arrays.asList("event", "hardware", "icebreaker", "program", "resource"));
        } else {
            categories = new ArrayList<String>(Arrays.asList("agenda", "asset", "operation", "upgrade", "barrier", "code gate", "sentry", "mythic", "trap"));
        }

        Set<DeckHasCard> cards = deck.getCards();
        String factionCode = deck.getIdentity().getFaction_code();
        for (String category : categories) {
            List<DeckHasCard> selectedCards = filterDeck(cards, category);
            String capitalized = category.substring(0, 1).toUpperCase() + category.substring(1);
            result += "<strong>" + capitalized + " ("+ countCards(selectedCards) + ")</strong><br />\n";
            for (DeckHasCard card : selectedCards) {
                Card thecard = card.getCard();
                result += card.getQuantity() + "x " + thecard.getTitle();

                // display faction cost
                if (!thecard.getFaction_code().equals(factionCode)) {
                    result += " <span style=\"color: " + colorFaction(thecard.getFaction_code()) + "\">";
                    int times = thecard.getFactioncost() * card.getQuantity();
                    for (int i = 0; i < times; i++) {
                        result += "•";
                        if ((i+1) % 5 == 0) {
                            result += " ";
                        }
                    }
                    result += "</span>";
                }
                result += "<br />\n";
            }
            result += "<br />\n";
        }

        result += "<br />\n<a href=\"" + deck.getUrl() + "\" target=\"_blank\">" + deck.getUrl() + "</a>";

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

    // TODO: color duplication
    private String colorFaction(String title) {
        switch (title) {
            case "shaper":
                return("#7EAC39");
            case "criminal":
                return("#3962AC");
            case "anarch":
                return("#AC5439");
            case "jinteki":
                return("#b43018");
            case "haas-bioroid":
                return("#804a82");
            case "weyland-consortium":
                return("#4a8272");
            case "nbn":
                return("#A1762B");
            default:
                return("#CCCCCC");
        }
    }
}
