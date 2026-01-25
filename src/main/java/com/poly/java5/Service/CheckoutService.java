package com.poly.java5.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.poly.java5.Entity.Book;
import com.poly.java5.Entity.Cart;
import com.poly.java5.Entity.Order;
import com.poly.java5.Entity.OrderDetail;
import com.poly.java5.Entity.CartDetail;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class CheckoutService {
    
@PersistenceContext
    private EntityManager em;

// ✅ THÊM HÀM Ở ĐÂY
private String generateOrderCode() {
    return "ORD" + System.currentTimeMillis();
}

    public Order checkout(Integer userId,
                          String customerName,
                          String phone,
                          String address,
                          String paymentMethod) {

        // 1. Lấy cart ACTIVE
        List<Cart> carts = em.createQuery(
                "SELECT c FROM Cart c WHERE c.user.id = :uid AND c.status = 'ACTIVE'",
                Cart.class)
            .setParameter("uid", userId)
            .getResultList();

        if (carts.isEmpty()) {
            throw new RuntimeException("Không tìm thấy giỏ hàng");
        }

        Cart cart = carts.get(0);

        // 2. Lấy cart detail
        List<CartDetail> cartDetails = em.createQuery(
                "SELECT cd FROM CartDetail cd WHERE cd.cart.id = :cid",
                CartDetail.class)
            .setParameter("cid", cart.getId())
            .getResultList();

        if (cartDetails.isEmpty()) {
            throw new RuntimeException("Giỏ hàng trống");
        }

        // 3. Tạo order (⚠️ KHÔNG ĐƯỢC THIẾU orderCode)
        Order order = Order.builder()
        	.orderCode(generateOrderCode()) // ✅ GỌI Ở ĐÂY
            .user(cart.getUser())
            .orderCode("ORD-" + System.currentTimeMillis()) // 🔥 BẮT BUỘC
            .customerName(customerName)
            .customerPhone(phone)
            .customerAddress(address)
            .paymentMethod(paymentMethod)
            .status("CONFIRMED")          // ✅ đúng CHECK constraint
            .paymentStatus("PENDING")    // ✅ đúng CHECK constraint
            .totalAmount(BigDecimal.ZERO)
            .build();

        // 4. Persist order TRƯỚC
        em.persist(order);

        BigDecimal total = BigDecimal.ZERO;

        // 5. Xử lý từng sản phẩm
        for (CartDetail cd : cartDetails) {

            Book book = em.find(
                Book.class,
                cd.getBook().getId(),
                jakarta.persistence.LockModeType.PESSIMISTIC_WRITE
            );

            if (book.getQuantity() < cd.getQuantity()) {
                throw new RuntimeException("Không đủ hàng: " + book.getTitle());
            }

            book.setQuantity(book.getQuantity() - cd.getQuantity());

            OrderDetail od = OrderDetail.builder()
                .order(order)
                .book(book)
                .quantity(cd.getQuantity())
                .price(cd.getPrice())
                .build();

            em.persist(od); // persist từng detail

            total = total.add(od.calculateSubtotal());
        }

        // 6. Update tổng tiền
        order.setTotalAmount(total);
        em.merge(order);

        // 7. Clear cart
        cartDetails.forEach(em::remove);
        cart.setStatus("CHECKOUT");
        cart.setUpdatedDate(LocalDateTime.now());
        em.merge(cart);

        return order;
    }

}
