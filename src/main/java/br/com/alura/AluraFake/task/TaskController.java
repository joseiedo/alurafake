package br.com.alura.AluraFake.task;

import br.com.alura.AluraFake.course.Course;
import br.com.alura.AluraFake.course.CourseRepository;
import br.com.alura.AluraFake.util.ErrorItemDTO;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
public class TaskController {

    private final TaskRepository taskRepository;

    private final CourseRepository courseRepository;

    public TaskController(TaskRepository taskRepository, CourseRepository courseRepository) {
        this.taskRepository = taskRepository;
        this.courseRepository = courseRepository;
    }

    @PostMapping("/task/new/opentext")
    @Transactional
    public ResponseEntity newOpenTextExercise(@RequestBody @Valid NewOpenTextTaskDTO dto) {
        Optional<Course> possibleCourse = courseRepository.findById(dto.courseId());
        if (possibleCourse.isEmpty()) {
            return ResponseEntity.badRequest().body(new ErrorItemDTO("courseId", "Course not found"));
        }

        Course course = possibleCourse.get();
        if (!course.isBuilding()) {
            return ResponseEntity.badRequest().body(new ErrorItemDTO("courseId", "Course can't have new tasks when not in BUILDING status"));
        }

        if (course.hasTaskWithStatement(dto.statement())) {
            return ResponseEntity.badRequest().body(new ErrorItemDTO("statement", "Course can't have tasks with the same statement"));
        }

        if (!course.canAcceptOrder(dto.order())) {
            return ResponseEntity.badRequest().body(new ErrorItemDTO("order", "Order is breaking the sequence"));
        }

        Task task = dto.toModel(courseRepository);
        taskRepository.save(task);

        return ResponseEntity.ok().build();
    }

    @PostMapping("/task/new/singlechoice")
    @Transactional
    public ResponseEntity newSingleChoice(@RequestBody @Valid NewSingleChoiceTaskDTO dto) {
        long count = dto.options().stream().filter(NewSingleChoiceTaskDTO.TaskOptionDTO::isCorrect).count();
        if (count > 1) {
            return ResponseEntity.badRequest().body(new ErrorItemDTO("options", "More than one option has isCorrect as true"));
        }
        if (count < 1) {
            return ResponseEntity.badRequest().body(new ErrorItemDTO("options", "Task must have at least one option with isCorrect as true"));
        }

        Optional<Course> possibleCourse = courseRepository.findById(dto.courseId());
        if (possibleCourse.isEmpty()) {
            return ResponseEntity.badRequest().body(new ErrorItemDTO("courseId", "Course not found"));
        }

        Course course = possibleCourse.get();
        if (!course.isBuilding()) {
            return ResponseEntity.badRequest().body(new ErrorItemDTO("courseId", "Course can't have new tasks when not in BUILDING status"));
        }

        if (course.hasTaskWithStatement(dto.statement())) {
            return ResponseEntity.badRequest().body(new ErrorItemDTO("statement", "Course can't have tasks with the same statement"));
        }

        if (!course.canAcceptOrder(dto.order())) {
            return ResponseEntity.badRequest().body(new ErrorItemDTO("order", "Order is breaking the sequence"));
        }

        SingleChoiceTask task = dto.toModel(courseRepository);
        taskRepository.save(task);

        return ResponseEntity.ok().build();
    }

    @PostMapping("/task/new/multiplechoice")
    @Transactional
    public ResponseEntity newMultipleChoice(@RequestBody @Valid NewMultipleChoiceTaskDTO dto) {
        long correctCount = dto.options().stream().filter(NewMultipleChoiceTaskDTO.TaskOptionDTO::isCorrect).count();
        long wrongCount = dto.options().stream().filter(option -> !option.isCorrect()).count();

        if (correctCount < MultipleChoiceTask.MIN_CORRECT_OPTIONS) {
            return ResponseEntity.badRequest().body(new ErrorItemDTO("options", "Task must have at least 2 options with isCorrect as true"));
        }
        if (wrongCount < MultipleChoiceTask.MIN_WRONG_OPTIONS) {
            return ResponseEntity.badRequest().body(new ErrorItemDTO("options", "Task must have at least 1 option with isCorrect as false"));
        }

        Optional<Course> possibleCourse = courseRepository.findById(dto.courseId());
        if (possibleCourse.isEmpty()) {
            return ResponseEntity.badRequest().body(new ErrorItemDTO("courseId", "Course not found"));
        }

        Course course = possibleCourse.get();
        if (!course.isBuilding()) {
            return ResponseEntity.badRequest().body(new ErrorItemDTO("courseId", "Course can't have new tasks when not in BUILDING status"));
        }

        if (course.hasTaskWithStatement(dto.statement())) {
            return ResponseEntity.badRequest().body(new ErrorItemDTO("statement", "Course can't have tasks with the same statement"));
        }

        if (!course.canAcceptOrder(dto.order())) {
            return ResponseEntity.badRequest().body(new ErrorItemDTO("order", "Order is breaking the sequence"));
        }

        MultipleChoiceTask task = dto.toModel(courseRepository);
        taskRepository.save(task);

        return ResponseEntity.ok().build();
    }

}