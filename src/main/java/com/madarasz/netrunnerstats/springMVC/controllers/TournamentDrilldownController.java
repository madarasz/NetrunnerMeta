package com.madarasz.netrunnerstats.springMVC.controllers;

import com.madarasz.netrunnerstats.Statistics;
import com.madarasz.netrunnerstats.database.DOs.stats.TournamentDrilldown;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Created by madarasz on 4/8/16.
 * Controller class for tournament drilldown
 */
@Controller
public class TournamentDrilldownController {

    @Autowired
    Statistics statistics;

    // JSON - Tournament Drilldown data
    @RequestMapping(value="/JSON/Tournament/{sidecode}/{DPName}", method = RequestMethod.GET)
    public @ResponseBody
    TournamentDrilldown getDPIdentities(
            @PathVariable(value = "sidecode") String sidecode,
            @PathVariable(value = "DPName") String DPName) {
        return statistics.getTournamentDrilldown(DPName, sidecode);
    }
}
