package ru.org.dsr.domain;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class Book {
    private BookID bookID;
    private String desc;
    private List<String> commentsJSON;
    private Integer year;

    public Book(BookID bookID, String desc, List<String> commentsJSON, Integer year) {
        this.bookID = bookID;
        this.desc = desc;
        this.commentsJSON = commentsJSON;
        this.year = year;
    }

    public Book(BookID bookID, Integer year) {
        this();
        this.bookID = bookID;
        this.year = year;
    }

    public Book(BookID bookID) {
        this();
        this.bookID = bookID;
    }

    public Book() {
        commentsJSON = new ArrayList<>();
    }

    public BookID getBookID() {
        return bookID;
    }

    public void setBookID(BookID bookID) {
        this.bookID = bookID;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public Collection<String> getCommentsJSON() {
        return commentsJSON;
    }

    public void setCommentsJSON(List<String> commentsJSON) {
        this.commentsJSON = commentsJSON;
    }

    public void addCommentsJSON(Collection<String> commentsJSON) {
        this.commentsJSON.addAll(commentsJSON);
    }

    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }
}
