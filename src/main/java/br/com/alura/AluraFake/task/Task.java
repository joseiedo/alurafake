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
        name = "Task",
        uniqueConstraints = {@UniqueConstraint(columnNames = {"course_id", "statement"})}
)
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "type", discriminatorType = DiscriminatorType.STRING)
public abstract class Task implements Comparable<Task> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDateTime createdAt = LocalDateTime.now();

    @Size(min = 4, max = 255)
    private String statement;

    @NotNull
    @Column(name = "task_order")
    private Integer order;

    @ManyToOne
    @JoinColumn(columnDefinition = "course_id", referencedColumnName = "id")
    private Course course;

    @Deprecated
    public Task() {
    }

    protected Task(@NotNull Course course, @Size(min = 4, max = 255) String statement, @NotNull @Min(1) Integer order) {
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

    public Long getId() {
        return id;
    }

    public String getStatement() {
        return statement;
    }

    public Boolean isStatementEquals(String statement) {
        return this.statement.equals(statement);
    }

    public abstract Type getType();

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
        return Objects.equals(statement, task.statement) && Objects.equals(order, task.order) && getType() == task.getType() && Objects.equals(course, task.course);
    }

    @Override
    public int hashCode() {
        return Objects.hash(statement, order, getType(), course);
    }

    @Override
    public int compareTo(Task other) {
        return this.order.compareTo(other.order);
    }

    public void incrementOrder() {
        this.order++;
    }

}
