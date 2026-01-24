package com.poly.java5.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.poly.java5.Entity.Book;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class BookService {
	@PersistenceContext
    private EntityManager entityManager;
    
    // 1️⃣ LẤY TẤT CẢ SÁCH
    public List<Book> getAllBooks() {
        log.info(" Lấy tất cả sách từ database...");
        
        try {
            TypedQuery<Book> query = entityManager.createQuery(
                "SELECT b FROM Book b ORDER BY b.id DESC", 
                Book.class
            );
            
            List<Book> books = query.getResultList();
            log.info(" Lấy được {} cuốn sách", books.size());
            return books;
            
        } catch (Exception e) {
            log.error(" Lỗi khi lấy sách: ", e);
            throw new RuntimeException("Không thể lấy danh sách sách");
        }
    }
    
    // 2️⃣ LẤY SÁCH THEO ID
    public Book getBookById(Integer id) {
        log.info(" Tìm sách theo ID: {}", id);
        
        try {
            Book book = entityManager.find(Book.class, id);
            
            if (book == null) {
                log.warn("⚠ Không tìm thấy sách với ID: {}", id);
                throw new RuntimeException("Không tìm thấy sách");
            }
            
            log.info(" Tìm thấy sách: {}", book.getTitle());
            return book;
            
        } catch (Exception e) {
            log.error(" Lỗi khi lấy sách theo ID: ", e);
            throw new RuntimeException("Lỗi khi lấy thông tin sách");
        }
    }
    
    
 // 🔍 TÌM SÁCH (Tên | Tác giả | Thể loại | ISBN)
    public List<Book> searchBooks(
            String title,
            String author,
            String category,
            String isbn) {

        log.info("🔍 Tìm sách với điều kiện - title: {}, author: {}, category: {}, isbn: {}",
                title, author, category, isbn);

        try {
            StringBuilder jpql = new StringBuilder(
                "SELECT b FROM Book b WHERE 1=1 "
            );

            if (title != null && !title.isBlank()) {
                jpql.append(" AND LOWER(b.title) LIKE :title ");
            }

            if (author != null && !author.isBlank()) {
                jpql.append(" AND LOWER(b.author) LIKE :author ");
            }

            if (category != null && !category.isBlank()) {
                jpql.append(" AND LOWER(b.category.name) LIKE :category ");
            }

            if (isbn != null && !isbn.isBlank()) {
                jpql.append(" AND b.isbn LIKE :isbn ");
            }

            jpql.append(" ORDER BY b.title ASC");

            TypedQuery<Book> query = entityManager.createQuery(jpql.toString(), Book.class);

            if (title != null && !title.isBlank()) {
                query.setParameter("title", "%" + title.toLowerCase() + "%");
            }

            if (author != null && !author.isBlank()) {
                query.setParameter("author", "%" + author.toLowerCase() + "%");
            }

            if (category != null && !category.isBlank()) {
                query.setParameter("category", "%" + category.toLowerCase() + "%");
            }

            if (isbn != null && !isbn.isBlank()) {
                query.setParameter("isbn", "%" + isbn + "%");
            }

            List<Book> books = query.getResultList();
            log.info("✅ Tìm thấy {} sách", books.size());

            return books;

        } catch (Exception e) {
            log.error("❌ Lỗi tìm kiếm sách: ", e);
            throw new RuntimeException("Lỗi khi tìm kiếm sách");
        }
    }
    
    // 4️⃣ LẤY SÁCH MỚI NHẤT (10 cuốn)
    public List<Book> getNewBooks() {
        log.info(" Lấy 10 sách mới nhất...");
        
        try {
            TypedQuery<Book> query = entityManager.createQuery(
                "SELECT b FROM Book b ORDER BY b.id DESC", 
                Book.class
            );
            query.setMaxResults(10);
            
            return query.getResultList();
            
        } catch (Exception e) {
            log.error(" Lỗi khi lấy sách mới: ", e);
            throw new RuntimeException("Lỗi khi lấy sách mới");
        }
    }
}
