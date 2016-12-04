package com.madarasz.netrunnerstats.springMVC.controllers;

import com.madarasz.netrunnerstats.Operations;
import com.madarasz.netrunnerstats.Statistics;
import com.madarasz.netrunnerstats.brokers.AcooBroker;
import com.madarasz.netrunnerstats.brokers.NetrunnerDBBroker;
import com.madarasz.netrunnerstats.database.DOs.*;
import com.madarasz.netrunnerstats.database.DOs.admin.AdminData;
import com.madarasz.netrunnerstats.database.DOs.admin.VerificationProblem;
import com.madarasz.netrunnerstats.database.DOs.stats.*;
import com.madarasz.netrunnerstats.database.DOs.stats.entries.*;
import com.madarasz.netrunnerstats.database.DRs.AdminDataRepository;
import com.madarasz.netrunnerstats.database.DRs.DeckRepository;
import com.madarasz.netrunnerstats.database.DRs.StandingRepository;
import com.madarasz.netrunnerstats.database.DRs.TournamentRepository;
import com.madarasz.netrunnerstats.database.DRs.stats.CardPoolStatsRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.neo4j.template.Neo4jOperations;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by madarasz on 11/27/15.
 * Controller for the admin page
 */
@Controller
public class AdminController {

    private static final Logger logger = LoggerFactory.getLogger(AdminController.class);

    @Autowired
    Neo4jOperations template;

    @Autowired
    TournamentRepository tournamentRepository;

    @Autowired
    DeckRepository deckRepository;

    @Autowired
    StandingRepository standingRepository;

    @Autowired
    Operations operations;

    @Autowired
    AcooBroker acooBroker;

    @Autowired
    NetrunnerDBBroker netrunnerDBBroker;

    @Autowired
    AdminDataRepository adminDataRepository;

    @Autowired
    CardPoolStatsRepository cardPoolStatsRepository;

    @Autowired
    Statistics statistics;

    private final DateFormat df = new SimpleDateFormat("yyyy.MM.dd.");

    /**
     * Handles date conversion. Null date returns empty string.
     *
     * @param date date
     * @return formatted date
     */
    private String safeDateFormat(Date date) {
        if (date == null) {
            return "";
        } else {
            return df.format(date);
        }
    }

    // html output

    @RequestMapping(value = "/muchadmin", method = RequestMethod.GET)
    public String getAdminPage(Map<String, Object> model) {
        // Stimhack count
        model.put("countStimhackTournaments", tournamentRepository.countStimhackTournaments());
        model.put("lastStimhackTournament", safeDateFormat(tournamentRepository.getLastStimhackTournamentDate()));
        model.put("countStimhackDecks", deckRepository.countStimhackDecks());
        model.put("countStimhackStandings", standingRepository.countStimhackStandings());
        // Acoo count
        model.put("countAcooTournaments", tournamentRepository.countAcooTournaments());
        model.put("lastAcooTournament", safeDateFormat(tournamentRepository.getLastAcooTournamentDate()));
        model.put("countAcooDecks", deckRepository.countAcooDecks());
        model.put("countAcooStandings", standingRepository.countAcooStandings());
        // NetrunnerDB count
        model.put("countCards", template.count(Card.class));
        model.put("countCardPacks", template.count(CardPack.class));
        model.put("countCardCycles", template.count(CardCycle.class));
        model.put("countNetrunnerDBDecks", deckRepository.countNetrunnerDBDecks());
        // DB count
        model.put("countTournaments", template.count(Tournament.class));
        model.put("countDecks", template.count(Deck.class));
        model.put("countStandings", template.count(Standing.class));
//        model.put("countDeckHasCards", template.count(DeckHasCard.class));
        // stat coung
        model.put("countCardPoolStats", template.count(CardPoolStats.class));
        model.put("countCardPool", template.count(CardPool.class));
        model.put("countDPStatistics", template.count(DPStatistics.class));
        model.put("countCountDeckStands", template.count(CountDeckStands.class));
        model.put("countMDSEntry", template.count(MDSEntry.class));
        model.put("countCardUsageStat", template.count(CardUsageStat.class));
        model.put("countCardUsage", template.count(CardUsage.class));
        model.put("countIdentityAverage", template.count(IdentityAverage.class));
        model.put("countCardAverage", template.count(CardAverage.class));
        model.put("countAdminData", template.count(AdminData.class));
        model.put("countCardStat", template.count(CardStat.class));
        model.put("countCardCombo", template.count(CardCombo.class));
        model.put("countDPDecks", template.count(DPDecks.class));
        model.put("countTournamentDrilldown", template.count(TournamentDrilldown.class));
        model.put("countStandingDeckCount", template.count(StandingDeckCount.class));
        model.put("countStandingDeckCountID", template.count(StandingDeckCountID.class));
        model.put("cardpools", cardPoolStatsRepository.getCardPoolNames());
        List<Deck> withoutRel = deckRepository.getAllDecksWithoutRel();
        for (Deck deck : withoutRel) {
            logger.warn("Deck without rel: " + deck.toString());
        }
        model.put("countDecksWithoutRel", withoutRel.size());
//        model.put("countDoubledStandings", standingRepository.countDoubledStandings());

        AdminData denyurls = adminDataRepository.getDenyUrls();
        if (denyurls == null) {
            denyurls = new AdminData();
        }
        model.put("denyurls", denyurls.getData());
        return "Admin";
    }

