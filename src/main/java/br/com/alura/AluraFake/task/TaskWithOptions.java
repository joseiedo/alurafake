package br.com.alura.AluraFake.task;

import br.com.alura.AluraFake.course.Course;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MappedSuperclass;
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
    }

    protected TaskWithOptions(@NotNull Course course, @Size(min = 4, max = 255) String statement, @NotNull @Min(1) Integer order, @NotNull List<TaskOption> options) {
        super(course, statement, order);
        Assert.notNull(options, "options should not be null");
        Assert.isTrue(!TaskOption.isStatementInOptions(statement, options), "One of the received options is equal to the task statement");
        Assert.isTrue(!TaskOption.hasRepeatingOptions(options), "Received options has duplicated values");
    }

    public List<TaskOption> getOptions() {
        return options;
    }

}