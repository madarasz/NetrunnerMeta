package com.madarasz.netrunnerstats.brokers;

import com.madarasz.netrunnerstats.DOs.Card;
import com.madarasz.netrunnerstats.DOs.CardPack;
import com.madarasz.netrunnerstats.DOs.Deck;
import com.madarasz.netrunnerstats.DOs.Tournament;
import com.madarasz.netrunnerstats.DRs.CardPackRepository;
import com.madarasz.netrunnerstats.DRs.CardRepository;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Handling acoo.net stuff
 * Created by madarasz on 10/06/15.
 */
@Component
public class AcooBroker {
    public final static String ACOO_URL = "http://www.acoo.net/";
    private final static String JSOUP_DECK_CARDS = "div.deck-display-type > ul > li";
    private final static String JSOUP_TOURNAMENT_DESC = "div.section > p";
    private final static String JSOUP_TOURNAMENT_POOL = "div.section > p > a";
    private final static String JSOUP_TOURNAMENT_DECK = "div.rank-list > table > tbody > tr";
    private final static String JSOUP_TOURNAMENT_LIST = "div.tournament-list > table > tbody > tr";
    private final static String JSOUP_PAGINATION = "div.pagination > a";
    private final static String JSOUP_TITLE = "h1";
    private final static DateFormat format = new SimpleDateFormat("yyyy-MM-dd");

    @Autowired
    RegExBroker regExBroker;

    @Autowired
    CardRepository cardRepository;

    @Autowired
    CardPackRepository cardPackRepository;

    public Deck readDeck(int id) {
        String url = deckUrlFromId(id);
        HttpBroker.parseHtml(url);
        Deck resultDeck = parseDeck(HttpBroker.textsFromHtml(JSOUP_DECK_CARDS));

        String titlebar = HttpBroker.textFromHtml(JSOUP_TITLE);
        String[] titleparts = titlebar.split(", \\d*\\w* | - ");
        try {
            resultDeck.setName(titleparts[2]);
            resultDeck.setPlayer(titleparts[1]);
        } catch (Exception e) {
            System.out.println("ERROR - cannot parse title: " + titlebar + "// deckid: " + id);
            resultDeck.setName(titlebar);
            resultDeck.setPlayer("");
        }
        resultDeck.setUrl(url);

        return resultDeck;
    }

    public String deckUrlFromId(int id) {
        return ACOO_URL + "deck/" + id;
    }

    public String tournamentUrlFromId(int id) {
        return ACOO_URL + "anr-tournament/" + id;
    }

    public Deck parseDeck(ArrayList<String> lines) {

        Deck result = new Deck();
        for (String line : lines) {
            int quantity = regExBroker.getQuantity(line);
            String title = regExBroker.getCardFromLine(line);
            Card card = cardRepository.findByTitle(title);
            if (card == null) {
                System.out.println("ERROR - no such card: " + title);
            }
            if (quantity == 0) {    // identity
                result.setIdentity(card);
            } else {    // other card
                result.hasCard(card, quantity);
            }
        }

        return result;
    }

    public Tournament readTournament(int tournamentId) {
        String url = tournamentUrlFromId(tournamentId);
        HttpBroker.parseHtml(url);
        String titlebar = HttpBroker.textFromHtml(JSOUP_TITLE);
        String[] titleparts = titlebar.split(" \\(|\\)");
        String decription = HttpBroker.textFromHtml(JSOUP_TOURNAMENT_DESC);
        int playerNumber = regExBroker.getNumberFromBeginning(decription);
        Date date = null;
        try {
            date = format.parse(titleparts[1]);
        } catch (Exception e) {
            System.out.println("ERROR - cannot parse date from: " + titlebar + " // tournament id: " + tournamentId);
            date = new Date(0);
        }
        CardPack pool = cardPackRepository.findByName(HttpBroker.textFromHtml(JSOUP_TOURNAMENT_POOL));
        if (pool == null) {
            System.out.println("ERROR - no card pack found: " + HttpBroker.textFromHtml(JSOUP_TOURNAMENT_POOL) + " // tournament id: " + tournamentId);
        }
        return new Tournament(tournamentId, titleparts[0], date, pool, url, playerNumber);
    }

    public Map<Integer, Integer> loadTournamentDeckIds(int tournamentId) {
        Map<Integer, Integer> result = new HashMap<Integer, Integer>();
        String url = tournamentUrlFromId(tournamentId);
        HttpBroker.parseHtml(url);
        Elements rows = HttpBroker.elementsFromHtml(JSOUP_TOURNAMENT_DECK);
        for (Element row : rows) {
            String rankString = row.select("td").first().text();
            int rank = regExBroker.getNumberFromBeginning(rankString);
            Elements decks = row.select("td > a");
            for (Element deck : decks) {
                String[] hrefparts = deck.attr("href").split("/");
                int deckId = Integer.valueOf(hrefparts[2]);
                if (!result.containsKey(deckId)) {  // prioritize "Top results" over "Swiss"
                    result.put(deckId, rank);
                }
            }
        }
        return result;
    }

    public Set<Integer> loadTournamentIdsFromUrl(String url, boolean filterempty) {
        Set<Integer> result = new HashSet<Integer>();
        HttpBroker.parseHtml(url);
        Elements rows = HttpBroker.elementsFromHtml(JSOUP_TOURNAMENT_LIST);
        for (Element row : rows) {
            int decknumber = 0;
            String decknumbertext = row.select("td:eq(5)").first().text();
            try {
                decknumber = Integer.valueOf(decknumbertext);
            } catch (Exception e) {
                System.out.println("ERROR - unable to parse deck number for: " + decknumbertext + "// tournament: " + url);
            }
            if ((!filterempty) || (decknumber > 0)) {
                Element cell = row.select("td").first().select("a").first();
                String[] hrefparts = cell.attr("href").split("/");
                int tournamentId = Integer.valueOf(hrefparts[2]);
                result.add(tournamentId);
            }
        }
        return result;
    }

    public String getTournamentPageNextLink(String url) {
        HttpBroker.parseHtml(url);
        String result = "";
        Elements links = HttpBroker.elementsFromHtml(JSOUP_PAGINATION);
        for (Element link : links) {
            if (link.text().equals("next »")) {
                result = link.attr("href");
                break;
            }
        }
        if (!result.equals("")) {
            result = result.substring(1);
        }
        return result;
    }

}
