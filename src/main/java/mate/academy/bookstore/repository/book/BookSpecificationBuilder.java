package mate.academy.bookstore.repository.book;

import lombok.RequiredArgsConstructor;
import mate.academy.bookstore.dto.BookSearchParametersDto;
import mate.academy.bookstore.model.Book;
import mate.academy.bookstore.repository.SpecificationBuilder;
import mate.academy.bookstore.repository.SpecificationProviderManager;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class BookSpecificationBuilder implements SpecificationBuilder<Book> {
    private static final String AUTHOR = "author";
    private static final String TITLE = "title";
    private final SpecificationProviderManager<Book> specificationProviderManager;

    @Override
    public Specification<Book> build(BookSearchParametersDto searchParameters) {
        Specification<Book> spec = Specification.where(null);

        if (searchParameters != null) {
            if (searchParameters.authors() != null && searchParameters.authors().length > 0) {
                spec = spec.and(specificationProviderManager
                        .getSpecificationProvider(AUTHOR)
                        .getSpecification(searchParameters.authors()));
            }
            if (searchParameters.titles() != null && searchParameters.titles().length > 0) {
                spec = spec.and(specificationProviderManager
                        .getSpecificationProvider(TITLE)
                        .getSpecification(searchParameters.titles()));
            }
        }

        return spec;
    }
}
