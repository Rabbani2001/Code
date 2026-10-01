

CREATE TABLE stmaster.stclient_db_config (
 id BIGINT AUTO_INCREMENT PRIMARY KEY,
 tenant_id VARCHAR(255) UNIQUE NOT NULL,
 db_url VARCHAR(255) NOT NULL,
 db_username VARCHAR(255) NOT NULL,
 db_password VARCHAR(255) NOT NULL,
 db_driver VARCHAR(255) NOT NULL DEFAULT 'com.mysql.cj.jdbc.Driver',
 timestamp TIMESTAMP NOT NULL
);

CREATE TABLE stmaster.school_tenant (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    tenant_code VARCHAR(255) NOT NULL UNIQUE,
    tenant_name VARCHAR(255) NOT NULL,
    email VARCHAR(255),
    phone_no VARCHAR(20) NOT NULL,
    alt_phone_no VARCHAR(20),
    tenant_id VARCHAR(255) UNIQUE,
    is_active BOOLEAN,
    timestamp TIMESTAMP NOT NULL
);


