package mate.academy.bookstore.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import mate.academy.bookstore.dto.book.BookDto;
import mate.academy.bookstore.dto.book.BookSearchParametersDto;
import mate.academy.bookstore.dto.book.CreateBookRequestDto;
import mate.academy.bookstore.service.BookService;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Book management", description = "Book management endpoints")
@RequiredArgsConstructor
@RequestMapping(value = "/api/books")
@RestController
public class BookController {
    private final BookService bookService;

    @Operation(summary = "Get all books", description = "Get all books")
    @PreAuthorize("hasRole('USER')")
    @GetMapping
    public List<BookDto> getAll(Pageable pageable) {
        return bookService.findAll(pageable);
    }

    @Operation(summary = "Get a book by id", description = "Get a book by id")
    @PreAuthorize("hasAuthority('USER')")
    @GetMapping("/{id}")
    public BookDto getBookById(@PathVariable Long id) {

        return bookService.getBookById(id);
    }

    @Operation(summary = "Create a book", description = "Create a new book")
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public BookDto createBook(@RequestBody @Valid CreateBookRequestDto bookDto) {

        return bookService.save(bookDto);
    }

    @Operation(summary = "Delete a book by id", description = "Delete a book by id")
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {

        bookService.deleteById(id);
    }

    @Operation(summary = "Update a book by id", description = "Update a book by id")
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public BookDto updateBook(@PathVariable Long id,
                @RequestBody @Valid CreateBookRequestDto bookRequestDto) {
        return bookService.updateBookById(id, bookRequestDto);
    }

    @Operation(summary = "Search books by parameters", description = "Search book by parameters")
    @PreAuthorize("hasRole('USER')")
    @GetMapping("/search")
    public List<BookDto> search(@Valid BookSearchParametersDto searchParameters,
                                Pageable pageable) {
        return bookService.search(searchParameters, pageable);
    }
}

