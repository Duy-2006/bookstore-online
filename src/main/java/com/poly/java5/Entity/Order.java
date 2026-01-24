package com.poly.java5.Entity;

import java.util.Date;
import java.util.List;

import jakarta.persistence.*;
import lombok.Data;
import lombok.ToString; // Đã thêm import

@Data
@Entity
@Table(name = "Orders")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Temporal(TemporalType.TIMESTAMP)
    private Date createDate = new Date();

    private String address; 
    private String phone;   
    private String receiver; 

    private Double totalAmount;

    @Enumerated(EnumType.STRING)
    private OrderStatus status = OrderStatus.PENDING;

    @ManyToOne
    @JoinColumn(name = "user_id")
    @ToString.Exclude // Ngắt để không in ngược lại User
    private User user; 

    @OneToMany(mappedBy = "order")
    @ToString.Exclude // Ngắt để không in danh sách chi tiết đơn hàng
    private List<OrderDetail> orderDetails;
}