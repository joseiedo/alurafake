package br.com.alura.AluraFake.course;

import br.com.alura.AluraFake.task.domain.Task;
import br.com.alura.AluraFake.task.domain.Type;

import java.util.List;

public record FindCourseDTO(
        Long id,
        String title,
        String description,
        Status status,
        List<TaskDTO> tasks
) {

    public static FindCourseDTO fromModel(Course course) {
        List<TaskDTO> taskDTOs = course.getTasks().stream()
                .map(TaskDTO::fromModel)
                .toList();

        return new FindCourseDTO(
                course.getId(),
                course.getTitle(),
                course.getDescription(),
                course.getStatus(),
                taskDTOs
        );
    }

    public record TaskDTO(
            Long id,
            String statement,
            Integer order,
            Type type
    ) {
        public static TaskDTO fromModel(Task task) {
            return new TaskDTO(
                    task.getId(),
                    task.getStatement(),
                    task.getOrder(),
                    task.getType()
            );
        }
    }
}
