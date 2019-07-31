package ru.org.dsr.search;

import junit.framework.Assert;
import org.junit.jupiter.api.Test;
import ru.org.dsr.domain.Comment;
import ru.org.dsr.domain.ItemID;
import ru.org.dsr.exception.RequestException;
import ru.org.dsr.exception.RobotException;

import static org.junit.jupiter.api.Assertions.*;

class AbstractSearchTest {

    AbstractSearch search;
    ItemID itemID = new ItemID("Some item", "search", "");

    @org.junit.jupiter.api.Test
    void buildUrlSearchLiveLib() {
        search = new SearchLiveLibJSOUP();
        Assert.assertEquals(search.SEARCH + "Some+item+search", search.buildUrlSearch(itemID));
    }

    @org.junit.jupiter.api.Test
    void buildUrlSearchKinopoisk() {
        search = new SearchKinopoiskJSOUP();
        Assert.assertEquals(search.SEARCH + "Some+item+search", search.buildUrlSearch(itemID));
    }

    @org.junit.jupiter.api.Test
    void buildUrlSearchLabirint() {
        search = new SearchLabirintJSOUP();
        Assert.assertEquals(search.SEARCH + "Some+item+search", search.buildUrlSearch(itemID));
    }

    @org.junit.jupiter.api.Test
    void getDocLiveLib() {
        search = new SearchLiveLibJSOUP();
        try {
            Assert.assertNotNull(search.getDoc(search.buildUrlSearch(itemID)));
        } catch (RequestException | RobotException e) {
            System.out.println(e.toString());
            Assert.fail();
        }
    }

    @org.junit.jupiter.api.Test
    void getDocKinopoisk() {
        search = new SearchKinopoiskJSOUP();
        try {
            Assert.assertNotNull(search.getDoc(search.buildUrlSearch(itemID)));
        } catch (RequestException | RobotException e) {
            System.out.println(e.toString());
            Assert.fail();
        }
    }

    @org.junit.jupiter.api.Test
    void getDocLabirint() {
        search = new SearchLabirintJSOUP();
        try {
            Assert.assertNotNull(search.getDoc(search.buildUrlSearch(itemID)));
        } catch (RequestException | RobotException e) {
            System.out.println(e.toString());
            Assert.fail();
        }
    }
}