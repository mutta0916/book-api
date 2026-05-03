package com.example.bookapi.model.request

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.PastOrPresent
import java.time.LocalDate

data class AuthorRequest(
    @field:NotBlank
    val name: String?,
    @field:NotNull
    @field:PastOrPresent
    val birthDate: LocalDate?,
)
