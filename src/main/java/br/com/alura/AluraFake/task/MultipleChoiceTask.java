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

    @Deprecated
    public MultipleChoiceTask() {
        super();
    }

    public MultipleChoiceTask(@NotNull Course course, @Size(min = 4, max = 255) String statement, @NotNull @Min(1) Integer order) {
        super(course, statement, order);
    }

    @Override
    public Type getType() {
        return Type.MULTIPLE_CHOICE;
    }
}