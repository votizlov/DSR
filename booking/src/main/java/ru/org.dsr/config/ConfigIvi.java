package ru.org.dsr.config;

import ru.org.dsr.search.factory.TypeResource;

public class ConfigIvi extends AbstractConfigSearch {

    private static final String _SEARCH = "https://www.ivi.ru/search/?q=";
    private static final String _SITE = "https://www.ivi.ru";
    private static final TypeResource _TYPE_RESOURCE = TypeResource.IVI;

    public final String SELECT_COMMENTS = "#comment-list > li";
    public final String SELECT_COMMENT_DESC = "div > article";
    public final String SELECT_COMMENT_DTE = "div > header > time";
    public final String SELECT_COMMENT_AUTHOR = "div > header > cite";
    public final String SELECT_ITEM_LAST_NAME = "body > div.page-wrapper > div.content-main > section.cols-wrapper > article > dl > dd:nth-child(2) > a";
    public final String SELECT_ITEM_FIRST_NAME = "body > div.page-wrapper > div.content-main > section.top-wrapper.light > ul > li:nth-child(4)";
    public final String SELECT_ITEM_DESC = "body > div.page-wrapper > div.content-main > section.cols-wrapper > article > div.description > p:nth-child(1)";
    public final String SELECT_ITEM_URL_IMAGE = "#result-video > ul > li > a > span.image > img";
    public final String SELECT_ITEM = "#result-video > ul > li:nth-child(1) > a";

    public final String SUFFIX_COMMENTS = "/comments";

    public ConfigIvi() {
        super(_SEARCH, _SITE, _TYPE_RESOURCE);
    }
}
