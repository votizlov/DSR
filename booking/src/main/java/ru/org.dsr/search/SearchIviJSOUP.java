package ru.org.dsr.search;

import org.apache.log4j.Logger;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import ru.org.dsr.config.ConfigIvi;
import ru.org.dsr.domain.Comment;
import ru.org.dsr.domain.Item;
import ru.org.dsr.domain.ItemID;
import ru.org.dsr.exception.LoadedEmptyBlocksException;
import ru.org.dsr.exception.NoFoundElementsException;
import ru.org.dsr.exception.RequestException;
import ru.org.dsr.exception.RobotException;
import ru.org.dsr.search.factory.TypeItem;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class SearchIviJSOUP extends AbstractSearch {
    private static final Logger LOGGER = Logger.getLogger(SearchIviJSOUP.class.getName());

    private ConfigIvi cnf = new ConfigIvi();

    private final String MAIN_URL_MOVIE;
    private String URL_IMAGE;
    private boolean empty;
    private Queue<Comment> temp;

    public SearchIviJSOUP(ItemID itemID) throws RobotException, RequestException {
        initConfig(cnf);
        String url = buildUrlSearch(itemID);
        Document doc = getDoc(url);
        String path =  getAttrUnchecked(doc, cnf.SELECT_ITEM, "href");
        empty = path == null;
        if (!empty) {
            try {
                URL_IMAGE = getAttr(doc, cnf.SELECT_ITEM_URL_IMAGE, "src", url);
            } catch (NoFoundElementsException | LoadedEmptyBlocksException e) {
                LOGGER.warn(e);
            }
            MAIN_URL_MOVIE = cnf.SITE + path;
        } else {
            URL_IMAGE = MAIN_URL_MOVIE = null;
        }
    }

    //for test
    SearchIviJSOUP() {
        initConfig(cnf);
        empty = true;
        MAIN_URL_MOVIE = null;
    }

    @Override
    public Item getItem() throws RequestException, RobotException {
        return MAIN_URL_MOVIE == null ? null : initItem();
    }

    @Override
    public List<Comment> loadComments(int count) throws RobotException {
        LinkedList<Comment> comments = new LinkedList<>();
        if (temp != null)
            while (!temp.isEmpty() && count > 0) {
                comments.add(temp.poll());
                count--;
            }
        if (!empty) {
            try {
                temp = getComments(MAIN_URL_MOVIE + cnf.SUFFIX_COMMENTS);
            } catch (RequestException e) {
                LOGGER.info("No found comments? url = " + e.getUrl());
            }
            empty = true;
            int i;
            for (i = 0; i < count && (temp != null && !temp.isEmpty()); i++) {
                comments.add(temp.poll());
            }
        }
        return comments;
    }

    @Override
    public boolean isEmpty() {
        return empty && (temp == null || temp.isEmpty());
    }

    private Item initItem() throws RobotException, RequestException {
        Item item = new Item();
        Document doc = getDoc(MAIN_URL_MOVIE);

        try {
            item.setDesc(getText(doc, cnf.SELECT_ITEM_DESC, MAIN_URL_MOVIE));
        } catch (NoFoundElementsException | LoadedEmptyBlocksException e) {
            LOGGER.warn(e);
        }

        try {
            item.setFirstName(getText(doc, cnf.SELECT_ITEM_FIRST_NAME, MAIN_URL_MOVIE));
        } catch (NoFoundElementsException | LoadedEmptyBlocksException e) {
            LOGGER.warn(e);
        }

        try {
            item.setLastName(getText(doc, cnf.SELECT_ITEM_LAST_NAME, MAIN_URL_MOVIE));
        } catch (NoFoundElementsException | LoadedEmptyBlocksException e) {
            LOGGER.warn(e);
        }
        item.setUrlImg(URL_IMAGE);
        item.setType(TypeItem.MOVIE);

        return item;
    }

    private LinkedList<Comment> getComments(String url) throws RobotException, RequestException {
        LinkedList<Comment> comments = new LinkedList<>();
        Document docBook;
        docBook = getDoc(url);
        Elements elementsOfComments = docBook.select(cnf.SELECT_COMMENTS);
        for (Element e : elementsOfComments) {
            comments.add(initComment(e));
        }
        return comments;
    }

    private Comment initComment(Element element) {
        String author, desc, date;
        desc = date = null;

        author = getTextUnchecked(element, cnf.SELECT_COMMENT_AUTHOR);
        if (author.isEmpty()) author = "unknown";

        try {
            desc = getText(element, cnf.SELECT_COMMENT_DESC, MAIN_URL_MOVIE+cnf.SUFFIX_COMMENTS);
        } catch (NoFoundElementsException | LoadedEmptyBlocksException e) {
            LOGGER.warn(e);
        }
        try {
            date = getText(element, cnf.SELECT_COMMENT_DTE, MAIN_URL_MOVIE+cnf.SUFFIX_COMMENTS);
        } catch (NoFoundElementsException | LoadedEmptyBlocksException e) {
            LOGGER.warn(e);
        }
        return new Comment(cnf.SITE, author, "", desc, date);
    }
}
