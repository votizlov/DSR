package ru.org.dsr.search;

import ru.org.dsr.domain.Book;
import ru.org.dsr.domain.BookID;
import ru.org.dsr.exception.NoFoundBookException;
import ru.org.dsr.exception.RobotException;

import java.util.List;

public interface Search {

     Book getBook();

     List<String> loadJsonComments(int count);
}
