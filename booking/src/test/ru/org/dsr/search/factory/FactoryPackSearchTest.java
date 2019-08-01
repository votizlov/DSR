package ru.org.dsr.search.factory;

import junit.framework.Assert;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.org.dsr.config.ConfigFactory;
import ru.org.dsr.domain.ItemID;
import ru.org.dsr.domain.PackSearch;
import ru.org.dsr.exception.PropertiesException;
import ru.org.dsr.search.Search;

import java.util.ArrayList;
import java.util.Arrays;

class FactoryPackSearchTest {
    ConfigFactory configFactory;
    FactoryPackSearch factoryPackSearch;
    {
        configFactory = new ConfigFactory();
        configFactory.setMainBook(TypeResource.LABIRINT);
        configFactory.setMainMovie(TypeResource.KINOPOISK);
        configFactory.setMainGame(null);
        try {
            configFactory.afterPropertiesSet();
        } catch (PropertiesException e) {
            e.printStackTrace();
        }
        factoryPackSearch = new FactoryPackSearch(configFactory);
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