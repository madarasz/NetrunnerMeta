package com.madarasz.netrunnerstats.springMVC.controllers;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.Map;

/**
 * Created by madarasz on 11/27/15.
 * Controller for the coming soon screen :)
 */
@Controller
public class AdminController {

    // html output

    @RequestMapping(value="/login", method = RequestMethod.GET)
    public String getLoginPage(Map<String, Object> model) {
        return "Login";
    }

    @PreAuthorize("hasRole(@roles.ADMIN)")
    @RequestMapping(value="/muchadmin", method = RequestMethod.GET)
    public String getAdminPage(Map<String, Object> model) {
        return "Admin";
    }
}
