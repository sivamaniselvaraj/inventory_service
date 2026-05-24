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
    active            BOOLEAN         NOT NULL DEFAULT TRUE,
    deleted           BOOLEAN         NOT NULL DEFAULT FALSE,
    created_at        DATETIME        NOT NULL,
    updated_at        DATETIME,
    created_by        VARCHAR(100),
    updated_by        VARCHAR(100),

    PRIMARY KEY (id),
    UNIQUE KEY uq_product_hsc_code (product_hsc_code),
    INDEX idx_product_category (category_id),
    INDEX idx_product_active_deleted (active, deleted),
    INDEX idx_product_expiry (expiry_date),
    CONSTRAINT fk_product_category
    FOREIGN KEY (category_id) REFERENCES category (id)
    ON DELETE RESTRICT ON UPDATE CASCADE,
    CONSTRAINT fk_product_vendor
    ON DELETE RESTRICT ON UPDATE CASCADE
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ================================================================
-- PRODUCT-VENDOR ASSOCIATION — Complete SQL Script
-- Database : MySQL 8.0+
-- Table    : product_vendor
-- Purpose  : Manages the many-to-many relationship between products
--            and vendors, carrying rich supply-chain metadata per
--            association (supply price, lead time, MOQ, preferred
--            flag, contract reference, validity window).
-- ================================================================


CREATE TABLE IF NOT EXISTS product_vendor (

    -- Primary key
                                              id                BIGINT          NOT NULL AUTO_INCREMENT,

    -- Foreign keys
                                              product_id        BIGINT          NOT NULL COMMENT 'FK → product.id',
                                              vendor_id         BIGINT          NOT NULL COMMENT 'FK → vendor.id',

    -- Supply-chain metadata
                                              supply_price      DECIMAL(15, 2)           COMMENT 'Price the vendor charges us per unit',
    lead_time_days    INT                       COMMENT 'Typical days from PO to delivery',
    minimum_order_qty INT             NOT NULL DEFAULT 1
    COMMENT 'Minimum units vendor requires per order',
    preferred         BOOLEAN         NOT NULL DEFAULT FALSE
    COMMENT 'TRUE = primary supplier for this product',
    contract_ref      VARCHAR(100)             COMMENT 'PO or contract reference number',
    valid_from        DATE                     COMMENT 'First day this arrangement is active',
    valid_until       DATE                     COMMENT 'Last day (NULL = open-ended)',
    notes             TEXT                     COMMENT 'Free-text remarks on this arrangement',

    -- Soft-delete lifecycle
    active            BOOLEAN         NOT NULL DEFAULT TRUE,
    deleted           BOOLEAN         NOT NULL DEFAULT FALSE,

    -- Audit
    created_at        DATETIME        NOT NULL DEFAULT NOW(),
    updated_at        DATETIME                 DEFAULT NULL ON UPDATE NOW(),
    created_by        VARCHAR(100),
    updated_by        VARCHAR(100),

    -- Constraints
    PRIMARY KEY (id),

    UNIQUE KEY uq_product_vendor (product_id, vendor_id)
    COMMENT 'One row per (product, vendor) pair',

    CONSTRAINT fk_pv_product
    FOREIGN KEY (product_id) REFERENCES product (id)
                                                            ON DELETE RESTRICT ON UPDATE CASCADE,

    CONSTRAINT fk_pv_vendor
    FOREIGN KEY (vendor_id) REFERENCES vendor (id)
                                                            ON DELETE RESTRICT ON UPDATE CASCADE

    ) ENGINE = InnoDB
    COMMENT = 'Many-to-many association between products and vendors with supply-chain metadata';

-- ----------------------------------------------------------------
-- 2. INDEXES
-- ----------------------------------------------------------------

-- Queries by product_id (most frequent — list all vendors for a product)
CREATE INDEX idx_pv_product
    ON product_vendor (product_id);

-- Queries by vendor_id (list all products for a vendor)
CREATE INDEX idx_pv_vendor
    ON product_vendor (vendor_id);

-- Fast preferred-vendor lookup per product
CREATE INDEX idx_pv_preferred
    ON product_vendor (product_id, preferred);

-- Validity-window queries (expiry reporting)
CREATE INDEX idx_pv_valid_until
    ON product_vendor (valid_until);

-- Active associations only (soft-delete filter)
CREATE INDEX idx_pv_active_deleted
    ON product_vendor (active, deleted);


-- ----------------------------------------------------------------
-- DDL — CREATE vendor_contact_person TABLE
-- ----------------------------------------------------------------

CREATE TABLE IF NOT EXISTS vendor_contact_person (

     id               BIGINT        NOT NULL AUTO_INCREMENT,
     vendor_id        BIGINT        NOT NULL     COMMENT 'FK → vendor.id',
     full_name        VARCHAR(200)  NOT NULL     COMMENT 'Contact full name',
    role             VARCHAR(30)   NOT NULL     COMMENT 'ContactRole enum value',
    email            VARCHAR(150)               COMMENT 'Direct email address',
    phone            VARCHAR(20)                COMMENT 'Primary phone / mobile',
    alternate_phone  VARCHAR(20)                COMMENT 'Secondary phone number',
    designation      VARCHAR(150)               COMMENT 'Job title',
    department       VARCHAR(100)               COMMENT 'Department within vendor org',
    preferred        BOOLEAN       NOT NULL DEFAULT FALSE
    COMMENT 'TRUE = default contact when role unspecified',
    notes            TEXT                       COMMENT 'Free-text remarks',

    -- Soft-delete lifecycle
    active           BOOLEAN       NOT NULL DEFAULT TRUE,
    deleted          BOOLEAN       NOT NULL DEFAULT FALSE,

    -- Audit
    created_at       DATETIME      NOT NULL DEFAULT NOW(),
    updated_at       DATETIME               DEFAULT NULL ON UPDATE NOW(),
    created_by       VARCHAR(100),
    updated_by       VARCHAR(100),

    PRIMARY KEY (id),

    CONSTRAINT fk_vcp_vendor
    FOREIGN KEY (vendor_id) REFERENCES vendor (id)
                                                         ON DELETE RESTRICT ON UPDATE CASCADE,

    CONSTRAINT chk_vcp_role
    CHECK (role IN ('PRIMARY','SALES','PROCUREMENT','ACCOUNTS',
           'LOGISTICS','TECHNICAL','ESCALATION','EMERGENCY'))

    ) ENGINE = InnoDB
    DEFAULT CHARSET = utf8mb4
    COLLATE = utf8mb4_unicode_ci
    COMMENT = 'Multiple contact persons per vendor with role-based classification';

-- ----------------------------------------------------------------
-- INDEXES
-- ----------------------------------------------------------------
CREATE INDEX idx_vcp_vendor
    ON vendor_contact_person (vendor_id);

CREATE INDEX idx_vcp_role
    ON vendor_contact_person (vendor_id, role);

CREATE INDEX idx_vcp_primary
    ON vendor_contact_person (vendor_id, preferred);

CREATE INDEX idx_vcp_deleted
    ON vendor_contact_person (vendor_id, deleted);