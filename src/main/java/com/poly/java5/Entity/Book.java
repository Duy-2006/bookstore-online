package com.poly.java5.Entity;

import java.util.Date;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.ToString; // Đã thêm import này

@Data
@Entity
@Table(name = "Books")
public class Book {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Tên sách không được để trống")
    private String title;

    @NotBlank(message = "ISBN không được để trống")
    private String isbn;

    @ManyToOne
    @JoinColumn(name = "author_id")
    @ToString.Exclude // Ngắt toString tại đây để tránh vòng lặp với Author
    private Author author;

    private String publisher;

    @Min(value = 0, message = "Giá nhập phải dương")
    private Double importPrice;

    @Min(value = 0, message = "Giá bán phải dương")
    private Double salePrice;

    @Min(value = 0, message = "Số lượng phải dương")
    private Integer quantity;

    private String image; // Lưu tên file ảnh chính
    
    @Column(columnDefinition = "nvarchar(MAX)")
    private String description;

    private Boolean active = true; // Trạng thái: Đang bán / Ngừng bán
    private Boolean deleted = false; // Soft delete: false = chưa xóa

    @Temporal(TemporalType.DATE)
    private Date createDate = new Date();

    @ManyToOne
    @JoinColumn(name = "category_id")
    @ToString.Exclude // Ngắt toString tại đây để tránh vòng lặp với Category
    private Category category;
}