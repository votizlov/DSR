package ru.org.dsr.search;

import org.apache.log4j.Logger;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import ru.org.dsr.domain.Comment;
import ru.org.dsr.domain.Item;
import ru.org.dsr.domain.ItemID;
import ru.org.dsr.exception.LoadedEmptyBlocksException;
import ru.org.dsr.exception.NoFoundElementsException;
import ru.org.dsr.exception.RequestException;
import ru.org.dsr.exception.RobotException;
import ru.org.dsr.search.factory.TypeResource;

import java.util.*;

public class SearchKinopoiskJSOUP extends AbstractSearch{
    private static final Logger log = Logger.getLogger(SearchKinopoiskJSOUP.class);

    private final String FORM_URL_PAGE_COMMENTS = "https://www.kinopoisk.ru%s/reviews/ord/rating/status/all/perpage/200/page/%d";
    private final String URL_MAIN_ITEM;
    private Queue<String> movies;
    private ItemID itemID;

    public SearchKinopoiskJSOUP(ItemID itemID) throws RobotException, RequestException {
        super("https://www.kinopoisk.ru/index.php?kp_query=", "https://www.kinopoisk.ru");
        String url = buildUrlSearch(itemID);
        Document document = getDoc(url);
        movies = getUrlsMovies(document);
        URL_MAIN_ITEM = SITE + movies.peek();
        if (URL_MAIN_ITEM == null)
            throw new RobotException(document.toString());
        this.itemID = itemID;
    }

    SearchKinopoiskJSOUP() {
        super("https://www.kinopoisk.ru/index.php?kp_query=", "https://www.kinopoisk.ru");
        URL_MAIN_ITEM = null;
    }

    @Override
    public Item getItem() throws RequestException, RobotException {
        return isEmpty() ? null : initBook();
    }

    @Override
    public List<Comment> loadComments(int count) throws RobotException, RequestException {
        LinkedList<Comment> comments = new LinkedList<>();
        LinkedList<Comment> currentComments = new LinkedList<>();
        for(;;) {
            if (isEmpty()) break;
            currentComments.addAll(getComments(String.format(FORM_URL_PAGE_COMMENTS, movies.poll(), 1)));
            Iterator<Comment> it = currentComments.iterator();
            for (int i = 0; i < count && it.hasNext(); i++) {
                comments.add(it.next());
            }
            if ((count -= currentComments.size()) < 0) break;
            currentComments.clear();
        }
        return comments;
    }

    @Override
    public boolean isEmpty() {
        return movies == null || movies.isEmpty();
    }

    @Override
    public TypeResource getTypeResource() {
        return TypeResource.KINOPOISK;
    }

    private List<Comment> getComments (String urlBook) throws RobotException, RequestException {
        List<Comment> comments = new LinkedList<>();
        Document docBook;
        docBook = getDoc(urlBook);

        Elements elementsOfComments = docBook.select("div.reviewItem.userReview > div.response.good"); //block comments
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

    private LinkedList<String> getUrlsMovies(Document pages) {
        LinkedList<String> result = new LinkedList<>();
        Elements els = pages.select("#block_left_pad > div > div > div > p > a");
        for (Element e :
                els) {
            result.addLast(e.attr("data-url"));
        }
        return result;
    }
}
