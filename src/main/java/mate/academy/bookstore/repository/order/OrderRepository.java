package mate.academy.bookstore.repository.order;

import java.util.List;
import java.util.Optional;
import mate.academy.bookstore.model.Order;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Long> {
    Optional<Order> findOrderById(Long orderId);

    List<Order> findOrdersByUserId(Long userId, Pageable pageable);
}
