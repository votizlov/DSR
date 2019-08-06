package ru.org.dsr.config;

import ru.org.dsr.search.factory.TypeResource;

public class ConfigLiveLib extends AbstractConfigSearch {
    private static final String _SEARCH = "https://www.livelib.ru/find/books/";
    private static final String _SITE = "https://www.livelib.ru/";
    private static final TypeResource _TYPE_RESOURCE = TypeResource.LIVE_LIB;

    public final String SELECT_COMMENTS = "div.group-review.review-inner";
    public final String SELECT_COMMENT_TITLE  = "a.post-scifi-title";
    public final String SELECT_COMMENT_DESC = "div.description";
    public final String SELECT_COMMENT_DTE = "span.date";
    public final String SELECT_COMMENT_AUTHOR = "a.a-login-black span";
    public final String SELECT_ITEM_FIRST_NAME = "h1#book-title span";
    public final String SELECT_ITEM_LAST_NAME = "div.authors-maybe";
    public final String SELECT_ITEM_DESC = "div#book-right-data-left.book-right-data-left > div.block";
    public final String SELECT_ITEM_URL_IMAGE = "img#main-image-book";
    public final String SELECT_ITEMS = "div#objects-block.objects-wrapper div.brow-title a.title";

    public final String FORM_URL_PAGE_COMMENTS = "%s%s/~%d#reviews\"";

    public ConfigLiveLib() {
        super(_SEARCH, _SITE, _TYPE_RESOURCE);
    }
}
