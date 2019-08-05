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

     default Document connect(String url, TypeResource type) throws IOException, RobotException {
          Document doc = Jsoup.connect(url)
                  .userAgent(USER_AGENT)
                  .get();

          switch (type) {
              case LIVE_LIB: {
                  if (doc.getElementsByClass("page-404").size() != 0)
                      throw new RobotException(doc);
                  break;
              }
              case READ_CITY: {
                  String text = doc.select("body").text();
                  if (text == null || text.isEmpty())
                      throw new RobotException(doc);
                  break;
              }
              case LABIRINT: {
                  break;
              }
              case KINOPOISK: {
                  if (doc.getElementsByClass("image form__captcha").size() != 0)
                      throw new RobotException(doc);
                  break;
              }
          }

          return doc;
     }

     default Comment createComment(String author, String title, String desc, String date, String site) {
          return new Comment(site, author, title, desc, date);
     }
}
