CREATE TABLE IF NOT EXISTS tb_user (
    id       BIGINT AUTO_INCREMENT NOT NULL,
    uid      VARCHAR(255) NOT NULL,
    password VARCHAR(255) NOT NULL,
    encode   BIT(1) DEFAULT 0 NULL,
    CONSTRAINT pk_tb_user PRIMARY KEY (id)
);

ALTER TABLE tb_user
    ADD CONSTRAINT uc_tb_user_uid UNIQUE (uid);