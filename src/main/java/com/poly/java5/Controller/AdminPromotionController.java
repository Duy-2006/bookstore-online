package com.poly.java5.Controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.poly.java5.Entity.Promotion;
import com.poly.java5.Repository.BookRepository;
import com.poly.java5.Repository.CategoryRepository;
import com.poly.java5.Repository.PromotionRepository;
import com.poly.java5.Service.PromotionService;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/admin/promotions")
@RequiredArgsConstructor
public class AdminPromotionController {

    private final PromotionService promotionService;
    private final BookRepository bookRepository;
    private final CategoryRepository categoryRepository;
    private final PromotionRepository promotionRepository;

    // ================== LIST ==================
    @GetMapping
    public String list(Model model) {
        model.addAttribute("promotions", promotionService.getAll());
        return "promotion/list"; 
    }

    // ================== CREATE FORM ==================
    @GetMapping("/create")
    public String createForm(Model model) {

        model.addAttribute("promotion", new Promotion());
        model.addAttribute("books", bookRepository.findByDeletedFalse());
        model.addAttribute("categories", categoryRepository.findAll());

        return "promotion/create";
    }

    // ================== SAVE ==================
    @PostMapping("/save")
    public String save(@ModelAttribute Promotion promotion,
                       @RequestParam(required = false) List<Long> bookIds,
                       @RequestParam(required = false) List<Long> categoryIds) {

        promotionService.createPromotion(promotion, bookIds, categoryIds);
        
        if ((bookIds == null || bookIds.isEmpty()) 
        	    && (categoryIds == null || categoryIds.isEmpty())) {

        	    // hiểu là áp dụng toàn sàn
        	}

        return "redirect:/admin/promotions";
    }
 // ================== EDIT FORM ==================
    @GetMapping("/edit/{id}")
    public String editForm(@PathVariable Long id, Model model) {

        Promotion promotion = promotionService.getById(id);

        model.addAttribute("promotion", promotion);
        model.addAttribute("books", bookRepository.findByDeletedFalse());
        model.addAttribute("categories", categoryRepository.findAll());

        return "promotion/edit";
    }
    @PostMapping("/update")
    public String updatePromotion(
            @ModelAttribute Promotion promotion,
            @RequestParam(required = false) List<Long> bookIds,
            @RequestParam(required = false) List<Long> categoryIds
    ) {

        Promotion old = promotionRepository.findById(promotion.getId()).orElseThrow();

        old.setName(promotion.getName());
        old.setDiscountType(promotion.getDiscountType());
        old.setDiscountValue(promotion.getDiscountValue());
        old.setStartDate(promotion.getStartDate());
        old.setEndDate(promotion.getEndDate());
        old.setStatus(promotion.getStatus());

        // cập nhật book
        old.getBooks().clear();
        if (bookIds != null) {
            old.setBooks(bookRepository.findAllById(bookIds));
        }

        // cập nhật category
        old.getCategories().clear();
        if (categoryIds != null) {
            old.setCategories(categoryRepository.findAllById(categoryIds));
        }

        promotionRepository.save(old);

        return "redirect:/admin/promotions";
    }

}
