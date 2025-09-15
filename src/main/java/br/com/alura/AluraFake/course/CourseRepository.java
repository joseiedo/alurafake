package br.com.alura.AluraFake.course;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CourseRepository extends JpaRepository<Course, Long>{

    @Query("SELECT c.id as id, c.title as title, c.description as description, c.status as status, c.publishedAt as publishedAt, COUNT(t) as taskCount " +
           "FROM Course c LEFT JOIN c.tasks t " +
           "WHERE c.instructor.id = :instructorId " +
           "GROUP BY c.id, c.title, c.description, c.status, c.publishedAt")
    List<CourseProjection> findByInstructorIdWithTaskCount(Long instructorId);

    @Query("SELECT c.id as id, c.title as title, c.description as description, c.status as status, c.publishedAt as publishedAt, COUNT(t) as taskCount " +
           "FROM Course c LEFT JOIN c.tasks t " +
           "GROUP BY c.id, c.title, c.description, c.status, c.publishedAt")
    List<CourseProjection> findAllWithTaskCount();

}
