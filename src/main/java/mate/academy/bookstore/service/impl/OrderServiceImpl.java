package mate.academy.bookstore.service.impl;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import mate.academy.bookstore.dto.order.OrderItemResponseDto;
import mate.academy.bookstore.dto.order.OrderPresentationRequestDto;
import mate.academy.bookstore.dto.order.OrderResponseDto;
import mate.academy.bookstore.dto.order.OrderStatusRequestDto;
import mate.academy.bookstore.exception.EntityNotFoundException;
import mate.academy.bookstore.exception.OrderException;
import mate.academy.bookstore.mapper.OrderItemMapper;
import mate.academy.bookstore.mapper.OrderMapper;
import mate.academy.bookstore.model.CartItem;
import mate.academy.bookstore.model.Order;
import mate.academy.bookstore.model.OrderItem;
import mate.academy.bookstore.model.ShoppingCart;
import mate.academy.bookstore.model.User;
import mate.academy.bookstore.repository.order.OrderItemRepository;
import mate.academy.bookstore.repository.order.OrderRepository;
import mate.academy.bookstore.repository.shoppingcart.ShoppingCartRepository;
import mate.academy.bookstore.repository.user.UserRepository;
import mate.academy.bookstore.service.OrderService;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class OrderServiceImpl implements OrderService {
    private final ShoppingCartRepository shoppingCartRepository;
    private final OrderItemRepository orderItemRepository;
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final OrderMapper orderMapper;
    private final OrderItemMapper orderItemMapper;

    @Override
    @Transactional
    public OrderResponseDto createOrder(Long userId, Pageable pageable,
              OrderPresentationRequestDto requestDto) {
        User user = getUser(userId);

        ShoppingCart shoppingCart = shoppingCartRepository.findByUserId(userId).orElseThrow(
                () -> new EntityNotFoundException("Can't find ShoppingCart by userId: " + userId));

        Set<CartItem> cartItems = shoppingCart.getCartItems();
        if (cartItems.isEmpty()) {
            return new OrderResponseDto();
        }

        Order order = placeAnOrder(shoppingCart, requestDto.getShippingAddress());
        order.setOrderItems(createSetOfOrderItems(order, cartItems));
        OrderResponseDto orderResponseDto = orderMapper.toDto(order);

        cleanShoppingCart(shoppingCart);
        return orderResponseDto;
    }

    @Override
    public List<OrderResponseDto> getOrders(Long userId, Pageable pageable) {
        return orderRepository.findOrdersByUserId(userId, pageable).stream()
                .map(orderMapper::toDto)
                .toList();
    }

    @Override
    @Transactional
    public OrderResponseDto updateOrderStatus(Long userId, Long orderId,
                OrderStatusRequestDto requestDto) {
        Order order = orderRepository.findByIdAndUserId(userId, orderId)
                .orElseThrow(
                    () -> new EntityNotFoundException("Can't find order by id: " + orderId));
        order.setStatus(requestDto.getStatus());
        Order savedOrder = orderRepository.save(order);
        return orderMapper.toDto(savedOrder);
    }

    @Override
    public List<OrderItemResponseDto> getOrderItemsByOrderId(Long userId, Long orderId,
             Pageable pageable) {
        Order order = getOrderForUser(userId, orderId);
        List<OrderItem> orderItems = orderItemRepository.findOrderItemsByOrderId(orderId, pageable);
        return orderItems.stream()
                .map(orderItemMapper::toDto)
                .toList();
    }

    @Override
    public OrderItemResponseDto getOrderItemByIdAndOrderId(Long userId,
             Long orderId, Long orderItemId) {
        Order order = getOrderForUser(userId, orderId);
        OrderItem orderItem = orderItemRepository.findById(orderItemId).orElseThrow(
                () -> new EntityNotFoundException("Can't find OrderItem by id: " + orderId));
        if (!orderItem.getOrder().getId().equals(order.getId())) {
            throw new OrderException("We don't possess an OrderItem that matches the provided ID "
                    + orderItemId + " in your Order");
        }
        return orderItemMapper.toDto(orderItem);
    }

    private BigDecimal countTotalOrderPrice(Set<CartItem> cartItems) {
        return cartItems.stream()
                .map(c -> c.getBook().getPrice().multiply(BigDecimal.valueOf(c.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private Set<OrderItem> createSetOfOrderItems(Order order, Set<CartItem> cartItems) {
        Set<OrderItem> orderItems = cartItems.stream()
                .map(c -> createOrderItem(order, c))
                .collect(Collectors.toSet());
        orderItemRepository.saveAll(orderItems);
        return orderItems;
    }

    private OrderItem createOrderItem(Order order, CartItem cartItem) {
        OrderItem orderItem = orderItemMapper.toModelFromCartItem(cartItem);
        orderItem.setOrder(order);
        return orderItem;
    }

    private Order getOrderForUser(Long userId, Long orderId) {
        Order order = orderRepository.findByIdAndUserId(userId, orderId).orElseThrow(
                () -> new EntityNotFoundException("Can't find order by id: " + orderId));
        if (!order.getUser().getId().equals(userId)) {
            throw new OrderException("You don't have access to Order with id: " + orderId);
        }
        return order;
    }

    private ShoppingCart cleanShoppingCart(ShoppingCart shoppingCart) {
        shoppingCart.getCartItems().clear();
        return shoppingCartRepository.save(shoppingCart);
    }

    private Order placeAnOrder(ShoppingCart shoppingCart, String shippingAddress) {
        Order order = new Order();
        order.setUser(shoppingCart.getUser());
        order.setStatus(Order.Status.PENDING);
        order.setOrderDate(LocalDateTime.now());
        order.setShippingAddress(shippingAddress);
        order.setTotal(countTotalOrderPrice(shoppingCart.getCartItems()));
        return orderRepository.save(order);
    }

    private User getUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Can`t find user by id" + userId
                ));
    }
}
