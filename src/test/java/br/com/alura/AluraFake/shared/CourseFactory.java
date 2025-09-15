package br.com.alura.AluraFake.shared;

import br.com.alura.AluraFake.course.Course;
import br.com.alura.AluraFake.course.CourseRepository;
import br.com.alura.AluraFake.task.TaskRepository;
import br.com.alura.AluraFake.task.domain.*;
import br.com.alura.AluraFake.user.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

public class CourseFactory {

    public static Course createCourseReadyToPublish(String title, String description, User instructor) {
        Course course = new Course(title, description, instructor);
        OpenTextTask openTask = new OpenTextTask(course, "What is " + title + "?", 1);
        SingleChoiceTask singleTask = new SingleChoiceTask(course, "Choose the correct answer about " + title, 2,
            List.of(
                new TaskOption("This is correct", true),
                new TaskOption("This is incorrect", false)
            ));
        MultipleChoiceTask multiTask = new MultipleChoiceTask(course, "Select all that apply to " + title, 3,
            List.of(
                new TaskOption("First correct option", true),
                new TaskOption("Second correct option", true),
                new TaskOption("Incorrect option", false)
            ));

        return course;
    }
}