package com.mihash.ant_colony.controllers;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class PageController {
    @Value("${spring.application.name}")
    String appName;


    @RequestMapping("/")
    public String homePage(Model model) {
        return "index";
    }
}