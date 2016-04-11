package com.madarasz.netrunnerstats.springMVC.controllers;

import com.madarasz.netrunnerstats.database.DOs.stats.DPStatistics;
import com.madarasz.netrunnerstats.Statistics;
import com.madarasz.netrunnerstats.database.DRs.stats.CardPoolStatsRepository;
import com.madarasz.netrunnerstats.helper.AverageDigest;
import com.madarasz.netrunnerstats.helper.LastThree;
import com.madarasz.netrunnerstats.helper.gchartConverter.DPStatsToGchart;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Map;

/**
 * Created by madarasz on 11/9/15.
 * Controller class for Data Pack related statistics.
 */
@Controller
public class DPController {

    @Autowired
    Statistics statistics;

    @Autowired
    DPStatsToGchart dpStatsToGchart;

    @Autowired
    CardPoolStatsRepository cardPoolStatsRepository;

    @Autowired
    AverageDigest averageDigest;

    @Autowired
    LastThree lastThree;

    // JSON output DPstats
    @RequestMapping(value="/JSON/DPStats/{filter}/{DPName}", method = RequestMethod.GET)
    public @ResponseBody DPStatistics getDPStatistics(
            @PathVariable(value = "filter") String filter,
            @PathVariable(value = "DPName") String DPName) {
        switch (filter) {
            case "Top":
                return statistics.getPackStats(DPName, true);
            case "All":
                return statistics.getPackStats(DPName, false);
            default:
                return new DPStatistics();
        }
    }

    // html page output
    @RequestMapping(value="/DPStats/{DPName}", method = RequestMethod.GET)
    public String getDPPage(@PathVariable String DPName, Map<String, Object> model) {
        model.put("DPname", DPName);
        model.put("pageTitle", DPName + " - Know the Meta - Android: Netrunner");
        model.put("cardpools", cardPoolStatsRepository.getCardPoolNames());
        return "DPStat";
    }

}
