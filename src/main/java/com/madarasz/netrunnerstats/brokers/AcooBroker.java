package com.madarasz.netrunnerstats.brokers;

import com.madarasz.netrunnerstats.DOs.Deck;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

/**
 * Created by madarasz on 10/06/15.
 */
@Component
public class AcooBroker {
    private final static String ACOO_URL = "http://www.acoo.net/";
    private final static String JSOUP_CARDS = "div.deck-display-type > ul > li";

    @Autowired
    RegExBroker regExBroker;

    public Deck readDeck(int id) {
        HttpBroker.parseHtml(ACOO_URL + "deck/" + id);
        return parseDeck(HttpBroker.textsFromHtml(JSOUP_CARDS));
    }

    public Deck parseDeck(ArrayList<String> lines) {
        for (String line : lines) {
            int quantity = regExBroker.getQuantity(line);
            String title = regExBroker.getTitle(line);
            System.out.println(quantity + title);

        }
        return new Deck();
    }
}
