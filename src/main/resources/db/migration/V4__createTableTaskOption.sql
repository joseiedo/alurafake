CREATE TABLE TaskOption (
    id bigint(20) NOT NULL AUTO_INCREMENT,
    option_text varchar(80) NOT NULL,
    is_correct boolean NOT NULL,
    task_id bigint(20) NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT FK_Task FOREIGN KEY (task_id) REFERENCES Task(id) ON DELETE CASCADE,
    CONSTRAINT UQ_Task_Option UNIQUE (task_id, option_text),
    CONSTRAINT CHK_Option_Length CHECK (CHAR_LENGTH(option_text) >= 4 AND CHAR_LENGTH(option_text) <= 80),
    INDEX IDX_task_id (task_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci ROW_FORMAT=DYNAMIC;