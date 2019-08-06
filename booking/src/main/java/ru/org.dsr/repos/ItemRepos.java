package ru.org.dsr.repos;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.org.dsr.domain.Item;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

public interface ItemRepos extends JpaRepository<Item, Long> {

    Item findById(long id);

    Item findByFirstNameAndLastNameAndType(String firstName, String lastName, String type);

    void deleteByDateLessThan(LocalDateTime date);

    List<Item> findByDateLessThan(LocalDateTime date);

}
