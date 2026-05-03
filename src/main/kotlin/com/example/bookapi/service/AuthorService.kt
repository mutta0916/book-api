package com.example.bookapi.service

import com.example.bookapi.model.request.AuthorRequest
import com.example.bookapi.model.response.AuthorResponse
import com.example.bookapi.repository.AuthorRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class AuthorService(
    private val authorRepository: AuthorRepository,
) {
    @Transactional
    fun create(request: AuthorRequest): AuthorResponse =
        authorRepository.create(
            requireNotNull(request.name) { "name: null は許可されていません" },
            requireNotNull(request.birthDate) { "birthDate: null は許可されていません" },
        )
}
