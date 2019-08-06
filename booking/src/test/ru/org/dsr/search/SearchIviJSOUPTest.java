package ru.org.dsr.search;

import junit.framework.Assert;
import ru.org.dsr.domain.Comment;
import ru.org.dsr.domain.Item;
import ru.org.dsr.domain.ItemID;
import ru.org.dsr.exception.RequestException;
import ru.org.dsr.exception.RobotException;
import ru.org.dsr.search.factory.TypeItem;
import ru.org.dsr.search.factory.TypeResource;

import java.util.LinkedList;
import java.util.List;

class SearchIviJSOUPTest {

    private SearchIviJSOUP search;

    @org.junit.jupiter.api.Test
    void getItem() {
        try {
            try {
                ItemID itemID = new ItemID("Автостопом по галактике", "", TypeItem.MOVIE);
                search = new SearchIviJSOUP(itemID);
                Item item = search.getItem();
                Assert.assertNotNull(item);
                Assert.assertTrue(item.getItemID() != null &&
                        item.getItemID().getFirstName() != null &&
                        item.getItemID().getLastName() != null &&
                        item.getDesc() != null &&
                        item.getUrlImg() != null);
            } catch (RequestException e) {
                e.printStackTrace();
            }
        } catch (RobotException e) {
            e.printStackTrace();
        }
    }

    @org.junit.jupiter.api.Test
    void loadCommentsFull() {
        try {
            try {
                ItemID itemID = new ItemID("Мстители: Война бесконечности", "", TypeItem.MOVIE);
                search = new SearchIviJSOUP(itemID);
                List<Comment> comments = search.loadComments(1000);
                int n = comments.size();
                Assert.assertTrue((100 == n || search.isEmpty()) && 100 >= n);
            } catch (RequestException e) {
                e.printStackTrace();
            }
        } catch (RobotException e) {
            e.printStackTrace();
        }
    }

    @org.junit.jupiter.api.Test
    void loadCommentsParts() {
        try {
            try {
                ItemID itemID = new ItemID("Прочь", "", TypeItem.MOVIE);
                search = new SearchIviJSOUP(itemID);
                List<Comment> comments = new LinkedList<>();
                int n, part = 10;
                for (int i = 0; i < part * 10; i += part) {
                    comments.addAll(search.loadComments(part));
                    n = comments.size();
                    Assert.assertTrue((i + 10 == n || search.isEmpty()) && i+10 >= n);
                }
            } catch (RequestException e) {
                e.printStackTrace();
            }
        } catch (RobotException e) {
            e.printStackTrace();
        }
    }

    @org.junit.jupiter.api.Test
    void isEmpty() {
        search = new SearchIviJSOUP();
        Assert.assertTrue(search.isEmpty());
    }

    @org.junit.jupiter.api.Test
    void getTypeResource() {
        search = new SearchIviJSOUP();
        Assert.assertSame(search.getTypeResource(), TypeResource.IVI);
    }
}
