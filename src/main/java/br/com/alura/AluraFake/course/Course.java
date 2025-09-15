package br.com.alura.AluraFake.course;

import br.com.alura.AluraFake.task.domain.OrderedTasks;
import br.com.alura.AluraFake.task.domain.Task;
import br.com.alura.AluraFake.user.User;
import jakarta.persistence.*;
import org.springframework.util.Assert;

import java.time.LocalDateTime;
import java.util.SortedSet;
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

    /**
     * Tasks list.
     * Manipulation operations in this field must be made through {@link Course#orderedTasks}
     * to ensure proper order management and sequence validation.
     */
    @OneToMany(cascade = CascadeType.PERSIST, mappedBy = "course", fetch = FetchType.LAZY)
    private SortedSet<Task> tasks;

    /**
     * Wrapper for {@link Course#tasks}
     * field with order management and sequence validation.
     */
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
        this.tasks = new TreeSet<>();
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

    /**
     * Publishes the course after validating business rules.
     * Requires BUILDING status, continuous task sequence, and all task types present.
     */
    public void publish() {
        Assert.isTrue(this.isBuilding(), "Course is not in BUILDING status");
        Assert.isTrue(this.hasContinuousTaskSequence(), "Course task sequence is not continuous");
        Assert.isTrue(this.hasAllTaskTypes(), "Course must have at least one activity of each type");
        this.status = Status.PUBLISHED;
        this.publishedAt = LocalDateTime.now();
    }

    public boolean isBuilding() {
        return Status.BUILDING.equals(this.status);
    }

    public boolean isPublished() {
        return Status.PUBLISHED.equals(this.status);
    }

    public boolean hasTaskWithStatement(String statement) {
        return this.orderedTasks.hasTaskWithStatement(statement);
    }

    public boolean isOrderPlacementValid(Integer order) {
        return this.orderedTasks.fitsInSequence(order);
    }

    /**
     * Adds a task to the course with validation and automatic order management.
     * The ordering and shifting logic are handled by {@link OrderedTasks}.
     *
     * @see OrderedTasks
     */
    public void addTask(Task task){
       Assert.isTrue(this.isBuilding(), "Course can't receive more tasks when not in BUILDING status");
       Assert.isTrue(!this.hasTaskWithStatement(task.getStatement()), "Course can't have multiple tasks with the same statement");
       this.orderedTasks.add(task);
    }

    public Boolean hasContinuousTaskSequence() {
        return this.orderedTasks.hasContinuousTaskSequence();
    }

    public Boolean hasAllTaskTypes() {
        return this.orderedTasks.hasAllTaskTypes();
    }

    public SortedSet<Task> getTasks() {
        return this.orderedTasks.getTasks();
    }
}
