package mate.academy.bookstore.service;

import java.util.List;
import mate.academy.bookstore.dto.book.BookDto;
import mate.academy.bookstore.dto.book.BookSearchParametersDto;
import mate.academy.bookstore.dto.book.CreateBookRequestDto;
import org.springframework.data.domain.Pageable;

public interface BookService {
    BookDto save(CreateBookRequestDto bookRequestDto);

    BookDto getBookById(Long id);

    List<BookDto> findAll(Pageable pageable);

    void deleteById(Long id);

    BookDto updateBookById(Long id, CreateBookRequestDto bookRequestDto);

    List<BookDto> search(BookSearchParametersDto searchParameter, Pageable pageable);

    List<BookDto> findAllByCategoryId(Long categoryId, Pageable pageable);
}
