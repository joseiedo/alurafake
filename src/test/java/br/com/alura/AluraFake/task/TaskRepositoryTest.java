package br.com.alura.AluraFake.task;

import br.com.alura.AluraFake.course.Course;
import br.com.alura.AluraFake.course.CourseRepository;
import br.com.alura.AluraFake.task.domain.*;
import br.com.alura.AluraFake.user.Role;
import br.com.alura.AluraFake.user.User;
import br.com.alura.AluraFake.user.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest(properties = "spring.profiles.active=test")
@Transactional
public class TaskRepositoryTest {

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    public void save__should_persist_open_text_task() {
        User instructor = new User("John Doe", "john.opentext@alura.com", Role.INSTRUCTOR);
        userRepository.save(instructor);

        Course course = new Course("Java Basics", "Introduction to Java", instructor);
        courseRepository.save(course);

        OpenTextTask task = new OpenTextTask(course, "What is Java?", 1);
        Task savedTask = taskRepository.save(task);

        assertThat(savedTask.getId()).isNotNull();
        assertThat(savedTask.getStatement()).isEqualTo("What is Java?");
        assertThat(savedTask.getOrder()).isEqualTo(1);
        assertThat(savedTask.getType()).isEqualTo(Type.OPEN_TEXT);

        entityManager.flush();
        entityManager.clear();

        Optional<Task> foundTask = taskRepository.findById(savedTask.getId());
        assertThat(foundTask).isPresent();
        assertThat(foundTask.get()).isInstanceOf(OpenTextTask.class);
    }

    @Test
    public void save__should_persist_single_choice_task_entity() {
        User instructor = new User("Jane Smith", "jane.singlechoice@alura.com", Role.INSTRUCTOR);
        userRepository.save(instructor);

        Course course = new Course("Python Basics", "Introduction to Python", instructor);
        courseRepository.save(course);

        List<TaskOption> options = List.of(
                new TaskOption("Python is object-oriented", true),
                new TaskOption("Python is compiled only", false)
        );

        SingleChoiceTask task = new SingleChoiceTask(course, "What is a characteristic of Python?", 1, options);
        SingleChoiceTask savedTask = taskRepository.save(task);

        entityManager.flush();
        entityManager.clear();

        Optional<Task> foundTask = taskRepository.findById(savedTask.getId());
        assertThat(foundTask).isPresent();
        assertThat(foundTask.get()).isInstanceOf(SingleChoiceTask.class);

        SingleChoiceTask foundSingleChoiceTask = (SingleChoiceTask) foundTask.get();
        assertOptionsArePersisted(foundSingleChoiceTask.getOptions(), options);
    }

    @Test
    public void save__should_persist_multiple_choice_task_entity() {
        User instructor = new User("Bob Johnson", "bob.multiplechoice@alura.com", Role.INSTRUCTOR);
        userRepository.save(instructor);

        Course course = new Course("Web Development", "Full Stack Development", instructor);
        courseRepository.save(course);

        List<TaskOption> options = List.of(
                new TaskOption("HTML is markup language", true),
                new TaskOption("CSS is styling language", true),
                new TaskOption("HTML is programming language", false)
        );

        MultipleChoiceTask task = new MultipleChoiceTask(course, "Which are web technologies?", 1, options);
        MultipleChoiceTask savedTask = taskRepository.save(task);

        entityManager.flush();
        entityManager.clear();

        Optional<Task> foundTask = taskRepository.findById(savedTask.getId());
        assertThat(foundTask).isPresent();
        assertThat(foundTask.get()).isInstanceOf(MultipleChoiceTask.class);

        MultipleChoiceTask foundMultipleChoiceTask = (MultipleChoiceTask) foundTask.get();
        assertOptionsArePersisted(foundMultipleChoiceTask.getOptions(), options);
    }

    private void assertOptionsArePersisted(List<TaskOption> actualOptions, List<TaskOption> expectedOptions) {
        assertThat(actualOptions).hasSize(expectedOptions.size());
        assertThat(actualOptions).containsExactlyInAnyOrderElementsOf(expectedOptions);
    }

}