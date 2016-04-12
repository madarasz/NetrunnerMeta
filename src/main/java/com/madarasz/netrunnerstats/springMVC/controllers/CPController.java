package com.madarasz.netrunnerstats.springMVC.controllers;

import com.madarasz.netrunnerstats.Statistics;
import com.madarasz.netrunnerstats.database.DOs.stats.DPStatistics;
import com.madarasz.netrunnerstats.database.DOs.stats.entries.CardPool;
import com.madarasz.netrunnerstats.database.DRs.AdminDataRepository;
import com.madarasz.netrunnerstats.database.DRs.BlogRepository;
import com.madarasz.netrunnerstats.database.DRs.stats.CardPoolStatsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.neo4j.template.Neo4jOperations;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Created by madarasz on 11/12/15.
 * Cardpool controller.
 */
@Controller
public class CPController {

    @Autowired
    Statistics statistics;

    @Autowired
    Neo4jOperations template;

    @Autowired
    AdminDataRepository adminDataRepository;

    @Autowired
    BlogRepository blogRepository;

    @Autowired
    CardPoolStatsRepository cardPoolStatsRepository;

    // JSON output
    @RequestMapping(value="/JSON/Cardpool", method = RequestMethod.GET)
    public @ResponseBody
    List<CardPool> getCardPools() {
        return statistics.getCardPoolStats().getSortedCardpool();
    }

    @RequestMapping(value="/JSON/Cardpoolnames", method = RequestMethod.GET)
    public @ResponseBody
    List<String> getCardPoolNames() {
        return cardPoolStatsRepository.getCardPoolNames();
    }

    // html output
    @RequestMapping(value="/", method = RequestMethod.GET)
    public String getCPPage(Map<String, Object> model) {
        model.put("pageTitle", "Know the Meta - Android: Netrunner");
        model.put("blogs", blogRepository.getLastThree());
        model.put("cardpools", cardPoolStatsRepository.getCardPoolNames());
        model.put("lastUpdate", adminDataRepository.getLastUpdate().getData());

        return "Home";
    }

    // JSON for factions over time
    @RequestMapping(value="/JSON/FactionsOverTime", method = RequestMethod.GET)
    public @ResponseBody List<DPStatistics> getFactionsOverTime() {
        List<CardPool> cardPools = statistics.getCardPoolStats().getSortedCardpool();
        Collections.reverse(cardPools);
        List<DPStatistics> result = new ArrayList<>();
        for (CardPool cardPool : cardPools) {
            result.add(statistics.getPackStats(cardPool.getTitle()));
        }
        return result;
    }
}
