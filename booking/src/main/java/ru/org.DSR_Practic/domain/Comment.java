package ru.org.DSR_Practic.domain;

import java.util.Date;

public class Comment {
    private String title;
    private String comment;
    private Date date;
    private String author;

    //private String fromSite; TODO

    public Comment(String title, String comment, Date date, String author) {
        this.title = title;
        this.comment = comment;
        this.date = date;
        this.author = author;
    }

    public Comment() {}

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }
}
