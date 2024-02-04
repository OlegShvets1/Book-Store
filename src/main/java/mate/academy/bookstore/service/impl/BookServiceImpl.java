package mate.academy.bookstore.service.impl;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
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
import mate.academy.bookstore.service.BookService;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class BookServiceImpl implements BookService {
    private final BookMapper bookMapper;
    private final BookRepository bookRepository;
    private final CategoryRepository categoryRepository;
    private final BookSpecificationBuilder bookSpecificationBuilder;

    @Override
    public BookDto save(CreateBookRequestDto bookRequestDto) {
        Book bookModel = bookMapper.toModel(bookRequestDto);
        Set<Category> categories = setCategoriesById(bookRequestDto.getCategoryIds());
        bookModel.setCategories(categories);
        return bookMapper.toDto(bookRepository.save(bookModel));
    }

    public BookDto getBookById(Long id) {
        Book book = bookRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Can't find book by id: " + id));
        return bookMapper.toDto(book);
    }

    @Override
    public List<BookDto> findAll(Pageable pageable) {
        return bookRepository.findAll(pageable).stream()
                .map(bookMapper::toDto)
                .toList();
    }

    @Override
    public void deleteById(Long id) {
        bookRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Can't find book by id: " + id));
        bookRepository.deleteById(id);
    }

    @Override
    public BookDto updateBookById(Long id, CreateBookRequestDto bookRequestDto) {
        bookRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Can't find book by id: " + id));
        Book newBook = bookMapper.toModel(bookRequestDto);
        newBook.setId(id);
        return bookMapper.toDto(bookRepository.save(newBook));
    }

    @Override
    public List<BookDto> search(BookSearchParametersDto searchParameters, Pageable pageable) {
        Specification<Book> bookSpecification = bookSpecificationBuilder.build(searchParameters);
        return bookRepository.findAll(bookSpecification, pageable)
                .stream()
                .map(bookMapper::toDto)
                .toList();
    }

    @Override
    public List<BookDto> findAllByCategoryId(Long categoryId, Pageable pageable) {
        return bookRepository.findBooksByCategoryId(categoryId, pageable)
                .stream()
                .map(bookMapper::toDto)
                .toList();
    }

    private Set<Category> setCategoriesById(Set<Long> categoryIds) {
        if (categoryIds == null) {
            throw new IllegalArgumentException("categoryIds cannot be null");
        }

        return categoryIds.stream()
                .map(id -> {
                    if (id == null) {
                        throw new IllegalArgumentException("Category id cannot be null");
                    }
                    return categoryRepository.findById(id).orElseThrow(
                            () -> new EntityNotFoundException("Can't find category by id: " + id));
                })
                .collect(Collectors.toSet());
    }
}
