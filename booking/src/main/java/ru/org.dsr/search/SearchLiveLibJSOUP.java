package ru.org.dsr.search;

import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import ru.org.dsr.domain.Item;
import ru.org.dsr.domain.ItemID;
import ru.org.dsr.domain.Comment;
import ru.org.dsr.exception.LoadedEmptyBlocksException;
import ru.org.dsr.exception.NoFoundElementsException;
import ru.org.dsr.exception.RequestException;
import ru.org.dsr.exception.RobotException;
import ru.org.dsr.search.factory.TypeResource;

import java.io.IOException;
import java.util.*;

public class SearchLiveLibJSOUP extends AbstractSearch {
    private static final Logger log = Logger.getLogger(SearchLiveLibJSOUP.class);

    private final String USER_AGENT = "Mozilla/5.0 (Windows NT 6.1; rv:40.0) Gecko/20100101 Firefox/40.0 Chrome/74.0.3729.169 Safari/537.36";
    private final String URL_MAIN_BOOK;

    private Queue<String> books;
    private ItemID itemID;
    private int currentPage = 1;

    public SearchLiveLibJSOUP(ItemID itemID) throws RequestException, RobotException {
        super("https://www.livelib.ru/find/books/", "https://www.livelib.ru/");

        books = new LinkedList<>();

        String urlSearch = buildUrlSearch(itemID);
        Document pageSearch = getDoc(urlSearch);

        books = getUrlsBooks(pageSearch);
        URL_MAIN_BOOK = books.peek();
        this.itemID = itemID;
    }

    SearchLiveLibJSOUP() {
        super("https://www.livelib.ru/find/books/", "https://www.livelib.ru/");
        URL_MAIN_BOOK = null;
    }

    @Override
    public Item getItem() throws RobotException, RequestException {
            return isEmpty() ? null : initBook();
    }

    @Override
    public List<Comment> loadComments(int count) throws RobotException, RequestException {
        LinkedList<Comment> comments = new LinkedList<>();
        LinkedList<Comment> currentComments = new LinkedList<>();
        for(;;) {
            if (books.isEmpty()) break;
            currentComments.addAll(getComments(String.format("%s%s%d%s", books.peek(), "/~", currentPage, "#reviews\"")));
            if (currentComments.isEmpty()) {
                currentPage = 1;
                books.poll();
                break;
            } else {
                currentPage++;
            }
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
        return books == null || books.isEmpty();
    }

    @Override
    public TypeResource getTypeResource() {
        return TypeResource.LIVE_LIB;
    }

    private List<Comment> getComments (String urlBook) throws RobotException, RequestException {
        List<Comment> comments = new LinkedList<>();
        Document docBook;
        docBook = getDoc(urlBook);
        Elements elementsOfComments = docBook.select("div.group-review.review-inner"); //block comments
        for (Element e : elementsOfComments) {
            comments.add(initComment(e));
        }
        return comments;
    }

    private LinkedList<String> getUrlsBooks(Document pageBooks) {
        LinkedList<String> result = new LinkedList<>();
        Elements els = pageBooks.select("div#objects-block.objects-wrapper div.brow-title a.title");
        for (Element e :
                els) {
            result.addLast(SITE + e.attr("href"));
        }
        return result;
    }

    private Item initBook() throws RequestException, RobotException {
        Document pageBook;
        try {
            pageBook = Jsoup.connect(URL_MAIN_BOOK)
                    .userAgent(USER_AGENT)
                    .get();
        } catch (IOException e) {
            throw new RequestException(URL_MAIN_BOOK, "get");
        }

        String desc = null, name, author, urlImg;

        try {
            desc = getDescriptionBook(pageBook);
        } catch (LoadedEmptyBlocksException e) {
            log.warn(e.toString());
        } catch (NoFoundElementsException e) {
            log.warn(e.toString());
        }

        try {
            name = getNameBook(pageBook);
        } catch (LoadedEmptyBlocksException e) {
            log.warn(e.toString());
            name = itemID.getLastName();
        } catch (NoFoundElementsException e) {
            log.warn(e.toString());
            name = itemID.getLastName();
        }

        try {
            author = getNameAuthor(pageBook);
        } catch (LoadedEmptyBlocksException e) {
            log.warn(e.toString());
            author = itemID.getFirstName();
        } catch (NoFoundElementsException e) {
            log.warn(e.toString());
            author = itemID.getFirstName();
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
        return new Item(new ItemID(author, name, this.itemID.getType()), desc, urlImg);
    }

    private String getDescriptionBook(Document pageBook) throws NoFoundElementsException, LoadedEmptyBlocksException {
        Elements elements = pageBook.select("div.block p#full-description");
        if (elements == null || elements.size() == 0) {
            throw new NoFoundElementsException(URL_MAIN_BOOK, "div.block p#full-description");
        }
        String text = elements.get(0).text();
        if (text == null) {
            throw new LoadedEmptyBlocksException(URL_MAIN_BOOK, "p#full-description", "#text");
        }
        return text;
    }

    private String getNameBook(Document pageBook) throws NoFoundElementsException, LoadedEmptyBlocksException {
        Elements elsName = pageBook.select("h1#book-title span");
        if (elsName == null || elsName.size() == 0) {
            elsName = pageBook.select("h1#book-title");
        }
        if (elsName == null || elsName.size() == 0) {
            throw new NoFoundElementsException(URL_MAIN_BOOK, "h1#book-title span");
        }
        String name = elsName.get(0).text();
        if (name == null || name.equals("")) {
            throw new LoadedEmptyBlocksException(URL_MAIN_BOOK, "h1#book-title span", "#text");
        }
        return name;
    }

    private String getNameAuthor(Document pageBook) throws NoFoundElementsException, LoadedEmptyBlocksException {
        Elements elsName = pageBook.select("div.authors-maybe");
        if (elsName == null || elsName.size() == 0) {
            throw new NoFoundElementsException(URL_MAIN_BOOK, "div.authors-maybe");
        }
        String name = elsName.get(0).text();
        if (name == null || name.equals("")) {
            throw new LoadedEmptyBlocksException(URL_MAIN_BOOK, "div.authors-maybe", "#text");
        }
        return name;
    }

    private String getUrlImg(Document pageBook) throws NoFoundElementsException, LoadedEmptyBlocksException {
        Elements elsName = pageBook.select("img#main-image-book");
        if (elsName == null || elsName.size() == 0) {
            throw new NoFoundElementsException(URL_MAIN_BOOK, "img#main_image_book");
        }
        String url = elsName.get(0).attr("src");
        if (url == null || url.equals("")) {
            throw new LoadedEmptyBlocksException(URL_MAIN_BOOK, "img#main_image_book", "#src");
        }
        return url;
    }

    private Comment initComment(Element e) {
        String title = e.select("a.post-scifi-title").text().replace('\"', '\'');
        String desc = e.select("div.description").text().replace('\"', '\'');
        String date = e.select("span.date").text().replace('\"', '\'');
        String author = e.select("a.a-login-black span").text().replace('\"', '\'');
        return createComment(author, title, desc, date, SITE);
    }
}
