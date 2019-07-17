package ru.org.dsr.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.org.dsr.domain.Book;
import ru.org.dsr.domain.BookID;
import ru.org.dsr.exception.NoFoundBookException;
import ru.org.dsr.exception.RobotException;
import ru.org.dsr.search.Search;
import ru.org.dsr.search.SearchReadCityJSOUP;

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
            Search search;
            try {
                search = new SearchReadCityJSOUP(bookID);
                Book book = search.getBook();
                book.addCommentsJSON(search.loadJsonComments(2));
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
