package br.com.alura.AluraFake.task;

import br.com.alura.AluraFake.course.Course;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

@Entity
@DiscriminatorValue("SINGLE_CHOICE")
public class SingleChoiceTask extends TaskWithOptions {

    @Deprecated
    public SingleChoiceTask() {
    }

    public SingleChoiceTask(@NotNull Course course, @Size(min = 4, max = 255) String statement, @NotNull @Min(1) Integer order, @NotNull @Size(min = 3, max = 5) List<TaskOption> options) {
        super(course, statement, order, options);
    }

    @Override
    public Type getType() {
        return Type.SINGLE_CHOICE;
    }

}