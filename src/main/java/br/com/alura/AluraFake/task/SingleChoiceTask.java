package br.com.alura.AluraFake.task;

import br.com.alura.AluraFake.course.Course;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Entity
@DiscriminatorValue("SINGLE_CHOICE")
public class SingleChoiceTask extends TaskWithOptions {

    @Deprecated
    public SingleChoiceTask() {
        super();
    }

    public SingleChoiceTask(@NotNull Course course, @Size(min = 4, max = 255) String statement, @NotNull @Min(1) Integer order) {
        super(course, statement, order);
    }

    @Override
    public Type getType() {
        return Type.SINGLE_CHOICE;
    }

}