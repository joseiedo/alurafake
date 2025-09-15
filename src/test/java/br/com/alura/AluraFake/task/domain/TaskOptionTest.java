package br.com.alura.AluraFake.task.domain;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class TaskOptionTest {

    @ParameterizedTest
    @CsvSource({
            "What is Java?, true",
            "WHAT IS JAVA?, true",
            "What is Python?, false",
            "Object-oriented language, true",
            "Platform independent, true"
    })
    void should_check_statement_in_options(String statement, boolean expectedResult) {
        List<TaskOption> options = List.of(
                new TaskOption("What is Java?", true),
                new TaskOption("Object-oriented language", false),
                new TaskOption("Platform independent", false)
        );

        assertEquals(expectedResult, TaskOption.containsStatement(statement, options));
    }

    @Test
    void should_return_false_when_statement_is_not_in_empty_options() {
        String statement = "What is Java?";
        List<TaskOption> options = List.of();

        assertFalse(TaskOption.containsStatement(statement, options));
    }

    @Test
    void should_return_true_when_options_have_duplicates() {
        List<TaskOption> options = List.of(
                new TaskOption("Object-oriented language", true),
                new TaskOption("Platform independent", false),
                new TaskOption("Object-orieNTED language", false)
        );

        assertTrue(TaskOption.hasDuplicateOptions(options));
    }

    @Test
    void should_return_false_when_options_have_no_duplicates() {
        List<TaskOption> options = List.of(
                new TaskOption("Object-oriented language", true),
                new TaskOption("Platform independent", false),
                new TaskOption("Compiled language", false),
                new TaskOption("Interpreted language", false)
        );

        assertFalse(TaskOption.hasDuplicateOptions(options));
    }

    @Test
    void should_return_false_when_checking_empty_options_for_duplicates() {
        List<TaskOption> options = List.of();
        assertFalse(TaskOption.hasDuplicateOptions(options));
    }

    @Test
    void should_return_true_when_options_have_exactly_one_correct_option() {
        List<TaskOption> options = List.of(
                new TaskOption("Object-oriented language", true),
                new TaskOption("Platform independent", false),
                new TaskOption("Compiled language", false)
        );

        assertTrue(TaskOption.hasOneCorrectOption(options));
    }

    @Test
    void should_return_false_when_options_have_no_correct_option() {
        List<TaskOption> options = List.of(
                new TaskOption("Object-oriented language", false),
                new TaskOption("Platform independent", false),
                new TaskOption("Compiled language", false)
        );

        assertFalse(TaskOption.hasOneCorrectOption(options));
    }

    @Test
    void should_return_false_when_options_have_multiple_correct_options() {
        List<TaskOption> options = List.of(
                new TaskOption("Object-oriented language", true),
                new TaskOption("Platform independent", true),
                new TaskOption("Compiled language", false)
        );

        assertFalse(TaskOption.hasOneCorrectOption(options));
    }

    @Test
    void should_return_false_when_checking_empty_options_for_correct_option() {
        List<TaskOption> options = List.of();
        assertFalse(TaskOption.hasOneCorrectOption(options));
    }

    @Test
    void should_count_correct_options_correctly() {
        List<TaskOption> options = List.of(
                new TaskOption("Java is object-oriented", true),
                new TaskOption("Java is platform independent", true),
                new TaskOption("Java is functional only", false)
        );

        assertEquals(2L, TaskOption.countCorrectOptions(options));
    }

    @Test
    void should_count_wrong_options_correctly() {
        List<TaskOption> options = List.of(
                new TaskOption("Java is object-oriented", true),
                new TaskOption("Java is functional only", false),
                new TaskOption("Java is procedural only", false)
        );

        assertEquals(2L, TaskOption.countIncorrectOptions(options));
    }

    @Test
    void should_return_zero_when_no_matching_options() {
        List<TaskOption> options = List.of(
                new TaskOption("Java is object-oriented", true),
                new TaskOption("Java is platform independent", true),
                new TaskOption("Java is compiled", true)
        );

        assertEquals(0L, TaskOption.countIncorrectOptions(options));
    }

    @Test
    void should_return_zero_when_counting_empty_options() {
        List<TaskOption> options = List.of();
        assertEquals(0L, TaskOption.countCorrectOptions(options));
        assertEquals(0L, TaskOption.countIncorrectOptions(options));
    }
}