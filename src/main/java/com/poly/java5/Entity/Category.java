package com.poly.java5.Entity;
import lombok.*;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "Categories")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Category {
	 @Id
	    @GeneratedValue(strategy = GenerationType.IDENTITY)
	    @Column(name = "id")
	    private Integer id;
	    
	    @Column(name = "name", nullable = false, length = 100)
	    private String name;
	    
	    @Column(name = "description", columnDefinition = "NVARCHAR(500)")
	    private String description;
	    
	    @OneToMany(mappedBy = "category", cascade = CascadeType.ALL)
	    @ToString.Exclude
	    @EqualsAndHashCode.Exclude
	    private List<Book> books;
}
