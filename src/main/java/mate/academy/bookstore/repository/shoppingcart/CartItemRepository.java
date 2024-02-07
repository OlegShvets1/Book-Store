package mate.academy.bookstore.repository.shoppingcart;

import java.util.List;
import java.util.Set;
import mate.academy.bookstore.model.Book;
import mate.academy.bookstore.model.CartItem;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface CartItemRepository extends JpaRepository<CartItem, Long>,
        JpaSpecificationExecutor<Book> {
    Set<CartItem> findByShoppingCartId(Long shoppingCartId);

    List<CartItem> findByShoppingCartId(Long shoppingCartId, Pageable pageable);
}
