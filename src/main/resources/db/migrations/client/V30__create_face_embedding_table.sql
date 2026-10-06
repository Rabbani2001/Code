CREATE TABLE face_embeddings  (
                                  id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                  username VARCHAR(255) NOT NULL UNIQUE,
                                  embedding JSON,
                                  timestamp TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);