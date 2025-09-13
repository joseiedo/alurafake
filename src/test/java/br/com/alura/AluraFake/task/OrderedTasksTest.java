package br.com.alura.AluraFake.task;

import br.com.alura.AluraFake.course.Course;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.TreeSet;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class OrderedTasksTest {

    @Mock
    private Course mockCourse;

    private AutoCloseable closeable;

    @BeforeEach
    void setUpMocks() {
        closeable = MockitoAnnotations.openMocks(this);
        when(mockCourse.isBuilding()).thenReturn(true);
        when(mockCourse.hasTaskWithStatement(any())).thenReturn(false);
    }

    @AfterEach
    void releaseMocks() throws Exception {
        closeable.close();
    }

    @Test
    void add__should_add_task_when_order_is_not_in_use() {
        TreeSet<Task> tasks = new TreeSet<>();
        OrderedTasks organizer = new OrderedTasks(tasks);

        Task task = new Task(mockCourse, Type.OPEN_TEXT, "New task statement", 1);

        organizer.add(task);

        assertEquals(1, tasks.size());
        assertTrue(tasks.contains(task));
        assertEquals(1, task.getOrder());

        List<Task> orderedTasks = List.copyOf(tasks);
        assertEquals(task, orderedTasks.getFirst());
    }

    @Test
    void add__should_shift_task_with_equal_and_higher_orders_when_conflicting_order() {
        Task existingTask1 = new Task(mockCourse, Type.OPEN_TEXT, "First task", 1);
        Task existingTask2 = new Task(mockCourse, Type.OPEN_TEXT, "Second task", 2);
        Task existingTask3 = new Task(mockCourse, Type.OPEN_TEXT, "Second task", 3);
        Task taskWithConflictingOrder = new Task(mockCourse, Type.OPEN_TEXT, "New task at position 2", 2);

        TreeSet<Task> tasks = new TreeSet<>();
        OrderedTasks organizer = new OrderedTasks(tasks) {{
            add(existingTask1);
            add(existingTask2);
            add(existingTask3);
            add(taskWithConflictingOrder);
        }};

        ArrayList<Task> orderedTasks = new ArrayList<>(organizer.getTasks());
        assertEquals(1, orderedTasks.get(0).getOrder());
        assertEquals(existingTask1, orderedTasks.get(0));

        assertEquals(2, orderedTasks.get(1).getOrder());
        assertEquals(taskWithConflictingOrder, orderedTasks.get(1));

        assertEquals(3, orderedTasks.get(2).getOrder());
        assertEquals(existingTask2, orderedTasks.get(2));

        assertEquals(4, orderedTasks.get(3).getOrder());
        assertEquals(existingTask3, orderedTasks.get(3));
    }


    @Test
    void add__should_reject_task_with_large_gaps_in_order_sequence() {
        TreeSet<Task> tasks = new TreeSet<>();

        OrderedTasks organizer = new OrderedTasks(tasks) {{
            add(new Task(mockCourse, Type.OPEN_TEXT, "Task 1", 1));
        }};

        Task newTask = new Task(mockCourse, Type.OPEN_TEXT, "New task at 5", 5);

        assertThrows(IllegalArgumentException.class, () -> organizer.add(newTask));
    }

    @Test
    void add__should_add_task_at_end_without_shifting() {
        Task task1 = new Task(mockCourse, Type.OPEN_TEXT, "Task 1", 1);
        Task task2 = new Task(mockCourse, Type.SINGLE_CHOICE, "Task 2", 2);
        Task newTask = new Task(mockCourse, Type.MULTIPLE_CHOICE, "Task 3", 3);

        TreeSet<Task> tasks = new TreeSet<>();
        OrderedTasks organizer = new OrderedTasks(tasks) {{
            add(task1);
            add(task2);
            add(newTask);
        }};


        List<Task> orderedTasks = List.copyOf(organizer.tasks);
        assertEquals(task1, orderedTasks.get(0));
        assertEquals(task2, orderedTasks.get(1));
        assertEquals(newTask, orderedTasks.get(2));
    }

    @Test
    void hasValidRangeGaps__should_return_true_for_task_in_empty_organizer() {
        TreeSet<Task> tasks = new TreeSet<>();
        OrderedTasks organizer = new OrderedTasks(tasks);

        Task task = new Task(mockCourse, Type.OPEN_TEXT, "First task", 1);

        assertTrue(organizer.hasValidRangeGaps(task));
    }

    @Test
    void hasValidRangeGaps__should_return_false_when_task_creates_invalid_gap_in_sequence() {
        TreeSet<Task> tasks = new TreeSet<>();
        OrderedTasks organizer = new OrderedTasks(tasks);

        Task task1 = new Task(mockCourse, Type.OPEN_TEXT, "Task 1", 1);
        organizer.add(task1);

        Task newTask = new Task(mockCourse, Type.SINGLE_CHOICE, "Task 2", 3);

        assertFalse(organizer.hasValidRangeGaps(newTask));
    }

    @Test
    void hasValidRangeGaps__should_return_false_when_task_creates_invalid_gap_with_lower_neighbor() {
        TreeSet<Task> tasks = new TreeSet<>();
        OrderedTasks organizer = new OrderedTasks(tasks);

        Task task1 = new Task(mockCourse, Type.OPEN_TEXT, "Task 1", 1);
        tasks.add(task1);

        Task newTask = new Task(mockCourse, Type.SINGLE_CHOICE, "Task with gap", 3);

        assertFalse(organizer.hasValidRangeGaps(newTask));
    }

    @Test
    void hasValidRangeGaps__should_return_false_when_task_creates_invalid_gap_with_higher_neighbor() {
        TreeSet<Task> tasks = new TreeSet<>();
        OrderedTasks organizer = new OrderedTasks(tasks);

        Task task5 = new Task(mockCourse, Type.OPEN_TEXT, "Task 5", 5);
        tasks.add(task5);

        Task newTask = new Task(mockCourse, Type.SINGLE_CHOICE, "Task with gap", 3);

        assertFalse(organizer.hasValidRangeGaps(newTask));
    }

    @Test
    void hasValidRangeGaps__should_return_false_when_task_creates_gaps_with_both_neighbors() {
        TreeSet<Task> tasks = new TreeSet<>();
        OrderedTasks organizer = new OrderedTasks(tasks);

        Task task1 = new Task(mockCourse, Type.OPEN_TEXT, "Task 1", 1);
        Task task5 = new Task(mockCourse, Type.MULTIPLE_CHOICE, "Task 5", 5);

        tasks.add(task1);
        tasks.add(task5);

        Task newTask = new Task(mockCourse, Type.SINGLE_CHOICE, "Task with gaps", 3);

        assertFalse(organizer.hasValidRangeGaps(newTask));
    }

    @Test
    void hasValidRangeGaps__should_return_true_when_same_order_as_existing_task() {
        TreeSet<Task> tasks = new TreeSet<>();
        OrderedTasks organizer = new OrderedTasks(tasks);

        Task existingTask = new Task(mockCourse, Type.OPEN_TEXT, "Existing task", 2);
        tasks.add(existingTask);

        Task newTask = new Task(mockCourse, Type.SINGLE_CHOICE, "New task", 2);

        assertTrue(organizer.hasValidRangeGaps(newTask));
    }

}