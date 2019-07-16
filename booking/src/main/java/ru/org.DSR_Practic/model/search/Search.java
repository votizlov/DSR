package ru.org.DSR_Practic.model.search;

import ru.org.DSR_Practic.domain.Book;
import ru.org.DSR_Practic.domain.BookID;
import ru.org.DSR_Practic.model.exception.NoFoundBookException;
import ru.org.DSR_Practic.model.exception.RobotException;

@FunctionalInterface
public interface Search {

     Book get(BookID bookID) throws NoFoundBookException, RobotException;
}
