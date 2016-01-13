package com.madarasz.netrunnerstats.helper;

import com.madarasz.netrunnerstats.database.DOs.Card;
import com.madarasz.netrunnerstats.database.DOs.Deck;
import com.madarasz.netrunnerstats.database.DOs.Standing;
import com.madarasz.netrunnerstats.database.DOs.Tournament;
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

    private static final String[] TYPES_RUNNER = new String[]{"event", "hardware", "icebreaker", "program", "resource"};
    private static final String[] TYPES_CORP = new String[]{"agenda", "asset", "operation", "upgrade", "barrier", "code gate", "sentry", "mythic", "trap"};

    @Autowired
    StandingRepository standingRepository;

    public String digest(Deck deck) {
        String result = deck.getName();
        if (!deck.getPlayer().equals("")) {
            result += " by " + deck.getPlayer();
        }
        result += " (" + deck.countCards() + " cards)\n";

        Standing standing = standingRepository.findByDeckUrl(deck.getUrl());
        Tournament tournament = standing.getTournament();
        result += "#" + standing.getRank() + " / " + tournament.getPlayerNumber() + " at " + tournament.getName();
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
                " (" + deck.getIdentity().getCardPack().getName() + ")</em>";
        return result;
    }

    public String htmlDigest(Deck deck) {
        String result = shortHtmlDigest(deck) + "<br />\n<br />\n";

        List<String> categories;
        if (deck.getIdentity().getSide_code().equals("runner")) {
            categories = new ArrayList<>(Arrays.asList(TYPES_RUNNER));
        } else {
            categories = new ArrayList<>(Arrays.asList(TYPES_CORP));
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
                result += card.getQuantity() + "x ";
                result += "<a href=\"/Cards/" + thecard.getTitle() + "\">" + thecard.getTitle() + "</a>";
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

        // card count and up to
        result += deck.countCards() + " cards, up to: <em>" + deck.getUpto() + "</em><br />\n";

        // tournament and deck links
        if (!deck.getUrl().contains("stimhack")) {
            result += "<a href=\"" + deck.getUrl() + "\" target=\"_blank\">" + deck.getUrl() + "</a><br />\n";
        }
        Standing standing = standingRepository.findByDeckUrl(deck.getUrl());
        Tournament tournament = standing.getTournament();
        result += "<a href=\"" + tournament.getUrl() + "\" target=\"_blank\">"
                + tournament.getName() + "</a> - rank: #" + standing.getRank() + " / " + tournament.getPlayerNumber();

        return result;
    }

    /**
     * Creates a deck link html code from Deck object.
     * Used on the card statistics page.
     * @param deck Deck object
     * @return deck link string
     */
    public String getDeckLink(Deck deck) {
        String result;
        Standing standing = standingRepository.findByDeckUrl(deck.getUrl());
        Tournament tournament = standing.getTournament();
        result = "<a href=\"" + deck.getUrl() + "\" rel=\"nofollow\" target=\"_blank\">";
        if (deck.getName().equals("")) {
            result += "unnamed";
        } else {
            result += deck.getName();
        }
        result += "</a>";
        result += " by <em>" + deck.getPlayer() + "</em>";
        result += " (#" + standing.getRank() + " / " + tournament.getPlayerNumber() + " at " + tournament.getName() + ")";
        return result;
    }

    private List<DeckHasCard> filterDeck(Set<DeckHasCard> cards, String typeFilter) {
        List<DeckHasCard> result = new ArrayList<>();
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
