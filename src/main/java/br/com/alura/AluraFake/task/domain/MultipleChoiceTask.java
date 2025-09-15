package br.com.alura.AluraFake.task.domain;

import br.com.alura.AluraFake.course.Course;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.springframework.util.Assert;

import java.util.List;

@Entity
@DiscriminatorValue("MULTIPLE_CHOICE")
public class MultipleChoiceTask extends TaskWithOptions {

    public static final Long MIN_WRONG_OPTIONS_SIZE = 1L;
    public static final Long MIN_CORRECT_OPTIONS_SIZE = 2L;

    @Deprecated
    public MultipleChoiceTask() {
    }

    public MultipleChoiceTask(@NotNull Course course, @Size(min = 4, max = 255) String statement, @NotNull @Min(1) Integer order, @NotNull @Size(min = 3, max = 5) List<TaskOption> options) {
        super(course, statement, order, options);
        Assert.isTrue(options.size() >= 3 && options.size() <= 5, "options has an invalid size");
        Assert.isTrue(TaskOption.countCorrectOptions(options) >= MIN_CORRECT_OPTIONS_SIZE, "Not enough options with isCorrect true");
        Assert.isTrue(TaskOption.countIncorrectOptions(options) >= MIN_WRONG_OPTIONS_SIZE, "Not enough options with isCorrect false");
    }

    @Override
    public Type getType() {
        return Type.MULTIPLE_CHOICE;
    }
}