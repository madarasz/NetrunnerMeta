package com.madarasz.netrunnerstats.springMVC.controllers;

import com.madarasz.netrunnerstats.DOs.stats.IdentityMDS;
import com.madarasz.netrunnerstats.Statistics;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Created by madarasz on 11/10/15.
 */
@Controller
public class MDSController {

    @Autowired
    Statistics statistics;

    // JSON output
    @RequestMapping(value="/JSON/MDSIdentity/{DPName}/{Identity}", method = RequestMethod.GET)
    public @ResponseBody
    IdentityMDS getDPJSON(@PathVariable(value="Identity") String identity, @PathVariable(value="DPName") String DPName) {
        IdentityMDS MDS = statistics.getPackMath(identity, DPName);
        return MDS;
    }
}
