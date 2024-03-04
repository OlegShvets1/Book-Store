package mate.academy.bookstore.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import java.util.Set;
import mate.academy.bookstore.model.CartItem;
import mate.academy.bookstore.repository.shoppingcart.CartItemRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.jdbc.Sql;

@Sql(scripts = "classpath:database/books/add-user-to-db.sql",
        executionPhase = Sql.ExecutionPhase.BEFORE_TEST_CLASS)
@Sql(scripts = "classpath:database/books/add-books-to-shoppingCart-to-db.sql",
        executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts = "classpath:database/books/delete-books-and-shoppingCart-from-db.sql",
        executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
@Sql(scripts = "classpath:database/books/delete-user-from-db.sql",
        executionPhase = Sql.ExecutionPhase.AFTER_TEST_CLASS)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@DataJpaTest
public class CartItemRepositoryTest {
    private static final Long VALID_USER_ID = 1L;
    private static final Long INVALID_USER_ID = -99L;
    private static final int CART_ITEMS_AMOUNT = 2;
    @Autowired
    private CartItemRepository cartItemRepository;

    @Test
    void findByShoppingCartId_validId_ok() {
        Set<CartItem> cartItems = cartItemRepository.findByShoppingCartId(VALID_USER_ID);
        assertEquals(CART_ITEMS_AMOUNT, cartItems.size());
    }

    @Test
    void findByShoppingCartId_invalidId_notOk() {
        Set<CartItem> cartItems = cartItemRepository.findByShoppingCartId(INVALID_USER_ID);
        assertEquals(0, cartItems.size());
    }

    @Test
    void findByShoppingCartId_validIdAndPage_ok() {
        Pageable pageable = PageRequest.of(0, CART_ITEMS_AMOUNT - 1);
        List<CartItem> cartItems = cartItemRepository.findByShoppingCartId(VALID_USER_ID, pageable);
        assertEquals(CART_ITEMS_AMOUNT - 1, cartItems.size());
    }
}
