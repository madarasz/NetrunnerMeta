package com.madarasz.netrunnerstats.brokers;

import com.madarasz.netrunnerstats.DOs.*;
import com.madarasz.netrunnerstats.DRs.CardPackRepository;
import com.madarasz.netrunnerstats.DRs.CardRepository;
import com.madarasz.netrunnerstats.DRs.DeckRepository;
import com.madarasz.netrunnerstats.helper.TitleGuesser;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
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
    private final static String JSOUP_TOURNAMENT_DECK_ROW = "div.rank-list:lt(1) > table > tbody > tr";
    private final static String JSOUP_TOURNAMENT_DECK_ROW2 = "div.rank-list:gt(0) > table > tbody > tr:gt(%d)";    // skipping the first %d+1 rows
    private final static String JSOUP_TOURNAMENT_LIST = "div.tournament-list > table > tbody > tr";
    private final static String JSOUP_PAGINATION = "div.pagination > a";
    private final static String JSOUP_TITLE = "h1";
    private final static String JSOUP_TOURNAMENT_DECK_ID = "div.rank-list > table > tbody > tr > td > img";
    private final static String JSOUP_TOURNAMENT_DECK_ID2 = "div.rank-list:gt(0) > table > tbody > tr > td > img";
    private final static String JSOUP_TOURNAMENT_RANKLIST = "div.rank-list";

    @Autowired
    RegExBroker regExBroker;

    @Autowired
    CardRepository cardRepository;

    @Autowired
    CardPackRepository cardPackRepository;

    @Autowired
    DeckRepository deckRepository;

    @Autowired
    TitleGuesser titleGuesser;


    /**
     * Reads Deck information from Acoo. Also adds deck metadata.
     * @param id Acoo deck ID
     * @return deck
     */
    public Deck readDeck(int id) {
        String url = deckUrlFromId(id);
        HttpBroker.parseHtml(url);
        Deck resultDeck = parseDeck(HttpBroker.linesFromHtml(JSOUP_DECK_CARDS));

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

    /**
     * Reads card data for Acoo deck
     * @param lines card lines from html
     * @return deck without metadata, just cards
     */
    public Deck parseDeck(ArrayList<String> lines) {

        Deck result = new Deck();
        for (String line : lines) {
            int quantity = regExBroker.getCardQuantity(line);
            String title = regExBroker.getCardFromLine(line);
            Card card = cardRepository.findByTitle(title);
            if (card == null) {
                title = titleGuesser.alternateTitle(title);
                card = cardRepository.findByTitle(title);
                if (card == null) {
                    System.out.println("ERROR - no such card");
                }
            }
            if (quantity == 0) {    // identity
                result.setIdentity(card);
            } else {    // other card
                result.hasCard(card, quantity);
            }
        }

        return result;
    }

    /**
     * Reads tournament metadata
     * @param tournamentId Acoo tournament ID
     * @return tournament without standings, decks
     */
    public Tournament readTournament(int tournamentId) {
        String url = tournamentUrlFromId(tournamentId);
        HttpBroker.parseHtml(url);
        String titlebar = HttpBroker.textFromHtml(JSOUP_TITLE);
        String[] titleparts = titlebar.split(" \\(|\\)");
        String decription = HttpBroker.textFromHtml(JSOUP_TOURNAMENT_DESC);
        int playerNumber = regExBroker.getNumberFromBeginning(decription);
        Date date = regExBroker.parseDate(titleparts[1]);
        CardPack pool = cardPackRepository.findByName(HttpBroker.textFromHtml(JSOUP_TOURNAMENT_POOL));
        if (pool == null) {
            System.out.println("ERROR - no card pack found: " + HttpBroker.textFromHtml(JSOUP_TOURNAMENT_POOL) + " // tournament id: " + tournamentId);
        }
        return new Tournament(tournamentId, titleparts[0], date, pool, url, playerNumber);
    }

    /**
     * Loads tournament IDs from tournament list page
     * @param url page URL
     * @param filterempty filter out tournaments with 0 decklists
     * @return Acoo tournament IDS
     */
    public Set<Integer> loadTournamentIdsFromUrl(String url, boolean filterempty) {
        Set<Integer> result = new HashSet<Integer>();
        HttpBroker.parseHtml(url);
        Elements rows = HttpBroker.elementsFromHtml(JSOUP_TOURNAMENT_LIST);
        for (Element row : rows) {
            int decknumber = 0;
            String decknumbertext = "";
            try {
                decknumbertext = row.select("td:eq(5)").first().text();
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

    /**
     * Counts number of defined identities in tournament
     * @param url url of tournament
     * @return number of defined indentities
     */
    public int getTournamentIdentityCount(String url) {
        HttpBroker.parseHtml(url);
        Elements lists = HttpBroker.elementsFromHtml(JSOUP_TOURNAMENT_RANKLIST);

        // just swiss
        if (lists.size() == 1) {
            Elements ids = HttpBroker.elementsFromHtml(JSOUP_TOURNAMENT_DECK_ID);
            return ids.size();
        // swiss + top X
        } else {
            Elements ids = HttpBroker.elementsFromHtml(JSOUP_TOURNAMENT_DECK_ID2);
            return ids.size();
        }
    }

    /**
     * Loads standings and decks from tournament
     * @param url Acoo tournament url
     * @param tournament tournament
     * @return standings with decks
     */
    public Set<Standing> loadTournamentStandingsAndDecks(String url, Tournament tournament) {
        Set<Standing> standings = new HashSet<Standing>();
        HttpBroker.parseHtml(url);
        Elements lists = HttpBroker.elementsFromHtml(JSOUP_TOURNAMENT_RANKLIST);

        // extract top X or all swiss (if there was no top X)
        Elements rows = HttpBroker.elementsFromHtml(JSOUP_TOURNAMENT_DECK_ROW);
        standings.addAll(standingsFromRows(tournament, rows));

        // extract top X minus swiss
        if (lists.size() == 2) {
            HttpBroker.parseHtml(url);
            int toskip = standings.size() / 2 - 1;  // skipping top X results from swiss
            rows = HttpBroker.elementsFromHtml(String.format(JSOUP_TOURNAMENT_DECK_ROW2, toskip));
            standings.addAll(standingsFromRows(tournament, rows));
        }
        return standings;
    }

    /**
     * Helper for extracting Standings and Decks from tournament html rows.
     * @param tournament tournament
     * @param rows html table rows
     * @return standings with decks
     */
    private Set<Standing> standingsFromRows(Tournament tournament, Elements rows) {
        Set<Standing> standings = new HashSet<Standing>();
        for (Element row : rows) {
            // extract rank
            String rankString = row.select("td").first().text();
            int rank = regExBroker.getNumberFromBeginning(rankString);
            Elements ids = row.select("td > img");
            for (Element id : ids) {
                // extract standing
                String[] hrefparts = id.attr("src").split("/");
                String identitystring = iconToIdentity(hrefparts[4]);
                Card identity = cardRepository.findByTitle(identitystring);
                if (identity == null) {
                    System.out.println("ERROR - can't find identity for title: " + identitystring);
                }
                boolean topdeck = rank <= tournament.getPlayerNumber() * 0.3;    // top 30% of decks

                // extract deck
                Elements deckpart = id.parent().select("a");
                if (deckpart.isEmpty()) {
                    standings.add(new Standing(tournament, rank, identity, topdeck, identity.isRunner()));
                } else {
                    System.out.print("Rank: " + rank + " - ");
                    hrefparts = deckpart.first().attr("href").split("/");
                    int deckId = Integer.valueOf(hrefparts[2]);
                    Deck exists = deckRepository.findByUrl(deckUrlFromId(deckId));
                    if (exists == null) {
                        Deck deck = readDeck(deckId);
                        System.out.println("Saving new deck! - " + deck.toString());
                        standings.add(new Standing(tournament, rank, identity, topdeck, identity.isRunner(), deck));
                    } else {
                        System.out.println("Already added");
                        standings.add(new Standing(tournament, rank, identity, topdeck, identity.isRunner()));
                    }
                }
            }
        }
        return standings;
    }

    /**
     * Translates Acoo identity icon name to identity card title
     * @param filename filename
     * @return card title
     */
    private String iconToIdentity(String filename) {
        String identityName;
        switch (filename) {
            case "icon-adam.png" :  // not sure
                identityName = "Adam: Compulsive Hacker";
                break;
            case "icon-andromeda.jpg" :
                identityName = "Andromeda: Dispossessed Ristie";
                break;
            case "icon-apex.png" :
                identityName = "Apex: Invasive Predator";
                break;
            case "icon-argus-security.png" :
                identityName = "Argus Security: Protection Guaranteed";
                break;
            case "icon-armand.jpg" :
                identityName = "Armand \"Geist\" Walker: Tech Lord";
                break;
            case "icon-blue-sun.jpg" :
                identityName = "Blue Sun: Powering the Future";
                break;
            case "icon-cerebral-imaging.jpg" :
                identityName = "Cerebral Imaging: Infinite Frontiers";
                break;
            case "icon-chaos-theory.jpg" :
                identityName = "Chaos Theory: Wünderkind";
                break;
            case "icon-chronos-protocol.png" :
                identityName = "Chronos Protocol: Selective Mind-mapping";
                break;
            case "icon-custom-biotics.jpg" :
                identityName = "Custom Biotics: Engineered for Success";
                break;
            case "icon-cybernetics-division.png" :
                identityName = "Cybernetics Division: Humanity Upgraded";
                break;
            case "icon-edward-kim.png" :
                identityName = "Edward Kim: Humanity's Hammer";
                break;
            case "icon-exile-streethawk.jpg" :
                identityName = "Exile: Streethawk";
                break;
            case "icon-gabriel-santiago.jpg" :
                identityName = "Gabriel Santiago: Consummate Professional";
                break;
            case "icon-gagarin-deep-space.png" :
                identityName = "Gagarin Deep Space: Expanding the Horizon";
                break;
            case "icon-grdnl.jpg" :
                identityName = "GRNDL: Power Unleashed";
                break;
            case "icon-haarpsichord.png" :
                identityName = "Haarpsichord Studios: Entertainment Unleashed";
                break;
            case "hb-etf-icon.png" :
                identityName = "Haas-Bioroid: Engineering the Future";
                break;
            case "icon-stronger-together.jpg" :
                identityName = "Haas-Bioroid: Stronger Together";
                break;
            case "icon-harmony-medtech.jpg" :
                identityName = "Harmony Medtech: Biomedical Pioneer";
                break;
            case "icon-hayley-kaplan.png" :
                identityName = "Hayley Kaplan: Universal Scholar";
                break;
            case "icon-iain-stirling.png" :
                identityName = "Iain Stirling: Retired Spook";
                break;
            case "icon-industrial-genomics.jpg" :
                identityName = "Industrial Genomics: Growing Solutions";
                break;
            case "icon-jinteki-biotech-life.png" :
                identityName = "Jinteki Biotech: Life Imagined";
                break;
            case "jinteki-pe-icon.png" :
                identityName = "Jinteki: Personal Evolution";
                break;
            case "icon-jinteki-rp.jpg" :
                identityName = "Jinteki: Replicating Perfection";
                break;
            case "kate-icon.png" :
                identityName = "Kate \"Mac\" McCaffrey: Digital Tinker";
                break;
            case "icon-ken-tenma.png" :
                identityName = "Ken \"Express\" Tenma: Disappeared Clone";
                break;
            case "icon-laramy-fisk.png" :
                identityName = "Laramy Fisk: Savvy Investor";
                break;
            case "iconr-leela-patel.jpg" :
                identityName = "Leela Patel: Trained Pragmatist";
                break;
            case "icon-maxx.png" :
                identityName = "MaxX: Maximum Punk Rock";
                break;
            case "nasir-icon.jpg" :
                identityName = "Nasir Meidan: Cyber Explorer";
                break;
            case "icon-nbn-mn.jpg" :
                identityName = "NBN: Making News";
                break;
            case "icon-nbn-twiy.jpg" :
                identityName = "NBN: The World is Yours*";
                break;
            case "Near-earth-hub-icon.png" :
                identityName = "Near-Earth Hub: Broadcast Center";
                break;
            case "icon-sol.png" :   // not sure
                identityName = "New Angeles Sol: Your News";
                break;
            case "icon-next-design.jpg" :
                identityName = "NEXT Design: Guarding the Net";
                break;
            case "icon-nisei-division.png" :
                identityName = "Nisei Division: The Next Generation";
                break;
            case "noise-icon.png" :
                identityName = "Noise: Hacker Extraordinaire";
                break;
            case "quetzal-icon.png" :
                identityName = "Quetzal: Free Spirit";
                break;
            case "icon-reina-roja.jpg" :
                identityName = "Reina Roja: Freedom Fighter";
                break;
            case "icon-rielle-kit-.jpg" :
                identityName = "Rielle \"Kit\" Peddler: Transhuman";
                break;
            case "icon-silhouette.jpg" :
                identityName = "Silhouette: Stealth Operative";
                break;
            case "icon-spark.png" :  // not sure
                identityName = "Spark Agency: Worldswide Reach";
                break;
            case "icon-sunny.png" :  // not sure
                identityName = "Sunny Lebeau: Security Specialist";
                break;
            case "icon-sync.png" :  // not sure
                identityName = "SYNC: Everything, Everywhere";
                break;
            case "icon-tennin-institute.png" :
                identityName = "Tennin Institute: The Secrets Within";
                break;
            case "icon-the-foundry.jpg" :
                identityName = "The Foundry: Refining the Process";
                break;
            case "icon-the-professor.jpg" :
                identityName = "The Professor: Keeper of Knowledge";
                break;
            case "icon-titan-transnational.png" :
                identityName = "Titan Transnational: Investing In Your Future";
                break;
            case "icon-valencia-estevez.png" :
                identityName = "Valencia Estevez: The Angel of Cayambe";
                break;
            case "icon-weyland-BWBi.jpg" :
                identityName = "Weyland Consortium: Because We Built It";
                break;
            case "icon-weyland-consortium-babw.jpg" :
                identityName = "Weyland Consortium: Building a Better World";
                break;
            case "icon-whizzard.jpg" :
                identityName = "Whizzard: Master Gamer";
                break;

            default:
                System.out.println(String.format("ERROR - Unknown ID for icon: %s", filename));
                identityName = "The Collective: Williams, Wu, et al.";  // fallback ID
        }
        return identityName;
    }

}
