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
@DiscriminatorValue("MULTIPLE_CHOICE")
public class MultipleChoiceTask extends TaskWithOptions {

    public static final int MIN_WRONG_OPTIONS = 1;
    public static final int MIN_CORRECT_OPTIONS = 2;

    @Deprecated
    public MultipleChoiceTask() {
    }

    public MultipleChoiceTask(@NotNull Course course, @Size(min = 4, max = 255) String statement, @NotNull @Min(1) Integer order, @NotNull @Size(min = 3, max = 5) List<TaskOption> options) {
        super(course, statement, order, options);
        Assert.isTrue(TaskOption.countOptionsByIsCorrect(options, true) >= MIN_CORRECT_OPTIONS, "Not enough options with isCorrect true");
        Assert.isTrue(TaskOption.countOptionsByIsCorrect(options, false) >= MIN_WRONG_OPTIONS, "Not enough options with isCorrect false");
    }

    @Override
    public Type getType() {
        return Type.MULTIPLE_CHOICE;
    }
}