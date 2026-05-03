package com.example.bookapi.repository

import com.example.bookapi.model.response.AuthorResponse
import com.example.bookapi.tables.Authors.AUTHORS
import org.jooq.DSLContext
import org.springframework.stereotype.Repository
import java.time.LocalDate

@Repository
class AuthorRepository(
    private val dsl: DSLContext,
) {
    fun create(
        name: String,
        birthDate: LocalDate,
    ): AuthorResponse {
        val record =
            dsl
                .insertInto(AUTHORS)
                .set(AUTHORS.NAME, name)
                .set(AUTHORS.BIRTH_DATE, birthDate)
                .returning()
                .fetchOne()!!
        return AuthorResponse(record.id!!, record.name!!, record.birthDate!!)
    }
}
