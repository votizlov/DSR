package ru.org.dsr.search;

import org.apache.log4j.Logger;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import ru.org.dsr.config.ConfigLiveLib;
import ru.org.dsr.domain.Item;
import ru.org.dsr.domain.ItemID;
import ru.org.dsr.domain.Comment;
import ru.org.dsr.exception.LoadedEmptyBlocksException;
import ru.org.dsr.exception.NoFoundElementsException;
import ru.org.dsr.exception.RequestException;
import ru.org.dsr.exception.RobotException;
import ru.org.dsr.search.factory.TypeItem;

import java.util.*;

public class SearchLiveLibJSOUP extends AbstractSearch {
    private static final Logger LOGGER = Logger.getLogger(SearchLiveLibJSOUP.class);
    private final String URL_MAIN_BOOK;

    private ConfigLiveLib cnf = new ConfigLiveLib();

    private Queue<String> books;
    private Queue<Comment> temp;
    private int currentPage = 1;

    public SearchLiveLibJSOUP(ItemID itemID) throws RequestException, RobotException {
        initConfig(cnf);

        books = new LinkedList<>();

        String urlSearch = buildUrlSearch(itemID);
        Document pageSearch = getDoc(urlSearch);

        books = getAttrsUnchecked(pageSearch, cnf.SELECT_ITEMS, "href");
        URL_MAIN_BOOK = cnf.SITE + books.peek();
    }

    //for test
    SearchLiveLibJSOUP() {
        initConfig(cnf);
        URL_MAIN_BOOK = null;
    }

    @Override
    public Item getItem() throws RobotException, RequestException {
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
        for(;count > 0 && !books.isEmpty();) {
            temp = getComments(String.format(cnf.FORM_URL_PAGE_COMMENTS, cnf.SITE, books.peek(), currentPage));
            if (temp.isEmpty()) {
                currentPage = 1;
                books.poll();
                break;
            } else {
                currentPage++;
            }
            int i;
            for (i = 0; i < count && !temp.isEmpty(); i++) {
                comments.add(temp.poll());
            }
            count -= i;
        }
        return comments;
    }

    @Override
    public boolean isEmpty() {
        return (books == null || books.isEmpty()) && (temp == null || temp.isEmpty());    }

    private LinkedList<Comment> getComments (String urlBook) throws RobotException, RequestException {
        LinkedList<Comment> comments = new LinkedList<>();
        Document docBook;
        docBook = getDoc(urlBook);
        Elements elementsOfComments = docBook.select(cnf.SELECT_COMMENTS);
        for (Element e : elementsOfComments) {
            comments.add(initComment(e, urlBook));
        }
        return comments;
    }

    private Item initBook() throws RequestException, RobotException {
        Item item = new Item();
        Document doc = getDoc(URL_MAIN_BOOK);

        try {
            item.setDesc(getText(doc, cnf.SELECT_ITEM_DESC, URL_MAIN_BOOK));
        } catch (NoFoundElementsException | LoadedEmptyBlocksException e) {
            LOGGER.warn(e);
        }

        try {
            item.setFirstName(getText(doc, cnf.SELECT_ITEM_FIRST_NAME, URL_MAIN_BOOK));
        } catch (NoFoundElementsException | LoadedEmptyBlocksException e) {
            LOGGER.warn(e);
        }

        try {
            item.setLastName(getText(doc, cnf.SELECT_ITEM_LAST_NAME, URL_MAIN_BOOK));
        } catch (NoFoundElementsException | LoadedEmptyBlocksException e) {
            LOGGER.warn(e);
        }

        try {
            item.setUrlImg(getAttr(doc, cnf.SELECT_ITEM_URL_IMAGE, "src", URL_MAIN_BOOK));
        } catch (NoFoundElementsException | LoadedEmptyBlocksException e) {
            LOGGER.warn(e);
        }
        item.setType(TypeItem.BOOK);

        return item;
    }

    private Comment initComment(Element element, String url) {
        String title, author, desc, date;
        author = desc = date = null;

        title = getTextUnchecked(element, cnf.SELECT_COMMENT_TITLE);

        try {
            author = getText(element, cnf.SELECT_COMMENT_AUTHOR, url);
        } catch (NoFoundElementsException | LoadedEmptyBlocksException e) {
            LOGGER.warn(e);
        }
        try {
            desc = getText(element, cnf.SELECT_COMMENT_DESC, url);
        } catch (NoFoundElementsException | LoadedEmptyBlocksException e) {
            LOGGER.warn(e);
        }
        try {
            date = getText(element, cnf.SELECT_COMMENT_DTE, url);
        } catch (NoFoundElementsException | LoadedEmptyBlocksException e) {
            LOGGER.warn(e);
        }
        return new Comment(cnf.SITE, author, title, desc, date);
    }
}
