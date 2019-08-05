package ru.org.dsr.search;

import org.apache.log4j.Logger;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import ru.org.dsr.config.ConfigKinopoisk;
import ru.org.dsr.domain.Comment;
import ru.org.dsr.domain.Item;
import ru.org.dsr.domain.ItemID;
import ru.org.dsr.exception.*;
import ru.org.dsr.search.factory.TypeItem;

import java.util.*;

public class SearchKinopoiskJSOUP extends AbstractSearch{
    private static final Logger log = Logger.getLogger(SearchKinopoiskJSOUP.class);
    private ConfigKinopoisk cnf = new ConfigKinopoisk();

    private final String URL_MAIN_ITEM;
    private boolean empty = false;
    private String movieID;
    private Queue<Comment> temp;
    private ItemID itemID;

    public SearchKinopoiskJSOUP(ItemID itemID) throws RobotException, RequestException {
        initConfig(cnf);
        String url = buildUrlSearch(itemID);
        Document document = getDoc(url);
        try {
            movieID = getUrlsItem(document);
        } catch (NoFoundElementsException e) {
            log.warn(e);
        }
        URL_MAIN_ITEM = String.format("%s%s",cnf.SITE, movieID);
        this.itemID = itemID;
    }

    SearchKinopoiskJSOUP() {
        initConfig(cnf);
        URL_MAIN_ITEM = null;
        empty = true;
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
        if (count>0 && !empty) {
            temp = getComments(String.format(cnf.FORM_URL_PAGE_COMMENTS, movieID, 1));
            int i;
            for (i = 0; i < count && !temp.isEmpty(); i++) {
                comments.add(temp.poll());
            }
            empty = true;
        }
        return comments;
    }

    @Override
    public boolean isEmpty() {
        return empty && (temp == null || temp.isEmpty());
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

        Elements elementsOfComments = docItem.select(cnf.SELECT_COMMENTS);
        for (Element e : elementsOfComments) {
            comments.add(initComment(e));
        }
        return comments;
    }

    private Comment initComment(Element e) {
        String title = e.select(cnf.SELECT_COMMENT_TITLE).text().replace('\"', '\'');
        String desc = e.select(cnf.SELECT_COMMENT_DESC).text().replace('\"', '\'');
        String date = e.select(cnf.SELECT_COMMENT_DTE).text().replace('\"', '\'');
        String author = e.select(cnf.SELECT_COMMENT_AUTHOR).text().replace('\"', '\'');
        return createComment(author, title, desc, date, cnf.SITE);
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
        return new Item(new ItemID(firstName, lastName, TypeItem.MOVIE), desc, urlImg);
    }

    private String getFirstName(Document document) throws NoFoundElementsException, LoadedEmptyBlocksException {
        Elements elsFirstName = document.select(cnf.SELECT_ITEM_FIRST_NAME);
        if (elsFirstName == null || elsFirstName.isEmpty()) {
            throw new NoFoundElementsException(URL_MAIN_ITEM, cnf.SELECT_ITEM_FIRST_NAME);
        }
        String firstName = elsFirstName.get(0).text();
        if (firstName == null || firstName.isEmpty())
            throw new LoadedEmptyBlocksException("#text", cnf.SELECT_ITEM_FIRST_NAME, URL_MAIN_ITEM);
        return firstName;
    }

    private String getLastName(Document document) throws NoFoundElementsException, LoadedEmptyBlocksException {
        Elements elsLastName = document.select(cnf.SELECT_ITEM_LAST_NAME);
        if (elsLastName == null || elsLastName.isEmpty()) {
            throw new NoFoundElementsException(URL_MAIN_ITEM, cnf.SELECT_ITEM_LAST_NAME);
        }
        String lastName = elsLastName.text();
        if (lastName == null || lastName.isEmpty())
            throw new LoadedEmptyBlocksException("#text", cnf.SELECT_ITEM_LAST_NAME, URL_MAIN_ITEM);
        return lastName;
    }

    private String getDesc(Document document) throws NoFoundElementsException, LoadedEmptyBlocksException {
        Elements elsDesc = document.select(cnf.SELECT_ITEM_DESC);
        if (elsDesc == null || elsDesc.isEmpty()) {
            throw new NoFoundElementsException(URL_MAIN_ITEM, cnf.SELECT_ITEM_DESC);
        }
        String desc = elsDesc.get(0).text();
        if (desc == null || desc.isEmpty())
            throw new LoadedEmptyBlocksException("#text", cnf.SELECT_ITEM_DESC, URL_MAIN_ITEM);
        return desc;
    }

    private String getUrlImg(Document document) throws NoFoundElementsException, LoadedEmptyBlocksException {
        Elements elsDesc = document.select(cnf.SELECT_ITEM_URL_IMAGE);
        if (elsDesc == null || elsDesc.isEmpty()) {
            throw new NoFoundElementsException(URL_MAIN_ITEM, cnf.SELECT_ITEM_URL_IMAGE);
        }
        String urlImg = elsDesc.get(0).attr("src");
        if (urlImg == null || urlImg.isEmpty())
            throw new LoadedEmptyBlocksException("#src", cnf.SELECT_ITEM_URL_IMAGE, URL_MAIN_ITEM);
        return urlImg;
    }

    private String getUrlsItem(Document pages) throws NoFoundElementsException {
        String result;
        Elements els = pages.select(cnf.SELECT_ITEMS);
        if (els.size() != 1) {
            els = pages.select(cnf.SELECT_ITEMS);
            if (els.size() != 1)
                throw new NoFoundElementsException(cnf.SEARCH, "only one : "+cnf.SELECT_ITEMS);
        }
        result = els.get(0).attr("data-url");
        return result;
    }
}
