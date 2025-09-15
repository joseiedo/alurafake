package br.com.alura.AluraFake.task;

import br.com.alura.AluraFake.task.domain.Task;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TaskRepository extends JpaRepository<Task, Long> {


}
