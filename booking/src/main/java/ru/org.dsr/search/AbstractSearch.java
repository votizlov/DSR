package ru.org.dsr.search;

import org.jsoup.nodes.Document;
import ru.org.dsr.domain.ItemID;
import ru.org.dsr.exception.RequestException;
import ru.org.dsr.exception.RobotException;
import ru.org.dsr.search.factory.TypeResource;

import java.io.IOException;
import java.util.Scanner;

public abstract class AbstractSearch implements Search {

    protected final String SEARCH;
    protected final String SITE;
    protected final TypeResource TYPE_RESOURCE;

    AbstractSearch(String search, String site, TypeResource typeResource) {
        SEARCH = search;
        SITE = site;
        this.TYPE_RESOURCE = typeResource;
    }

    protected String buildUrlSearch(ItemID itemID) {
        String lastName = itemID.getLastName();
        String firstName = itemID.getFirstName();
        Scanner scanner = new Scanner(String.format("%s %s", firstName, lastName));
        StringBuilder stringBuilder = new StringBuilder(
                (firstName == null ? 0 : firstName.length())
                        +(lastName == null ? 0 :lastName.length())+10
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
            return getDocument(urlBook, TYPE_RESOURCE);
        } catch (IOException e) {
            throw new RequestException(urlBook, "get");
        }
    }

    @Override
    public TypeResource getTypeResource() {
        return TYPE_RESOURCE;
    }
}
