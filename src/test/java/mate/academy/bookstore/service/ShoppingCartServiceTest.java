package mate.academy.bookstore.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import mate.academy.bookstore.dto.shoppingcart.ShoppingCartDto;
import mate.academy.bookstore.dto.shoppingcart.cartitem.CartItemRequestDto;
import mate.academy.bookstore.dto.shoppingcart.cartitem.CartItemResponseDto;
import mate.academy.bookstore.dto.shoppingcart.cartitem.CartItemUpdateDataRequestDto;
import mate.academy.bookstore.exception.EntityNotFoundException;
import mate.academy.bookstore.mapper.CartItemMapper;
import mate.academy.bookstore.mapper.ShoppingCartMapper;
import mate.academy.bookstore.model.Book;
import mate.academy.bookstore.model.CartItem;
import mate.academy.bookstore.model.ShoppingCart;
import mate.academy.bookstore.model.User;
import mate.academy.bookstore.repository.book.BookRepository;
import mate.academy.bookstore.repository.shoppingcart.CartItemRepository;
import mate.academy.bookstore.repository.shoppingcart.ShoppingCartRepository;
import mate.academy.bookstore.service.impl.ShoppingCartServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;

@ExtendWith(MockitoExtension.class)
public class ShoppingCartServiceTest {
    @Mock
    private Authentication authentication;
    @Mock
    private ShoppingCartRepository shoppingCartRepository;
    @Mock
    private CartItemRepository cartItemRepository;
    @Mock
    private BookRepository bookRepository;
    @Mock
    private ShoppingCartMapper shoppingCartMapper;
    @Mock
    private CartItemMapper cartItemMapper;
    @InjectMocks
    private ShoppingCartServiceImpl shoppingCartService;

    @Test
    void findShoppingCart_validUser_ok() {
        User user = new User();
        user.setId(1L);
        CartItem cartItem1 = new CartItem();
        CartItem cartItem2 = new CartItem();
        CartItem cartItem3 = new CartItem();
        ShoppingCart expected = new ShoppingCart();
        expected.getCartItems().add(cartItem1);
        expected.getCartItems().add(cartItem2);
        expected.getCartItems().add(cartItem3);
        Pageable pageable = Pageable.unpaged();

        when(authentication.getPrincipal()).thenReturn(user);
        when(shoppingCartRepository.findByUserId(user.getId())).thenReturn(Optional.of(expected));
        when(shoppingCartMapper.toDto(expected)).thenReturn(new ShoppingCartDto());
        when(cartItemRepository.findByShoppingCartId(expected.getId(), pageable))
                .thenReturn(List.of(cartItem1, cartItem2, cartItem3));
        when(cartItemMapper.toDto(any())).thenReturn(new CartItemResponseDto());

        ShoppingCartDto actual = shoppingCartService.findShoppingCart(authentication, pageable);

        assertNotNull(actual);
        assertEquals(expected.getCartItems().size(), actual.getCartItems().size());
    }

    @Test
    void findShoppingCart_invalidUser_notOk() {
        User user = new User();
        user.setId(1L);
        Pageable pageable = Pageable.unpaged();

        when(authentication.getPrincipal()).thenReturn(user);
        when(shoppingCartRepository.findByUserId(user.getId())).thenReturn(Optional.empty());
        assertThrows(IllegalStateException.class, () -> shoppingCartService
                .findShoppingCart(authentication, pageable));
    }

