package com.madarasz.netrunnerstats.springMVC.controllers;

import com.madarasz.netrunnerstats.database.DOs.stats.IdentityMDS;
import com.madarasz.netrunnerstats.Statistics;
import com.madarasz.netrunnerstats.database.DOs.stats.entries.CardAverage;
import com.madarasz.netrunnerstats.database.DRs.stats.CardPoolStatsRepository;
import com.madarasz.netrunnerstats.helper.AverageDigest;
import com.madarasz.netrunnerstats.helper.gchart.DataTable;
import com.madarasz.netrunnerstats.helper.gchartConverter.MDSToGchart;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
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
    MDSToGchart mdsToGchart;

    @Autowired
    AverageDigest averageDigest;

    @Autowired
    CardPoolStatsRepository cardPoolStatsRepository;

    // Google Chart DataTable output
    @RequestMapping(value="/DataTable/MDSIdentity/{DPName}/{identity}", method = RequestMethod.GET)
    public @ResponseBody
    DataTable getMDSDataTable(@PathVariable(value="identity") String identity,
                              @PathVariable(value="DPName") String DPName) {
        IdentityMDS MDS = statistics.getPackMath(identity, DPName);
        return mdsToGchart.converter(MDS);
    }

    // html output
    @RequestMapping(value="/MDSIdentity/{DPName}/{identity}", method = RequestMethod.GET)
    public String getMDSPage(@PathVariable(value="identity") String identity,
                             @PathVariable(value="DPName") String DPName, Map<String, Object> model) {
        model.put("DPname", DPName);
        model.put("identity", identity);
        model.put("pageTitle", identity + " - " + DPName + " Know the Meta - Android: Netrunner");
        model.put("cardpools", cardPoolStatsRepository.getCardPoolNames());
        return "MDS";
    }

    // JSON output for deck averages
    @RequestMapping(value="/JSON/Average/{DPName}/{identity}/{part}", method = RequestMethod.GET)
    public @ResponseBody
    List<CardAverage> getDeckAverage(
            @PathVariable(value="identity") String identity,
            @PathVariable(value="DPName") String DPName,
            @PathVariable(value="part") int part) {
        return averageDigest.getSortedAverages(statistics.getIdentityAverage(identity, DPName), part);
    }

    // JSON output for deck averages
    @RequestMapping(value="/JSON/Average/{DPName}/{identity}", method = RequestMethod.GET)
    public @ResponseBody
    List<CardAverage> getDeckAverage(
            @PathVariable(value="identity") String identity,
            @PathVariable(value="DPName") String DPName) {
        return averageDigest.getSortedAverages(statistics.getIdentityAverage(identity, DPName));
    }
}
