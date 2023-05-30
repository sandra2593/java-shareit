CREATE TABLE IF NOT EXISTS users
(
    id    BIGINT GENERATED BY DEFAULT AS IDENTITY,
    name  VARCHAR(255) NOT NULL,
    email VARCHAR(512) NOT NULL,
    CONSTRAINT pk_user_id PRIMARY KEY (id),
    CONSTRAINT uq_user_email UNIQUE (email)
    );

CREATE TABLE IF NOT EXISTS items
(
    id          BIGINT GENERATED BY DEFAULT AS IDENTITY,
    name        VARCHAR(255)  NOT NULL,
    description VARCHAR(1000) NOT NULL,
    is_available   bool          NOT NULL,
    owner_id     BIGINT        NOT NULL,
    request_id   BIGINT,
    CONSTRAINT pk_item_id PRIMARY KEY (id),
    CONSTRAINT fk_item_owner FOREIGN KEY (owner_id) REFERENCES users (id)
    );

CREATE TABLE IF NOT EXISTS bookings
(
    id       BIGINT GENERATED BY DEFAULT AS IDENTITY,
    start_date TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    end_date   TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    item_id     BIGINT                      NOT NULL,
    booker_id   BIGINT,
    status   VARCHAR(20),
    CONSTRAINT pk_booking_id PRIMARY KEY (id),
    CONSTRAINT fk_booking_item FOREIGN KEY (item_id) REFERENCES items (id),
    CONSTRAINT fk_booking_booker FOREIGN KEY (booker_id) REFERENCES users (id)
    );

CREATE TABLE IF NOT EXISTS comments
(
    id      BIGINT GENERATED BY DEFAULT AS IDENTITY,
    text    VARCHAR(1000) NOT NULL,
    item_id    BIGINT        NOT NULL,
    author_id  BIGINT        NOT NULL,
    created TIMESTAMP WITHOUT TIME ZONE DEFAULT NOW(),
    CONSTRAINT pk_comment_id PRIMARY KEY (id),
    CONSTRAINT fk_comment_item FOREIGN KEY (item_id) REFERENCES items (id),
    CONSTRAINT fk_comment_author FOREIGN KEY (author_id) REFERENCES users (id)
    );