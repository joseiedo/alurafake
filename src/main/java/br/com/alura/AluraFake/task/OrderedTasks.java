package br.com.alura.AluraFake.task;

import jakarta.validation.constraints.NotNull;
import org.springframework.util.Assert;

import java.util.*;

public class OrderedTasks {

    TreeSet<Task> tasks;

    final Integer MAX_GAP_BETWEEN_TASK_ORDER = 1;

    public OrderedTasks(TreeSet<Task> tasks) {
        Assert.notNull(tasks, "Received tasks must not be null");
        this.tasks = tasks;
    }

    public OrderedTasks(Collection<Task> tasks) {
        this(new TreeSet<>(tasks));
    }

    public void add(Task task) {
        Assert.isTrue(hasOrderRespectingTasksSize(task), "Task order value is higher than list size");
        Assert.isTrue(hasValidRangeGaps(task), "Task has an order with an invalid gap between existing tasks");
        if (isOrderInUse(task.getOrder())) {
            shiftTasksWithOrderHigherOrEqualTo(task.getOrder());
        }
        tasks.add(task);
    }

    public Boolean hasOrderRespectingTasksSize(@NotNull Task task) {
        Assert.notNull(task, "Received task can't be null");
        return task.getOrder() <= tasks.size() + 1;
    }

    public Boolean hasValidRangeGaps(@NotNull Task task) {
        Assert.notNull(task, "Received task can't be null");

        Optional<Task> lower = Optional.ofNullable(tasks.lower(task));
        if (lower.isPresent() && lower.get().getOrderDistance(task) > MAX_GAP_BETWEEN_TASK_ORDER) {
            return false;
        }

        Optional<Task> higher = Optional.ofNullable(tasks.higher(task));
        if (higher.isPresent() && higher.get().getOrderDistance(task) > MAX_GAP_BETWEEN_TASK_ORDER) {
            return false;
        }

        return true;
    }

    public Boolean hasTaskWithStatement(String statement) {
        return this.tasks.stream().anyMatch(currentTask -> currentTask.isStatementEquals(statement));
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

    public Boolean hasContinuousTaskSequence() {
        if (tasks.isEmpty()) return true;
        if (tasks.first().getOrder() != 1) return false;
        if (tasks.size() == 1) return true;

        List<Task> sortedTasks = tasks.stream().sorted().toList();
        for (int index = 1; index < sortedTasks.size(); index++) {
            Task previousTask = sortedTasks.get(index - 1);
            if (!Objects.equals(sortedTasks.get(index).getOrderDistance(previousTask), MAX_GAP_BETWEEN_TASK_ORDER)) {
                return false;
            }
        }
        return true;
    }

    public Boolean hasAllTaskTypes() {
        Set<Type> requiredTypes = Set.of(Type.values());
        Set<Type> existingTypes = tasks.stream()
                .map(Task::getType)
                .collect(java.util.stream.Collectors.toSet());
        return existingTypes.containsAll(requiredTypes);
    }
}