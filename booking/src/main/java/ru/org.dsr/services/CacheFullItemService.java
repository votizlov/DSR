package ru.org.dsr.services;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.org.dsr.domain.Comment;
import ru.org.dsr.domain.FullItem;
import ru.org.dsr.domain.Item;
import ru.org.dsr.domain.ItemID;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Service
public class CacheFullItemService implements InitializingBean {

    private static final Logger LOGGER = Logger.getLogger(CacheFullItemService.class.getName());

    private final int NUMBER_COMMENTS = 10;
    private final int PERIOD_HOURS = 3;
    private final int DATE_HOURS_MINUS = 3;

    @Autowired
    private ItemService itemService;
    @Autowired
    private CommentService commentService;

    public long save(FullItem fullItem) {
        LocalDateTime date = LocalDateTime.now();
        Item item = fullItem.getItem();
        item.setDate(date);
        List<Comment> comments = fullItem.getComments();
        long idItem =  itemService.save(item);
        int page = 0;
        for (Comment c :
            comments) {
            c.setPage(page/NUMBER_COMMENTS+1);
            c.setIdItem(idItem);
            commentService.save(c);
            page++;
        }
        return idItem;
    }

    public Item getItemByItemID(ItemID itemID) {
        return itemService.getByItemID(itemID);
    }

    public Item getItemById(long id) {
        return itemService.getById(id);
    }

    public List<Comment> getCommentsByIdItem(long id, int page) {
        return commentService.getComments(id, page);
    }

    public void clear() {
        List<Item> items = itemService.getAll();
        for (Item item :
                items) {
            commentService.deleteByIdItem(item.getId());
        }
    }

    public void cleanup(LocalDateTime date) {
        LOGGER.info("Date : " + date);
        List<Item> items = itemService.removeOldItems(date);
        LOGGER.info("Clear : " + items.size());
        for (Item item :
                items) {
            commentService.deleteByIdItem(item.getId());
        }
    }

    @Override
    public void afterPropertiesSet() {
        ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
        scheduler.scheduleAtFixedRate(() -> {
            LOGGER.info("Start cleaning data base");
            LocalDateTime date = LocalDateTime.now();
            date = date.minusHours(DATE_HOURS_MINUS);
            cleanup(date);
            LOGGER.info("Close cleaning data base");
        }, PERIOD_HOURS, PERIOD_HOURS, TimeUnit.HOURS);
    }
}
