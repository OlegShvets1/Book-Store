package mate.academy.bookstore.dto.order;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;
import lombok.Data;
import mate.academy.bookstore.model.Order;

@Data
public class OrderResponseDto {
    private Long id;
    private Long userId;
    private LocalDateTime orderDate;
    private Set<OrderItemResponseDto> orderItems;
    private Order.Status status;
    private BigDecimal total;
}
