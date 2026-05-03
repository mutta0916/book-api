package com.example.bookapi.service

import com.example.bookapi.exception.NotFoundException
import com.example.bookapi.model.request.AuthorRequest
import com.example.bookapi.model.response.AuthorResponse
import com.example.bookapi.model.response.BookResponse
import com.example.bookapi.repository.AuthorRepository
import com.example.bookapi.repository.BookRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class AuthorService(
    private val authorRepository: AuthorRepository,
    private val bookRepository: BookRepository,
) {
    @Transactional
    fun create(request: AuthorRequest): AuthorResponse =
        authorRepository.create(
            requireNotNull(request.name) { "name: null は許可されていません" },
            requireNotNull(request.birthDate) { "birthDate: null は許可されていません" },
        )

    @Transactional
    fun update(
        id: Long,
        request: AuthorRequest,
    ): AuthorResponse =
        authorRepository.update(
            id,
            requireNotNull(request.name) { "name: null は許可されていません" },
            requireNotNull(request.birthDate) { "birthDate: null は許可されていません" },
        ) ?: throw NotFoundException("Author not found: id=$id")

    fun findBooks(id: Long): List<BookResponse> {
        if (!authorRepository.existsById(id)) throw NotFoundException("Author not found: id=$id")
        return bookRepository.findByAuthorId(id)
    }
}
