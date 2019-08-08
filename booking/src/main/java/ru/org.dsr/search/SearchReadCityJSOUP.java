package ru.org.dsr.search;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import ru.org.dsr.domain.Item;
import ru.org.dsr.domain.ItemID;
import ru.org.dsr.domain.Comment;
import ru.org.dsr.exception.JSONImproperHandling;
import ru.org.dsr.exception.NoFoundElementsException;
import ru.org.dsr.exception.RequestException;
import ru.org.dsr.exception.RobotException;
import ru.org.dsr.search.factory.TypeResource;

import java.io.IOException;
import java.util.*;

public class SearchReadCityJSOUP implements Search {
    private static final Logger log = Logger.getLogger(SearchReadCityJSOUP.class);

    private final String SITE = "https://www.chitai-gorod.ru";
    private final String SEARCH = "https://www.chitai-gorod.ru/search.php";
    private final String USER_AGENT = "Mozilla/5.0 (Windows NT 6.1; rv:40.0) Gecko/20100101 Firefox/40.0 Chrome/74.0.3729.169 Safari/537.36";
    private final int MAX_COUNT_BOOKS = 3;
    private final JSONObject JSON_MAIN_BOOK;

    private Queue<JSONObject> books;
    private Queue<Comment> temp;
    private ItemID itemID;


    public SearchReadCityJSOUP(ItemID itemID) throws RequestException, RobotException, JSONImproperHandling {

        books = new LinkedList<>();
        String request = String.format("%s %s", itemID.getFirstName(), itemID.getLastName());
        JSONObject[] jsonArray = getJsonBooks(request);

        assert jsonArray != null;
        books.addAll(Arrays.asList(jsonArray));

        JSON_MAIN_BOOK = jsonArray[0];
        this.itemID = itemID;
    }

    @Override
    public boolean isEmpty() {
        return (books == null || books.isEmpty()) && (temp == null || temp.isEmpty());    }

    @Override
    public List<Comment> loadComments(int count) throws RobotException, RequestException {
        LinkedList<Comment> comments = new LinkedList<>();
        if (temp != null)
            while (!temp.isEmpty() && count > 0) {
                comments.add(temp.poll());
                count--;
            }
        while (count>0 && !books.isEmpty()) {
            temp = getComments(books.poll());
            for (int i = 0; i < count && !temp.isEmpty(); i++) {
                comments.add(temp.poll());
            }
            count -= temp.size();
        }
        return comments;
    }

    @Override
    public Item getItem() throws RobotException, RequestException {
        return JSON_MAIN_BOOK == null ? null : initBook(JSON_MAIN_BOOK);
    }

    @Override
    public TypeResource getTypeResource() {
        return TypeResource.READ_CITY;
    }

    private Item initBook(JSONObject JSONBook) throws RobotException, RequestException {
        Item item;
        String name, author, description;
        try {
            Document pageBook = getDocBook(JSON_MAIN_BOOK);
            Elements elsDescription = pageBook.select("div.product__description div");
            if (elsDescription == null || elsDescription.size() == 0) {
                throw new NoFoundElementsException(JSON_MAIN_BOOK.toString(), "div.product__description div");
            }
            description = elsDescription.get(0).text();
        } catch (NoFoundElementsException | JSONImproperHandling e) {
            e.printStackTrace();
            description = "Error: not found elements";
        }

        try {
            name = JSONBook.getJSONObject("_source")
                    .getString("name");
        } catch (JSONException e) {
            e.printStackTrace();
            name = "Error: not found elements";
        }

        //possible short last name
        try {
            author = JSONBook.getJSONObject("_source")
                    .getString("author");
        } catch (JSONException e) {
            e.printStackTrace();
            author = "Error: not found elements";
        }

        item = new Item(new ItemID(author, name, this.itemID.getType()), description);

        return item;
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
                throw new RobotException(json);
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

    private LinkedList<Comment> getComments (JSONObject JSONBook) throws RobotException, RequestException {
        LinkedList<Comment> comments = new LinkedList<>();
        Document docBook;
        try {
            docBook = getDocBook(JSONBook);
        } catch (JSONImproperHandling e) {
            log.error(e.toString(), e);
            return comments;
        }
        Elements elementsOfComments = docBook.select("div.card_review"); //block comments
        for (Element e : elementsOfComments) {
            comments.add(getComment(e));
        }
        return comments;
    }

    private Comment getComment(Element e) {
        String title = e.select("div.review__title").text().replace('\"', '\'');
        String desc = e.select("div.review__text.text").text().replace('\"', '\'');
        String date = e.select("div.review__date").text().replace('\"', '\'');
        String author = e.select("div.review__author").text().replace('\"', '\'');
        return createComment(author, title, desc, date, SITE);
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

            return connect(siteBook, TypeResource.READ_CITY);
        } catch (JSONException e) {
            throw new JSONImproperHandling(JSONBook.toString(), "unknown param: _source || main_url");
        } catch (IOException e) {
            throw new RequestException(siteBook, "get");
        }
    }
}
