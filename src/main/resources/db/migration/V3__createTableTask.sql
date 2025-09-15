CREATE TABLE Task (
    id bigint(20) NOT NULL AUTO_INCREMENT,
    statement varchar(255) NOT NULL,
    task_order int NOT NULL,
    course_id bigint(20) NOT NULL,
    type enum('OPEN_TEXT', 'MULTIPLE_CHOICE', 'SINGLE_CHOICE') CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT FK_Course FOREIGN KEY (course_id) REFERENCES Course(id) ON DELETE CASCADE,
    CONSTRAINT UQ_Course_Statement UNIQUE (course_id, statement),
    CONSTRAINT CHK_Statement_Length CHECK (CHAR_LENGTH(statement) >= 4 AND CHAR_LENGTH(statement) <= 255),
    CONSTRAINT CHK_Positive_Order CHECK (task_order > 0),
    INDEX IDX_course_id (course_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci ROW_FORMAT=DYNAMIC;