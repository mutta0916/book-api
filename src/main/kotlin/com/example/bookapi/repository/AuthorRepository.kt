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

    fun update(
        id: Long,
        name: String,
        birthDate: LocalDate,
    ): AuthorResponse? {
        val record =
            dsl
                .update(AUTHORS)
                .set(AUTHORS.NAME, name)
                .set(AUTHORS.BIRTH_DATE, birthDate)
                .where(AUTHORS.ID.eq(id))
                .returning()
                .fetchOne()
        return record?.let { AuthorResponse(it.id!!, it.name!!, it.birthDate!!) }
    }

    fun countByIds(ids: List<Long>): Int =
        dsl
            .selectCount()
            .from(AUTHORS)
            .where(AUTHORS.ID.`in`(ids))
            .fetchOne(0, Int::class.java)!!

    fun existsById(id: Long): Boolean =
        dsl.fetchExists(
            dsl.selectOne().from(AUTHORS).where(AUTHORS.ID.eq(id)),
        )
}
