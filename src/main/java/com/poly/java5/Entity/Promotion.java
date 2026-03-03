package com.poly.java5.Entity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "promotions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Promotion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    // Tên chương trình khuyến mãi
    @Column(nullable = false, length = 200)
    private String name;

    // PERCENT hoặc MONEY
    @Column(name = "discount_type", length = 20)
    private String discountType;

    // giá trị giảm (10% hoặc 50000đ)
    @Column(name = "discount_value", precision = 10, scale = 2)
    private BigDecimal discountValue;

    // thời gian áp dụng
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Column(name = "start_date")
    private LocalDate startDate;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Column(name = "end_date")
    private LocalDate endDate;

    // đang bật hay tắt
    @Column(name = "status")
    private Boolean status = true;

    // ALL | BOOK | CATEGORY
    @Column(name = "apply_type", length = 20)
    private String applyType;

    // chi tiết áp dụng cho sách / thể loại
    @OneToMany(mappedBy = "promotion", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PromotionDetail> details;
    
    

}

