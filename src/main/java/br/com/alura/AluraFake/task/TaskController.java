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

        Task task = dto.toModel(courseRepository);
        taskRepository.save(task);

        return ResponseEntity.ok().build();
    }

    @PostMapping("/task/new/singlechoice")
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

        SingleChoiceTask task = dto.toModel(courseRepository);
        taskRepository.save(task);

        return ResponseEntity.ok().build();
    }

    @PostMapping("/task/new/multiplechoice")
    public ResponseEntity newMultipleChoice() {
        return ResponseEntity.ok().build();
    }

}