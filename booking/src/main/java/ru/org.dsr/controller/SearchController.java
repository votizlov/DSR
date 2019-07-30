package ru.org.dsr.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

@RestController
public class SearchController {

    @GetMapping("/search")
    public ModelAndView toSearch() {
        ModelAndView mav = new ModelAndView();
        mav.setViewName("search");
        return mav;
    }

    @GetMapping("/")
    public String toMain() {

        return "redirect:search";
    }

}
