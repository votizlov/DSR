package ru.org.dsr.repos;

import org.springframework.data.repository.CrudRepository;
import ru.org.dsr.domain.Comment;

import java.util.List;

public interface CommentsRepos extends CrudRepository<Comment, Integer> {

    void deleteById(int id);

    List<Comment> findById(int id);

    void deleteAll();

    List<Comment> findByIdBetween(int idStart, int idEnd);

}
