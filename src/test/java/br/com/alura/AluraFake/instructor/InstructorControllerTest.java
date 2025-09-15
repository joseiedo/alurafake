package br.com.alura.AluraFake.instructor;

import br.com.alura.AluraFake.course.CourseProjection;
import br.com.alura.AluraFake.course.CourseRepository;
import br.com.alura.AluraFake.course.Status;
import br.com.alura.AluraFake.user.Role;
import br.com.alura.AluraFake.user.User;
import br.com.alura.AluraFake.user.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.*;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(InstructorController.class)
class InstructorControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private CourseRepository courseRepository;

    @Test
    void getInstructorCourseReport__should_return_not_found_when_user_does_not_exist() throws Exception {
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/instructor/999/courses"))
                .andExpect(status().isNotFound());
    }

    @Test
    void getInstructorCourseReport__should_return_bad_request_when_user_is_not_instructor() throws Exception {
        User student = new User("Student", "student@alura.com", Role.STUDENT);
        when(userRepository.findById(1L)).thenReturn(Optional.of(student));

        mockMvc.perform(get("/instructor/1/courses"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.field").value("user"))
                .andExpect(jsonPath("$.message").value("User is not an instructor"));
    }

    @Test
    void getInstructorCourseReport__should_return_empty_list_when_instructor_has_no_courses() throws Exception {
        User instructor = new User("Instructor", "instructor@alura.com", Role.INSTRUCTOR);
        when(userRepository.findById(1L)).thenReturn(Optional.of(instructor));
        when(courseRepository.findByInstructorIdWithTaskCount(1L)).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/instructor/1/courses"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.courses").isEmpty())
                .andExpect(jsonPath("$.totalPublishedCourses").value(0));
    }

    @Test
    void getInstructorCourseReport__should_get_published_courses_by_instructor() throws Exception {
        User instructor = new User("John", "john@alura.com", Role.INSTRUCTOR);
        when(userRepository.findById(1L)).thenReturn(Optional.of(instructor));

        List<CourseProjection> courses = List.of(
            createMockProjection(1L, "Published", Status.PUBLISHED, LocalDateTime.now(), 3L)
        );
        when(courseRepository.findByInstructorIdWithTaskCount(1L)).thenReturn(courses);

        mockMvc.perform(get("/instructor/1/courses"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.courses.length()").value(1))
                .andExpect(jsonPath("$.courses[0].id").value(1))
                .andExpect(jsonPath("$.courses[0].title").value("Published"))
                .andExpect(jsonPath("$.courses[0].status").value("PUBLISHED"))
                .andExpect(jsonPath("$.courses[0].totalTasks").value(3))
                .andExpect(jsonPath("$.courses[0].publishedAt").exists())
                .andExpect(jsonPath("$.totalPublishedCourses").value(1));
    }


    @Test
    void getInstructorCourseReport__should_get_building_courses_by_instructor() throws Exception {
        User instructor = new User("John", "john@alura.com", Role.INSTRUCTOR);
        when(userRepository.findById(1L)).thenReturn(Optional.of(instructor));

        List<CourseProjection> courses = List.of(
                createMockProjection(1L, "Building", Status.BUILDING, LocalDateTime.now(), 3L)
        );
        when(courseRepository.findByInstructorIdWithTaskCount(1L)).thenReturn(courses);

        mockMvc.perform(get("/instructor/1/courses"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.courses.length()").value(1))
                .andExpect(jsonPath("$.courses[0].id").value(1))
                .andExpect(jsonPath("$.courses[0].title").value("Building"))
                .andExpect(jsonPath("$.courses[0].status").value("BUILDING"))
                .andExpect(jsonPath("$.courses[0].totalTasks").value(3))
                .andExpect(jsonPath("$.courses[0].publishedAt").exists())
                .andExpect(jsonPath("$.totalPublishedCourses").value(0));
    }

    private CourseProjection createMockProjection(Long id, String title, Status status, LocalDateTime publishedAt, Long taskCount) {
        CourseProjection projection = mock(CourseProjection.class, withSettings().defaultAnswer(CALLS_REAL_METHODS));
        doReturn(id).when(projection).getId();
        doReturn(title).when(projection).getTitle();
        doReturn("Description for " + title).when(projection).getDescription();
        doReturn(status).when(projection).getStatus();
        doReturn(publishedAt).when(projection).getPublishedAt();
        doReturn(taskCount).when(projection).getTaskCount();
        return projection;
    }

}