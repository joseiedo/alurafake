package br.com.alura.AluraFake.course;

import br.com.alura.AluraFake.task.OrderedTasks;
import br.com.alura.AluraFake.task.Task;
import br.com.alura.AluraFake.user.User;
import jakarta.persistence.*;
import org.springframework.util.Assert;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;

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

    @OneToMany(cascade = CascadeType.PERSIST, mappedBy = "course", fetch = FetchType.LAZY)
    private List<Task> tasks;

    @Transient
    private OrderedTasks orderedTasks;

    @Deprecated
    public Course(){}

    public Course(String title, String description, User instructor) {
        Assert.isTrue(instructor.isInstructor(), "Usuario deve ser um instrutor");
        this.title = title;
        this.instructor = instructor;
        this.description = description;
        this.status = Status.BUILDING;
        this.tasks = new ArrayList<>();
        this.orderedTasks = new OrderedTasks(this.tasks);
    }

    @PostLoad
    private void postLoad() {
        this.orderedTasks = new OrderedTasks(this.tasks);
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

    public boolean hasTaskWithStatement(String statement) {
        return this.orderedTasks.hasTaskWithStatement(statement);
    }

    public void addTask(Task task){
       Assert.isTrue(this.isBuilding(), "Course can't receive more tasks when not in BUILDING status");
       Assert.isTrue(!this.hasTaskWithStatement(task.getStatement()), "Course can't have multiple tasks with the same statement");
       this.orderedTasks.add(task);
    }
}
