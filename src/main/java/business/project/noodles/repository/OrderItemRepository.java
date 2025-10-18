package business.project.noodles.repository;

import business.project.noodles.entity.KeyOrderItem;
import business.project.noodles.entity.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, KeyOrderItem> {
}
