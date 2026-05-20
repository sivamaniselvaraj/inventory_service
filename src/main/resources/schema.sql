-- ------------------------------------------------------------
-- 1. VENDOR TABLE
-- ------------------------------------------------------------
CREATE TABLE IF NOT EXISTS vendor (
    id                  BIGINT          NOT NULL AUTO_INCREMENT,
    vendor_code         VARCHAR(50)     NOT NULL,
    name                VARCHAR(200)    NOT NULL,
    email               VARCHAR(150)    NOT NULL,
    phone               VARCHAR(20),
    address_line1       VARCHAR(255),
    address_line2       VARCHAR(255),
    city                VARCHAR(100),
    state               VARCHAR(100),
    country             VARCHAR(100),
    postal_code         VARCHAR(20),
    gstin               VARCHAR(20),
    pan                 VARCHAR(20),
    contact_person      VARCHAR(150),
    contract_start_date DATE,
    contract_end_date   DATE,
    active              BOOLEAN         NOT NULL DEFAULT TRUE,
    deleted             BOOLEAN         NOT NULL DEFAULT FALSE,
    created_at          DATETIME        NOT NULL,
    updated_at          DATETIME,
    created_by          VARCHAR(100),
    updated_by          VARCHAR(100),

    PRIMARY KEY (id),
    UNIQUE KEY uq_vendor_code (vendor_code),
    INDEX idx_vendor_active_deleted (active, deleted),
    INDEX idx_vendor_name (name)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ------------------------------------------------------------
-- 2. CATEGORY TABLE
-- ------------------------------------------------------------
CREATE TABLE IF NOT EXISTS category (
    id          BIGINT          NOT NULL AUTO_INCREMENT,
    code        VARCHAR(50)     NOT NULL,
    name        VARCHAR(200)    NOT NULL,
    description TEXT,
    sort_order   BIGINT,
    parent_id   BIGINT,
    active      BOOLEAN         NOT NULL DEFAULT TRUE,
    deleted     BOOLEAN         NOT NULL DEFAULT FALSE,
    created_at  DATETIME        NOT NULL,
    updated_at  DATETIME,

    PRIMARY KEY (id),
    UNIQUE KEY uq_category_code (code),
    CONSTRAINT fk_category_parent
    FOREIGN KEY (parent_id) REFERENCES category (id)
    ON DELETE RESTRICT ON UPDATE CASCADE
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ------------------------------------------------------------
-- 3. PRODUCT TABLE
-- ------------------------------------------------------------
CREATE TABLE IF NOT EXISTS product (
                                       id                BIGINT          NOT NULL AUTO_INCREMENT,
                                       product_hsc_code  VARCHAR(50)     NOT NULL,
    name              VARCHAR(300)    NOT NULL,
    description       TEXT,
    unit_price        DECIMAL(15, 2)  NOT NULL,
    selling_price     DECIMAL(15, 2)  NOT NULL,
    units_available   INT             NOT NULL DEFAULT 0,
    unit_of_measure   VARCHAR(50),
    manufactured_by   VARCHAR(300)    NOT NULL,
    manufactured_on   DATE,
    expiry_date       DATE,
    shelf_life_days   INT,
    batch_number      VARCHAR(100),
    reorder_level     INT             DEFAULT 0,
    max_stock_level   INT,
    category_id       BIGINT          NOT NULL,
    vendor_id         BIGINT          NOT NULL,
    active            BOOLEAN         NOT NULL DEFAULT TRUE,
    deleted           BOOLEAN         NOT NULL DEFAULT FALSE,
    created_at        DATETIME        NOT NULL,
    updated_at        DATETIME,
    created_by        VARCHAR(100),
    updated_by        VARCHAR(100),

    PRIMARY KEY (id),
    UNIQUE KEY uq_product_hsc_code (product_hsc_code),
    INDEX idx_product_category (category_id),
    INDEX idx_product_vendor (vendor_id),
    INDEX idx_product_active_deleted (active, deleted),
    INDEX idx_product_expiry (expiry_date),
    CONSTRAINT fk_product_category
    FOREIGN KEY (category_id) REFERENCES category (id)
    ON DELETE RESTRICT ON UPDATE CASCADE,
    CONSTRAINT fk_product_vendor
    FOREIGN KEY (vendor_id) REFERENCES vendor (id)
    ON DELETE RESTRICT ON UPDATE CASCADE
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;