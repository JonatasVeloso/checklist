package br.com.checklistweb.task;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public class TaskRequest {

    @NotNull
    private Long categoryId;

    @NotBlank
    private String description;

    private String observation;

    @NotNull
    private TaskStatus status;

    @NotNull
    private TaskType type;

    private LocalDate referenceDate;

    @NotNull
    private Integer displayOrder;

    public TaskRequest() {
    }

    public Long getCategoryId() {
        return categoryId;
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

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setObservation(String observation) {
        this.observation = observation;
    }

    public void setStatus(TaskStatus status) {
        this.status = status;
    }

    public void setType(TaskType type) {
        this.type = type;
    }

    public void setReferenceDate(LocalDate referenceDate) {
        this.referenceDate = referenceDate;
    }

    public void setDisplayOrder(Integer displayOrder) {
        this.displayOrder = displayOrder;
    }
}