package ru.org.dsr.search;

import junit.framework.Assert;
import ru.org.dsr.domain.Comment;
import ru.org.dsr.domain.Item;
import ru.org.dsr.domain.ItemID;
import ru.org.dsr.exception.RequestException;
import ru.org.dsr.exception.RobotException;
import ru.org.dsr.search.factory.TypeResource;

import java.util.LinkedList;
import java.util.List;

class SearchKinopoiskJSOUPTest {

    SearchKinopoiskJSOUP search;

    @org.junit.jupiter.api.Test
    void getItem() {
        try {
            try {
                ItemID itemID = new ItemID("Автостопом по галактике", "", "MOVIE");
                search = new SearchKinopoiskJSOUP(itemID);
                Item item = search.getItem();
                Assert.assertNotNull(item);
                Assert.assertTrue(item.getItemID()!=null &&
                        item.getItemID().getFirstName() != null &&
                        item.getItemID().getLastName() != null &&
                        item.getDesc() != null &&
                        item.getUrlImg() != null);
            } catch (RequestException e) {
                e.printStackTrace();
            }
        }catch (RobotException e) {
            e.printStackTrace();
        }
    }

    @org.junit.jupiter.api.Test
    void loadCommentsFull() {
        try {
            try {
                ItemID itemID = new ItemID("Автостопом по галактике", "", "MOVIE");
                search = new SearchKinopoiskJSOUP(itemID);
                List<Comment> comments = search.loadComments(1000);
                int n;
                Assert.assertTrue((100 == (n = comments.size()) || search.isEmpty()) && 100 >= n && n > 0);
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
                ItemID itemID = new ItemID("Автостопом по галактике", "", "MOVIE");
                search = new SearchKinopoiskJSOUP(itemID);
                List<Comment> comments = new LinkedList<>();
                int n, part = 10;
                for (int i = 0; i < part*10; i+=part) {
                    comments.addAll(search.loadComments(part));
                    Assert.assertTrue((i+10 == (n = comments.size()) || search.isEmpty()) && n > 0);
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
        search = new SearchKinopoiskJSOUP();
        Assert.assertTrue(search.isEmpty());
    }

    @org.junit.jupiter.api.Test
    void getTypeResource() {
        search = new SearchKinopoiskJSOUP();
        Assert.assertTrue(search.getTypeResource() == TypeResource.KINOPOISK);
    }
}