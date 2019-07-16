package ru.org.DSR_Practic.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;
import ru.org.DSR_Practic.domain.BookID;

@Controller
public class ResultController {

    @GetMapping("/result")
    @PostMapping("/result")
    public ModelAndView backSearch(ModelAndView model) {
        model.clear();
        model.setViewName("search");
        model.addObject("bookID", new BookID());
        return model;
    }
}
