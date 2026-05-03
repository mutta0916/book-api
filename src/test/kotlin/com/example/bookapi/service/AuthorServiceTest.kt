package com.example.bookapi.service

import com.example.bookapi.model.request.AuthorRequest
import com.example.bookapi.model.response.AuthorResponse
import com.example.bookapi.repository.AuthorRepository
import org.junit.jupiter.api.Assertions.assertEquals
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
}
