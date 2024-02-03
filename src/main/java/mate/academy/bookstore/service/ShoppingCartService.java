package mate.academy.bookstore.service;

import mate.academy.bookstore.dto.shoppingcart.ShoppingCartDto;
import mate.academy.bookstore.dto.shoppingcart.cartitem.CartItemRequestDto;
import mate.academy.bookstore.dto.shoppingcart.cartitem.CartItemResponseDto;
import mate.academy.bookstore.dto.shoppingcart.cartitem.CartItemUpdateDataRequestDto;
import mate.academy.bookstore.model.ShoppingCart;
import mate.academy.bookstore.model.User;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;

public interface ShoppingCartService {
    ShoppingCartDto findShoppingCart(Authentication authentication, Pageable pageable);

    CartItemResponseDto addBookToShoppingCart(Authentication authentication,
                                              CartItemRequestDto requestDto);

    CartItemResponseDto updateBookQuantityInTheShoppingCart(Authentication authentication,
                               Long cartItemId,
                               CartItemUpdateDataRequestDto requestDto);

    void deleteBookFromTheShoppingCart(Authentication authentication, Long cartItemId);

    ShoppingCart createNewShoppingCart(User user);
}
