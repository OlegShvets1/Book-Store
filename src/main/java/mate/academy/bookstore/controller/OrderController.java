package mate.academy.bookstore.controller;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import mate.academy.bookstore.dto.order.OrderItemResponseDto;
import mate.academy.bookstore.dto.order.OrderPresentationRequestDto;
import mate.academy.bookstore.dto.order.OrderResponseDto;
import mate.academy.bookstore.dto.order.OrderStatusRequestDto;
import mate.academy.bookstore.model.User;
import mate.academy.bookstore.service.OrderService;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/api/orders")
@RequiredArgsConstructor
@RestController
public class OrderController {
    private final OrderService orderService;

    @Operation(summary = "Submit order", description = "submit current order")
    @PostMapping
    public OrderResponseDto submitOrder(@RequestBody @Valid OrderPresentationRequestDto requestDto,
                         Pageable pageable, @AuthenticationPrincipal User user) {
        return orderService.createOrder(user.getId(), pageable, requestDto);
    }

    @Operation(summary = "Get orders", description = "get user orders")
    @GetMapping
    public List<OrderResponseDto> getOrders(Long userId, Pageable pageable) {
        return orderService.getOrders(userId, pageable);
    }

    @Operation(summary = "Update order status", description = "update order status bu orderId")
    @PatchMapping("/{orderId}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public OrderResponseDto updateOrderStatus(@PathVariable Long userId, Long orderId,
                       @RequestBody @Valid OrderStatusRequestDto requestDto) {
        return orderService.updateOrderStatus(userId, orderId, requestDto);
    }

    @Operation(summary = "Get order items", description = "get order items by orderId")
    @GetMapping("/{orderId}/items")
    public List<OrderItemResponseDto> getOrderItemsByOrderId(Long userId,
                       @PathVariable Long orderId, Pageable pageable) {
        return orderService.getOrderItemsByOrderId(userId, orderId, pageable);
    }

    @Operation(summary = "Get OrderItem", description = "get OrderItems by orderId and ItemId")
    @GetMapping("/{orderId}/items/{orderItemId}")
    public OrderItemResponseDto getOrderItemByIdAndOrderId(Long userId,
                      @PathVariable Long orderId, @PathVariable Long orderItemId) {
        return orderService.getOrderItemByIdAndOrderId(userId, orderId, orderItemId);
    }
}
