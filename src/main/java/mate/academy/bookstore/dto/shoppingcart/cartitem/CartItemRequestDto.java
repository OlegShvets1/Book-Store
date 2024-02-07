package mate.academy.bookstore.dto.shoppingcart.cartitem;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CartItemRequestDto {
    @NotNull
    @Min(0)
    private Long bookId;
    @NotNull
    @Min(0)
    private Integer quantity;
}
