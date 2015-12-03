package com.madarasz.netrunnerstats.springMVC.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.Map;

/**
 * Created by madarasz on 11/27/15.
 * Controller for the special pages
 */
@Controller
public class SpecialController {

    // html output

    @RequestMapping(value="/login", method = RequestMethod.GET)
    public String getLoginPage(Map<String, Object> model) {
        return "Login";
    }

    @RequestMapping(value="/404", method = RequestMethod.GET)
    public String get404Page(Map<String, Object> model) {
        return "404";
    }

    @RequestMapping(value="/soon", method = RequestMethod.GET)
    public String getSoonPage(Map<String, Object> model) {
        return "Soon";
    }
}
