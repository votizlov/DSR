package ru.org.dsr.search;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import ru.org.dsr.domain.Book;
import ru.org.dsr.domain.BookID;
import ru.org.dsr.exception.JSONImproperHandling;
import ru.org.dsr.exception.NoFoundElementsException;
import ru.org.dsr.exception.RequestException;
import ru.org.dsr.exception.RobotException;
import ru.org.dsr.model.MainLog;

import java.io.IOException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SearchReadCityJSOUP implements Search {

    private final String SITE = "https://www.chitai-gorod.ru";
    private final String SEARCH = "https://www.chitai-gorod.ru/search.php";
    private final String USER_AGENT = "Mozilla/5.0 (Windows NT 6.1; rv:40.0) Gecko/20100101 Firefox/40.0 Chrome/74.0.3729.169 Safari/537.36";
    private final int MAX_COUNT_BOOKS = 50;
    private final JSONObject JSON_MAIN_BOOK;

    private Queue<String> tmpComments;
    private Queue<JSONObject> books;
    private Logger log;

    public SearchReadCityJSOUP(BookID bookID) throws RequestException, RobotException, JSONImproperHandling {
        log = MainLog.getLog();

        tmpComments = new LinkedList<>();
        books = new LinkedList<>();

        String request = String.format("%s %s", bookID.getName(), bookID.getAuthor());
        JSONObject[] jsonArray = getJsonBooks(request);

        assert jsonArray != null;
        books.addAll(Arrays.asList(jsonArray));

        JSON_MAIN_BOOK = jsonArray[0];
    }

    @Override
    public boolean isEmpty() {
        return books.isEmpty() && tmpComments.isEmpty();
    }

    @Override
    public List<String> loadJsonComments(int count) throws RobotException {
        LinkedList<String> comments = new LinkedList<>();
        Iterator<String> it;
        if (tmpComments.isEmpty()) {
            List<String> loadedComments = null;
            while (!books.isEmpty()) {
                JSONObject urlBook = books.poll();
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
                JSONObject JSONBook = books.poll();
                List<String> loadedComments = getComments(JSONBook);
                it = loadedComments.iterator();
                for (int i = 0; i < count-comments.size() && it.hasNext(); i++) {
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
    public Book getBook() throws RobotException, RequestException {
        return initBook(JSON_MAIN_BOOK);
    }

    private Book initBook(JSONObject JSONBook) throws RobotException, RequestException {
        Book book = null;
        String name, author, description;
        try {
            Document pageBook = getDocBook(JSON_MAIN_BOOK);
            Elements elsDescription = pageBook.select("div.product__description div");
            if (elsDescription == null || elsDescription.size() == 0) {
                throw new NoFoundElementsException(JSON_MAIN_BOOK.toString(), "div.product__description div");
            }
            description = elsDescription.get(0).text();
        } catch (NoFoundElementsException | JSONImproperHandling e) {
            log.log(Level.WARNING, e.toString(), e);
            description = "Error: not found elements";
        }

        try {
            name = JSONBook.getJSONObject("_source")
                    .getString("name");
        } catch (JSONException e) {
            log.log(Level.FINE, e.toString(), e);
            name = "Error: not found elements";
        }

        //possible short last name
        try {
            author = JSONBook.getJSONObject("_source")
                    .getString("author");
        } catch (JSONException e) {
            log.log(Level.FINE, e.toString(), e);
            author = "Error: not found elements";
        }

        book = new Book(new BookID(author, name), description);

        return book;
    }

    private JSONObject[] getJsonBooks(String request) throws RobotException,
            JSONImproperHandling, RequestException {
        String json = null;
        try {
            json = Jsoup.connect(SEARCH)
                    .data("index", "goods")
                    .data("query", request)
                    .data("type", "common")
                    .data("per_page", String.valueOf(MAX_COUNT_BOOKS))
                    .userAgent(USER_AGENT)
                    .method(Connection.Method.POST)
                    .execute()
                    .body();

            if (json != null && json.contains("<META NAME=\"robots\" CONTENT=\"noindex,nofollow\">")) {
                throw new RobotException();
            }

            JSONArray jsonArray = new JSONObject(json)
                    .getJSONObject("hits")
                    .getJSONArray("hits");

            if (jsonArray == null || jsonArray.length() == 0) {
                return null;
            }

            return toArray(jsonArray);

        } catch (NullPointerException | JSONException e) {
            throw new JSONImproperHandling(json,
                    String.format("Request : %s, unknown param : hits", SEARCH));
        } catch (IOException e) {
            throw new RequestException(SEARCH, "post", new String[]{"index: goods", "query: " + request, "type: common", "per_page: MAX_COUNT_BOOKS"});
        }
    }

    private List<String> getComments (JSONObject JSONBook) throws RobotException {
        List<String> JSONComments = new LinkedList<>();
        Document docBook;
        try {
            docBook = getDocBook(JSONBook);
        }
        catch (RequestException e) {
            log.log(Level.SEVERE, e.toString(), e);
            return JSONComments;
        } catch (JSONImproperHandling e) {
            log.log(Level.WARNING, e.toString(), e);
            return JSONComments;
        }
        Elements elementsOfComments = docBook.select("div.card_review"); //block comments
        for (Element e : elementsOfComments) {
            JSONComments.add(toJsonComment(e));
        }
        return JSONComments;
    }

    private String toJsonComment(Element e) {
        String title = e.select("div.review__title").text().replace('\"', '\'');
        String desc = e.select("div.review__text.text").text().replace('\"', '\'');
        String date = e.select("div.review__date").text().replace('\"', '\'');
        String author = e.select("div.review__author").text().replace('\"', '\'');
        return toJsonComments(author, title, desc, date, SITE);
    }

    private JSONObject[] toArray(JSONArray jsonArray) throws JSONException {
        int n;
        JSONObject[] arr = new JSONObject[n = jsonArray.length()];
        for (int i = 0; i < n; i++) {
            arr[i] = jsonArray.getJSONObject(i);
        }
        return arr;
    }

    private Document getDocBook(JSONObject JSONBook) throws RequestException, JSONImproperHandling, RobotException {
        String siteBook = null;
        try {

            String suffix = JSONBook.getJSONObject("_source").getString("main_url");
            siteBook = String.format("%s%s", SITE, suffix);

            return getDocument(siteBook);

        } catch (JSONException e) {
            throw new JSONImproperHandling(JSONBook.toString(), "unknown param: _source || main_url");
        } catch (IOException e) {
            throw new RequestException(siteBook, "get");
        }
    }
}
