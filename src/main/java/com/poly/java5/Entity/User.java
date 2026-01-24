package com.poly.java5.Entity;

import lombok.*;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import com.poly.java5.Entity.Order;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {
	 @Id
	    @GeneratedValue(strategy = GenerationType.IDENTITY)
	    @Column(name = "id")
	    private Integer id;
	    
	    @Column(name = "username", nullable = false, unique = true, length = 50)
	    private String username;
	    
	    @Column(name = "password", nullable = false, length = 100)
	    private String password;
	    
	    @Column(name = "email", nullable = false, unique = true, length = 100)
	    private String email;
	    
	    @Column(name = "full_name", length = 100)
	    private String fullName;
	    
	    @Column(name = "phone", length = 20)
	    private String phone;
	    
	    @Column(name = "user_type", length = 10)
	    private String userType = "BUYER";  // ADMIN, SELLER, BUYER
	    
	    @Column(name = "created_date")
	    private LocalDateTime createdDate;
	    
	    // OneToMany relationships
	    @OneToMany(mappedBy = "seller", cascade = CascadeType.ALL)
	    @ToString.Exclude
	    @EqualsAndHashCode.Exclude
	    private List<Book> booksForSale;
	    
	    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
	    @ToString.Exclude
	    @EqualsAndHashCode.Exclude
	    private List<Order> orders;
	    
	    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
	    @ToString.Exclude
	    @EqualsAndHashCode.Exclude
	    private List<Cart> cartItems;
	    
	    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
	    @ToString.Exclude
	    @EqualsAndHashCode.Exclude
	    private List<Review> reviews;
	    
	    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
	    @ToString.Exclude
	    @EqualsAndHashCode.Exclude
	    private List<Wishlist> wishlists;
	    
	    @PrePersist
	    protected void onCreate() {
	        createdDate = LocalDateTime.now();
	    }
	    
	    // Business methods
	    public boolean isAdmin() {
	        return "ADMIN".equals(userType);
	    }
	    
	    public boolean isSeller() {
	        return "SELLER".equals(userType);
	    }
	    
	    public boolean isBuyer() {
	        return "BUYER".equals(userType);
	    }
}
