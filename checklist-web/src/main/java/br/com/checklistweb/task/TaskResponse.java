package br.com.checklistweb.task;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class TaskResponse {

    private Long id;
    private Long categoryId;
    private String categoryName;
    private String description;
    private String observation;
    private TaskStatus status;
    private TaskType type;
    private LocalDate referenceDate;
    private Integer displayOrder;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public TaskResponse() {
    }

    public TaskResponse(Task task) {
        this.id = task.getId();
        this.categoryId = task.getCategory().getId();
        this.categoryName = task.getCategory().getName();
        this.description = task.getDescription();
        this.observation = task.getObservation();
        this.status = task.getStatus();
        this.type = task.getType();
        this.referenceDate = task.getReferenceDate();
        this.displayOrder = task.getDisplayOrder();
        this.createdAt = task.getCreatedAt();
        this.updatedAt = task.getUpdatedAt();
    }

    public Long getId() {
        return id;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public String getDescription() {
        return description;
    }

    public String getObservation() {
        return observation;
    }

    public TaskStatus getStatus() {
        return status;
    }

    public TaskType getType() {
        return type;
    }

    public LocalDate getReferenceDate() {
        return referenceDate;
    }

    public Integer getDisplayOrder() {
        return displayOrder;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
}