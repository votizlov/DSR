package ru.org.dsr.search;

import junit.framework.Assert;
import ru.org.dsr.domain.ItemID;
import ru.org.dsr.exception.RequestException;
import ru.org.dsr.exception.RobotException;
import ru.org.dsr.search.factory.TypeItem;

class AbstractSearchTest {

    AbstractSearch search;
    ItemID itemID = new ItemID("Some item", "search", TypeItem.MOVIE);

    @org.junit.jupiter.api.Test
    void buildUrlSearchLiveLib() {
        search = new SearchLiveLibJSOUP();
        Assert.assertEquals(search.cnf.SEARCH + "Some+item+search", search.buildUrlSearch(itemID));
    }

    @org.junit.jupiter.api.Test
    void buildUrlSearchKinopoisk() {
        search = new SearchKinopoiskJSOUP();
        Assert.assertEquals(search.cnf.SEARCH + "Some+item+search", search.buildUrlSearch(itemID));
    }

    @org.junit.jupiter.api.Test
    void buildUrlSearchLabirint() {
        search = new SearchLabirintJSOUP();
        Assert.assertEquals(search.cnf.SEARCH + "Some+item+search", search.buildUrlSearch(itemID));
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