package br.com.alura.AluraFake.task;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.springframework.util.Assert;

@Embeddable
public record TaskOption(
        @NotNull
        @Size(min = 4, max = 80)
        @Column(name = "option_text")
        String option,

        @NotNull
        @Column(name = "is_correct")
        Boolean isCorrect
) {

    public Boolean hasOptionText(String text){
        Assert.notNull(text, "Received text should not be null");
        return this.option.equals(text);
    }
}