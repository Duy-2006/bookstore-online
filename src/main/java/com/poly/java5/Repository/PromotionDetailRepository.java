package com.poly.java5.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.poly.java5.Entity.Promotion;
import com.poly.java5.Entity.PromotionDetail;

public interface PromotionDetailRepository extends JpaRepository<PromotionDetail, Integer> {

	@Query("""
		    SELECT p
		    FROM Promotion p
		    JOIN p.books b
		    WHERE b.id = :bookId
		    AND p.status = true
		    AND CURRENT_DATE BETWEEN p.startDate AND p.endDate
		""")
		List<Promotion> findActiveByBookId(@Param("bookId") Integer bookId);


    @Query("""
    		SELECT pd.promotion 
    		FROM PromotionDetail pd 
    		WHERE pd.category.id = :categoryId 
    		AND pd.promotion.status = true 
    		AND CURRENT_DATE BETWEEN pd.promotion.startDate AND pd.promotion.endDate
    		""")
    		List<Promotion> findByCategoryId(Long categoryId);
    
    

}

