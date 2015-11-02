package com.madarasz.netrunnerstats.brokers;

import com.madarasz.netrunnerstats.DOs.Card;
import com.madarasz.netrunnerstats.DOs.Deck;
import com.madarasz.netrunnerstats.DRs.CardPackRepository;
import com.madarasz.netrunnerstats.DRs.CardRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.jsoup.nodes.Element;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

/**
 * Handling Stimhack stuff
 * Created by madarasz on 2015.09.06.
 */
@Component
public class StimhackBroker {

    @Autowired
    RegExBroker regExBroker;

    @Autowired
    CardRepository cardRepository;

    /**
     * Reads Deck information from Acoo. Also adds deck metadata.
     * @param url url of deck
     * @return deck
     */
    public Set<Deck> readDeck(String url) {
        HttpBroker.parseHtml(url);
        String info = HttpBroker.htmlFromHtml("div.wc-shortcodes-box");
        String[] infoparts = info.split(": |</strong>");
        String playerName = infoparts[1];
        String firstDeck = HttpBroker.htmlFromHtml("div.wc-shortcodes-column-first");
        String secondDeck = HttpBroker.htmlFromHtml("div.wc-shortcodes-column-last");

        Set<Deck> resultDecks = new HashSet<Deck>();
        resultDecks.add(parseDeck(firstDeck));
        resultDecks.add(parseDeck(secondDeck));

        for (Deck deck : resultDecks) {
            deck.setUrl(url);
            deck.setPlayer(playerName);
        }

        return resultDecks;
    }

    /**
     * Reads card data for Strimhach deck
     * @param text html text
     * @return deck
     */
    public Deck parseDeck(String text) {

        String[] lines = text.split("<br class=\"break\">");

        String decktitle = lines[0].split("[<>]")[2];
        String identity = lines[2].split("[<>]")[2];

        System.out.println(String.format("%s --- %s", decktitle, identity));

        Deck result = new Deck();
        result.setIdentity(cardRepository.findByTitle(identity));
        result.setName(decktitle);
        for (String line : lines) {
            int quantity = regExBroker.getQuantity(line);
            String code = regExBroker.getSecondQuantity(line);
            if ((quantity > 0) && (code.length() > 0)) {
                Card card = cardRepository.findByCode(code);
//                System.out.println(String.format("%dx %s", quantity, card.getTitle()));
                if (card != null) {
                    result.hasCard(card, quantity);
                } else {
                    System.out.println("ERROR - no such card code: " + code);
                }
            }
        }

        return result;
    }
}
