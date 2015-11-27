package com.madarasz.netrunnerstats.springMVC.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.Map;

/**
 * Created by madarasz on 11/27/15.
 * Controller for the coming soon screen :)
 */
@Controller
public class SoonController {

    // html output

    @RequestMapping(value="/", method = RequestMethod.GET)
    public String getSoonPage(Map<String, Object> model) {
        return "Soon";
    }

}
