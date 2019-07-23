package ru.org.dsr.search;

import ru.org.dsr.domain.Book;
import ru.org.dsr.domain.BookID;
import ru.org.dsr.exception.JSONImproperHandling;
import ru.org.dsr.exception.RequestException;
import ru.org.dsr.exception.RobotException;
import ru.org.dsr.model.MainLog;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ManagerSearch {
   // private final String PATH_PROPERTIES = Application.class.getResourceAsStream("/manager_search.properties");

    private final int MAX_NUMBER_SRC = 2;

    private Collection<Search> sites;
    private Logger log;

    private Search liveLib;
    private Search readCity;
    private BookID bookID;

    public ManagerSearch() {}

    public void init(BookID bookID) throws RobotException {
        log = MainLog.getLog();
        sites = new LinkedList<>();
        this.bookID = bookID;
        try {
            liveLib = new SearchLiveLibJSOUP(bookID);
        } catch (RequestException e) {
            log.log(Level.SEVERE, e.toString(), e);
        } catch (RobotException e) {
            log.log(Level.FINE, "Live lib blocked", e);
        }
        try {
            readCity = new SearchReadCityJSOUP(bookID);
        } catch (RequestException e) {
            log.log(Level.SEVERE, e.toString(), e);
        } catch (RobotException e) {
            log.log(Level.FINE, "Read city blocked", e);
        } catch (JSONImproperHandling e) {
            log.log(Level.SEVERE, e.toString(), e);
        }
        if (liveLib != null && !liveLib.isEmpty()) sites.add(liveLib);
        if (readCity != null && !readCity.isEmpty()) sites.add(readCity);
        if (sites.isEmpty()) throw new RobotException();
    }

    public List<String> getComments(int count) {
        List<String> result = new ArrayList<>();
        for (; ; ) {
            int tmp = 0, n = (count) / sites.size();
            for (Search s :
                    sites) {
                List<String> comments = null;
                try {
                    comments = s.loadJsonComments(n);
                    result.addAll(comments);
                } catch (RobotException e) {
                    log.log(Level.FINE, "Src blocked", e);
                }
                if (comments == null || comments.size() == 0) continue;
                tmp += comments.size();
            }
            count -= tmp;
            if (count <= 1) break;
        }
        return result;
    }

    public Book getBook() throws RobotException {
        try {
            return liveLib.getBook();
        } catch (RequestException e) {
            log.log(Level.SEVERE, e.toString(), e);
            return new Book(bookID);
        }
    }

}
