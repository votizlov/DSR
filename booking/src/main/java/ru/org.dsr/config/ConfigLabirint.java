package ru.org.dsr.config;

import ru.org.dsr.search.factory.TypeResource;

public class ConfigLabirint extends ConfigAbstractSearch {
    private static final String _SEARCH = "https://www.labirint.ru/search/";
    private static final String _SITE = "https://www.labirint.ru";
    private static final TypeResource _TYPE_RESOURCE = TypeResource.LABIRINT;

    public final String SELECT_COMMENTS = "#product-comments > div";
    public final String SELECT_COMMENT_DESC = "div > div > div > p";
    public final String SELECT_COMMENT_DTE = "div > noindex > div > div.date";
    public final String SELECT_COMMENT_AUTHOR = "div.comment-user-info-top > div.user-name > a";
    public final String SELECT_ITEM_FIRST_NAME = "#product-title > h1";
    public final String SELECT_ITEM_LAST_NAME = "#product-specs > div.product-description > div:nth-child(2)";
    public final String SELECT_ITEM_DESC = "#product-about > p > noindex";
    public final String SELECT_ITEMS = "#rubric-tab > div.b-search-page-content > div > div.products-row-outer.responsive-cards > div > div > div > div.product-cover > a";

    public final String REVIEWS_COMMENTS = "https://www.labirint.ru/reviews/goods";

    public final String URL_IMG_FORM = "https://img2.labirint.ru/books45/%scovermid.jpg";

    public ConfigLabirint() {
        super(_SEARCH, _SITE, _TYPE_RESOURCE);
    }
}
