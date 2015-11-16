package com.madarasz.netrunnerstats.helper;

import com.madarasz.netrunnerstats.database.DOs.Card;
import com.madarasz.netrunnerstats.database.DOs.Deck;
import com.madarasz.netrunnerstats.database.DOs.Standing;
import com.madarasz.netrunnerstats.database.DOs.relationships.DeckHasCard;
import com.madarasz.netrunnerstats.database.DRs.StandingRepository;
import com.madarasz.netrunnerstats.helper.comparator.DeckHasCardComparator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

/**
 * Created by madarasz on 11/16/15.
 *
 */
@Component
public class DeckDigest {

    @Autowired
    StandingRepository standingRepository;

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
        result += "<em>" + deck.getIdentity().getTitle() +
                " (" + deck.getIdentity().getCardPack().getName() + ")</em> - "
                + deck.countCards() + " cards";
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
            if (selectedCards.size() > 0) {
                result += "<strong>" + capitalized + " (" + countCards(selectedCards) + ")</strong><br />\n";
            }
            for (DeckHasCard card : selectedCards) {
                Card thecard = card.getCard();
                result += card.getQuantity() + "x " + thecard.getTitle();
                result += " <em>(" + thecard.getCardPack().getName() + ")</em>";

                // display faction cost
                if (!thecard.getFaction_code().equals(factionCode)) {
                    result += " <span class=\"influence inf-" + thecard.getFaction_code() + "\">";
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
            if (selectedCards.size() > 0) {
                result += "<br />\n";
            }
        }


        // tournament and deck links
        if (!deck.getUrl().contains("stimhack")) {
            result += "<br />\n<a href=\"" + deck.getUrl() + "\" target=\"_blank\">" + deck.getUrl() + "</a>";
        }
        Standing standing = standingRepository.findByDeckUrl(deck.getUrl());
        result += "<br>\n<a href=\"" + standing.getTournament().getUrl() + "\" target=\"_blank\">"
                + standing.getTournament().getName() + "</a> - rank: #" + standing.getRank();

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
