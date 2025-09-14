package br.com.alura.AluraFake.instructor;

import br.com.alura.AluraFake.course.Course;
import br.com.alura.AluraFake.course.CourseRepository;
import br.com.alura.AluraFake.user.Role;
import br.com.alura.AluraFake.user.User;
import br.com.alura.AluraFake.user.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

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

        verify(userRepository, times(1)).findById(999L);
        verifyNoInteractions(courseRepository);
    }

    @Test
    void getInstructorCourseReport__should_return_bad_request_when_user_is_not_instructor() throws Exception {
        User student = new User("Student Name", "student@alura.com.br", Role.STUDENT);
        when(userRepository.findById(1L)).thenReturn(Optional.of(student));

        mockMvc.perform(get("/instructor/1/courses"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.field").value("user"))
                .andExpect(jsonPath("$.message").value("User is not an instructor"));

        verify(userRepository, times(1)).findById(1L);
        verifyNoInteractions(courseRepository);
    }

    @Test
    void getInstructorCourseReport__should_return_empty_list_when_instructor_has_no_courses() throws Exception {
        User instructor = new User("Instructor Name", "instructor@alura.com.br", Role.INSTRUCTOR);
        when(userRepository.findById(1L)).thenReturn(Optional.of(instructor));
        when(courseRepository.findByInstructorId(1L)).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/instructor/1/courses"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.courses").isArray())
                .andExpect(jsonPath("$.courses").isEmpty())
                .andExpect(jsonPath("$.totalPublishedCourses").value(0));

        verify(userRepository, times(1)).findById(1L);
        verify(courseRepository, times(1)).findByInstructorId(1L);
    }

    @Test
    void getInstructorCourseReport__should_get_courses_by_instructor() throws Exception {
        User instructor = new User("Ana Costa", "ana@alura.com.br", Role.INSTRUCTOR);
        when(userRepository.findById(2L)).thenReturn(Optional.of(instructor));

        Course publishedCourseSpy = spy(new Course("Published Course", "A published course", instructor));
        when(publishedCourseSpy.hasContinuousTaskSequence()).thenReturn(true);
        when(publishedCourseSpy.hasAllTaskTypes()).thenReturn(true);
        publishedCourseSpy.publish();

        Course buildingCourse = new Course("Building Course", "A course in building", instructor);

        when(courseRepository.findByInstructorId(2L)).thenReturn(List.of(publishedCourseSpy, buildingCourse));

        mockMvc.perform(get("/instructor/2/courses"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.courses").isArray())
                .andExpect(jsonPath("$.courses.length()").value(2))
                .andExpect(jsonPath("$.totalPublishedCourses").value(1));
    }

}