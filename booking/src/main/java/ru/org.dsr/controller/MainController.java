package ru.org.dsr.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import ru.org.dsr.domain.Book;
import ru.org.dsr.domain.BookID;
import ru.org.dsr.exception.RobotException;
import ru.org.dsr.search.ManagerSearch;

@Controller
@SessionAttributes("manager")
public class MainController {

    @ModelAttribute("manager")
    public ManagerSearch createManager() {
        return new ManagerSearch();
    }

    @PostMapping("/search")
    public ModelAndView toResult(@ModelAttribute BookID bookID, @ModelAttribute("manager") ManagerSearch manager, ModelAndView model) {
        try {
            manager.init(bookID);
        } catch (RobotException e) {
            e.printStackTrace();
        }
        model.setViewName("result");
        return model;
    }

    @ResponseBody
    @GetMapping("/result")
    public Book initBook(@ModelAttribute("manager") ManagerSearch manager) {
        try {
            Book book = manager.getBook();
            return book;
        } catch (RobotException e) {
            return null;
        }
    }

    @ResponseBody
    @PostMapping("/result")
    public String getComments(@RequestBody int count, @ModelAttribute("manager") ManagerSearch manager) {
        return manager.getComments(count).toString();
    }

    @PostMapping("/resultExit")
    public String toSearch (Model model) {
        model.addAttribute("bookID", new BookID());
        return "search";
    }

    @GetMapping("/search")
    public String home(Model model) {
        model.addAttribute("bookID", new BookID());
        return "search";
    }

    @GetMapping("/")
    public String redirect(Model model) {
        model.addAttribute("bookID", new BookID());
        return "redirect:search";
    }
}
