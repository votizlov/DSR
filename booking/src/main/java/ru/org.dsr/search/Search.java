package ru.org.dsr.search;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import ru.org.dsr.domain.Book;
import ru.org.dsr.exception.RequestException;
import ru.org.dsr.exception.RobotException;

import java.io.IOException;
import java.util.List;

public interface Search {
     String USER_AGENT = "Mozilla/5.0 (Windows NT 6.1; rv:40.0) Gecko/20100101 Firefox/40.0 Chrome/74.0.3729.169 Safari/537.36";


     Book getBook() throws RequestException, RobotException;

     List<String> loadJsonComments(int count) throws RobotException;

     boolean isEmpty();

     default String toJsonComments(String author, String title, String desc, String date, String site) {
          int mitCapacityBld = 45;
          StringBuilder JSONCommentBuilder = new StringBuilder(
                  title.length()+desc.length()+date.length()+author.length()+mitCapacityBld
          );

          JSONCommentBuilder.append('{');
          JSONCommentBuilder.append(String.format("\"author\":\"%s\",", author));
          JSONCommentBuilder.append(String.format("\"title\":\"%s\",", title));
          JSONCommentBuilder.append(String.format("\"desc\":\"%s\",", desc));
          JSONCommentBuilder.append(String.format("\"date\":\"%s\",", date));
          JSONCommentBuilder.append(String.format("\"site\":\"%s\"", site));
          JSONCommentBuilder.append('}');

          return JSONCommentBuilder.toString();
     }

     default Document getDocument(String url) throws IOException, RobotException {
          Document doc = Jsoup.connect(url)
                  .userAgent(USER_AGENT)
                  .get();

          String tmp;
          if ((tmp = doc.select("body").text()) == null || tmp.equals(""))
               throw new RobotException();

          return doc;
     }
}
