package br.com.checklistweb.task;

import br.com.checklistweb.category.ChecklistPerson;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TaskRepository extends JpaRepository<Task, Long> {

    List<Task> findAllByOrderByCategoryDisplayOrderAscDisplayOrderAscIdAsc();

    List<Task> findByCategoryIdOrderByDisplayOrderAscIdAsc(Long categoryId);

    List<Task> findByCategoryPersonOrderByCategoryDisplayOrderAscDisplayOrderAscIdAsc(ChecklistPerson person);

}