package com.madarasz.netrunnerstats.springMVC.controllers;

import com.madarasz.netrunnerstats.database.DOs.stats.IdentityAverage;
import com.madarasz.netrunnerstats.Statistics;
import com.madarasz.netrunnerstats.database.DRs.CardRepository;
import com.madarasz.netrunnerstats.database.DRs.stats.CardPoolStatsRepository;
import com.madarasz.netrunnerstats.helper.AverageDigest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Map;

/**
 * Created by madarasz on 11/10/15.
 * Controller class for multi dimension scaling related statistics.
 */
@Controller
public class MDSController {

    @Autowired
    Statistics statistics;

    @Autowired
    AverageDigest averageDigest;

    @Autowired
    CardPoolStatsRepository cardPoolStatsRepository;

    @Autowired
    CardRepository cardRepository;

    // html output
    @RequestMapping(value="/MDSIdentity/{DPName}/{identity}", method = RequestMethod.GET)
    public String getMDSPage(@PathVariable(value="identity") String identity,
                             @PathVariable(value="DPName") String DPName, Map<String, Object> model) {
        model.put("DPname", DPName);
        model.put("identity", identity);
        model.put("pageTitle", identity + " - " + DPName + " Know the Meta - Android: Netrunner");
        model.put("type", "identity");
        model.put("faction", cardRepository.findByTitle(identity).getFaction_code());
        model.put("cardpools", cardPoolStatsRepository.getCardPoolNames());
        return "MDS";
    }

    // html output
    @RequestMapping(value="/MDSFaction/{DPName}/{faction}", method = RequestMethod.GET)
    public String getMDSFactionPage(@PathVariable(value="faction") String faction,
                             @PathVariable(value="DPName") String DPName, Map<String, Object> model) {
        model.put("DPname", DPName);
        model.put("faction", faction);
        model.put("pageTitle", faction + " - " + DPName + " Know the Meta - Android: Netrunner");
        model.put("type", "faction");
        model.put("cardpools", cardPoolStatsRepository.getCardPoolNames());
        return "MDS";
    }

    // JSON output for identity drilldown
    @RequestMapping(value="/JSON/Identity/{DPName}/{identity}", method = RequestMethod.GET)
    public @ResponseBody
    IdentityAverage getIdentityDrilldown(
            @PathVariable(value="identity") String identity,
            @PathVariable(value="DPName") String DPName) {
        return statistics.getIdentityAverage(identity, DPName);
    }

    // JSON output for faction drilldown
    @RequestMapping(value="/JSON/Faction/{DPName}/{faction}", method = RequestMethod.GET)
    public @ResponseBody
    IdentityAverage getFactionDrilldown(
            @PathVariable(value="faction") String faction,
            @PathVariable(value="DPName") String DPName) {
        return statistics.getFactionAverage(faction, DPName);
    }
}
