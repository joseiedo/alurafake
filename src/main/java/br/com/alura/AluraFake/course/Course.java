package br.com.alura.AluraFake.course;

import br.com.alura.AluraFake.task.Task;
import br.com.alura.AluraFake.user.User;
import jakarta.persistence.*;
import org.springframework.util.Assert;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Course {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private LocalDateTime createdAt = LocalDateTime.now();
    private String title;
    private String description;
    @ManyToOne
    private User instructor;
    @Enumerated(EnumType.STRING)
    private Status status;
    private LocalDateTime publishedAt;

    @OneToMany
    private List<Task> tasks = new ArrayList<>();

    @Deprecated
    public Course(){}

    public Course(String title, String description, User instructor) {
        Assert.isTrue(instructor.isInstructor(), "Usuario deve ser um instrutor");
        this.title = title;
        this.instructor = instructor;
        this.description = description;
        this.status = Status.BUILDING;
    }

    public Long getId() {
        return id;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public String getTitle() {
        return title;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public User getInstructor() {
        return instructor;
    }

    public String getDescription() {
        return description;
    }

    public Status getStatus() {
        return status;
    }

    public LocalDateTime getPublishedAt() {
        return publishedAt;
    }

    public boolean isBuilding() {
        return Status.BUILDING.equals(this.status);
    }

    // Aqui estou assumindo que um curso não haveria um número enorme de atividades (e que cada atividade não possui campos pesados).
    // Mas caso fosse esse o caso, essa checagem poderia ser problemática por carregar todas as atividades em memória.
    public boolean hasTaskWithStatement(String statement) {
        return this.tasks.stream().anyMatch(currentTask -> currentTask.isStatementEquals(statement));
    }

    public void addTask(Task task){
       Assert.isTrue(this.isBuilding(), "Course can't receive more tasks when not in BUILDING status");
       Assert.isTrue(!this.hasTaskWithStatement(task.getStatement()), "Course can't have multiple tasks with the same statement");
       this.tasks.add(task);
    }
}
