package ru.org.dsr.search;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import ru.org.dsr.domain.Comment;
import ru.org.dsr.domain.Item;
import ru.org.dsr.domain.ItemID;
import ru.org.dsr.exception.RequestException;
import ru.org.dsr.exception.RobotException;
import ru.org.dsr.search.Search;

import java.io.IOException;
import java.util.Scanner;

public abstract class AbstractSearch implements Search {

    private final String SEARCH;
    protected final String SITE;

    AbstractSearch(String search, String site) {
        SEARCH = search;
        SITE = site;
    }

    protected String buildUrlSearch(ItemID itemID) {
        String author = itemID.getFirstName();
        String name = itemID.getLastName();
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

    protected Document getDoc(String urlBook) throws RequestException, RobotException {
        try {
            return getDocument(urlBook);
        } catch (IOException e) {
            throw new RequestException(urlBook, "get");
        }
    }
}
