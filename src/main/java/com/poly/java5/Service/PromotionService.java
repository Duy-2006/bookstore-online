package com.poly.java5.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.poly.java5.Entity.Book;
import com.poly.java5.Entity.Category;
import com.poly.java5.Entity.Promotion;
import com.poly.java5.Entity.PromotionDetail;
import com.poly.java5.Repository.BookRepository;
import com.poly.java5.Repository.CategoryRepository;
import com.poly.java5.Repository.PromotionDetailRepository;
import com.poly.java5.Repository.PromotionRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PromotionService {

    private final PromotionRepository promotionRepository;
    private final PromotionDetailRepository promotionDetailRepository;
    private final BookRepository bookRepository;
    private final CategoryRepository categoryRepository;

    private final LocalDate today = LocalDate.now(); // Có thể inject nếu cần timezone khác

    /**
     * Tính giá cuối cùng sau khi áp dụng khuyến mãi mạnh nhất (giá thấp nhất)
     */
    @Transactional(readOnly = true)
    public BigDecimal getFinalPrice(Integer bookId) {
        Book book = bookRepository.findById(bookId).orElse(null);
        if (book == null) return BigDecimal.ZERO;

        BigDecimal originalPrice = book.getPrice();
        if (originalPrice == null || originalPrice.compareTo(BigDecimal.ZERO) <= 0) {
            return originalPrice;
        }

        List<Promotion> activePromotions = getActivePromotionsForBook(book);
        if (activePromotions.isEmpty()) return originalPrice;

        return activePromotions.stream()
                .map(p -> calculateDiscountedPrice(originalPrice, p))
                .min(BigDecimal::compareTo)
                .orElse(originalPrice);
    }

    /**
     * Tính % giảm giá cao nhất đang áp dụng cho sách (dùng để hiển thị badge % trên card)
     * Trả về null nếu không có khuyến mãi nào
     */
    @Transactional(readOnly = true)
public BigDecimal getMaxDiscountPercentage(Integer bookId) {
    Book book = bookRepository.findById(bookId).orElse(null);
    if (book == null) return null;

    List<Promotion> activePromos = getActivePromotionsForBook(book);
    if (activePromos.isEmpty()) return null;

    return activePromos.stream()
            .map(p -> getPromotionDiscountPercentage(p, book.getPrice()))
            .filter(p -> p != null && p.compareTo(BigDecimal.ZERO) > 0)
            .max(BigDecimal::compareTo)
            .orElse(null);
}

    private List<Promotion> getActivePromotionsForBook(Book book) {

        List<Promotion> bookPromos =
            promotionDetailRepository.findActiveByBookId(book.getId());

        List<Promotion> categoryPromos =
            (book.getCategory() != null)
                ? promotionDetailRepository.findByCategoryId(book.getCategory().getId())
                : List.of();

        List<Promotion> allPromos =
            promotionRepository.findActiveAllPromotions();

        return Stream.of(bookPromos, categoryPromos, allPromos)
                .flatMap(List::stream)
                .distinct()
                .collect(Collectors.toList());
    }

    private BigDecimal calculateDiscountedPrice(BigDecimal price, Promotion promo) {
        if (promo == null || promo.getDiscountValue() == null) {
            return price;
        }

        if ("PERCENT".equalsIgnoreCase(promo.getDiscountType())) {
            BigDecimal discount = price.multiply(promo.getDiscountValue())
                    .divide(BigDecimal.valueOf(100), 2, BigDecimal.ROUND_HALF_UP);
            return price.subtract(discount);
        }

        if ("AMOUNT".equalsIgnoreCase(promo.getDiscountType())) {
            return price.subtract(promo.getDiscountValue());
        }

        return price;
    }

    /**
     * Tính % giảm của 1 promotion (chỉ khi type PERCENT, MONEY thì ước lượng %)
     */
    private BigDecimal getPromotionDiscountPercentage(Promotion promo, BigDecimal originalPrice) {

        if ("PERCENT".equalsIgnoreCase(promo.getDiscountType())) {
            return promo.getDiscountValue();
        }

        if ("AMOUNT".equalsIgnoreCase(promo.getDiscountType())
                && originalPrice != null
                && originalPrice.compareTo(BigDecimal.ZERO) > 0) {

            return promo.getDiscountValue()
                    .multiply(BigDecimal.valueOf(100))
                    .divide(originalPrice, 0, BigDecimal.ROUND_HALF_UP);
        }

        return null;
    }

    @Transactional(readOnly = true)
    public List<Promotion> getAll() {
        return promotionRepository.findAll();
    }

    @Transactional
public Promotion createPromotion(Promotion promotion,
                                  List<Integer> bookIds,
                                  List<Integer> categoryIds) {

    promotion.setStatus(true);
    Promotion saved = promotionRepository.save(promotion);

    savePromotionRelations(saved, bookIds, categoryIds);
    return saved;
}

    @Transactional
    public Promotion updatePromotion(Promotion promotion, List<Integer> bookIds, List<Integer> categoryIds) {
        Promotion existing = promotionRepository.findById(promotion.getId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy promotion id = " + promotion.getId()));

        // Update fields
        existing.setName(promotion.getName());
        existing.setDiscountType(promotion.getDiscountType());
        existing.setDiscountValue(promotion.getDiscountValue());
        existing.setStartDate(promotion.getStartDate());
        existing.setEndDate(promotion.getEndDate());
        existing.setStatus(promotion.getStatus());

        // Xóa quan hệ cũ và thêm mới
        existing.getBooks().clear();
        existing.getCategories().clear();

        if (bookIds != null && !bookIds.isEmpty()) {
            existing.getBooks().addAll(bookRepository.findAllById(bookIds));
        }
        if (categoryIds != null && !categoryIds.isEmpty()) {
            existing.getCategories().addAll(categoryRepository.findAllById(categoryIds));
        }

        return promotionRepository.save(existing);
    }

    @Transactional
    public void deletePromotion(Integer id) {
        Promotion promo = promotionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy promotion id = " + id));

        // Clear relations trước khi delete
        promo.getBooks().clear();
        promo.getCategories().clear();
        promotionRepository.save(promo); // flush clear

        promotionRepository.delete(promo);
    }

    private void savePromotionRelations(Promotion promotion,
                                    List<Integer> bookIds,
                                    List<Integer> categoryIds) {

    List<PromotionDetail> details = new ArrayList<>();

    Optional.ofNullable(bookIds).orElse(List.of()).forEach(id -> {
        bookRepository.findById(id).ifPresent(book -> {
            PromotionDetail detail = new PromotionDetail();
            detail.setPromotion(promotion);
            detail.setBook(book);
            details.add(detail);
        });
    });

    Optional.ofNullable(categoryIds).orElse(List.of()).forEach(id -> {
        categoryRepository.findById(id).ifPresent(cat -> {
            PromotionDetail detail = new PromotionDetail();
            detail.setPromotion(promotion);
            detail.setCategory(cat);
            details.add(detail);
        });
    });

    if (!details.isEmpty()) {
        promotionDetailRepository.saveAll(details);
    }
}
    public Optional<Promotion> findById(Integer id) {
        return promotionRepository.findById(id);
    }
}