package com.madarasz.netrunnerstats.brokers;

import com.madarasz.netrunnerstats.database.DOs.*;
import com.madarasz.netrunnerstats.database.DRs.AdminDataRepository;
import com.madarasz.netrunnerstats.database.DRs.CardPackRepository;
import com.madarasz.netrunnerstats.database.DRs.CardRepository;
import com.madarasz.netrunnerstats.database.DRs.DeckRepository;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * Handling acoo.net stuff
 * Created by madarasz on 10/06/15.
 */
@Component
public class ABRBroker {

    private static final Logger logger = LoggerFactory.getLogger(ABRBroker.class);

    public final String URL_API_TOURNAMENTS = "https://alwaysberunning.net/api/tournaments?approved=1&concluded=1&recur=0&cardpool=";
    public final String URL_API_ENTRIES = "https://alwaysberunning.net/api/entries?id=";
    public final String URL_TOURNAMENT = "https://www.alwaysberunning.net/tournaments/";

    @Autowired
    RegExBroker regExBroker;

    @Autowired
    CardRepository cardRepository;

    @Autowired
    CardPackRepository cardPackRepository;

    @Autowired
    DeckRepository deckRepository;
    
    @Autowired
    HttpBroker httpBroker;

    @Autowired
    NetrunnerDBBroker netrunnerDBBroker;

    @Autowired
    AdminDataRepository adminDataRepository;


    public Set<Tournament> getTournamentIDsForPack(String pack) {
        CardPack cardPack = cardPackRepository.findByCode(pack);
        Set<Tournament> result = new HashSet<>();
        if (cardPack == null) {
            logger.warn("No such cardpack code: " + pack);
            return result;
        }

        JSONArray data = new JSONArray();

        try {
            data = new JSONObject(httpBroker.readFromUrl(URL_API_TOURNAMENTS + pack, true))
                    .getJSONArray("input");
        } catch (Exception ex) {
            logger.error("Could not read ABR tournaments", ex);
        }

        for (int i = 0; i < data.length(); i++) {
            JSONObject tournamentData = data.getJSONObject(i);

            if (tournamentData.getString("format").equals("standard")) {

                Date date = new Date();
                try {
                    date = regExBroker.parseDate(tournamentData.getString("date"));
                } catch (Exception ex) {
                    logger.warn("Couldn't parse date from: " + tournamentData.getString("date"));
                }

                Tournament tournament = new Tournament(100000 + tournamentData.getInt("id"),
                        tournamentData.getString("title"), date, cardPack, URL_TOURNAMENT + tournamentData.getInt("id"),
                        tournamentData.getInt("players_count"));

                result.add(tournament);

            } else {
                logger.info("Skipping non-standard tournament: " + tournamentData.getString("title"));
            }
        }
        return result;
    }


    public Set<Standing> getStandings(Tournament tournament) {
        Set<Standing> result = new HashSet<>();

        JSONArray data = new JSONArray();

        try {
            data = new JSONObject(httpBroker.readFromUrl(URL_API_ENTRIES + (tournament.getId() - 100000), true))
                    .getJSONArray("input");
        } catch (Exception ex) {
            logger.error("Cannot download standings/decks for: " + tournament.toString());
        }

        for (int i = 0; i < data.length(); i++) {
            JSONObject entryData = data.getJSONObject(i);
            Card runnerID = cardRepository.findByCode(entryData.getString("runner_deck_identity_id"));
            Card corpID = cardRepository.findByCode(entryData.getString("corp_deck_identity_id"));
            netrunnerDBBroker.updateCardWithCore2(runnerID);
            netrunnerDBBroker.updateCardWithCore2(corpID);
            int rank;
            try {
                rank = entryData.getInt("rank_top");
            } catch (Exception ex) {
                rank = 0;
            }
            if (rank == 0) {
                rank = entryData.getInt("rank_swiss");
            }

            Deck runnerDeck = getDeckFromDeckURL(entryData.getString("runner_deck_url"));
            Deck corpDeck = getDeckFromDeckURL(entryData.getString("corp_deck_url"));

            if (runnerID.getCardPack().getCyclenumber() != 0) {
                if (runnerDeck == null) {
                    result.add(new Standing(tournament, rank, runnerID, rank <= tournament.getPlayerNumber() * 0.3, true));
                } else {
                    result.add(new Standing(tournament, rank, runnerID, rank <= tournament.getPlayerNumber() * 0.3, true, runnerDeck));
                }
            }
            if (corpID.getCardPack().getCyclenumber() != 0) {
                if (corpDeck == null) {
                    result.add(new Standing(tournament, rank, corpID, rank <= tournament.getPlayerNumber() * 0.3, false));
                } else {
                    result.add(new Standing(tournament, rank, corpID, rank <= tournament.getPlayerNumber() * 0.3, false, corpDeck));
                }
            }
        }

        return result;
    }

    private Deck getDeckFromDeckURL(String url) {
        if (url.length() == 0) {
            return null;
        }
        if (url.contains("https://netrunnerdb.com/en/decklist/")) {
            return netrunnerDBBroker.readDeck(Integer.parseInt(url.substring(url.lastIndexOf("/") + 1)));
        } else {
            if (url.contains("https://netrunnerdb.com/en/deck/view/")) {
                return netrunnerDBBroker.readDeck(Integer.parseInt(url.substring(url.lastIndexOf("/") + 1)), false);
            }
        }
        return null;

    }
}
