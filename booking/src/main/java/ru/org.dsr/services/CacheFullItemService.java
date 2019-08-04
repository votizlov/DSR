package ru.org.dsr.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.org.dsr.domain.Comment;
import ru.org.dsr.domain.FullItem;
import ru.org.dsr.domain.Item;
import ru.org.dsr.domain.ItemID;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class CacheFullItemService {

    private final int NUMBER_COMMENTS = 10;

    @Autowired
    ItemService itemService;
    @Autowired
    CommentService commentService;

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
        items.clear();
    }
}
