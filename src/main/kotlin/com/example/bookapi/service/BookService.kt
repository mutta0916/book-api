package com.example.bookapi.service

import com.example.bookapi.enums.PublishStatus
import com.example.bookapi.exception.BusinessException
import com.example.bookapi.exception.NotFoundException
import com.example.bookapi.model.request.BookRequest
import com.example.bookapi.model.response.BookResponse
import com.example.bookapi.repository.AuthorRepository
import com.example.bookapi.repository.BookRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class BookService(
    private val bookRepository: BookRepository,
    private val authorRepository: AuthorRepository,
) {
    @Transactional
    fun create(request: BookRequest): BookResponse {
        val authorIds = requireNotNull(request.authorIds).distinct()

        if (authorRepository.countByIds(authorIds) != authorIds.size) {
            throw BusinessException("存在しない著者 ID が含まれています")
        }

        return bookRepository.create(
            requireNotNull(request.title),
            requireNotNull(request.price),
            requireNotNull(request.publishStatus),
            authorIds,
        )
    }

    @Transactional
    fun update(
        id: Long,
        request: BookRequest,
    ): BookResponse {
        val authorIds = requireNotNull(request.authorIds).distinct()

        val book = bookRepository.findById(id) ?: throw NotFoundException("Book not found: id=$id")

        if (book.publishStatus == PublishStatus.PUBLISHED && request.publishStatus == PublishStatus.UNPUBLISHED) {
            throw BusinessException("出版済みの書籍を未出版に変更することはできません")
        }

        if (authorRepository.countByIds(authorIds) != authorIds.size) {
            throw BusinessException("存在しない著者 ID が含まれています")
        }

        return bookRepository.update(
            id,
            requireNotNull(request.title),
            requireNotNull(request.price),
            requireNotNull(request.publishStatus),
            authorIds,
        )
    }
}
