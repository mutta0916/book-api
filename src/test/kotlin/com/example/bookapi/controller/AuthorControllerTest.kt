package com.example.bookapi.controller

import com.example.bookapi.enums.PublishStatus
import com.example.bookapi.exception.NotFoundException
import com.example.bookapi.model.response.AuthorResponse
import com.example.bookapi.model.response.AuthorSummary
import com.example.bookapi.model.response.BookResponse
import com.example.bookapi.service.AuthorService
import org.junit.jupiter.api.Test
import org.mockito.BDDMockito.given
import org.mockito.kotlin.any
import org.mockito.kotlin.eq
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest
import org.springframework.http.MediaType
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put
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

    // --- PUT /authors/{id} ---

    @Test
    fun test_updateAuthor_success() {
        val response = AuthorResponse(1L, "山田 太郎", LocalDate.of(1980, 1, 15))
        given(authorService.update(eq(1L), any())).willReturn(response)

        mockMvc
            .perform(
                put("/authors/1")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("""{"name": "山田 太郎", "birthDate": "1980-01-15"}"""),
            ).andExpect(status().isOk)
            .andExpect(jsonPath("$.id").value(1))
            .andExpect(jsonPath("$.name").value("山田 太郎"))
            .andExpect(jsonPath("$.birthDate").value("1980-01-15"))
    }

    @Test
    fun test_updateAuthor_notFound_returns404() {
        given(authorService.update(eq(999L), any())).willThrow(NotFoundException("Author not found: id=999"))

        mockMvc
            .perform(
                put("/authors/999")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("""{"name": "山田 太郎", "birthDate": "1980-01-15"}"""),
            ).andExpect(status().isNotFound)
            .andExpect(jsonPath("$.message").exists())
    }

    @Test
    fun test_updateAuthor_nameEmpty_returns400() {
        mockMvc
            .perform(
                put("/authors/1")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("""{"name": "", "birthDate": "1980-01-15"}"""),
            ).andExpect(status().isBadRequest)
            .andExpect(jsonPath("$.message").exists())
    }

    @Test
    fun test_updateAuthor_nameBlank_returns400() {
        mockMvc
            .perform(
                put("/authors/1")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("""{"name": "   ", "birthDate": "1980-01-15"}"""),
            ).andExpect(status().isBadRequest)
            .andExpect(jsonPath("$.message").exists())
    }

    @Test
    fun test_updateAuthor_nameMissing_returns400() {
        mockMvc
            .perform(
                put("/authors/1")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("""{"birthDate": "1980-01-15"}"""),
            ).andExpect(status().isBadRequest)
            .andExpect(jsonPath("$.message").exists())
    }

    @Test
    fun test_updateAuthor_birthDateFuture_returns400() {
        mockMvc
            .perform(
                put("/authors/1")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("""{"name": "山田 太郎", "birthDate": "9999-12-31"}"""),
            ).andExpect(status().isBadRequest)
            .andExpect(jsonPath("$.message").exists())
    }

    @Test
    fun test_updateAuthor_birthDateMissing_returns400() {
        mockMvc
            .perform(
                put("/authors/1")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("""{"name": "山田 太郎"}"""),
            ).andExpect(status().isBadRequest)
            .andExpect(jsonPath("$.message").exists())
    }

    // --- GET /authors/{id}/books ---

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
        given(authorService.findBooks(eq(1L))).willReturn(books)

        mockMvc
            .perform(get("/authors/1/books"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$[0].id").value(1))
            .andExpect(jsonPath("$[0].title").value("Kotlin 入門"))
            .andExpect(jsonPath("$[0].price").value(3000))
            .andExpect(jsonPath("$[0].publishStatus").value("PUBLISHED"))
            .andExpect(jsonPath("$[0].authors[0].id").value(1))
            .andExpect(jsonPath("$[0].authors[0].name").value("山田 太郎"))
            .andExpect(jsonPath("$[0].authors[1].id").value(2))
            .andExpect(jsonPath("$[0].authors[1].name").value("鈴木 花子"))
    }

    @Test
    fun test_findBooks_emptyList_returns200() {
        given(authorService.findBooks(eq(1L))).willReturn(emptyList())

        mockMvc
            .perform(get("/authors/1/books"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$").isArray)
            .andExpect(jsonPath("$").isEmpty)
    }

    @Test
    fun test_findBooks_authorNotFound_returns404() {
        given(authorService.findBooks(eq(999L))).willThrow(NotFoundException("Author not found: id=999"))

        mockMvc
            .perform(get("/authors/999/books"))
            .andExpect(status().isNotFound)
            .andExpect(jsonPath("$.message").exists())
    }
}
