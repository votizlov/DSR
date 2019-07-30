package ru.org.dsr.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.org.dsr.domain.Comment;
import ru.org.dsr.repos.CommentsRepos;

import java.util.ArrayList;
import java.util.List;

@Service
public class CommentsService  {
    @Autowired
    CommentsRepos repos;

    int currentID = 1;

    public List<Comment> getAllComments() {
        List<Comment> persons = new ArrayList<Comment>();
        repos.findAll().forEach(person -> persons.add(person));
        return persons;
    }

    public Comment getPersonById(int id) {
        return repos.findById(id).get(0);
    }

    public void saveOrUpdate(Comment comment) {
        comment.setId(currentID++);
        repos.save(comment);
    }

    public void delete(int id) {
        repos.deleteById(id);
    }

    public void clear() {
        currentID = 1;
        repos.deleteAll();
    }

    public List<Comment> getCommentsFromPage(int page) {
        int start = 10*(page-1)+1;
        int end = 10*page;

        return repos.findByIdBetween(start, end);
    }
}
