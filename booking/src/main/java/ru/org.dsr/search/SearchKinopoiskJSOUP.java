package ru.org.dsr.search;

import org.apache.log4j.Logger;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import ru.org.dsr.domain.Comment;
import ru.org.dsr.domain.Item;
import ru.org.dsr.domain.ItemID;
import ru.org.dsr.exception.*;
import ru.org.dsr.search.factory.TypeResource;

import java.util.*;

public class SearchKinopoiskJSOUP extends AbstractSearch{
    private static final Logger log = Logger.getLogger(SearchKinopoiskJSOUP.class);

    private final String FORM_URL_PAGE_COMMENTS = "https://www.kinopoisk.ru%s/reviews/ord/rating/status/all/perpage/200/page/%d";
    private final String URL_MAIN_ITEM;
    private String movieID;
    private Queue<Comment> temp;
    private ItemID itemID;

    public SearchKinopoiskJSOUP(ItemID itemID) throws RobotException, RequestException {
        super("https://www.kinopoisk.ru/index.php?kp_query=", "https://www.kinopoisk.ru", TypeResource.KINOPOISK);
        String url = buildUrlSearch(itemID);
        Document document = getDoc(url);
        try {
            movieID = getUrlsItem(document);
        } catch (NoFoundElementsException e) {
            log.warn(e);
        }
        URL_MAIN_ITEM = String.format("%s%s",SITE, movieID);
        this.itemID = itemID;
    }

    SearchKinopoiskJSOUP() {
        super("https://www.kinopoisk.ru/index.php?kp_query=", "https://www.kinopoisk.ru", TypeResource.KINOPOISK);
        URL_MAIN_ITEM = null;
    }

    @Override
    public Item getItem() throws RequestException, RobotException {
        return isEmpty() ? null : initBook();
    }

    @Override
    public List<Comment> loadComments(int count) throws RobotException {
        LinkedList<Comment> comments = new LinkedList<>();
        if (temp != null)
            while (!temp.isEmpty() && count > 0) {
                comments.add(temp.poll());
                count--;
            }
        if (count>0 && movieID!=null) {
            temp = getComments(String.format(FORM_URL_PAGE_COMMENTS, movieID, 1));
            int i;
            for (i = 0; i < count && !temp.isEmpty(); i++) {
                comments.add(temp.poll());
            }
            movieID = null;
        }
        return comments;
    }

    @Override
    public boolean isEmpty() {
        return (movieID == null && temp == null) ||  (movieID == null && temp.isEmpty());
    }

    private Queue<Comment> getComments (String urlBook) throws RobotException {
        LinkedList<Comment> comments = new LinkedList<>();
        Document docItem;
        try {
            docItem = getDoc(urlBook);
        } catch (RequestException e) {
            log.info(e+"\nDidn't comments find?");
            return comments;
        }

        Elements elementsOfComments = docItem.select("div.reviewItem.userReview > div.response.good"); //block comments
        for (Element e : elementsOfComments) {
            comments.add(initComment(e));
        }
        return comments;
    }

    private Comment initComment(Element e) {
        String title = e.select("p.sub_title").text().replace('\"', '\'');
        String desc = e.select("span._reachbanner_").text().replace('\"', '\'');
        String date = e.select("span.date").text().replace('\"', '\'');
        String author = e.select("div > div > p.profile_name > a").text().replace('\"', '\'');
        return createComment(author, title, desc, date, SITE);
    }

