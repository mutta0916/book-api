package com.example.bookapi.service

import com.example.bookapi.enums.PublishStatus
import com.example.bookapi.exception.NotFoundException
import com.example.bookapi.model.request.AuthorRequest
import com.example.bookapi.model.response.AuthorResponse
import com.example.bookapi.model.response.AuthorSummary
import com.example.bookapi.model.response.BookResponse
import com.example.bookapi.repository.AuthorRepository
import com.example.bookapi.repository.BookRepository
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.BDDMockito.given
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import java.time.LocalDate

@ExtendWith(MockitoExtension::class)
class AuthorServiceTest {
    @Mock
    lateinit var authorRepository: AuthorRepository

    @Mock
    lateinit var bookRepository: BookRepository

    @InjectMocks
    lateinit var authorService: AuthorService

    // --- create ---

    @Test
    fun test_create_success() {
        val request = AuthorRequest("山田 太郎", LocalDate.of(1980, 1, 15))
        val expected = AuthorResponse(1L, "山田 太郎", LocalDate.of(1980, 1, 15))
        given(authorRepository.create("山田 太郎", LocalDate.of(1980, 1, 15))).willReturn(expected)

        val result = authorService.create(request)

        assertEquals(expected, result)
    }

    // --- update ---

    @Test
    fun test_update_success() {
        val request = AuthorRequest("山田 次郎", LocalDate.of(1990, 6, 1))
        val expected = AuthorResponse(1L, "山田 次郎", LocalDate.of(1990, 6, 1))
        given(authorRepository.update(1L, "山田 次郎", LocalDate.of(1990, 6, 1))).willReturn(expected)

        val result = authorService.update(1L, request)

        assertEquals(expected, result)
    }

    @Test
    fun test_update_authorNotFound_throwsNotFoundException() {
        val request = AuthorRequest("山田 次郎", LocalDate.of(1990, 6, 1))
        given(authorRepository.update(999L, "山田 次郎", LocalDate.of(1990, 6, 1))).willReturn(null)

        val exception =
            assertThrows(NotFoundException::class.java) {
                authorService.update(999L, request)
            }
        assertEquals("Author not found: id=999", exception.message)
    }

    // --- findBooks ---

    @Test
    fun test_findBooks_success() {
        val books =
            listOf(
                BookResponse(
                    1L,
                    "Kotlin 入門",
                    3000,
                    PublishStatus.PUBLISHED,
                    listOf(AuthorSummary(1L, "山田 太郎"), AuthorSummary(2L, "鈴木 花子")),
                ),
            )
        given(authorRepository.existsById(1L)).willReturn(true)
        given(bookRepository.findByAuthorId(1L)).willReturn(books)

        val result = authorService.findBooks(1L)

        assertEquals(books, result)
    }

    @Test
    fun test_findBooks_emptyList_success() {
        given(authorRepository.existsById(1L)).willReturn(true)
        given(bookRepository.findByAuthorId(1L)).willReturn(emptyList())

        val result = authorService.findBooks(1L)

        assertEquals(emptyList<BookResponse>(), result)
    }

    @Test
    fun test_findBooks_authorNotFound_throwsNotFoundException() {
        given(authorRepository.existsById(999L)).willReturn(false)

        val exception =
            assertThrows(NotFoundException::class.java) {
                authorService.findBooks(999L)
            }
        assertEquals("Author not found: id=999", exception.message)
    }
}
