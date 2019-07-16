package ru.org.DSR_Practic.model.search;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import ru.org.DSR_Practic.domain.Book;
import ru.org.DSR_Practic.domain.BookID;
import ru.org.DSR_Practic.model.exception.NoFoundBookException;
import ru.org.DSR_Practic.model.exception.RobotException;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

public class SearchReadCity implements Search {

    private final String SITE = "https://www.chitai-gorod.ru";
    private final String SEARCH = "/search/result/?q=";
    private final String USER_AGENT = "Mozilla/5.0 (Windows NT 6.1; rv:40.0) Gecko/20100101 Firefox/40.0 Chrome/74.0.3729.169 Safari/537.36";
    private final int MAX_COUNT_BOOKS = 30;

    @Override
    public Book get(BookID bookID) throws NoFoundBookException, RobotException {
        String request = String.format("%s %s", bookID.getName(), bookID.getAuthor());
        /*
        For ozon future
        {
            Scanner scanner = new Scanner(String.format("%s, %s", bookID.getName(), bookID.getAuthor()));
            StringBuilder stringBuilder = new StringBuilder(String.format("%s %s", SITE, SEARCH));
            if (scanner.hasNext())
                for(;;) {
                    stringBuilder.append(scanner.next());
                    if (scanner.hasNext())
                        stringBuilder.append('+');
                    else break;
                }
            else {
                throw new NoFoundBookException(bookID);
            }
            pageBooks = stringBuilder.toString();
        }
           */

        //Getting information of all books on json
        String json = null;
        try {
            json = Jsoup.connect("https://www.chitai-gorod.ru/search.php")
                    .data("index", "goods")
                    .data("query", request)
                    .data("type", "common")
                    .data("per_page", String.valueOf(MAX_COUNT_BOOKS))
                    .userAgent(USER_AGENT)
                    .method(Connection.Method.POST)
                    .execute()
                    .body();
        } catch (IOException e) {
            e.printStackTrace();
        }

        JSONArray books = null;
        if (json != null && json.contains("<META NAME=\"robots\" CONTENT=\"noindex,nofollow\">")) {
            throw new RobotException();
        }
        /*
           From the result of the query (json),
           we give an array of books in json format.
           Source in main folder (first)
         */
        try {
            books = new JSONObject(json)
                    .getJSONObject("hits")
                    .getJSONArray("hits");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        //parse to books
        Book book = null;
        if (books != null && books.length() != 0) {
            try {
                book = initBook(books.getJSONObject(0));
                for (int i = 1; i < books.length(); i++) {
                    System.out.println(book.getCommentsJSON().toString() + "   !!!!!pref!!!\n\n");
                    JSONObject JSONBook = books.getJSONObject(i);

                    List<String> comments;
                    if ((comments = getComments(JSONBook)) != null) {
                        book.addCommentsJSON(comments);
                    }
                    System.out.println(book.getCommentsJSON().toString());
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            throw new NoFoundBookException("No books found", bookID);
        }

        //testing

//        Book book;
//        {
//            book = new Book(new BookID("Адамс Дуглас", "Автостопом по галактике"), 2016);
//            book.setDesc("«Автостопом по Галактике», стартовав в качестве радиопостановки на Би-би-си, имел грандиозный успех. Одноименный роман в 1984 году возглавил список английских бестселлеров, а сам Адамс стал самым молодым писателем, получившим награду «Золотая ручка», вручаемую за 1 млн. проданных книг. \n" +
//                    "Телепостановка 1982 года упрочила успех серии книг про «Автостоп», а полнометражный фильм 2005 года при бюджете в $50 млн. дважды «отбил» расходы на экранизацию и был номинирован на 7 премий. \n" +
//                    "Популярность сатирической «трилогии в пяти частях» выплеснулась в музыкальную и компьютерную индустрию. Так, группы Radiohead, Coldplay, NOFX используют цитаты из романа Адамса, а Level 42 названа в честь главной сюжетной линии романа.");
//            List<String> list = new ArrayList<>();
//            String decs = "По образованию я психолог,читала много книг из классической и популярной психологии. В момент покупки скептически думала \"чем меня может удивить эта книга?\", и знаете - удивила. Лабковский настолько четко и понятно объясняет суть наших проблем, что задаешься вопрос - блин,как я раньше этого не понимала,несмотря на свои знания? Идеи,изложенные в книги не новы, но они преподносятся настолько доступно и так \"остро\", что они пробивают все твои легенды и аргументы(почему у меня не выходит это,почему у меня не выходит то),что тебе не остается другого выхода, как взять на себя ответственность и, наконец, разобраться со своими проблемами. Мне бы очень хотелось,чтобы многие в нашей стране прочитали эту книгу,потому что у многих абсолютная психологическая безграмотность(даже у тех людей,что окончили психологический вуз),что собственно делает нашу нацию хмурой, жертвенной и болезненной.".replace('\"', '\'');
//            String name = "Юлия".replace('\"', '|');
//            String title = "Замечательная книга!".replace('\"', '|');
//            list.add("{\"author\":\""+name+"\",\"title\":\""+title+"\",\"desc\":\""+decs+"\",\"date\":\"02.06.2017\"}");
//            list.add("{\"author\":\"Ольга\",\"title\":\"Примитивненько, не советую\",\"desc\":\"Да в книге полно юмора, да она легко читается и все такое. Меня не зацепила, юмор примитивный, сюжетная линия слишком проста и предсказуема. После прочтения осталось впечатление о зря потраченном времени.\",\"date\":\"22.06.2017\"}\n");
//            list.add("{\"author\":\"\",\"title\":\"Замечательная книга!\",\"desc\":\"Автостопом по галактике. Можно читать и перечитывать- и каждый раз с огромным интересом. Потрясающий сюжет, и юмор у Дугласа Адамса искрометный, есть сарказм, пародия- книгу можно на любой странице открыть и наслаждаться. Сюжет увлекательный и сам по себе, много неожиданных поворотов. робот с его вечной депрессией просто находка. Одна из лучших книг в современной фантастике, и перевод хороший.\",\"date\":\"23.05.2016\"}\n");
//            book.setCommentsJSON(list);
//        }


        return book;
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

            List<String> comments = getComments(JSONBook);

            book = new Book(new BookID(author, name), null, comments, year);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return book;
    }

    private List<String> getComments (JSONObject JSONBook) {
        List<String> JSONComments = new LinkedList<>();
        try {
            String suffix = JSONBook.getJSONObject("_source").getString("main_url");
            String siteBook = String.format("%s%s", SITE, suffix);
            System.out.println(siteBook); //You can visit all affected sites to check.
            Document docBook = Jsoup.connect(siteBook)
                    .userAgent(USER_AGENT)
                    .get();
            Elements comments = docBook.select("div.card_review"); //block comments
            for (Element comment : comments) {
                String title = comment.select("div.review__title").text().replace('\"', '\''); //title comment
                String desc = comment.select("div.review__text.text").text().replace('\"', '\''); //text comment
                String date = comment.select("div.review__date").text().replace('\"', '\'');
                String author = comment.select("div.review__author").text().replace('\"', '\'');
                StringBuilder JSONComment = new StringBuilder();
                {
                    JSONComment.append('{');
                    JSONComment.append(String.format("\"author\":\"%s\",", author));
                    JSONComment.append(String.format("\"title\":\"%s\",", title));
                    JSONComment.append(String.format("\"desc\":\"%s\",", desc));
                    JSONComment.append(String.format("\"date\":\"%s\"", date));
                    JSONComment.append('}');
                }
                JSONComments.add(JSONComment.toString());
            }
        } catch (JSONException | IOException e) {
            e.printStackTrace();
        }
        return JSONComments;
    }
}
