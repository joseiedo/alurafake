package br.com.alura.AluraFake.task.domain;

import br.com.alura.AluraFake.course.Course;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.doReturn;

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

        OpenTextTask task = new OpenTextTask(mockCourse, "New task statement", 1);

        organizer.add(task);

        assertEquals(1, tasks.size());
        assertTrue(tasks.contains(task));
        assertEquals(1, task.getOrder());

        List<Task> orderedTasks = List.copyOf(tasks);
        assertEquals(task, orderedTasks.getFirst());
    }

    @Test
    void add__should_shift_task_with_equal_and_higher_orders_when_conflicting_order() {
        OpenTextTask existingTask1 = new OpenTextTask(mockCourse, "First task", 1);
        OpenTextTask existingTask2 = new OpenTextTask(mockCourse, "Second task", 2);
        OpenTextTask existingTask3 = new OpenTextTask(mockCourse, "Second task", 3);
        OpenTextTask taskWithConflictingOrder = new OpenTextTask(mockCourse, "New task at position 2", 2);

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
            add(new OpenTextTask(mockCourse, "Task 1", 1));
        }};

        OpenTextTask newTask = new OpenTextTask(mockCourse, "New task at 5", 5);

        assertThrows(IllegalArgumentException.class, () -> organizer.add(newTask));
    }

    @Test
    void add__should_add_task_at_end_without_shifting() {
        OpenTextTask task1 = new OpenTextTask(mockCourse, "Task 1", 1);
        OpenTextTask task2 = new OpenTextTask(mockCourse, "Task 2", 2);
        OpenTextTask newTask = new OpenTextTask(mockCourse, "Task 3", 3);

        TreeSet<Task> tasks = new TreeSet<>();
        OrderedTasks organizer = new OrderedTasks(tasks) {{
            add(task1);
            add(task2);
            add(newTask);
        }};


        List<Task> orderedTasks = List.copyOf(organizer.getTasks());
        assertEquals(task1, orderedTasks.get(0));
        assertEquals(task2, orderedTasks.get(1));
        assertEquals(newTask, orderedTasks.get(2));
    }

    @Test
    void hasValidOrderGaps__should_return_true_for_task_in_empty_organizer() {
        TreeSet<Task> tasks = new TreeSet<>();
        OrderedTasks organizer = new OrderedTasks(tasks);

        OpenTextTask task = new OpenTextTask(mockCourse, "First task", 1);

        assertTrue(organizer.fitsInSequence(task));
    }

    @Test
    void hasValidOrderGaps__should_return_false_when_task_creates_invalid_gap_in_sequence() {
        TreeSet<Task> tasks = new TreeSet<>();
        OrderedTasks organizer = new OrderedTasks(tasks);

        OpenTextTask task1 = new OpenTextTask(mockCourse, "Task 1", 1);
        organizer.add(task1);

        OpenTextTask newTask = new OpenTextTask(mockCourse, "Task 2", 3);

        assertFalse(organizer.fitsInSequence(newTask));
    }

    @Test
    void hasValidOrderGaps__should_return_false_when_task_creates_invalid_gap_with_lower_neighbor() {
        TreeSet<Task> tasks = new TreeSet<>();
        OrderedTasks organizer = spy(new OrderedTasks(tasks));

        OpenTextTask task1 = new OpenTextTask(mockCourse, "Task 1", 1);
        tasks.add(task1);

        doReturn(true).when(organizer).hasContinuousTaskSequence();

        OpenTextTask newTask = new OpenTextTask(mockCourse, "Task with gap", 3);

        assertFalse(organizer.fitsInSequence(newTask));
    }


    @Test
    void hasValidOrderGaps__should_return_true_when_same_order_as_existing_task() {
        TreeSet<Task> tasks = new TreeSet<>();
        OrderedTasks organizer = spy(new OrderedTasks(tasks));

        OpenTextTask existingTask = new OpenTextTask(mockCourse, "Existing task", 2);
        tasks.add(existingTask);

        when(organizer.hasContinuousTaskSequence()).thenReturn(true);

        OpenTextTask newTask = new OpenTextTask(mockCourse, "New task", 2);

        assertTrue(organizer.fitsInSequence(newTask));
    }

    @Test
    void hasContinuousTaskSequence__should_return_true_for_empty_task_list() {
        TreeSet<Task> tasks = new TreeSet<>();
        OrderedTasks organizer = new OrderedTasks(tasks);

        assertTrue(organizer.hasContinuousTaskSequence());
    }

    @Test
    void hasContinuousTaskSequence__should_return_true_for_single_task_starting_at_one() {
        TreeSet<Task> tasks = new TreeSet<>();
        OrderedTasks organizer = new OrderedTasks(tasks);

        OpenTextTask task = new OpenTextTask(mockCourse, "Task 1", 1);
        tasks.add(task);

        assertTrue(organizer.hasContinuousTaskSequence());
    }

    @Test
    void hasContinuousTaskSequence__should_return_false_for_single_task_not_starting_at_one() {
        TreeSet<Task> tasks = new TreeSet<>();
        OrderedTasks organizer = new OrderedTasks(tasks);

        OpenTextTask task = new OpenTextTask(mockCourse, "Task 2", 2);
        tasks.add(task);

        assertFalse(organizer.hasContinuousTaskSequence());
    }

    @Test
    void hasContinuousTaskSequence__should_return_true_for_continuous_sequence_starting_at_one() {
        TreeSet<Task> tasks = new TreeSet<>();
        OrderedTasks organizer = new OrderedTasks(tasks);

        OpenTextTask task1 = new OpenTextTask(mockCourse, "Task 1", 1);
        OpenTextTask task2 = new OpenTextTask(mockCourse, "Task 2", 2);
        OpenTextTask task3 = new OpenTextTask(mockCourse, "Task 3", 3);

        tasks.add(task1);
        tasks.add(task2);
        tasks.add(task3);

        assertTrue(organizer.hasContinuousTaskSequence());
    }

    @Test
    void hasContinuousTaskSequence__should_return_false_for_sequence_with_gaps() {
        TreeSet<Task> tasks = new TreeSet<>();
        OrderedTasks organizer = new OrderedTasks(tasks);

        OpenTextTask task1 = new OpenTextTask(mockCourse, "Task 1", 1);
        OpenTextTask task3 = new OpenTextTask(mockCourse, "Task 3", 3);
        OpenTextTask task4 = new OpenTextTask(mockCourse, "Task 4", 4);

        tasks.add(task1);
        tasks.add(task3);
        tasks.add(task4);

        assertFalse(organizer.hasContinuousTaskSequence());
    }

    @Test
    void hasContinuousTaskSequence__should_return_false_when_sequence_does_not_start_at_one() {
        TreeSet<Task> tasks = new TreeSet<>();
        OrderedTasks organizer = new OrderedTasks(tasks);

        OpenTextTask task2 = new OpenTextTask(mockCourse, "Task 2", 2);
        OpenTextTask task3 = new OpenTextTask(mockCourse, "Task 3", 3);
        OpenTextTask task4 = new OpenTextTask(mockCourse, "Task 4", 4);

        tasks.add(task2);
        tasks.add(task3);
        tasks.add(task4);

        assertFalse(organizer.hasContinuousTaskSequence());
    }

    @Test
    void hasContinuousTaskSequence__should_return_true_for_unordered_continuous_sequence() {
        TreeSet<Task> tasks = new TreeSet<>();
        OrderedTasks organizer = new OrderedTasks(tasks);

        OpenTextTask task3 = new OpenTextTask(mockCourse, "Task 3", 3);
        OpenTextTask task1 = new OpenTextTask(mockCourse, "Task 1", 1);
        OpenTextTask task2 = new OpenTextTask(mockCourse, "Task 2", 2);

        tasks.add(task3);
        tasks.add(task1);
        tasks.add(task2);

        assertTrue(organizer.hasContinuousTaskSequence());
    }

    @Test
    void hasContinuousTaskSequence__should_return_false_for_multiple_gaps_in_sequence() {
        TreeSet<Task> tasks = new TreeSet<>();
        OrderedTasks organizer = new OrderedTasks(tasks);

        OpenTextTask task1 = new OpenTextTask(mockCourse, "Task 1", 1);
        OpenTextTask task4 = new OpenTextTask(mockCourse, "Task 4", 4);
        OpenTextTask task7 = new OpenTextTask(mockCourse, "Task 7", 7);

        tasks.add(task1);
        tasks.add(task4);
        tasks.add(task7);

        assertFalse(organizer.hasContinuousTaskSequence());
    }

    @Test
    void hasAllTaskTypes__should_return_true_when_empty_task_list() {
        TreeSet<Task> tasks = new TreeSet<>();
        OrderedTasks organizer = new OrderedTasks(tasks);

        assertFalse(organizer.hasAllTaskTypes());
    }

    @Test
    void hasAllTaskTypes__should_return_false_when_missing_task_types() {
        TreeSet<Task> tasks = new TreeSet<>();
        OrderedTasks organizer = new OrderedTasks(tasks);

        OpenTextTask openTextTask = new OpenTextTask(mockCourse, "Open text task", 1);

        List<TaskOption> singleChoiceOptions = List.of(
                new TaskOption("Option A", true),
                new TaskOption("Option B", false)
        );
        SingleChoiceTask singleChoiceTask = new SingleChoiceTask(mockCourse, "Single choice task", 2, singleChoiceOptions);

        tasks.add(openTextTask);
        tasks.add(singleChoiceTask);

        assertFalse(organizer.hasAllTaskTypes());
    }

    @Test
    void hasAllTaskTypes__should_return_true_when_all_task_types_present() {
        TreeSet<Task> tasks = new TreeSet<>();
        OrderedTasks organizer = new OrderedTasks(tasks);

        OpenTextTask openTextTask = new OpenTextTask(mockCourse, "Open text task", 1);

        List<TaskOption> singleChoiceOptions = List.of(
                new TaskOption("Option A", true),
                new TaskOption("Option B", false)
        );
        SingleChoiceTask singleChoiceTask = new SingleChoiceTask(mockCourse, "Single choice task", 2, singleChoiceOptions);

        List<TaskOption> multipleChoiceOptions = List.of(
                new TaskOption("Option A", true),
                new TaskOption("Option B", true),
                new TaskOption("Option C", false)
        );
        MultipleChoiceTask multipleChoiceTask = new MultipleChoiceTask(mockCourse, "Multiple choice task", 3, multipleChoiceOptions);

        tasks.add(openTextTask);
        tasks.add(singleChoiceTask);
        tasks.add(multipleChoiceTask);

        assertTrue(organizer.hasAllTaskTypes());
    }

    @Test
    void hasAllTaskTypes__should_return_true_when_multiple_tasks_of_each_type() {
        TreeSet<Task> tasks = new TreeSet<>();
        OrderedTasks organizer = new OrderedTasks(tasks);

        OpenTextTask openTextTask1 = new OpenTextTask(mockCourse, "Open text task 1", 1);
        OpenTextTask openTextTask2 = new OpenTextTask(mockCourse, "Open text task 2", 2);

        List<TaskOption> singleChoiceOptions = List.of(
                new TaskOption("Option A", true),
                new TaskOption("Option B", false)
        );
        SingleChoiceTask singleChoiceTask = new SingleChoiceTask(mockCourse, "Single choice task", 3, singleChoiceOptions);

        List<TaskOption> multipleChoiceOptions = List.of(
                new TaskOption("Option A", true),
                new TaskOption("Option B", true),
                new TaskOption("Option C", false)
        );
        MultipleChoiceTask multipleChoiceTask = new MultipleChoiceTask(mockCourse, "Multiple choice task", 4, multipleChoiceOptions);

        tasks.add(openTextTask1);
        tasks.add(openTextTask2);
        tasks.add(singleChoiceTask);
        tasks.add(multipleChoiceTask);

        assertTrue(organizer.hasAllTaskTypes());
    }

}