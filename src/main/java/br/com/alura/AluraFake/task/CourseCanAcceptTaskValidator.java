package br.com.alura.AluraFake.task;

import br.com.alura.AluraFake.course.Course;
import br.com.alura.AluraFake.util.ErrorItemDTO;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.util.Optional;

@Component
public class CourseCanAcceptTaskValidator {

    public Optional<ErrorItemDTO> validate(Course course, String statement, Integer order) {
        Assert.notNull(course, "Received course must not be null");
        Assert.notNull(statement, "Received statement must not be null");
        Assert.notNull(order, "Received order must not be null");


        if (!course.isBuilding()) {
            return Optional.of(new ErrorItemDTO(
                    "courseId",
                    "Course can't have new tasks when not in BUILDING status"
            ));
        }

        if (course.hasTaskWithStatement(statement)) {
            return Optional.of(new ErrorItemDTO(
                    "statement",
                    "Course can't have tasks with the same statement"
            ));
        }

        if (!course.isOrderPlacementValid(order)) {
            return Optional.of(new ErrorItemDTO(
                    "order",
                    "Order placement is invalid"
            ));
        }

        return Optional.empty();
    }
}
