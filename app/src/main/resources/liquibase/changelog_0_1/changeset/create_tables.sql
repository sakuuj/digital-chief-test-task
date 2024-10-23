CREATE TABLE PRODUCTS
(
    product_id     UUID PRIMARY KEY,

    title         VARCHAR(200) NOT NULL,
    description   VARCHAR(500) NOT NULL,
    type          VARCHAR(100) NOT NULL,
    brand         VARCHAR(50) NOT NULL,

    created_at    TIMESTAMP          NOT NULL,
    updated_at    TIMESTAMP          NOT NULL,

    version       SMALLINT           NOT NULL
);

CREATE TABLE SKUS
(
    seq_number          UUID PRIMARY KEY,

    department          VARCHAR(50) NOT NULL,
    store_location      VARCHAR(50) NOT NULL,
    product_price       DECIMAL(10, 2) NOT NULL,
    product_size        SMALLINT NOT NULL,

    created_at    TIMESTAMP          NOT NULL,
    updated_at    TIMESTAMP          NOT NULL,

    version             SMALLINT NOT NULL,

    product_id          UUID NOT NULL,

    FOREIGN KEY (product_id) REFERENCES PRODUCTS(product_id)
);
