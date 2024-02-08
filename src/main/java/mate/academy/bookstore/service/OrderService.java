package mate.academy.bookstore.service;

import java.util.List;
import mate.academy.bookstore.dto.order.OrderItemResponseDto;
import mate.academy.bookstore.dto.order.OrderPresentationRequestDto;
import mate.academy.bookstore.dto.order.OrderResponseDto;
import mate.academy.bookstore.dto.order.OrderStatusRequestDto;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;

public interface OrderService {
    OrderResponseDto createOrder(Authentication authentication, Pageable pageable,
            OrderPresentationRequestDto requestDto);

    List<OrderResponseDto> getOrders(Authentication authentication, Pageable pageable);

    OrderResponseDto updateOrderStatus(Long orderId, OrderStatusRequestDto requestDto);

    List<OrderItemResponseDto> getOrderItemsByOrderId(Authentication authentication,
            Long orderId, Pageable pageable);

    OrderItemResponseDto getOrderItemByIdAndOrderId(Authentication authentication,
            Long orderId, Long orderItemId);
}
