package br.com.alura.AluraFake.course;

import br.com.alura.AluraFake.user.*;
import br.com.alura.AluraFake.util.ErrorItemDTO;
import br.com.alura.AluraFake.util.NotFoundItemDTO;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
public class CourseController {

    private final CourseRepository courseRepository;
    private final UserRepository userRepository;

    @Autowired
    public CourseController(CourseRepository courseRepository, UserRepository userRepository){
        this.courseRepository = courseRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    @PostMapping("/course/new")
    public ResponseEntity createCourse(@Valid @RequestBody NewCourseDTO newCourse) {

        //Caso implemente o bonus, pegue o instrutor logado
        Optional<User> possibleAuthor = userRepository
                .findByEmail(newCourse.getEmailInstructor())
                .filter(User::isInstructor);

        if(possibleAuthor.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorItemDTO("emailInstructor", "Usuário não é um instrutor"));
        }

        Course course = new Course(newCourse.getTitle(), newCourse.getDescription(), possibleAuthor.get());

        courseRepository.save(course);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("/course/all")
    public ResponseEntity<List<CourseListItemDTO>> createCourse() {
        List<CourseListItemDTO> courses = courseRepository.findAll().stream()
                .map(CourseListItemDTO::new)
                .toList();
        return ResponseEntity.ok(courses);
    }

    @GetMapping("/course/{id}")
    public ResponseEntity<?> findCourse(@PathVariable("id") Long id) {
        Optional<Course> possibleCourse = courseRepository.findById(id);
        if (possibleCourse.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new NotFoundItemDTO(Course.class));
        }

        return ResponseEntity.ok(FindCourseDTO.fromModel(possibleCourse.get()));
    }

    @Transactional
    @PostMapping("/course/{id}/publish")
    public ResponseEntity<?> publishCourse(@PathVariable("id") Long id) {
        Optional<Course> possibleCourse = courseRepository.findById(id);
        if (possibleCourse.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new NotFoundItemDTO(Course.class));
        }

        Course course = possibleCourse.get();
        if (!course.isBuilding()) {
            return ResponseEntity.badRequest().body(new ErrorItemDTO("course", "Course is not in BUILDING status"));
        }

        if (!course.hasContinuousTaskSequence()) {
            return ResponseEntity.badRequest().body(new ErrorItemDTO("tasks", "Course task sequence is not continuous"));
        }

        if (!course.hasAllTaskTypes()) {
            return ResponseEntity.badRequest().body(new ErrorItemDTO("tasks", "Course must have at least one activity of each type"));
        }

        course.publish();
        courseRepository.save(course);

        return ResponseEntity.ok(new CoursePublishResponseDTO(course));
    }

}
