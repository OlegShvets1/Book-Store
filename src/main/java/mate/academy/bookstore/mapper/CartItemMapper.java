package mate.academy.bookstore.mapper;

import mate.academy.bookstore.config.MapperConfig;
import mate.academy.bookstore.dto.shoppingcart.cartitem.CartItemRequestDto;
import mate.academy.bookstore.dto.shoppingcart.cartitem.CartItemResponseDto;
import mate.academy.bookstore.model.CartItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(config = MapperConfig.class)
public interface CartItemMapper {
    CartItem toModel(CartItemRequestDto requestDto);

    @Mapping(source = "cartItem.book.id", target = "bookId")
    @Mapping(source = "cartItem.book.title", target = "bookTitle")
    CartItemResponseDto toDto(CartItem cartItem);
}
