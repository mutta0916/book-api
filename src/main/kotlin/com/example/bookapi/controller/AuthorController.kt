package com.example.bookapi.controller

import com.example.bookapi.model.request.AuthorRequest
import com.example.bookapi.model.response.AuthorResponse
import com.example.bookapi.service.AuthorService
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
@RequestMapping("/authors")
class AuthorController(
    private val authorService: AuthorService,
) {
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun create(
        @RequestBody @Validated request: AuthorRequest,
    ): AuthorResponse = authorService.create(request)

    @PutMapping("/{id}")
    fun update(
        @PathVariable id: Long,
        @RequestBody @Validated request: AuthorRequest,
    ): AuthorResponse = authorService.update(id, request)
}
