package ru.org.dsr.config;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.Scope;
import ru.org.dsr.exception.PropertiesException;
import ru.org.dsr.search.factory.TypeItem;
import ru.org.dsr.search.factory.TypeResource;

import java.util.*;

@Configuration
@PropertySource("classpath:application.properties")
@ConfigurationProperties("factory")
@Scope("singleton")
public class ConfigFactory implements InitializingBean {
    private LinkedList<TypeResource> resources = new LinkedList<>();

    private TypeResource mainMovie;
    private TypeResource mainBook;

    private HashMap<TypeItem, List<TypeResource>> data = new HashMap<>();

    public ConfigFactory() {
    }

    public LinkedList<TypeResource> getResources() {
        return resources;
    }

    public void setResources(LinkedList<TypeResource> resources) {
        this.resources = resources;
    }

    public TypeResource getMainMovie() {
        return mainMovie;
    }

    public void setMainMovie(TypeResource mainMovie) {
        this.mainMovie = mainMovie;
    }


    public TypeResource getMainBook() {
        return mainBook;
    }

    public void setMainBook(TypeResource mainBook) {
        this.mainBook = mainBook;
    }

    public HashMap<TypeItem, List<TypeResource>> getData() {
        return data;
    }

    public void setData(HashMap<TypeItem, List<TypeResource>> data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "ConfigFactory{" +
                "resources=" + resources +
                ", mainMovie='" + mainMovie + '\'' +
                ", mainBook='" + mainBook + '\'' +
                ", data=" + data +
                '}';
    }

    @Override
    public void afterPropertiesSet() throws PropertiesException {
        checkTypeItem(TypeItem.BOOK, mainBook);
        checkTypeItem(TypeItem.MOVIE, mainMovie);
        checkResources(mainBook, resources);
        checkResources(mainMovie, resources);

        for (TypeResource c :
                resources) {
            List<TypeResource> tmp;
            switch (c) {
                case LABIRINT:
                case READ_CITY:
                case LIVE_LIB:
                    if ((tmp = data.get(TypeItem.BOOK)) != null) {
                        tmp.add(c);
                    } else {
                        tmp = new LinkedList<>();
                        tmp.add(c);
                        data.put(TypeItem.BOOK, tmp);
                    }
                    break;
                case KINOPOISK:
                case IVI:
                    if ((tmp = data.get(TypeItem.MOVIE)) != null) {
                        tmp.add(c);
                    } else {
                        tmp = new LinkedList<>();
                        tmp.add(c);
                        data.put(TypeItem.MOVIE, tmp);
                    }
                    break;
            }
        }
        resources.clear();
        resources = null;
    }

    private static void checkResources(TypeResource type, LinkedList<TypeResource> resources) throws PropertiesException {
        boolean b = false;
        for (TypeResource c :
                resources) {
            if (c == type) {
                b = true;
                break;
            }
        }
        if (!b) resources.addFirst(type);
    }

    private static void checkTypeItem(TypeItem typeItem, TypeResource typeResource) throws PropertiesException {
        try {
            switch (typeResource) {
                case LIVE_LIB:
                case READ_CITY:
                case LABIRINT:
                    if (typeItem != TypeItem.BOOK) {
                        throw new PropertiesException(String.format("Search %s cannot be main search of %s", typeResource, typeItem));
                    }
                    break;
                case KINOPOISK:
                case IVI:
                    if (typeItem != TypeItem.MOVIE) {
                        throw new PropertiesException(String.format("Search %s cannot be main search of %s", typeResource, typeItem));
                    }
                    break;
            }
        } catch (NullPointerException e) {
            throw new PropertiesException(String.format("Main search of %s is not found", typeItem));
        }
    }
}