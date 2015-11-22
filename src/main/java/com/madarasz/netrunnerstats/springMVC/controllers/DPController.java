package com.madarasz.netrunnerstats.springMVC.controllers;

import com.madarasz.netrunnerstats.database.DOs.stats.DPStatistics;
import com.madarasz.netrunnerstats.Statistics;
import com.madarasz.netrunnerstats.database.DOs.stats.entries.DPIdentity;
import com.madarasz.netrunnerstats.springMVC.gchart.DataTable;
import com.madarasz.netrunnerstats.springMVC.gchartConverter.DPStatsToCompareGchart;
import com.madarasz.netrunnerstats.springMVC.gchartConverter.DPStatsToGchart;
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
    DPStatsToGchart dpStatsToGchart;

    @Autowired
    DPStatsToCompareGchart dpStatsToCompareGchart;

    // JSON output
    @RequestMapping(value="/JSON/DPStats/{filter}/{DPName}", method = RequestMethod.GET)
    public @ResponseBody DPStatistics getDPJSON(
            @PathVariable(value = "filter") String filter,
            @PathVariable(value = "DPName") String DPName) {
        DPStatistics stats = statistics.getPackStats(DPName, filter.equals("Top"));
        return stats;
    }

    // Google Chart DataTable output
    @RequestMapping(value="/DataTable/DPStats/{filter}/{sidecode}/{stattype}/{DPName}", method = RequestMethod.GET)
    public @ResponseBody DataTable getDPTopDataTable(
            @PathVariable(value = "filter") String filter,
            @PathVariable(value = "sidecode") String sidecode,
            @PathVariable(value = "stattype") String stattype,
            @PathVariable(value = "DPName") String DPName) {

        if (filter.equals("Compare")) {  // comparison
            DPStatistics allstats = statistics.getPackStats(DPName, false);
            DPStatistics topstats = statistics.getPackStats(DPName, true);
            return dpStatsToCompareGchart.converter(topstats, allstats, sidecode, stattype);

        } else if (filter.equals("Top")) {  // top
            DPStatistics stats = statistics.getPackStats(DPName, true);
            return dpStatsToGchart.converter(stats, sidecode, stattype);

        } else if (filter.equals("All")) {  // all
            DPStatistics stats = statistics.getPackStats(DPName, false);
            return dpStatsToGchart.converter(stats, sidecode, stattype);

        } else {    // none
            return new DataTable();
        }
    }

    // html page output
    @RequestMapping(value="/DPStats/{DPName}", method = RequestMethod.GET)
    public String getDPPage(@PathVariable String DPName, Map<String, Object> model) {
        model.put("DPname", DPName);
        return "DPStat";
    }

    // JSON - identities in the Data Pack
    @RequestMapping(value="/JSON/DPStats/Identities/{sidecode}/{DPName}", method = RequestMethod.GET)
    public @ResponseBody
    List<DPIdentity> getDPIdentities(
            @PathVariable(value = "sidecode") String sidecode,
            @PathVariable(value = "DPName") String DPName) {
        return statistics.getIdentityLinksForDataPack(DPName, sidecode).getSortedIdentities();
    }
}
