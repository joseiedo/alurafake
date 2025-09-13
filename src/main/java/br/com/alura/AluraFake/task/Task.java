package br.com.alura.AluraFake.task;

import br.com.alura.AluraFake.course.Course;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.springframework.util.Assert;

import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(
        name = "IDX_course_id",
        uniqueConstraints = {@UniqueConstraint(columnNames = {"course_id", "statement"})}
)
public class Task implements Comparable<Task> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDateTime createdAt = LocalDateTime.now();

    @Size(min = 4, max = 255)
    private String statement;

    @NotNull
    @Column(name = "task_order")
    private Integer order;

    @NotNull
    @Enumerated(EnumType.STRING)
    private Type type;

    @ManyToOne
    private Course course;

    @Deprecated
    public Task() {
    }

    public Task(@NotNull Course course, @NotNull Type type, @Size(min = 4, max = 255) String statement, @NotNull @Min(1) Integer order) {
        Assert.notNull(type, "type cannot be null");
        Assert.notNull(course, "course cannot be null");
        Assert.hasText(statement, "statement cannot be null or empty");
        Assert.isTrue(statement.length() >= 4 && statement.length() <= 255, "statement must be between 4 and 255 characters");
        Assert.notNull(order, "order cannot be null");
        Assert.isTrue(order >= 1, "order must be at least 1");

        this.type = type;
        this.course = course;
        this.statement = statement;
        this.order = order;
        this.course.addTask(this);
    }

    public String getStatement() {
        return statement;
    }

    public Boolean isStatementEquals(String statement) {
        return this.statement.equals(statement);
    }

    public Type getType() {
        return type;
    }

    public Integer getOrder() {
        return order;
    }

    public Integer getOrderDistance(Task task) {
        Assert.notNull(task, "Received task can't be null");
        return Math.abs(this.order - task.getOrder());
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Task task)) return false;
        return Objects.equals(statement, task.statement) && Objects.equals(order, task.order) && type == task.type && Objects.equals(course, task.course);
    }

    @Override
    public int hashCode() {
        return Objects.hash(statement, order, type, course);
    }

    @Override
    public int compareTo(Task other) {
        return this.order.compareTo(other.order);
    }

    public void incrementOrder() {
        this.order++;
    }
}
