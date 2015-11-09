package com.madarasz.netrunnerstats.springMVC.controllers;

import com.madarasz.netrunnerstats.DOs.stats.DPStatistics;
import com.madarasz.netrunnerstats.Statistics;
import com.madarasz.netrunnerstats.springMVC.gchart.DataTable;
import com.madarasz.netrunnerstats.springMVC.gchartConverter.DPStatsToGchart;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Created by madarasz on 11/9/15.
 */
@Controller
public class DPController {

    @Autowired
    Statistics statistics;

    @Autowired
    DPStatsToGchart dpStatsToGchart;

    // JSON output
    @RequestMapping(value="/JSON/DPStats/Top/{DPName}", method = RequestMethod.GET)
    public @ResponseBody DPStatistics getDPJSON(@PathVariable String DPName) {
        DPStatistics stats = statistics.getPackStats(DPName);
        return stats;
    }

    // Google Chart DataTable output
    @RequestMapping(value="/DataTable/DPStats/Top/{sidecode}/{stattype}/{DPName}", method = RequestMethod.GET)
    public @ResponseBody DataTable getDPTopDataTable(
            @PathVariable(value = "sidecode") String sidecode,
            @PathVariable(value = "stattype") String stattype,
            @PathVariable(value = "DPName") String DPName) {
        DPStatistics stats = statistics.getPackStats(DPName);
        return dpStatsToGchart.converter(stats, sidecode, stattype);
    }

    // Google Chart DataTable output
    @RequestMapping(value="/Color/DPStats/Top/{sidecode}/{stattype}/{DPName}", method = RequestMethod.GET)
    public @ResponseBody List<String> getDPTopColors(
            @PathVariable(value = "sidecode") String sidecode,
            @PathVariable(value = "stattype") String stattype,
            @PathVariable(value = "DPName") String DPName) {
        DPStatistics stats = statistics.getPackStats(DPName);
        return dpStatsToGchart.colorConverter(stats, sidecode, stattype);
    }

    // html output
    @RequestMapping(value="/DPStats/{DPName}", method = RequestMethod.GET)
    public String getDPPage(@PathVariable String DPName, Map<String, Object> model) {
        model.put("DPname", DPName);
        return "DPStat";
    }

    @RequestMapping(value="/Color")
    public @ResponseBody List<String> getcolors() {
        List<String> colors = Arrays.asList("#FF0000", "#00FF00", "#0000FF");
        return colors;
    }
}
