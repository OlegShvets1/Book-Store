package mate.academy.bookstore.repository.order;

import java.util.List;
import mate.academy.bookstore.model.OrderItem;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
    List<OrderItem> findOrderItemsByOrderId(Long orderId, Pageable pageable);
}
