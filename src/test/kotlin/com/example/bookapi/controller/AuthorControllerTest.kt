package com.example.bookapi.controller

import com.example.bookapi.model.response.AuthorResponse
import com.example.bookapi.service.AuthorService
import org.junit.jupiter.api.Test
import org.mockito.BDDMockito.given
import org.mockito.kotlin.any
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest
import org.springframework.http.MediaType
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import java.time.LocalDate

@WebMvcTest(AuthorController::class)
class AuthorControllerTest {
    @Autowired
    lateinit var mockMvc: MockMvc

    @MockitoBean
    lateinit var authorService: AuthorService

    // --- POST /authors ---

    @Test
    fun test_createAuthor_success() {
        val response = AuthorResponse(1L, "山田 太郎", LocalDate.of(1980, 1, 15))
        given(authorService.create(any())).willReturn(response)

        mockMvc
            .perform(
                post("/authors")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("""{"name": "山田 太郎", "birthDate": "1980-01-15"}"""),
            ).andExpect(status().isCreated)
            .andExpect(jsonPath("$.id").value(1))
            .andExpect(jsonPath("$.name").value("山田 太郎"))
            .andExpect(jsonPath("$.birthDate").value("1980-01-15"))
    }

    @Test
    fun test_createAuthor_nameEmpty_returns400() {
        mockMvc
            .perform(
                post("/authors")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("""{"name": "", "birthDate": "1980-01-15"}"""),
            ).andExpect(status().isBadRequest)
            .andExpect(jsonPath("$.message").exists())
    }

    @Test
    fun test_createAuthor_nameBlank_returns400() {
        mockMvc
            .perform(
                post("/authors")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("""{"name": "   ", "birthDate": "1980-01-15"}"""),
            ).andExpect(status().isBadRequest)
            .andExpect(jsonPath("$.message").exists())
    }

    @Test
    fun test_createAuthor_nameMissing_returns400() {
        mockMvc
            .perform(
                post("/authors")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("""{"birthDate": "1980-01-15"}"""),
            ).andExpect(status().isBadRequest)
            .andExpect(jsonPath("$.message").exists())
    }

    @Test
    fun test_createAuthor_birthDateFuture_returns400() {
        mockMvc
            .perform(
                post("/authors")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("""{"name": "山田 太郎", "birthDate": "9999-12-31"}"""),
            ).andExpect(status().isBadRequest)
            .andExpect(jsonPath("$.message").exists())
    }

    @Test
    fun test_createAuthor_birthDateToday_success() {
        val today = LocalDate.now()
        val response = AuthorResponse(1L, "山田 太郎", today)
        given(authorService.create(any())).willReturn(response)

        mockMvc
            .perform(
                post("/authors")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("""{"name": "山田 太郎", "birthDate": "$today"}"""),
            ).andExpect(status().isCreated)
    }

    @Test
    fun test_createAuthor_birthDateMissing_returns400() {
        mockMvc
            .perform(
                post("/authors")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("""{"name": "山田 太郎"}"""),
            ).andExpect(status().isBadRequest)
            .andExpect(jsonPath("$.message").exists())
    }
}
