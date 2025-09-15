package br.com.alura.AluraFake.task.domain;

import jakarta.validation.constraints.NotNull;
import org.springframework.util.Assert;

import java.util.*;

/**
 * Manages an ordered collection of tasks, ensuring proper sequence validation
 * and automatic order adjustment when inserting new tasks.
 *
 * <p>This class maintains tasks in a continuous sequence starting from 1 (1,2,3...) with no gaps.
 * When a new task is inserted at an existing order position, existing tasks are automatically
 * shifted to higher orders to maintain the sequence.</p>
 *
 * <p>Key constraints enforced:</p>
 * <ul>
 *   <li>Tasks must have orders in continuous sequence (no gaps)</li>
 *   <li>New task order cannot exceed collection size + 1</li>
 *   <li>Automatic order shifting when inserting at existing positions</li>
 *   <li>Validation for course publishing requirements (all task types present)</li>
 * </ul>
 *
 * @see Task
 */
public class OrderedTasks {

    private final TreeSet<Task> tasks;

    final Integer MAX_GAP_BETWEEN_TASK_ORDER = 1;

    public OrderedTasks(TreeSet<Task> tasks) {
        Assert.notNull(tasks, "Received tasks must not be null");
        this.tasks = tasks;
    }

    public OrderedTasks(Collection<Task> tasks) {
        this(new TreeSet<>(tasks));
    }

    /**
     * Adds a task to the ordered collection, automatically handling order conflicts.
     * If the task's order position is already occupied, existing tasks are shifted
     * to higher orders to maintain continuous sequence.
     *
     * @param task the task to add
     * @throws IllegalArgumentException if the task order is invalid
     */
    public void add(Task task) {
        Assert.isTrue(isOrderWithinBounds(task), "Task order value is higher than list size");
        Assert.isTrue(fitsInSequence(task), "Task has an order with an invalid gap between existing tasks");
        if (isOrderInUse(task.getOrder())) {
            shiftTasksWithOrderHigherOrEqualTo(task.getOrder());
        }
        tasks.add(task);
    }

    /**
     * Validates that the task's order doesn't exceed the allowed maximum.
     *
     * @param task the task to validate
     * @return true if order is within valid range
     */
    public Boolean isOrderWithinBounds(@NotNull Task task) {
        Assert.notNull(task, "Received task can't be null");
        return task.getOrder() <= tasks.size() + 1;
    }

    public Boolean fitsInSequence(@NotNull Task task) {
        Assert.notNull(task, "Received task can't be null");
        Assert.isTrue(hasContinuousTaskSequence(), "Tasks are not in a continuous sequence");
        return fitsInSequence(task.getOrder());
    }


    /**
     * Checks if the received order will cause any gaps in the tasks list
     * @param order The order to check
     * @return true if the order is respecting the sequence rule
     */
    public Boolean fitsInSequence(@NotNull Integer order) {
        if (tasks.isEmpty()) {
            return order == 1;
        }

        Integer lastOrder = tasks.last().getOrder();
        return order >= 1 && order - lastOrder <= MAX_GAP_BETWEEN_TASK_ORDER;
    }

    public Boolean hasTaskWithStatement(String statement) {
        return this.tasks.stream().anyMatch(currentTask -> currentTask.matchesStatement(statement));
    }

    private void shiftTasksWithOrderHigherOrEqualTo(Integer order) {
        List<Task> tasksToShift = tasks.stream()
                .filter(task -> task.getOrder() >= order)
                .toList();

        tasksToShift.forEach(task -> {
            tasks.remove(task);
            task.incrementOrder();
        });

        tasks.addAll(tasksToShift);
    }

    public TreeSet<Task> getTasks() {
        return tasks;
    }

    private Boolean isOrderInUse(Integer order) {
        return tasks.stream().anyMatch(existingTask -> Objects.equals(existingTask.getOrder(), order));
    }

    /**
     * Checks if all tasks form a continuous sequence starting from 1.
     *
     * @return true if tasks form a continuous sequence (1,2,3,...)
     */
    public Boolean hasContinuousTaskSequence() {
        if (tasks.isEmpty()) return true;
        if (tasks.first().getOrder() != 1) return false;
        if (tasks.size() == 1) return true;

        List<Task> sortedTasks = tasks.stream().sorted().toList();
        for (int index = 1; index < sortedTasks.size(); index++) {
            Task previousTask = sortedTasks.get(index - 1);
            if (!Objects.equals(sortedTasks.get(index).getOrderGap(previousTask), MAX_GAP_BETWEEN_TASK_ORDER)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Validates that the collection contains at least one task of each required type.
     *
     * @return true if all task types are present
     * @see Type
     */
    public Boolean hasAllTaskTypes() {
        Set<Type> requiredTypes = Set.of(Type.values());
        Set<Type> existingTypes = tasks.stream()
                .map(Task::getType)
                .collect(java.util.stream.Collectors.toSet());
        return existingTypes.containsAll(requiredTypes);
    }
}