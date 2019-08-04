package ru.org.dsr.repos;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import ru.org.dsr.domain.Comment;
import ru.org.dsr.domain.Item;
import ru.org.dsr.search.factory.TypeItem;

import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface ItemRepos extends JpaRepository<Item, Long> {

    Item findById(long id);

    Item findByFirstNameAndLastNameAndType(String firstName, String lastName, String type);

    void deleteByDateLessThan(Date date);

}
