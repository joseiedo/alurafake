package br.com.alura.AluraFake.task;

import br.com.alura.AluraFake.course.Course;
import br.com.alura.AluraFake.course.CourseRepository;
import br.com.alura.AluraFake.user.Role;
import br.com.alura.AluraFake.user.User;
import br.com.alura.AluraFake.user.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(properties = "spring.profiles.active=test")
@AutoConfigureMockMvc
@Transactional
public class TaskControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private TaskRepository taskRepository;

    @Test
    public void shouldCreateOpenTextTaskSuccessfully() throws Exception {
        User instructor = new User("John Doe", "john.opentext@alura.com", Role.INSTRUCTOR);
        userRepository.save(instructor);

        Course course = new Course("Java Basics", "Introduction to Java", instructor);
        courseRepository.save(course);

        NewOpenTextTaskDTO dto = new NewOpenTextTaskDTO(
                course.getId(),
                "What is Java?",
                1
        );

        mockMvc.perform(post("/task/new/opentext")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk());
    }

    @Test
    public void shouldCreateSingleChoiceTaskSuccessfully() throws Exception {
        User instructor = new User("Jane Smith", "jane.singlechoice@alura.com", Role.INSTRUCTOR);
        userRepository.save(instructor);

        Course course = new Course("Python Basics", "Introduction to Python", instructor);
        courseRepository.save(course);

        List<NewSingleChoiceTaskDTO.TaskOptionDTO> options = List.of(
                new NewSingleChoiceTaskDTO.TaskOptionDTO("Python is object-oriented", true),
                new NewSingleChoiceTaskDTO.TaskOptionDTO("Python is compiled only", false),
                new NewSingleChoiceTaskDTO.TaskOptionDTO("Python is assembly language", false)
        );

        NewSingleChoiceTaskDTO dto = new NewSingleChoiceTaskDTO(
                course.getId(),
                "What is a characteristic of Python?",
                1,
                options
        );

        mockMvc.perform(post("/task/new/singlechoice")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk());
    }

    @Test
    public void shouldCreateMultipleChoiceTaskSuccessfully() throws Exception {
        User instructor = new User("Bob Johnson", "bob.multiplechoice@alura.com", Role.INSTRUCTOR);
        userRepository.save(instructor);

        Course course = new Course("Web Development", "Full Stack Development", instructor);
        courseRepository.save(course);

        List<NewMultipleChoiceTaskDTO.TaskOptionDTO> options = List.of(
                new NewMultipleChoiceTaskDTO.TaskOptionDTO("HTML is markup language", true),
                new NewMultipleChoiceTaskDTO.TaskOptionDTO("CSS is styling language", true),
                new NewMultipleChoiceTaskDTO.TaskOptionDTO("HTML is programming language", false),
                new NewMultipleChoiceTaskDTO.TaskOptionDTO("CSS is database language", false)
        );

        NewMultipleChoiceTaskDTO dto = new NewMultipleChoiceTaskDTO(
                course.getId(),
                "Which are web technologies?",
                1,
                options
        );

        mockMvc.perform(post("/task/new/multiplechoice")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk());
    }

    @Test
    public void shouldCreateMultipleTasksInSameCourse() throws Exception {
        User instructor = new User("Alice Brown", "alice.multiple@alura.com", Role.INSTRUCTOR);
        userRepository.save(instructor);

        Course course = new Course("Data Structures", "Learn data structures", instructor);
        courseRepository.save(course);

        NewOpenTextTaskDTO openTextDto = new NewOpenTextTaskDTO(
                course.getId(),
                "Explain what is an array",
                1
        );

        mockMvc.perform(post("/task/new/opentext")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(openTextDto)))
                .andExpect(status().isOk());

        List<NewSingleChoiceTaskDTO.TaskOptionDTO> singleChoiceOptions = List.of(
                new NewSingleChoiceTaskDTO.TaskOptionDTO("Array has fixed size", true),
                new NewSingleChoiceTaskDTO.TaskOptionDTO("Array has dynamic size", false),
                new NewSingleChoiceTaskDTO.TaskOptionDTO("Array stores different types", false)
        );

        NewSingleChoiceTaskDTO singleChoiceDto = new NewSingleChoiceTaskDTO(
                course.getId(),
                "What is true about arrays?",
                2,
                singleChoiceOptions
        );

        mockMvc.perform(post("/task/new/singlechoice")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(singleChoiceDto)))
                .andExpect(status().isOk());

        List<NewMultipleChoiceTaskDTO.TaskOptionDTO> multipleChoiceOptions = List.of(
                new NewMultipleChoiceTaskDTO.TaskOptionDTO("Arrays are indexed", true),
                new NewMultipleChoiceTaskDTO.TaskOptionDTO("Arrays store elements", true),
                new NewMultipleChoiceTaskDTO.TaskOptionDTO("Arrays are trees", false),
                new NewMultipleChoiceTaskDTO.TaskOptionDTO("Arrays are graphs", false)
        );

        NewMultipleChoiceTaskDTO multipleChoiceDto = new NewMultipleChoiceTaskDTO(
                course.getId(),
                "What are characteristics of arrays?",
                3,
                multipleChoiceOptions
        );

        mockMvc.perform(post("/task/new/multiplechoice")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(multipleChoiceDto)))
                .andExpect(status().isOk());
    }
 }