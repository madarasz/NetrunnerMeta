package com.madarasz.netrunnerstats.brokers;

import com.madarasz.netrunnerstats.DOs.Card;
import com.madarasz.netrunnerstats.DOs.CardPack;
import com.madarasz.netrunnerstats.DOs.Deck;
import com.madarasz.netrunnerstats.DOs.Tournament;
import com.madarasz.netrunnerstats.DRs.CardPackRepository;
import com.madarasz.netrunnerstats.DRs.CardRepository;
import org.jsoup.Jsoup;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.jsoup.nodes.Element;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
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
            decktitle = Jsoup.parse(lines[0]).text();
            identity = Jsoup.parse(lines[2]).text().split(" \\(")[0];
        } catch (Exception e) {
            System.out.println("ERROR - cannot parse deck title or identity- url: " + url);
        }

        // acoo import case
        if (identity.equals("")) {
            decktitle = "";
            identity = Jsoup.parse(lines[1]).text().split(" \\(")[0];
        }
        identity = regExBroker.sanitizeText(identity);

        Deck result = new Deck();
        result.setIdentity(cardRepository.findByTitle(identity));
        if (result.getIdentity() == null) {
            System.out.println("ERROR - can't parse identity: " + identity);
        }
        result.setName(decktitle);
        result.setPlayer(playername);
        result.setUrl(url);

        for (String line : lines) {
            line = line.trim();
            // read quantity
            int quantity = regExBroker.getCardQuantity(line);

            if (quantity > 0) {
                // read card
                String code = regExBroker.getSecondQuantity(line);
                Card card = cardRepository.findByCode(code);
                String cardtitle = "";

                if (card == null) {
                    String[] chop = Jsoup.parse(line).text().split("^\\dx? | •| \\(");
                    if (chop.length > 1) {
                        cardtitle = regExBroker.sanitizeText(chop[1]);
                        card = cardRepository.findByTitle(cardtitle);
                    }
                }

                if (card != null) {
//                    System.out.println(String.format("%dx %s", quantity, card.getTitle()));
                    result.hasCard(card, quantity);
                } else {
//                    System.out.println("ERROR - Can't parse card name: " + cardtitle);
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
