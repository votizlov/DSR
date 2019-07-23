package ru.org.dsr.domain;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class Book {
    private BookID bookID;
    private String desc;

    public Book(BookID bookID, String desc) {
        this.bookID = bookID;
        this.desc = desc;
    }

    public Book(BookID bookID) {
        this.bookID = bookID;
    }

    public Book() {}

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

    @Override
    public String toString() {
        return "Book{" +
                "bookID=" + bookID +
                ", desc='" + desc + '\'' +
                '}';
    }
}
