package com.example.bookapi.model.response

import com.example.bookapi.enums.PublishStatus

data class BookResponse(
    val id: Long,
    val title: String,
    val price: Int,
    val publishStatus: PublishStatus,
    val authors: List<AuthorSummary>,
)
