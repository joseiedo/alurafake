package br.com.alura.AluraFake.instructor;

import br.com.alura.AluraFake.course.Course;
import br.com.alura.AluraFake.course.Status;

import java.time.LocalDateTime;
import java.util.List;

public record InstructorCourseReportResponseDTO(
        List<InstructorCourseReportItemDTO> courses,
        long totalPublishedCourses
) {
    public static InstructorCourseReportResponseDTO fromModel(List<Course> courses) {
        List<InstructorCourseReportItemDTO> courseItems = courses.stream()
                .map(InstructorCourseReportItemDTO::fromModel)
                .toList();

        long publishedCount = courses.stream()
                .filter(Course::isPublished)
                .count();

        return new InstructorCourseReportResponseDTO(courseItems, publishedCount);
    }

    public record InstructorCourseReportItemDTO(
            Long id,
            String title,
            Status status,
            LocalDateTime publishedAt,
            int totalTasks
    ) {
        public static InstructorCourseReportItemDTO fromModel(Course course) {
            return new InstructorCourseReportItemDTO(
                    course.getId(),
                    course.getTitle(),
                    course.getStatus(),
                    course.getPublishedAt(),
                    course.getTasks().size()
            );
        }
    }
}