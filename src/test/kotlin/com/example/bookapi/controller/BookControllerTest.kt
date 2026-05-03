package com.example.bookapi.controller

import com.example.bookapi.enums.PublishStatus
import com.example.bookapi.exception.BusinessException
import com.example.bookapi.model.response.AuthorSummary
import com.example.bookapi.model.response.BookResponse
import com.example.bookapi.service.BookService
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

@WebMvcTest(BookController::class)
class BookControllerTest {
    @Autowired
    lateinit var mockMvc: MockMvc

    @MockitoBean
    lateinit var bookService: BookService

    private val validRequest =
        """
        {
          "title": "Kotlin 入門",
          "price": 3000,
          "publishStatus": "UNPUBLISHED",
          "authorIds": [1, 2]
        }
        """.trimIndent()

    private val successResponse =
        BookResponse(
            1L,
            "Kotlin 入門",
            3000,
            PublishStatus.UNPUBLISHED,
            listOf(AuthorSummary(1L, "山田 太郎"), AuthorSummary(2L, "鈴木 花子")),
        )

    // --- POST /books ---

    @Test
    fun test_createBook_success() {
        given(bookService.create(any())).willReturn(successResponse)

        mockMvc
            .perform(
                post("/books")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(validRequest),
            ).andExpect(status().isCreated)
            .andExpect(jsonPath("$.id").value(1))
            .andExpect(jsonPath("$.title").value("Kotlin 入門"))
            .andExpect(jsonPath("$.price").value(3000))
            .andExpect(jsonPath("$.publishStatus").value("UNPUBLISHED"))
            .andExpect(jsonPath("$.authors[0].id").value(1))
            .andExpect(jsonPath("$.authors[0].name").value("山田 太郎"))
            .andExpect(jsonPath("$.authors[1].id").value(2))
            .andExpect(jsonPath("$.authors[1].name").value("鈴木 花子"))
    }

    @Test
    fun test_createBook_priceZero_success() {
        val response = successResponse.copy(price = 0)
        given(bookService.create(any())).willReturn(response)

        mockMvc
            .perform(
                post("/books")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("""{"title": "無料本", "price": 0, "publishStatus": "UNPUBLISHED", "authorIds": [1]}"""),
            ).andExpect(status().isCreated)
    }

    @Test
    fun test_createBook_titleEmpty_returns400() {
        mockMvc
            .perform(
                post("/books")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("""{"title": "", "price": 3000, "publishStatus": "UNPUBLISHED", "authorIds": [1]}"""),
            ).andExpect(status().isBadRequest)
            .andExpect(jsonPath("$.message").exists())
    }

    @Test
    fun test_createBook_titleBlank_returns400() {
        mockMvc
            .perform(
                post("/books")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("""{"title": "   ", "price": 3000, "publishStatus": "UNPUBLISHED", "authorIds": [1]}"""),
            ).andExpect(status().isBadRequest)
            .andExpect(jsonPath("$.message").exists())
    }

    @Test
    fun test_createBook_titleMissing_returns400() {
        mockMvc
            .perform(
                post("/books")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("""{"price": 3000, "publishStatus": "UNPUBLISHED", "authorIds": [1]}"""),
            ).andExpect(status().isBadRequest)
            .andExpect(jsonPath("$.message").exists())
    }

    @Test
    fun test_createBook_priceNegative_returns400() {
        mockMvc
            .perform(
                post("/books")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("""{"title": "Kotlin 入門", "price": -1, "publishStatus": "UNPUBLISHED", "authorIds": [1]}"""),
            ).andExpect(status().isBadRequest)
            .andExpect(jsonPath("$.message").exists())
    }

    @Test
    fun test_createBook_priceMissing_returns400() {
        mockMvc
            .perform(
                post("/books")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("""{"title": "Kotlin 入門", "publishStatus": "UNPUBLISHED", "authorIds": [1]}"""),
            ).andExpect(status().isBadRequest)
            .andExpect(jsonPath("$.message").exists())
    }

    @Test
    fun test_createBook_publishStatusMissing_returns400() {
        mockMvc
            .perform(
                post("/books")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("""{"title": "Kotlin 入門", "price": 3000, "authorIds": [1]}"""),
            ).andExpect(status().isBadRequest)
            .andExpect(jsonPath("$.message").exists())
    }

    @Test
    fun test_createBook_authorIdsEmpty_returns400() {
        mockMvc
            .perform(
                post("/books")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("""{"title": "Kotlin 入門", "price": 3000, "publishStatus": "UNPUBLISHED", "authorIds": []}"""),
            ).andExpect(status().isBadRequest)
            .andExpect(jsonPath("$.message").exists())
    }

    @Test
    fun test_createBook_authorIdsMissing_returns400() {
        mockMvc
            .perform(
                post("/books")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("""{"title": "Kotlin 入門", "price": 3000, "publishStatus": "UNPUBLISHED"}"""),
            ).andExpect(status().isBadRequest)
            .andExpect(jsonPath("$.message").exists())
    }

    @Test
    fun test_createBook_publishStatusInvalid_returns400() {
        mockMvc
            .perform(
                post("/books")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("""{"title": "Kotlin 入門", "price": 3000, "publishStatus": "INVALID", "authorIds": [1]}"""),
            ).andExpect(status().isBadRequest)
            .andExpect(jsonPath("$.message").exists())
    }

    @Test
    fun test_createBook_nonExistentAuthorId_returns400() {
        given(bookService.create(any())).willThrow(BusinessException("存在しない著者 ID が含まれています"))

        mockMvc
            .perform(
                post("/books")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("""{"title": "Kotlin 入門", "price": 3000, "publishStatus": "UNPUBLISHED", "authorIds": [999]}"""),
            ).andExpect(status().isBadRequest)
            .andExpect(jsonPath("$.message").exists())
    }
}
