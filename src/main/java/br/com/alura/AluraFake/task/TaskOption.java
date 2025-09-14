package br.com.alura.AluraFake.task;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.springframework.util.Assert;

import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

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
        return this.option.equalsIgnoreCase(text);
    }

    public static Boolean isStatementInOptions(@NotNull String statement, @NotNull List<TaskOption> taskOptions) {
        Assert.notNull(statement, "Received statement should not be null");
        Assert.notNull(taskOptions, "Received taskOptions should not be null");
        return taskOptions.stream().anyMatch(taskOption -> taskOption.hasOptionText(statement));
    }

    public static Boolean hasRepeatingOptions(@NotNull List<TaskOption> taskOptions) {
        Set<String> seen = new HashSet<>();
        return taskOptions.stream()
                .map(taskOption -> taskOption.option().toLowerCase(Locale.ROOT))
                .anyMatch(option -> !seen.add(option));
    }

    public static Boolean hasOneCorrectOption(@NotNull List<TaskOption> taskOptions) {
        return taskOptions.stream()
                .filter(TaskOption::isCorrect)
                .count() == 1;
    }

    public static Long countOptionsByIsCorrect(@NotNull List<TaskOption> taskOptions, Boolean isCorrect) {
        return taskOptions.stream()
                .filter(taskOption -> taskOption.isCorrect == isCorrect)
                .count();
    }
}