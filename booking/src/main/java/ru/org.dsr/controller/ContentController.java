package ru.org.dsr.controller;


import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import ru.org.dsr.domain.FullItem;
import ru.org.dsr.domain.Item;
import ru.org.dsr.domain.ItemID;
import ru.org.dsr.domain.Comment;
import ru.org.dsr.services.CacheFullItemService;
import ru.org.dsr.services.ManagerSearch;

import java.util.List;

@RestController
public class ContentController {

    private static final Logger log = Logger.getLogger(ContentController.class);

    @Autowired
    CacheFullItemService service;
    @Autowired
    ManagerSearch manager;

    @GetMapping("/content")
    public ModelAndView toContent() {
        return new ModelAndView("content");
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
        {
            Item DBItem = service.getItemByItemID(itemID);
            if (DBItem != null) {
                return DBItem.getId();
            }
        }
        log.info("Start save item");
        log.info("Start manager initialization");
        manager.init(itemID);
        log.info("Close  manager initialization");
        if (manager.isEmpty()) return -1;
        Item nowItem = manager.getItem();
        if (nowItem != null) {
            Item DBItem = service.getItemByItemID(nowItem.getItemID());
            if (DBItem != null) {
                return DBItem.getId();
            }
        } else {

        }
        FullItem fullItem = new FullItem();
        fullItem.setItem(nowItem);
        fullItem.setComments(manager.getComments(50));
        long id = service.save(fullItem);
        log.info("Close save item");
        return id;
    }
}
