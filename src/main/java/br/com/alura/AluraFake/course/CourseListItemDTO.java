package br.com.alura.AluraFake.course;

import java.io.Serializable;

public class CourseListItemDTO implements Serializable {

    private Long id;
    private String title;
    private String description;
    private Status status;
    private Long taskCount;

    public CourseListItemDTO(Course course) {
        this.id = course.getId();
        this.title = course.getTitle();
        this.description = course.getDescription();
        this.status = course.getStatus();
        this.taskCount = (long) course.getTasks().size();
    }

    public CourseListItemDTO(CourseProjection projection) {
        this.id = projection.getId();
        this.title = projection.getTitle();
        this.description = projection.getDescription();
        this.status = projection.getStatus();
        this.taskCount = projection.getTaskCount();
    }

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public Status getStatus() {
        return status;
    }

    public Long getTaskCount() {
        return taskCount;
    }
}
