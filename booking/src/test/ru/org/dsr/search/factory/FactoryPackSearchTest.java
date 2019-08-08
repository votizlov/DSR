package ru.org.dsr.search.factory;

import junit.framework.Assert;
import org.junit.jupiter.api.Test;
import ru.org.dsr.config.ConfigFactory;
import ru.org.dsr.domain.ItemID;
import ru.org.dsr.domain.PackSearch;
import ru.org.dsr.exception.PropertiesException;
import ru.org.dsr.search.Search;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;

class FactoryPackSearchTest {
    FactoryPackSearch factoryPackSearch;
    {
        ConfigFactory configFactory;
        configFactory = new ConfigFactory();
        configFactory.setMainBook(TypeResource.LABIRINT);
        configFactory.setMainMovie(TypeResource.KINOPOISK);
        configFactory.setResources(new LinkedList<>(Arrays.asList(TypeResource.values())));
        try {
            configFactory.afterPropertiesSet();
        } catch (PropertiesException e) {
            e.printStackTrace();
        }
        factoryPackSearch = new FactoryPackSearch(configFactory);
    }

    @Test
    void createPackSearch() {
        PackSearch packSearch = factoryPackSearch.createPackSearch(new ItemID("Остров проклятых", "", TypeItem.MOVIE));
        boolean b = true;
        for (Search s :
                packSearch.getSearches()) {
            switch (s.getTypeResource()) {
                case IVI:
                case KINOPOISK:
                    break;
                default:
                    b = false;
            }
        }
        Assert.assertTrue(b);

        packSearch = factoryPackSearch.createPackSearch(new ItemID("Автостопом по галактике", "", TypeItem.BOOK));
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