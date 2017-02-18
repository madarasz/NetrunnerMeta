package com.madarasz.netrunnerstats.springMVC.controllers;

import com.madarasz.netrunnerstats.database.DOs.Deck;
import com.madarasz.netrunnerstats.database.DOs.Standing;
import com.madarasz.netrunnerstats.database.DOs.Tournament;
import com.madarasz.netrunnerstats.database.DOs.admin.AdminData;
import com.madarasz.netrunnerstats.database.DRs.AdminDataRepository;
import com.madarasz.netrunnerstats.database.DRs.stats.CardPoolStatsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.neo4j.template.Neo4jOperations;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Map;

/**
 * Created by madarasz on 11/27/15.
 * Controller for the special pages
 */
@Controller
public class SpecialController {

    @Autowired
    Neo4jOperations template;

    @Autowired
    AdminDataRepository adminDataRepository;

    @Autowired
    CardPoolStatsRepository cardPoolStatsRepository;

    // html output

    @RequestMapping(value="/login", method = RequestMethod.GET)
    public String getLoginPage(Map<String, Object> model) {
        model.put("pageTitle", "Know the Meta - Android: Netrunner");
        model.put("cardpools", cardPoolStatsRepository.getCardPoolNames());
        return "Login";
    }

    @RequestMapping(value="/404", method = RequestMethod.GET)
    public String get404Page(Map<String, Object> model) {
        model.put("pageTitle", "Know the Meta - Android: Netrunner");
        model.put("cardpools", cardPoolStatsRepository.getCardPoolNames());
        return "404";
    }

    @RequestMapping(value="/soon", method = RequestMethod.GET)
    public String getSoonPage(Map<String, Object> model) {
        model.put("pageTitle", "Know the Meta - Android: Netrunner");
        model.put("cardpools", cardPoolStatsRepository.getCardPoolNames());
        return "Soon";
    }

    @RequestMapping(value="/Info", method = RequestMethod.GET)
    public String getInfoPage(Map<String, Object> model) {
        model.put("pageTitle", "Know the Meta - Android: Netrunner - Information");
        model.put("cardpools", cardPoolStatsRepository.getCardPoolNames());
        model.put("tournamentCount", template.count(Tournament.class));
        model.put("rankingCount", template.count(Standing.class));
        model.put("deckCount", template.count(Deck.class));
        AdminData lastUpdate = adminDataRepository.getLastUpdate();
        if (lastUpdate == null) {
            model.put("lastUpdate", "no data");
        } else {
            model.put("lastUpdate", lastUpdate.getData());
        }
        return "Info";
    }

    @RequestMapping(value="/LastUpdate", method = RequestMethod.GET)
    @ResponseBody String getUpdateTime() {
        return adminDataRepository.getLastUpdate().getData();
    }
}
