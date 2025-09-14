package br.com.alura.AluraFake.task;

import br.com.alura.AluraFake.course.Course;
import br.com.alura.AluraFake.course.CourseRepository;
import br.com.alura.AluraFake.user.Role;
import br.com.alura.AluraFake.user.User;
import br.com.alura.AluraFake.user.UserRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
public class TaskRepositoryTest {

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EntityManager entityManager;

    private Course testCourse;

    @BeforeEach
    void setUp() {
        User instructor = new User("John Doe", "john@alura.com", Role.INSTRUCTOR);
        userRepository.save(instructor);

        testCourse = new Course("Java Basics", "Introduction to Java", instructor);
        courseRepository.save(testCourse);
    }

    static Stream<Arguments> taskTypeProvider() {
        return Stream.of(
                Arguments.of("What is polymorphism?", Type.OPEN_TEXT, OpenTextTask.class),
                Arguments.of("Which is correct about Java?", Type.SINGLE_CHOICE, SingleChoiceTask.class),
                Arguments.of("Select valid Java keywords:", Type.MULTIPLE_CHOICE, MultipleChoiceTask.class)
        );
    }

    @ParameterizedTest(name = "should save and retrieve {0}")
    @MethodSource("taskTypeProvider")
    void should_save_and_retrieve_task(String statement, Type expectedType, Class<? extends Task> expectedClass) {
        Task task = createTaskByType(expectedType, statement);
        Task savedTask = taskRepository.save(task);

        entityManager.clear();

        Optional<Task> retrievedTask = taskRepository.findById(savedTask.getId());
        assertTrue(retrievedTask.isPresent());

        Task retrievedTaskEntity = retrievedTask.get();
        assertInstanceOf(expectedClass, retrievedTaskEntity);
        assertEquals(expectedType, retrievedTaskEntity.getType());
        assertEquals(statement, retrievedTaskEntity.getStatement());
        assertEquals(1, retrievedTaskEntity.getOrder());
    }

    private Task createTaskByType(Type type, String statement) {

        return switch (type) {
            case OPEN_TEXT -> new OpenTextTask(testCourse, statement, 1);
            case SINGLE_CHOICE -> new SingleChoiceTask(testCourse, statement, 1, List.of(
                new TaskOption("Option A", true),
                new TaskOption("Option B", false),
                new TaskOption("Option C", false)
            ));
            case MULTIPLE_CHOICE -> new MultipleChoiceTask(testCourse, statement, 1, List.of(
                    new TaskOption("Option A", true),
                    new TaskOption("Option B", true),
                    new TaskOption("Option C", false)
            ));
        };
    }
}