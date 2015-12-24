package com.madarasz.netrunnerstats.springMVC.controllers;

import com.madarasz.netrunnerstats.Statistics;
import com.madarasz.netrunnerstats.database.DOs.Deck;
import com.madarasz.netrunnerstats.database.DOs.Standing;
import com.madarasz.netrunnerstats.database.DOs.Tournament;
import com.madarasz.netrunnerstats.database.DOs.admin.BlogEntry;
import com.madarasz.netrunnerstats.database.DOs.stats.entries.CardPool;
import com.madarasz.netrunnerstats.database.DRs.AdminDataRepository;
import com.madarasz.netrunnerstats.database.DRs.BlogRepository;
import com.madarasz.netrunnerstats.helper.gchart.DataTable;
import com.madarasz.netrunnerstats.helper.gchartConverter.DPStatsToOverTimeGchart;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.neo4j.template.Neo4jOperations;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

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
    DPStatsToOverTimeGchart dpStatsToOverTimeGchart;

    @Autowired
    Neo4jOperations template;

    @Autowired
    AdminDataRepository adminDataRepository;

    @Autowired
    BlogRepository blogRepository;

    // JSON output
    @RequestMapping(value="/JSON/Cardpool", method = RequestMethod.GET)
    public @ResponseBody
    List<CardPool> getCardPools() {
        return statistics.getCardPoolStats().getSortedCardpool();
    }

    // html output
    @RequestMapping(value="/", method = RequestMethod.GET)
    public String getCPPage(Map<String, Object> model) {
        model.put("pageTitle", "Know the Meta - Android: Netrunner");
        model.put("tournamentCount", template.count(Tournament.class));
        model.put("rankingCount", template.count(Standing.class));
        model.put("deckCount", template.count(Deck.class));
        model.put("blogs", blogRepository.getAll());

        return "Home";
    }

    // DataTable output, factions over time
    @RequestMapping(value="/DataTable/Cardpool/{sidecode}", method = RequestMethod.GET)
    public @ResponseBody DataTable getDPTopDataTable(@PathVariable(value = "sidecode") String sidecode) {
        return dpStatsToOverTimeGchart.converter(sidecode);
    }
}
