package br.com.alura.AluraFake.instructor;

import br.com.alura.AluraFake.course.CourseProjection;
import br.com.alura.AluraFake.course.Status;

import java.time.LocalDateTime;
import java.util.List;

public record InstructorCourseReportResponseDTO(
        List<InstructorCourseReportItemDTO> courses,
        long totalPublishedCourses
) {

    public static InstructorCourseReportResponseDTO fromProjections(List<CourseProjection> projections) {
        List<InstructorCourseReportItemDTO> courseItems = projections.stream()
                .map(InstructorCourseReportItemDTO::fromProjection)
                .toList();

        long publishedCount = projections.stream()
                .filter(CourseProjection::isPublished)
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

        public static InstructorCourseReportItemDTO fromProjection(CourseProjection projection) {
            return new InstructorCourseReportItemDTO(
                    projection.getId(),
                    projection.getTitle(),
                    projection.getStatus(),
                    projection.getPublishedAt(),
                    projection.getTaskCount().intValue()
            );
        }
    }
}