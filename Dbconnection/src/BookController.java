
package com.example.demo.controller;

import java.util.HashMap;


import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.entity.BookEntity;
import com.example.demo.repository.BookRepository;
import com.example.demo.service.BookService;

@RequestMapping("api/book")
@RestController()
public class BookController {
    @Autowired
    private BookService bookservice;
    @Autowired
    private BookRepository bookRepository;

    @PostMapping("/register")
    public BookEntity register(@RequestBody BookEntity books) {

        return bookservice.addbooks(books);

    }

    @GetMapping("/getallbooks")
    public List<BookEntity> getallbooks() {

        return bookservice.gettallbooks();

    }
}
