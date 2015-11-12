package com.madarasz.netrunnerstats.springMVC.controllers;

import com.madarasz.netrunnerstats.DOs.stats.CardPoolStats;
import com.madarasz.netrunnerstats.Statistics;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Map;

/**
 * Created by madarasz on 11/12/15.
 */
@Controller
public class CPController {

    @Autowired
    Statistics statistics;

    // JSON output
    @RequestMapping(value="/JSON/Cardpool", method = RequestMethod.GET)
    public @ResponseBody
    CardPoolStats getCardPools() {
        return statistics.getCardPoolStats();
    }

    // html output
    @RequestMapping(value="/Cardpool", method = RequestMethod.GET)
    public String getCPPage(Map<String, Object> model) {
        return "Cardpool";
    }
}
