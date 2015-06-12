package com.madarasz.netrunnerstats.brokers;

import com.madarasz.netrunnerstats.DOs.Card;
import com.madarasz.netrunnerstats.DOs.Deck;
import com.madarasz.netrunnerstats.DRs.CardRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.util.ArrayList;

/**
 * Handling acoo.net stuff
 * Created by madarasz on 10/06/15.
 */
@Component
public class AcooBroker {
    private final static String ACOO_URL = "http://www.acoo.net/";
    private final static String JSOUP_CARDS = "div.deck-display-type > ul > li";
    private final static String JSOUP_TITLE = "h1";

    @Autowired
    RegExBroker regExBroker;

    @Autowired
    CardRepository cardRepository;

    public Deck readDeck(int id) {
        String url = ACOO_URL + "deck/" + id;
        HttpBroker.parseHtml(url);
        Deck resultDeck = parseDeck(HttpBroker.textsFromHtml(JSOUP_CARDS));

        String titlebar = HttpBroker.textFromHtml(JSOUP_TITLE);
        String[] titleparts = titlebar.split(", \\d*\\w* | - ");    // TODO: th place, tournament
        resultDeck.setName(titleparts[2]);
        resultDeck.setPlayer(titleparts[1]);
        resultDeck.setUrl(url);

        return resultDeck;
    }

    public Deck parseDeck(ArrayList<String> lines) {

        Deck result = new Deck();
        for (String line : lines) {
            int quantity = regExBroker.getQuantity(line);
            String title = regExBroker.getCardFromLine(line);
            Card card = cardRepository.findByTitle(title);
            if (quantity == 0) {    // identity
                result.setIdentity(card);
            } else {    // other card
                result.hasCard(card, quantity);
            }
        }

        return result;
    }
}