    // submissions

    // denyUrl data
    @RequestMapping(value = "/muchadmin/Verify", method = RequestMethod.GET)
    public
    @ResponseBody
    List<VerificationProblem> verifyData() {
        // remove already banned urls
        List<VerificationProblem> problems = operations.checkDataValidity();
        return problems;
    }

    @RequestMapping(value = "/muchadmin/BanDeck", method = RequestMethod.GET)
//    public @ResponseBody String banDeck(@PathVariable(value="url") String url, final RedirectAttributes redirectAttributes) {
    public
    @ResponseBody
    String banDeck(String url, final RedirectAttributes redirectAttributes) {
        if (!url.equals("")) {
            AdminData denyurls = adminDataRepository.getDenyUrls();
            if (denyurls == null) {
                denyurls = new AdminData("denyUrls", "");
            }
            // check if not banned already
            if (!denyurls.isIn(url)) {
                denyurls.setData(denyurls.getData() + "\n" + url);
                adminDataRepository.save(denyurls);
                logger.info("Denied urls updated.");
            }
            // delete deck with standings
            deleteDeck(url, redirectAttributes);
            return "OK";
        } else {
            return "Missing url";
        }
    }

    // add stimhack tournament
    @RequestMapping(value = "/muchadmin/Stimhack/AddTournament", method = RequestMethod.POST)
    public String addStimhackTournament(String url, final RedirectAttributes redirectAttributes) {
        Tournament exists = tournamentRepository.findByUrl(url);
        if (exists == null) {
            try {
                operations.loadStimhackTournament(url);
                redirectAttributes.addFlashAttribute("successMessage",
                        String.format("Tournament is added to DB: %s", url));
            } catch (Exception ex) {
                logger.error("logged exception", ex);
                redirectAttributes.addFlashAttribute("errorMessage",
                        String.format("Error loading tournament with url: %s", url));
            }
        } else {
            redirectAttributes.addFlashAttribute("warningMessage",
                    String.format("Tournament is already in DB: %s", url));
        }
        operations.updateLastUpdateDate();
        return "redirect:/muchadmin";
    }

    // add multiple stimhack tournament by datapack
    @RequestMapping(value = "/muchadmin/Stimhack/AddDataPack", method = RequestMethod.POST)
    public String addStimhackTournamentsByDP(String datapack, final RedirectAttributes redirectAttributes) {
        try {
            long count = template.count(Tournament.class);
            operations.loadStimhackPackTournaments(datapack);
            count = template.count(Tournament.class) - count;
            if (count > 0) {
                redirectAttributes.addFlashAttribute("successMessage",
                        String.format("%d new tournaments are added to DB for datapack: %s", count, datapack));
            } else {
                redirectAttributes.addFlashAttribute("warningMessage",
                        String.format("No new tournaments are added to DB for datapack: %s", datapack));
            }
        } catch (Exception ex) {
            logger.error("logged exception", ex);
            redirectAttributes.addFlashAttribute("errorMessage",
                    String.format("Error loading Stimhack tournaments with datapack: %s", datapack));
        }
        operations.updateLastUpdateDate();
        return "redirect:/muchadmin";
    }