    private Item initBook() throws RequestException, RobotException {
        Document pageBook = getDoc(URL_MAIN_ITEM);

        String desc = null, firstName, lastName, urlImg;

        try {
            desc = getDesc(pageBook);
        } catch (LoadedEmptyBlocksException e) {
            log.warn(e.toString());
        } catch (NoFoundElementsException e) {
            log.warn(e.toString());
        }

        try {
            firstName = getFirstName(pageBook);
        } catch (LoadedEmptyBlocksException e) {
            log.warn(e.toString());
            firstName = itemID.getFirstName();
        } catch (NoFoundElementsException e) {
            log.warn(e.toString());
            firstName = itemID.getFirstName();
        }

        try {
            lastName = getLastName(pageBook);
        } catch (LoadedEmptyBlocksException e) {
            log.warn(e.toString());
            lastName = itemID.getLastName();
        } catch (NoFoundElementsException e) {
            log.warn(e.toString());
            lastName = itemID.getLastName();
        }

        try {
            urlImg = getUrlImg(pageBook);
        } catch (LoadedEmptyBlocksException e) {
            log.warn(e.toString());
            urlImg = "";
        } catch (NoFoundElementsException e) {
            log.warn(e.toString());
            urlImg = "";
        }
        return new Item(new ItemID(firstName, lastName, "MOVIE"), desc, urlImg);
    }

    private String getFirstName(Document document) throws NoFoundElementsException, LoadedEmptyBlocksException {
        Elements elsFirstName = document.select("#headerFilm > h1 > span.moviename-title-wrapper");
        if (elsFirstName == null || elsFirstName.isEmpty()) {
            throw new NoFoundElementsException(URL_MAIN_ITEM, "#headerFilm > h1 > span.moviename-title-wrapper");
        }
        String firstName = elsFirstName.get(0).text();
        if (firstName == null || firstName.isEmpty())
            throw new LoadedEmptyBlocksException("#text", "#headerFilm > h1 > span.moviename-title-wrapper", URL_MAIN_ITEM);
        return firstName;
    }

    private String getLastName(Document document) throws NoFoundElementsException, LoadedEmptyBlocksException {
        Elements elsLastName = document.select("#infoTable > table > tbody > tr:nth-child(6) > td:nth-child(2) > a");
        if (elsLastName == null || elsLastName.isEmpty()) {
            throw new NoFoundElementsException(URL_MAIN_ITEM, "#infoTable > table > tbody > tr:nth-child(6) > td:nth-child(2) > a");
        }
        String lastName = elsLastName.text();
        if (lastName == null || lastName.isEmpty())
            throw new LoadedEmptyBlocksException("#text", "#infoTable > table > tbody > tr:nth-child(6) > td:nth-child(2) > a", URL_MAIN_ITEM);
        return lastName;
    }

    private String getDesc(Document document) throws NoFoundElementsException, LoadedEmptyBlocksException {
        Elements elsDesc = document.select("#syn > tbody > tr:nth-child(1) > td > table > tbody > tr:nth-child(1) > td > span > div");
        if (elsDesc == null || elsDesc.isEmpty()) {
            throw new NoFoundElementsException(URL_MAIN_ITEM, "#syn > tbody > tr:nth-child(1) > td > table > tbody > tr:nth-child(1) > td > span > div");
        }
        String desc = elsDesc.get(0).text();
        if (desc == null || desc.isEmpty())
            throw new LoadedEmptyBlocksException("#text", "#infoTable > table > tbody > tr:nth-child(6) > td:nth-child(2) > a", URL_MAIN_ITEM);
        return desc;
    }

    private String getUrlImg(Document document) throws NoFoundElementsException, LoadedEmptyBlocksException {
        Elements elsDesc = document.select("a.popupBigImage > img");
        if (elsDesc == null || elsDesc.isEmpty()) {
            throw new NoFoundElementsException(URL_MAIN_ITEM, "a.popupBigImage > img");
        }
        String urlImg = elsDesc.get(0).attr("src");
        if (urlImg == null || urlImg.isEmpty())
            throw new LoadedEmptyBlocksException("#src", "a.popupBigImage > img", URL_MAIN_ITEM);
        return urlImg;
    }

    private String getUrlsItem(Document pages) throws NoFoundElementsException {
        String result;
        Elements els = pages.select("div.element.most_wanted > p.pic > a");
        if (els.size() != 1) {
            els = pages.select("div.element > p.pic > a");
            if (els.size() != 1)
                throw new NoFoundElementsException(SEARCH, "only one : div.element.most_wanted > p.pic > a");
        }
        result = els.get(0).attr("data-url");
        return result;
    }
}
