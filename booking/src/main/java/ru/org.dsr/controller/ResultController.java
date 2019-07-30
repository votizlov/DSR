package ru.org.dsr.controller;


import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import ru.org.dsr.domain.Item;
import ru.org.dsr.domain.ItemID;
import ru.org.dsr.domain.Comment;
import ru.org.dsr.exception.RobotException;
import ru.org.dsr.search.SearchKinopoiskJSOUP;
import ru.org.dsr.services.CommentsService;
import ru.org.dsr.services.ManagerSearch;

import java.util.List;

@RestController
public class ResultController {

    private static final Logger log = Logger.getLogger(SearchKinopoiskJSOUP.class);


    @Autowired
    CommentsService commentsService;
    @Autowired
    ManagerSearch manager;

    private Item item;

    @ResponseBody
    @RequestMapping(value = "/result-clear", method = RequestMethod.DELETE)
    public boolean clearBD() {
        commentsService.clear();
        item = null;
        log.info("DB clear");
        return true;
    }

    @GetMapping("/result")
    public ModelAndView toResult() {
        ModelAndView mav = new ModelAndView();
        mav.setViewName("result");
        return mav;
    }

    @ResponseBody
    @RequestMapping(value = "/result-getComments", method = RequestMethod.POST)
    public List<Comment> getComments(@RequestBody int page) {
        List<Comment> list = commentsService.getCommentsFromPage(page);
        return list;
    }

    @ResponseBody
    @RequestMapping(value = "/result-getItem", method = RequestMethod.POST)
    public Item getItem() {
        return item;
    }

    @ResponseBody
    @RequestMapping(value = "/result-init", method = RequestMethod.POST)
    public boolean initManager(@RequestBody ItemID itemID) throws RobotException {
        try {
            manager.init(itemID);
        } catch (RobotException e) {
            log.warn(e.getSrcForRobot(), e);
            return false;
        }
        item = manager.getItem();
        List<Comment> comments = manager.getComments(50);
        for (Comment c :
                comments) {
            commentsService.saveOrUpdate(c);
        }
        return true;
    }
}
