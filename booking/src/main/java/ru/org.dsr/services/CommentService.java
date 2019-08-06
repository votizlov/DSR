package ru.org.dsr.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.org.dsr.domain.Comment;
import ru.org.dsr.repos.CommentRepos;

import java.util.List;

@Service
public class CommentService {

    @Autowired
    CommentRepos repos;

    public void save(Comment comment) {
        repos.save(comment);
    }

    public List<Comment> getComments(long idItem, int page) {
        return repos.findByIdItemAndPage(idItem, page);
    }

    public void deleteByIdItem(long idItem) {
        List<Comment> list = repos.findByIdItem(idItem);
        for (Comment c :
                list) {
            repos.deleteById(c.getId());
        }
    }

}
