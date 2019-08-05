package ru.org.dsr.search.factory;

import org.apache.log4j.Logger;
import ru.org.dsr.config.ConfigFactory;
import ru.org.dsr.domain.ItemID;
import ru.org.dsr.domain.PackSearch;
import ru.org.dsr.exception.JSONImproperHandling;
import ru.org.dsr.exception.PropertiesException;
import ru.org.dsr.exception.RequestException;
import ru.org.dsr.exception.RobotException;
import ru.org.dsr.search.*;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class FactoryPackSearch {
    private static final Logger log = Logger.getLogger(FactoryPackSearch.class);
    private TypeResource mainSearchBook;
    private TypeResource mainSearchMovie;
    private TypeResource mainSearchGame;
    private HashMap<TypeItem, List<TypeResource>> data;

    public FactoryPackSearch(ConfigFactory configFactory) {
        mainSearchBook = configFactory.getMainBook();
        mainSearchMovie = configFactory.getMainMovie();
        mainSearchGame = configFactory.getMainGame();
        data = configFactory.getData();
    }

    public PackSearch createPackSearch(ItemID itemID) {
        TypeItem typeItem = itemID.getType();
        LinkedList<Search> searches = new LinkedList<>();
        Search mainSearch = null;
        TypeResource main = null;
        switch (typeItem) {
            case GAME:
                main = mainSearchGame;
                break;
            case MOVIE:
                main = mainSearchMovie;
                break;
            case BOOK:
                main = mainSearchBook;
                break;
        }
        boolean emptyMain = false;
        for (TypeResource type :
                data.get(typeItem)) {
            try {
                Search search = createSearch(type, itemID);
                assert search != null;
                if (search.isEmpty()) {
                    emptyMain = main == type;
                    log.info(String.format("%s%s", type, " did not find"));
                } else {
                    if (type == main || emptyMain) {
                        mainSearch = search;
                        searches.add(search);
                    } else {
                        searches.add(search);
                    }
                    log.info(String.format("%s%s", type, " connected"));
                }
            } catch (RobotException e) {
                log.info(String.format("%s%s%s", type, " is close\n", e.toString()));
            } catch (RequestException | JSONImproperHandling e) {
                log.fatal("", e);
            }
        }
        return new PackSearch(searches, mainSearch);
    }

    private Search createSearch(TypeResource type, ItemID itemID) throws RobotException, RequestException, JSONImproperHandling {
        switch (type) {
            case LIVE_LIB:
                return new SearchLiveLibJSOUP(itemID);
            case READ_CITY:
                return new SearchReadCityJSOUP(itemID);
            case LABIRINT:
                return new SearchLabirintJSOUP(itemID);
            case KINOPOISK:
                return new SearchKinopoiskJSOUP(itemID);
        }
        return null;
    }
}
