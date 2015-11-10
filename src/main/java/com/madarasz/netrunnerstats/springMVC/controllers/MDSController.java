package com.madarasz.netrunnerstats.springMVC.controllers;

import com.madarasz.netrunnerstats.DOs.stats.IdentityMDS;
import com.madarasz.netrunnerstats.Statistics;
import com.madarasz.netrunnerstats.springMVC.gchart.DataTable;
import com.madarasz.netrunnerstats.springMVC.gchartConverter.MDSToGchart;
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
    MDSToGchart mdsToGchart;

    // JSON output
    @RequestMapping(value="/JSON/MDSIdentity/{DPName}/{identity}", method = RequestMethod.GET)
    public @ResponseBody
    IdentityMDS getDPJSON(@PathVariable(value="identity") String identity, @PathVariable(value="DPName") String DPName) {
        IdentityMDS MDS = statistics.getPackMath(identity, DPName);
        return MDS;
    }

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
        return "MDS";
    }
}
