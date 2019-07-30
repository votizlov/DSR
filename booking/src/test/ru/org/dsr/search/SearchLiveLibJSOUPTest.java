package ru.org.dsr.search;

import junit.framework.Assert;
import org.junit.jupiter.api.Test;
import ru.org.dsr.domain.Comment;
import ru.org.dsr.domain.Item;
import ru.org.dsr.domain.ItemID;
import ru.org.dsr.exception.RequestException;
import ru.org.dsr.exception.RobotException;
import ru.org.dsr.search.factory.TypeResource;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class SearchLiveLibJSOUPTest {
    SearchLiveLibJSOUP search;
    ItemID itemID;

    @Test
    void getItem() {
        itemID = new ItemID("Автостопом по Галактике", "", "MOVIE");
        try {
            try {
                search = new SearchLiveLibJSOUP(itemID);
                Item item = search.getItem();
                Assert.assertTrue(item != null &&
                        item.getItemID()!=null &&
                        item.getItemID().getFirstName() != null &&
                        item.getItemID().getLastName() != null &&
                        item.getDesc() != null &&
                        item.getUrlImg() != null);
            } catch (RequestException e) {
                e.printStackTrace();
            }
        } catch (RobotException e) {
            return;
        }
    }

    @Test
    void loadComments() {
        itemID = new ItemID("Автостопом по Галактике", "", "MOVIE");
        try {
            try {
                search = new SearchLiveLibJSOUP(itemID);
                List<Comment> comments = search.loadComments(20);
                Assert.assertTrue((100 == comments.size() || search.isEmpty()) && 100 >= comments.size());
            } catch (RequestException e) {
                e.printStackTrace();
            }
        } catch (RobotException e) {
            return;
        }
    }

    @Test
    void isEmpty() {
        search = new SearchLiveLibJSOUP();
        Assert.assertTrue(search.isEmpty());
    }

    @Test
    void getTypeResource() {
        search = new SearchLiveLibJSOUP();
        Assert.assertTrue(search.getTypeResource() == TypeResource.LIVE_LIB);
    }
}