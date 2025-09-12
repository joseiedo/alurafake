package br.com.alura.AluraFake.task;

import br.com.alura.AluraFake.course.Course;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.springframework.util.Assert;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "IDX_course_id",
        uniqueConstraints = {@UniqueConstraint(columnNames = {"course_id", "statement"})}
)
public class Task {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDateTime createdAt = LocalDateTime.now();

    @Size(min = 4, max = 255)
    private String statement;

    @NotNull
    @Column(name = "task_order")
    private Integer order;

    @Enumerated(EnumType.STRING)
    @NotNull
    private Type type;

    @ManyToOne
    private Course course;

    @Deprecated
    public Task() {
    }

    public Task(@NotNull Course course, @Size(min = 4, max = 255) String statement, @NotNull @Min(1) Integer order) {
        Assert.notNull(course, "course cannot be null");
        Assert.hasText(statement, "statement cannot be null or empty");
        Assert.isTrue(statement.length() >= 4 && statement.length() <= 255, "statement must be between 4 and 255 characters");
        Assert.notNull(order, "order cannot be null");
        Assert.isTrue(order >= 1, "order must be at least 1");
        
        this.course = course;
        this.statement = statement;
        this.order = order;
        this.course.addTask(this);
    }

    public boolean isStatementEquals(String statement) {
        return this.statement.equals(statement);
    }

    public String getStatement() {
        return statement;
    }
}
