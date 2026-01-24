package com.poly.java5.Service;

import com.poly.java5.Entity.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class UserService {
	@PersistenceContext
    private EntityManager manager;

    /**
     * Đăng ký user
     */
    @Transactional
    public Map<String, String> register(User user) {
        Map<String, String> errorsMap = new HashMap<>();

        // 1️⃣ Kiểm tra trùng dữ liệu
        String sql = "SELECT u FROM User u WHERE u.username = :username OR u.email = :email OR u.phone = :phone";
        Query query = manager.createQuery(sql, User.class);
        query.setParameter("username", user.getUsername());
        query.setParameter("email", user.getEmail());
        query.setParameter("phone", user.getPhone());

        List<User> users = query.getResultList();
        for (User u : users) {
            if (u.getUsername().equals(user.getUsername())) {
                errorsMap.put("username", "Tên đăng nhập đã tồn tại");
            }
            if (u.getEmail().equals(user.getEmail())) {
                errorsMap.put("email", "Email đã tồn tại");
            }
            if (u.getPhone().equals(user.getPhone())) {
                errorsMap.put("phone", "Số điện thoại đã tồn tại");
            }
        }

        // 2️⃣ Nếu không có lỗi -> persist
        if (errorsMap.isEmpty()) {
            try {
                manager.persist(user);
            } catch (Exception e) {
                // Bắt lỗi unique constraint nếu có (concurrent)
                errorsMap.put("db", "Dữ liệu đã tồn tại trong hệ thống");
            }
        }

        return errorsMap;
    }

    /**
     * Login user (username/email + password)
     */
    public User login(String usernameOrEmail, String password) {
        String jpql = "SELECT u FROM User u WHERE u.username = :ue OR u.email = :ue";
        Query query = manager.createQuery(jpql, User.class);
        query.setParameter("ue", usernameOrEmail);
        List<User> users = query.getResultList();

        if (!users.isEmpty()) {
            User u = users.get(0);
            if (u.getPassword().equals(password)) {
                return u;
            }
        }
        return null;
    }
}
