package br.com.alura.AluraFake.task;

import br.com.alura.AluraFake.course.Course;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.springframework.util.Assert;

import java.util.List;

@Entity
@DiscriminatorValue("SINGLE_CHOICE")
public class SingleChoiceTask extends TaskWithOptions {

    public static final Integer REQUIRED_OPTIONS = 1;

    @Deprecated
    public SingleChoiceTask() {
    }

    public SingleChoiceTask(@NotNull Course course, @Size(min = 4, max = 255) String statement, @NotNull @Min(1) Integer order, @NotNull @Size(min = 2, max = 5) List<TaskOption> options) {
        super(course, statement, order, options);
        Assert.isTrue(options.size() >= 2 && options.size() <= 5, "options has an invalid size");
        Assert.isTrue(TaskOption.hasOneCorrectOption(options), "Task must have one option with isCorrect true");
    }

    @Override
    public Type getType() {
        return Type.SINGLE_CHOICE;
    }

}