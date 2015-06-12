package com.madarasz.netrunnerstats.brokers;

import com.madarasz.netrunnerstats.DOs.Card;
import com.madarasz.netrunnerstats.DOs.CardPack;
import com.madarasz.netrunnerstats.DOs.Deck;
import com.madarasz.netrunnerstats.DOs.Tournament;
import com.madarasz.netrunnerstats.DRs.CardPackRepository;
import com.madarasz.netrunnerstats.DRs.CardRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * Handling acoo.net stuff
 * Created by madarasz on 10/06/15.
 */
@Component
public class AcooBroker {
    private final static String ACOO_URL = "http://www.acoo.net/";
    private final static String JSOUP_DECK_CARDS = "div.deck-display-type > ul > li";
    private final static String JSOUP_TOURNAMENT_POOL = "div.section > p > a";
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
        String[] titleparts = titlebar.split(", \\d*\\w* | - ");    // TODO: th place, tournament
        resultDeck.setName(titleparts[2]);
        resultDeck.setPlayer(titleparts[1]);
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
        String[] titleparts = titlebar.split(" (|)");
        Date date = null;
        try {
            date = format.parse(titleparts[1]);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        CardPack pool = cardPackRepository.findByName(HttpBroker.textFromHtml(JSOUP_DECK_CARDS));
//        return new Tournament(tournamentId, titleparts[0], date, pool, url);
        return new Tournament();
    }

    public ArrayList<Integer> loadTournamentDeckIds(int tournamentId) {
        ArrayList<Integer> result = new ArrayList<Integer>();
        String url = tournamentUrlFromId(tournamentId);
        HttpBroker.parseHtml(url);
        String titlebar = HttpBroker.textFromHtml(JSOUP_TITLE);
        String[] titleparts = titlebar.split(" (|)");
        return result;
    }

    public Set<Deck> loadTourmanetDecks(ArrayList<Integer> deckIds) {
        Set<Deck> result = new HashSet<Deck>();
        for (Integer deckId : deckIds) {
            result.add(readDeck(deckId.intValue()));
        }
        return result;
    }
}
