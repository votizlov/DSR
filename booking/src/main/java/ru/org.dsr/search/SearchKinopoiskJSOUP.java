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
    private static final Logger LOGGER = Logger.getLogger(SearchKinopoiskJSOUP.class);
    private ConfigKinopoisk cnf = new ConfigKinopoisk();

    private final String URL_ITEM;
    private final String URL_COMMENTS;
    private boolean empty;
    private String movieID;
    private Queue<Comment> temp;

    public SearchKinopoiskJSOUP(ItemID itemID) throws RobotException, RequestException {
        initConfig(cnf);
        String url = buildUrlSearch(itemID);
        Document document = getDoc(url);
        movieID = getAttrUnchecked(document, cnf.SELECT_ITEMS, "data-url");
        empty = movieID == null;
        if (!empty) {
            URL_ITEM = String.format("%s%s", cnf.SITE, movieID);
            URL_COMMENTS = String.format(cnf.FORM_URL_PAGE_COMMENTS, movieID, 1);
        } else {
            URL_COMMENTS = null;
            URL_ITEM = null;
        }
    }

    //for test
    public SearchKinopoiskJSOUP() {
        initConfig(cnf);
        URL_ITEM = null;
        empty = true;
        URL_COMMENTS = null;
    }

    @Override
    public Item getItem() throws RequestException, RobotException {
        return isEmpty() ? null : initBook();
    }

    @Override
    public List<Comment> loadComments(int count) throws RobotException, RequestException {
        LinkedList<Comment> comments = new LinkedList<>();
        if (temp != null)
            while (!temp.isEmpty() && count > 0) {
                comments.add(temp.poll());
                count--;
            }
        if (!empty) {
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

    private Queue<Comment> getComments (String urlBook) throws RobotException, RequestException {
        LinkedList<Comment> comments = new LinkedList<>();
        Document docItem;
        docItem = getDoc(urlBook);

        Elements elementsOfComments = docItem.select(cnf.SELECT_COMMENTS);
        for (Element e : elementsOfComments) {
            comments.add(initComment(e));
        }
        return comments;
    }

    private Comment initComment(Element element) {
        String title, author, desc, date;
        author = desc = date = null;

        title = getTextUnchecked(element, cnf.SELECT_COMMENT_TITLE);
        title = title == null ? "" : title;

        try {
            author = getText(element, cnf.SELECT_COMMENT_AUTHOR, URL_COMMENTS);
        } catch (NoFoundElementsException | LoadedEmptyBlocksException e) {
            LOGGER.warn(e);
        }
        try {
            desc = getText(element, cnf.SELECT_COMMENT_DESC, URL_COMMENTS);
        } catch (NoFoundElementsException | LoadedEmptyBlocksException e) {
            LOGGER.warn(e);
        }
        try {
            date = getText(element, cnf.SELECT_COMMENT_DTE, URL_COMMENTS);
        } catch (NoFoundElementsException | LoadedEmptyBlocksException e) {
            LOGGER.warn(e);
        }
        return new Comment(cnf.SITE, author, title, desc, date);
    }

    private Item initBook() throws RequestException, RobotException {
        Item item = new Item();
        Document doc = getDoc(URL_ITEM);

        try {
            item.setDesc(getText(doc, cnf.SELECT_ITEM_DESC, URL_ITEM));
        } catch (NoFoundElementsException | LoadedEmptyBlocksException e) {
            LOGGER.warn(e);
        }

        try {
            item.setFirstName(getText(doc, cnf.SELECT_ITEM_FIRST_NAME, URL_ITEM));
        } catch (NoFoundElementsException | LoadedEmptyBlocksException e) {
            LOGGER.warn(e);
        }

        try {
            item.setLastName(getText(doc, cnf.SELECT_ITEM_LAST_NAME, URL_ITEM));
        } catch (NoFoundElementsException | LoadedEmptyBlocksException e) {
            LOGGER.warn(e);
        }

        try {
            item.setUrlImg(getAttr(doc, cnf.SELECT_ITEM_URL_IMAGE, "src", URL_ITEM));
        } catch (NoFoundElementsException | LoadedEmptyBlocksException e) {
            LOGGER.warn(e);
        }
        item.setType(TypeItem.MOVIE);

        return item;
    }
}
