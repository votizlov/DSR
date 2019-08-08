package ru.org.dsr.search;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import ru.org.dsr.config.AbstractConfigSearch;
import ru.org.dsr.domain.ItemID;
import ru.org.dsr.exception.LoadedEmptyBlocksException;
import ru.org.dsr.exception.NoFoundElementsException;
import ru.org.dsr.exception.RequestException;
import ru.org.dsr.exception.RobotException;
import ru.org.dsr.search.factory.TypeResource;

import java.io.IOException;
import java.util.LinkedList;
import java.util.Scanner;

public abstract class AbstractSearch implements Search {

    protected AbstractConfigSearch cnf;

    @Override
    public TypeResource getTypeResource() {
        return cnf.TYPE_RESOURCE;
    }

    String buildUrlSearch(ItemID itemID) {
        String lastName = itemID.getLastName();
        String firstName = itemID.getFirstName();
        Scanner scanner = new Scanner(String.format("%s %s", firstName, lastName));
        StringBuilder stringBuilder = new StringBuilder(
                (firstName == null ? 0 : firstName.length())+
                        (lastName == null ? 0 :lastName.length())
                        +10
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
        return cnf.SEARCH+stringBuilder.toString();
    }

    Document getDoc(String urlBook) throws RequestException, RobotException {
        try {
            return connect(urlBook, cnf.TYPE_RESOURCE);
        } catch (IOException e) {
            throw new RequestException(urlBook, "get");
        }
    }

    void initConfig(AbstractConfigSearch cnf) {
        this.cnf = cnf;
    }

    String getText(Element doc, String select, String url) throws NoFoundElementsException, LoadedEmptyBlocksException {
        String text;
        Elements els = doc.select(select);
        if (els.isEmpty()) {
            throw new NoFoundElementsException(url, select);
        }
        text = els.text().replace('\"', '\'');
        if (text.isEmpty()) {
            throw new LoadedEmptyBlocksException(select, url, "#text");
        }
        return text;
    }

    String getTextUnchecked(Element doc, String select) {
        String text;
        Elements els = doc.select(select);
        text = els.text().replace('\"', '\'');
        return text;
    }

    String getAttr(Element doc, String select, String attr, String url) throws NoFoundElementsException, LoadedEmptyBlocksException {
        String value;
        Elements els = doc.select(select);
        if (els.isEmpty()) {
            throw new NoFoundElementsException(url, select);
        }
        value = els.get(0).attr(attr);
        if (value.isEmpty()) {
            throw new LoadedEmptyBlocksException(url, select, attr);
        }
        return value;
    }

    String getAttrUnchecked(Element doc, String select, String attr) {
        String value;
        Elements els = doc.select(select);
        if (els.isEmpty()) {
            return null;
        }
        value = els.get(0).attr(attr);
        return value;
    }

    LinkedList<String> getAttrsUnchecked(Element doc, String select, String attr) {
        Elements els = doc.select(select);
        LinkedList<String> list = new LinkedList<>();
        for (Element e :
                els) {
            String s = e.attr(attr);
            list.add(s);
        }
        return list;
    }

    LinkedList<String> getAttrs(Element doc, String select, String attr, String url) throws NoFoundElementsException, LoadedEmptyBlocksException {
        Elements els = doc.select(select);
        if (els.isEmpty()) {
            throw new NoFoundElementsException(url, select);
        }
        LinkedList<String> list = new LinkedList<>();
        for (Element e :
                els) {
            String s = e.attr(attr);
            if (s.isEmpty()) {
                throw new LoadedEmptyBlocksException(url, select, attr);
            }
            list.add(e.attr(attr));
        }
        return list;
    }
}
