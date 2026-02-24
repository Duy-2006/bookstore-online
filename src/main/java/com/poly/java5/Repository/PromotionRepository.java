package com.poly.java5.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.poly.java5.Entity.Promotion;

public interface PromotionRepository extends JpaRepository<Promotion, Long> {

    @Query("""
        SELECT p FROM Promotion p
        WHERE p.status = true
        AND CURRENT_DATE BETWEEN p.startDate AND p.endDate
        AND p.applyType = 'ALL'
    """)
    List<Promotion> findActiveAllPromotions();
}

