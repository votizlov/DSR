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

import java.util.*;

public class SearchLiveLibJSOUP extends AbstractSearch {
    private static final Logger log = Logger.getLogger(SearchLiveLibJSOUP.class);
    private final String URL_MAIN_BOOK;

    private ConfigLiveLib cnf = new ConfigLiveLib();

    private Queue<String> books;
    private Queue<Comment> temp;
    private ItemID itemID;
    private int currentPage = 1;

    public SearchLiveLibJSOUP(ItemID itemID) throws RequestException, RobotException {
        initConfig(cnf);

        books = new LinkedList<>();

        String urlSearch = buildUrlSearch(itemID);
        Document pageSearch = getDoc(urlSearch);

        books = getUrlsItem(pageSearch);
        URL_MAIN_BOOK = books.peek();
        this.itemID = itemID;
    }

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
            temp = getComments(String.format(cnf.FORM_URL_PAGE_COMMENTS, books.peek(), currentPage));
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
        Elements elementsOfComments = docBook.select(cnf.SELECT_COMMENTS); //block comments
        for (Element e : elementsOfComments) {
            comments.add(initComment(e));
        }
        return comments;
    }

    private LinkedList<String> getUrlsItem(Document pageBooks) {
        LinkedList<String> result = new LinkedList<>();
        Elements els = pageBooks.select(cnf.SELECT_ITEMS);
        for (Element e :
                els) {
            result.addLast(cnf.SITE + e.attr("href"));
        }
        return result;
    }

    private Item initBook() throws RequestException, RobotException {
        Document pageBook = getDoc(URL_MAIN_BOOK);

        String desc = null, firstName, lastName, urlImg;

        try {
            desc = getDescriptionItem(pageBook);
        } catch (LoadedEmptyBlocksException e) {
            log.warn(e.toString());
        } catch (NoFoundElementsException e) {
            log.warn(e.toString());
        }

        try {
            firstName = getFirstNameBook(pageBook);
        } catch (LoadedEmptyBlocksException e) {
            log.warn(e.toString());
            firstName = itemID.getLastName();
        } catch (NoFoundElementsException e) {
            log.warn(e.toString());
            firstName = itemID.getLastName();
        }

        try {
            lastName = getNameAuthor(pageBook);
        } catch (LoadedEmptyBlocksException e) {
            log.warn(e.toString());
            lastName = itemID.getFirstName();
        } catch (NoFoundElementsException e) {
            log.warn(e.toString());
            lastName = itemID.getFirstName();
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
        return new Item(new ItemID(firstName, lastName, this.itemID.getType()), desc, urlImg);
    }

    private String getDescriptionItem(Document pageBook) throws NoFoundElementsException, LoadedEmptyBlocksException {
        Elements elements = pageBook.select(cnf.SELECT_ITEM_DESC);
        if (elements == null || elements.size() == 0) {
            throw new NoFoundElementsException(URL_MAIN_BOOK, cnf.SELECT_ITEM_DESC);
        }
        String text = elements.get(0).text();
        if (text == null) {
            throw new LoadedEmptyBlocksException(URL_MAIN_BOOK, cnf.SELECT_ITEM_DESC, "#text");
        }
        return text;
    }

    private String getFirstNameBook(Document pageBook) throws NoFoundElementsException, LoadedEmptyBlocksException {
        Elements elsName = pageBook.select(cnf.SELECT_ITEM_FIRST_NAME);
        if (elsName == null || elsName.size() == 0) {
            elsName = pageBook.select(cnf.SELECT_ITEM_FIRST_NAME);
        }
        if (elsName == null || elsName.size() == 0) {
            throw new NoFoundElementsException(URL_MAIN_BOOK, cnf.SELECT_ITEM_FIRST_NAME);
        }
        String name = elsName.get(0).text();
        if (name == null || name.equals("")) {
            throw new LoadedEmptyBlocksException(URL_MAIN_BOOK, cnf.SELECT_ITEM_FIRST_NAME, "#text");
        }
        return name;
    }

    private String getNameAuthor(Document pageBook) throws NoFoundElementsException, LoadedEmptyBlocksException {
        Elements elsName = pageBook.select(cnf.SELECT_ITEM_LAST_NAME);
        if (elsName == null || elsName.size() == 0) {
            throw new NoFoundElementsException(URL_MAIN_BOOK, cnf.SELECT_ITEM_LAST_NAME);
        }
        String name = elsName.get(0).text();
        if (name == null || name.equals("")) {
            throw new LoadedEmptyBlocksException(URL_MAIN_BOOK, cnf.SELECT_ITEM_LAST_NAME, "#text");
        }
        return name;
    }

    private String getUrlImg(Document pageBook) throws NoFoundElementsException, LoadedEmptyBlocksException {
        Elements elsName = pageBook.select(cnf.SELECT_ITEM_URL_IMAGE);
        if (elsName == null || elsName.size() == 0) {
            throw new NoFoundElementsException(URL_MAIN_BOOK, cnf.SELECT_ITEM_URL_IMAGE);
        }
        String url = elsName.get(0).attr("src");
        if (url == null || url.equals("")) {
            throw new LoadedEmptyBlocksException(URL_MAIN_BOOK, cnf.SELECT_ITEM_URL_IMAGE, "#src");
        }
        return url;
    }

    private Comment initComment(Element e) {
        String title = e.select(cnf.SELECT_COMMENT_TITLE).text().replace('\"', '\'');
        String desc = e.select(cnf.SELECT_COMMENT_DESC).text().replace('\"', '\'');
        String date = e.select(cnf.SELECT_COMMENT_DTE).text().replace('\"', '\'');
        String author = e.select(cnf.SELECT_COMMENT_AUTHOR).text().replace('\"', '\'');
        return createComment(author, title, desc, date, cnf.SITE);
    }
}
