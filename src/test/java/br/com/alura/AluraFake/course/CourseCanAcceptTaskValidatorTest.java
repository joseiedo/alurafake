package br.com.alura.AluraFake.course;

import br.com.alura.AluraFake.task.CourseCanAcceptTaskValidator;
import br.com.alura.AluraFake.util.ErrorItemDTO;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class CourseCanAcceptTaskValidatorTest {

    private final CourseCanAcceptTaskValidator validator = new CourseCanAcceptTaskValidator();

    @Test
    public void shouldRejectSingleChoiceTask_when_course_is_published() {
        Course course = mock(Course.class);
        when(course.isBuilding()).thenReturn(false);

        Optional<ErrorItemDTO> error = validator.validate(course, "", 0);

        assertTrue(error.isPresent());
        assertEquals("Course can't have new tasks when not in BUILDING status", error.get().getMessage());
    }

    @Test
    public void shouldRejectTask_when_statement_already_exists(){
        String statement = "";
        Course course = mock(Course.class);
        when(course.isBuilding()).thenReturn(true);
        when(course.hasTaskWithStatement(statement)).thenReturn(true);

        Optional<ErrorItemDTO> error = validator.validate(course, statement, 0);

        assertTrue(error.isPresent());
        assertEquals("Course can't have tasks with the same statement", error.get().getMessage());
    }

    @Test
    public void validate__should_return_error_when_order_placement_is_invalid() {
        String statement = "";
        Course course = mock(Course.class);
        when(course.isBuilding()).thenReturn(true);
        when(course.hasTaskWithStatement(statement)).thenReturn(false);
        when(course.isOrderPlacementValid(0)).thenReturn(false);

        Optional<ErrorItemDTO> error = validator.validate(course, statement, 0);

        assertTrue(error.isPresent());
        assertEquals("Order placement is invalid", error.get().getMessage());
    }


    @Test
    public void validate__should_return_not_return_error_when_course_can_accept_tasks() {
        String statement = "";
        Course course = mock(Course.class);
        when(course.isBuilding()).thenReturn(true);
        when(course.hasTaskWithStatement(statement)).thenReturn(false);
        when(course.isOrderPlacementValid(0)).thenReturn(true);

        Optional<ErrorItemDTO> error = validator.validate(course, statement, 0);

        assertTrue(error.isEmpty());
    }
}