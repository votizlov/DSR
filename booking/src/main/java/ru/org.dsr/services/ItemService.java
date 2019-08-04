package ru.org.dsr.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.org.dsr.domain.Item;
import ru.org.dsr.domain.ItemID;
import ru.org.dsr.repos.ItemRepos;

import java.util.List;

@Service
public class ItemService {

    @Autowired
    ItemRepos repos;

    public long save(Item item) {
        repos.save(item);
        return item.getId();
    }

    public Item getById(long id) {
        return repos.findById(id);
    }

    public void clear() {
        repos.deleteAll();
    }

    public Item getByItemID(ItemID itemID) {
        return repos.findByFirstNameAndLastNameAndType(itemID.getFirstName(),
                itemID.getLastName(),
                itemID.getType().toString());
    }

    public List<Item> getAll() {
        return repos.findAll();
    }

}
