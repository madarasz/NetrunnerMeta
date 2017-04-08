package com.madarasz.netrunnerstats.springMVC.controllers;

import com.madarasz.netrunnerstats.Statistics;
import com.madarasz.netrunnerstats.database.DOs.stats.TournamentDrilldown;
import com.madarasz.netrunnerstats.database.DRs.stats.CardPoolStatsRepository;
import com.madarasz.netrunnerstats.helper.AverageDigest;
import com.madarasz.netrunnerstats.helper.LastThree;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
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
    CardPoolStatsRepository cardPoolStatsRepository;

    @Autowired
    AverageDigest averageDigest;

    @Autowired
    LastThree lastThree;

    // html page output
    @RequestMapping(value="/DPStats/{DPName}", method = RequestMethod.GET)
    public String getDPPage(@PathVariable String DPName, Map<String, Object> model) {
        List<String> cardpoolNames = cardPoolStatsRepository.getCardPoolNames();
        int index = cardpoolNames.indexOf(DPName);

        model.put("DPname", DPName);
        model.put("pageTitle", DPName + " - Know the Meta - Android: Netrunner");
        model.put("cardpools", cardpoolNames);
        model.put("DPindex", index);

        if (index > 0) {
            model.put("DPnameAfter", cardpoolNames.get(index-1));
        }
        if (index < cardpoolNames.size() - 1) {
            model.put("DPnameBefore", cardpoolNames.get(index+1));
        }

        return "DPStat";
    }

    // JSON - Tournament Drilldown data
    @RequestMapping(value="/JSON/Tournament/{sidecode}/{DPName}", method = RequestMethod.GET)
    public @ResponseBody
    TournamentDrilldown getDPIdentities(
            @PathVariable(value = "sidecode") String sidecode,
            @PathVariable(value = "DPName") String DPName) {
        return statistics.getTournamentDrilldown(DPName, sidecode);
    }
}
