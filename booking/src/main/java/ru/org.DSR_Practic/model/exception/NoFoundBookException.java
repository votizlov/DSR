package ru.org.DSR_Practic.model.exception;

import ru.org.DSR_Practic.domain.BookID;

public class NoFoundBookException extends Exception {
    private String requestBook;

    public String getRequestBook() {
        return requestBook;
    }

    public NoFoundBookException(String msg, BookID bookID) {
        super(msg);
        requestBook = String.format("Author: %s; Book: %s", bookID.getAuthor(), bookID.getName());
    }

    public NoFoundBookException(BookID bookID) {
        super();
        requestBook = String.format("Author: %s; Book: %s", bookID.getAuthor(), bookID.getName());
    }
}
