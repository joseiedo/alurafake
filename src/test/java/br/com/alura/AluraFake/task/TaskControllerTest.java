package br.com.alura.AluraFake.task;

import br.com.alura.AluraFake.course.Course;
import br.com.alura.AluraFake.course.CourseRepository;
import br.com.alura.AluraFake.course.Status;
import br.com.alura.AluraFake.user.Role;
import br.com.alura.AluraFake.user.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;

import static java.lang.String.format;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TaskController.class)
public class TaskControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private TaskRepository taskRepository;

    @MockBean
    private CourseRepository courseRepository;

    @Test
    public void shouldRejectStatementTooShort() throws Exception {
        NewOpenTextTaskDTO dto = new NewOpenTextTaskDTO(1L, "abc", 1);

        mockMvc.perform(post("/task/new/opentext")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$[0].field").value("statement"))
                .andExpect(jsonPath("$[0].message").value("size must be between 4 and 255"));
    }

    @Test
    public void shouldRejectStatementTooLong() throws Exception {
        String longStatement = "a".repeat(256);
        NewOpenTextTaskDTO dto = new NewOpenTextTaskDTO(1L, longStatement, 1);

        mockMvc.perform(post("/task/new/opentext")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$[0].field").value("statement"))
                .andExpect(jsonPath("$[0].message").value("size must be between 4 and 255"));
    }

    @Test
    public void shouldRejectOrderTooSmall() throws Exception {
        NewOpenTextTaskDTO dto = new NewOpenTextTaskDTO(1L, "Valid statement", 0);

        mockMvc.perform(post("/task/new/opentext")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$[0].field").value("order"))
                .andExpect(jsonPath("$[0].message").value("must be greater than or equal to 1"));
    }

    @Test
    public void shouldRejectNonExistentCourse() throws Exception {
        NewOpenTextTaskDTO dto = new NewOpenTextTaskDTO(999L, "Valid statement", 1);

        when(courseRepository.findById(999L)).thenReturn(Optional.empty());

        mockMvc.perform(post("/task/new/opentext")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.field").value("courseId"))
                .andExpect(jsonPath("$.message").value("Course not found"));
    }

    @Test
    public void shouldCreateTaskSuccessfully_when_course_is_building_and_statement_is_unique() throws Exception {
        NewOpenTextTaskDTO dto = new NewOpenTextTaskDTO(1L, "Valid statement", 1);
        User instructor = new User("John", "john@alura.com", Role.INSTRUCTOR);
        Course buildingCourse = new Course("Java Basics", "Introduction to Java", instructor);

        when(courseRepository.findById(1L)).thenReturn(Optional.of(buildingCourse));

        mockMvc.perform(post("/task/new/opentext")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk());
    }

    @Test
    public void shouldRejectTask_when_course_is_published() throws Exception {
        NewOpenTextTaskDTO dto = new NewOpenTextTaskDTO(1L, "Valid statement", 1);
        User instructor = new User("John", "john@alura.com", Role.INSTRUCTOR);
        Course publishedCourse = new Course("Java Basics", "Introduction to Java", instructor);
        publishedCourse.setStatus(Status.PUBLISHED);

        when(courseRepository.findById(1L)).thenReturn(Optional.of(publishedCourse));

        mockMvc.perform(post("/task/new/opentext")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.field").value("courseId"))
                .andExpect(jsonPath("$.message").value("Course can't have new tasks when not in BUILDING status"));
    }

    @Test
    public void shouldRejectTask_when_statement_already_exists() throws Exception {
        NewOpenTextTaskDTO dto = new NewOpenTextTaskDTO(1L, "Duplicate statement", 1);
        User instructor = new User("John", "john@alura.com", Role.INSTRUCTOR);
        Course course = new Course("Java Basics", "Introduction to Java", instructor);

        OpenTextTask existingTask = new OpenTextTask(course, "Duplicate statement", 1);

        when(courseRepository.findById(1L)).thenReturn(Optional.of(course));

        mockMvc.perform(post("/task/new/opentext")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.field").value("statement"))
                .andExpect(jsonPath("$.message").value("Course can't have tasks with the same statement"));
    }

    @Test
    public void shouldAcceptTask_when_course_is_building_and_statement_is_different() throws Exception {
        NewOpenTextTaskDTO dto = new NewOpenTextTaskDTO(1L, "New statement", 2);
        User instructor = new User("John", "john@alura.com", Role.INSTRUCTOR);
        Course course = new Course("Java Basics", "Introduction to Java", instructor);

        OpenTextTask existingTask = new OpenTextTask(course, "Existing statement", 1);

        when(courseRepository.findById(1L)).thenReturn(Optional.of(course));

        mockMvc.perform(post("/task/new/opentext")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk());
    }

    @Test
    public void shouldCreateSingleChoiceTaskSuccessfully_when_has_exactly_one_correct_option() throws Exception {
        List<NewSingleChoiceTaskDTO.TaskOptionDTO> options = List.of(
                new NewSingleChoiceTaskDTO.TaskOptionDTO("Java is object-oriented", true),
                new NewSingleChoiceTaskDTO.TaskOptionDTO("Java is functional only", false),
                new NewSingleChoiceTaskDTO.TaskOptionDTO("Java is procedural only", false)
        );
        NewSingleChoiceTaskDTO dto = new NewSingleChoiceTaskDTO(1L, "What is Java?", 1, options);

        User instructor = new User("John", "john@alura.com", Role.INSTRUCTOR);
        Course buildingCourse = new Course("Java Basics", "Introduction to Java", instructor);

        when(courseRepository.findById(1L)).thenReturn(Optional.of(buildingCourse));

        mockMvc.perform(post("/task/new/singlechoice")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk());
    }

    @Test
    public void shouldRejectSingleChoiceTask_when_has_no_correct_option() throws Exception {
        List<NewSingleChoiceTaskDTO.TaskOptionDTO> options = List.of(
                new NewSingleChoiceTaskDTO.TaskOptionDTO("Java is object-oriented", false),
                new NewSingleChoiceTaskDTO.TaskOptionDTO("Java is functional only", false),
                new NewSingleChoiceTaskDTO.TaskOptionDTO("Java is procedural only", false)
        );
        NewSingleChoiceTaskDTO dto = new NewSingleChoiceTaskDTO(1L, "What is Java?", 1, options);

        mockMvc.perform(post("/task/new/singlechoice")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.field").value("options"))
                .andExpect(jsonPath("$.message").value(format("Task must have %d correct option.", SingleChoiceTask.REQUIRED_OPTIONS)));
    }

    @Test
    public void shouldRejectSingleChoiceTask_when_has_multiple_correct_options() throws Exception {
        List<NewSingleChoiceTaskDTO.TaskOptionDTO> options = List.of(
                new NewSingleChoiceTaskDTO.TaskOptionDTO("Java is object-oriented", true),
                new NewSingleChoiceTaskDTO.TaskOptionDTO("Java is platform independent", true),
                new NewSingleChoiceTaskDTO.TaskOptionDTO("Java is procedural only", false)
        );
        NewSingleChoiceTaskDTO dto = new NewSingleChoiceTaskDTO(1L, "What is Java?", 1, options);

        mockMvc.perform(post("/task/new/singlechoice")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.field").value("options"))
                .andExpect(jsonPath("$.message").value(format("Task must have %d correct option.", SingleChoiceTask.REQUIRED_OPTIONS)));
    }

    @Test
    public void shouldRejectSingleChoiceTask_when_course_is_published() throws Exception {
        List<NewSingleChoiceTaskDTO.TaskOptionDTO> options = List.of(
                new NewSingleChoiceTaskDTO.TaskOptionDTO("Java is object-oriented", true),
                new NewSingleChoiceTaskDTO.TaskOptionDTO("Java is functional only", false),
                new NewSingleChoiceTaskDTO.TaskOptionDTO("Java is procedural only", false)
        );
        NewSingleChoiceTaskDTO dto = new NewSingleChoiceTaskDTO(1L, "What is Java?", 1, options);

        User instructor = new User("John", "john@alura.com", Role.INSTRUCTOR);
        Course publishedCourse = new Course("Java Basics", "Introduction to Java", instructor);
        publishedCourse.setStatus(Status.PUBLISHED);

        when(courseRepository.findById(1L)).thenReturn(Optional.of(publishedCourse));

        mockMvc.perform(post("/task/new/singlechoice")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.field").value("courseId"))
                .andExpect(jsonPath("$.message").value("Course can't have new tasks when not in BUILDING status"));
    }

    @Test
    public void shouldRejectSingleChoiceTask_when_course_not_found() throws Exception {
        List<NewSingleChoiceTaskDTO.TaskOptionDTO> options = List.of(
                new NewSingleChoiceTaskDTO.TaskOptionDTO("Java is object-oriented", true),
                new NewSingleChoiceTaskDTO.TaskOptionDTO("Java is functional only", false),
                new NewSingleChoiceTaskDTO.TaskOptionDTO("Java is procedural only", false)
        );
        NewSingleChoiceTaskDTO dto = new NewSingleChoiceTaskDTO(999L, "What is Java?", 1, options);

        when(courseRepository.findById(999L)).thenReturn(Optional.empty());

        mockMvc.perform(post("/task/new/singlechoice")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.field").value("courseId"))
                .andExpect(jsonPath("$.message").value("Course not found"));
    }

    @Test
    public void shouldRejectSingleChoiceTask_when_has_too_few_options() throws Exception {
        List<NewSingleChoiceTaskDTO.TaskOptionDTO> options = List.of(
                new NewSingleChoiceTaskDTO.TaskOptionDTO("Java is object-oriented", true)
        );
        NewSingleChoiceTaskDTO dto = new NewSingleChoiceTaskDTO(1L, "What is Java?", 1, options);

        mockMvc.perform(post("/task/new/singlechoice")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$[0].field").value("options"))
                .andExpect(jsonPath("$[0].message").value("size must be between 2 and 5"));
    }

    @Test
    public void shouldRejectSingleChoiceTask_when_has_too_many_options() throws Exception {
        List<NewSingleChoiceTaskDTO.TaskOptionDTO> options = List.of(
                new NewSingleChoiceTaskDTO.TaskOptionDTO("Java is object-oriented", true),
                new NewSingleChoiceTaskDTO.TaskOptionDTO("Java is functional only", false),
                new NewSingleChoiceTaskDTO.TaskOptionDTO("Java is procedural only", false),
                new NewSingleChoiceTaskDTO.TaskOptionDTO("Java is scripting language", false),
                new NewSingleChoiceTaskDTO.TaskOptionDTO("Java is assembly language", false),
                new NewSingleChoiceTaskDTO.TaskOptionDTO("Java is machine language", false)
        );
        NewSingleChoiceTaskDTO dto = new NewSingleChoiceTaskDTO(1L, "What is Java?", 1, options);

        mockMvc.perform(post("/task/new/singlechoice")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$[0].field").value("options"))
                .andExpect(jsonPath("$[0].message").value("size must be between 2 and 5"));
    }

    @Test
    public void shouldCreateMultipleChoiceTaskSuccessfully_when_has_valid_options() throws Exception {
        List<NewMultipleChoiceTaskDTO.TaskOptionDTO> options = List.of(
                new NewMultipleChoiceTaskDTO.TaskOptionDTO("Java is object-oriented", true),
                new NewMultipleChoiceTaskDTO.TaskOptionDTO("Java is platform independent", true),
                new NewMultipleChoiceTaskDTO.TaskOptionDTO("Java is functional only", false),
                new NewMultipleChoiceTaskDTO.TaskOptionDTO("Java is procedural only", false)
        );
        NewMultipleChoiceTaskDTO dto = new NewMultipleChoiceTaskDTO(1L, "What are Java characteristics?", 1, options);

        User instructor = new User("John", "john@alura.com", Role.INSTRUCTOR);
        Course buildingCourse = new Course("Java Basics", "Introduction to Java", instructor);

        when(courseRepository.findById(1L)).thenReturn(Optional.of(buildingCourse));

        mockMvc.perform(post("/task/new/multiplechoice")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk());
    }

    @Test
    public void shouldRejectMultipleChoiceTask_when_has_insufficient_correct_options() throws Exception {
        List<NewMultipleChoiceTaskDTO.TaskOptionDTO> options = List.of(
                new NewMultipleChoiceTaskDTO.TaskOptionDTO("Java is object-oriented", true),
                new NewMultipleChoiceTaskDTO.TaskOptionDTO("Java is functional only", false),
                new NewMultipleChoiceTaskDTO.TaskOptionDTO("Java is procedural only", false)
        );
        NewMultipleChoiceTaskDTO dto = new NewMultipleChoiceTaskDTO(1L, "What are Java characteristics?", 1, options);

        mockMvc.perform(post("/task/new/multiplechoice")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.field").value("options"))
                .andExpect(jsonPath("$.message").value(format(
                        "Task must include at least %d correct option(s) and %d incorrect option(s).",
                        MultipleChoiceTask.MIN_CORRECT_OPTIONS,
                        MultipleChoiceTask.MIN_WRONG_OPTIONS
                )));
    }

    @Test
    public void shouldRejectMultipleChoiceTask_when_has_insufficient_wrong_options() throws Exception {
        List<NewMultipleChoiceTaskDTO.TaskOptionDTO> options = List.of(
                new NewMultipleChoiceTaskDTO.TaskOptionDTO("Java is object-oriented", true),
                new NewMultipleChoiceTaskDTO.TaskOptionDTO("Java is platform independent", true),
                new NewMultipleChoiceTaskDTO.TaskOptionDTO("Java is compiled", true)
        );
        NewMultipleChoiceTaskDTO dto = new NewMultipleChoiceTaskDTO(1L, "What are Java characteristics?", 1, options);

        mockMvc.perform(post("/task/new/multiplechoice")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.field").value("options"))
                .andExpect(jsonPath("$.message").value(format(
                        "Task must include at least %d correct option(s) and %d incorrect option(s).",
                        MultipleChoiceTask.MIN_CORRECT_OPTIONS,
                        MultipleChoiceTask.MIN_WRONG_OPTIONS
                )));
    }

    @Test
    public void shouldRejectMultipleChoiceTask_when_course_is_published() throws Exception {
        List<NewMultipleChoiceTaskDTO.TaskOptionDTO> options = List.of(
                new NewMultipleChoiceTaskDTO.TaskOptionDTO("Java is object-oriented", true),
                new NewMultipleChoiceTaskDTO.TaskOptionDTO("Java is platform independent", true),
                new NewMultipleChoiceTaskDTO.TaskOptionDTO("Java is functional only", false)
        );
        NewMultipleChoiceTaskDTO dto = new NewMultipleChoiceTaskDTO(1L, "What are Java characteristics?", 1, options);

        User instructor = new User("John", "john@alura.com", Role.INSTRUCTOR);
        Course publishedCourse = new Course("Java Basics", "Introduction to Java", instructor);
        publishedCourse.setStatus(Status.PUBLISHED);

        when(courseRepository.findById(1L)).thenReturn(Optional.of(publishedCourse));

        mockMvc.perform(post("/task/new/multiplechoice")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.field").value("courseId"))
                .andExpect(jsonPath("$.message").value("Course can't have new tasks when not in BUILDING status"));
    }

    @Test
    public void shouldRejectMultipleChoiceTask_when_course_not_found() throws Exception {
        List<NewMultipleChoiceTaskDTO.TaskOptionDTO> options = List.of(
                new NewMultipleChoiceTaskDTO.TaskOptionDTO("Java is object-oriented", true),
                new NewMultipleChoiceTaskDTO.TaskOptionDTO("Java is platform independent", true),
                new NewMultipleChoiceTaskDTO.TaskOptionDTO("Java is functional only", false)
        );
        NewMultipleChoiceTaskDTO dto = new NewMultipleChoiceTaskDTO(999L, "What are Java characteristics?", 1, options);

        when(courseRepository.findById(999L)).thenReturn(Optional.empty());

        mockMvc.perform(post("/task/new/multiplechoice")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.field").value("courseId"))
                .andExpect(jsonPath("$.message").value("Course not found"));
    }

}