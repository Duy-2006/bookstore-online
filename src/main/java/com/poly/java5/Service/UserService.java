package com.poly.java5.Service;

import com.poly.java5.Bean.LoginBean;
import com.poly.java5.Entity.User;
import com.poly.java5.Utils.Utils;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import jakarta.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class UserService {
	 @PersistenceContext
	    private EntityManager manager;

	    // =========================
	    // REGISTER
	    // =========================
	    @Transactional
	    public Map<String, String> register(User user) {

	        Map<String, String> errorsMap = new HashMap<>();

	        String jpql = """
	            SELECT u FROM User u
	            WHERE u.username = :username
	               OR u.email = :email
	               OR u.phone = :phone
	        """;

	        List<User> users = manager
	                .createQuery(jpql, User.class)
	                .setParameter("username", user.getUsername())
	                .setParameter("email", user.getEmail())
	                .setParameter("phone", user.getPhone())
	                .getResultList();

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

	        if (errorsMap.isEmpty()) {
	            // 🔐 HASH PASSWORD
	            user.setPassword(Utils.hashPassword(user.getPassword()));
	            manager.persist(user);
	        }

	        return errorsMap;
	    }

	    // =========================
	    // LOGIN
	   
	    // =========================
	    @Transactional
	public User login(String usernameOrEmail, String password) {

    String jpql = """
        SELECT u FROM User u
        WHERE (u.username = :ue OR u.email = :ue)
          AND u.active = true
    """;

    Query query = manager.createQuery(jpql, User.class);
    query.setParameter("ue", usernameOrEmail);

    List<User> users = query.getResultList();
    if (users.isEmpty()) return null;

    User user = users.get(0);

    String dbPassword = user.getPassword();
    String inputHashed = Utils.hashPassword(password);

    // ===== LOG DEBUG =====
    System.out.println(">>> DB PASS    = " + dbPassword);
    System.out.println(">>> INPUT HASH = " + inputHashed);

    // =========================
    // CASE 1: DB đang lưu HASH
    // =========================
    if (dbPassword.length() >= 60 || dbPassword.length() >= 40) {
        if (dbPassword.equals(inputHashed)) {
            return user;
        }
    }

    // =========================
    // CASE 2: DB đang lưu PLAIN TEXT
    // =========================
    if (dbPassword.equals(password)) {
        System.out.println("⚠ PASSWORD PLAIN → AUTO HASH");

        // auto hash lại để lần sau chuẩn
        user.setPassword(inputHashed);
        manager.merge(user);

        return user;
    }

    System.out.println("❌ WRONG PASSWORD");
    return null;
}


	    // =========================
	    // FIND BY ID (CHO MENU)
	    // =========================
	    public User findById(Integer id) {
	        return manager.find(User.class, id);
	    }

	    @Transactional
	    public void save(User user) {
	        manager.merge(user);
	    }
		
	    
	  
}
