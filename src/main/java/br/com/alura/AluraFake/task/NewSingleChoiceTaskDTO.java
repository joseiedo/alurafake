package br.com.alura.AluraFake.task;

import br.com.alura.AluraFake.course.Course;
import br.com.alura.AluraFake.course.CourseRepository;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

public record NewSingleChoiceTaskDTO(
        @NotNull
        Long courseId,
        @Size(min = 4, max = 255)
        String statement,

        @NotNull
        @Min(1)
        Integer order,

        @NotNull
        @Size(min = 2, max = 5)
        List<TaskOptionDTO> options
) {

    public SingleChoiceTask toModel(
            CourseRepository courseRepository
    ) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new IllegalArgumentException("Course not found"));

        List<TaskOption> taskOptions = options.stream()
                .map(TaskOptionDTO::toModel)
                .toList();

        return new SingleChoiceTask(course, statement, order, taskOptions);
    }

    public record TaskOptionDTO(
            @NotNull
            @Size(min = 4, max = 80)
            String option,

            @NotNull
            Boolean isCorrect
    ) {
        public TaskOption toModel() {
            return new TaskOption(option, isCorrect);
        }
    }

}