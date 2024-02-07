package mate.academy.bookstore.service.impl;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
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
import mate.academy.bookstore.service.ShoppingCartService;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class ShoppingCartServiceImpl implements ShoppingCartService {
    private final ShoppingCartRepository shoppingCartRepository;
    private final CartItemRepository cartItemRepository;
    private final ShoppingCartMapper shoppingCartMapper;
    private final CartItemMapper cartItemMapper;
    private final BookRepository bookRepository;

    @Override
    public ShoppingCartDto findShoppingCart(Authentication authentication, Pageable pageable) {
        User user = (User) authentication.getPrincipal();

        ShoppingCart shoppingCart = shoppingCartRepository.findByUserId(user.getId())
                .orElseThrow(() -> new IllegalStateException("Cart cannot be found for User "
                        + "with email - " + user.getEmail()));

        ShoppingCartDto shoppingCartDto = shoppingCartMapper.toDto(shoppingCart);
        Set<CartItemResponseDto> cartItemsSet = cartItemRepository
                .findByShoppingCartId(shoppingCart.getId(), pageable)
                .stream()
                .map(cartItemMapper::toDto)
                .collect(Collectors.toCollection(LinkedHashSet::new));
        shoppingCartDto.setCartItems(cartItemsSet);

        return shoppingCartDto;
    }

    @Override
    public CartItemResponseDto addBookToShoppingCart(Authentication authentication,
                                                     CartItemRequestDto requestDto) {
        User user = (User) authentication.getPrincipal();
        ShoppingCart shoppingCart = shoppingCartRepository.findByUserId(user.getId())
                .orElseThrow(() -> new IllegalStateException(
                        "Cart cannot be found for User "
                                + "with email - " + user.getEmail()));

        Book book = bookRepository.findById(requestDto.getBookId())
                .orElseThrow(() -> new EntityNotFoundException("The book with the current ID: "
                        + requestDto.getBookId() + " does not exist."));

        CartItem cartItem = cartItemMapper.toModel(requestDto);
        cartItem.setShoppingCart(shoppingCart);
        cartItem.setBook(book);
        CartItem savedCartItem = cartItemRepository.save(cartItem);
        Set<CartItem> cartItems = cartItemRepository.findByShoppingCartId(shoppingCart.getId());
        cartItems.add(savedCartItem);
        shoppingCart.setCartItems(cartItems);
        shoppingCartRepository.save(shoppingCart);
        return cartItemMapper.toDto(cartItem);
    }

    @Override
    public CartItemResponseDto updateBookQuantityInTheShoppingCart(Authentication authentication,
                               Long cartItemId,
                               CartItemUpdateDataRequestDto requestDto) {
        User user = (User) authentication.getPrincipal();
        CartItem cartItem = getValidCartItem(cartItemId);
        ShoppingCart shoppingCart = getValidShoppingCart(user.getId());
        if (!cartItemRepository.findByShoppingCartId(shoppingCart.getId()).contains(cartItem)) {
            throw new EntityNotFoundException("Can't find CartItem by id: " + cartItemId
                    + " in the ShoppingCart to update");
        }
        cartItem.setQuantity(requestDto.getQuantity());
        cartItemRepository.save(cartItem);
        return cartItemMapper.toDto(cartItem);
    }

    private CartItem getValidCartItem(Long cartItemId) {
        return cartItemRepository.findById(cartItemId).orElseThrow(
                () -> new EntityNotFoundException("Can't find CartItem by id: " + cartItemId));
    }

    @Override
    public void deleteBookFromTheShoppingCart(Authentication authentication, Long cartItemId) {
        User user = (User) authentication.getPrincipal();
        CartItem cartItem = getValidCartItem(cartItemId);
        ShoppingCart shoppingCart = getValidShoppingCart(user.getId());
        if (!cartItemRepository.findByShoppingCartId(shoppingCart.getId()).contains(cartItem)) {
            throw new EntityNotFoundException("Unable to locate CartItem with ID: " + cartItemId
                    + "  in the ShoppingCart for deletion.");
        }
        cartItemRepository.delete(cartItem);
    }

    private ShoppingCart getValidShoppingCart(Long userId) {
        return shoppingCartRepository.findByUserId(userId).orElseThrow(
                () -> new EntityNotFoundException("Can't find ShoppingCart by userId: "
                        + userId));
    }
}
