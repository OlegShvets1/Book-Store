package mate.academy.bookstore.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import mate.academy.bookstore.model.Book;
import mate.academy.bookstore.repository.book.BookRepository;
import mate.academy.bookstore.repository.category.CategoryRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.jdbc.Sql;

@Sql(scripts = "classpath:database/books/add-books-and-category-to-db.sql",
        executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts = "classpath:database/books/remove-books-and-category-from-db.sql",
        executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class BookRepositoryTest {
    @Autowired
    private BookRepository bookRepository;
    @Autowired
    private CategoryRepository categoryRepository;

    @Test
    void findBooksByCategoryId_NonExistingId_ReturnEmptyList() {
        List<Book> books = bookRepository.findBooksByCategoryId(2L, null);
        assertEquals(0, books.size());
    }

    @Test
    void findBooksByCategoryId_ZeroId_ReturnEmptyList() {
        List<Book> books = bookRepository.findBooksByCategoryId(0L, null);
        assertEquals(0, books.size());
    }

    @Test
    void findBooksByCategoryId_NegativeValueId_ReturnEmptyList() {
        List<Book> books = bookRepository.findBooksByCategoryId(-3L, null);
        assertEquals(0, books.size());
    }

    @Test
    void findBooksByCategoryId_CorrectId_ReturnList() {
        List<Book> books = bookRepository.findBooksByCategoryId(1L, null);
        assertEquals(2, books.size());
    }
}
