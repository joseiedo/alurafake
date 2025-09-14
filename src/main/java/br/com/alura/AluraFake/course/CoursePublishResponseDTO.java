package br.com.alura.AluraFake.course;

import java.time.LocalDateTime;

public record CoursePublishResponseDTO(
        Long id,
        String title,
        Status status,
        LocalDateTime publishedAt
) {
    public CoursePublishResponseDTO(Course course) {
        this(course.getId(), course.getTitle(), course.getStatus(), course.getPublishedAt());
    }
}