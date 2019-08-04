package ru.org.dsr.repos;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.org.dsr.domain.Comment;

import java.beans.Customizer;
import java.util.List;

public interface CommentRepos extends JpaRepository<Comment, Long> {

    Comment findById(long id);

    List<Comment> findByIdItemAndPage(long idItem, int page);
    void deleteByIdItem(long idItem);
}
