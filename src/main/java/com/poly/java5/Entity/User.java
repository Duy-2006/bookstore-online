package com.poly.java5.Entity;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString; // Đã thêm import

@Data
@Entity
@Table(name = "Users")
public class User implements Serializable {
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, unique = true, length = 50)
    private String username;

    @Column(nullable = false, length = 100)
    private String password;

    @Column(nullable = false, unique = true, length = 100)
    private String email;

    @Column(name = "full_name", length = 100)
    private String fullName;

    @Column(length = 20)
    private String phone;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserRole role = UserRole.USER;

    @Column(nullable = false)
    private Boolean active = true;

    @Column(name = "created_date")
    private LocalDateTime createdDate;

    // ===== RELATION =====
    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    @ToString.Exclude
    private List<Order> orders;

    @PrePersist
    protected void onCreate() {
        createdDate = LocalDateTime.now();
    }

    // ===== ROLE CHECK =====
    public boolean isAdmin() {
        return role == UserRole.ADMIN;
    }

    public boolean isSeller() {
        return role == UserRole.SELLER;
    }

    public boolean isBuyer() {
        return role == UserRole.USER;
    }

    // ===== BUSINESS =====
    public Double getTotalSpending() {
        if (orders == null || orders.isEmpty()) return 0.0;
        return orders.stream()
                .filter(o -> "COMPLETED".equals(o.getStatus()))
                .mapToDouble(o -> o.getTotalAmount().doubleValue())
                .sum();
    }

    public String getCustomerType() {
        double spent = getTotalSpending();
        if (spent >= 5_000_000) return "VIP (Thân thiết)";
        if (spent > 0) return "Tiềm năng";
        return "Khách mới";
    }
}