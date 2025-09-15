package br.com.alura.AluraFake.course;

import java.time.LocalDateTime;

public interface CourseProjection {
    Long getId();
    String getTitle();
    String getDescription();
    Status getStatus();
    LocalDateTime getPublishedAt();
    Long getTaskCount();

    default boolean isPublished() {
        return Status.PUBLISHED.equals(getStatus());
    }
}