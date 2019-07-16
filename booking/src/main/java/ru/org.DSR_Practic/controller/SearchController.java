package ru.org.DSR_Practic.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.org.DSR_Practic.domain.Book;
import ru.org.DSR_Practic.domain.BookID;
import ru.org.DSR_Practic.model.exception.NoFoundBookException;
import ru.org.DSR_Practic.model.exception.RobotException;
import ru.org.DSR_Practic.model.search.Search;
import ru.org.DSR_Practic.model.search.SearchReadCity;

@Controller
public class SearchController {

    @GetMapping("/search")
    public String getIndex(@RequestParam(name="name", required=false, defaultValue="World") String name, Model model) {
        model.addAttribute("bookID", new BookID());
        return "search";
    }

    @PostMapping("/search")
    public String example(@ModelAttribute BookID bookID, Model model) {
        if (bookID != null && !(
                (bookID.getAuthor() == null || bookID.getAuthor().equals("")) &&
                        (bookID.getName() == null || bookID.getName().equals("")) )
        ){
            Search search = new SearchReadCity();
            try {
                Book book = search.get(bookID);
                model.addAttribute("book", book);
            } catch (NoFoundBookException e) {
                model.addAttribute("bookID", new BookID());
                model.addAttribute("message", "Книга не найдена - " + e.getRequestBook());
                return "search";
            } catch (RobotException r) {
                model.addAttribute("bookID", new BookID());
                model.addAttribute("message", r.getMessage());
                return "search";
            }
        }
        else {
            model.addAttribute("bookID", new BookID());
            return "search";
        }
        return "result";
    }
}
