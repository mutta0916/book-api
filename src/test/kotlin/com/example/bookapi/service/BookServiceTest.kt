package com.example.bookapi.service

import com.example.bookapi.enums.PublishStatus
import com.example.bookapi.exception.BusinessException
import com.example.bookapi.exception.NotFoundException
import com.example.bookapi.model.request.BookRequest
import com.example.bookapi.model.response.AuthorSummary
import com.example.bookapi.model.response.BookResponse
import com.example.bookapi.repository.AuthorRepository
import com.example.bookapi.repository.BookRepository
import com.example.bookapi.tables.records.BooksRecord
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.BDDMockito.given
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension

@ExtendWith(MockitoExtension::class)
class BookServiceTest {
    @Mock
    lateinit var bookRepository: BookRepository

    @Mock
    lateinit var authorRepository: AuthorRepository

    @InjectMocks
    lateinit var bookService: BookService

    // --- create ---

    @Test
    fun test_create_success() {
        val request = BookRequest("Kotlin 入門", 3000, PublishStatus.UNPUBLISHED, listOf(1L, 2L))
        val expected =
            BookResponse(
                1L,
                "Kotlin 入門",
                3000,
                PublishStatus.UNPUBLISHED,
                listOf(AuthorSummary(1L, "山田 太郎"), AuthorSummary(2L, "鈴木 花子")),
            )
        given(authorRepository.countByIds(listOf(1L, 2L))).willReturn(2)
        given(bookRepository.create("Kotlin 入門", 3000, PublishStatus.UNPUBLISHED, listOf(1L, 2L))).willReturn(expected)

        val result = bookService.create(request)

        assertEquals(expected, result)
    }

    @Test
    fun test_create_duplicateAuthorIds_deduplicatedAndSuccess() {
        val request = BookRequest("Kotlin 入門", 3000, PublishStatus.UNPUBLISHED, listOf(1L, 1L))
        val expected =
            BookResponse(
                1L,
                "Kotlin 入門",
                3000,
                PublishStatus.UNPUBLISHED,
                listOf(AuthorSummary(1L, "山田 太郎")),
            )
        given(authorRepository.countByIds(listOf(1L))).willReturn(1)
        given(bookRepository.create("Kotlin 入門", 3000, PublishStatus.UNPUBLISHED, listOf(1L))).willReturn(expected)

        val result = bookService.create(request)

        assertEquals(expected, result)
    }

    @Test
    fun test_create_nonExistentAuthorId_throwsBusinessException() {
        val request = BookRequest("Kotlin 入門", 3000, PublishStatus.UNPUBLISHED, listOf(1L, 999L))
        given(authorRepository.countByIds(listOf(1L, 999L))).willReturn(1)

        assertThrows(BusinessException::class.java) {
            bookService.create(request)
        }
    }

    // --- update ---

    private fun booksRecord(
        id: Long,
        publishStatus: PublishStatus,
    ): BooksRecord {
        val record = BooksRecord()
        record.id = id
        record.title = "Kotlin 入門"
        record.price = 3000
        record.publishStatus = publishStatus
        return record
    }

    @Test
    fun test_update_success() {
        val request = BookRequest("Kotlin 入門 改訂版", 3500, PublishStatus.PUBLISHED, listOf(1L, 2L))
        val expected =
            BookResponse(
                1L,
                "Kotlin 入門 改訂版",
                3500,
                PublishStatus.PUBLISHED,
                listOf(AuthorSummary(1L, "山田 太郎"), AuthorSummary(2L, "鈴木 花子")),
            )
        given(bookRepository.findById(1L)).willReturn(booksRecord(1L, PublishStatus.UNPUBLISHED))
        given(authorRepository.countByIds(listOf(1L, 2L))).willReturn(2)
        given(bookRepository.update(1L, "Kotlin 入門 改訂版", 3500, PublishStatus.PUBLISHED, listOf(1L, 2L))).willReturn(expected)

        val result = bookService.update(1L, request)

        assertEquals(expected, result)
    }

    @Test
    fun test_update_bookNotFound_throwsNotFoundException() {
        val request = BookRequest("Kotlin 入門", 3000, PublishStatus.UNPUBLISHED, listOf(1L))
        given(bookRepository.findById(999L)).willReturn(null)

        assertThrows(NotFoundException::class.java) {
            bookService.update(999L, request)
        }
    }

    @Test
    fun test_update_publishedToUnpublished_throwsBusinessException() {
        val request = BookRequest("Kotlin 入門", 3000, PublishStatus.UNPUBLISHED, listOf(1L))
        given(bookRepository.findById(1L)).willReturn(booksRecord(1L, PublishStatus.PUBLISHED))

        assertThrows(BusinessException::class.java) {
            bookService.update(1L, request)
        }
    }

    @Test
    fun test_update_unpublishedToPublished_success() {
        val request = BookRequest("Kotlin 入門", 3000, PublishStatus.PUBLISHED, listOf(1L))
        val expected =
            BookResponse(
                1L,
                "Kotlin 入門",
                3000,
                PublishStatus.PUBLISHED,
                listOf(AuthorSummary(1L, "山田 太郎")),
            )
        given(bookRepository.findById(1L)).willReturn(booksRecord(1L, PublishStatus.UNPUBLISHED))
        given(authorRepository.countByIds(listOf(1L))).willReturn(1)
        given(bookRepository.update(1L, "Kotlin 入門", 3000, PublishStatus.PUBLISHED, listOf(1L))).willReturn(expected)

        val result = bookService.update(1L, request)

        assertEquals(expected, result)
    }

    @Test
    fun test_update_nonExistentAuthorId_throwsBusinessException() {
        val request = BookRequest("Kotlin 入門", 3000, PublishStatus.UNPUBLISHED, listOf(1L, 999L))
        given(bookRepository.findById(1L)).willReturn(booksRecord(1L, PublishStatus.UNPUBLISHED))
        given(authorRepository.countByIds(listOf(1L, 999L))).willReturn(1)

        assertThrows(BusinessException::class.java) {
            bookService.update(1L, request)
        }
    }
}
