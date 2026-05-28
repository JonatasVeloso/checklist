package br.com.checklistweb.task;

import br.com.checklistweb.category.Category;
import br.com.checklistweb.category.CategoryService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;

@Service
public class TaskService {

    private final TaskRepository taskRepository;
    private final CategoryService categoryService;

    public TaskService(TaskRepository taskRepository, CategoryService categoryService) {
        this.taskRepository = taskRepository;
        this.categoryService = categoryService;
    }

    public List<TaskResponse> findAll() {
        return taskRepository.findAllByOrderByCategoryDisplayOrderAscDisplayOrderAscIdAsc()
                .stream()
                .map(TaskResponse::new)
                .toList();
    }

    public List<TaskResponse> findByCategory(Long categoryId) {
        return taskRepository.findByCategoryIdOrderByDisplayOrderAscIdAsc(categoryId)
                .stream()
                .map(TaskResponse::new)
                .toList();
    }

    public List<TaskResponse> findToday() {
        LocalDate today = LocalDate.now();

        return taskRepository.findAllByOrderByCategoryDisplayOrderAscDisplayOrderAscIdAsc()
                .stream()
                .filter(task -> shouldAppearToday(task, today))
                .sorted(Comparator
                        .comparing((Task task) -> task.getCategory().getDisplayOrder())
                        .thenComparing(Task::getDisplayOrder)
                        .thenComparing(Task::getId))
                .map(TaskResponse::new)
                .toList();
    }

    @Transactional
    public TaskResponse create(TaskRequest request) {
        validateRequest(request);

        Category category = categoryService.findEntityById(request.getCategoryId());

        Task task = new Task();
        task.setCategory(category);
        task.setDescription(request.getDescription());
        task.setObservation(request.getObservation());
        task.setStatus(request.getStatus());
        task.setType(request.getType());
        task.setReferenceDate(request.getReferenceDate());
        task.setDisplayOrder(request.getDisplayOrder());

        Task savedTask = taskRepository.save(task);

        return new TaskResponse(savedTask);
    }

    @Transactional
    public TaskResponse update(Long id, TaskRequest request) {
        validateRequest(request);

        Task task = findEntityById(id);
        Category category = categoryService.findEntityById(request.getCategoryId());

        task.setCategory(category);
        task.setDescription(request.getDescription());
        task.setObservation(request.getObservation());
        task.setStatus(request.getStatus());
        task.setType(request.getType());
        task.setReferenceDate(request.getReferenceDate());
        task.setDisplayOrder(request.getDisplayOrder());

        return new TaskResponse(task);
    }

    @Transactional
    public TaskResponse updateStatus(Long id, TaskStatus status) {
        Task task = findEntityById(id);
        task.setStatus(status);

        return new TaskResponse(task);
    }

    @Transactional
    public void delete(Long id) {
        Task task = findEntityById(id);
        taskRepository.delete(task);
    }

    private Task findEntityById(Long id) {
        return taskRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Tarefa não encontrada: " + id));
    }

    private boolean shouldAppearToday(Task task, LocalDate today) {
        if (task.isDone()) {
            return false;
        }

        if (task.getType() == null) {
            return false;
        }

        return switch (task.getType()) {
            case DIARIA -> true;
            case SEMANAL -> isSameDayOfWeek(task.getReferenceDate(), today);
            case MENSAL -> isSameDayOfMonth(task.getReferenceDate(), today);
            case PROGRAMADA -> isTodayOrOverdue(task.getReferenceDate(), today);
        };
    }

    private boolean isSameDayOfWeek(LocalDate referenceDate, LocalDate today) {
        return referenceDate != null
                && referenceDate.getDayOfWeek().equals(today.getDayOfWeek());
    }

    private boolean isSameDayOfMonth(LocalDate referenceDate, LocalDate today) {
        return referenceDate != null
                && referenceDate.getDayOfMonth() == today.getDayOfMonth();
    }

    private boolean isTodayOrOverdue(LocalDate referenceDate, LocalDate today) {
        return referenceDate != null
                && !referenceDate.isAfter(today);
    }

    private void validateRequest(TaskRequest request) {
        if (request.getType() == TaskType.SEMANAL && request.getReferenceDate() == null) {
            throw new IllegalArgumentException("Tarefas SEMANAL precisam de uma data de referência.");
        }

        if (request.getType() == TaskType.MENSAL && request.getReferenceDate() == null) {
            throw new IllegalArgumentException("Tarefas MENSAL precisam de uma data de referência.");
        }

        if (request.getType() == TaskType.PROGRAMADA && request.getReferenceDate() == null) {
            throw new IllegalArgumentException("Tarefas PROGRAMADA precisam de uma data de referência.");
        }
    }
}