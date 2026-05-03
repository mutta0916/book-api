CREATE TYPE publish_status AS ENUM ('UNPUBLISHED', 'PUBLISHED');

CREATE TABLE authors (
    id         BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    name       TEXT NOT NULL,
    birth_date DATE NOT NULL
);

CREATE TABLE books (
    id             BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    title          TEXT           NOT NULL,
    price          INTEGER        NOT NULL CHECK (price >= 0),
    publish_status publish_status NOT NULL
);

CREATE TABLE book_authors (
    book_id   BIGINT NOT NULL REFERENCES books(id) ON DELETE CASCADE,
    author_id BIGINT NOT NULL REFERENCES authors(id) ON DELETE RESTRICT,
    PRIMARY KEY (book_id, author_id)
);
