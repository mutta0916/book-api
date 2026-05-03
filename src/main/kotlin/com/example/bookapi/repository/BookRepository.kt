package com.example.bookapi.repository

import com.example.bookapi.enums.PublishStatus
import com.example.bookapi.model.response.AuthorSummary
import com.example.bookapi.model.response.BookResponse
import com.example.bookapi.tables.Authors.AUTHORS
import com.example.bookapi.tables.BookAuthors.BOOK_AUTHORS
import com.example.bookapi.tables.Books.BOOKS
import com.example.bookapi.tables.records.BooksRecord
import org.jooq.DSLContext
import org.springframework.stereotype.Repository

@Repository
class BookRepository(
    private val dsl: DSLContext,
) {
    fun create(
        title: String,
        price: Int,
        publishStatus: PublishStatus,
        authorIds: List<Long>,
    ): BookResponse {
        val bookRecord =
            dsl
                .insertInto(BOOKS)
                .set(BOOKS.TITLE, title)
                .set(BOOKS.PRICE, price)
                .set(BOOKS.PUBLISH_STATUS, publishStatus)
                .returning()
                .fetchOne()!!

        val bookId = bookRecord.id!!
        insertBookAuthors(bookId, authorIds)

        val authors =
            dsl
                .select(AUTHORS.ID, AUTHORS.NAME)
                .from(AUTHORS)
                .where(AUTHORS.ID.`in`(authorIds))
                .orderBy(AUTHORS.ID)
                .fetch { AuthorSummary(it[AUTHORS.ID]!!, it[AUTHORS.NAME]!!) }

        return BookResponse(bookId, bookRecord.title!!, bookRecord.price!!, bookRecord.publishStatus!!, authors)
    }

    fun findById(id: Long): BooksRecord? =
        dsl
            .selectFrom(BOOKS)
            .where(BOOKS.ID.eq(id))
            .fetchOne()

    fun findByAuthorId(authorId: Long): List<BookResponse> {
        val baAll = BOOK_AUTHORS.`as`("ba_all")

        return dsl
            .select(BOOKS.ID, BOOKS.TITLE, BOOKS.PRICE, BOOKS.PUBLISH_STATUS, AUTHORS.ID, AUTHORS.NAME)
            .from(BOOK_AUTHORS)
            .join(BOOKS)
            .on(BOOK_AUTHORS.BOOK_ID.eq(BOOKS.ID))
            .join(baAll)
            .on(BOOKS.ID.eq(baAll.BOOK_ID))
            .join(AUTHORS)
            .on(baAll.AUTHOR_ID.eq(AUTHORS.ID))
            .where(BOOK_AUTHORS.AUTHOR_ID.eq(authorId))
            .orderBy(BOOKS.ID, AUTHORS.ID)
            .fetch()
            .groupBy { it[BOOKS.ID]!! }
            .map { (bookId, rows) ->
                val first = rows.first()
                BookResponse(
                    id = bookId,
                    title = first[BOOKS.TITLE]!!,
                    price = first[BOOKS.PRICE]!!,
                    publishStatus = first[BOOKS.PUBLISH_STATUS]!!,
                    authors = rows.map { AuthorSummary(it[AUTHORS.ID]!!, it[AUTHORS.NAME]!!) },
                )
            }
    }

    fun update(
        id: Long,
        title: String,
        price: Int,
        publishStatus: PublishStatus,
        authorIds: List<Long>,
    ): BookResponse {
        dsl
            .update(BOOKS)
            .set(BOOKS.TITLE, title)
            .set(BOOKS.PRICE, price)
            .set(BOOKS.PUBLISH_STATUS, publishStatus)
            .where(BOOKS.ID.eq(id))
            .execute()

        deleteBookAuthors(id)
        insertBookAuthors(id, authorIds)

        val authors =
            dsl
                .select(AUTHORS.ID, AUTHORS.NAME)
                .from(AUTHORS)
                .where(AUTHORS.ID.`in`(authorIds))
                .orderBy(AUTHORS.ID)
                .fetch { AuthorSummary(it[AUTHORS.ID]!!, it[AUTHORS.NAME]!!) }

        return BookResponse(id, title, price, publishStatus, authors)
    }

    private fun insertBookAuthors(
        bookId: Long,
        authorIds: List<Long>,
    ) {
        authorIds.forEach { authorId ->
            dsl
                .insertInto(BOOK_AUTHORS)
                .set(BOOK_AUTHORS.BOOK_ID, bookId)
                .set(BOOK_AUTHORS.AUTHOR_ID, authorId)
                .execute()
        }
    }

    private fun deleteBookAuthors(bookId: Long) {
        dsl
            .deleteFrom(BOOK_AUTHORS)
            .where(BOOK_AUTHORS.BOOK_ID.eq(bookId))
            .execute()
    }
}
