package com.poly.java5.Controller;

import java.io.File;


import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.poly.java5.Entity.*;
import com.poly.java5.Repository.*;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private BookRepository bookRepo;

    @Autowired
    private CategoryRepository catRepo;

    @Autowired
    private AuthorRepository authorRepo;

    @Autowired
    private OrderRepository orderRepo;
    
    @Autowired
    private UserRepository userRepo;
    
    @Autowired
    private InventoryLogRepository inventoryRepo;
    
    @Autowired
    private HttpSession session;

    // --- HELPER: KIỂM TRA QUYỀN ADMIN ---
    private boolean isAdmin() {
        User user = (User) session.getAttribute("currentUser");
        return user != null && user.getRole() == UserRole.ADMIN;
    }

    // ================= DASHBOARD =================
    @GetMapping
    public String dashboard() {
        return "admin/dashboard";
    }

    // ================== QUẢN LÝ THỂ LOẠI ==================
    @GetMapping("/categories")
    public String listCategories(Model model) {
        model.addAttribute("categories", catRepo.findAll());
        return "admin/categories";
    }

    @GetMapping("/categories/create")
    public String formCategory(Model model) {
        model.addAttribute("category", new Category());
        return "admin/category-form";
    }

    @GetMapping("/categories/edit/{id}")
    public String editCategory(@PathVariable("id") Long id, Model model) {
        Category cat = catRepo.findById(id).orElseThrow(() -> new RuntimeException("Not found"));
        model.addAttribute("category", cat);
        return "admin/category-form";
    }

    @PostMapping("/categories/save")
    public String saveCategory(@Valid @ModelAttribute("category") Category category, BindingResult result) {
        if (result.hasErrors()) return "admin/category-form";
        catRepo.save(category);
        return "redirect:/admin/categories";
    }

    @GetMapping("/categories/delete/{id}")
    public String deleteCategory(@PathVariable("id") Long id, RedirectAttributes params) {
        // SECURITY CHECK
        if (!isAdmin()) {
            params.addFlashAttribute("error", "Quyền hạn không đủ: Chỉ Admin được xóa dữ liệu!");
            return "redirect:/admin/categories";
        }

        Category cat = catRepo.findById(id).orElse(null);
        if (cat != null && cat.getBooks() != null && !cat.getBooks().isEmpty()) {
            params.addFlashAttribute("error", "Không thể xóa thể loại đang có sách!");
        } else {
            catRepo.deleteById(id);
            params.addFlashAttribute("success", "Xóa thành công!");
        }
        return "redirect:/admin/categories";
    }

    // ================== QUẢN LÝ TÁC GIẢ ==================
    @GetMapping("/authors")
    public String listAuthors(Model model) {
        model.addAttribute("authors", authorRepo.findAll());
        return "admin/authors";
    }

    @GetMapping("/authors/create")
    public String formAuthor(Model model) {
        model.addAttribute("author", new Author());
        return "admin/author-form";
    }

    @GetMapping("/authors/edit/{id}")
    public String editAuthor(@PathVariable("id") Long id, Model model) {
        Author auth = authorRepo.findById(id).orElseThrow(() -> new RuntimeException("Not found"));
        model.addAttribute("author", auth);
        return "admin/author-form";
    }

    @PostMapping("/authors/save")
    public String saveAuthor(@Valid @ModelAttribute("author") Author author, BindingResult result) {
        if (result.hasErrors()) return "admin/author-form";
        authorRepo.save(author);
        return "redirect:/admin/authors";
    }

    @GetMapping("/authors/delete/{id}")
    public String deleteAuthor(@PathVariable("id") Long id, RedirectAttributes params) {
        // SECURITY CHECK
        if (!isAdmin()) {
            params.addFlashAttribute("error", "Quyền hạn không đủ: Chỉ Admin được xóa dữ liệu!");
            return "redirect:/admin/authors";
        }

        Author auth = authorRepo.findById(id).orElse(null);
        if (auth != null && auth.getBooks() != null && !auth.getBooks().isEmpty()) {
            params.addFlashAttribute("error", "Không thể xóa tác giả đang có sách!");
        } else {
            authorRepo.deleteById(id);
            params.addFlashAttribute("success", "Xóa thành công!");
        }
        return "redirect:/admin/authors";
    }

    // ================= QUẢN LÝ SÁCH (BOOK) =================
    @GetMapping("/books")
    public String listBooks(Model model) {
        model.addAttribute("books", bookRepo.findByDeletedFalse());
        return "admin/books";
    }

    @GetMapping("/books/create")
    public String createBook(Model model) {
        model.addAttribute("book", new Book());
        model.addAttribute("categories", catRepo.findAll());
        model.addAttribute("authors", authorRepo.findAll());
        return "admin/book-form";
    }

    @GetMapping("/books/edit/{id}")
    public String editBook(@PathVariable("id") Long id, Model model) {
        Book book = bookRepo.findById(id).orElseThrow(() -> new RuntimeException("Not found"));
        model.addAttribute("book", book);
        model.addAttribute("categories", catRepo.findAll());
        model.addAttribute("authors", authorRepo.findAll());
        return "admin/book-form";
    }

    @PostMapping("/books/save")
    public String saveBook(@Valid @ModelAttribute("book") Book book, 
                           BindingResult result,
                           @RequestParam("imageFile") MultipartFile file, 
                           Model model) {
        if (result.hasErrors()) {
            model.addAttribute("categories", catRepo.findAll());
            model.addAttribute("authors", authorRepo.findAll());
            return "admin/book-form";
        }

        if (!file.isEmpty()) {
            try {
                String filename = file.getOriginalFilename();
                File saveFile = new ClassPathResource("static/images/books").getFile();
                Path path = Paths.get(saveFile.getAbsolutePath() + File.separator + filename);
                Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
                book.setImageUrl(filename);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        bookRepo.save(book);
        return "redirect:/admin/books";
    }

    @GetMapping("/books/delete/{id}")
    public String deleteBook(@PathVariable("id") Long id, RedirectAttributes params) {
        // SECURITY CHECK
        if (!isAdmin()) {
            params.addFlashAttribute("error", "Quyền hạn không đủ: Chỉ Admin được xóa dữ liệu!");
            return "redirect:/admin/books";
        }

        Book book = bookRepo.findById(id).orElse(null);
        if (book != null) {
//            book.setDeleted(true); // Soft delete
            bookRepo.save(book);
            params.addFlashAttribute("success", "Đã ẩn sách thành công!");
        }
        return "redirect:/admin/books";
    }

    // ================= QUẢN LÝ KHO (INVENTORY) =================
    @GetMapping("/inventory")
    public String inventory(Model model) {
        List<Book> lowStockBooks = bookRepo.findAll().stream()
                .filter(b -> b.getQuantity() < 10)
                .collect(Collectors.toList());
        model.addAttribute("lowStockBooks", lowStockBooks);
        model.addAttribute("books", bookRepo.findAll());
        model.addAttribute("logs", inventoryRepo.findAllByOrderByLogDateDesc());
        return "admin/inventory";
    }

    @PostMapping("/inventory/import")
    public String importStock(@RequestParam("bookId") Long bookId,
                              @RequestParam("quantity") Integer quantity,
                              @RequestParam("note") String note,
                              RedirectAttributes params) {
        if (quantity <= 0) {
            params.addFlashAttribute("error", "Số lượng nhập phải lớn hơn 0");
            return "redirect:/admin/inventory";
        }
        Book book = bookRepo.findById(bookId).orElse(null);
        if (book != null) {
            book.setQuantity(book.getQuantity() + quantity);
            bookRepo.save(book);
            inventoryRepo.save(new InventoryLog(book, quantity, "IMPORT", note));
            params.addFlashAttribute("success", "Đã nhập thêm " + quantity + " cuốn: " + book.getTitle());
        }
        return "redirect:/admin/inventory";
    }

    // ================= QUẢN LÝ ĐƠN HÀNG (ORDER) =================
    @GetMapping("/orders")
    public String listOrders(Model model) {
        model.addAttribute("orders", orderRepo.findAllByOrderByOrderDateDesc());
        return "admin/orders";
    }

    @GetMapping("/orders/detail/{id}")
    public String detailOrder(@PathVariable("id") Integer id, Model model) {
    	Order order = orderRepo.findById(id).orElse(null);
        model.addAttribute("order", order);
        return "admin/order-detail";
    }

    
@PostMapping("/orders/update/{id}")
public String updateOrderStatus(@PathVariable Integer id,
                                @RequestParam String status,
                                RedirectAttributes ra) {

    Order order = orderRepo.findById(id).orElse(null);
    if (order == null) {
        ra.addFlashAttribute("error", "Không tìm thấy đơn hàng");
        return "redirect:/admin/orders";
    }

    order.setStatus(status);
    orderRepo.save(order);

    ra.addFlashAttribute("success", "Cập nhật trạng thái thành công");
    return "redirect:/admin/orders/detail/" + id;
}



    // ================= QUẢN LÝ KHÁCH HÀNG (CUSTOMERS) =================
    @GetMapping("/customers")
    public String listCustomers(Model model) {
        // Sử dụng Stream API để lọc danh sách khách hàng có Role là USER
        List<User> customers = userRepo.findAll().stream()
                .filter(u -> u.getRole() == UserRole.USER)
                .collect(Collectors.toList());
        
        model.addAttribute("customers", customers);
        return "admin/customers";
    }
    @GetMapping("/customers/toggle/{username}")
    public String toggleCustomer(@PathVariable("username") String username, RedirectAttributes params) {
        if(!isAdmin()) return "redirect:/admin/customers"; // Chỉ Admin được khóa
        
        User user = userRepo.findById(username).orElse(null);
        if (user != null) {
            user.setActive(!user.getActive());
            userRepo.save(user);
            params.addFlashAttribute("success", "Đã đổi trạng thái tài khoản [" + username + "]");
        }
        return "redirect:/admin/customers";
    }

    @GetMapping("/customers/history/{username}")
    public String customerHistory(@PathVariable("username") String username, Model model) {
        User user = userRepo.findById(username).orElse(null);
        model.addAttribute("customer", user);
        return "admin/customer-history";
    }

    // ================= QUẢN LÝ NHÂN SỰ (USERS) - CHỈ ADMIN =================
    @GetMapping("/users")
    public String listUsers(Model model, RedirectAttributes params) {
        if (!isAdmin()) {
            params.addFlashAttribute("error", "Truy cập bị từ chối! Chỉ Admin được quản lý nhân sự.");
            return "redirect:/admin";
        }
        model.addAttribute("users", userRepo.findAll());
        return "admin/users";
    }

    @GetMapping("/users/create")
    public String formUser(Model model) {
        if (!isAdmin()) return "redirect:/admin";
        model.addAttribute("user", new User());
        return "admin/user-form";
    }

    @GetMapping("/users/edit/{username}")
    public String editUser(@PathVariable("username") String username, Model model) {
        if (!isAdmin()) return "redirect:/admin";
        User user = userRepo.findById(username).orElse(null);
        model.addAttribute("user", user);
        return "admin/user-form";
    }

    @PostMapping("/users/save")
    public String saveUser(@Valid @ModelAttribute("user") User user, BindingResult result) {
        if (!isAdmin()) return "redirect:/admin";
        if (result.hasErrors()) return "admin/user-form";
        userRepo.save(user);
        return "redirect:/admin/users";
    }

    @GetMapping("/users/delete/{username}")
    public String deleteUser(@PathVariable("username") String username, RedirectAttributes params) {
        if (!isAdmin()) {
            params.addFlashAttribute("error", "Truy cập bị từ chối!");
            return "redirect:/admin";
        }
        User currentUser = (User) session.getAttribute("currentUser");
        if (currentUser.getUsername().equals(username)) {
            params.addFlashAttribute("error", "Không thể tự xóa tài khoản đang đăng nhập!");
            return "redirect:/admin/users";
        }
        userRepo.deleteById(username);
        params.addFlashAttribute("success", "Đã xóa nhân viên: " + username);
        return "redirect:/admin/users";
    }
    
    
}