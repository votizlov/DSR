package ru.org.dsr.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;
import ru.org.dsr.search.factory.TypeItem;
import ru.org.dsr.search.factory.TypeResource;

import java.util.*;

@Component
@PropertySource("classpath:resource.properties")
public class ConfigFactory {
    private List<String> resources;

    @Value("${movie}")
    private String mainSearchMovie;
    @Value("${game}")
    private String mainSearchGame;
    @Value("${book}")
    private String mainSearchBook;

    private HashMap<TypeItem, List<TypeResource>> data = new HashMap<>();

    public ConfigFactory() {
        mainSearchBook = "LIVE_LIB";
                mainSearchGame = null;
        mainSearchMovie = "KINOPOISK";
        resources = new LinkedList<>();
        resources.add(mainSearchMovie);
        resources.add(mainSearchBook);
        resources.add("READ_CITY");
        resources.add("LABIRINT");
    }

    public List<String> getResources() {
        return resources;
    }

    public void setResources(List<String> resources) {
        this.resources = resources;
    }

    public String getMainSearchMovie() {
        return mainSearchMovie;
    }

    public void setMainSearchMovie(String mainSearchMovie) {
        this.mainSearchMovie = mainSearchMovie;
    }

    public String getMainSearchGame() {
        return mainSearchGame;
    }

    public void setMainSearchGame(String mainSearchGame) {
        this.mainSearchGame = mainSearchGame;
    }

    public String getMainSearchBook() {
        return mainSearchBook;
    }

    public void setMainSearchBook(String mainSearchBook) {
        this.mainSearchBook = mainSearchBook;
    }

    public HashMap<TypeItem, List<TypeResource>> getData() {
        return data;
    }

    public void setData(HashMap<TypeItem, List<TypeResource>> data) {
        this.data = data;
    }
}