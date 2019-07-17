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
import ru.org.dsr.exception.NoFoundBookException;
import ru.org.dsr.exception.RequestException;
import ru.org.dsr.exception.RobotException;

import java.io.IOException;
import java.util.*;

public class SearchReadCityJSOUP implements Search {

    private final String SITE = "https://www.chitai-gorod.ru";
    private final String SEARCH = "https://www.chitai-gorod.ru/search.php";
    private final String USER_AGENT = "Mozilla/5.0 (Windows NT 6.1; rv:40.0) Gecko/20100101 Firefox/40.0 Chrome/74.0.3729.169 Safari/537.36";
    private final int MAX_COUNT_BOOKS = 50;
    private final int MIN_CAPACITY_COMMENT = 45;

    private Queue<String> cache;
    private Queue<JSONObject> books;
    private JSONObject JSONMainBook;

    public SearchReadCityJSOUP(BookID bookID) throws RobotException, NoFoundBookException {
        cache = new LinkedList<>();
        books = new LinkedList<>();
        String request = String.format("%s %s", bookID.getName(), bookID.getAuthor());

        try {
            JSONObject[] jsonArray = getJsonBooks(request);
            books.addAll(Arrays.asList(jsonArray));
            JSONMainBook = jsonArray[0];
        } catch (JSONImproperHandling | RequestException e) {
            //TODO log
        }
    }

    @Override
    public List<String> loadJsonComments(int count) {
        LinkedList<String> comments = new LinkedList<>();
        Iterator<String> it;
        if (cache.isEmpty()) {
            if (books.isEmpty()) return null;
            JSONObject JSONBook = books.poll();
            List<String> loadedComments = getComments(JSONBook);
            it = loadedComments.iterator();
            for (int i = 0; i < count && it.hasNext(); i++) {
                comments.add(it.next());
            }
            while (it.hasNext()) {
                cache.add(it.next());
            }
        } else {
            if (cache.size() > count) {
                for (int i = 0; i < count; i++) {
                    comments.add(cache.poll());
                }
            } else {
                while (!cache.isEmpty()) {
                    comments.add(cache.poll());
                }
                JSONObject JSONBook = books.poll();
                List<String> loadedComments = getComments(JSONBook);
                it = loadedComments.iterator();
                for (int i = 0; i < count-comments.size() && it.hasNext(); i++) {
                    comments.add(it.next());
                }
                while (it.hasNext()) {
                    cache.add(it.next());
                }
            }
        }
        return comments;
    }

    @Override
    public Book getBook() {
        return initBook(JSONMainBook);
    }

    private Book initBook(JSONObject JSONBook) {
        Book book = null;
        try {
            String name = JSONBook.getJSONObject("_source")
                    .getString("name");

            //possible short last name
            String author = JSONBook.getJSONObject("_source")
                    .getString("author");

            Integer year = JSONBook.getJSONObject("_source")
                    .getInt("year");

            book = new Book(new BookID(author, name), year);
        } catch (JSONException e) {
            //TODO log
        }

        return book;
    }

    private JSONObject[] getJsonBooks(String request) throws NoFoundBookException, RobotException,
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
                throw new NoFoundBookException();
            }

            return toArray(new JSONObject(json)
                    .getJSONObject("hits")
                    .getJSONArray("hits"));

        } catch (NullPointerException | JSONException e) {
            throw new JSONImproperHandling(json,
                    String.format("Request : %s, unknown param : hits", SEARCH));
        } catch (IOException e) {
            throw new RequestException(SEARCH, "post", new String[]{"index: goods", "query: " + request, "type: common", "per_page: MAX_COUNT_BOOKS"});
        }
    }

    private List<String> getComments (JSONObject JSONBook) {
        List<String> JSONComments = new LinkedList<>();
        Document docBook;
        try {
            docBook = getDocBook(JSONBook);
        }
        catch (RequestException | JSONImproperHandling e) {
            //TODO log
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
        StringBuilder JSONCommentBuilder = new StringBuilder(
                title.length()+desc.length()+date.length()+author.length()+MIN_CAPACITY_COMMENT
        );

        JSONCommentBuilder.append('{');
        JSONCommentBuilder.append(String.format("\"author\":\"%s\",", author));
        JSONCommentBuilder.append(String.format("\"title\":\"%s\",", title));
        JSONCommentBuilder.append(String.format("\"desc\":\"%s\",", desc));
        JSONCommentBuilder.append(String.format("\"date\":\"%s\"", date));
        JSONCommentBuilder.append('}');

        return JSONCommentBuilder.toString();
    }

    private JSONObject[] toArray(JSONArray jsonArray) throws JSONException {
        int n;
        JSONObject[] arr = new JSONObject[n = jsonArray.length()];
        for (int i = 0; i < n; i++) {
            arr[i] = jsonArray.getJSONObject(i);
        }
        return arr;
    }

    private Document getDocBook(JSONObject JSONBook) throws RequestException, JSONImproperHandling {
        String siteBook = null;
        try {

            String suffix = JSONBook.getJSONObject("_source").getString("main_url");
            siteBook = String.format("%s%s", SITE, suffix);

            return Jsoup.connect(siteBook)
                    .userAgent(USER_AGENT)
                    .get();

        } catch (JSONException e) {
            throw new JSONImproperHandling(JSONBook.toString(), "unknown param: _source || main_url");
        }
        catch (IOException e) {
            throw new RequestException(siteBook, "get");
        }
    }
}
