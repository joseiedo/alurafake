package br.com.alura.AluraFake.course;

import br.com.alura.AluraFake.task.domain.OpenTextTask;
import br.com.alura.AluraFake.user.Role;
import br.com.alura.AluraFake.user.User;
import br.com.alura.AluraFake.user.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static br.com.alura.AluraFake.shared.CourseFactory.createCourseReadyToPublish;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
@ActiveProfiles("test")
@Transactional
class CourseRepositoryTest {

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    void findAllWithTaskCount__should_return_courses_with_correct_task_counts() {
        User instructor = new User("John", "john@alura.com", Role.INSTRUCTOR);
        userRepository.save(instructor);

        Course courseA = new Course("Course A", "Description A", instructor);
        Course courseB = new Course("Course B", "Description B", instructor);
        new OpenTextTask(courseB, "Task 1", 1);
        new OpenTextTask(courseB, "Task 2", 2);

        courseRepository.saveAll(List.of(courseA, courseB));

        entityManager.flush();
        entityManager.clear();

        List<CourseProjection> result = courseRepository.findAllWithTaskCount();

        assertThat(result).hasSize(2);
        assertThat(result).extracting(CourseProjection::getTaskCount).containsExactlyInAnyOrder(0L, 2L);

        CourseProjection projectionA = result.stream()
            .filter(c -> c.getTitle().equals("Course A"))
            .findFirst()
            .orElseThrow();
        assertEquals("Course A", projectionA.getTitle());
        assertEquals("Description A", projectionA.getDescription());
        assertEquals(Status.BUILDING, projectionA.getStatus());
        assertEquals(0L, projectionA.getTaskCount());

        CourseProjection projectionB = result.stream()
            .filter(c -> c.getTitle().equals("Course B"))
            .findFirst()
            .orElseThrow();
        assertEquals("Course B", projectionB.getTitle());
        assertEquals("Description B", projectionB.getDescription());
        assertEquals(Status.BUILDING, projectionB.getStatus());
        assertEquals(2L, projectionB.getTaskCount());
    }

    @Test
    void findByInstructorIdWithTaskCount__should_return_courses_for_specific_instructor() {
        User instructor1 = new User("John", "john@alura.com", Role.INSTRUCTOR);
        User instructor2 = new User("Jane", "jane@alura.com", Role.INSTRUCTOR);
        userRepository.saveAll(List.of(instructor1, instructor2));

        Course course1 = new Course("Java", "Java course", instructor1);
        Course course2 = new Course("Python", "Python course", instructor2);
        new OpenTextTask(course1, "What is Java?", 1);
        courseRepository.saveAll(List.of(course1, course2));

        entityManager.flush();
        entityManager.clear();

        List<CourseProjection> result = courseRepository.findByInstructorIdWithTaskCount(instructor1.getId());

        assertThat(result).hasSize(1);

        CourseProjection javaProjection = result.getFirst();
        assertEquals("Java", javaProjection.getTitle());
        assertEquals("Java course", javaProjection.getDescription());
        assertEquals(Status.BUILDING, javaProjection.getStatus());
        assertEquals(1L, javaProjection.getTaskCount());
        assertThat(javaProjection.getPublishedAt()).isNull();
    }

    @Test
    void findByInstructorIdWithTaskCount__should_return_empty_list_when_instructor_has_no_courses() {
        User instructor = new User("No Courses", "nocourses@alura.com", Role.INSTRUCTOR);
        userRepository.save(instructor);

        entityManager.flush();
        entityManager.clear();

        List<CourseProjection> result = courseRepository.findByInstructorIdWithTaskCount(instructor.getId());

        assertThat(result).isEmpty();
    }

    @Test
    void findAllWithTaskCount__should_include_published_courses() {
        User instructor = new User("John", "john@alura.com", Role.INSTRUCTOR);
        userRepository.save(instructor);

        Course course = createCourseReadyToPublish("Course", "Description", instructor);
        course.publish();
        courseRepository.save(course);

        entityManager.flush();
        entityManager.clear();

        List<CourseProjection> result = courseRepository.findAllWithTaskCount();

        assertThat(result).hasSize(1);

        CourseProjection published = result.getFirst();
        assertEquals("Course", published.getTitle());
        assertEquals("Description", published.getDescription());
        assertEquals(Status.PUBLISHED, published.getStatus());
        assertEquals(3L, published.getTaskCount());
        assertThat(published.isPublished()).isTrue();
        assertThat(published.getPublishedAt()).isNotNull();
    }
}