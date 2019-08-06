package ru.org.dsr.config;

import ru.org.dsr.search.factory.TypeResource;

public class ConfigKinopoisk extends AbstractConfigSearch {

    private static final String _SEARCH = "https://www.kinopoisk.ru/index.php?kp_query=";
    private static final String _SITE = "https://www.kinopoisk.ru";
    private static final TypeResource _TYPE_RESOURCE = TypeResource.KINOPOISK;

    public final String SELECT_COMMENTS = "div.reviewItem.userReview > div.response.good";
    public final String SELECT_COMMENT_TITLE  = "p.sub_title";
    public final String SELECT_COMMENT_DESC = "span._reachbanner_";
    public final String SELECT_COMMENT_DTE = "span.date";
    public final String SELECT_COMMENT_AUTHOR = "div > div > p.profile_name > a";
    public final String SELECT_ITEM_FIRST_NAME = "#headerFilm > h1 > span.moviename-title-wrapper";
    public final String SELECT_ITEM_LAST_NAME = "#infoTable > table > tbody > tr:nth-child(6) > td:nth-child(2) > a";
    public final String SELECT_ITEM_DESC = "div.brand_words.film-synopsys";
    public final String SELECT_ITEM_URL_IMAGE = "a.popupBigImage > img";
    public final String SELECT_ITEMS = "div.element.most_wanted > p.pic > a";

    public final String FORM_URL_PAGE_COMMENTS = "https://www.kinopoisk.ru%s/reviews/ord/rating/status/all/perpage/200/page/%d";

    public ConfigKinopoisk() {
        super(_SEARCH, _SITE, _TYPE_RESOURCE);
    }
}
