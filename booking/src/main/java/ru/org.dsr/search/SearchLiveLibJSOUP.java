package ru.org.dsr.search;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import ru.org.dsr.domain.Book;
import ru.org.dsr.domain.BookID;
import ru.org.dsr.exception.LoadedEmptyBlocksException;
import ru.org.dsr.exception.NoFoundElementsException;
import ru.org.dsr.exception.RequestException;
import ru.org.dsr.exception.RobotException;
import ru.org.dsr.model.MainLog;

import java.io.IOException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SearchLiveLibJSOUP implements Search {

    private final String SITE = "https://www.livelib.ru/";
    private final String SEARCH = "https://www.livelib.ru/find/books/";
    private final String USER_AGENT = "Mozilla/5.0 (Windows NT 6.1; rv:40.0) Gecko/20100101 Firefox/40.0 Chrome/74.0.3729.169 Safari/537.36";
    private final String URL_MAIN_BOOK;

    private Queue<String> tmpComments;
    private Queue<String> books;
    private BookID bookID;
    private Logger log;

    public SearchLiveLibJSOUP(BookID bookID) throws RequestException, RobotException {

        log = MainLog.getLog();

        tmpComments = new LinkedList<>();
        books = new LinkedList<>();

        String urlSearch = buildUrlSearchBook(bookID);
        Document pageSearch = getDocBook(urlSearch);

        System.out.println(urlSearch);

        String tmp;
        if ((tmp = pageSearch.select("body").text()) == null || tmp.equals(""))
            throw new RobotException();

        books = getUrlsBooks(pageSearch);
        URL_MAIN_BOOK = books.peek();
        this.bookID = bookID;
    }


    @Override
    public Book getBook() throws RequestException, RobotException {
        System.out.println(URL_MAIN_BOOK);
        return isEmpty() ? null : initBook();
    }

    @Override
    public List<String> loadJsonComments(int count) throws RobotException {
        LinkedList<String> comments = new LinkedList<>();
        Iterator<String> it;
        if (tmpComments.isEmpty()) {
            List<String> loadedComments = null;
            while (!books.isEmpty()) {
                String urlBook = books.poll();
                loadedComments = getComments(urlBook);
                if (!loadedComments.isEmpty()) break;
            }
            assert loadedComments != null;
            if (books.isEmpty() && loadedComments.isEmpty()) return null;
            it = loadedComments.iterator();
            for (int i = 0; i < count && it.hasNext(); i++) {
                comments.add(it.next());
            }
            while (it.hasNext()) {
                tmpComments.add(it.next());
            }
        } else {
            if (tmpComments.size() > count) {
                for (int i = 0; i < count; i++) {
                    comments.add(tmpComments.poll());
                }
            } else {
                while (!tmpComments.isEmpty()) {
                    comments.add(tmpComments.poll());
                }
                String urlBook = books.poll();
                List<String> loadedComments = getComments(urlBook);
                it = loadedComments.iterator();
                for (int i = 0; i < count - comments.size() && it.hasNext(); i++) {
                    comments.add(it.next());
                }
                while (it.hasNext()) {
                    tmpComments.add(it.next());
                }
            }
        }
        return comments;
    }

    @Override
    public boolean isEmpty() {
        return books.isEmpty() && tmpComments.isEmpty();
    }

    private List<String> getComments (String urlBook) throws RobotException {
        List<String> JSONComments = new LinkedList<>();
        Document docBook;
        try {
            docBook = getDocBook(urlBook);
        }
        catch (RequestException e) {
            log.log(Level.SEVERE, e.toString(), e);
            return JSONComments;
        }

        Elements elementsOfComments = docBook.select("div.group-review.review-inner"); //block comments
        for (Element e : elementsOfComments) {
            JSONComments.add(toJsonComment(e));
        }
        return JSONComments;
    }

    private LinkedList<String> getUrlsBooks(Document pageBooks) {
        LinkedList<String> result = new LinkedList<>();
        Elements els = pageBooks.select("div#objects-block.objects-wrapper div.brow-title a.title");
        for (Element e :
                els) {
            result.addLast(SITE + e.attr("href"));
            System.out.println(SITE + e.attr("href"));
        }
        return result;
    }

    private Book initBook() throws RequestException, RobotException {
        System.out.println(URL_MAIN_BOOK);
        Document pageBook;
        try {
            pageBook = Jsoup.connect(URL_MAIN_BOOK)
                    .userAgent(USER_AGENT)
                    .get();
        } catch (IOException e) {
            throw new RequestException(URL_MAIN_BOOK, "get");
        }

        String tmp;
        if ((tmp = pageBook.select("body").text()) == null || tmp.equals(""))
            throw new RobotException();

        String text, name, author;
        text = "Error load";

        try {
            text = getDescriptionBook(pageBook);
        } catch (LoadedEmptyBlocksException e) {
            log.log(Level.FINE, e.toString(), e);
        } catch (NoFoundElementsException e) {
            log.log(Level.WARNING, e.toString(), e);
        }

        try {
            name = getNameBook(pageBook);
        } catch (LoadedEmptyBlocksException e) {
            log.log(Level.FINE, e.toString(), e);
            name = bookID.getName();
        } catch (NoFoundElementsException e) {
            log.log(Level.WARNING, e.toString(), e);
            name = bookID.getName();
        }

        try {
            author = getNameAuthor(pageBook);
        } catch (LoadedEmptyBlocksException e) {
            log.log(Level.FINE, e.toString(), e);
            author = bookID.getAuthor();
        } catch (NoFoundElementsException e) {
            log.log(Level.WARNING, e.toString(), e);
            author = bookID.getAuthor();
        }
        return new Book(new BookID(author, name), text);
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

    private Document getDocBook(String urlBook) throws RequestException, RobotException {
        try {
            return getDocument(urlBook);
        } catch (IOException e) {
            throw new RequestException(urlBook, "get");
        }
    }

    private String toJsonComment(Element e) {
        String title = e.select("a.post-scifi-title").text().replace('\"', '\'');
        String desc = e.select("div.description").text().replace('\"', '\'');
        String date = e.select("span.date").text().replace('\"', '\'');
        String author = e.select("a.a-login-black span").text().replace('\"', '\'');
        return toJsonComments(author, title, desc, date, SITE);
    }

    private String buildUrlSearchBook(BookID bookID) {
        String author = bookID.getAuthor();
        String name = bookID.getName();
        Scanner scanner = new Scanner(String.format("%s %s", name, author));
        StringBuilder stringBuilder = new StringBuilder(
                (name == null ? 0 : name.length())
                        +(author == null ? 0 :author.length())+10
        );
        if (scanner.hasNext()) {
            for(;;) {
                stringBuilder.append(scanner.next());
                if (scanner.hasNext()) {
                    stringBuilder.append('+');
                } else {
                    break;
                }
            }
        }
        return SEARCH+stringBuilder.toString();
    }

}
