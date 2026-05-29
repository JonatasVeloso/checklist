package br.com.checklistweb.task;

import br.com.checklistweb.category.ChecklistPerson;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {

    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @GetMapping
    public List<TaskResponse> findAll() {
        return taskService.findAll();
    }

    @GetMapping("/person/{person}")
    public List<TaskResponse> findByPerson(@PathVariable ChecklistPerson person) {
        return taskService.findByPerson(person);
    }

    @GetMapping("/today")
    public List<TaskResponse> findToday() {
        return taskService.findToday();
    }

    @GetMapping("/today/person/{person}")
    public List<TaskResponse> findTodayByPerson(@PathVariable ChecklistPerson person) {
        return taskService.findTodayByPerson(person);
    }

    @GetMapping("/category/{categoryId}")
    public List<TaskResponse> findByCategory(@PathVariable Long categoryId) {
        return taskService.findByCategory(categoryId);
    }

    @PostMapping
    public TaskResponse create(@RequestBody @Valid TaskRequest request) {
        return taskService.create(request);
    }

    @PutMapping("/{id}")
    public TaskResponse update(
            @PathVariable Long id,
            @RequestBody @Valid TaskRequest request
    ) {
        return taskService.update(id, request);
    }

    @PatchMapping("/{id}/status")
    public TaskResponse updateStatus(
            @PathVariable Long id,
            @RequestParam TaskStatus status
    ) {
        return taskService.updateStatus(id, status);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        taskService.delete(id);
    }
}