package ru.org.dsr.search.factory;

import junit.framework.Assert;
import org.junit.jupiter.api.Test;
import ru.org.dsr.domain.ItemID;
import ru.org.dsr.domain.PackSearch;
import ru.org.dsr.exception.PropertiesException;
import ru.org.dsr.search.Search;

class FactoryPackSearchTest {
    FactoryPackSearch factoryPackSearch;
    {
        try {
            factoryPackSearch = new FactoryPackSearch();
        } catch (PropertiesException e) {
            e.printStackTrace();
        }
    }

    @Test
    void createPackSearch() {
        PackSearch packSearch = factoryPackSearch.createPackSearch(new ItemID("Остров проклятых", "", "MOVIE"));
        boolean b = true;
        for (Search s :
                packSearch.getSearches()) {
            switch (s.getTypeResource()) {
                case KINOPOISK:
                    break;
                default:
                    b = false;
            }
        }
        Assert.assertTrue(b);

        packSearch = factoryPackSearch.createPackSearch(new ItemID("Автостопом по галактике", "", "BOOK"));
        b = true;
        for (Search s :
                packSearch.getSearches()) {
            switch (s.getTypeResource()) {
                case LIVE_LIB:
                case READ_CITY:
                case LABIRINT:
                    break;
                default:
                    b = false;
            }
        }
        Assert.assertTrue(b);
    }
}