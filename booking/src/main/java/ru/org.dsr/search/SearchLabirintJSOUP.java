package ru.org.dsr.search;

import org.apache.log4j.Logger;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import ru.org.dsr.domain.Comment;
import ru.org.dsr.domain.Item;
import ru.org.dsr.domain.ItemID;
import ru.org.dsr.exception.NoFoundElementsException;
import ru.org.dsr.exception.RequestException;
import ru.org.dsr.exception.RobotException;
import ru.org.dsr.search.factory.TypeItem;
import ru.org.dsr.search.factory.TypeResource;

import java.util.*;

public class SearchLabirintJSOUP extends AbstractSearch {
    private static final Logger log = Logger.getLogger(SearchLabirintJSOUP.class);
    private final String REVIEWS_COMMENTS = "https://www.labirint.ru/reviews/goods";

    private final String URL_IMG_FORM = "https://img2.labirint.ru/books45/%scovermid.jpg";
    private String urlImg;
    private String urlMainBook;

    private Queue<String> books;
    private ItemID itemID;
    private Queue<Comment> temp;

    public SearchLabirintJSOUP(ItemID itemID) throws RobotException, RequestException {
        super("https://www.labirint.ru/search/", "https://www.labirint.ru", TypeResource.LABIRINT);

        String url = buildUrlSearch(itemID);
        books = getUrlBooks(url);

        this.itemID = itemID;
    }

    SearchLabirintJSOUP() {
        super("https://www.labirint.ru/search/", "https://www.labirint.ru", TypeResource.LABIRINT);
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
            temp = getComments(books.poll());
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
        return (books == null || books.isEmpty()) && (temp == null || temp.isEmpty());
    }

    private Item initBook() throws RobotException, RequestException {
        Document document = getDoc(urlMainBook);
        String firstName = null, lastName = null, desc = null;
        try {
            lastName = getLastName(document);
        } catch (NoFoundElementsException e) {
            log.warn(e.toString());
        }
        try {
            firstName = getFirstName(document);
        } catch (NoFoundElementsException e) {
            log.warn(e.toString());
        }
        try {
            desc = getDesc(document);
        } catch (NoFoundElementsException e) {
            log.warn(e.toString());
        }
        return new Item(new ItemID(firstName, lastName, TypeItem.BOOK), desc, this.urlImg);
    }

    private String getFirstName(Document document) throws NoFoundElementsException {
        Elements elsFirstName = document.select("#product-title > h1");
        if (elsFirstName == null || elsFirstName.isEmpty()) {
            throw new NoFoundElementsException(urlMainBook, "#product-title > h1");
        }
        return elsFirstName.get(0).text();
    }

    private String getLastName(Document document) throws NoFoundElementsException {
        Elements elsLastName = document.select("#product-specs > div.product-description > div:nth-child(2)");
        if (elsLastName == null || elsLastName.isEmpty()) {
            throw new NoFoundElementsException(urlMainBook, "#product-specs > div.product-description > div:nth-child(2)");
        }
        return elsLastName.get(0).text();
    }

    private String getDesc(Document document) throws NoFoundElementsException {
        Elements elsDesc = document.select("#product-about > p > noindex");
        if (elsDesc == null || elsDesc.isEmpty()) {
            throw new NoFoundElementsException(urlMainBook, "#product-about > p > noindex");
        }
        return elsDesc.get(0).text();
    }

    private Queue<Comment> getComments (String urlBook) throws RobotException, RequestException {
        LinkedList<Comment> comments = new LinkedList<>();
        Document docBook;
        docBook = getDoc(urlBook);

        Elements els = docBook.select("#product-comments > div");
        if (els == null || els.isEmpty()) return comments;
        Element elementsComment = els.get(0);
        try {
            comments = initComments(elementsComment, urlBook);
        } catch (NoFoundElementsException e) {
            log.warn(e.toString());
        }
        return comments;
    }

    private LinkedList<Comment> initComments(Element column, String url) throws NoFoundElementsException {
        LinkedList<Comment> comments = new LinkedList<>();
        Elements elsAuthor = column.select("div.comment-user-info-top > div.user-name > a");
        if (elsAuthor == null || elsAuthor.isEmpty())
            throw new NoFoundElementsException(url, "div.comment-user-info-top > div.user-name > a");
        Elements elsDesc = column.select("div > div > div > p");
        if (elsDesc == null || elsDesc.isEmpty())
            throw new NoFoundElementsException(url, "div > div > div > p");
        Elements elsDate = column.select("div > noindex > div > div.date");
        if (elsDate == null || elsDate.isEmpty())
            throw new NoFoundElementsException(url, "div > noindex > div > div.date");
        Iterator<String> listAuthor = elementsToText(elsAuthor).iterator();
        Iterator<String> listDesc = elementsToText(elsDesc).iterator();
        Iterator<String> listDate = elementsToText(elsDate).iterator();
        while (listAuthor.hasNext() && listDate.hasNext() && listDesc.hasNext()) {
            comments.add(createComment(listAuthor.next(), "", listDesc.next(), listDate.next(), SITE));
        }
        return comments;
    }

    private List<String> elementsToText(Elements els) {
        LinkedList<String> list = new LinkedList<>();
        for (Element e :
                els) {
            list.add(elementToText(e));
        }
        return list;
    }

    private String elementToText(Element e) {
        return e.text();
    }

    private LinkedList<String> getUrlBooks(String url) throws RobotException, RequestException {
        Document document;
        document = getDoc(url);
        LinkedList<String> books = new LinkedList<>();
        Elements els = document.select("#rubric-tab > div.b-search-page-content > div > div.products-row-outer.responsive-cards > div > div > div > div.product-cover > a");
        if (els == null || els.isEmpty()) {
            return books;
        }
        String id = els.get(0).attr("href");
        urlMainBook = SITE + id;
        urlImg = String.format(URL_IMG_FORM, id.substring(7));
        for (Element e :
                els) {
            String path = e.attr("href");
            if (path.contains("books")) {
                id = path.substring(6, path.length()-1);
                books.add(String.format("%s%s%s", REVIEWS_COMMENTS, id, "/?onpage=100"));
            }
        }
        return books;
    }
}