    // add acoo deck
    @RequestMapping(value = "/muchadmin/Acoo/AddDeck", method = RequestMethod.POST)
    public String addAcooDeck(String deckid, final RedirectAttributes redirectAttributes) {
        try {
            int id = Integer.parseInt(deckid);
            Deck exists = deckRepository.findByUrl(acooBroker.deckUrlFromId(id));
            if (exists == null) {
                operations.loadAcooDeck(id);
                redirectAttributes.addFlashAttribute("successMessage",
                        String.format("Tournament is added to DB with ID: %d", id));
            } else {
                redirectAttributes.addFlashAttribute("warningMessage",
                        String.format("Tournament is already in DB with Acoo ID: %d", id));
            }
        } catch (Exception ex) {
            logger.error("logged exception", ex);
            redirectAttributes.addFlashAttribute("errorMessage",
                    String.format("Error loading tournament with ID: %s", deckid));
            return "redirect:/muchadmin";
        }
        return "redirect:/muchadmin";
    }

    // add acoo tournament
    @RequestMapping(value = "/muchadmin/Acoo/AddTournament", method = RequestMethod.POST)
    public String addAcooTournament(String tournamentid, final RedirectAttributes redirectAttributes) {
        try {
            int id = Integer.parseInt(tournamentid);
            long tournamentcount = template.count(Tournament.class);
            long deckcount = template.count(Deck.class);
            long standingcount = template.count(Standing.class);
            operations.loadAcooTournamentDecks(id);
            tournamentcount = template.count(Tournament.class) - tournamentcount;
            deckcount = template.count(Deck.class) - deckcount;
            standingcount = template.count(Standing.class) - standingcount;
            if (tournamentcount + deckcount + standingcount > 0) {
                redirectAttributes.addFlashAttribute("successMessage",
                        String.format("Tournament is added to DB with ID: %d - new decks: %d, new standings: %d", id, deckcount, standingcount));
            } else {
                redirectAttributes.addFlashAttribute("warningMessage",
                        String.format("No new data was added with Acoo tournament ID: %d", id));
            }
        } catch (Exception ex) {
            logger.error("logged exception", ex);
            redirectAttributes.addFlashAttribute("errorMessage",
                    String.format("Error loading tournament with ID: %s", tournamentid));
            return "redirect:/muchadmin";
        }
        operations.updateLastUpdateDate();
        return "redirect:/muchadmin";
    }

    // add acoo tournament from page
    @RequestMapping(value = "/muchadmin/Acoo/AddTournaments", method = RequestMethod.POST)
    public String addAcooTournaments(String url, boolean paging, final RedirectAttributes redirectAttributes) {
        try {
            long tournamentcount = template.count(Tournament.class);
            long deckcount = template.count(Deck.class);
            long standingcount = template.count(Standing.class);
            operations.loadAcooTournamentsFromUrl(url, paging, false);
            tournamentcount = template.count(Tournament.class) - tournamentcount;
            deckcount = template.count(Deck.class) - deckcount;
            standingcount = template.count(Standing.class) - standingcount;
            if (tournamentcount + deckcount + standingcount > 0) {
                redirectAttributes.addFlashAttribute("successMessage",
                        String.format("Tournaments added to DB from URL: %s - new tournaments: %d, new decks: %d, new standings: %d",
                                url, tournamentcount, deckcount, standingcount));
            } else {
                redirectAttributes.addFlashAttribute("warningMessage",
                        String.format("No new data was added with Acoo URL: %s", url));
            }
        } catch (Exception ex) {
            logger.error("logged exception", ex);
            redirectAttributes.addFlashAttribute("errorMessage",
                    String.format("Error loading tournament with URL: %s", url));
            return "redirect:/muchadmin";
        }
        operations.updateLastUpdateDate();
        return "redirect:/muchadmin";
    }

