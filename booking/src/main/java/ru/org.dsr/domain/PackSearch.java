package ru.org.dsr.domain;

import ru.org.dsr.search.Search;
import sun.awt.image.ImageWatched;

import java.util.LinkedList;

public class PackSearch {
    private LinkedList<Search> searches = new LinkedList<>();
    private Search mainSearch;

    public PackSearch(LinkedList<Search> searches, Search mainSearch) {
        this.searches = searches;
        this.mainSearch = mainSearch;
    }

    public LinkedList<Search> getSearches() {
        return searches;
    }

    public Search getMainSearch() {
        return mainSearch;
    }
}
