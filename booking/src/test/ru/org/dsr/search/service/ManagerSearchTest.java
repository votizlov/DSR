package ru.org.dsr.search.service;

import junit.framework.Assert;
import org.junit.jupiter.api.Test;
import ru.org.dsr.domain.Comment;
import ru.org.dsr.domain.Item;
import ru.org.dsr.domain.ItemID;
import ru.org.dsr.exception.RobotException;
import ru.org.dsr.services.ManagerSearch;

import java.util.List;

class ManagerSearchTest {

    ManagerSearch managerSearch;

    @Test
    void getCommentsBook() {
        managerSearch = new ManagerSearch();
        ItemID itemID = new ItemID("Автостопом по галактике", "", "BOOK");
        try {
            managerSearch.init(itemID);
        } catch (RobotException e) {
            e.printStackTrace();
        }
        List<Comment> comments = managerSearch.getComments(100);
        Assert.assertTrue((100 == comments.size() || managerSearch.isEmpty()) && 100 >= comments.size());
    }

    @Test
    void getItemBook() {
        managerSearch = new ManagerSearch();
        ItemID itemID = new ItemID("Автостопом по галактике", "", "BOOK");
        try {
            managerSearch.init(itemID);
        } catch (RobotException e) {
            e.printStackTrace();
        }
        Item item = managerSearch.getItem();
        Assert.assertTrue(item != null &&
                item.getItemID()!=null &&
                item.getItemID().getFirstName() != null &&
                item.getItemID().getLastName() != null &&
                item.getDesc() != null &&
                item.getUrlImg() != null);
    }

    @Test
    void getCommentsMovie() {
        managerSearch = new ManagerSearch();
        ItemID itemID = new ItemID("Автостопом по галактике", "", "BOOK");
        try {
            managerSearch.init(itemID);
        } catch (RobotException e) {
            e.printStackTrace();
        }
        List<Comment> comments = managerSearch.getComments(100);
        Assert.assertTrue((100 == comments.size() || managerSearch.isEmpty()) && 100 >= comments.size());
    }

    @Test
    void getItemMovie() {
        managerSearch = new ManagerSearch();
        ItemID itemID = new ItemID("Автостопом по галактике", "", "BOOK");
        try {
            managerSearch.init(itemID);
        } catch (RobotException e) {
            e.printStackTrace();
        }
        Item item = managerSearch.getItem();
        Assert.assertTrue(item != null &&
                item.getItemID()!=null &&
                item.getItemID().getFirstName() != null &&
                item.getItemID().getLastName() != null &&
                item.getDesc() != null &&
                item.getUrlImg() != null);
    }
}