    // load Netrunner DB cards and cardpacks
    @RequestMapping(value = "/muchadmin/NetrunnerDB/LoadDB", method = RequestMethod.POST)
    public String loadNetrunnerDB(final RedirectAttributes redirectAttributes) {
        try {
            long cardcount = template.count(Card.class);
            long cardpackcount = template.count(CardPack.class);
            operations.loadNetrunnerDB();
            cardcount = template.count(Card.class) - cardcount;
            cardpackcount = template.count(CardPack.class) - cardpackcount;
            if (cardcount + cardpackcount > 0) {
                redirectAttributes.addFlashAttribute("successMessage",
                        String.format("NetrunnerDB refreshed - new cards: %d, new cardpacks: %d",
                                cardcount, cardpackcount));
            } else {
                redirectAttributes.addFlashAttribute("warningMessage", "No new data was added from NetrunnerDB.");
            }
        } catch (Exception ex) {
            logger.error("logged exception", ex);
            redirectAttributes.addFlashAttribute("errorMessage", "Error loading NetrunnerDB");
            return "redirect:/muchadmin";
        }
        return "redirect:/muchadmin";
    }

    // add NetrunnerDB deck
    @RequestMapping(value = "/muchadmin/NetrunnerDB/LoadDeck", method = RequestMethod.POST)
    public String addNetrunnerDBDeck(String deckid, final RedirectAttributes redirectAttributes) {
        try {
            int id = Integer.parseInt(deckid);
            Deck exists = deckRepository.findByUrl(netrunnerDBBroker.deckUrlFromId(id));
            if (exists == null) {
                operations.loadNetrunnerDbDeck(id);
                redirectAttributes.addFlashAttribute("successMessage",
                        String.format("Tournament is added to DB with NetrunnerDB ID: %d", id));
            } else {
                redirectAttributes.addFlashAttribute("warningMessage",
                        String.format("Tournament is already in DB with NetrunnerDB ID: %d", id));
            }
        } catch (Exception ex) {
            logger.error("logged exception", ex);
            redirectAttributes.addFlashAttribute("errorMessage",
                    String.format("Error loading tournament with NetrunnerDB ID: %s", deckid));
            return "redirect:/muchadmin";
        }
        return "redirect:/muchadmin";
    }

    // delete deck
    @RequestMapping(value = "/muchadmin/Delete/Deck", method = RequestMethod.POST)
    public String deleteDeck(String url, final RedirectAttributes redirectAttributes) {
        Deck exists = deckRepository.findByUrl(url);
        if (exists != null) {
            try {
                operations.deleteDeck(url);
                logger.info("Deck deleted: " + url);
                redirectAttributes.addFlashAttribute("successMessage",
                        String.format("Deck deleted with URL: %s", url));
            } catch (Exception ex) {
                logger.error("logged exception", ex);
                redirectAttributes.addFlashAttribute("errorMessage",
                        String.format("Error deleting deck with url: %s", url));
            }
        } else {
            redirectAttributes.addFlashAttribute("warningMessage",
                    String.format("No deck with URL: %s", url));
        }
        return "redirect:/muchadmin";
    }

    // delete tournament
    @RequestMapping(value = "/muchadmin/Delete/Tournament", method = RequestMethod.POST)
    public String deleteTournament(String url, final RedirectAttributes redirectAttributes) {
        Tournament exists = tournamentRepository.findByUrl(url);
        if (exists != null) {
            try {
                long tournamentcount = template.count(Tournament.class);
                long deckcount = template.count(Deck.class);
                long standingcount = template.count(Standing.class);
                operations.deleteTournament(url);
                tournamentcount -= template.count(Tournament.class);
                deckcount -= template.count(Deck.class);
                standingcount -= template.count(Standing.class);
                redirectAttributes.addFlashAttribute("successMessage",
                        String.format("Tournament deleted with URL: %s - deleted tournaments: %d, decks: %d, standings: %d",
                                url, tournamentcount, deckcount, standingcount));
            } catch (Exception ex) {
                logger.error("logged exception", ex);
                redirectAttributes.addFlashAttribute("errorMessage",
                        String.format("Error deleting tournament with url: %s", url));
            }
        } else {
            redirectAttributes.addFlashAttribute("warningMessage",
                    String.format("No tournament with URL: %s", url));
        }
        return "redirect:/muchadmin";
    }

