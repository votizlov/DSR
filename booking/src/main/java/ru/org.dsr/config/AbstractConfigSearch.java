package ru.org.dsr.config;

import ru.org.dsr.search.factory.TypeResource;

public abstract class AbstractConfigSearch {

    public final String SEARCH;
    public final String SITE;
    public final TypeResource TYPE_RESOURCE;

    public AbstractConfigSearch(String SEARCH, String SITE, TypeResource TYPE_RESOURCE) {
        this.SEARCH = SEARCH;
        this.SITE = SITE;
        this.TYPE_RESOURCE = TYPE_RESOURCE;
    }
}
