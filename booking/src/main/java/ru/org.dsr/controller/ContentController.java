package ru.org.dsr.controller;


import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import ru.org.dsr.domain.FullItem;
import ru.org.dsr.domain.Item;
import ru.org.dsr.domain.ItemID;
import ru.org.dsr.domain.Comment;
import ru.org.dsr.exception.RobotException;
import ru.org.dsr.search.SearchKinopoiskJSOUP;
import ru.org.dsr.services.CacheFullItemService;
import ru.org.dsr.services.ManagerSearch;

import java.util.List;

@RestController
public class ContentController {

    private static final Logger log = Logger.getLogger(SearchKinopoiskJSOUP.class);

    @Autowired
    CacheFullItemService service;
    @Autowired
    ManagerSearch manager;

    @ResponseBody
    @DeleteMapping(value = "/cache/delete")
    public boolean clearBD() {
        service.clear();
        return true;
    }

    @GetMapping("/content")
    public ModelAndView toResult() {
        return new ModelAndView("result");
    }

    @ResponseBody
    @PostMapping(value = "/cache/comments")
    public List<Comment> getComments(@RequestParam(name = "page") int page, @RequestBody long id) {
        return service.getCommentsByIdItem(id, page);
    }

    @ResponseBody
    @PostMapping(value = "/cache/item")
    public Item getItem(@RequestBody long id) {
        return service.getItemById(id);
    }

    @ResponseBody
    @PutMapping(value = "/cache/save")
    public long saveFullItem(@RequestBody ItemID itemID) {
        log.info("Start save item");
        log.info("Start manager initialization");
        try {
            manager.init(itemID);
        } catch (RobotException e) {
            log.warn(e.getSrcForRobot(), e);
            return -1;
        }
        log.info("Close  manager initialization");
        FullItem fullItem = new FullItem();
        fullItem.setItem(manager.getItem());
        fullItem.setComments(manager.getComments(50));
        long id = service.save(fullItem);
        log.info("Close save item");
        return id;
    }
}
