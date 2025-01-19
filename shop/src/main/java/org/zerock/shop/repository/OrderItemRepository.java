package org.zerock.shop.repository;

import org.springframework.data.jpa.repository.Query;
import org.zerock.shop.entity.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {

    @Query("select o from OrderItem o where o.order.id = :orderId and o.item.id = :itemId")
    OrderItem findByOrderIdAndItemId(Long orderId, Long itemId);
}