package com.poly.java5.Controller;


import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.poly.java5.Entity.Book;
import com.poly.java5.Service.BookService;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/search")
@RequiredArgsConstructor
public class SearchCotroller {

    private final BookService bookService;

    @GetMapping("")
    public String search(
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String author,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String isbn,
            Model model) {

        List<Book> books = bookService.searchBooks(
                title, author, category, isbn
        );

        model.addAttribute("books", books);
        model.addAttribute("keyword", title);

        return "search";
    }
}
