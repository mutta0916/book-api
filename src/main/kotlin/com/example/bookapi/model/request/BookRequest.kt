package com.example.bookapi.model.request

import com.example.bookapi.enums.PublishStatus
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotEmpty
import jakarta.validation.constraints.NotNull

data class BookRequest(
    @field:NotBlank
    val title: String?,
    @field:NotNull
    @field:Min(0)
    val price: Int?,
    @field:NotNull
    val publishStatus: PublishStatus?,
    @field:NotEmpty
    val authorIds: List<Long>?,
)
