package mate.academy.bookstore.service;

import mate.academy.bookstore.dto.book.BookDto;
import mate.academy.bookstore.dto.book.BookSearchParametersDto;
import mate.academy.bookstore.dto.book.CreateBookRequestDto;
import mate.academy.bookstore.exception.EntityNotFoundException;
import mate.academy.bookstore.mapper.BookMapper;
import mate.academy.bookstore.model.Book;
import mate.academy.bookstore.model.Category;
import mate.academy.bookstore.repository.book.BookRepository;
import mate.academy.bookstore.repository.book.BookSpecificationBuilder;
import mate.academy.bookstore.repository.category.CategoryRepository;
import mate.academy.bookstore.service.impl.BookServiceImpl;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class BookServiceTest {
    private static final Long DEFAULT_ID = 1L;
    private static final BigDecimal DEFAULT_PRICE = BigDecimal.valueOf(69.0);

    private Book book;
    @Mock
    private BookSpecificationBuilder bookSpecificationBuilder;
    @Mock
    private BookMapper bookMapper;
    @Mock
    private BookRepository bookRepository;
    @Mock
    private CategoryRepository categoryRepository;
    @InjectMocks
    private BookServiceImpl bookService;

    @BeforeEach
    public void setup() {
        book = new Book();
        book.setId(DEFAULT_ID);
        book.setTitle("Robinson Crusoe");
        book.setAuthor("Daniel Defoe");
        book.setIsbn("121-000-001");
        book.setPrice(DEFAULT_PRICE);
    }

    @Test
    public void save_ValidCreateBookRequestDto_ReturnsValidBookDto() {
        CreateBookRequestDto bookRequestDto = new CreateBookRequestDto();
        bookRequestDto.setAuthor("Daniel Defoe");
        bookRequestDto.setTitle("Robinson Crusoe");
        bookRequestDto.setPrice(DEFAULT_PRICE);
        bookRequestDto.setCategoryIds(Set.of(DEFAULT_ID));
        bookRequestDto.setIsbn("121-000-001");

        Category category = new Category();
        category.setName("Adventure");
        category.setDescription("Adventure books");
        category.setId(DEFAULT_ID);
        book.setCategories(Set.of(category));

        BookDto bookDto = new BookDto();
        bookDto.setAuthor("Daniel Defoe");
        bookDto.setTitle("Robinson Crusoe");
        bookDto.setPrice(DEFAULT_PRICE);
        bookDto.setCategoryIds(Set.of(1L));
        bookDto.setIsbn("121-000-001");
        bookDto.setId(DEFAULT_ID);

        when(categoryRepository.findById(DEFAULT_ID)).thenReturn(Optional.of(category));
        when(bookMapper.toModel(bookRequestDto)).thenReturn(book);
        when(bookRepository.save(book)).thenReturn(book);
        when(bookMapper.toDto(book)).thenReturn(bookDto);

        BookDto savedBookDto = bookService.save(bookRequestDto);

        assertEquals(bookDto, savedBookDto);
    }

    @Test
    public void getBookById_validId_ok() {
        BookDto bookDto = new BookDto();
        bookDto.setId(DEFAULT_ID);
        bookDto.setTitle("Robinson Crusoe");

        when(bookRepository.findById(DEFAULT_ID)).thenReturn(Optional.of(book));
        when(bookMapper.toDto(book)).thenReturn(bookDto);

        BookDto retrievedBookDto = bookService.getBookById(DEFAULT_ID);

        assertEquals(book.getId(), retrievedBookDto.getId());
        assertNotNull(retrievedBookDto);
    }

    @Test
    public void getBookById_invalidId_notOk() {
        Long invalidBookId = 111L;

        when(bookRepository.findById(invalidBookId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> bookService.getBookById(invalidBookId));
    }

    @Test
    public void updateBookById_ValidData_ok() {
        CreateBookRequestDto updatedBookRequestDto = new CreateBookRequestDto();
        updatedBookRequestDto.setAuthor("Updated Author");
        updatedBookRequestDto.setTitle("Updated Title");

        Book updatedBook = new Book();
        updatedBook.setId(DEFAULT_ID);
        updatedBook.setAuthor("Updated Author");
        updatedBook.setTitle("Updated Title");

        BookDto updatedBookDto = new BookDto();
        updatedBookDto.setId(DEFAULT_ID);
        updatedBookDto.setAuthor("Updated Author");
        updatedBookDto.setTitle("Updated Title");

        when(bookRepository.findById(DEFAULT_ID)).thenReturn(Optional.of(book));
        when(bookMapper.toModel(updatedBookRequestDto)).thenReturn(updatedBook);
        when(bookRepository.save(updatedBook)).thenReturn(updatedBook);
        when(bookMapper.toDto(updatedBook)).thenReturn(updatedBookDto);

        BookDto resultBookDto = bookService.updateBookById(DEFAULT_ID, updatedBookRequestDto);

        assertNotNull(resultBookDto);
        assertEquals(DEFAULT_ID, resultBookDto.getId());
        assertEquals("Updated Author", resultBookDto.getAuthor());
        assertEquals("Updated Title", resultBookDto.getTitle());
    }

    @Test
    public void updateBookById_InvalidId_notOk() {
        Long invalidBookId = 111L;
        CreateBookRequestDto updatedBookRequestDto = new CreateBookRequestDto();

        when(bookRepository.findById(invalidBookId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> bookService.updateBookById(invalidBookId, updatedBookRequestDto));
    }

    @Test
    public void search_ReturnsListOfTwoBooks_WhenNoConditionsProvided() {
        Book book1 = new Book();
        book1.setId(1L);
        book1.setTitle("Book 1");

        Book book2 = new Book();
        book2.setId(2L);
        book2.setTitle("Book 2");

        List<Book> books = List.of(book1, book2);
        Page<Book> bookPage = new PageImpl<>(books);
        Specification<Book> spec = Specification.where(null);

        BookSearchParametersDto searchParameters = new BookSearchParametersDto(null, null);
        Pageable pageable = Pageable.unpaged();

        when(bookSpecificationBuilder.build(searchParameters)).thenReturn(spec);
        when(bookRepository.findAll(spec, pageable)).thenReturn(bookPage);
        when(bookMapper.toDto(eq(book1))).thenReturn(new BookDto());
        when(bookMapper.toDto(eq(book2))).thenReturn(new BookDto());

        List<BookDto> result = bookService.search(searchParameters, pageable);

        assertEquals(2, result.size());
    }
}
