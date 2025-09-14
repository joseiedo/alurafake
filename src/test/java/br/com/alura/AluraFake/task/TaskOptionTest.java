package br.com.alura.AluraFake.task;

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

        assertEquals(expectedResult, TaskOption.isStatementInOptions(statement, options));
    }

    @Test
    void should_return_false_when_statement_is_not_in_empty_options() {
        String statement = "What is Java?";
        List<TaskOption> options = List.of();

        assertFalse(TaskOption.isStatementInOptions(statement, options));
    }

    @Test
    void should_return_true_when_options_have_duplicates() {
        List<TaskOption> options = List.of(
                new TaskOption("Object-oriented language", true),
                new TaskOption("Platform independent", false),
                new TaskOption("Object-orieNTED language", false)
        );

        assertTrue(TaskOption.hasRepeatingOptions(options));
    }

    @Test
    void should_return_false_when_options_have_no_duplicates() {
        List<TaskOption> options = List.of(
                new TaskOption("Object-oriented language", true),
                new TaskOption("Platform independent", false),
                new TaskOption("Compiled language", false),
                new TaskOption("Interpreted language", false)
        );

        assertFalse(TaskOption.hasRepeatingOptions(options));
    }

    @Test
    void should_return_false_when_checking_empty_options_for_duplicates() {
        List<TaskOption> options = List.of();
        assertFalse(TaskOption.hasRepeatingOptions(options));
    }
}