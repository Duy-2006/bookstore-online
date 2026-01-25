package com.poly.java5.Service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.annotation.Propagation;

import com.poly.java5.Entity.ActivityLog;
import com.poly.java5.Entity.Book;
import com.poly.java5.Entity.Cart;
import com.poly.java5.Entity.CartDetail;
import com.poly.java5.Entity.User;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class CartService {
	@PersistenceContext
	private EntityManager entityManager;

	private final BookService bookService;

	// ============ CART OPERATIONS ============

	// Lấy hoặc tạo giỏ hàng cho user
	public Cart getOrCreateCart(Integer userId) {
		TypedQuery<Cart> query = entityManager
				.createQuery("SELECT c FROM Cart c WHERE c.user.id = :userId AND c.status = :status", Cart.class);
		query.setParameter("userId", userId);
		query.setParameter("status", "ACTIVE");

		List<Cart> carts = query.getResultList();
		if (!carts.isEmpty()) {
			return carts.get(0);
		}

		User user = entityManager.find(User.class, userId);
		if (user == null) {
			throw new RuntimeException("User không tồn tại");
		}

		Cart cart = Cart.builder().user(user).status("ACTIVE").createdDate(LocalDateTime.now())
				.updatedDate(LocalDateTime.now()).build();

		entityManager.persist(cart);
		return cart;
	}

	// Thêm sản phẩm vào giỏ hàng
	public Map<String, Object> addToCart(Integer userId, Integer bookId, Integer quantity) {

		if (quantity <= 0) {
			throw new RuntimeException("Số lượng không hợp lệ");
		}

		Book book = bookService.getBookById(bookId);
		if (book == null) {
			throw new RuntimeException("Sách không tồn tại");
		}

		if (book.getQuantity() < quantity) {
			throw new RuntimeException("Không đủ hàng trong kho");
		}

		Cart cart = getOrCreateCart(userId);

		TypedQuery<CartDetail> query = entityManager.createQuery(
				"SELECT cd FROM CartDetail cd WHERE cd.cart.id = :cartId AND cd.book.id = :bookId", CartDetail.class);
		query.setParameter("cartId", cart.getId());
		query.setParameter("bookId", bookId);

		List<CartDetail> list = query.getResultList();

		if (!list.isEmpty()) {
			CartDetail cd = list.get(0);
			cd.setQuantity(cd.getQuantity() + quantity);
			entityManager.merge(cd);
		} else {
			CartDetail cd = CartDetail.builder().cart(cart).book(book).quantity(quantity).price(book.getPrice())
					.build();
			entityManager.persist(cd);
		}

		cart.setUpdatedDate(LocalDateTime.now());
		entityManager.merge(cart);

		logActivity(userId, "ADD_TO_CART", "Thêm sách: " + book.getTitle());

		return getCartSummary(userId);
	}

	// Lấy chi tiết giỏ hàng
	public Map<String, Object> getCartSummary(Integer userId) {
		log.info("Lấy thông tin giỏ hàng cho user ID: {}", userId);

		try {
			Cart cart = getOrCreateCart(userId);

			// Lấy tất cả sản phẩm trong giỏ
			TypedQuery<CartDetail> query = entityManager
					.createQuery("SELECT cd FROM CartDetail cd WHERE cd.cart.id = :cartId", CartDetail.class);
			query.setParameter("cartId", cart.getId());

			List<CartDetail> cartDetails = query.getResultList();

			// Chuyển đổi sang Map để dễ xử lý trong template
			List<Map<String, Object>> cartItems = new ArrayList<>();
			BigDecimal totalAmount = BigDecimal.ZERO;
			int totalItems = 0;

			for (CartDetail detail : cartDetails) {
				Map<String, Object> item = new HashMap<>();
				item.put("cartDetailId", detail.getId());
				item.put("quantity", detail.getQuantity());
				item.put("price", detail.getPrice());
				item.put("bookId", detail.getBook().getId());
				item.put("title", detail.getBook().getTitle());
				item.put("author", detail.getBook().getAuthor());
				item.put("imageUrl", detail.getBook().getImageUrl());
				item.put("stockQuantity", detail.getBook().getQuantity());

				BigDecimal itemTotal = detail.getPrice().multiply(BigDecimal.valueOf(detail.getQuantity()));
				item.put("itemTotal", itemTotal);

				cartItems.add(item);
				totalAmount = totalAmount.add(itemTotal);
				totalItems += detail.getQuantity();
			}

			Map<String, Object> result = new HashMap<>();
			result.put("cartId", cart.getId());
			result.put("status", cart.getStatus());
			result.put("cartItems", cartItems);
			result.put("totalItems", totalItems);
			result.put("totalAmount", totalAmount);
			result.put("shippingFee", BigDecimal.ZERO);
			result.put("finalAmount", totalAmount);

			log.info("Giỏ hàng có {} sản phẩm, tổng tiền: {}", totalItems, totalAmount);
			return result;

		} catch (Exception e) {
			log.error("Lỗi khi lấy thông tin giỏ hàng: ", e);
			throw new RuntimeException("Không thể lấy thông tin giỏ hàng");
		}
	}

	// Cập nhật số lượng
	public Map<String, Object> updateCartItem(Integer userId, Integer cartDetailId, Integer quantity) {
		log.info("Cập nhật số lượng cartDetail ID: {}, số lượng mới: {}", cartDetailId, quantity);

		try {
			if (quantity <= 0) {
				return removeFromCart(userId, cartDetailId);
			}

			// Tìm CartDetail
			CartDetail cartDetail = entityManager.find(CartDetail.class, cartDetailId);
			if (cartDetail == null) {
				throw new RuntimeException("Không tìm thấy sản phẩm trong giỏ hàng");
			}

			// Kiểm tra quyền sở hữu
			if (!cartDetail.getCart().getUser().getId().equals(userId)) {
				throw new RuntimeException("Bạn không có quyền chỉnh sửa giỏ hàng này");
			}

			// Kiểm tra số lượng trong kho
			Book book = cartDetail.getBook();
			if (book.getQuantity() < quantity) {
				throw new RuntimeException("Số lượng trong kho không đủ. Chỉ còn: " + book.getQuantity());
			}

			// Cập nhật số lượng
			int oldQuantity = cartDetail.getQuantity();
			cartDetail.setQuantity(quantity);
			entityManager.merge(cartDetail);

			// Cập nhật thời gian giỏ hàng
			Cart cart = cartDetail.getCart();
			cart.setCreatedDate(LocalDateTime.now());
			entityManager.merge(cart);

			// Log activity
			logActivity(userId, "UPDATE_CART",
					String.format("Cập nhật sách '%s' từ %d thành %d", book.getTitle(), oldQuantity, quantity));

			return getCartSummary(userId);

		} catch (Exception e) {
			log.error("Lỗi khi cập nhật giỏ hàng: ", e);
			throw new RuntimeException(e.getMessage());
		}
	}

	// Xóa sản phẩm khỏi giỏ hàng
	public Map<String, Object> removeFromCart(Integer userId, Integer cartDetailId) {
		log.info("Xóa cartDetail ID: {} khỏi giỏ hàng", cartDetailId);

		try {
			// Tìm CartDetail
			CartDetail cartDetail = entityManager.find(CartDetail.class, cartDetailId);
			if (cartDetail == null) {
				throw new RuntimeException("Không tìm thấy sản phẩm trong giỏ hàng");
			}

			// Kiểm tra quyền sở hữu
			if (!cartDetail.getCart().getUser().getId().equals(userId)) {
				throw new RuntimeException("Bạn không có quyền xóa sản phẩm này");
			}

			String bookTitle = cartDetail.getBook().getTitle();

			// Xóa CartDetail
			entityManager.remove(cartDetail);

			// Cập nhật thời gian giỏ hàng
			Cart cart = cartDetail.getCart();
			cart.setCreatedDate(LocalDateTime.now());
			entityManager.merge(cart);

			// Log activity
			logActivity(userId, "REMOVE_FROM_CART", String.format("Xóa sách '%s' khỏi giỏ hàng", bookTitle));

			return getCartSummary(userId);

		} catch (Exception e) {
			log.error("Lỗi khi xóa khỏi giỏ hàng: ", e);
			throw new RuntimeException("Không thể xóa sản phẩm");
		}
	}

	// Xóa toàn bộ giỏ hàng
	public void clearCart(Integer userId) {
		log.info("Xóa toàn bộ giỏ hàng user ID: {}", userId);

		try {
			Cart cart = getOrCreateCart(userId);

			// Xóa tất cả CartDetail
			TypedQuery<CartDetail> query = entityManager
					.createQuery("SELECT cd FROM CartDetail cd WHERE cd.cart.id = :cartId", CartDetail.class);
			query.setParameter("cartId", cart.getId());

			List<CartDetail> cartDetails = query.getResultList();
			for (CartDetail detail : cartDetails) {
				entityManager.remove(detail);
			}

			// Cập nhật thời gian giỏ hàng
			cart.setCreatedDate(LocalDateTime.now());
			entityManager.merge(cart);

			// Log activity
			logActivity(userId, "CLEAR_CART", "Xóa toàn bộ giỏ hàng");

			log.info("Đã xóa toàn bộ giỏ hàng");

		} catch (Exception e) {
			log.error("Lỗi khi xóa giỏ hàng: ", e);
			throw new RuntimeException("Không thể xóa giỏ hàng");
		}
	}

	// Đếm số lượng sản phẩm trong giỏ
	public Integer getCartItemCount(Integer userId) {
		log.info("Đếm số lượng sản phẩm trong giỏ user ID: {}", userId);

		try {
			Cart cart = getOrCreateCart(userId);

			TypedQuery<Long> query = entityManager.createQuery(
					"SELECT COALESCE(SUM(cd.quantity), 0) FROM CartDetail cd WHERE cd.cart.id = :cartId", Long.class);
			query.setParameter("cartId", cart.getId());

			Long count = query.getSingleResult();
			return count.intValue();

		} catch (Exception e) {
			log.error("Lỗi khi đếm sản phẩm trong giỏ: ", e);
			return 0;
		}
	}

	// ============ HELPER METHODS ============

	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public void logActivity(Integer userId, String activityType, String description) {
	    User user = entityManager.find(User.class, userId);
	    if (user == null) return;

	    ActivityLog activityLog = ActivityLog.builder()
	        .user(user)
	        .activityType(activityType)
	        .description(description)
	        .ipAddress("127.0.0.1")
	        .createdDate(LocalDateTime.now())
	        .build();

	    entityManager.persist(activityLog);
	}

}
