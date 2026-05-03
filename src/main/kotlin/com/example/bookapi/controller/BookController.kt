package com.example.bookapi.controller

import com.example.bookapi.model.request.BookRequest
import com.example.bookapi.model.response.BookResponse
import com.example.bookapi.service.BookService
import org.springframework.http.HttpStatus
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/books")
class BookController(
    private val bookService: BookService,
) {
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun create(
        @RequestBody @Validated request: BookRequest,
    ): BookResponse = bookService.create(request)

    @PutMapping("/{id}")
    fun update(
        @PathVariable id: Long,
        @RequestBody @Validated request: BookRequest,
    ): BookResponse = bookService.update(id, request)
}
