package br.com.alura.AluraFake.task;

import br.com.alura.AluraFake.course.Course;
import br.com.alura.AluraFake.course.CourseRepository;
import br.com.alura.AluraFake.task.domain.OpenTextTask;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record NewOpenTextTaskDTO(
        @NotNull
        Long courseId,
        @Size(min = 4, max = 255)
        String statement,

        @NotNull
        @Min(1)
        Integer order
) {

    public OpenTextTask toModel(
            CourseRepository courseRepository
    ) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new IllegalArgumentException("Course not found"));
        return new OpenTextTask(course, statement, order);
    }

}