    @Test
    void addBookToShoppingCart_validBook_ok() {
        User user = new User();
        user.setId(1L);

        CartItemResponseDto expected = new CartItemResponseDto();
        expected.setBookId(1L);
        expected.setQuantity(3);
        ShoppingCart shoppingCart = new ShoppingCart();
        CartItemRequestDto requestDto = new CartItemRequestDto();
        CartItem cartItem1 = new CartItem();

        when(authentication.getPrincipal()).thenReturn(user);
        when(shoppingCartRepository.findByUserId(user.getId()))
                .thenReturn(Optional.of(shoppingCart));
        when(bookRepository.findById(requestDto.getBookId())).thenReturn(Optional.of(new Book()));
        when(cartItemMapper.toModel(requestDto)).thenReturn(cartItem1);
        when(cartItemMapper.toDto(cartItem1)).thenReturn(expected);

        CartItemResponseDto actual = shoppingCartService
                .addBookToShoppingCart(authentication, requestDto);

        assertNotNull(actual);
        assertEquals(expected.getQuantity(), actual.getQuantity());
        assertEquals(expected.getBookId(), actual.getBookId());
    }

    @Test
    void addBookToShoppingCart_invalidBookId_notOk() {
        User user = new User();
        user.setId(1L);
        CartItemRequestDto requestDto = new CartItemRequestDto();
        ShoppingCart shoppingCart = new ShoppingCart();
        requestDto.setBookId(99L);

        when(authentication.getPrincipal()).thenReturn(user);
        when(shoppingCartRepository.findByUserId(user.getId()))
                .thenReturn(Optional.of(shoppingCart));
        when(bookRepository.findById(requestDto.getBookId())).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> shoppingCartService
                .addBookToShoppingCart(authentication, requestDto));
    }

    @Test
    void updateBookQuantity_validRequest_ok() {
        Long cartItemId = 1L;
        Long userId = 1L;
        User user = new User();
        user.setId(userId);

        CartItem cartItem1 = new CartItem();
        cartItem1.setQuantity(5);
        cartItem1.setId(cartItemId);
        CartItem cartItem2 = new CartItem();
        cartItem2.setId(cartItemId + 1);

        CartItemUpdateDataRequestDto requestDto = new CartItemUpdateDataRequestDto();
        requestDto.setQuantity(10);

        CartItemResponseDto expected = new CartItemResponseDto();
        expected.setId(cartItemId);
        expected.setQuantity(10);

        ShoppingCart shoppingCart = new ShoppingCart();

        when(authentication.getPrincipal()).thenReturn(user);
        when(cartItemRepository.findById(cartItemId)).thenReturn(Optional.of(cartItem1));
        when(shoppingCartRepository.findByUserId(userId)).thenReturn(Optional.of(shoppingCart));
        when(cartItemRepository.findByShoppingCartId(shoppingCart.getId()))
                .thenReturn(Set.of(cartItem1, cartItem2));
        when(cartItemMapper.toDto(cartItem1)).thenReturn(expected);

        CartItemResponseDto actual = shoppingCartService
                .updateBookQuantityInTheShoppingCart(authentication, cartItemId, requestDto);

        assertNotNull(actual);
        assertEquals(expected.getId(), actual.getId());
        assertEquals(expected.getQuantity(), actual.getQuantity());
    }

    @Test
    void deleteBookFromShoppingCart_validCartItemId_ok() {
        Long userId = 1L;
        User user = new User();
        user.setId(userId);
        CartItem cartItem1 = new CartItem();
        cartItem1.setId(1L);
        CartItem cartItem2 = new CartItem();
        cartItem2.setId(2L);
        ShoppingCart shoppingCart = new ShoppingCart();
        Long cartItemId = 2L;

        when(authentication.getPrincipal()).thenReturn(user);
        when(cartItemRepository.findById(cartItemId)).thenReturn(Optional.of(cartItem2));
        when(shoppingCartRepository.findByUserId(userId)).thenReturn(Optional.of(shoppingCart));
        when(cartItemRepository.findByShoppingCartId(shoppingCart.getId()))
                .thenReturn(Set.of(cartItem1, cartItem2));

        shoppingCartService.deleteBookFromTheShoppingCart(authentication, cartItemId);

        verify(shoppingCartRepository).findByUserId(userId);
        verify(cartItemRepository).findById(cartItemId);
        verify(cartItemRepository).delete(cartItem2);
    }
}
