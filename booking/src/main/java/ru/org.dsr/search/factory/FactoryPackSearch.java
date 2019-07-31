package ru.org.dsr.search.factory;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import ru.org.dsr.config.ConfigFactory;
import ru.org.dsr.domain.ItemID;
import ru.org.dsr.domain.PackSearch;
import ru.org.dsr.exception.*;
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
    private String mainSearchBook;
    private String mainSearchMovie;
    private String mainSearchGame;
    private List<String> resource;
    private HashMap<TypeItem, List<TypeResource>> data;

    public FactoryPackSearch(ConfigFactory configFactory) throws PropertiesException {
//        if (!checkConfiguration(mainSearchMovie)) {
//            throw new PropertiesException("mainSearchMovie didn't find");
//        }
//        if (!checkConfiguration(mainSearchGame)) {
//            throw new PropertiesException("mainSearchGame didn't find");
//        }
//        if (!checkConfiguration(mainSearchBook)) {
//            throw new PropertiesException("mainSearchBook didn't find");
//        }
        configFactory = new ConfigFactory();
        mainSearchBook = configFactory.getMainSearchBook();
        mainSearchMovie = configFactory.getMainSearchMovie();
        mainSearchGame = configFactory.getMainSearchGame();
        resource = configFactory.getResources();
        data = configFactory.getData();
        for (String c :
                resource) {
            List<TypeResource> tmp;
            if (c == null) continue;
            switch (c) {
                case "LIVE_LIB":
                    if ((tmp = data.get(TypeItem.BOOK)) != null) {
                        tmp.add(TypeResource.LIVE_LIB);
                    } else {
                        tmp = new LinkedList<>();
                        tmp.add(TypeResource.LIVE_LIB);
                        data.put(TypeItem.BOOK, tmp);
                    }
                    break;
                case "READ_CITY":
                    if ((tmp = data.get(TypeItem.BOOK)) != null) {
                        tmp.add(TypeResource.READ_CITY);
                    } else {
                        tmp = new LinkedList<>();
                        tmp.add(TypeResource.READ_CITY);
                        data.put(TypeItem.BOOK, tmp);
                    }
                    break;
                case "LABIRINT":
                    if ((tmp = data.get(TypeItem.BOOK)) != null) {
                        tmp.add(TypeResource.LABIRINT);
                    } else {
                        tmp = new LinkedList<>();
                        tmp.add(TypeResource.LABIRINT);
                        data.put(TypeItem.BOOK, tmp);
                    }
                    break;
                case "KINOPOISK":
                    if ((tmp = data.get(TypeItem.MOVIE)) != null) {
                        tmp.add(TypeResource.KINOPOISK);
                    } else {
                        tmp = new LinkedList<>();
                        tmp.add(TypeResource.KINOPOISK);
                        data.put(TypeItem.MOVIE, tmp);
                    }
                    break;
            }
        }
    }

    public PackSearch createPackSearch(ItemID itemID) {
        TypeItem typeItem = TypeItem.valueOf(itemID.getType());
        LinkedList<Search> searches = new LinkedList<>();
        Search mainSearch = null;
        switch (typeItem) {
            case BOOK: {
                TypeResource main = TypeResource.valueOf(mainSearchBook);
                for (TypeResource type :
                        data.get(TypeItem.BOOK)) {
                    try {
                        if (type == main) {
                            mainSearch = createSearch(type, itemID);
                            searches.add(mainSearch);
                        } else {
                            searches.add(createSearch(type, itemID));
                        }
                        log.info(String.format("%s%s", type, " connected"));
                    } catch (RobotException e) {
                        log.info(String.format("%s%s%s", type, " is close\n", e.toString()));
                    } catch (RequestException e) {
                        log.fatal(e.toString(), e);
                    } catch (JSONImproperHandling e) {
                        log.fatal(e.toString(), e);
                    }
                }
                break;
            }
            case MOVIE: {
                TypeResource main = TypeResource.valueOf(mainSearchMovie);
                for (TypeResource type :
                        data.get(TypeItem.MOVIE)) {
                    try {
                        if (type == main) {
                            mainSearch = createSearch(type, itemID);
                            searches.add(mainSearch);
                        } else {
                            searches.add(createSearch(type, itemID));
                        }
                        log.info(String.format("%s%s", type, " connected"));
                    } catch (RobotException e) {
                        log.info(String.format("%s%s%s", type, " is close\n", e.getSrcForRobot()));
                    } catch (RequestException e) {
                        log.fatal(e.toString(), e);
                    } catch (JSONImproperHandling e) {
                        log.fatal(e.toString(), e);
                    }
                }
                break;
            }
            case GAME: {
                TypeResource main = TypeResource.valueOf(mainSearchGame);
                for (TypeResource type :
                        data.get(TypeItem.GAME)) {
                    try {
                        if (type == main) {
                            mainSearch = createSearch(type, itemID);
                            searches.add(mainSearch);
                        } else {
                            searches.add(createSearch(type, itemID));
                        }
                        log.info(String.format("%s%s", type, " connected"));
                    } catch (RobotException e) {
                        log.info(String.format("%s%s%s", type, " is close\n", e.getSrcForRobot()));
                    } catch (RequestException e) {
                        log.fatal(e.toString(), e);
                    } catch (JSONImproperHandling e) {
                        log.fatal(e.toString(), e);
                    }
                }
                break;
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

    private boolean checkConfiguration(String nameSearch) {
        boolean b = false;
        for (String c :
                resource) {
            if (c.equals(nameSearch)) b = true;
        }
        return b;
    }
}
