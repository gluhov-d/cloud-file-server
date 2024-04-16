CREATE TABLE users (
                       id BIGINT AUTO_INCREMENT PRIMARY KEY,
                       username VARCHAR(64) NOT NULL UNIQUE ,
                       password VARCHAR(2048) NOT NULL,
                       role VARCHAR(32) NOT NULL,
                       first_name VARCHAR(64) NOT NULL,
                       last_name VARCHAR(64) NOT NULL,
                       enabled BOOLEAN  NOT NULL DEFAULT FALSE,
                       created_at TIMESTAMP,
                       updated_at TIMESTAMP,
                       created_by BIGINT NOT NULL,
                       modified_by BIGINT NOT NULL,
                       status varchar(50) NOT NULL
);

CREATE TABLE files (
                       id BIGINT AUTO_INCREMENT PRIMARY KEY,
                       name VARCHAR(255) NOT NULL,
                       file_path VARCHAR(255) NOT NULL,
                       status varchar(50) NOT NULL,
                       created_at TIMESTAMP,
                       updated_at TIMESTAMP,
                       created_by BIGINT NOT NULL,
                       modified_by BIGINT NOT NULL
);

CREATE TABLE events (
                        id BIGINT AUTO_INCREMENT PRIMARY KEY,
                        user_id BIGINT NOT NULL,
                        file_id BIGINT NOT NULL,
                        status varchar(50) NOT NULL,
                        created_at TIMESTAMP,
                        updated_at TIMESTAMP,
                        created_by BIGINT NOT NULL,
                        modified_by BIGINT NOT NULL,
                        FOREIGN KEY (file_id) REFERENCES files(id),
                        FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);