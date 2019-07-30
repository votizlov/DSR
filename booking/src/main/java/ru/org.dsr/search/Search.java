package ru.org.dsr.search;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import ru.org.dsr.domain.Item;
import ru.org.dsr.domain.Comment;
import ru.org.dsr.exception.RequestException;
import ru.org.dsr.exception.RobotException;
import ru.org.dsr.search.factory.TypeResource;

import java.io.IOException;
import java.util.List;

public interface Search {
     String USER_AGENT = "Mozilla/5.0 (Windows NT 6.1; rv:40.0) Gecko/20100101 Firefox/40.0 Chrome/74.0.3729.169 Safari/537.36";


     Item getItem() throws RequestException, RobotException;

     List<Comment> loadComments(int count) throws RobotException, RequestException;

     boolean isEmpty();

     TypeResource getTypeResource();

     default Document getDocument(String url) throws IOException, RobotException {
          Document doc = Jsoup.connect(url)
                  .userAgent(USER_AGENT)
                  .get();

          String tmp;
          if ((tmp = doc.select("body").text()) == null || tmp.isEmpty()
                  || tmp.contains("много запросов"))
               throw new RobotException(doc.toString());

          return doc;
     }

     default Comment createComment(String author, String title, String desc, String date, String site) {
          return new Comment(site, author, title, desc, date);
     }
}
