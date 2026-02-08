package com.poly.java5.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.poly.java5.Entity.Book;
import com.poly.java5.Entity.Category;
import com.poly.java5.Entity.Promotion;
import com.poly.java5.Entity.PromotionDetail;
import com.poly.java5.Repository.BookRepository;
import com.poly.java5.Repository.CategoryRepository;
import com.poly.java5.Repository.PromotionDetailRepository;
import com.poly.java5.Repository.PromotionRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PromotionService {

	private final PromotionRepository promotionRepository;
	private final PromotionDetailRepository promotionDetailRepository;
	private final BookRepository bookRepository;
	private final CategoryRepository categoryRepository;

	public BigDecimal getFinalPrice(Long bookId) {

		Book book = bookRepository.findById(bookId).orElse(null);
		if (book == null)
			return BigDecimal.ZERO;

		BigDecimal originalPrice = book.getPrice();

		// 1. tìm khuyến mãi theo BOOK
		List<Promotion> bookPromos = promotionDetailRepository.findByBookId(bookId);

		// 2. tìm theo CATEGORY
		List<Promotion> categoryPromos = promotionDetailRepository.findByCategoryId(book.getCategory().getId());

		// 3. tìm ALL
		List<Promotion> allPromos = promotionRepository.findActiveAllPromotions();

		// gộp tất cả
		List<Promotion> all = new ArrayList<>();
		all.addAll(bookPromos);
		all.addAll(categoryPromos);
		all.addAll(allPromos);

		if (all.isEmpty())
			return originalPrice;

		// chọn khuyến mãi mạnh nhất
		BigDecimal finalPrice = originalPrice;

		for (Promotion p : all) {
			BigDecimal discounted = calculateDiscount(originalPrice, p);

			if (discounted.compareTo(finalPrice) < 0) {
				finalPrice = discounted;
			}
		}

		return finalPrice;
	}

	private BigDecimal calculateDiscount(BigDecimal price, Promotion promotion) {

		if ("PERCENT".equals(promotion.getDiscountType())) {
			return price.subtract(price.multiply(promotion.getDiscountValue()).divide(BigDecimal.valueOf(100)));
		}

		if ("MONEY".equals(promotion.getDiscountType())) {
			return price.subtract(promotion.getDiscountValue());
		}

		return price;
	}

	public List<Promotion> getAll() {
		return promotionRepository.findAll();
	}

	@Transactional
	public void createPromotion(Promotion promotion, List<Long> bookIds, List<Long> categoryIds) {

		promotion.setStatus(true);

		Promotion savedPromotion = promotionRepository.save(promotion);

		List<PromotionDetail> details = new ArrayList<>();

		// áp dụng theo BOOK
		if (bookIds != null) {
			for (Long id : bookIds) {

				Book book = bookRepository.findById(id).orElse(null);
				if (book == null)
					continue;

				PromotionDetail d = new PromotionDetail();
				d.setPromotion(savedPromotion);
				d.setBook(book);

				details.add(d);
			}
		}

		// áp dụng theo CATEGORY
		if (categoryIds != null) {
			for (Long id : categoryIds) {

				Category cate = categoryRepository.findById(id).orElse(null);
				if (cate == null)
					continue;

				PromotionDetail d = new PromotionDetail();
				d.setPromotion(savedPromotion);
				d.setCategory(cate);

				details.add(d);
			}
		}

		promotionDetailRepository.saveAll(details);
	}

	public Promotion getById(Long id) {
	    return promotionRepository.findById(id)
	            .orElseThrow(() -> new RuntimeException("Không tìm thấy promotion id = " + id));
	}


	public void updatePromotion(Promotion promotion, List<Long> bookIds, List<Long> categoryIds) {

		Promotion old = promotionRepository.findById(promotion.getId()).orElseThrow();

		old.setName(promotion.getName());
		old.setDiscountType(promotion.getDiscountType());
		old.setDiscountValue(promotion.getDiscountValue());
		old.setStartDate(promotion.getStartDate());
		old.setEndDate(promotion.getEndDate());
		old.setStatus(promotion.getStatus());

// clear relation cũ
		old.getBooks().clear();
		old.getCategories().clear();

		if (bookIds != null) {
			old.setBooks(bookRepository.findAllById(bookIds));
		}

		if (categoryIds != null) {
			old.setCategories(categoryRepository.findAllById(categoryIds));
		}

		promotionRepository.save(old);
	}
	public void deletePromotion(Long id) {

	    Promotion promotion = promotionRepository.findById(id)
	            .orElseThrow(() -> new RuntimeException("Không tìm thấy promotion"));

	    promotion.getBooks().clear();
	    promotion.getCategories().clear();

	    promotionRepository.save(promotion); // update lại DB

	    promotionRepository.delete(promotion);
	}


}
