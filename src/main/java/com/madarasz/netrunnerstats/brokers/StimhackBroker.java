package com.madarasz.netrunnerstats.brokers;

import com.madarasz.netrunnerstats.database.DOs.Card;
import com.madarasz.netrunnerstats.database.DOs.CardPack;
import com.madarasz.netrunnerstats.database.DOs.Deck;
import com.madarasz.netrunnerstats.database.DOs.Tournament;
import com.madarasz.netrunnerstats.database.DRs.CardPackRepository;
import com.madarasz.netrunnerstats.database.DRs.CardRepository;
import com.madarasz.netrunnerstats.helper.TitleGuesser;
import org.jsoup.Jsoup;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.jsoup.nodes.Element;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * Handling Stimhack stuff
 * Created by madarasz on 2015.09.06.
 */
@Component
public class StimhackBroker {

    private static final Logger logger = LoggerFactory.getLogger(StimhackBroker.class);

    private static final String HTML_INFO_BOX = "div.wc-shortcodes-box";
    private static final String HTML_FIRST_DECK = "div.wc-shortcodes-column-first";
    private static final String HTML_SECOND_DECK = "div.wc-shortcodes-column-last";
    private static final String HTML_LINE_BREAK = "<br class=\"break\">|</p><p>";
    private static final String HTML_TAGS = "span.footer-tags > a";
    private static final String HTML_TITLE = "h1.entry-title";
    private static final String SPLITTER_INFO = ": |</strong>";
    private static final String SLITTER_CARD_NAME = "^\\dx? | •| \\(| ●|•| ☆";
    private static final String SLITTER_DATE = "Date: ";
    private static final String URL_TOURNAMENTS = "http://stimhack.com/tournament-decklists/";

    @Autowired
    RegExBroker regExBroker;

    @Autowired
    CardRepository cardRepository;

    @Autowired
    CardPackRepository cardPackRepository;
    
    @Autowired
    HttpBroker httpBroker;

    @Autowired
    TitleGuesser titleGuesser;

    /**
     * Reads Deck information from Stimhack. Also adds deck metadata.
     * @param url url of deck
     * @return deck
     */
    public Set<Deck> readDeck(String url) {
        httpBroker.parseHtml(url);
        String info = httpBroker.htmlFromHtml(HTML_INFO_BOX);
        String[] infoparts = info.split(SPLITTER_INFO);
        String playerName = infoparts[1];
        String firstDeck = httpBroker.htmlFromHtml(HTML_FIRST_DECK);
        String secondDeck = httpBroker.htmlFromHtml(HTML_SECOND_DECK);

        Set<Deck> resultDecks = new HashSet<>();
        resultDecks.add(parseDeck(firstDeck, playerName, url + "#1"));
        resultDecks.add(parseDeck(secondDeck, playerName, url + "#2"));

        return resultDecks;
    }

    /**
     * Reads card data for Strimhack deck
     * @param text html text
     * @return deck
     */
    public Deck parseDeck(String text, String playername, String url) {

        String[] lines = text.split(HTML_LINE_BREAK);
        String decktitle = Jsoup.parse(lines[0]).text();
        Card identity = null;
        String identitytext;
        int i;
        for (i = 0; i < 3; i++) {
            identitytext = titleGuesser.alternateTitle(
                    regExBroker.sanitizeText(Jsoup.parse(lines[i]).text().split(" \\(")[0]));
            identity = cardRepository.findByTitle(identitytext);
            if (identity != null) {
                break;
            }
        }
        if (i < 2) {
            decktitle = "";
        }

        Deck result = new Deck();
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

                if (card == null) {
                    card = cardRepository.findByTitle(titleGuesser.alternateTitle(cardtitle));
                }

                // results
                if (card != null) {
                    logger.trace(String.format("%dx %s", quantity, card.getTitle()));
                    result.hasCard(card, quantity);
                } else {
                    logger.trace("ERROR - Can't parse card name: " + cardtitle);
                }
            }
        }

        // last chance to get identity via tags
        if (identity == null) {
            String side = result.getCards().iterator().next().getCard().getSide_code();
            Elements tags = httpBroker.elementsFromHtml(HTML_TAGS);
            for (Element tag : tags) {
                Card card = cardRepository.findByTitle(titleGuesser.alternateTitle(tag.text()));
                if ((card != null) && (card.getType_code().equals("identity")) && (card.getSide_code().equals(side))) {
                    identity = card;
                    logger.warn("Guessing identity from tags: " + identity.getTitle());
                    break;
                }
            }
        }
        if (identity == null) {
            logger.error("ERROR - can't parse identity: " + url);
        }
        result.setIdentity(identity);
        return result;
    }

    /**
     * Reads tournament information from Stimhack.
     * @param url Stimhack url
     * @return tournament object
     */
    public Tournament readTournament(String url) {
        // gather info
        httpBroker.parseHtml(url);
        String titletext = httpBroker.textFromHtml(HTML_TITLE);
        String name = titletext.split("\\(")[0].trim();
        int playernumber = regExBroker.getNumberFromBeginning(titletext);

        // get date
        String datestring = httpBroker.htmlFromHtml(HTML_INFO_BOX).split(SLITTER_DATE)[1];
        Date date = regExBroker.parseDate(datestring);

        // get cardpool
        CardPack cardpool = new CardPack();
        Elements tags = httpBroker.elementsFromHtml(HTML_TAGS);
        for (Element tag : tags) {
            cardpool = cardPackRepository.findByName(tag.text());

            if (tag.text().equals("Data & Destiny")) {
                cardpool = cardPackRepository.findByName("Data and Destiny");
            }

            if (cardpool != null) {
                break;
            }
        }

        return new Tournament(-1, name, date, cardpool, url, playernumber);
    }

    public Set<String> getTournamentURLs(String cardpoolName) {
        Set<String> result = new HashSet<>();
        httpBroker.parseHtml(URL_TOURNAMENTS);
        Elements rows = httpBroker.elementsFromHtml("tbody.list > tr");
        for (Element row : rows) {
            if ((cardpoolName.equals("")) || (row.child(0).text().equals(cardpoolName))) {
                result.add(row.child(1).child(0).attr("href"));
            }
        }
        return result;

    }
}
