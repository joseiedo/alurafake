package br.com.alura.AluraFake.instructor;

import br.com.alura.AluraFake.course.Course;
import br.com.alura.AluraFake.course.CourseProjection;
import br.com.alura.AluraFake.course.CourseRepository;
import br.com.alura.AluraFake.user.User;
import br.com.alura.AluraFake.user.UserRepository;
import br.com.alura.AluraFake.util.ErrorItemDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

@RestController
public class InstructorController {

    private final UserRepository userRepository;
    private final CourseRepository courseRepository;

    public InstructorController(UserRepository userRepository, CourseRepository courseRepository) {
        this.userRepository = userRepository;
        this.courseRepository = courseRepository;
    }

    @GetMapping("/instructor/{id}/courses")
    @Transactional(readOnly = true)
    public ResponseEntity getInstructorCourseReport(@PathVariable("id") Long id) {
        Optional<User> possibleUser = userRepository.findById(id);
        if (possibleUser.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        User user = possibleUser.get();
        if (!user.isInstructor()) {
            return ResponseEntity.badRequest().body(new ErrorItemDTO("user", "User is not an instructor"));
        }

        List<CourseProjection> courses = courseRepository.findByInstructorIdWithTaskCount(id);
        InstructorCourseReportResponseDTO response = InstructorCourseReportResponseDTO.fromProjections(courses);

        return ResponseEntity.ok(response);
    }

}
