package com.poly.java5.Entity;

import java.io.Serializable;
import java.util.List;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.ToString; // Đã thêm import

@Data
@Entity
@Table(name = "Users")
public class User implements Serializable {

    @Id
    @NotBlank(message = "Username không được để trống")
    @Column(length = 50)
    private String username;

    @NotBlank(message = "Mật khẩu không được để trống")
    private String password;

    @NotBlank(message = "Họ tên không được để trống")
    private String fullname;

    @Email(message = "Email không đúng định dạng")
    private String email;

    @Column(nullable = false)
    private Boolean active = true;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserRole role = UserRole.USER;

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    @ToString.Exclude // QUAN TRỌNG: Tránh quét list Order khi in log User
    private List<Order> orders;

    /* ================= BUSINESS LOGIC ================= */

    public Double getTotalSpending() {
        if (orders == null || orders.isEmpty()) return 0.0;
        return orders.stream()
                .filter(o -> o.getStatus() == OrderStatus.COMPLETED)
                .mapToDouble(Order::getTotalAmount)
                .sum();
    }

    public String getCustomerType() {
        double spent = getTotalSpending();
        if (spent >= 5_000_000) return "VIP (Thân thiết)";
        if (spent > 0) return "Tiềm năng";
        return "Khách mới";
    }
}