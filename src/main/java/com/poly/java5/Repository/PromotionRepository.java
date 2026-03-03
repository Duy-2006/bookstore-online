package com.poly.java5.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.poly.java5.Entity.Promotion;

public interface PromotionRepository extends JpaRepository<Promotion, Integer> {

    
    @Query("SELECT p FROM Promotion p " +
    	       "WHERE p.status = true " +
    	       "AND CURRENT_DATE BETWEEN p.startDate AND p.endDate " +
    	       "AND EXISTS (SELECT 1 FROM p.books b WHERE b.id = :bookId)")
    	List<Promotion> findActivePromotionsByBookId(@Param("bookId") Integer bookId);

    	@Query("SELECT p FROM Promotion p " +
    	       "WHERE p.status = true " +
    	       "AND CURRENT_DATE BETWEEN p.startDate AND p.endDate " +
    	       "AND EXISTS (SELECT 1 FROM p.categories c WHERE c.id = :categoryId)")
    	List<Promotion> findActivePromotionsByCategoryId(@Param("categoryId") Integer categoryId);

    	@Query("SELECT p FROM Promotion p " +
    	       "WHERE p.status = true " +
    	       "AND CURRENT_DATE BETWEEN p.startDate AND p.endDate " +
    	       "AND p.applyType = 'ALL'")
    	List<Promotion> findActiveAllPromotions();
}

