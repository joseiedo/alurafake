package br.com.alura.AluraFake.task;

import br.com.alura.AluraFake.course.Course;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.springframework.util.Assert;

import java.util.List;

@MappedSuperclass
public abstract class TaskWithOptions extends Task {

    @ElementCollection
    @CollectionTable(name = "TaskOption", joinColumns = @JoinColumn(name = "task_id"))
    private List<TaskOption> options;

    @Deprecated
    public TaskWithOptions() {
        super();
    }

    protected TaskWithOptions(@NotNull Course course, @Size(min = 4, max = 255) String statement, @NotNull @Min(1) Integer order) {
        super(course, statement, order);
    }

    public void addOptions(@NotNull List<TaskOption> newOptions) {
        Assert.notNull(newOptions, "Received options list cannot be null");

        for (TaskOption newOption : newOptions) {
            Assert.notNull(newOption, "TaskOption cannot be null");
            Assert.isTrue(!isOptionEqualToStatement(newOption), "Received TaskOption \"option\" value is equal to task statement");
            Assert.isTrue(!hasTaskOptionWithSameOption(newOption), "Task already has a TaskOption with the same \"option\" value");
            Assert.isTrue(!hasOptionTextInList(newOption, newOptions), "Duplicate options found in the provided list");
        }

        this.options.addAll(newOptions);
    }

    public boolean hasTaskOptionWithSameOption(@NotNull TaskOption newOption) {
        Assert.notNull(newOption, "Received taskOption is null");
        return this.options.stream().anyMatch(taskOption -> taskOption.hasOptionText(newOption.option()));
    }

    public boolean isOptionEqualToStatement(@NotNull TaskOption taskOption) {
        Assert.notNull(taskOption, "Received taskOption is null");
        Assert.notNull(taskOption.option(), "Received taskOption \"option\" value is null");
        return taskOption.hasOptionText(this.getStatement());
    }

    public List<TaskOption> getOptions() {
        return options;
    }

    private boolean hasOptionTextInList(TaskOption targetOption, List<TaskOption> optionsList) {
        return optionsList.stream()
                .anyMatch(option -> option.hasOptionText(targetOption.option()));
    }
}