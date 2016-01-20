package com.madarasz.netrunnerstats.springMVC.controllers;

import com.madarasz.netrunnerstats.Statistics;
import com.madarasz.netrunnerstats.database.DOs.stats.entries.CardPool;
import com.madarasz.netrunnerstats.database.DRs.AdminDataRepository;
import com.madarasz.netrunnerstats.database.DRs.BlogRepository;
import com.madarasz.netrunnerstats.database.DRs.stats.CardPoolStatsRepository;
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

        return "Home";
    }

    // DataTable output, factions over time
    @RequestMapping(value="/DataTable/Cardpool/{sidecode}", method = RequestMethod.GET)
    public @ResponseBody DataTable getDPTopDataTable(@PathVariable(value = "sidecode") String sidecode) {
        return dpStatsToOverTimeGchart.converter(sidecode);
    }
}
