package br.com.alura.AluraFake.task.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.springframework.util.Assert;

import java.util.*;

@Embeddable
public final class TaskOption {
    @Column(name = "option_text")
    @NotNull
    @Size(min = 4, max = 80)
    private String option;
    @Column(name = "is_correct")
    private @NotNull Boolean isCorrect;

    public TaskOption(
            @NotNull
            @Size(min = 4, max = 80)
            String option,

            @NotNull
            Boolean isCorrect
    ) {
        Assert.notNull(option, "Received option must not be null");
        Assert.notNull(isCorrect, "Received isCorrect must not be null");
        this.option = option;
        this.isCorrect = isCorrect;
    }

    @Deprecated
    public TaskOption() {
    }

    public Boolean hasOptionText(String text) {
        Assert.notNull(text, "Received text should not be null");
        return this.option.equalsIgnoreCase(text);
    }

    public static Boolean containsStatement(@NotNull String statement, @NotNull List<TaskOption> taskOptions) {
        Assert.notNull(statement, "Received statement should not be null");
        Assert.notNull(taskOptions, "Received taskOptions should not be null");
        return taskOptions.stream().anyMatch(taskOption -> taskOption.hasOptionText(statement));
    }

    public static Boolean hasDuplicateOptions(@NotNull List<TaskOption> taskOptions) {
        Set<String> seen = new HashSet<>();
        return taskOptions.stream()
                .map(taskOption -> taskOption.getOption().toLowerCase(Locale.ROOT))
                .anyMatch(option -> !seen.add(option));
    }

    public static Boolean hasOneCorrectOption(@NotNull List<TaskOption> taskOptions) {
        return taskOptions.stream()
                .filter(TaskOption::getCorrect)
                .count() == 1;
    }

    public static Long countCorrectOptions(@NotNull List<TaskOption> taskOptions) {
        return taskOptions.stream()
                .filter(TaskOption::getCorrect)
                .count();
    }

    public static Long countIncorrectOptions(@NotNull List<TaskOption> taskOptions) {
        return taskOptions.stream()
                .filter(taskOption -> !taskOption.getCorrect())
                .count();
    }

    public String getOption() {
        return option;
    }

    public Boolean getCorrect() {
        return isCorrect;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (TaskOption) obj;
        return Objects.equals(this.option, that.option) &&
                Objects.equals(this.isCorrect, that.isCorrect);
    }

    @Override
    public int hashCode() {
        return Objects.hash(option, isCorrect);
    }

}