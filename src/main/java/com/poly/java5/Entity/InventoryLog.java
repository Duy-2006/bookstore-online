package com.poly.java5.Entity;

import java.util.Date;
import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "InventoryLogs")
public class InventoryLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "book_id")
    private Book book;

    private Integer changeAmount; // Số lượng thay đổi (Dương là nhập, Âm là bán)
    
    private String type; // "IMPORT" (Nhập) hoặc "SALE" (Bán) hoặc "CANCEL" (Hủy đơn trả hàng)
    
    private String note; // Ghi chú (Ví dụ: Nhập hàng đợt 1, Bán cho đơn #10)

    @Temporal(TemporalType.TIMESTAMP)
    private Date logDate = new Date();

    // Constructor tiện lợi
    public InventoryLog() {}
    public InventoryLog(Book book, Integer changeAmount, String type, String note) {
        this.book = book;
        this.changeAmount = changeAmount;
        this.type = type;
        this.note = note;
    }
}