    // delete denied decks
    @RequestMapping(value = "/muchadmin/Execute/DenyUrls", method = RequestMethod.POST)
    public String deleteDeniedDecks(final RedirectAttributes redirectAttributes) {
        try {
            AdminData denyurls = adminDataRepository.getDenyUrls();
            if (denyurls != null) {
                long deckcount = template.count(Deck.class);
                long standingcount = template.count(Standing.class);
                List<Deck> decks = deckRepository.getAllDecks();
                for (Deck deck : decks) {
                    if (denyurls.getData().contains(deck.getUrl())) {
                        operations.deleteDeck(deck.getUrl());
                    }
                }
                deckcount -= template.count(Deck.class);
                standingcount -= template.count(Standing.class);
                redirectAttributes.addFlashAttribute("successMessage",
                        String.format("Denied decks deleted: %d - deleted standings: %d", deckcount, standingcount));
            }
        } catch (Exception ex) {
            logger.error("logged exception", ex);
            redirectAttributes.addFlashAttribute("errorMessage",
                    String.format("Error deleting denied decks"));
        }
        return "redirect:/muchadmin";
    }

    // reset all data
    @RequestMapping(value = "/muchadmin/PurgeAll", method = RequestMethod.POST)
    public String purgeAll(Map<String, Object> model) {
        operations.cleanDB();
        return "redirect:/muchadmin";
    }

    // reset statistical data
    @RequestMapping(value = "/muchadmin/PurgeStat", method = RequestMethod.POST)
    public String purgeStats(Map<String, Object> model) {
        operations.resetStats();
        return "redirect:/muchadmin";
    }

    // reset statistical data on datapack
    @RequestMapping(value = "/muchadmin/PurgeStat/{DPName}", method = RequestMethod.POST)
    public String purgeStatsDP(@PathVariable(value = "DPName") String DPName) {
        operations.resetStatsDP(DPName);
        return "redirect:/muchadmin";
    }

    // deny urls
    @RequestMapping(value = "/muchadmin/Update/DenyUrls", method = RequestMethod.POST)
    public String denyUrl(String urls, Map<String, Object> model) {
        AdminData newurls = new AdminData("denyUrls", urls);
        adminDataRepository.save(newurls);
        return "redirect:/muchadmin";
    }

    // add DAD MWL
    @RequestMapping(value = "/muchadmin/Experimental", method = RequestMethod.POST)
    public String experimental(final RedirectAttributes redirectAttributes) {
        operations.detectPostMWL(false);
        return "redirect:/muchadmin";
    }

    // add DAD MWL
    @RequestMapping(value = "/muchadmin/Experimental2", method = RequestMethod.POST)
    public String experimental2(final RedirectAttributes redirectAttributes) {
        operations.detectPostMWL(true);
        return "redirect:/muchadmin";
    }

    // detect win more cards
    @RequestMapping(value = "/muchadmin/WinMore", method = RequestMethod.POST)
    public String winMore(final RedirectAttributes redirectAttributes) {
        statistics.getWinMoreCards();
        return "redirect:/muchadmin";
    }

    // add acoo tournament from page
    @RequestMapping(value = "/muchadmin/ABR/AddPack", method = RequestMethod.POST)
    public String addABRTournaments(String pack, RedirectAttributes redirectAttributes) {
        try {
            long tournamentcount = template.count(Tournament.class);
            long deckcount = template.count(Deck.class);
            long standingcount = template.count(Standing.class);
            operations.loadABRTournamentsForPack(pack);
            tournamentcount = template.count(Tournament.class) - tournamentcount;
            deckcount = template.count(Deck.class) - deckcount;
            standingcount = template.count(Standing.class) - standingcount;
            if (tournamentcount + deckcount + standingcount > 0) {
                redirectAttributes.addFlashAttribute("successMessage",
                        String.format("Tournaments added to DB for packcode: %s - new tournaments: %d, new decks: %d, new standings: %d",
                                pack, tournamentcount, deckcount, standingcount));
            } else {
                redirectAttributes.addFlashAttribute("warningMessage",
                        String.format("No new data was added for packcodeL: %s", pack));
            }
        } catch (Exception ex) {
            logger.error("logged exception", ex);
            redirectAttributes.addFlashAttribute("errorMessage",
                    String.format("Error loading tournaments with packcode: %s", pack));
            return "redirect:/muchadmin";
        }
        operations.updateLastUpdateDate();
        return "redirect:/muchadmin";
    }

}
