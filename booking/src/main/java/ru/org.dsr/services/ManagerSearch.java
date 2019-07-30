package ru.org.dsr.services;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;
import ru.org.dsr.domain.Item;
import ru.org.dsr.domain.ItemID;
import ru.org.dsr.domain.Comment;
import ru.org.dsr.exception.PropertiesException;
import ru.org.dsr.exception.RequestException;
import ru.org.dsr.exception.RobotException;
import ru.org.dsr.search.Search;
import ru.org.dsr.search.factory.FactoryPackSearch;
import ru.org.dsr.domain.PackSearch;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Service
public class ManagerSearch {
    private static final Logger log = Logger.getLogger(ManagerSearch.class);

    private Collection<Search> sites;
    private Search mainSearch;

    private FactoryPackSearch factory;

    public ManagerSearch() {}

    public void init(ItemID itemID) throws RobotException {
        try {
            factory = new FactoryPackSearch();
        } catch (PropertiesException e) {
            log.fatal(e.getNeed(), e);
        }
        PackSearch packSearch = factory.createPackSearch(itemID);
        sites = packSearch.getSearches();
        mainSearch = packSearch.getMainSearch();
        if (sites.isEmpty()) {
            throw new RobotException("all resource is close");
        }
    }

    public List<Comment> getComments(int count) {
        if (sites.isEmpty()) {
            return null;
        }

        List<Comment> result = new ArrayList<>();
        for (; ; ) {
            int tmp = 0, n = (count) / sites.size();
            List<Search> deleted = new ArrayList<>(sites.size());
            for (Search s :
                    sites) {
                List<Comment> comments = null;
                try {
                    comments = s.loadComments(n);
                    if (!comments.isEmpty())
                    result.addAll(comments);
                    log.info(String.format("%s gave %d comments", s.getTypeResource(), comments.size()));
                } catch (RobotException e) {
                    deleted.add(s);
                    log.info(s.getTypeResource() + " is close\n" + e.getSrcForRobot());
                } catch (RequestException e) {
                    deleted.add(s);
                    log.fatal(e.toString(), e);
                }
                if (comments == null || comments.size() == 0) continue;
                tmp += comments.size();
            }
            sites.removeAll(deleted);
            count -= tmp;
            if (count <= 1 || tmp == 0) break;
        }
        return result;
    }

    public Item getItem() {
        try {
            return mainSearch.getItem();
        } catch (RequestException e) {
            log.fatal(e.toString(), e);
        } catch (RobotException e) {
            log.info(mainSearch.getTypeResource() + " is close\n" + e.getSrcForRobot());
        }
        return null;
    }

    public boolean isEmpty() {
        return sites == null || sites.isEmpty();
    }
}
