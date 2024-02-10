package mate.academy.bookstore.repository.order;

import java.util.List;
import java.util.Optional;
import mate.academy.bookstore.model.Order;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

public interface OrderRepository extends JpaRepository<Order, Long> {
    Optional<Order> findByIdAndUserId(@Param("userId") Long userId,
                                      @Param("orderId") Long orderId);

    List<Order> findOrdersByUserId(@Param("userId")Long userId, Pageable pageable);
}
