package ru.org.dsr.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class ResultController {

    @GetMapping("/result")
    public String redirect() {
        return "redirect:search";
    }

    @PostMapping("/result")
    public String back() {
        return "redirect:search";
    }
}
