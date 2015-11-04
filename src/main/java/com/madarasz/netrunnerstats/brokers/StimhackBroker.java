package com.madarasz.netrunnerstats.brokers;

import com.madarasz.netrunnerstats.DOs.Card;
import com.madarasz.netrunnerstats.DOs.CardPack;
import com.madarasz.netrunnerstats.DOs.Deck;
import com.madarasz.netrunnerstats.DOs.Tournament;
import com.madarasz.netrunnerstats.DRs.CardPackRepository;
import com.madarasz.netrunnerstats.DRs.CardRepository;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.jsoup.nodes.Element;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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

    @Autowired
    CardPackRepository cardPackRepository;

    private final static DateFormat format = new SimpleDateFormat("yyyy-MM-dd");

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
        resultDecks.add(parseDeck(firstDeck, playerName, url + "#1"));
        resultDecks.add(parseDeck(secondDeck, playerName, url + "#2"));

        return resultDecks;
    }

    /**
     * Reads card data for Strimhach deck
     * @param text html text
     * @return deck
     */
    public Deck parseDeck(String text, String playername, String url) {

        String[] lines = text.split("<br class=\"break\">");
        String decktitle = "";
        String identity = "";
        try {
            decktitle = lines[0].split("[<>]")[2];
            identity = lines[2].split("[<>]")[2];
        } catch (Exception e) {
            // TODO: parse different stimhack formats
            System.out.println("ERROR - cannot parse decktitle - url: " + url);
        }


        Deck result = new Deck();
        result.setIdentity(cardRepository.findByTitle(identity));
        result.setName(decktitle);
        result.setPlayer(playername);
        result.setUrl(url);

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

    /**
     * Reads tournament information from Stimhack.
     * @param url Stimhack url
     * @return tournament object
     */
    public Tournament readTournament(String url) {
        // gather info
        HttpBroker.parseHtml(url);
        String titletext = HttpBroker.textFromHtml("h1.entry-title");
        String name = titletext.split("\\(")[0].trim();
        int playernumber = regExBroker.getNumberFromBeginning(titletext);

        // get date
        Date date = null;
        String datestring = HttpBroker.htmlFromHtml("div.wc-shortcodes-box").split("Date: ")[1];
        try {
            date = format.parse(datestring);
        } catch (Exception e) {
            System.out.println("ERROR - cannot parse date from: " + datestring + " // tournament url: " + url);
            date = new Date(0);
        }

        // get cardpool
        CardPack cardpool = new CardPack();
        Elements tags = HttpBroker.elementsFromHtml("span.footer-tags > a");
        for (Element tag : tags) {
            cardpool = cardPackRepository.findByName(tag.text());
            if (cardpool != null) {
                break;
            }
        }

        Tournament tournament = new Tournament(-1, name, date, cardpool, url, playernumber);
        return tournament;
    }
}
