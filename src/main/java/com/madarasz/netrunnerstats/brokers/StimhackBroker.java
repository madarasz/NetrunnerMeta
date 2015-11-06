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

    public static final String HTML_INFO_BOX = "div.wc-shortcodes-box";
    public static final String HTML_FIRST_DECK = "div.wc-shortcodes-column-first";
    public static final String HTML_SECOND_DECK = "div.wc-shortcodes-column-last";
    public static final String HTML_LINE_BREAK = "<br class=\"break\">|</p><p>";
    public static final String HTML_TAGS = "span.footer-tags > a";
    public static final String HTML_TITLE = "h1.entry-title";
    public static final String SPLITTER_INFO = ": |</strong>";
    public static final String SLITTER_CARD_NAME = "^\\dx? | •| \\(";
    public static final String SLITTER_DATE = "Date: ";
    public static final String URL_TOURNAMENTS = "http://stimhack.com/tournament-decklists/";

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
        String info = HttpBroker.htmlFromHtml(HTML_INFO_BOX);
        String[] infoparts = info.split(SPLITTER_INFO);
        String playerName = infoparts[1];
        String firstDeck = HttpBroker.htmlFromHtml(HTML_FIRST_DECK);
        String secondDeck = HttpBroker.htmlFromHtml(HTML_SECOND_DECK);

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

        String[] lines = text.split(HTML_LINE_BREAK);
        String decktitle = "";
        String identitytext = "";
        try {
            decktitle = Jsoup.parse(lines[0]).text();
            identitytext = Jsoup.parse(lines[2]).text().split(" \\(")[0];
        } catch (Exception e) {
            System.out.println("ERROR - cannot parse deck title or identity- url: " + url);
        }

        // acoo import case
        if (identitytext.equals("")) {
            decktitle = "";
            identitytext = Jsoup.parse(lines[1]).text().split(" \\(")[0];
        }
        identitytext = regExBroker.sanitizeText(identitytext);

        Deck result = new Deck();
        Card identity = cardRepository.findByTitle(identitytext);
        if (identity == null) {
            identitytext = Jsoup.parse(lines[0]).text().split(" \\(")[0];
            identity = cardRepository.findByTitle(identitytext);
        }
        if (identity == null) {
            System.out.println("ERROR - can't parse identity: " + identitytext);
        }
        result.setIdentity(identity);
        result.setName(decktitle);
        result.setPlayer(playername);
        result.setUrl(url);

        for (String line : lines) {
            line = line.trim();
            // read quantity
            int quantity = regExBroker.getCardQuantity(line);

            if (quantity > 0) {
                // read card
                Card card = null;
                String cardtitle = "";
                String[] chop = Jsoup.parse(line).text().split(SLITTER_CARD_NAME);
                if (chop.length > 1) {
                    cardtitle = regExBroker.sanitizeText(chop[1].trim());
                    card = cardRepository.findByTitle(cardtitle);
                }

                if (card == null) {
                    String code = regExBroker.getSecondQuantity(line);
                    card = cardRepository.findByCode(code);
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
        String titletext = HttpBroker.textFromHtml(HTML_TITLE);
        String name = titletext.split("\\(")[0].trim();
        int playernumber = regExBroker.getNumberFromBeginning(titletext);

        // get date
        Date date = null;
        String datestring = HttpBroker.htmlFromHtml(HTML_INFO_BOX).split(SLITTER_DATE)[1];
        try {
            date = format.parse(datestring); // add date format: MMMM dd, yyyy / mm/dd/yyyy
        } catch (Exception e) {
            System.out.println("ERROR - cannot parse date from: " + datestring + " // tournament url: " + url);
            date = new Date(0);
        }

        // get cardpool
        CardPack cardpool = new CardPack();
        Elements tags = HttpBroker.elementsFromHtml(HTML_TAGS);
        for (Element tag : tags) {
            cardpool = cardPackRepository.findByName(tag.text());
            if (cardpool != null) {
                break;
            }
        }

        Tournament tournament = new Tournament(-1, name, date, cardpool, url, playernumber);
        return tournament;
    }

    public Set<String> getTournamentURLs(String cardpoolName) {
        Set<String> result = new HashSet<String>();
        HttpBroker.parseHtml(URL_TOURNAMENTS);
        Elements rows = HttpBroker.elementsFromHtml("tbody.list > tr");
        for (Element row : rows) {
            if (row.child(0).text().equals(cardpoolName)) {
                result.add(row.child(1).child(0).attr("href"));
            }
        }
        return result;

    }
}
