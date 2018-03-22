package com.madarasz.netrunnerstats.brokers;

import com.madarasz.netrunnerstats.database.DOs.*;
import com.madarasz.netrunnerstats.database.DRs.*;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

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
    public final String URL_MATCH_JSON = "https://alwaysberunning.net/tjsons/";

    @Autowired
    RegExBroker regExBroker;

    @Autowired
    CardRepository cardRepository;

    @Autowired
    CardPackRepository cardPackRepository;

    @Autowired
    DeckRepository deckRepository;

    @Autowired
    TournamentRepository tournamentRepository;
    
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
            runnerID = netrunnerDBBroker.updateCardWithCore2(runnerID);
            corpID = netrunnerDBBroker.updateCardWithCore2(corpID);
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

    public List<Match> getMatchesForTournament(int tournamentID) {

        List<Match> results = new ArrayList<>();

        // find related tournament
        Tournament tournament = tournamentRepository.findByUrl(URL_TOURNAMENT + tournamentID);
        if (tournament == null) {
            logger.error("No ABR tournament for ID: " + tournamentID);
            return results;
        }

        // open NRTM JSON
        JSONObject data;
        String rawdata;
        rawdata = httpBroker.readFromUrl(URL_MATCH_JSON + tournamentID + ".json", false);
        if (rawdata.length() == 0 || rawdata.charAt(0) != '{') {
            logger.trace("Could not read NRTM JSON: " + URL_MATCH_JSON + tournamentID + ".json");
            return results;
        } else {
            logger.info("Parsing matches for: " + tournament.getName());
        }

        data = new JSONObject(rawdata);

        // parse players
        JSONArray players;
        try {
            players = data.getJSONArray("players");
        } catch (Exception e) {
            logger.error("Cannot parse match players for: " + tournament.getName());
            return results;
        }
        List<Card> corps = new ArrayList<>();
        List<Card> runners = new ArrayList<>();
        Card runnerBlank = cardRepository.findByCode("00006");
        Card corpBlank = cardRepository.findByCode("00005");
        int maxid = players.length();
        for (int i = 0; i < players.length(); i++) {
            if (players.getJSONObject(i).getInt("id") > maxid) {
                maxid = players.getJSONObject(i).getInt("id");
            }
        }
        for (int i = 0; i < maxid; i++) {
            corps.add(corpBlank);
            runners.add(runnerBlank);
        }

        for (int i = 0; i < players.length() ; i++) {
            JSONObject player = players.getJSONObject(i);
            Card corpID;
            Card runnerID;
            try {
                corpID = cardRepository.findByTitleLike(".*" + addSpecialCharsToIDs(player.getString("corpIdentity")) + ".*");
                if (corpID == null) {
                    logger.error("Cannot parse ID for: " + player.getString("corpIdentity"));
                    return results;
                }
                runnerID = cardRepository.findByTitleLike(".*" + addSpecialCharsToIDs(player.getString("runnerIdentity")) + ".*");
                if (runnerID == null) {
                    logger.error("Cannot parse ID for: " + player.getString("runnerIdentity"));
                    return results;
                }
            } catch (Exception ex) {
                logger.error("Corp or player identity cannot be parsed");
                return results;
            }

            corps.set(player.getInt("id")-1, corpID);
            runners.set(player.getInt("id")-1, runnerID);
        }

        // parse matches
        JSONArray rounds;
        try {
            rounds = data.getJSONArray("rounds");
        } catch (Exception e) {
            logger.error("Cannot parse match rounds for: " + tournament.getName());
            return results;
        }

        for (int roundNum = 0; roundNum < rounds.length() ; roundNum++) {
            JSONArray round = rounds.getJSONArray(roundNum);
            for (int u = 0; u < round.length(); u++) {
                JSONObject match = round.getJSONObject(u);
                JSONObject player1 = match.getJSONObject("player1");
                JSONObject player2 = match.getJSONObject("player2");

                // only if there were no intentional draw and not a BYE
                if (player1.get("id") != JSONObject.NULL && player2.get("id") != JSONObject.NULL &&
                        !match.getBoolean("intentionalDraw") && player1.getInt("id") > 0 && player2.getInt("id") > 0) {

                    Card player1Corp = corps.get(player1.getInt("id")-1);
                    Card player2Corp = corps.get(player2.getInt("id")-1);
                    Card player1Runner = runners.get(player1.getInt("id")-1);
                    Card player2Runner = runners.get(player2.getInt("id")-1);

                    if (!match.getBoolean("eliminationGame")) {
                        // swiss

                        // if there are points
                        if (player1.getInt("corpScore") + player2.getInt("corpScore") +
                                player1.getInt("runnerScore") + player2.getInt("runnerScore") > 0) {

                            // first match: player 1 runner vs player 2 corp
                            Match matchABR1;
                            boolean tie = player1.getInt("runnerScore") == 1 && player2.getInt("corpScore") == 1;
                            if (tie) {
                                matchABR1 = new Match(tournament, player1Runner, player2Corp,
                                        false, true, false, match.getInt("table"), roundNum);
                            } else {
                                boolean timed = player1.getInt("runnerScore") == 2 || player2.getInt("corpScore") == 2;
                                Card winner = player1.getInt("runnerScore") > player2.getInt("corpScore") ? player1Runner : player2Corp;
                                Card loser = player1.getInt("runnerScore") > player2.getInt("corpScore") ? player2Corp : player1Runner;

                                matchABR1 = new Match(tournament, winner, loser,
                                        timed, false, false, match.getInt("table"), roundNum);
                            }

                            // second match: player 1 corp vs player 2 runner
                            Match matchABR2;
                            tie = player1.getInt("corpScore") == 1 && player2.getInt("runnerScore") == 1;
                            if (tie) {
                                matchABR2 = new Match(tournament, player1Corp, player1Runner,
                                        false, true, false, match.getInt("table"), roundNum);
                            } else {
                                boolean timed = player1.getInt("corpScore") == 2 || player2.getInt("runnerScore") == 2;
                                Card winner = player1.getInt("corpScore") > player2.getInt("runnerScore") ? player1Corp : player2Runner;
                                Card loser = player1.getInt("corpScore") > player2.getInt("runnerScore") ? player2Runner : player1Corp;

                                matchABR2 = new Match(tournament, winner, loser,
                                        timed, false, false, match.getInt("table"), roundNum);
                            }

                            results.add(matchABR1);
                            results.add(matchABR2);
                        } else {
                            // cobr.ai
                            try {
                                if (player1.getInt("combinedScore") == 6) {
                                    Match matchCobra1 = new Match(tournament, player1Corp, player2Runner,
                                            false, false, false, match.getInt("table"), roundNum);
                                    Match matchCobra2 = new Match(tournament, player1Runner, player2Corp,
                                            false, false, false, match.getInt("table"), roundNum);
                                    results.add(matchCobra1);
                                    results.add(matchCobra2);
                                } else if (player2.getInt("combinedScore") == 6) {
                                    Match matchCobra1 = new Match(tournament, player2Corp, player1Runner,
                                            false, false, false, match.getInt("table"), roundNum);
                                    Match matchCobra2 = new Match(tournament, player2Runner, player1Corp,
                                            false, false, false, match.getInt("table"), roundNum);
                                    results.add(matchCobra1);
                                    results.add(matchCobra2);
                                }
                            } catch (Exception ex) {
                                logger.warn("Problems with parsing Cobr.ai resutls");
                            }
                        }
                    } else {
                        // top-cut
                        Card winner;
                        Card loser;

                        try {
                            if (player1.getBoolean("winner")) {
                                if (player1.getString("role").equals("runner")) {
                                    winner = player1Runner;
                                    loser = player2Corp;
                                } else {
                                    winner = player1Corp;
                                    loser = player2Runner;
                                }
                            } else {
                                if (player1.getString("role").equals("runner")) {
                                    winner = player2Corp;
                                    loser = player1Runner;
                                } else {
                                    winner = player2Runner;
                                    loser = player1Corp;
                                }
                            }

                            Match matchABR = new Match(tournament, winner, loser, false, false, true,
                                    match.getInt("table"), roundNum);

                            results.add(matchABR);
                        } catch (Exception ex) {
                            logger.warn("Having problem parsing top cut");
                        }
                    }

                }
            }
        }

        return results;
    }

    public String addSpecialCharsToIDs(String title) {
        return title
                .replace("Wunderkind", "Wünderkind")
                .replace("Palana", "Pālanā");
    }
}
