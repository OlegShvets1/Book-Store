package mate.academy.bookstore.service;

import java.util.List;
import mate.academy.bookstore.dto.order.OrderItemResponseDto;
import mate.academy.bookstore.dto.order.OrderPresentationRequestDto;
import mate.academy.bookstore.dto.order.OrderResponseDto;
import mate.academy.bookstore.dto.order.OrderStatusRequestDto;
import org.springframework.data.domain.Pageable;

public interface OrderService {
    OrderResponseDto createOrder(Long userId, Pageable pageable,
            OrderPresentationRequestDto requestDto);

    List<OrderResponseDto> getOrders(Long userId, Pageable pageable);

    OrderResponseDto updateOrderStatus(Long userId, Long orderId, OrderStatusRequestDto requestDto);

    List<OrderItemResponseDto> getOrderItemsByOrderId(Long userId,
            Long orderId, Pageable pageable);

    OrderItemResponseDto getOrderItemByIdAndOrderId(Long userId,
            Long orderId, Long orderItemId);
}
