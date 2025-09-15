package br.com.alura.AluraFake.task;

import br.com.alura.AluraFake.course.Course;
import br.com.alura.AluraFake.course.CourseRepository;
import br.com.alura.AluraFake.task.domain.MultipleChoiceTask;
import br.com.alura.AluraFake.task.domain.SingleChoiceTask;
import br.com.alura.AluraFake.task.domain.Task;
import br.com.alura.AluraFake.util.ErrorItemDTO;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

import static java.lang.String.format;

@RestController
public class TaskController {

    private final TaskRepository taskRepository;

    private final CourseRepository courseRepository;

    private final CourseCanAcceptTaskValidator courseCanAcceptTaskValidator;

    public TaskController(TaskRepository taskRepository, CourseRepository courseRepository, CourseCanAcceptTaskValidator courseCanAcceptTaskValidator) {
        this.taskRepository = taskRepository;
        this.courseRepository = courseRepository;
        this.courseCanAcceptTaskValidator = courseCanAcceptTaskValidator;
    }

    @PostMapping("/task/new/opentext")
    @Transactional
    public ResponseEntity newOpenTextExercise(@RequestBody @Valid NewOpenTextTaskDTO dto) {
        Optional<Course> possibleCourse = courseRepository.findById(dto.courseId());
        if (possibleCourse.isEmpty()) {
            return ResponseEntity.badRequest().body(new ErrorItemDTO("courseId", "Course not found"));
        }

        Optional<ErrorItemDTO> error = courseCanAcceptTaskValidator.validate(possibleCourse.get(), dto.statement(), dto.order());

        if (error.isPresent()) {
            return ResponseEntity.badRequest().body(error);
        }

        Task task = dto.toModel(courseRepository);
        taskRepository.save(task);

        return ResponseEntity.ok().build();
    }

    @PostMapping("/task/new/singlechoice")
    @Transactional
    public ResponseEntity<?> newSingleChoice(@RequestBody @Valid NewSingleChoiceTaskDTO dto) {
        if (!dto.hasRequiredCorrectOptionCounts()) {
            String message = format("Task must have %d correct option.", SingleChoiceTask.REQUIRED_CORRECT_OPTIONS_SIZE);
            return ResponseEntity.badRequest().body(new ErrorItemDTO("options", message));
        }

        Optional<Course> possibleCourse = courseRepository.findById(dto.courseId());
        if (possibleCourse.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorItemDTO("courseId", "Course not found"));
        }

        Optional<ErrorItemDTO> error = courseCanAcceptTaskValidator.validate(possibleCourse.get(), dto.statement(), dto.order());
        if (error.isPresent()) {
            return ResponseEntity.badRequest().body(error);
        }

        SingleChoiceTask task = dto.toModel(courseRepository);
        taskRepository.save(task);

        return ResponseEntity.ok().build();
    }

    @PostMapping("/task/new/multiplechoice")
    @Transactional
    public ResponseEntity<?> newMultipleChoice(@RequestBody @Valid NewMultipleChoiceTaskDTO dto) {
        if (!dto.hasRequiredOptionCounts()) {
            String message = format("Task must include at least %d correct option(s) and %d incorrect option(s).", MultipleChoiceTask.MIN_CORRECT_OPTIONS_SIZE, MultipleChoiceTask.MIN_WRONG_OPTIONS_SIZE);
            return ResponseEntity.badRequest().body(new ErrorItemDTO("options", message));
        }

        Optional<Course> possibleCourse = courseRepository.findById(dto.courseId());
        if (possibleCourse.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorItemDTO("courseId", "Course not found"));
        }

        Optional<ErrorItemDTO> error = courseCanAcceptTaskValidator.validate(possibleCourse.get(), dto.statement(), dto.order());
        if (error.isPresent()) {
            return ResponseEntity.badRequest().body(error);
        }

        MultipleChoiceTask task = dto.toModel(courseRepository);
        taskRepository.save(task);

        return ResponseEntity.ok().build();
    }

}