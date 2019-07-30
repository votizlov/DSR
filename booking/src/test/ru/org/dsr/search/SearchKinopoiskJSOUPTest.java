package ru.org.dsr.search;

import junit.framework.Assert;
import ru.org.dsr.domain.Comment;
import ru.org.dsr.domain.Item;
import ru.org.dsr.domain.ItemID;
import ru.org.dsr.exception.RequestException;
import ru.org.dsr.exception.RobotException;
import ru.org.dsr.search.factory.TypeResource;

import java.util.List;

class SearchKinopoiskJSOUPTest {

    SearchKinopoiskJSOUP search;

    @org.junit.jupiter.api.Test
    void buildUrlSearch() {
        search = new SearchKinopoiskJSOUP();
        ItemID itemID = new ItemID("Автостопом по Галактике", "", "MOVIE");
        Assert.assertEquals("https://www.kinopoisk.ru/index.php?kp_query=Автостопом+по+Галактике", search.buildUrlSearch(itemID));
    }

    @org.junit.jupiter.api.Test
    void getDoc() {
        try {
            try {
                search = new SearchKinopoiskJSOUP();
                ItemID itemID = new ItemID("Автостопом по галактике", "", "MOVIE");
                Assert.assertNotNull(search.getDoc(search.buildUrlSearch(itemID)));
            } catch (RequestException e) {
                e.printStackTrace();
            }
        } catch (RobotException e) {
            return;
        }
    }

    @org.junit.jupiter.api.Test
    void createComment() {
        search = new SearchKinopoiskJSOUP();
        Comment comment = search.createComment("author", "title", "desc", "now", "test");
        Assert.assertTrue(comment != null &&
                comment.getAuthor()!=null &&
                comment.getTitle() != null &&
                comment.getDesc() != null &&
                comment.getDate() != null);
    }

    @org.junit.jupiter.api.Test
    void getItem() {
        try {
            try {
                ItemID itemID = new ItemID("Автостопом по галактике", "", "MOVIE");
                search = new SearchKinopoiskJSOUP(itemID);
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
        }catch (RobotException e) {
            return;
        }
    }

    @org.junit.jupiter.api.Test
    void loadComments() {
        try {
            try {
                ItemID itemID = new ItemID("Автостопом по галактике", "", "MOVIE");
                search = new SearchKinopoiskJSOUP(itemID);
                List<Comment> comments = search.loadComments(20);
                Assert.assertTrue((100 == comments.size() || search.isEmpty()) && 100 >= comments.size());
            } catch (RequestException e) {
                e.printStackTrace();
            }
        } catch (RobotException e) {
            return;
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