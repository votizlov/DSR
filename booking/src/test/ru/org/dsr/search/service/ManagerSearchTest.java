package ru.org.dsr.search.service;

import junit.framework.Assert;
import org.junit.jupiter.api.Test;
import ru.org.dsr.config.ConfigFactory;
import ru.org.dsr.domain.Comment;
import ru.org.dsr.domain.Item;
import ru.org.dsr.domain.ItemID;
import ru.org.dsr.exception.PropertiesException;
import ru.org.dsr.exception.RobotException;
import ru.org.dsr.search.factory.FactoryPackSearch;
import ru.org.dsr.search.factory.TypeResource;
import ru.org.dsr.services.ManagerSearch;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

class ManagerSearchTest {

    ManagerSearch managerSearch;
    ConfigFactory configFactory;
    {
        configFactory = new ConfigFactory();
        configFactory.setMainBook(TypeResource.LABIRINT);
        configFactory.setMainMovie(TypeResource.KINOPOISK);
        configFactory.setMainGame(TypeResource.GAME);
        configFactory.setResources(new ArrayList<>(Arrays.asList(TypeResource.values())));
        try {
            configFactory.afterPropertiesSet();
        } catch (PropertiesException e) {
            e.printStackTrace();
        }
    }

    @Test
    void getCommentsBook() {
        managerSearch = new ManagerSearch(configFactory);
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
        managerSearch = new ManagerSearch(configFactory);
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
        managerSearch = new ManagerSearch(configFactory);
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
        managerSearch = new ManagerSearch(configFactory);
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