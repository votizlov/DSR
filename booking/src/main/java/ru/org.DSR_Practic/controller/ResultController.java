package ru.org.DSR_Practic.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.method.support.ModelAndViewContainer;
import org.springframework.web.servlet.ModelAndView;
import ru.org.DSR_Practic.domain.BookID;

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
