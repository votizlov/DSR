package ru.org.DSR_Practic.domain;

public class BookID {
    private String author;
    private String name;

    public BookID(String author, String name) {
        this.author = author;
        this.name = name;
    }

    public BookID() {}

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
