package ru.org.dsr.exception;

import ru.org.dsr.domain.BookID;

public class NoFoundBookException extends Exception {
    private String requestBook;

    public String getRequestBook() {
        return requestBook;
    }

    public NoFoundBookException() {}

    public NoFoundBookException(String message, BookID bookID) {
        super(message);
        requestBook = String.format("Author: %s; Book: %s", bookID.getAuthor(), bookID.getName());
    }

    public NoFoundBookException(BookID bookID) {
        super();
        requestBook = String.format("Author: %s; Book: %s", bookID.getAuthor(), bookID.getName());
    }
}
